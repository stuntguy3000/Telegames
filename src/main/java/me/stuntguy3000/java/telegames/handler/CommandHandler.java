package me.stuntguy3000.java.telegames.handler;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import me.stuntguy3000.java.telegames.object.command.Command;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

/**
 * Handles command registration and execution
 * <p>Can also generate a BotFather command String.</p>
 *
 * @author aaomidi
 * @author stuntguy3000
 */
@Data
public class CommandHandler {
    public HashMap<String[], Command> commands = new HashMap<>();

    /**
     * Execute a command
     *
     * @param command String the name of the command
     * @param event   CommandMessageReceivedEvent the event in which the command was instructed to
     *                be called
     */
    public void executeCommand(String command, CommandMessageReceivedEvent event) {
        Command cmd = null;

        for (Map.Entry<String[], Command> commandInstance : commands.entrySet()) {
            for (String name : commandInstance.getKey()) {
                if (command.equalsIgnoreCase(name)) {
                    cmd = commandInstance.getValue();
                }
            }
        }

        if (cmd != null) {
            cmd.processCommand(event);
        }
    }

    /**
     * Generate a command list for Telegram's Bot Father protocol
     * <p>Allows Telegram clients to see a list of commands</p>
     *
     * @return String the list of commands accepted by botfather
     */
    public String getBotFatherString() {
        StringBuilder sb = new StringBuilder();
        for (Command cmd : commands.values()) {
            sb.append(cmd.createBotFatherString()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Register a new command
     *
     * @param cmd Command the command to be registered
     */
    public void registerCommand(Command cmd) {
        commands.put(cmd.getNames(), cmd);
    }
}
