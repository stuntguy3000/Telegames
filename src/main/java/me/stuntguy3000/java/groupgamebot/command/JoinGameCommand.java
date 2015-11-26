package me.stuntguy3000.java.groupgamebot.command;

import me.stuntguy3000.java.groupgamebot.GroupGameBot;
import me.stuntguy3000.java.groupgamebot.handler.TelegramGame;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

// @author Luke Anderson | stuntguy3000
public class JoinGameCommand extends TelegramCommand {
    public JoinGameCommand(GroupGameBot instance) {
        super(instance, "joingame", "/joingame Join the current game");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();

        TelegramGame game = getInstance().getGameHandler().getGame(event.getChat());
        TelegramGame userGame = getInstance().getGameHandler().getGame(event.getMessage().getSender());

        if (game != null) {
            if (userGame == null) {
                getInstance().getGameHandler().joinGame(chat, event.getMessage().getSender());
            } else {
                respond(chat, "You are already in a game!");
            }
        } else {
            respond(chat, "No game is running!\nUse /startgame for start one.");
        }
    }
}
    