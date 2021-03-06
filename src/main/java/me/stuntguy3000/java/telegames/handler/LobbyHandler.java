package me.stuntguy3000.java.telegames.handler;

import lombok.Getter;
import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.exception.*;
import me.stuntguy3000.java.telegames.object.game.Game;
import me.stuntguy3000.java.telegames.object.lobby.Lobby;
import me.stuntguy3000.java.telegames.object.timer.LobbySecondTimer;
import me.stuntguy3000.java.telegames.object.user.TelegramUser;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;

import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

// @author Luke Anderson | stuntguy3000
public class LobbyHandler {
    @Getter
    private HashMap<String, Lobby> activeLobbies = new HashMap<>();
    @Getter
    private HashMap<String, TimerTask> lobbyTimers = new HashMap<>();

    /**
     * Creates a Lobby
     *
     * @param user User the owner of the Lobby
     */
    private Lobby createLobby(TelegramUser user) {
        Lobby lobby = new Lobby(user, Telegames.getInstance().getRandomString().nextString().toUpperCase());
        activeLobbies.put(lobby.getLobbyID(), lobby);

        SendableTextMessage sendableTextMessage = SendableTextMessage.builder().message("*You have created a lobby!*\n" +
                "[Send this link to your friends to play!](http://telegram.me/" + TelegramHook.getBot().getBotUsername() + "?start=" + lobby.getLobbyID() + ")").parseMode(ParseMode.MARKDOWN).disableWebPagePreview(true).build();

        TelegramBot.getChat(user.getUserID()).sendMessage(sendableTextMessage, lobby.getTelegramBot());
        try {
            lobby.userJoin(user);
        } catch (LobbyLockedException | UserBannedException | LobbyFullException ignore) {

        }

        //Telegames.getInstance().getConfigHandler().getLobbyList().addLobby(lobby.getLobbyID(), lobby.getTelegramUsers());
        return lobby;
    }

    public void createLobby(String lobbyID, List<Long> playerList) {
        // TODO: Need a way to get a User [specifically a username]
        // Feature was intended for on bot reboot, reconnect users.
    }

    /**
     * Creates a Lobby used for matchmaking
     *
     * @param game
     * @return
     */
    public Lobby createMatchmakingLobby(Game game) {
        Lobby lobby = new Lobby(game);
        activeLobbies.put(lobby.getLobbyID(), lobby);
        return lobby;
    }

    /**
     * Destroys a Lobby
     *
     * @param lobbyID String the ID of the Lobby to destroy
     */
    public void destroyLobby(String lobbyID) {
        lobbyID = lobbyID.toUpperCase();
        activeLobbies.remove(lobbyID);

        //Telegames.getInstance().getConfigHandler().getLobbyList().removeLobby(lobbyID);
    }

    /**
     * Expires a lobby after about 10min of inactivity
     *
     * @param lobby Lobby the Lobby to destroy
     */
    public void expireLobby(Lobby lobby) {
        lobby.sendMessage(KeyboardHandler.createLobbyCreationMenu().message("\n\n*This lobby has expired!*\n\n").parseMode(ParseMode.MARKDOWN).build());

        destroyLobby(lobby.getLobbyID());
    }

    /**
     * Returns a Lobby with the associated ID
     *
     * @param lobbyID String the requested lobby's ID
     * @return Lobby the associated Lobby
     */
    public Lobby getLobby(String lobbyID) {
        if (lobbyID != null && lobbyID != null) {
            for (Lobby lobby : activeLobbies.values()) {
                if (lobby.getLobbyID().equalsIgnoreCase(lobbyID) || (lobby.getCustomName() != null && lobby.getCustomName().equalsIgnoreCase(lobbyID))) {
                    return lobby;
                }
            }
        }

        return null;
    }

    /**
     * Return the User's current Lobby
     *
     * @param user User the requested user
     * @return Lobby the Lobby the user is in
     */
    public Lobby getLobby(TelegramUser user) {
        if (user != null) {
            for (Lobby lobby : activeLobbies.values()) {
                if (lobby.isInLobby(user.getUsername())) {
                    return lobby;
                }
            }
        }

        return null;
    }

    /**
     * Returns if a lobby exists
     *
     * @param newName String the custom name of the lobby
     * @return true if exists
     */
    public boolean lobbyExists(String newName) {
        for (Lobby lobby : getActiveLobbies().values()) {
            if (lobby.getCustomName() != null && lobby.getCustomName().equalsIgnoreCase(newName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Starts a LobbySecondTimer instance
     *
     * @param lobby Lobby the associated Lobby
     */
    public void startTimer(Lobby lobby) {
        if (!lobbyTimers.containsKey(lobby.getLobbyID().toLowerCase())) {
            lobbyTimers.put(lobby.getLobbyID().toLowerCase(), new LobbySecondTimer(lobby));
        }
    }

    /**
     * Stops a GameSecondTimer instance
     *
     * @param lobby Lobby the associated Lobby
     */
    public void stopTimer(Lobby lobby) {
        if (lobbyTimers.containsKey(lobby.getLobbyID().toLowerCase())) {
            lobbyTimers.remove(lobby.getLobbyID().toLowerCase()).cancel();
        }
    }

    public Lobby tryCreateLobby(TelegramUser user) throws UserIsMatchmakingException, UserHasLobbyException {
        if (getLobby(user) != null) {
            throw new UserHasLobbyException();
        }

        if (Telegames.getInstance().getMatchmakingHandler().isInQueue(user)) {
            throw new UserIsMatchmakingException();
        }

        return createLobby(user);
    }
}
