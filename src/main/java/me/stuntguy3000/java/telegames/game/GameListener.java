package me.stuntguy3000.java.telegames.game;

import me.stuntguy3000.java.telegames.game.keyboard.KeyboardButton;

public interface GameListener {

    void onStart(Game game);

    void onFinish(Game game);

    void onJoin(Game game, GameUser user);

    void onQuit(Game game, GameUser user);

    void onMessage(Game game, GameUser user, String message);

    void onClick(Game game, GameUser user, KeyboardButton button);

}
