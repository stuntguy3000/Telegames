package me.stuntguy3000.java.telegames.util.string;

import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;

// @author Luke Anderson | stuntguy3000
public class Lang {

    public static SendableTextMessage.SendableTextMessageBuilder build(String text, Object... variables) {
        return SendableTextMessage.builder().message(String.format(text, variables)).parseMode(ParseMode.MARKDOWN);
    }
}
    