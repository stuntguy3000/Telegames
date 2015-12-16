package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.Command;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

// @author Luke Anderson | stuntguy3000
public class GameListCommand extends Command {
    public GameListCommand() {
        super(Telegames.getInstance(), "/gamelist List all available games.", "gamelist");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();

        respond(chat, TelegramEmoji.BOOK.getText() + " " + getInstance().getGameHandler().getGameList());
    }
}