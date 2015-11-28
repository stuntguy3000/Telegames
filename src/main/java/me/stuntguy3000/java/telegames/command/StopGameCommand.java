package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.TelegramGame;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

// @author Luke Anderson | stuntguy3000
public class StopGameCommand extends TelegramCommand {
    public StopGameCommand(Telegames instance) {
        super(instance, "stopgame", "/stopgame Stop the current game");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();

        TelegramGame game = getInstance().getGameHandler().getGame(event.getChat());

        if (game != null) {
            getInstance().getGameHandler().stopGame(chat, false);
        } else {
            respond(chat, "No game is running!\nUse /startgame for start one.");
        }
    }
}
    