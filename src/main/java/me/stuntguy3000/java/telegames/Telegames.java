package me.stuntguy3000.java.telegames;

import lombok.Getter;
import me.stuntguy3000.java.telegames.handler.*;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.Lobby;
import me.stuntguy3000.java.telegames.util.RandomString;
import org.apache.commons.io.FileUtils;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

// @author Luke Anderson | stuntguy3000
public class Telegames {
    public static int BUILD = 0;
    public static boolean DEV_MODE = false;
    @Getter
    public static Telegames instance;
    @Getter
    private CommandHandler commandHandler = new CommandHandler();
    @Getter
    private ConfigHandler configHandler;
    @Getter
    private GameHandler gameHandler = new GameHandler();
    @Getter
    private LobbyHandler lobbyHandler = new LobbyHandler();
    @Getter
    private RandomString randomString = new RandomString(5);
    private Thread updaterThread;

    private void connectTelegram() {
        LogHandler.log("Connecting to Telegram...");
        new TelegramHook(configHandler.getBotSettings().getTelegramKey(), this);
    }

    /**
     * Crazy ideas: - Matchmaking - Private/Public Matches (Joinable via IDs, or passwords) - Computer AI
     */
    public void main() {
        instance = this;
        configHandler = new ConfigHandler();

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


        LogHandler.log("======================================");
        LogHandler.log(" Telegames build " + BUILD + " by @stuntguy3000");
        LogHandler.log("======================================");

        connectTelegram();

        if (this.getConfigHandler().getBotSettings().getAutoUpdater()) {
            LogHandler.log("Starting auto updater...");
            Thread updater = new Thread(new UpdateHandler(this, "Telegames"));
            updater.start();
            updaterThread = updater;
        } else {
            LogHandler.log("** Auto Updater is set to false **");
        }

        DEV_MODE = getConfigHandler().getBotSettings().getDevMode();

        while (true) {
            String in = System.console().readLine();
            switch (in.toLowerCase()) {
                case "list": {
                    LogHandler.log("Lobby List:");

                    for (Lobby lobby : lobbyHandler.getActiveLobbies().values()) {
                        LogHandler.log(String.format("ID: %s Owner: %s", lobby.getLobbyID(), lobby.getLobbyOwner().getUsername()));
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

    public void stopUpdater() {
        updaterThread.interrupt();
    }
}
    