package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;
import me.stuntguy3000.java.telegames.object.command.Command;
import me.stuntguy3000.java.telegames.object.game.Game;
import me.stuntguy3000.java.telegames.object.lobby.Lobby;
import me.stuntguy3000.java.telegames.object.user.TelegramUser;
import me.stuntguy3000.java.telegames.util.string.Lang;
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
        TelegramUser user = new TelegramUser(sender);
        LobbyHandler lobbyHandler = getInstance().getLobbyHandler();

        if (event.getChat().getType() == ChatType.PRIVATE) {
            Lobby lobby = lobbyHandler.getLobby(user);
            if (lobby != null) {
                Game lobbyGame = lobby.getCurrentGame();

                if (lobbyGame != null) {
                    if (lobby.getLobbyOwner().getUserID() == sender.getId()) {
                        lobby.stopGame();
                    } else {
                        respond(chat, Lang.ERROR_NOT_AUTH);
                    }
                } else {
                    respond(chat, Lang.ERROR_GAME_NOT_RUNNING);
                }
            } else {
                respond(chat, Lang.ERROR_USER_NOT_IN_LOBBY);
            }
        } else {
            respond(chat, Lang.ERROR_COMMAND_PM_ONLY);
        }
    }
}