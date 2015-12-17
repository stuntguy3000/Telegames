package me.stuntguy3000.java.telegames.object;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;

import java.util.ArrayList;
import java.util.TimerTask;

public class LobbyTimer extends TimerTask {

    @Override
    public void run() {
        Long currentTime = System.currentTimeMillis();
        LobbyHandler lobbyHandler = Telegames.getInstance().getLobbyHandler();

        for (Lobby lobby : new ArrayList<>(lobbyHandler.getActiveLobbies().values())) {
            if (currentTime - lobby.getLastLobbyAction() >= 600000) {
                lobbyHandler.expireLobby(lobby);
            }
        }
    }
}