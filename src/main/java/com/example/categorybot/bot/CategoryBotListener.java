package com.example.categorybot.bot;

import com.example.categorybot.command.CommandDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
public class CategoryBotListener extends TelegramLongPollingBot {
    private final CommandDispatcher dispatcher;

    public CategoryBotListener(@Value("${bot.token}") String botToken, CommandDispatcher dispatcher) {
        super(botToken);
        this.dispatcher = dispatcher;
    }
    /**
     * Обрабатывает полученные обновления от Telegram.
     * Логирует обновление и передает его для дальнейшей обработки в {@link CommandDispatcher}.
     *
     * @param update Входящее обновление от Telegram.
     */
    @Override
    public void onUpdateReceived(Update update) {
        log.info("Get update " + update.toString());
        dispatcher.handleUpdate(update);
    }

    @Override
    public String getBotUsername() {
        return "category_tree_test_bot";
    }
}
