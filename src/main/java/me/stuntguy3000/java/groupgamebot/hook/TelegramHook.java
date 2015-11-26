package me.stuntguy3000.java.groupgamebot.hook;

import lombok.Getter;
import me.stuntguy3000.java.groupgamebot.GroupGameBot;
import me.stuntguy3000.java.groupgamebot.command.*;
import me.stuntguy3000.java.groupgamebot.handler.TelegramGame;
import me.stuntguy3000.java.groupgamebot.util.ClassGetter;
import me.stuntguy3000.java.groupgamebot.util.LogHandler;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.ParticipantJoinGroupChatEvent;
import pro.zackpollard.telegrambot.api.event.chat.ParticipantLeaveGroupChatEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;

import java.util.List;

// @author Luke Anderson | stuntguy3000
public class TelegramHook implements Listener {
    @Getter
    private static TelegramBot bot;
    @Getter
    private final GroupGameBot instance;

    public TelegramHook(String authKey, GroupGameBot instance) {
        this.instance = instance;

        bot = TelegramBot.login(authKey);
        bot.startUpdates(false);
        bot.getEventsManager().register(this);
        LogHandler.log("Connected to Telegram.");

        instance.sendToAdmins("Bot has connected, running build #" + GroupGameBot.BUILD);

        this.initializeCommands();
        this.initializeGames();
    }

    private void initializeGames() {
        List<Class<?>> allGames = ClassGetter.getClassesForPackage("me.stuntguy3000.java.groupgamebot.game.");
        for (Class<?> clazz : allGames) {
            if (TelegramGame.class.isAssignableFrom(clazz)) {
                try {
                    getInstance().getGameHandler().registerGame((TelegramGame) clazz.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    LogHandler.log(clazz.getSimpleName() + " failed to instantiate:");
                    e.printStackTrace();
                }
            }
        }
    }

    private void initializeCommands() {
        new VersionCommand(instance);
        new GameHelpCommand(instance);
        new GameListCommand(instance);
        new AdminCommand(instance);
        new JoinGameCommand(instance);
        new LeaveGameCommand(instance);
        new StartGameCommand(instance);
        new StopGameCommand(instance);
        new StatusCommand(instance);
    }

    @Override
    public void onTextMessageReceived(TextMessageReceivedEvent event) {
        TelegramGame telegramGame = getInstance().getGameHandler().getGame(event.getMessage().getSender());

        if (telegramGame != null) {
            telegramGame.onTextMessageReceived(event);
        }
    }

    @Override
    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        String command = event.getCommand();

        instance.getCommandHandler().executeCommand(command, event);
    }

    @Override
    public void onParticipantJoinGroupChat(ParticipantJoinGroupChatEvent event) {
        if (event.getParticipant().getUsername().equals("GroupGameBot")) {
            event.getChat().sendMessage("Thank you for using GroupGameBot by @stuntguy3000.\n\n" +
                    "Please be advised, This bot will read all messages sent to the group. " +
                    "This is required as games use custom command prefixes not recognised by Telegram. No messages are recorded.",
                    getBot());
        }
    }

    @Override
    public void onParticipantLeaveGroupChat(ParticipantLeaveGroupChatEvent event) {
        TelegramGame telegramGame = getInstance().getGameHandler().getGame(event.getChat());

        if (telegramGame != null) {
            getInstance().getGameHandler().leaveGame(event.getChat(), event.getParticipant());
        }
    }
}
    