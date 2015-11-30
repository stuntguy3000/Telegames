package me.stuntguy3000.java.telegames.handler;

import lombok.Getter;
import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.Lobby;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.HashMap;

// @author Luke Anderson | stuntguy3000
public class LobbyHandler {
    @Getter
    private HashMap<String, Lobby> activeLobbies = new HashMap<>();

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
     * Creates a Lobby
     *
     * @param user User the owner of the Lobby
     */
    public void createLobby(User user) {
        Lobby lobby = new Lobby(user, Telegames.getInstance().getRandomString().nextString().toUpperCase());
        activeLobbies.put(lobby.getLobbyID(), lobby);

        SendableTextMessage sendableTextMessage = SendableTextMessage.builder()
                .message("*You have created a Lobby!*\n" +
                        "*Lobby ID: *" + lobby.getLobbyID())
                .parseMode(ParseMode.MARKDOWN)
                .build();

        TelegramBot.getChat(user.getId()).sendMessage(sendableTextMessage, lobby.getTelegramBot());
        lobby.userJoin(user);
    }

    /**
     * Destroys a Lobby
     *
     * @param lobbyID String the ID of the Lobby to destroy
     */
    public void destroyLobby(String lobbyID) {
        lobbyID = lobbyID.toUpperCase();
        activeLobbies.remove(lobbyID);
    }

    /**
     * Returns a Lobby with the associated ID
     *
     * @param lobbyID String the requested lobby's ID
     * @return Lobby the associated Lobby
     */
    public Lobby getLobby(String lobbyID) {
        if (lobbyID != null) {
            return getActiveLobbies().get(lobbyID.toUpperCase());
        } else {
            return null;
        }
    }
}
