package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.command.Command;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class LeaveCommand extends Command {

    public LeaveCommand() {
        super(Telegames.getInstance(), "/leave Leave a lobby.", false, "leave", "quit", "exit");
    }

    public void processCommand(CommandMessageReceivedEvent event) {

    }

}