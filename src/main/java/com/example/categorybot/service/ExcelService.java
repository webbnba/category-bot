package com.example.categorybot.service;

import com.example.categorybot.entity.Category;
import com.example.categorybot.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelService {
    private final CategoryRepository categoryRepository;

    public String createExcelTable() {
        List<Category> categories = categoryRepository.findAll();
        String filePath = "Categories.xlsx";

        try (Workbook workbook = new Workbook(new FileOutputStream(filePath), "Categories", "1.0")) {
            Worksheet sheet = workbook.newWorksheet("Categories");
            List<Category> printedCategories = new ArrayList<>();
            int rowIndex = 0;

            for (Category category : categories) {
                rowIndex = createCategoryRow(sheet, category, 0, rowIndex, printedCategories);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return filePath;
    }

    /**
     * Рекурсивно создает строку - категорию в Excel
     *
     * @param sheet             лист
     * @param category          категория
     * @param depth             глубина вложенности
     * @param rowIndex          индекс строки
     * @param printedCategories список уже внесенных категорий
     * @return индекс строки
     */
    private int createCategoryRow(Worksheet sheet, Category category, int depth, int rowIndex, List<Category> printedCategories) {
        if (!printedCategories.contains(category)) {
            sheet.value(rowIndex, depth, category.getName());
            printedCategories.add(category);

            rowIndex++;

            List<Category> subcategories = categoryRepository.findAllByParentId(category.getId()).orElseThrow();
            for (Category subcategory : subcategories) {
                rowIndex = createCategoryRow(sheet, subcategory, depth + 1, rowIndex, printedCategories);
            }
        }
        return rowIndex;
    }

    /**
     * Принимает URL-ссылку на загруженный в телеграм файл с excel-таблицей и выгружает данные из неё.
     * @return Данные в формате Map, где ключ - это номер строки, а значение хранит список с ячейками.
     */
    private Map<Integer, List<String>> getExcelTableFromTg(String filePath) {
        Map<Integer, List<String>> data = new HashMap<>();

        try (InputStream file = new URI(filePath).toURL().openStream();
             ReadableWorkbook wb = new ReadableWorkbook(file)) {

            Sheet sheet = wb.getFirstSheet();

            try (Stream<Row> rows = sheet.openStream()) {
                rows.forEach(r -> {
                    data.put(r.getRowNum(), new ArrayList<>());

                    for (Cell cell : r) {
                        if (cell == null) {
                            data.get(r.getRowNum()).add(null);
                        } else {
                            data.get(r.getRowNum()).add(cell.getRawValue());
                        }
                    }
                });
            }
        } catch (FileNotFoundException e) {
            log.error("Не найден файл с загруженной таблицей на сервере телеграмма.", e);
        } catch (IOException e) {
            log.error("Ошибка при чтении из файла с загруженной таблицей.", e);
        } catch (URISyntaxException e) {
            log.error("Ссылка на файл с загруженной таблицей имеет неправильный формат.", e);
        }
        return data;
    }

    /**
     * Обрабатывает полученный словарь с данными и загружает его в базу данных.
     */
    public void parseExcelTable(String filePath) {
        Map<Integer, List<String>> data = getExcelTableFromTg(filePath);
        categoryRepository.deleteAll();

        Map<Integer, Long> lastCategoryAtDepth = new HashMap<>();

        for (int i = 0; i <= data.size(); i++) {
            List<String> row = data.get(i);
            if (row != null) {
                int level = row.size() - 1;
                String categoryName = row.get(level);

                Long parentId = lastCategoryAtDepth.get(level - 1);
                Category category = new Category(parentId, categoryName);

                category = categoryRepository.save(category);

                lastCategoryAtDepth.put(level, category.getId());
            }
        }
    }
}



