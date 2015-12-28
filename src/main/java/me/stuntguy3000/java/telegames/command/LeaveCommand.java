package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.command.Command;
import me.stuntguy3000.java.telegames.object.lobby.Lobby;
import me.stuntguy3000.java.telegames.object.user.TelegramUser;
import me.stuntguy3000.java.telegames.util.string.Lang;
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
        TelegramUser user = new TelegramUser(sender);
        Lobby lobby = Telegames.getInstance().getLobbyHandler().getLobby(user);

        if (lobby != null) {
            lobby.userLeave(lobby.getTelegramUser(sender.getUsername()), false);
        } else {
            respond(chat, Lang.ERROR_USER_NOT_IN_LOBBY);
        }
    }
}