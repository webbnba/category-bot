package com.example.categorybot.bot;

import com.example.categorybot.command.Command;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Component
public class CategoryBot extends TelegramLongPollingBot {


    public CategoryBot(@Value("${bot.token}") String botToken) {
        super(botToken);

    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String commandKey = extractCommandKey(messageText);

            Command command = commands.get(commandKey);
            if (command != null) {
                command.execute(update);
            } else {
                // Обработка неизвестной команды
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "category_tree_test_bot";
    }

    private String extractCommandKey(String messageText) {
        return messageText.split(" ")[0];
    }
}
