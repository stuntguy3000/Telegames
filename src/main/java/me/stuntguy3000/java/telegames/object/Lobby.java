package me.stuntguy3000.java.telegames.object;

import lombok.Getter;
import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
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
            if (lobbyMember.getUsername().equalsIgnoreCase(username)) {
                return lobbyMember;
            }
        }

        return null;
    }

    /**
     * Returns a LobbyMember belonging to the username
     *
     * @param id Integer the ID of the player
     */
    public LobbyMember getLobbyMember(int id) {
        for (LobbyMember lobbyMember : getLobbyMembers()) {
            if (lobbyMember.getUserID() == id) {
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
            if (lobbyMember.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Kicks a player from a Lobby
     *
     * @param lobbyMember the player to be kicked
     */
    public void kickPlayer(LobbyMember lobbyMember) {
        sendMessage(SendableTextMessage.builder().message(TelegramEmoji.RED_CROSS.getText() + " *" + lobbyMember.getUsername() + " was removed from the lobby!*").parseMode(ParseMode.MARKDOWN).build());
        userLeave(lobbyMember, true);
    }

    /**
     * Called when a TextMessage is received by TelegramBot
     *
     * @param event TextMessageReceivedEvent
     */
    public void onTextMessageReceived(TextMessageReceivedEvent event) {
        String message = event.getContent().getContent();

        if (currentGame == null) {
            if (message.startsWith("▶️ ")) {
                int indexToRemove = 0;

                for (char c : message.toCharArray()) {
                    if ((c >= 'a' && c <= 'z') ||
                            (c >= 'A' && c <= 'Z') ||
                            (c >= '0' && c <= '9')) {
                        break;
                    }

                    indexToRemove++;
                }

                message = message.substring(indexToRemove);

                Game targetGame = Telegames.getInstance().getGameHandler().getGame(message);

                if (targetGame != null) {
                    startGame(targetGame);
                } else {
                    event.getChat().sendMessage("Unknown game!\nUse /gamelist for help.", TelegramHook.getBot());
                }
            } else {
                userChat(event.getMessage().getSender(), message);
            }
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

            sendMessage(SendableTextMessage.builder().message(TelegramEmoji.JOYSTICK.getText() + " *Starting game: " + newGame.getGameName() + "*").parseMode(ParseMode.MARKDOWN).build());
            if (newGame.tryStartGame()) {
                currentGame = newGame;
            } else {
                sendMessage(SendableTextMessage.builder().message(TelegramEmoji.RED_CROSS.getText() + " *Unable to start game!*").parseMode(ParseMode.MARKDOWN).build());
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();

            sendMessage(SendableTextMessage.builder().message(TelegramEmoji.RED_CROSS.getText() + " *Unexpected Error Occurred! Contact @stuntguy3000*").parseMode(ParseMode.MARKDOWN).build());
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
        message = message.replace('*', ' ').replace('_', ' ');
        // .replace(":)", TelegramEmoji.HAPPY_FACE.getText()).replace(":(", TelegramEmoji.SAD_FACE.getText());

        for (LobbyMember lobbyMember : lobbyMembers) {
            if (!lobbyMember.getUsername().equalsIgnoreCase(sender.getUsername())) {
                lobbyMember.getChat().sendMessage(SendableTextMessage.builder().message(TelegramEmoji.PERSON_SPEAKING.getText() + " *" + sender.getUsername() + ":* " + message).parseMode(ParseMode.MARKDOWN).build(), getTelegramBot());
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

        SendableTextMessage sendableTextMessage = SendableTextMessage.builder().message(TelegramEmoji.PERSON.getText() + " *" + user.getUsername() + " joined!*").parseMode(ParseMode.MARKDOWN).replyMarkup(new ReplyKeyboardHide()).build();
        sendMessage(sendableTextMessage);

        //Telegames.getInstance().getConfigHandler().getLobbyList().addPlayer(getLobbyID(), lobbyMember.getUserID());

        if (game != null) {
            sendableTextMessage = SendableTextMessage.builder().message(TelegramEmoji.MONKEY_HIDING.getText() + " *You are spectating a game of " + game.getGameName() + ".*").parseMode(ParseMode.MARKDOWN).build();
            lobbyMember.getChat().sendMessage(sendableTextMessage, getTelegramBot());
        }
    }

    /**
     * Called when a user left this lobby
     *
     * @param user User the user who left the Lobby
     */
    public void userLeave(LobbyMember user, boolean silent) {
        if (!silent) {
            SendableTextMessage sendableTextMessage = SendableTextMessage.builder().message(TelegramEmoji.PERSON.getText() + " *" + user.getUsername() + " left!*").parseMode(ParseMode.MARKDOWN).build();
            sendMessage(sendableTextMessage);
        }

        String username = user.getUsername();
        int id = user.getUserID();

        for (LobbyMember lobbyMember : new ArrayList<>(lobbyMembers)) {
            if (lobbyMember.getUsername().equalsIgnoreCase(user.getUsername())) {
                lobbyMembers.remove(lobbyMember);
            }
        }

        if (currentGame != null) {
            currentGame.playerLeave(username, id);
        }

        //Telegames.getInstance().getConfigHandler().getLobbyList().removePlayer(getLobbyID(), user.getUserID());

        if (lobbyMembers.size() == 0) {
            Telegames.getInstance().getLobbyHandler().destroyLobby(lobbyID);
        }
    }
}
    