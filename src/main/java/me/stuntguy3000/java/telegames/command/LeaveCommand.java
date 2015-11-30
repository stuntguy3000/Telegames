package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.Command;
import me.stuntguy3000.java.telegames.object.Lobby;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class LeaveCommand extends Command {
    public LeaveCommand() {
        super(Telegames.getInstance(), "leave", "/leave Leave a Lobby.");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        User sender = event.getMessage().getSender();
        Lobby lobby = Telegames.getInstance().getLobbyHandler().getLobby(sender);

        if (lobby != null) {
            lobby.userLeave(lobby.getLobbyMember(sender.getUsername()));
        } else {
            respond(chat, "You are not in a Lobby!");
        }
    }
}