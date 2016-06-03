package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.command.Command;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class PlayCommand extends Command {

    public PlayCommand() {
        super(Telegames.getInstance(), "/play <game> Play a game.", false, "play", "startgame");
    }

    public void processCommand(CommandMessageReceivedEvent event) {

    }

}