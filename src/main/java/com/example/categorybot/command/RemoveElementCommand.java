package com.example.categorybot.command;

import com.example.categorybot.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class RemoveElementCommand extends Command {

    private final CategoryService categoryService;

    /**
     * Обрабатывает команду удаления категории и всех её потомков.
     * Команда должна быть в формате "<имя категории>", где имя категории указывается в угловых скобках.
     * Если категория найдена, она и все её потомки будут удалены.
     *
     * @param sender объект для отправки сообщений через Telegram API
     * @param chatId идентификатор чата, в который необходимо отправить ответное сообщение
     * @param text текст команды, содержащий категорию для удаления
     */
    void execute(AbsSender sender, String chatId, String text) {

        Pattern pattern = Pattern.compile("^/removeElement <([^<>]+)>$");
        Matcher matcher = pattern.matcher(text);

        String messageText;

        if (matcher.matches()) {
            String[] messageParts = text.split("[<>]");
            String categoryName = messageParts[1].trim();

            if (categoryService.checkIfCategoryExist(categoryName)) {
                try {
                    categoryService.deleteCategoryAndDescendants(categoryName);
                    messageText = "Категория '" + categoryName + "' и все ее потомки успешно удалены.";
                } catch (NoSuchElementException e) {
                    messageText = "Ошибка: " + e.getMessage();
                }
            } else {
                messageText = "Категория '" + categoryName + "' не найдена.";
            }
        } else {
            messageText = "Неверный формат команды /removeElements.\n" +
                    "Для просмотра описания команд введите /help";
        }

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableHtml(true);
        message.setText(messageText);

        execute(sender, message);
    }
}
