package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;
import me.stuntguy3000.java.telegames.object.Command;
import me.stuntguy3000.java.telegames.object.Lobby;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class CreateCommand extends Command {
    public CreateCommand() {
        super(Telegames.getInstance(), "/create Create a lobby.", "create", "start");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        User sender = event.getMessage().getSender();
        LobbyHandler lobbyHandler = getInstance().getLobbyHandler();
        String[] args = event.getArgs();

        if (lobbyHandler.getLobby(sender) == null) {
            if (args.length > 0) {
                String id = args[0];
                Lobby targetLobby = lobbyHandler.getLobby(id);

                if (targetLobby == null) {
                    respond(chat, TelegramEmoji.RED_CROSS.getText() + " No such lobby exists!");
                } else {
                    targetLobby.userJoin(sender);
                }
            } else {
                lobbyHandler.createLobby(sender);
            }
        } else {
            respond(chat, TelegramEmoji.RED_CROSS.getText() + " You are already in a lobby!");
        }
    }
}