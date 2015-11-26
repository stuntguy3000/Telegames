package me.stuntguy3000.java.groupgamebot.command;

import me.stuntguy3000.java.groupgamebot.GroupGameBot;
import me.stuntguy3000.java.groupgamebot.handler.TelegramGame;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

// @author Luke Anderson | stuntguy3000
public class LeaveGameCommand extends TelegramCommand {
    public LeaveGameCommand(GroupGameBot instance) {
        super(instance, "leavegame", "/leavegame Leave the current game");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();

        TelegramGame game = getInstance().getGameHandler().getGame(event.getChat());

        if (game != null) {
            getInstance().getGameHandler().leaveGame(chat, event.getMessage().getSender());
        } else {
            respond(chat, "No game is running!\nUse /startgame for start one.");
        }
    }
}
    