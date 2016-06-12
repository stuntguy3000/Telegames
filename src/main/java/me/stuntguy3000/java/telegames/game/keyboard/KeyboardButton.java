package me.stuntguy3000.java.telegames.game.keyboard;

import java.util.function.BiConsumer;

import me.stuntguy3000.java.telegames.game.Game;
import pro.zackpollard.telegrambot.api.user.User;

public class KeyboardButton {

    private String text;
    private BiConsumer<Game, User> onClick;

    public KeyboardButton(String text) {
        this.setText(text);
        this.onClick = (g, u) -> {};
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = String.valueOf(text);
    }

    public void onClick(BiConsumer<Game, User> listener) {
        this.onClick = this.onClick.andThen(listener);
    }

    public void onClick(Game game, User user) {
        this.onClick.accept(game, user);
    }

}
