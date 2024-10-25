package com.example.categorybot.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class UnknownCommand extends Command {

    public void execute(AbsSender sender, String chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableHtml(true);
        message.setText("Введённой команды не существует. Для получения списка доступных команд введите /help");

        execute(sender, message);
    }
}
