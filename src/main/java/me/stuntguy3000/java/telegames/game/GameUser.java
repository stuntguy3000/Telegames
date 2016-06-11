package me.stuntguy3000.java.telegames.game;

import java.util.Objects;

import lombok.Data;
import pro.zackpollard.telegrambot.api.user.User;

/**
 * Represents a user within a Game
 */
@Data
public class GameUser {
    private final Game game;
    private final User user;

    public GameUser(Game game, User user) {
        this.game = Objects.requireNonNull(game, "game");
        this.user = Objects.requireNonNull(user, "user");
    }

    public final void join() {
        this.game.join(this);
    }

    public final void quit() {
        this.game.quit(this);
    }

}
