package me.stuntguy3000.java.telegames.object;

import lombok.Getter;
import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardHide;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.ArrayList;
import java.util.List;

// @author Luke Anderson | stuntguy3000
public class Lobby {
    @Getter
    private Game currentGame;
    private SendableTextMessage lobbyHeader;
    @Getter
    private String lobbyID;
    @Getter
    private List<LobbyMember> lobbyMembers = new ArrayList<>();
    @Getter
    private LobbyMember lobbyOwner;

    /**
     * Constructs a new Lobby instance
     *
     * @param owner   User the owner of the Lobby
     * @param lobbyID String the Lobby's ID
     */
    public Lobby(User owner, String lobbyID) {
        this.lobbyOwner = new LobbyMember(owner);
        this.lobbyID = lobbyID;

        lobbyHeader = SendableTextMessage.builder().message("*[---------- " + owner.getUsername() + "'s Lobby ----------]*").parseMode(ParseMode.MARKDOWN).build();
    }

    /**
     * Returns a LobbyMember belonging to the username
     *
     * @param username String the username of the player
     */
    public LobbyMember getLobbyMember(String username) {
        for (LobbyMember lobbyMember : getLobbyMembers()) {
            if (lobbyMember.getUsername().equals(username)) {
                return lobbyMember;
            }
        }

        return null;
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
     * Returns if a User is in the Lobby
     *
     * @param username String the specified user
     * @return True if user is in the Lobby
     */
    public boolean isInLobby(String username) {
        for (LobbyMember lobbyMember : lobbyMembers) {
            if (lobbyMember.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Called when a TextMessage is received by TelegramBot
     *
     * @param event TextMessageReceivedEvent
     */
    public void onTextMessageReceived(TextMessageReceivedEvent event) {
        String message = event.getContent().getContent();

        if (currentGame == null) {
            userChat(event.getMessage().getSender(), message);
        } else {
            currentGame.onTextMessageReceived(event);
        }
    }

    /**
     * Send a message to all players in the lobby
     *
     * @param message SendableTextMessage the message to be sent
     */
    public void sendMessage(SendableTextMessage message) {
        for (LobbyMember lobbyMember : lobbyMembers) {
            lobbyMember.getChat().sendMessage(message, getTelegramBot());
        }
    }

    /**
     * Send a message to all players in the lobby
     *
     * @param message String the message to be sent
     */
    public void sendMessage(String message) {
        for (LobbyMember lobbyMember : lobbyMembers) {
            lobbyMember.getChat().sendMessage(message, getTelegramBot());
        }
    }

    /**
     * Starts a game in the Lobby
     *
     * @param targetGame Game the game to be played
     */
    public void startGame(Game targetGame) {
        try {
            Game newGame = targetGame.getClass().newInstance();
            newGame.setGameLobby(this);

            for (LobbyMember lobbyMember : getLobbyMembers()) {
                newGame.playerJoin(lobbyMember);
            }

            if (newGame.tryStartGame()) {
                currentGame = newGame;
            } else {
                sendMessage(SendableTextMessage.builder().message("*Unable to start game!*").parseMode(ParseMode.MARKDOWN).build());
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();

            sendMessage(SendableTextMessage.builder().message("*Unexpected Error Occurred! Contact @stuntguy3000*").parseMode(ParseMode.MARKDOWN).build());
        }
    }

    public void stopGame() {
        currentGame.endGame();
        currentGame = null;

        sendMessage(lobbyHeader);
    }

    /**
     * Called when a User sends a message for all users
     *
     * @param sender  User the message sender
     * @param message String the message
     */
    public void userChat(User sender, String message) {
        for (LobbyMember lobbyMember : lobbyMembers) {
            if (!lobbyMember.getUsername().equals(sender.getUsername())) {
                lobbyMember.getChat().sendMessage(SendableTextMessage.builder().message("*[Chat]* " + sender.getUsername() + ": " + message).parseMode(ParseMode.MARKDOWN).build(), getTelegramBot());
            }
        }
    }

    /**
     * Called when a user joined this Lobby
     *
     * @param user User the user who joined the Lobby
     */
    public void userJoin(User user) {
        LobbyMember lobbyMember = new LobbyMember(user);
        lobbyMembers.add(lobbyMember);
        Game game = getCurrentGame();
        lobbyMember.getChat().sendMessage(lobbyHeader, getTelegramBot());

        SendableTextMessage sendableTextMessage = SendableTextMessage.builder().message("*[Lobby]* User @" + user.getUsername() + " joined this lobby!").parseMode(ParseMode.MARKDOWN).replyMarkup(new ReplyKeyboardHide()).build();
        sendMessage(sendableTextMessage);

        if (game != null) {
            sendableTextMessage = SendableTextMessage.builder().message("You are spectating a game of " + game.getGameName() + ".").parseMode(ParseMode.MARKDOWN).build();
            lobbyMember.getChat().sendMessage(sendableTextMessage, getTelegramBot());
        }
    }

    /**
     * Called when a user left this lobby
     *
     * @param user User the user who left the Lobby
     */
    public void userLeave(LobbyMember user) {
        SendableTextMessage sendableTextMessage = SendableTextMessage.builder().message("*[Lobby]* User @" + user.getUsername() + " (" + user.getFullName() + ") left this lobby!").parseMode(ParseMode.MARKDOWN).build();
        sendMessage(sendableTextMessage);

        String username = user.getUsername();
        int id = user.getUserID();

        for (LobbyMember lobbyMember : new ArrayList<>(lobbyMembers)) {
            if (lobbyMember.getUsername().equals(user.getUsername())) {
                lobbyMembers.remove(lobbyMember);
            }
        }

        if (currentGame != null) {
            currentGame.playerLeave(username, id);
        }

        if (lobbyMembers.size() == 0) {
            Telegames.getInstance().getLobbyHandler().destroyLobby(lobbyID);
        }
    }
}
    