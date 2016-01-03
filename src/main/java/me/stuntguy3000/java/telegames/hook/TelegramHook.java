package me.stuntguy3000.java.telegames.hook;

import lombok.Getter;
import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.KeyboardHandler;
import me.stuntguy3000.java.telegames.handler.LogHandler;
import me.stuntguy3000.java.telegames.handler.MatchmakingHandler;
import me.stuntguy3000.java.telegames.object.command.Command;
import me.stuntguy3000.java.telegames.object.config.LobbyList;
import me.stuntguy3000.java.telegames.object.exception.*;
import me.stuntguy3000.java.telegames.object.game.Game;
import me.stuntguy3000.java.telegames.object.lobby.Lobby;
import me.stuntguy3000.java.telegames.object.user.TelegramUser;
import me.stuntguy3000.java.telegames.util.ClassGetter;
import me.stuntguy3000.java.telegames.util.string.Emoji;
import me.stuntguy3000.java.telegames.util.string.Lang;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.PhotoMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

// @author Luke Anderson | stuntguy3000
public class TelegramHook implements Listener {
    @Getter
    private static TelegramBot bot;
    @Getter
    private final Telegames instance;
    @Getter
    private List<String> enteringlobby = new ArrayList<>();

    public TelegramHook(String authKey, Telegames instance) {
        this.instance = instance;

        bot = TelegramBot.login(authKey);
        bot.startUpdates(false);
        bot.getEventsManager().register(this);
        LogHandler.log("Connected to Telegram.");

        instance.sendToAdmins("Bot has connected, running build #" + Telegames.BUILD);

        this.initializeCommands();
        this.initializeGames();
        this.initializeLobbies();
    }

    private void initializeCommands() {
        List<Class<?>> allCommands = ClassGetter.getClassesForPackage("me.stuntguy3000.java.telegames.command.");
        allCommands.stream().filter(Command.class::isAssignableFrom).forEach(clazz -> {
            try {
                Command command = (Command) clazz.newInstance();
                LogHandler.log("Registered command " + Arrays.toString(command.getNames()));
            } catch (InstantiationException | IllegalAccessException e) {
                LogHandler.log(clazz.getSimpleName() + " failed to instantiate:");
                e.printStackTrace();
            }
        });
    }

    private void initializeGames() {
        List<Class<?>> allGames = ClassGetter.getClassesForPackage("me.stuntguy3000.java.telegames.game.");
        allGames.stream().filter(Game.class::isAssignableFrom).forEach(clazz -> {
            try {
                Game game = (Game) clazz.newInstance();
                if (game.isDevModeOnly() && !Telegames.DEV_MODE) {
                    return;
                }
                getInstance().getGameHandler().registerGame(game);
            } catch (InstantiationException | IllegalAccessException e) {
                LogHandler.log(clazz.getSimpleName() + " failed to instantiate:");
                e.printStackTrace();
            }
        });
    }

    private void initializeLobbies() {
        LobbyList lobbyList = getInstance().getConfigHandler().getLobbyList();

        if (lobbyList != null) {
            for (Map.Entry<String, List<Integer>> lobby : lobbyList.getActiveLobbies().entrySet()) {
                getInstance().getLobbyHandler().createLobby(lobby.getKey(), lobby.getValue());
            }
        }
    }

    @Override
    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        String command = event.getCommand();

