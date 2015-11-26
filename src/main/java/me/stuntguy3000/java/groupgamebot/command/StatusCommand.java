package me.stuntguy3000.java.groupgamebot.command;

import me.stuntguy3000.java.groupgamebot.GroupGameBot;
import me.stuntguy3000.java.groupgamebot.handler.TelegramGame;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

// @author Luke Anderson | stuntguy3000
public class StatusCommand extends TelegramCommand {
    public StatusCommand(GroupGameBot instance) {
        super(instance, "status", "/status Get the status of the channel");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();

        TelegramGame game = getInstance().getGameHandler().getGame(chat);

        if (game != null) {
            respond(chat, "Game \"" + game.getName() + "\" is running on this chat.");
        } else {
            respond(chat, "No game is running on this chat.\nUse /startgame for start one.");
        }
    }
}
    