package me.stuntguy3000.java.groupgamebot.command;

import me.stuntguy3000.java.groupgamebot.GroupGameBot;
import me.stuntguy3000.java.groupgamebot.hook.TelegramHook;
import me.stuntguy3000.java.groupgamebot.util.BotSettings;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class AdminCommand extends TelegramCommand {
    public AdminCommand(GroupGameBot instance) {
        super(instance, "admin", "");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        User sender = event.getMessage().getSender();
        String[] args = event.getArgs();

        BotSettings botSettings = getInstance().getConfig().getBotSettings();

        if (botSettings.getTelegramAdmins().contains(sender.getId())) {
            if (args.length == 0) {
                chat.sendMessage("Admin Commands: admins, stop, botfather", TelegramHook.getBot());
            } else {
                switch (args[0].toLowerCase()) {
                    case "botfather": {
                        chat.sendMessage(getInstance().getCommandHandler().getBotFatherString(), TelegramHook.getBot());
                        return;
                    }
                    case "stop": {
                        chat.sendMessage("Shutting bot down.", TelegramHook.getBot());
                        System.exit(0);
                        return;
                    }
                    case "admins": {
                        chat.sendMessage("Admins: " + botSettings.getTelegramAdmins(), TelegramHook.getBot());
                        return;
                    }
                    default: {
                        chat.sendMessage("Admin Commands: admins, stop, botfather", TelegramHook.getBot());
                    }
                }
            }
        } else {
            chat.sendMessage("You cannot use this command " + sender.getUsername(), TelegramHook.getBot());
        }
    }
}
    