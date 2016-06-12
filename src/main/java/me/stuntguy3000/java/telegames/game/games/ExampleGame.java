package me.stuntguy3000.java.telegames.game.games;

import me.stuntguy3000.java.telegames.game.Game;
import pro.zackpollard.telegrambot.api.user.User;

/**
 * @author stuntguy3000
 */
public class ExampleGame extends Game {
    public ExampleGame(String inlineMessageID) {
        super("Example Game", "This is an example description of the game.", inlineMessageID);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void join(User user) {

    }

    @Override
    public void quit(User user) {

    }

    @Override
    public void onSecond() {

    }
}
