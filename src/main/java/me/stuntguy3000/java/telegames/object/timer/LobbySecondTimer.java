package me.stuntguy3000.java.telegames.object.timer;

import me.stuntguy3000.java.telegames.object.lobby.Lobby;

import java.util.Timer;
import java.util.TimerTask;

public class LobbySecondTimer extends TimerTask {
    private Lobby instance;

    public LobbySecondTimer(Lobby instance) {
        this.instance = instance;
        new Timer().schedule(this, 0, 1000);
    }

    @Override
    public void run() {
        instance.onSecond();
    }
}