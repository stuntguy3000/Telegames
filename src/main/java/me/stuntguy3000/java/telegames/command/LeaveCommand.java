package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.command.Command;
import me.stuntguy3000.java.telegames.object.lobby.Lobby;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class LeaveCommand extends Command {
    public LeaveCommand() {
        super(Telegames.getInstance(), "/leave Leave a lobby.", "leave", "quit", "exit");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        User sender = event.getMessage().getSender();
        Lobby lobby = Telegames.getInstance().getLobbyHandler().getLobby(sender);

        if (lobby != null) {
            lobby.userLeave(lobby.getLobbyMember(sender.getUsername()), false);
        } else {
            respond(chat, TelegramEmoji.RED_CROSS.getText() + " You are not in a lobby!");
        }
    }
}