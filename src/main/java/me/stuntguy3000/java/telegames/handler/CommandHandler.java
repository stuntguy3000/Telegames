package me.stuntguy3000.java.telegames.handler;

import me.stuntguy3000.java.telegames.object.Command;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

import java.util.HashMap;

/**
 * Created by amir on 2015-11-25.
 */
public class CommandHandler {
    public HashMap<String, Command> commands;

    public CommandHandler() {
        commands = new HashMap<>();
    }

    public void executeCommand(String s, CommandMessageReceivedEvent event) {
        Command cmd = commands.get(s.toLowerCase());
        if (cmd == null) {
            return;
        }
        cmd.processCommand(event);
    }

    public String getBotFatherString() {
        StringBuilder sb = new StringBuilder();
        for (Command cmd : commands.values()) {
            sb.append(cmd.createBotFatherString()).append("\n");
        }

        return sb.toString();
    }

    public void registerCommand(Command cmd) {
        commands.put(cmd.getName().toLowerCase(), cmd);
    }
}
