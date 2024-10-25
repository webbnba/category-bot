package com.example.categorybot.command;

import com.example.categorybot.bot.CategoryBotSender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
@Slf4j
public class CommandDispatcher {

    private final CategoryBotSender botSender;
    private final StartCommand startCommand;
    @Getter
    private final HelpCommand helpCommand;
    private final ViewTreeCommand viewTreeCommand;
    private final AddElementCommand addElementCommand;
    private final RemoveElementCommand removeElementCommand;
    private final DownloadCommand downloadCommand;
    private final UploadCommand uploadCommand;
    private final PerformUploadCommand performUploadCommand;
    private final UnknownCommand unknownCommand;

    /**
     * Словарь с командами и их описаниями
     */
    private final Map<String, String> commandsDesc = new HashMap<>(Map.of(
            "/help", "Отображает список доступных команд с описанием",
            "/viewTree", "Отображает дерево категорий",
            "/addElement &ltназвание элемента&gt", "Добавляет новую категорию в корень дерева",
            "/addElement &ltродительский элемент&gt &ltдочерний элемент&gt", "Добавляет новую категорию к родительской",
            "/removeElement &ltназвание элемента&gt", "Удаляет категорию и вскх её потомков",
            "/download", "Скачивает Excel документ с деревом категорий",
            "/upload", "Принимает Excel документ с деревом категорий и сохраняет все элементы в базе данных"
    ));

    /**
     * Словарь с флагами, указывающими на то, была ли введена в конкретном чате команда /upload
     */
    private final Map<String, Boolean> isWaitingForExcelFile = new ConcurrentHashMap<>();

    /**
     * Получает апдейт от бота, обрабатывает его и вызывает исполняющий метод соответствующей команды.
     * Печатает в бот сообщение об ошибке, если команда была неопознана
     */
    public void handleUpdate(Update update) {

        if (!update.hasMessage()) {
            log.error("Получен апдейт без поля Message");
            return;
        }

        String chatId = update.getMessage().getChatId().toString();
        /**
         * Обработка сообщения без текста.
         * Если бот ожидает загрузку файла с excel-таблицей, и в сообщении есть вложенный документ,
         * тогда запускается команда на загрузку этого документа.
         * В ином случае возвращается ошибка.
         */
        if (!update.getMessage().hasText()) {
            Message mess = update.getMessage();
            Boolean uploading = isWaitingForExcelFile.get(chatId);
            if (mess.hasDocument() && uploading != null && uploading) {
                performUploadCommand.execute(chatId, mess.getDocument(), isWaitingForExcelFile);
                botSender.sendMessage(chatId, "Таблица успешно загружена");
                return;
            } else {
                botSender.sendMessage(chatId, "Бот принимает только текстовые команды.\n" +
                        "Для получения списка команд введите /help");
                return;
            }
        }

        String messageText = update.getMessage().getText();

        if (messageText.charAt(0) != '/') {
            botSender.sendMessage(chatId, "Бот принимает только команды." +
                    "\nДля получения списка команд введите /help");
            return;
        }

        String[] messageParts = messageText.split(" ");

        switch (messageParts[0]) {
            case "/start" -> startCommand.execute(botSender, chatId);
            case "/help" -> helpCommand.execute(botSender, chatId, commandsDesc);
            case "/viewTree" -> viewTreeCommand.execute(botSender, chatId);
            case "/addElement" -> addElementCommand.execute(botSender, chatId, messageText);
            case "/removeElement" -> removeElementCommand.execute(botSender, chatId, messageText);
            case "/download" -> downloadCommand.execute(botSender, chatId);
            case "/upload" -> uploadCommand.execute(botSender, chatId, isWaitingForExcelFile);
            default -> unknownCommand.execute(botSender, chatId);
        }
    }
}
