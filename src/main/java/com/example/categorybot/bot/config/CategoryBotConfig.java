package com.example.categorybot.bot.config;

import com.example.categorybot.bot.CategoryBotListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class CategoryBotConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(CategoryBotListener categoryBotListener) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(categoryBotListener);
        return api;
    }
}
