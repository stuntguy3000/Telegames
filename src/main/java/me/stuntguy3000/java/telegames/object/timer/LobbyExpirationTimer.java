package me.stuntguy3000.java.telegames.object.timer;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;
import me.stuntguy3000.java.telegames.object.lobby.Lobby;

import java.util.ArrayList;
import java.util.TimerTask;

public class LobbyExpirationTimer extends TimerTask {

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