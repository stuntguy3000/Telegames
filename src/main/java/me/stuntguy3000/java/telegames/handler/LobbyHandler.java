package me.stuntguy3000.java.telegames.handler;

import lombok.Getter;
import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.Lobby;
import me.stuntguy3000.java.telegames.object.timer.GameSecondTimer;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardMarkup;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.*;

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
    public Lobby createLobby(User user) {
        Lobby lobby = new Lobby(user, Telegames.getInstance().getRandomString().nextString().toUpperCase());
        activeLobbies.put(lobby.getLobbyID(), lobby);

        SendableTextMessage sendableTextMessage = SendableTextMessage.builder().message("*You have created a lobby!*\n" +
                "[Send this link to your friends to play!](http://telegram.me/" + TelegramHook.getBot().getBotUsername() + "?start=" + lobby.getLobbyID() + ")").parseMode(ParseMode.MARKDOWN).build();

        TelegramBot.getChat(user.getId()).sendMessage(sendableTextMessage, lobby.getTelegramBot());
        lobby.userJoin(user);

        //Telegames.getInstance().getConfigHandler().getLobbyList().addLobby(lobby.getLobbyID(), lobby.getLobbyMembers());
        return lobby;
    }

    public void createLobby(String lobbyID, List<Integer> playerList) {
        // TODO: Need a way to get a User [specifically a username]
        // Feature was intended for on bot reboot, reconnect users.
    }

    /**
     * Returns a Lobby creation menu
     *
     * @return
     */
    public SendableTextMessage.SendableTextMessageBuilder createLobbyCreationMenu() {
        List<List<String>> buttonList = new ArrayList<>();

        buttonList.add(Collections.singletonList(TelegramEmoji.JOYSTICK.getText() + " Create a lobby"));
        buttonList.add(Collections.singletonList(TelegramEmoji.PERSON.getText() + " Join a lobby"));

        return SendableTextMessage.builder().replyMarkup(new ReplyKeyboardMarkup(buttonList, true, false, false));
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
     * Expires a lobby after 10min of inactivity
     *
     * @param lobby Lobby the Lobby to destroy
     */
    public void expireLobby(Lobby lobby) {
        lobby.sendMessage(createLobbyCreationMenu().message("\n\n*This lobby has expired!*\n\n").parseMode(ParseMode.MARKDOWN).build());

        destroyLobby(lobby.getLobbyID());
    }

    /**
     * Returns a Lobby with the associated ID
     *
     * @param lobbyID String the requested lobby's ID
     * @return Lobby the associated Lobby
     */
    public Lobby getLobby(String lobbyID) {
        if (lobbyID != null) {
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
    public Lobby getLobby(User user) {
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
     * Starts a GameSecondTimer instance
     *
     * @param lobby Lobby the associated Lobby
     */
    public void startTimer(Lobby lobby) {
        if (!lobbyTimers.containsKey(lobby.getLobbyID().toLowerCase())) {
            lobbyTimers.put(lobby.getLobbyID().toLowerCase(), new GameSecondTimer(lobby.getCurrentGame()));
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
}
