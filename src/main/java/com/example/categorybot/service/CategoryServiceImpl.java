package com.example.categorybot.service;

import com.example.categorybot.entity.Category;
import com.example.categorybot.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Сервис для работы с категориями
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Добавляет корневой элемент
     *
     * @param name имя элемента
     */
    public void addRootElement(String name) {
        Category category = new Category(0L, name);
        categoryRepository.save(category);
    }

    /**
     * Добавляет элемент у которого есть родитель
     *
     * @param parent имя родителя
     * @param child  имя элемента
     */
    public void addChildElement(String parent, String child) {
        Long parentId = categoryRepository.findByName(parent).orElseThrow().getId();
        Category category = new Category(parentId, child);
        categoryRepository.save(category);
    }

    /**
     * Создает строку - дерево категорий для вывода в telegram
     *
     * @param stringBuilder базовая строка
     * @param parentId      id родителя
     * @param depth         начальная глубина вложения элемента
     */
    public String viewTree(StringBuilder stringBuilder, long parentId, int depth) {
        List<Category> categoryList = categoryRepository.findAllByParentId(parentId)
                .orElseThrow(() -> new NoSuchElementException("Элементы не найдены"));

        for (Category cat : categoryList) {
            long categoryId = cat.getId();
            String categoryName = cat.getName();

            stringBuilder.append("  ".repeat(Math.max(0, depth)));
            stringBuilder.append("• ").append(categoryName).append("\n");

            viewTree(stringBuilder, categoryId, depth + 1);
        }
        return stringBuilder.toString();
    }

    /**
     * Вызывыает метод удаления категории и всех ее потомков, если такая категория существует
     *
     * @param categoryName
     */
    @Transactional
    public void deleteCategoryAndDescendants(String categoryName) {
        Category categoryToDelete = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new NoSuchElementException("Категория не найдена"));
        deleteCategoryAndDescendantsRecursive(categoryToDelete);
    }

    /**
     * @return id корневой категории
     */
    public long findMinParent() {
        return categoryRepository.findMinParent();
    }

    /**
     * Проверяет есть ли такая категории в БД
     *
     * @param s имя категории
     * @return
     */
    public boolean checkIfCategoryExist(String s) {
        return categoryRepository.findByName(s).isPresent();
    }

    /**
     * Удаляет рекурсивно категорию и ее потомков
     *
     * @param category
     */
    private void deleteCategoryAndDescendantsRecursive(Category category) {

        List<Category> children = categoryRepository.findAllByParentId(category.getId()).orElseThrow();
        for (Category child : children
        ) {
            deleteCategoryAndDescendantsRecursive(child);
        }
        categoryRepository.delete(category);
    }
}
