package me.stuntguy3000.java.telegames.object;

import lombok.Getter;
import lombok.Setter;
import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.ArrayList;
import java.util.List;

// @author Luke Anderson | stuntguy3000
public class Lobby {
    @Getter
    @Setter
    private String lobbyID;

    @Getter
    @Setter
    private User lobbyOwner;

    @Getter
    @Setter
    private List<Player> lobbyPlayers;

    @Getter
    @Setter
    private Game currentGame;

    /**
     * Constructs a new Lobby instance
     *
     * @param owner   User the owner of the Lobby
     * @param lobbyID String the Lobby's ID
     */
    public Lobby(User owner, String lobbyID) {
        setLobbyOwner(owner);
        setLobbyID(lobbyID);
        lobbyPlayers = new ArrayList<>();
    }

    /**
     * Send a message to all players in the lobby
     *
     * @param message SendableTextMessage the message to be sent
     */
    public void sendMessage(SendableTextMessage message) {
        for (Player player : lobbyPlayers) {
            getTelegramChat(player.getUserID()).sendMessage(message, getTelegramBot());
        }
    }

    /**
     * Send a message to all players in the lobby
     *
     * @param message String the message to be sent
     */
    public void sendMessage(String message) {
        for (Player player : lobbyPlayers) {
            getTelegramChat(player.getUserID()).sendMessage(message, getTelegramBot());
        }
    }

    /**
     * Send a message to a specific player
     *
     * @param message SendableTextMessage the message to be sent
     */
    public void sendMessage(int chatID, SendableTextMessage message) {
        getTelegramChat(chatID).sendMessage(message, getTelegramBot());
    }

    /**
     * Send a message to specific player
     *
     * @param message String the message to be sent
     */
    public void sendMessage(int chatID, String message) {
        getTelegramChat(chatID).sendMessage(message, getTelegramBot());
    }

    /**
     * Returns the active TelegramBot instance
     *
     * @return TelegramBot active TelegramBot instance
     */
    public TelegramBot getTelegramBot() {
        return TelegramHook.getBot();
    }

    /**
     * Returns a Telegram Chat object
     *
     * @param id Integer the ID of the chat
     * @return Chat Telegram chat object
     */
    public Chat getTelegramChat(int id) {
        return TelegramBot.getChat(id);
    }

    /**
     * Called when a user joined this Lobby
     *
     * @param user User the user who joined the Lobby
     */
    public void userJoin(User user) {
        getLobbyPlayers().add(new Player(user));
    }

    /**
     * Called when a user left this lobby
     *
     * @param user   User the user who left the Lobby
     * @param silent Boolean if any messages should be sent
     */
    public void userLeave(User user, boolean silent) {
        new ArrayList<>(getLobbyPlayers()).stream().filter(player -> player.getUsername().equals(user.getUsername())).forEach(player -> {
            getLobbyPlayers().remove(player);
        });

        if (!silent) {
            SendableTextMessage sendableTextMessage = SendableTextMessage.builder()
                    .message("*[Lobby]* User @" + user.getUsername() + " (" + user.getFullName() + ") left this lobby!")
                    .parseMode(ParseMode.MARKDOWN)
                    .build();
            sendMessage(sendableTextMessage);
        }

        if (getLobbyPlayers().size() == 0) {
            Telegames.getInstance().getLobbyHandler().destroyLobby(getLobbyID());
        }
    }

    /**
     * Returns if a User is in the Lobby
     *
     * @param username String the specified user
     * @return True if user is in the Lobby
     */
    public boolean isInLobby(String username) {
        for (Player player : getLobbyPlayers()) {
            if (player.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Called when a TextMessage is received by TelegramBot
     *
     * @param event
     */
    public void onTextMessageReceived(TextMessageReceivedEvent event) {
        String message = event.getContent().getContent();

        if (getCurrentGame() == null) {
            userChat(event.getMessage().getSender(), message);
        } else {
            getCurrentGame().onTextMessageReceived(event);
        }
    }

    /**
     * Called when a User sends a message for all users
     *
     * @param sender  User the message sender
     * @param message String the message
     */
    public void userChat(User sender, String message) {
        getLobbyPlayers().stream().filter(player -> !player.getUsername().equals(sender.getUsername())).forEach(player -> {
            sendMessage(player.getUserID(),
                    SendableTextMessage.builder()
                            .message("*[Chat]* " + sender.getUsername() + ": " + message)
                            .parseMode(ParseMode.MARKDOWN)
                            .build()
            );
        });
    }

    /**
     * Starts a game in the Lobby
     *
     * @param targetGame Game the game to be played
     */
    public void startGame(Game targetGame) {
        try {
            Game newGame = targetGame.getClass().newInstance();
            newGame.setLobby(this);

            for (Player player : getLobbyPlayers()) {
                newGame.playerJoin(player, true);
            }

            if (newGame.startGame()) {
                currentGame = newGame;
            } else {
                sendMessage(
                        SendableTextMessage.builder()
                                .message("*Unable to start game!*")
                                .parseMode(ParseMode.MARKDOWN)
                                .build()
                );
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();

            sendMessage(
                    SendableTextMessage.builder()
                            .message("*Unexpected Error Occurred! Contact @stuntguy3000*")
                            .parseMode(ParseMode.MARKDOWN)
                            .build()
            );
        }
    }

    /**
     * Returns a Player instance
     *
     * @param userName String the requested username
     * @return Player
     */
    public Player getPlayer(String userName) {
        for (Player player : getLobbyPlayers()) {
            if (player.getUsername().equalsIgnoreCase(userName)) {
                return player;
            }
        }

        return null;
    }

    /**
     * Stops the current game
     *
     * @param silent Boolean true to display messages
     */
    public void stopGame(boolean silent) {
        getCurrentGame().stopGame(silent);
        setCurrentGame(null);
    }
}
    