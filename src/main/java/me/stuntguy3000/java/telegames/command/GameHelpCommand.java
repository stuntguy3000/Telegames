package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.command.Command;
import me.stuntguy3000.java.telegames.util.string.Emoji;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class GameHelpCommand extends Command {

    public GameHelpCommand() {
        super(Telegames.getInstance(), "/gamehelp <game> View specific help information for a game", false, "gamehelp", "ghelp");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();

        if (event.getArgs().length < 1) {
            chat.sendMessage(Emoji.RED_CROSS.getText() + " Correct Syntax: /gamehelp <game>");
        } else {
            /*
            Below requires that games be added

            Game game = getInstance().getGameHandler().getGame(event.getArgs()[0]);

            if (game != null) {
                respond(chat, TelegramEmoji.BOOK.getText() + " Help for " + game.getGameName() + ":\n" + game.getGameHelp());
            } else {
                respond(chat, TelegramEmoji.RED_CROSS.getText() + " Game \"" + event.getArgs()[0] + "\" could not be found.\nUse /gamelist for help.");
            }
            */
        }
    }

}