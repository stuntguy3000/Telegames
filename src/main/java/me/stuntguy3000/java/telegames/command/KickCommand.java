package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.command.Command;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class KickCommand extends Command {

    public KickCommand() {
        super(Telegames.getInstance(), "/kick <name> Remove a player from the lobby.", false, "kick");
    }

    public void processCommand(CommandMessageReceivedEvent event) {

    }

}