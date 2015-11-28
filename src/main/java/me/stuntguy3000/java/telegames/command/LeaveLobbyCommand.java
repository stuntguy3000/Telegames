package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;
import me.stuntguy3000.java.telegames.object.Command;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class LeaveLobbyCommand extends Command {
    public LeaveLobbyCommand() {
        super(Telegames.getInstance(), "leavelobby", "/leavelobby Leave a Lobby.");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        User sender = event.getMessage().getSender();
        LobbyHandler lobbyHandler = getInstance().getLobbyHandler();

        if (lobbyHandler.getLobby(sender) != null) {
            respond(chat, "You have left the Lobby!");
            lobbyHandler.userLeaveLobby(sender);
        } else {
            respond(chat, "You are not in a Lobby!");
        }
    }
}
    