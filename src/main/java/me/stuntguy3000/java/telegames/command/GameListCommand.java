package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.command.Command;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

// @author Luke Anderson | stuntguy3000
public class GameListCommand extends Command {
    public GameListCommand() {
        super(Telegames.getInstance(), "/gamelist List all available games.", "gamelist");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        String[] args = event.getArgs();
        boolean restricted = false;

        if (args != null && args.length > 0 && args[0].equalsIgnoreCase("all")) {
            restricted = true;
        }

        respond(chat, SendableTextMessage.builder().message(TelegramEmoji.BOOK.getText() + " " + getInstance().getGameHandler().getGameList(restricted)).parseMode(ParseMode.MARKDOWN).build());
    }
}