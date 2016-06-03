package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.command.Command;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class StatsCommand extends Command {

    public StatsCommand() {
        super(Telegames.getInstance(), "/stats View the bot's statistics", false, "stats", "statistics");
    }

    public void processCommand(CommandMessageReceivedEvent event) {

    }

}