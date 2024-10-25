package com.example.categorybot.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryBotConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(WeatherBot weatherBot) throws TelegramApiException{
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(weatherBot);
        return api;
    }
}
