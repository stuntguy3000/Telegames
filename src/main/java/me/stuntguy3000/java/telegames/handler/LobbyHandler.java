package me.stuntguy3000.java.telegames.handler;

import java.util.HashMap;

import lombok.Data;
import me.stuntguy3000.java.telegames.object.exception.LobbyLockedException;
import me.stuntguy3000.java.telegames.object.lobby.Lobby;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.user.User;

/**
 * @author stuntguy3000
 */
@Data
public class LobbyHandler {
    private HashMap<String, Lobby> lobbyList = new HashMap<>();

    /**
     * Return a Lobby instance by ID
     *
     * @param id String the ID of the lobby
     *
     * @return Lobby the lobby instance
     */
    public Lobby getLobby(String id) {
        return lobbyList.get(id.toLowerCase());
    }

    /**
     * Return a Lobby instance by User
     *
     * @param user User the member of the lobby
     *
     * @return Lobby the lobby instance
     */
    public Lobby getLobby(User user) {
        for (Lobby lobby : lobbyList.values()) {
            for (User lobbyUser : lobby.getLobbyUsers().keySet()) {
                if (user.getId() == lobbyUser.getId()) {
                    return lobby;
                }
            }
        }
        return null;
    }

    /**
     * Add a Lobby to the lobbyList instance
     *
     * @param lobby Lobby the lobby to be added
     */
    public void addLobby(Lobby lobby) {
        lobbyList.put(lobby.getLobbyID(), lobby);
    }

    /**
     * Create a new Lobby
     *
     * @param owner User the owner of the lobby
     * @param chat  The chat it was created in
     *
     * @return Lobby the created lobby
     */
    public Lobby createLobby(User owner, Chat chat) {
        long chatID;
        try {
            chatID = Long.parseLong(chat.getId());
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return null;
        }

        Lobby lobby = new Lobby(owner);

        if (chatID < 0) {
            lobby.setInlineMessage(chat.sendMessage("Loading..."));
        }

        try {
            addLobby(lobby);
            lobby.addUser(owner);
        } catch (LobbyLockedException ignore) {
            // Will never happen
        }

        return lobby;
    }
}
