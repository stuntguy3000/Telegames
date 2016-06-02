package me.stuntguy3000.java.telegames.object.command;

import java.util.Arrays;

import lombok.Data;
import me.stuntguy3000.java.telegames.Telegames;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

/**
 * Represents a command
 */
@Data
public abstract class Command {
    private final String[] names;
    private final String description;
    private final boolean adminOnly;
    private final Telegames instance;

    /**
     * Initiates a new Command
     *
     * @param instance    Telegames the instance of Telegames
     * @param description String the command description
     * @param adminOnly   Boolean true if restricted to bot admins
     * @param names       String[] aliases to trigger the command
     */
    public Command(Telegames instance, String description, boolean adminOnly, String... names) {
        this.instance = instance;
        this.adminOnly = adminOnly;
        this.names = names;
        this.description = description;

        instance.getCommandHandler().registerCommand(this);
    }

    /**
     * Create a Bot Father String
     *
     * @return String a command description used for Telegram's Bot Father service
     */
    public String createBotFatherString() {
        return String.format("%s - %s", Arrays.toString(names), description);
    }

    /**
     * Abstract method to process the command
     *
     * @param event CommandMessageReceivedEvent the event which caused the trigger
     */
    public abstract void processCommand(CommandMessageReceivedEvent event);
}
