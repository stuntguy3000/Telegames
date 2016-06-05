package me.stuntguy3000.java.telegames.game.scripted;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import me.stuntguy3000.java.telegames.game.Game;
import me.stuntguy3000.java.telegames.game.GameListener;
import me.stuntguy3000.java.telegames.game.GameUser;
import me.stuntguy3000.java.telegames.game.keyboard.KeyboardButton;
import me.stuntguy3000.java.telegames.util.TriConsumer;

public class ScriptedGameListener implements GameListener {

    private Consumer<Game> onStart, onFinish;
    private BiConsumer<Game, GameUser> onJoin, onQuit;
    private TriConsumer<Game, GameUser, String> onMessage;
    private TriConsumer<Game, GameUser, KeyboardButton> onClick;

    public ScriptedGameListener() {
        this.onStart = g -> {};
        this.onFinish = g -> {};
        this.onJoin = (g, u) -> {};
        this.onQuit = (g, u) -> {};
        this.onMessage = (g, u, s) -> {};
        this.onClick = (g, u, b) -> {};
    }

    public void onStart(Consumer<Game> listener) {
        this.onStart = this.onStart.andThen(Objects.requireNonNull(listener, "listener"));
    }

    public void onFinish(Consumer<Game> listener) {
        this.onFinish = this.onFinish.andThen(Objects.requireNonNull(listener, "listener"));
    }

    public void onJoin(BiConsumer<Game, GameUser> listener) {
        this.onJoin = this.onJoin.andThen(Objects.requireNonNull(listener, "listener"));
    }

    public void onQuit(BiConsumer<Game, GameUser> listener) {
        this.onQuit = this.onQuit.andThen(Objects.requireNonNull(listener, "listener"));
    }

    public void onMessage(TriConsumer<Game, GameUser, String> listener) {
        this.onMessage = this.onMessage.andThen(Objects.requireNonNull(listener, "listener"));
    }

    public void onClick(TriConsumer<Game, GameUser, KeyboardButton> listener) {
        this.onClick = this.onClick.andThen(Objects.requireNonNull(listener, "listener"));
    }

    @Override
    public void onStart(Game game) {
        try {
            this.onStart.accept(game);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onFinish(Game game) {
        try {
            this.onFinish.accept(game);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onJoin(Game game, GameUser user) {
        try {
            this.onJoin.accept(game, user);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onQuit(Game game, GameUser user) {
        try {
            this.onQuit.accept(game, user);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onMessage(Game game, GameUser user, String message) {
        try {
            this.onMessage.accept(game, user, message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(Game game, GameUser user, KeyboardButton button) {
        try {
            this.onClick.accept(game, user, button);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
