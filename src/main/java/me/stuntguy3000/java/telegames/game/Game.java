package me.stuntguy3000.java.telegames.game;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;
import me.stuntguy3000.java.telegames.util.string.ConsecutiveId;

/**
 * Abstract class represents a Game
 */
@Data
public abstract class Game {
    private String id;
    private String name;
    private String inlineMessageID;
    private String description;
    private List<GameUser> users;

    public Game(String gameName, String gameDescription, String inlineMessageID) {
        this.name = gameName;
        this.description = gameDescription;
        this.id = ConsecutiveId.next(getName());
        this.inlineMessageID = inlineMessageID;
        this.users = new LinkedList<>();
    }

    public final String getNextCallbackID() {
        return ConsecutiveId.next("game-" + this.id);
    }

    public boolean isInline() {
        return this.inlineMessageID != null;
    }

    public abstract void start();

    public abstract void stop();

    public abstract void join(GameUser user);

    public abstract void quit(GameUser user);

    public abstract void onSecond();
}
