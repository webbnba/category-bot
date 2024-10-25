package com.example.categorybot.command;

import com.example.categorybot.service.ExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PerformUploadCommand extends Command {

    private final ExcelService excelTableService;
    @Value("${bot.token}")
    private String botToken;
    @Autowired
    @Lazy
    private TelegramLongPollingBot bot;

    public void execute(String chatId,
                        Document document,
                        Map<String, Boolean> isWaitingForExcelFile) {

        try {
            GetFile getFile = new GetFile();
            getFile.setFileId(document.getFileId());
            File file = bot.execute(getFile);

            excelTableService.parseExcelTable(file.getFileUrl(botToken));

        } catch (TelegramApiException e) {
            log.error("Произошла ошибка при попытке получить из бота загруженный файл.", e);
        }

        isWaitingForExcelFile.put(chatId, false);
    }
}
