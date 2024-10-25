package com.example.categorybot.command;

import com.example.categorybot.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class AddElementCommand extends Command {

    private final CategoryService categoryService;

    void execute(AbsSender sender, String chatId, String text) {
        Pattern pattern = Pattern.compile("^/addElement <.+>$");
        Matcher matcher = pattern.matcher(text);

        Pattern patternWithParent = Pattern.compile("^/addElement <.+> <.+>$");
        Matcher matcherWithParent = patternWithParent.matcher(text);

        String messageText;
        if (matcherWithParent.matches()) {
            String[] messageParts = text.split("[<>]");
            messageText = handleTwoCategories(messageParts[1].trim(), messageParts[3].trim());
        } else if (matcher.matches()) {
            String[] messageParts = text.split("[<>]");
            messageText = handleSingleCategory(messageParts[1].trim());
        } else {
            messageText = "Неверный формат команды /addElements.\n" +
                    "Для просмотра описания команд введите /help";
        }

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableHtml(true);
        message.setText(messageText);

        execute(sender, message);
    }

    private String handleSingleCategory(String category) {
        if (!categoryService.checkIfCategoryExist(category)) {
            categoryService.addRootElement(category);
            return "Корневая категория '" + category + "' успешно добавлена.";
        } else {
            return "Категория '" + category + "' уже существует.";
        }
    }

    private String handleTwoCategories(String parent, String child) {
        boolean parentExists = categoryService.checkIfCategoryExist(parent);
        boolean childExists = categoryService.checkIfCategoryExist(child);

        if (parentExists && !childExists) {
            categoryService.addChildElement(parent, child);
            return "Категория '" + child + "' успешно добавлена как дочерняя к '" + parent + "'.";
        } else if (!parentExists) {
            return "Родительская категория '" + parent + "' не существует.";
        } else {
            return "Категория '" + child + "' уже существует.";
        }
    }

}
