package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.command.Command;
import me.stuntguy3000.java.telegames.object.game.Game;
import me.stuntguy3000.java.telegames.object.lobby.Lobby;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class StopCommand extends Command {
    public StopCommand() {
        super(Telegames.getInstance(), "/stop <game> Start a game.", "stop");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        User sender = event.getMessage().getSender();
        LobbyHandler lobbyHandler = getInstance().getLobbyHandler();

        if (event.getChat().getType() == ChatType.PRIVATE) {
            Lobby lobby = lobbyHandler.getLobby(sender);
            if (lobby != null) {
                Game lobbyGame = lobby.getCurrentGame();

                if (lobbyGame != null) {
                    lobby.stopGame();
                } else {
                    respond(chat, TelegramEmoji.RED_CROSS.getText() + " No game is running!");
                }
            } else {
                respond(chat, TelegramEmoji.RED_CROSS.getText() + " You are not in a lobby!");
            }
        } else {
            respond(chat, TelegramEmoji.RED_CROSS.getText() + " This command can only be executed via a private message to @" + TelegramHook.getBot().getBotUsername());
        }
    }
}