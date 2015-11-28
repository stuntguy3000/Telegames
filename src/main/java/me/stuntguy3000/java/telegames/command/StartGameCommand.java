package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.TelegramGame;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

// @author Luke Anderson | stuntguy3000
public class StartGameCommand extends TelegramCommand {
    public StartGameCommand(Telegames instance) {
        super(instance, "startgame", "/startgame <game> Start a game");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();

        if (event.getArgs().length < 1) {
            respond(chat, "Correct Syntax: /startgame <game>");
        } else {
            TelegramGame game = getInstance().getGameHandler().getGame(event.getArgs()[0]);

            if (game != null) {
                TelegramGame channelGame = getInstance().getGameHandler().getGame(chat);
                if (channelGame == null) {
                    getInstance().getGameHandler().startGame(game, chat);
                } else {
                    respond(chat, "A game is already running!");
                }
            } else {
                respond(chat, "Game \"" + event.getArgs()[0] + "\" could not be found.\nUse /gamelist for help.");
            }
        }
    }
}
    