        instance.getCommandHandler().executeCommand(command, event);
    }

    @Override
    public void onPhotoMessageReceived(PhotoMessageReceivedEvent event) {
        User user = event.getMessage().getSender();
        TelegramUser telegramUser = new TelegramUser(user);
        Lobby lobby = Telegames.getInstance().getLobbyHandler().getLobby(telegramUser);

        if (lobby != null) {
            lobby.onPhotoMessageReceivedEvent(event);
        }
    }

    @Override
    public void onTextMessageReceived(TextMessageReceivedEvent event) {
        User user = event.getMessage().getSender();
        TelegramUser telegramUser = new TelegramUser(user);
        String message = event.getContent().getContent();
        Lobby lobby = Telegames.getInstance().getLobbyHandler().getLobby(telegramUser);

        if (lobby != null) {
            lobby.onTextMessageReceived(event);
            LogHandler.debug("(Chat) [#%s] %s: %s", lobby.getLobbyID(), user.getUsername(), event.getContent().getContent());
        } else if (message.equalsIgnoreCase(Lang.KEYBOARD_CANCEL)) {
            enteringlobby.remove(user.getUsername());
            event.getChat().sendMessage(KeyboardHandler.createLobbyCreationMenu().message(Lang.GENERAL_RETURNING_MENU).parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
        } else if (message.equalsIgnoreCase(Lang.KEYBOARD_CREATE_LOBBY)) {
            try {
                Telegames.getInstance().getLobbyHandler().tryCreateLobby(telegramUser);
            } catch (UserIsMatchmakingException e) {
                event.getChat().sendMessage(SendableTextMessage.builder().message(Lang.ERROR_LOBBY_CREATE_MATCHMAKING).parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
            } catch (UserHasLobbyException e) {
                event.getChat().sendMessage(Lang.ERROR_USER_IN_LOBBY, TelegramHook.getBot());
            }
        } else if (message.equalsIgnoreCase(Lang.KEYBOARD_JOIN_LOBBY)) {
            event.getChat().sendMessage(KeyboardHandler.createCancelMenu().message(Lang.GENERAL_ENTER_LOBBY_NAME).parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
            if (!enteringlobby.contains(user.getUsername())) {
                enteringlobby.add(user.getUsername());
            }
        } else if (message.equalsIgnoreCase(Lang.KEYBOARD_JOIN_MATCHMAKING)) {
            MatchmakingHandler matchmakingHandler = getInstance().getMatchmakingHandler();

            if (!Telegames.DEV_MODE) {
                event.getChat().sendMessage(Emoji.RED_CROSS.getText() + " Feature coming soon...", TelegramHook.getBot());
                return;
            }

            if (!matchmakingHandler.isInQueue(telegramUser)) {
                event.getChat().sendMessage(KeyboardHandler.createMatchmakingMenu(null).message(String.format(Lang.MATCHMAKING_DESCRIPTION, matchmakingHandler.getQueueCount())).parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                matchmakingHandler.addNewUser(telegramUser);
            }
        } else if (message.equalsIgnoreCase(Lang.KEYBOARD_QUIT_MATCHMAKING)) {
            MatchmakingHandler matchmakingHandler = getInstance().getMatchmakingHandler();

            if (matchmakingHandler.isInQueue(telegramUser)) {
                matchmakingHandler.removeUser(user);
            }

            event.getChat().sendMessage(KeyboardHandler.createLobbyCreationMenu().message(Lang.GENERAL_RETURNING_MENU).parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
        } else {
            MatchmakingHandler matchmakingHandler = getInstance().getMatchmakingHandler();


            if (matchmakingHandler.isInQueue(telegramUser)) {
                if (message.contains(" ")) {
                    Game game = getInstance().getGameHandler().getGame(message.split(" ")[1]);

                    if (game != null) {
                        if (message.startsWith(Emoji.RED_CIRCLE.getText())) {
                            matchmakingHandler.addGame(telegramUser, game.getGameName());
                            event.getChat().sendMessage(KeyboardHandler.createMatchmakingMenu(matchmakingHandler.getUserOptions(telegramUser)).message(Emoji.GREEN_BOX_TICK.getText() + " *Included " + game.getGameName() + "*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                        } else if (message.startsWith(Emoji.BLUE_CIRCLE.getText())) {
                            matchmakingHandler.removeGame(telegramUser, game.getGameName());
                            event.getChat().sendMessage(KeyboardHandler.createMatchmakingMenu(matchmakingHandler.getUserOptions(telegramUser)).message(Emoji.RED_CROSS.getText() + " *Removed " + game.getGameName() + "*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                        }
                    }
                }
            } else if (enteringlobby.contains(user.getUsername())) {
                String name = event.getContent().getContent().replace(" ", "");
                Lobby targetLobby = Telegames.getInstance().getLobbyHandler().getLobby(name);

                if (targetLobby != null) {
                    try {
                        targetLobby.userJoin(telegramUser);
                        enteringlobby.remove(user.getUsername());
                    } catch (LobbyLockedException | UserBannedException | LobbyFullException e) {
                        SendableTextMessage sendableTextMessage = KeyboardHandler.createLobbyCreationMenu().message(Lang.ERROR_CANNOT_JOIN_LOBBY).parseMode(ParseMode.MARKDOWN).build();
                        event.getChat().sendMessage(sendableTextMessage, TelegramHook.getBot());
                    }
                } else {
                    event.getChat().sendMessage(SendableTextMessage.builder().message(Lang.ERROR_LOBBY_NOT_FOUND).parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                }
            }
        }
    }
}
    