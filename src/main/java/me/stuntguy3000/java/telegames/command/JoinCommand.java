package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.KeyboardHandler;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;
import me.stuntguy3000.java.telegames.object.command.Command;
import me.stuntguy3000.java.telegames.object.exception.LobbyFullException;
import me.stuntguy3000.java.telegames.object.exception.LobbyLockedException;
import me.stuntguy3000.java.telegames.object.exception.UserBannedException;
import me.stuntguy3000.java.telegames.object.lobby.Lobby;
import me.stuntguy3000.java.telegames.object.user.TelegramUser;
import me.stuntguy3000.java.telegames.util.string.Lang;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class JoinCommand extends Command {
    public JoinCommand() {
        super(Telegames.getInstance(), "/join <ID> Join a lobby.", "join");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        User sender = event.getMessage().getSender();
        TelegramUser user = new TelegramUser(sender);
        LobbyHandler lobbyHandler = getInstance().getLobbyHandler();
        String[] args = event.getArgs();

        if (event.getChat().getType() == ChatType.PRIVATE) {
            if (lobbyHandler.getLobby(user) == null) {
                if (args.length > 0) {
                    String id = args[0];
                    Lobby targetLobby = lobbyHandler.getLobby(id);

                    if (targetLobby == null) {
                        respond(chat, Lang.ERROR_LOBBY_NOT_FOUND);
                    } else {
                        try {
                            targetLobby.userJoin(user);
                        } catch (LobbyLockedException | UserBannedException | LobbyFullException e) {
                            SendableTextMessage sendableTextMessage = KeyboardHandler.createLobbyCreationMenu().message(Lang.ERROR_CANNOT_JOIN_LOBBY).parseMode(ParseMode.MARKDOWN).build();
                            respond(chat, sendableTextMessage);
                        }
                    }
                } else {
                    respond(chat, String.format(Lang.ERROR_SYNTAX_INVALID, "joinlobby", "<ID>"));
                }
            } else {
                respond(chat, Lang.ERROR_USER_IN_LOBBY);
            }
        } else {
            respond(chat, Lang.ERROR_COMMAND_PM_ONLY);
        }
    }
}