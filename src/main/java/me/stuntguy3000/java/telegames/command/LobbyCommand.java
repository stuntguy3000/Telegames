package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.command.Command;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class LobbyCommand extends Command {

    public LobbyCommand() {
        super(Telegames.getInstance(), "/lobby View current lobby information.", false, "lobby");
    }

    public void processCommand(CommandMessageReceivedEvent event) {

    }

}