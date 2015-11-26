package me.stuntguy3000.java.groupgamebot.command;

import me.stuntguy3000.java.groupgamebot.GroupGameBot;
import me.stuntguy3000.java.groupgamebot.handler.TelegramGame;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

// @author Luke Anderson | stuntguy3000
public class GameHelpCommand extends TelegramCommand {
    public GameHelpCommand(GroupGameBot instance) {
        super(instance, "gamehelp", "/gamehelp <game> View specific help information for a game");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();

        if (event.getArgs().length < 1) {
            respond(chat, "Correct Syntax: /gamehelp <game>");
        } else {
            TelegramGame game = getInstance().getGameHandler().getGame(event.getArgs()[0]);

            if (game != null) {
                respond(chat, "Help for " + game.getName() + ":\n" + game.getHelp());
            } else {
                respond(chat, "Game \"" + event.getArgs()[0] + "\" could not be found.\nUse /gamelist for help.");
            }
        }
    }
}
    