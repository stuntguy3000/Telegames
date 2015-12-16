package me.stuntguy3000.java.telegames.handler;

import me.stuntguy3000.java.telegames.object.Command;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amir on 2015-11-25.
 * Modified by stuntguy3000 :D
 */
public class CommandHandler {
    public HashMap<String[], Command> commands = new HashMap<>();

    public void executeCommand(String s, CommandMessageReceivedEvent event) {
        Command cmd = null;

        for (Map.Entry<String[], Command> command : commands.entrySet()) {
            for (String name : command.getKey()) {
                if (s.equalsIgnoreCase(name)) {
                    cmd = command.getValue();
                }
            }
        }

        if (cmd != null) {
            cmd.processCommand(event);
        }
    }

    public String getBotFatherString() {
        StringBuilder sb = new StringBuilder();
        for (Command cmd : commands.values()) {
            sb.append(cmd.createBotFatherString()).append("\n");
        }

        return sb.toString();
    }

    public void registerCommand(Command cmd) {
        commands.put(cmd.getNames(), cmd);
    }
}
