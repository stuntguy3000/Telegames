package me.stuntguy3000.java.telegames;

import lombok.Data;
import me.stuntguy3000.java.telegames.handler.LogHandler;
import me.stuntguy3000.java.telegames.handler.TelegramEventHandler;
import pro.zackpollard.telegrambot.api.TelegramBot;

// @author Luke Anderson | stuntguy3000
@Data
public class TelegramHook {
    private static TelegramBot bot;
    private final Telegames instance;

    /**
     * Connect to the Telegram API
     *
     * @param authKey  String the authentication key
     * @param instance Telegames the instance of the main class
     */
    public TelegramHook(String authKey, Telegames instance) {
        this.instance = instance;

        LogHandler.debug("[==========] Connecting to Telegram [==========]");

        LogHandler.debug("> Logging in using the Authentication Key");
        bot = TelegramBot.login(authKey);

        LogHandler.debug("> Starting updates, ignore previous requests");
        bot.startUpdates(false);

        LogHandler.debug("> Registering events manager");
        bot.getEventsManager().register(new TelegramEventHandler());

        instance.sendToAdmins("> Telegames has connected to Telegram. Running build " + instance.getCurrentBuild());

        LogHandler.debug("============================================");

        LogHandler.debug("[==========] Initializing Bot Content [==========]");

        this.initializeCommands();
        this.initializeGames();
        this.initializeLobbies();

        LogHandler.debug("============================================");
    }

    /**
     * Return an instance of TelegramBot
     *
     * @return TelegramBot the instance of the telegram bot
     */
    public static TelegramBot getBot() {
        return bot;
    }

    /**
     * Initialize commands
     */
    private void initializeCommands() {
        LogHandler.debug("> Initializing commands");
    }

    /**
     * Initialize games
     */
    private void initializeGames() {
        LogHandler.debug("> Initializing games");
    }

    /**
     * Initialize lobbies
     */
    private void initializeLobbies() {
        LogHandler.debug("> Initializing lobbies");
    }
}
    