package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;
import me.stuntguy3000.java.telegames.object.command.Command;
import me.stuntguy3000.java.telegames.object.lobby.Lobby;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class LobbyCommand extends Command {

    public LobbyCommand() {
        super(Telegames.getInstance(), "/lobby View current lobby information.", false, "lobby");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        LobbyHandler lobbyHandler = Telegames.getInstance().getLobbyHandler();

        Lobby lobby = lobbyHandler.getLobby(event.getMessage().getSender().getUsername());

        if (lobby != null) {
            event.getChat().sendMessage(lobby.getLobbyInformation());
        } else {
            lobby = lobbyHandler.createLobby(event.getMessage().getSender(), event.getChat());
        }
    }

}