package me.stuntguy3000.java.telegames.hook;

import lombok.Getter;
import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.LogHandler;
import me.stuntguy3000.java.telegames.handler.MatchmakingHandler;
import me.stuntguy3000.java.telegames.object.Command;
import me.stuntguy3000.java.telegames.object.Game;
import me.stuntguy3000.java.telegames.object.Lobby;
import me.stuntguy3000.java.telegames.object.config.LobbyList;
import me.stuntguy3000.java.telegames.object.exception.UserHasLobbyException;
import me.stuntguy3000.java.telegames.object.exception.UserIsMatchmakingException;
import me.stuntguy3000.java.telegames.util.ClassGetter;
import me.stuntguy3000.java.telegames.util.KeyboardUtil;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.PhotoMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardHide;
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
        Lobby lobby = Telegames.getInstance().getLobbyHandler().getLobby(user);

        if (lobby != null) {
            lobby.onPhotoMessageReceivedEvent(event);
        }
    }

    @Override
    public void onTextMessageReceived(TextMessageReceivedEvent event) {
        User user = event.getMessage().getSender();
        String message = event.getContent().getContent();
        Lobby lobby = Telegames.getInstance().getLobbyHandler().getLobby(user);

        if (lobby != null) {
            lobby.onTextMessageReceived(event);
            LogHandler.log("[Chat] [%s] %s: %s", lobby.getLobbyID(), user.getUsername(), event.getContent().getContent());
        } else if (message.equalsIgnoreCase(TelegramEmoji.RED_CROSS.getText() + " Cancel")) {
            event.getChat().sendMessage(KeyboardUtil.createLobbyCreationMenu().message(TelegramEmoji.PENCIL.getText() + " *Returning to lobby menu:*").parseMode(ParseMode.MARKDOWN).replyMarkup(new ReplyKeyboardHide()).build(), TelegramHook.getBot());
        } else if (message.equalsIgnoreCase(TelegramEmoji.JOYSTICK.getText() + " Create a lobby")) {
            try {
                Telegames.getInstance().getLobbyHandler().tryCreateLobby(user);
            } catch (UserIsMatchmakingException e) {
                event.getChat().sendMessage(TelegramEmoji.RED_CROSS.getText() + " You cannot create a lobby while in matchmaking!", TelegramHook.getBot());
                return;
            } catch (UserHasLobbyException e) {
                event.getChat().sendMessage(TelegramEmoji.RED_CROSS.getText() + " You are already have a lobby!", TelegramHook.getBot());
                return;
            }
        } else if (message.equalsIgnoreCase(TelegramEmoji.PERSON.getText() + " Join a lobby")) {
            event.getChat().sendMessage(KeyboardUtil.createCancelMenu().message(TelegramEmoji.PENCIL.getText() + " *Enter the name or ID of the lobby:*").parseMode(ParseMode.MARKDOWN).replyMarkup(new ReplyKeyboardHide()).build(), TelegramHook.getBot());
            if (!enteringlobby.contains(user.getUsername())) {
                enteringlobby.add(user.getUsername());
            }
        } else if (message.equalsIgnoreCase(TelegramEmoji.BLUE_RIGHT_ARROW.getText() + " Enter matchmaking")) {
            MatchmakingHandler matchmakingHandler = getInstance().getMatchmakingHandler();

            if (!matchmakingHandler.isInQueue(user)) {
                event.getChat().sendMessage(KeyboardUtil.createMatchmakingMenu().message(TelegramEmoji.GREEN_BOX_TICK.getText() + " *Welcome to Telegames Matchmaking!*\n\n" +
                        "Matchmaking is a simple feature allowing players to quickly play a game with random people" +
                        "around the world, with no lobbies required.\n\n" +
                        "To begin matchmaking, simply click on a game's name in the menu below to toggle if" +
                        "you want to include that game in the matchmaking search. All games are disabled by default.\n\n" +
                        "*Players in matchmaking queue: " + matchmakingHandler.getQueueCount() + "*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
            }
        } else {
            if (enteringlobby.contains(user.getUsername())) {
                String name = event.getContent().getContent().replace(" ", "");
                Lobby targetLobby = Telegames.getInstance().getLobbyHandler().getLobby(name);

                if (targetLobby != null) {
                    if (targetLobby.userJoin(user)) {
                        enteringlobby.remove(user.getUsername());
                    }
                } else {
                    event.getChat().sendMessage(SendableTextMessage.builder().message(TelegramEmoji.RED_CROSS.getText() + " *Unknown lobby!*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                }
            }
        }
    }
}
    