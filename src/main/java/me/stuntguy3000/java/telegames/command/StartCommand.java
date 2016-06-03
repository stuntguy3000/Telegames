package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.command.Command;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class StartCommand extends Command {

    public StartCommand() {
        super(Telegames.getInstance(), "/start Create a lobby.", false, "start");
    }

    public void processCommand(CommandMessageReceivedEvent event) {

    }

}