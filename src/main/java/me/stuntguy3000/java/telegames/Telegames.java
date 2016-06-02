package me.stuntguy3000.java.telegames;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import lombok.Data;
import me.stuntguy3000.java.telegames.handler.ConfigHandler;
import me.stuntguy3000.java.telegames.handler.LogHandler;
import me.stuntguy3000.java.telegames.handler.UpdateHandler;
import me.stuntguy3000.java.telegames.handler.UpdaterAnnouncerHandler;

/**
 * Main class for Telegames
 * Initializes all handlers and connects to the Telegram API
 *
 * @author stuntguy3000
 */
@Data
public class Telegames {
    /*
        Instance
     */
    private static Telegames instance;
    /*
        Runtime Build Options, set by configuration
     */
    private int currentBuild = 0;
    private boolean developmentMode = false;
    /*
        Handlers
     */
    private LogHandler logHandler;
    private ConfigHandler configHandler;
    private UpdaterAnnouncerHandler updaterAnnouncerHandler;

    /*
        Configuration
     */
    private File outputFolder;

    /*
        Misc
     */
    private Thread updaterThread;

    /**
     * Returns the instance of the main class
     *
     * @return Telegames the instance of the main class
     */
    public static Telegames getInstance() {
        return instance;
    }

    /**
     * Initialize this class
     *
     * @param args String[] Java application launch arguments
     */
    public static void main(String[] args) {
        new Telegames().startTelegames();
    }

    /**
     * Begin the startup process for Telegames
     */
    public void startTelegames() {
        instance = this;

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
            currentBuild = Integer.parseInt(FileUtils.readFileToString(build));
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
         * Start various timers and threads
         *
         * Load the auto updater
         */
        registerHandlers();
        developmentMode = getConfigHandler().getBotSettings().getDevMode();
        
        connectTelegram();

        if (this.getConfigHandler().getBotSettings().getAutoUpdater()) {
            LogHandler.log("Starting auto updater...");
            Thread updater = new Thread(new UpdateHandler(this, "Telegames-" + (developmentMode ? "Development" : "Master"), "Telegames"));
            updater.start();
            updaterThread = updater;
        } else {
            LogHandler.log("** Auto Updater is set to false **");
        }

        while (true) {
            // Hello!
        }
    }

    /**
     * Register all handlers
     */
    private void registerHandlers() {
        configHandler = new ConfigHandler();
        logHandler = new LogHandler();
        updaterAnnouncerHandler = new UpdaterAnnouncerHandler();
    }

    /**
     * Connect to the Telegram servers through @zackpollard's API
     *
     * @source https://github.com/zackpollard/JavaTelegramBot-API
     */
    private void connectTelegram() {
        new TelegramHook(configHandler.getBotSettings().getTelegramKey(), this);
    }

    /**
     * Sends a message to all bot admins
     *
     * @param message String the message to be sent
     */
    public void sendToAdmins(String message) {
        for (int adminID : configHandler.getBotSettings().getTelegramAdmins()) {
            TelegramHook.getBot().getChat(adminID).sendMessage(message);
        }
    }
}
