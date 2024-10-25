package com.example.categorybot.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Map;

@Component
public final class HelpCommand extends Command {

    public void execute(AbsSender sender, String chatId, Map<String, String> commands) {

        StringBuilder messageBuilder = new StringBuilder("<b>Доступные команды:</b>\n");

        commands.forEach((key, value) -> {
            messageBuilder.append("\n");
            messageBuilder.append(key);
            messageBuilder.append("\n");
            messageBuilder.append(value);
            messageBuilder.append("\n");
        });

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableHtml(true);
        message.setText(messageBuilder.toString());

        execute(sender, message);
    }
}
