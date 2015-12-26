package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.command.Command;
import me.stuntguy3000.java.telegames.object.game.Game;
import me.stuntguy3000.java.telegames.util.string.Lang;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

// @author Luke Anderson | stuntguy3000
public class GameHelpCommand extends Command {
    public GameHelpCommand() {
        super(Telegames.getInstance(), "/gamehelp <game> View specific help information for a game", "gamehelp", "ghelp");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();

        if (event.getArgs().length < 1) {
            respond(chat, String.format(Lang.ERROR_SYNTAX_INVALID, "gamehelp", "<name>"));
        } else {
            Game game = getInstance().getGameHandler().getGame(event.getArgs()[0]);

            if (game != null) {
                respond(chat, Lang.COMMAND_GAMEHELP + "\n" + game.getGameHelp());
            } else {
                respond(chat, Lang.ERROR_GAME_NOT_FOUND);
            }
        }
    }
}