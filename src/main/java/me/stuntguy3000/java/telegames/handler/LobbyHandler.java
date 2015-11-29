package me.stuntguy3000.java.telegames.handler;

import lombok.Getter;
import lombok.Setter;
import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.Game;
import me.stuntguy3000.java.telegames.object.Lobby;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardHide;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.HashMap;

// @author Luke Anderson | stuntguy3000
public class LobbyHandler {
    @Getter
    @Setter
    private HashMap<String, Lobby> activeLobbies = new HashMap<>();
    @Getter
    @Setter
    private HashMap<Integer, String> userLobbies = new HashMap<>();

    /**
     * Return the User's current Lobby
     *
     * @param user User the requested user
     * @return Lobby the Lobby the user is in
     */
    public Lobby getLobby(User user) {
        if (user != null) {
            return getLobby(getUserLobbies().get(user.getId()));
        } else {
            return null;
        }
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

    /**
     * Add a User to a Lobby
     *
     * @param lobbyID String the Lobby's ID
     * @param user    User the specified user
     */
    public void userJoinLobby(String lobbyID, User user) {
        if (lobbyID != null && user != null) {
            lobbyID = lobbyID.toUpperCase();
            Lobby lobby = getLobby(lobbyID);

            if (lobby != null) {
                lobby.userJoin(user);
                Game game = lobby.getCurrentGame();

                if (game != null) {
                    if (!game.playerJoin(lobby.getPlayer(user.getUsername()), false)) {
                        lobby.userLeave(user, true);

                        SendableTextMessage sendableTextMessage = SendableTextMessage.builder()
                                .message("*You cannot join this Lobby while a game is in progress!*")
                                .parseMode(ParseMode.MARKDOWN)
                                .build();

                        lobby.getTelegramChat(user.getId()).sendMessage(sendableTextMessage, lobby.getTelegramBot());
                        return;
                    }
                }

                SendableTextMessage sendableTextMessage = SendableTextMessage.builder()
                        .message("*[Lobby]* User @" + user.getUsername() + " joined this lobby!")
                        .parseMode(ParseMode.NONE)
                        .replyMarkup(new ReplyKeyboardHide())
                        .build();
                lobby.sendMessage(sendableTextMessage);

                getUserLobbies().put(user.getId(), lobbyID);
            } else {
                throw new NullPointerException("Lobby " + lobbyID + " does not exist!");
            }
        }
    }

    /**
     * Remove a User from a Lobby
     *
     * @param user User the specified user
     */
    public void userLeaveLobby(User user) {
        if (user != null) {
            Lobby lobby = getLobby(user);

            if (lobby != null) {
                Game game = lobby.getCurrentGame();

                if (game != null) {
                    game.playerLeave(lobby.getPlayer(user.getUsername()), false);
                }

                getUserLobbies().remove(user.getId());
                lobby.userLeave(user, false);
            } else {
                throw new NullPointerException("User " + user.getUsername() + " is not in a lobby!");
            }
        }
    }

    /**
     * Creates a Lobby
     *
     * @param user User the owner of the Lobby
     */
    public void createLobby(User user) {
        Lobby lobby = new Lobby(user, Telegames.getInstance().getRandomString().nextString().toUpperCase());
        getActiveLobbies().put(lobby.getLobbyID(), lobby);

        SendableTextMessage sendableTextMessage = SendableTextMessage.builder()
                .message("*You have created a Lobby!*\n" +
                        "*Lobby ID: *" + lobby.getLobbyID())
                .parseMode(ParseMode.MARKDOWN)
                .build();

        lobby.getTelegramChat(user.getId()).sendMessage(sendableTextMessage, lobby.getTelegramBot());

        userJoinLobby(lobby.getLobbyID(), user);
    }

    /**
     * Destroys a Lobby
     *
     * @param lobbyID String the ID of the Lobby to destroy
     */
    public void destroyLobby(String lobbyID) {
        lobbyID = lobbyID.toUpperCase();
        getActiveLobbies().remove(lobbyID);

        final String finalLobbyID = lobbyID;
        getUserLobbies().entrySet().stream().filter(user -> user.getValue().equalsIgnoreCase(finalLobbyID)).forEach(user -> getUserLobbies().remove(user.getKey()));
    }
}
    