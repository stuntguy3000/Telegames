package me.stuntguy3000.java.telegames.object.lobby;

import lombok.Getter;
import lombok.Setter;
import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.KeyboardHandler;
import me.stuntguy3000.java.telegames.handler.LogHandler;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.exception.*;
import me.stuntguy3000.java.telegames.object.game.Game;
import me.stuntguy3000.java.telegames.object.user.TelegramUser;
import me.stuntguy3000.java.telegames.util.string.StringUtil;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.message.content.type.PhotoSize;
import pro.zackpollard.telegrambot.api.chat.message.send.*;
import pro.zackpollard.telegrambot.api.event.chat.message.PhotoMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardHide;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.ArrayList;
import java.util.List;

// @author Luke Anderson | stuntguy3000
public class Lobby {
    @Getter
    private Game currentGame;
    @Getter
    @Setter
    private String customName;
    @Getter
    @Setter
    private boolean isMatchmaking = false;
    @Getter
    private List<Integer> kickList;
    @Getter
    private long lastLobbyAction;
    private SendableTextMessage lobbyHeader;
    @Getter
    private String lobbyID;
    @Getter
    private List<TelegramUser> telegramUsers = new ArrayList<>();
    @Getter
    private LobbyOptions lobbyOptions = new LobbyOptions();
    @Getter
    private TelegramUser lobbyOwner;
    @Getter
    @Setter
    private Game matchmakingGame;
    @Getter
    @Setter
    private int maxPlayers = 0;
    @Getter
    private String previousGame;
    @Getter
    @Setter
    private boolean renamingLobby = false;

    /**
     * Constructs a new Lobby instance
     *
     * @param owner   User the owner of the Lobby
     * @param lobbyID String the Lobby's ID
     */
    public Lobby(User owner, String lobbyID) {
        this.lobbyOwner = new TelegramUser(owner);
        this.lobbyID = lobbyID;

        kickList = new ArrayList<>();
        lastLobbyAction = System.currentTimeMillis();
        updateHeader();
    }

    /**
     * Constructs a new Lobby instance <p>Used for Matchmaking</p>
     */
    public Lobby(Game game) {
        setMatchmaking(true);
        setMatchmakingGame(game);
        setMaxPlayers(game.getMaxPlayers());
    }

    /**
     * Returns a LobbyMember belonging to the username
     *
     * @param username String the username of the player
     */
    public TelegramUser getLobbyMember(String username) {
        for (TelegramUser telegramUser : getTelegramUsers()) {
            if (telegramUser.getUsername().equalsIgnoreCase(username)) {
                return telegramUser;
            }
        }

        return null;
    }

