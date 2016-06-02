/*
 * MIT License
 *
 * Copyright (c) 2016 Luke Anderson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.stuntguy3000.java.telegames;

import lombok.Data;
import me.stuntguy3000.java.telegames.command.VersionCommand;
import me.stuntguy3000.java.telegames.handler.CommandHandler;
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

        LogHandler.debug("================================================");

        LogHandler.debug("[==========] Initializing Bot Content [==========]");

        this.initializeCommands();
        this.initializeGames();
        this.initializeLobbies();

        LogHandler.debug("==================================================");
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

        CommandHandler commandHandler = getInstance().getCommandHandler();
        commandHandler.registerCommand(new VersionCommand());
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
    