package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.command.Command;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class JoinCommand extends Command {

    public JoinCommand() {
        super(Telegames.getInstance(), "/join <ID> Join a lobby.", false, "join");
    }

    public void processCommand(CommandMessageReceivedEvent event) {

    }

}