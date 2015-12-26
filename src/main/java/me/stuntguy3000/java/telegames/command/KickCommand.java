package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.command.Command;
import me.stuntguy3000.java.telegames.object.lobby.Lobby;
import me.stuntguy3000.java.telegames.object.user.TelegramUser;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
import me.stuntguy3000.java.telegames.util.string.Lang;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class KickCommand extends Command {
    public KickCommand() {
        super(Telegames.getInstance(), "/kick <name> Remove a player from the lobby.", "kick");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        User sender = event.getMessage().getSender();
        LobbyHandler lobbyHandler = getInstance().getLobbyHandler();
        String[] args = event.getArgs();

        if (event.getChat().getType() == ChatType.PRIVATE) {
            Lobby lobby = lobbyHandler.getLobby(sender);
            if (lobby != null) {
                if (args.length > 0) {
                    TelegramUser telegramUser = lobby.getLobbyMember(args[0]);
                    if (telegramUser != null) {
                        if (lobby.getLobbyOwner().getUserID() == sender.getId() && telegramUser.getUserID() != sender.getId()) {
                            lobby.kickPlayer(telegramUser);
                        } else {
                            respond(chat, Lang.COMMAND_KICK_UNKICKABLE);
                        }
                    } else {
                        respond(chat, Lang.ERROR_PLAYER_NOT_FOUND);
                    }
                } else {
                    respond(chat, String.format(Lang.ERROR_SYNTAX_INVALID, "kick", "<name>"));
                }
            } else {
                respond(chat, Lang.ERROR_USER_NOT_IN_LOBBY);
            }
        } else {
            respond(chat, Lang.ERROR_COMMAND_PM_ONLY);
        }
    }
}