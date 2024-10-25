package com.example.categorybot.command;


import com.example.categorybot.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.io.File;

/**
 * Обработчик команды /download
 */
@Component
@RequiredArgsConstructor
public final class DownloadCommand extends Command {

    private final ExcelService excelTableService;

    public void execute(AbsSender sender, String chatId) {

        String filePath = excelTableService.createExcelTable();

        File file = new File(filePath);
        InputFile inputFile = new InputFile(file, "tree.xlsx");

        SendDocument doc = new SendDocument();
        doc.setChatId(chatId);
        doc.setDocument(inputFile);

        execute(sender, doc);
    }
}
