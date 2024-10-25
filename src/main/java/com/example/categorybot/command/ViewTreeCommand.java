package com.example.categorybot.command;

import com.example.categorybot.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.NoSuchElementException;

/**
 * Отображает категории из БД в виде дерева категорий
 * Формирует строку с деревом категорий, начиная с корневого элемента, и отправляет её в Telegram.
 */
@Component
@RequiredArgsConstructor
public class ViewTreeCommand extends Command {

    private final CategoryService categoryService;

    void execute(AbsSender sender, String chatId) {
        long parentId = categoryService.findMinParent();
        StringBuilder messageBuilder = new StringBuilder("<b>Дерево категорий:</b>\n");
        String messageText;
        try {
            messageText = categoryService.viewTree(messageBuilder, parentId, 0);
        } catch (NoSuchElementException e) {
            messageText = e.getMessage();
        }
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableHtml(true);
        message.setText(messageText);
        execute(sender, message);
    }
}
