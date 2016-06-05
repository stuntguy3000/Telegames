package me.stuntguy3000.java.telegames.game;

import java.util.Objects;

import lombok.Getter;
import pro.zackpollard.telegrambot.api.user.User;

public class GameUser {

    @Getter
    private final Game game;

    @Getter
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
