package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.Command;
import me.stuntguy3000.java.telegames.object.Game;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

// @author Luke Anderson | stuntguy3000
public class GameHelpCommand extends Command {
    public GameHelpCommand() {
        super(Telegames.getInstance(), "gamehelp", "/gamehelp <game> View specific help information for a game");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();

        if (event.getArgs().length < 1) {
            respond(chat, "Correct Syntax: /gamehelp <game>");
        } else {
            Game game = getInstance().getGameHandler().getGame(event.getArgs()[0]);

            if (game != null) {
                respond(chat, "Help for " + game.getName() + ":\n" + game.getGameHelp());
            } else {
                respond(chat, "Game \"" + event.getArgs()[0] + "\" could not be found.\nUse /gamelist for help.");
            }
        }
    }
}
    