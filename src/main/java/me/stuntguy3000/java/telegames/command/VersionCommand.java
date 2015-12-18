package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.Command;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

// @author Luke Anderson | stuntguy3000
public class VersionCommand extends Command {
    public VersionCommand() {
        super(Telegames.getInstance(), "/version View the bot's current version", "version", "about", "info");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();

        respond(chat, SendableTextMessage.builder().message(TelegramEmoji.GHOST.getText() + " *Telegames" + (Telegames.DEV_MODE ? " Dev Mode " : " ") + "by* @stuntguy3000\n" +
                "*Current version:* " + Telegames.BUILD + "\n\n" +
                        "*Source* [Hosted on GitHub](https://github.com/stuntguy3000/telegames)\n" +
                        "*Created using* @zackpollard's [JavaTelegramBotAPI](https://github.com/zackpollard/JavaTelegramBot-API)\n" +
                "*Stay up-to-date with new features!*\n Join https://telegram.me/telegames").parseMode(ParseMode.MARKDOWN).disableWebPagePreview(true).build());
    }
}