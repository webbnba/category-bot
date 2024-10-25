package com.example.categorybot.command;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
@Slf4j
public abstract class Command {

    void execute(AbsSender sender, SendMessage message) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            log.error("Произошла ошибка при попытке отправить сообщение в бот.", e);
        }
    }

    void execute(AbsSender sender, SendDocument document) {
        try {
            sender.execute(document);
        } catch (TelegramApiException e) {
            log.error("Произошла ошибка при попытке отправить документ в бот.", e);
        }
    }
}
