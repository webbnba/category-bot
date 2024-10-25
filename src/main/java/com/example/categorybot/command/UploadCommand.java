package com.example.categorybot.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Map;

@Component
public class UploadCommand extends Command {

    public void execute(AbsSender sender, String chatId, Map<String, Boolean> isWaitingForExcelFile) {

        isWaitingForExcelFile.put(chatId, true);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableHtml(true);
        message.setText("Загрузите файл формата *.xls или *.xlsx");

        execute(sender, message);
    }
}
