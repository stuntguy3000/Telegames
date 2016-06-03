package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.command.Command;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class GameListCommand extends Command {

    public GameListCommand() {
        super(Telegames.getInstance(), "/gamelist List all available games.", false, "gamelist");
    }

    public void processCommand(CommandMessageReceivedEvent event) {

    }

}