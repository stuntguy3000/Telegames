package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.command.Command;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class HelpCommand extends Command {

    public HelpCommand() {
        super(Telegames.getInstance(), "/help View all commands.", false, "help");
    }

    public void processCommand(CommandMessageReceivedEvent event) {

    }

}