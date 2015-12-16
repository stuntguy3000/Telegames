package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.Command;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

// @author Luke Anderson | stuntguy3000
public class VersionCommand extends Command {
    public VersionCommand() {
        super(Telegames.getInstance(), "/version View the bot's current version", "version");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();

        respond(chat, TelegramEmoji.GHOST.getText() + " Telegames by @stuntguy3000\n\nSource: https://github.com/stuntguy3000/telegames\nCurrent build " + Telegames.BUILD + ".");
    }
}