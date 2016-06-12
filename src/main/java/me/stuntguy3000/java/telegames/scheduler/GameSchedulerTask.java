package me.stuntguy3000.java.telegames.scheduler;

import java.util.TimerTask;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.game.Game;

/**
 * @author stuntguy3000
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GameSchedulerTask extends TimerTask {
    private Game game;

    public GameSchedulerTask(Game game) {
        this.game = game;

        Telegames.getInstance().getTimer().schedule(this, 1000, 1000);
    }

    @Override
    public void run() {
        game.onSecond();
    }
}
