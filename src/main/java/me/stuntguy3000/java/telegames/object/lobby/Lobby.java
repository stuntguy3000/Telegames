package me.stuntguy3000.java.telegames.object.lobby;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import me.stuntguy3000.java.telegames.TelegramHook;
import me.stuntguy3000.java.telegames.game.Game;
import me.stuntguy3000.java.telegames.object.exception.LobbyInlineException;
import me.stuntguy3000.java.telegames.object.exception.LobbyLockedException;
import me.stuntguy3000.java.telegames.util.string.Lang;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.message.Message;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableMessage;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.user.User;

/**
 * @author stuntguy3000
 */
@Data
public class Lobby {
    private Game game;
    private HashMap<User, Message> lobbyUsers = new HashMap<>();
    private String lobbyID;
    private User lobbyOwner;
    private Message inlineMessage;
    private boolean matchmakingLobby = false;
    private boolean locked = false;

    /**
     * Creates a new Lobby instance, used for matchmaking
     *
     * @param matchmakingGame Game the game being played for this round of matchmaking
     */
    public Lobby(Game matchmakingGame) {
        setLobbyOwner(null);
        setMatchmakingLobby(true);
        setGame(matchmakingGame);
    }

    /**
     * Creates a standard Lobby instance
     *
     * @param lobbyOwner User the lobby's owner
     */
    public Lobby(User lobbyOwner) {
        setLobbyOwner(lobbyOwner);
        setMatchmakingLobby(false);
    }

    /**
     * Send a message to all Lobby players
     *
     * @param sendableMessage SendableMessage the message to be sent
     *
     * @throws LobbyInlineException thrown is the lobby cannot be messaged
     */
    public void messageLobby(SendableMessage sendableMessage) throws LobbyInlineException {
        if (getInlineMessage() != null) {
            throw new LobbyInlineException();
        } else {
            TelegramBot bot = TelegramHook.getBot();

            for (User user : lobbyUsers.keySet()) {
                bot.getChat(user.getId()).sendMessage(sendableMessage);
            }
        }
    }

    /**
     * Add a user to the Lobby
     *
     * @param user User the user to add
     *
     * @throws LobbyLockedException thrown if the lobby is locked
     */
    public void addUser(User user) throws LobbyLockedException {
        if (isLocked()) {
            throw new LobbyLockedException();
        } else {
            if (getInlineMessage() == null) {
                lobbyUsers.put(user, TelegramHook.getBot().getChat(user.getId()).sendMessage("Loading..."));

                try {
                    messageLobby(Lang.build(Lang.LOBBY_USER_JOIN, user.getUsername()).build());
                } catch (LobbyInlineException ignore) {
                    // Will never happen
                }
            } else {
                lobbyUsers.put(user, null);
            }

            updateLobbyInformation();
        }
    }

    /**
     * Remove a user from the Lobby
     *
     * @param user User the user to remove
     */
    public void removeUser(User user) {
        lobbyUsers.remove(user);

        if (getInlineMessage() == null) {
            try {
                messageLobby(Lang.build(Lang.LOBBY_USER_LEAVE, user.getUsername()).build());
            } catch (LobbyInlineException ignore) {
                // Will never happen
            }
        }

        updateLobbyInformation();
    }

    /**
     * Updates the Inline message for the bot.
     */
    public void updateLobbyInformation() {
        if (inlineMessage != null) {
            SendableTextMessage sendableTextMessage = getLobbyInformation();

            TelegramHook.getBot().editMessageText(inlineMessage,
                    sendableTextMessage.getMessage(),
                    sendableTextMessage.getParseMode(),
                    false,
                    null);
        }
    }

    /**
     * Returns a SendableTextMessage containing lobby information
     *
     * @return SendableTextMessage lobby information
     */
    public SendableTextMessage getLobbyInformation() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format(Lang.LOBBY_INFO_TITLE, getLobbyID()));
        stringBuilder.append("\n");

        if (isLocked()) {
            stringBuilder.append(Lang.LOBBY_INFO_LOCKED);
            stringBuilder.append("\n");
        }

        List<String> usernames = getLobbyUsers().keySet().stream().map(User::getUsername).collect(Collectors.toList());

        stringBuilder.append(String.format(Lang.LOBBY_INFO_OWNER, getLobbyOwner()));
        stringBuilder.append(String.format(Lang.LOBBY_INFO_PLAYERS, String.join(", ", usernames)));

        stringBuilder.append("\n");

        if (getGame() == null) {
            stringBuilder.append(Lang.LOBBY_INFO_WAITING);
        } else {
            stringBuilder.append(String.format(Lang.LOBBY_INFO_INGAME, getGame().getName()));
        }

        return SendableTextMessage.builder().message(stringBuilder.toString()).parseMode(ParseMode.MARKDOWN).build();
    }
}
