package com.example.categorybot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class CategoryBotSender extends DefaultAbsSender {
    protected CategoryBotSender(@Value("${bot.token}") String botToken) {
        super(new DefaultBotOptions(), botToken);
    }
    /**
     * Отправляет текстовое сообщение в чат Telegram.
     *
     * @param chatId Идентификатор чата, в который нужно отправить сообщение.
     * @param textToSend Текст сообщения, которое необходимо отправить.
     */
    public void sendMessage(String chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableHtml(true);
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Произошла ошибка при попытке отправить сообщение в бот.", e);
        }
    }
    /**
     * Отправляет объект {@link SendMessage} в чат Telegram.
     *
     * @param message Объект сообщения, которое нужно отправить.
     */
    public void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Произошла ошибка при попытке отправить сообщение в бот.", e);
        }
    }
    /**
     * Отправляет документ в чат Telegram.
     *
     * @param document Объект документа, который необходимо отправить.
     */
    public void sendDocument(SendDocument document) {
        try {
            execute(document);
        } catch (TelegramApiException e) {
            log.error("Произошла ошибка при попытке отправить документ в бот.", e);
        }
    }
}
