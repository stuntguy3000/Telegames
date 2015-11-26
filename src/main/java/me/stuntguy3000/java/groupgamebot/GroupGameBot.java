package me.stuntguy3000.java.groupgamebot;

import lombok.Getter;
import me.stuntguy3000.java.groupgamebot.handler.CommandHandler;
import me.stuntguy3000.java.groupgamebot.handler.GameHandler;
import me.stuntguy3000.java.groupgamebot.hook.TelegramHook;
import me.stuntguy3000.java.groupgamebot.util.Config;
import me.stuntguy3000.java.groupgamebot.util.LogHandler;
import me.stuntguy3000.java.groupgamebot.util.Updater;
import org.apache.commons.io.FileUtils;
import pro.zackpollard.telegrambot.api.TelegramBot;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

// @author Luke Anderson | stuntguy3000
public class GroupGameBot {
    public static Integer BUILD = 0;
    @Getter
    public static GroupGameBot instance;
    @Getter
    private Config config;
    @Getter
    private CommandHandler commandHandler = new CommandHandler();
    @Getter
    private GameHandler gameHandler = new GameHandler();

    public static void main(String[] args) {
        new GroupGameBot().main();
    }

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
    