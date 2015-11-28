package me.stuntguy3000.java.telegames;

import lombok.Getter;
import me.stuntguy3000.java.telegames.handler.CommandHandler;
import me.stuntguy3000.java.telegames.handler.GameHandler;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.util.Config;
import me.stuntguy3000.java.telegames.util.LogHandler;
import me.stuntguy3000.java.telegames.util.Updater;
import org.apache.commons.io.FileUtils;
import pro.zackpollard.telegrambot.api.TelegramBot;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

// @author Luke Anderson | stuntguy3000
public class Telegames {
    public static Integer BUILD = 0;
    @Getter
    public static Telegames instance;
    @Getter
    private Config config;
    @Getter
    private CommandHandler commandHandler = new CommandHandler();
    @Getter
    private GameHandler gameHandler = new GameHandler();

    public static void main(String[] args) {
        new Telegames().main();
    }

    // TODO: Remove dependency of channels all together

    /**
     * Crazy ideas:
     * - Matchmaking
     * - Private/Public Matches (Joinable via IDs, or passwords)
     * - Computer AI
     */
    public void main() {
        instance = this;
        config = new Config();

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

        connectTelegram();

        if (getConfig().getBotSettings().getAutoUpdater()) {
            LogHandler.log("Starting auto updater...");
            new Thread(new Updater(this)).start();
        } else {
            LogHandler.log("** Auto Updater is set to false **");
        }

        while (true) {
            String in = System.console().readLine();
            switch (in.toLowerCase()) {
                default: {
                    // TODO: Add Console Commands
                    LogHandler.log("");
                }
            }
        }
    }

    private void connectTelegram() {
        LogHandler.log("Connecting to Telegram...");
        new TelegramHook(config.getBotSettings().getTelegramKey(), this);
    }

    public void sendToAdmins(String message) {
        for (int admin : config.getBotSettings().getTelegramAdmins()) {
            TelegramBot.getChat(admin).sendMessage(message, TelegramHook.getBot());
        }
    }
}
    