package me.stuntguy3000.java.telegames.object;

import java.util.Timer;
import java.util.TimerTask;

public class GameSecondTimer extends TimerTask {
    private Game instance;

    public GameSecondTimer(Game instance) {
        this.instance = instance;
        new Timer().schedule(this, 0, 1000);
    }

    @Override
    public void run() {
        instance.onSecond();
    }
}