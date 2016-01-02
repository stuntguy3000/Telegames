package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.command.Command;
import me.stuntguy3000.java.telegames.util.string.Emoji;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

// @author Luke Anderson | stuntguy3000
public class HelpCommand extends Command {
    public HelpCommand() {
        super(Telegames.getInstance(), "/help View all commands.", "help");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();

        respond(chat, Emoji.BOOK.getText() + " Help Menu:\n" + Telegames.getInstance().getCommandHandler().getBotFatherString());
    }
}