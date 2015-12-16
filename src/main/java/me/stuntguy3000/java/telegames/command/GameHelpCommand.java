package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.Command;
import me.stuntguy3000.java.telegames.object.Game;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
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
            respond(chat, TelegramEmoji.RED_CROSS.getText() + " Correct Syntax: /gamehelp <game>");
        } else {
            Game game = getInstance().getGameHandler().getGame(event.getArgs()[0]);

            if (game != null) {
                respond(chat, TelegramEmoji.BOOK.getText() + " Help for " + game.getGameName() + ":\n" + game.getGameHelp());
            } else {
                respond(chat, TelegramEmoji.RED_CROSS.getText() + " Game \"" + event.getArgs()[0] + "\" could not be found.\nUse /gamelist for help.");
            }
        }
    }
}