package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.command.Command;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class StopCommand extends Command {

    public StopCommand() {
        super(Telegames.getInstance(), "/stop <game> Start a game.", false, "stop");
    }

    public void processCommand(CommandMessageReceivedEvent event) {

    }

}
