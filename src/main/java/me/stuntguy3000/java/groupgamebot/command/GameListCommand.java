package me.stuntguy3000.java.groupgamebot.command;

import me.stuntguy3000.java.groupgamebot.GroupGameBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

// @author Luke Anderson | stuntguy3000
public class GameListCommand extends TelegramCommand {
    public GameListCommand(GroupGameBot instance) {
        super(instance, "gamelist", "/gamelist List all available games.");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();

        respond(chat, getInstance().getGameHandler().getGameList());
    }
}
    