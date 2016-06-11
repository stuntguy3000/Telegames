package me.stuntguy3000.java.telegames.game;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.Data;
import me.stuntguy3000.java.telegames.util.string.ConsecutiveId;

/**
 * Abstract class represents a Game
 */
@Data
public abstract class Game {

    public static final String GAME_ID_NAMESPACE = "GAME_ID";

    private final String id, inline;
    private final List<GameUser> users;
    private final Set<GameListener> listeners;

    public Game(String inline) {
        this.id = ConsecutiveId.next(Game.GAME_ID_NAMESPACE);
        this.inline = inline;
        this.users = new LinkedList<>();
        this.listeners = new HashSet<>();
    }

    public final String getNextCallbackID() {
        return ConsecutiveId.next("game-" + this.id);
    }

    public final List<GameUser> getUsers() {
        return Collections.unmodifiableList(this.users);
    }

    public final void addListener(GameListener listener) {
        this.listeners.add(listener);
    }

    public boolean isInline() {
        return this.inline != null;
    }

    public String getInlineID() {
        return this.inline;
    }

    public abstract void start();

    public abstract void stop();

    public abstract void join(GameUser user);

    public abstract void quit(GameUser user);

}
