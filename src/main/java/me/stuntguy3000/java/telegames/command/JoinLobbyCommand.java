package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;
import me.stuntguy3000.java.telegames.object.Command;
import me.stuntguy3000.java.telegames.object.Lobby;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class JoinLobbyCommand extends Command {
    public JoinLobbyCommand() {
        super(Telegames.getInstance(), "joinlobby", "/joinlobby <ID> Join a Lobby.");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        User sender = event.getMessage().getSender();
        LobbyHandler lobbyHandler = getInstance().getLobbyHandler();
        String[] args = event.getArgs();

        if (event.getChat().getType() == ChatType.PRIVATE) {
            if (lobbyHandler.getLobby(sender) == null) {
                if (args.length > 0) {
                    String id = args[0];
                    Lobby targetLobby = lobbyHandler.getLobby(id);

                    if (targetLobby == null) {
                        respond(chat, "No such lobby exists!");
                    } else {
                        getInstance().getLobbyHandler().userJoinLobby(targetLobby.getLobbyID(), sender);
                    }
                } else {
                    respond(chat, "Please specify a Lobby ID!\nUsage: /joinlobby <ID>");
                }
            } else {
                respond(chat, "You are already in a Lobby!");
            }
        } else {
            respond(chat, "This command can only be executed via a private message to @TelegamesBot");
        }
    }
}
    