    /**
     * Returns a LobbyMember belonging to the username
     *
     * @param id Integer the ID of the player
     */
    public TelegramUser getLobbyMember(int id) {
        for (TelegramUser telegramUser : getTelegramUsers()) {
            if (telegramUser.getUserID() == id) {
                return telegramUser;
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
        for (TelegramUser telegramUser : telegramUsers) {
            if (telegramUser.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Kicks a player from a Lobby
     *
     * @param telegramUser the player to be kicked
     */
    public void kickPlayer(TelegramUser telegramUser) {
        sendMessage(SendableTextMessage.builder().message(TelegramEmoji.RED_CROSS.getText() + " *" + StringUtil.markdownSafe(telegramUser.getUsername()) + " was removed from the lobby!*").parseMode(ParseMode.MARKDOWN).build());
        userLeave(telegramUser, true);
        kickList.add(telegramUser.getUserID());
    }

    public void onPhotoMessageReceivedEvent(PhotoMessageReceivedEvent event) {
        PhotoSize[] sizes = event.getContent().getContent();
        PhotoSize lastPhoto = sizes[0];

        for (PhotoSize size : sizes) {
            if ((size.getWidth() * size.getHeight()) > (lastPhoto.getWidth() * lastPhoto.getHeight())) {
                lastPhoto = size;
            }
        }

        for (TelegramUser telegramUser : telegramUsers) {
            if (telegramUser.getUserID() != event.getMessage().getSender().getId()) {
                telegramUser.getChat().sendMessage(SendablePhotoMessage.builder().photo(new InputFile(lastPhoto.getFileId())).caption("Image from " + event.getMessage().getSender().getUsername()).build(), TelegramHook.getBot());
            }
        }
    }

    /**
     * Called when a TextMessage is received by TelegramBot
     *
     * @param event TextMessageReceivedEvent
     */
    public void onTextMessageReceived(TextMessageReceivedEvent event) {
        lastLobbyAction = System.currentTimeMillis();
        User sender = event.getMessage().getSender();
        String message = event.getContent().getContent();

        if (currentGame == null && !isMatchmaking) {
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
            } else if (message.equals(TelegramEmoji.REPLAY.getText() + " Replay previous game")) {
                if (currentGame == null && previousGame != null) {
                    startGame(Telegames.getInstance().getGameHandler().getGame(previousGame));
                }
            } else if (message.equals(TelegramEmoji.JOYSTICK.getText() + " Play a game")) {
                if (currentGame == null) {
                    event.getChat().sendMessage(KeyboardHandler.createGameSelector().message(TelegramEmoji.JOYSTICK.getText() + " *Please choose a game:*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                }
            } else if (message.equals(TelegramEmoji.END.getText() + " Leave the lobby")) {
                userLeave(getLobbyMember(sender.getUsername()), false);
            } else if (message.equals(TelegramEmoji.METAL_GEAR.getText() + " Lobby options")) {
                if (lobbyOwner.getUserID() == sender.getId()) {
                    event.getChat().sendMessage(KeyboardHandler.createLobbyOptionsMenu().message("Lobby Options").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                } else {
                    event.getChat().sendMessage(SendableTextMessage.builder().message(TelegramEmoji.RED_CROSS.getText() + " *You cannot perform this action!*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                }
            } else if (message.equals(TelegramEmoji.STAR.getText() + " Rate this bot")) {
                event.getChat().sendMessage(KeyboardHandler.createLobbyMenu(previousGame).message("To rate this bot, [click this link](http://telegram.me/storebot?start=telegamesbot)!\n\nIt will take less than a minute and every rating is appreciated!").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
            } else if (message.equals(TelegramEmoji.BOOK.getText() + " About")) {
                event.getChat().sendMessage(KeyboardHandler.createLobbyMenu(previousGame).message("Telegames is created by @stuntguy3000 to bring games to Telegram.\n\nType /version for more information.").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
            } else if (message.equals(TelegramEmoji.BACK.getText() + " Back to menu")) {
                if (currentGame == null) {
                    event.getChat().sendMessage(KeyboardHandler.createLobbyMenu(previousGame).message("You have returned to the menu.").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                }
            } else if (message.equals(TelegramEmoji.PADLOCK.getText() + " Lock/Unlock lobby")) {
                if (lobbyOwner.getUserID() == sender.getId()) {
                    boolean isLocked = getLobbyOptions().isLocked();

                    if (isLocked) {
                        event.getChat().sendMessage(KeyboardHandler.createLobbyOptionsMenu().message("The lobby has been *unlocked*.").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                    } else {
                        event.getChat().sendMessage(KeyboardHandler.createLobbyOptionsMenu().message("The lobby has been *locked*.").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                    }

                    lobbyOptions.setLocked(!isLocked);
                } else {
                    event.getChat().sendMessage(SendableTextMessage.builder().message(TelegramEmoji.RED_CROSS.getText() + " *You cannot perform this action!*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                }
            } else if (message.equals(TelegramEmoji.PENCIL.getText() + " Rename lobby")) {
                if (lobbyOwner.getUserID() == sender.getId()) {
                    event.getChat().sendMessage(KeyboardHandler.createCancelMenu().message("*Please enter the name of the lobby:*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                    renamingLobby = true;
                } else {
                    event.getChat().sendMessage(SendableTextMessage.builder().message(TelegramEmoji.RED_CROSS.getText() + " *You cannot perform this action!*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                }
            } else if (message.equals(TelegramEmoji.RED_CROSS.getText() + " Cancel")) {
                event.getChat().sendMessage(KeyboardHandler.createLobbyMenu(previousGame).message("*Returning to lobby menu*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                renamingLobby = false;
            } else if (sender.getId() == getLobbyOwner().getUserID() && renamingLobby) {
                String newName = message.replace(" ", "").toLowerCase();

                if (Telegames.getInstance().getLobbyHandler().lobbyExists(newName)) {
                    event.getChat().sendMessage(KeyboardHandler.createCancelMenu().message(TelegramEmoji.RED_CROSS.getText() + " *That name is already taken!\n\nPlease enter a different name.*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                } else {
                    renamingLobby = false;
                    customName = newName;
                    event.getChat().sendMessage(KeyboardHandler.createLobbyMenu(previousGame).message(TelegramEmoji.GREEN_BOX_TICK.getText() + " *The Lobby has been renamed to \"" + StringUtil.markdownSafe(customName) + "\"*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                }
            } else {
                userChat(sender, message);
            }
        } else {
            currentGame.onTextMessageReceived(event);
        }
    }

    /**
     * Send a message to all players in the lobby
     *
     * @param message SendableMessage the message to be sent
     */
    public void sendMessage(SendableMessage message) {
        for (TelegramUser telegramUser : telegramUsers) {
            telegramUser.getChat().sendMessage(message, getTelegramBot());
        }
    }

    /**
     * Send a message to all players in the lobby
     *
     * @param message String the message to be sent
     */
    public void sendMessage(String message) {
        for (TelegramUser telegramUser : telegramUsers) {
            telegramUser.getChat().sendMessage(message, getTelegramBot());
        }
    }

    /**
     * Starts a game in the Lobby
     *
     * @param targetGame Game the game to be played
     */
    public void startGame(Game targetGame) {
        renamingLobby = false;
        lastLobbyAction = System.currentTimeMillis();
        currentGame = null;
        try {
            Game newGame = targetGame.getClass().newInstance();
            newGame.setGameLobby(this);

            for (TelegramUser telegramUser : getTelegramUsers()) {
                telegramUser.setGameScore(0);
                try {
                    newGame.playerJoin(telegramUser);
                } catch (GameInProgressException e) {
                    LogHandler.error("GameInProgressException occurred on new game! Game: " + newGame.getGameName());
                }
            }

            sendMessage(SendableTextMessage.builder().message(TelegramEmoji.JOYSTICK.getText() + " *Starting game: " + newGame.getGameName() + "*").parseMode(ParseMode.MARKDOWN).replyMarkup(new ReplyKeyboardHide()).build());
            try {
                newGame.tryStartGame();
            } catch (GameStartException ex) {
                sendMessage(KeyboardHandler.createLobbyMenu(previousGame).message(TelegramEmoji.RED_CROSS.getText() + " *Unable to start game!\n" + ex.getReason() + "*").parseMode(ParseMode.MARKDOWN).build());
                return;
            }

            currentGame = newGame;
            Telegames.getInstance().getLobbyHandler().startTimer(this);
            Telegames.getInstance().getConfigHandler().getUserStatistics().addGame(newGame);
        } catch (InstantiationException | IllegalAccessException e) {
            LogHandler.error("InstantiationException or IllegalAccessException occurred!");
            e.printStackTrace();

            sendMessage(KeyboardHandler.createLobbyMenu(previousGame).message(TelegramEmoji.RED_CROSS.getText() + " *Unexpected Error Occurred! Contact @stuntguy3000*").parseMode(ParseMode.MARKDOWN).build());
        }
    }

    /**
     * Stop the current game
     */
    public void stopGame(User sender) {
        if (sender == null || lobbyOwner.getUserID() == sender.getId()) {
            lastLobbyAction = System.currentTimeMillis();
            currentGame.endGame();
            previousGame = currentGame.getGameName();
            currentGame = null;

            Telegames.getInstance().getLobbyHandler().stopTimer(this);

            if (!isMatchmaking) {
                for (TelegramUser telegramUser : getTelegramUsers()) {
                    SendableTextMessage sendableTextMessage = SendableTextMessage.builder().message(TelegramEmoji.PERSON.getText() + " *Returned to the main menu!*").parseMode(ParseMode.MARKDOWN).build();
                    TelegramBot.getChat(telegramUser.getUserID()).sendMessage(sendableTextMessage, TelegramHook.getBot());
                    userLeave(telegramUser, true);
                }
            } else {
                updateHeader();
                sendMessage(lobbyHeader);
            }
        } else {
            TelegramBot.getChat(sender.getId()).sendMessage(SendableTextMessage.builder().message(TelegramEmoji.RED_CROSS.getText() + " *You cannot perform this action!*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
        }
    }

    public void stopGame() {
        stopGame(null);
    }

    private void updateHeader() {
        lobbyHeader = KeyboardHandler.createLobbyMenu(previousGame).message(TelegramEmoji.SPACE_INVADER.getText() + " *" + StringUtil.markdownSafe(getLobbyOwner().getUsername()) + "'s Lobby* " + TelegramEmoji.SPACE_INVADER.getText()).parseMode(ParseMode.MARKDOWN).build();
    }

    /**
     * Called when a User sends a message for all users
     *
     * @param sender  User the message sender
     * @param message String the message
     */
    public void userChat(User sender, String message) {
        message = message.replace('*', ' ').replace('_', ' ');

        for (TelegramUser telegramUser : telegramUsers) {
            if (!telegramUser.getUsername().equalsIgnoreCase(sender.getUsername())) {
                telegramUser.getChat().sendMessage(SendableTextMessage.builder().message(TelegramEmoji.PERSON_SPEAKING.getText() + " *" + StringUtil.markdownSafe(sender.getUsername()) + ":* " + message).parseMode(ParseMode.MARKDOWN).build(), getTelegramBot());
            }
        }
    }

    /**
     * Called when a user joined this Lobby
     *
     * @param user User the user who joined the Lobby
     */
    public void userJoin(User user) throws LobbyLockedException, UserBannedException, LobbyFullException {
        lastLobbyAction = System.currentTimeMillis();

        if (kickList.contains(user.getId())) {
            throw new UserBannedException();
        }

        if (lobbyOptions.isLocked()) {
            throw new LobbyLockedException();
        }

        if (maxPlayers > 0 && getTelegramUsers().size() == maxPlayers) {
            throw new LobbyFullException();
        }

        TelegramUser telegramUser = new TelegramUser(user);
        telegramUsers.add(telegramUser);
        Game game = getCurrentGame();
        updateHeader();
        telegramUser.getChat().sendMessage(lobbyHeader, getTelegramBot());

        SendableTextMessage sendableTextMessage = SendableTextMessage.builder().message(TelegramEmoji.PERSON.getText() + " *" + StringUtil.markdownSafe(user.getUsername()) + " joined!*").parseMode(ParseMode.MARKDOWN).build();
        sendMessage(sendableTextMessage);

        //Telegames.getInstance().getConfigHandler().getLobbyList().addPlayer(getLobbyID(), lobbyMember.getUserID());
        Telegames.getInstance().getConfigHandler().getUserStatistics().addPlayer(user);

        if (game != null) {
            sendableTextMessage = SendableTextMessage.builder().message(TelegramEmoji.MONKEY_HIDING.getText() + " *You are spectating a game of " + game.getGameName() + ".*").parseMode(ParseMode.MARKDOWN).build();
            telegramUser.getChat().sendMessage(sendableTextMessage, getTelegramBot());
        }
    }

    /**
     * Called when a user left this lobby
     *
     * @param user User the user who left the Lobby
     */
    public void userLeave(TelegramUser user, boolean silent) {
        lastLobbyAction = System.currentTimeMillis();
        if (!silent) {
            for (TelegramUser telegramUser : telegramUsers) {
                if (telegramUser.getUserID() == user.getUserID()) {
                    SendableTextMessage sendableTextMessage = KeyboardHandler.createLobbyCreationMenu().message(TelegramEmoji.PERSON.getText() + " *" + StringUtil.markdownSafe(user.getUsername()) + " left!*").parseMode(ParseMode.MARKDOWN).build();
                    TelegramBot.getChat(telegramUser.getUserID()).sendMessage(sendableTextMessage, TelegramHook.getBot());
                } else {
                    SendableTextMessage sendableTextMessage = SendableTextMessage.builder().message(TelegramEmoji.PERSON.getText() + " *" + StringUtil.markdownSafe(user.getUsername()) + " left!*").parseMode(ParseMode.MARKDOWN).build();
                    TelegramBot.getChat(telegramUser.getUserID()).sendMessage(sendableTextMessage, TelegramHook.getBot());
                }
            }
        }

        String username = user.getUsername();
        int id = user.getUserID();

        for (TelegramUser telegramUser : new ArrayList<>(telegramUsers)) {
            if (telegramUser.getUserID() == user.getUserID()) {
                telegramUsers.remove(telegramUser);
            }
        }

        if (currentGame != null) {
            currentGame.playerLeave(username, id);
        }

        //Telegames.getInstance().getConfigHandler().getLobbyList().removePlayer(getLobbyID(), user.getUserID());

        if (telegramUsers.size() == 0) {
            Telegames.getInstance().getLobbyHandler().destroyLobby(lobbyID);
        }
    }
}
    