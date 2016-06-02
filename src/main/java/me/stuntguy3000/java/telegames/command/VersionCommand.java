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

package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.command.Command;
import me.stuntguy3000.java.telegames.util.string.Emoji;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

// @author Luke Anderson | stuntguy3000
public class VersionCommand extends Command {
    public VersionCommand() {
        super(Telegames.getInstance(), "/version View the bot's current version", false, "version", "about", "info");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();

        chat.sendMessage(SendableTextMessage.builder().message(
                Emoji.GHOST.getText() + " *Telegames" + (Telegames.getInstance().isDevelopmentMode() ? " Dev Mode " : " ") + "by* @stuntguy3000\n" +
                        "*Current version:* " + Telegames.getInstance().getCurrentBuild() + "\n\n" +
                        "Source [Available on GitHub](https://github.com/stuntguy3000/telegames)\n" +
                        "Created using @zackpollard's [JavaTelegramBotAPI](https://github.com/zackpollard/JavaTelegramBot-API)\n\n" +
                        "*Stay up-to-date with new features!*\nJoin https://telegram.me/telegames").parseMode(ParseMode.MARKDOWN).disableWebPagePreview(true).build());
    }
}