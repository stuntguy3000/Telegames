package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;
import me.stuntguy3000.java.telegames.object.Command;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class CreateCommand extends Command {
    public CreateCommand() {
        super(Telegames.getInstance(), "create", "/create Create a Lobby.");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        User sender = event.getMessage().getSender();
        LobbyHandler lobbyHandler = getInstance().getLobbyHandler();

        if (event.getChat().getType() == ChatType.PRIVATE) {
            if (lobbyHandler.getLobby(sender) == null) {
                lobbyHandler.createLobby(sender);
            } else {
                respond(chat, "You are already in a Lobby!");
            }
        } else {
            respond(chat, "This command can only be executed via a private message to @TelegamesBot");
        }
    }
}