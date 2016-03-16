package me.stuntguy3000.java.telegames;

import lombok.Getter;
import me.stuntguy3000.java.telegames.handler.*;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.lobby.Lobby;
import me.stuntguy3000.java.telegames.object.timer.LobbyExpirationTimer;
import me.stuntguy3000.java.telegames.util.string.RandomString;
import me.stuntguy3000.java.telegames.util.string.StringUtil;
import org.apache.commons.io.FileUtils;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Timer;

// @author Luke Anderson | stuntguy3000
public class Telegames {
    public static int BUILD = 0;
    public static boolean DEV_MODE = false;
    @Getter
    public static Telegames instance;
    @Getter
    private CommandHandler commandHandler;
    @Getter
    private ConfigHandler configHandler;
    @Getter
    private GameHandler gameHandler;
    @Getter
    private LobbyHandler lobbyHandler;
    @Getter
    private MatchmakingHandler matchmakingHandler;
    @Getter
    private File outputFolder;
    @Getter
    private RandomString randomString = new RandomString(5);
    @Getter
    private UpdaterAnnouncerHandler updaterAnnouncerHandler = new UpdaterAnnouncerHandler();
    @Getter
    private Thread updaterThread;

    private void connectTelegram() {
        LogHandler.log("Connecting to Telegram...");
        DEV_MODE = getConfigHandler().getBotSettings().getDevMode();
        LogHandler.log("Developer Mode is set to " + DEV_MODE);
        new TelegramHook(configHandler.getBotSettings().getTelegramKey(), this);
    }

    public void main() {
        instance = this;
        configHandler = new ConfigHandler();

        /**
         * Initialize Build Number
         */
        File build = new File("build");

        if (!build.exists()) {
            try {
                build.createNewFile();
                PrintWriter writer = new PrintWriter(build, "UTF-8");
                writer.print(0);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            BUILD = Integer.parseInt(FileUtils.readFileToString(build));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * Setup Jar Output folder
         */
        outputFolder = new File("output");

        if (outputFolder.exists()) {
            for (File file : outputFolder.listFiles()) {
                file.delete();
            }
        } else {
            outputFolder.mkdirs();
        }

        /**
         * Begin CLI for Telegames
         */
        LogHandler.log("======================================");
        LogHandler.log(" Telegames build " + BUILD + " by @stuntguy3000");
        LogHandler.log("======================================");

        /**
         * Initialize handlers
         */
        commandHandler = new CommandHandler();
        gameHandler = new GameHandler();
        lobbyHandler = new LobbyHandler();
        matchmakingHandler = new MatchmakingHandler();

        /**
         * Connect to Telegram
         */
        connectTelegram();

        /**
         * Begin auto updater
         */
        if (!DEV_MODE) {
            LogHandler.log("Starting update announcer...");
            updaterAnnouncerHandler = new UpdaterAnnouncerHandler();
            updaterAnnouncerHandler.runUpdater();
        } else {
            LogHandler.log("** Update Announcer is not running **");
        }

        /**
         * Start various timers and threads
         *
         * Load the auto updater
         */
        if (this.getConfigHandler().getBotSettings().getAutoUpdater()) {
            LogHandler.log("Starting auto updater...");
            Thread updater = new Thread(new UpdateHandler(this, "Telegames-" + (DEV_MODE ? "Development" : "Master"), "Telegames"));
            updater.start();
            updaterThread = updater;
        } else {
            LogHandler.log("** Auto Updater is set to false **");
        }

        /**
         * Start Lobby timer
         */
        LobbyExpirationTimer lobbyExpirationTimer = new LobbyExpirationTimer();
        new Timer().schedule(lobbyExpirationTimer, 0, 30 * 1000);

        /**
         * Keepalive
         */
        while (true) {
            String in = System.console().readLine();
            switch (in.toLowerCase()) {
                case "list": {
                    LogHandler.log("Lobby List:");

                    for (Lobby lobby : lobbyHandler.getActiveLobbies().values()) {
                        LogHandler.log(String.format("ID: %s Owner: %s Members: %s Last Active: %s %s", lobby.getLobbyID(), lobby.getLobbyOwner().getUsername(), lobby.getTelegramUsers().size(), StringUtil.millisecondsToHumanReadable(System.currentTimeMillis() - lobby.getLastLobbyAction()), lobby.getCurrentGame() != null ? "Playing " + lobby.getCurrentGame().getGameName() : ""));
                    }
                    continue;
                }
                case "botfather": {
                    LogHandler.log(getCommandHandler().getBotFatherString());
                    continue;
                }
                case "quit":
                case "stop":
                case "exit": {
                    configHandler.saveConfig("stats.json");
                    System.exit(0);
                }
            }
        }
    }

    public static void main(String[] args) {
        new Telegames().main();
    }

    public void sendToAdmins(String message) {
        for (int admin : configHandler.getBotSettings().getTelegramAdmins()) {
            TelegramBot.getChat(admin).sendMessage(message, TelegramHook.getBot());
        }
    }

    public void sendToLobbies(String message) {
        for (Lobby lobby : getLobbyHandler().getActiveLobbies().values()) {
            lobby.sendMessage(SendableTextMessage.builder().message(message).parseMode(ParseMode.MARKDOWN).build());
        }
    }
}
    