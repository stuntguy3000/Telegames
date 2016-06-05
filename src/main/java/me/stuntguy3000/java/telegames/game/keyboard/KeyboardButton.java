package me.stuntguy3000.java.telegames.game.keyboard;

import java.util.function.BiConsumer;

import me.stuntguy3000.java.telegames.game.Game;
import me.stuntguy3000.java.telegames.game.GameUser;

public class KeyboardButton {

    private String text;
    private BiConsumer<Game, GameUser> onClick;

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

    public void onClick(BiConsumer<Game, GameUser> listener) {
        this.onClick = this.onClick.andThen(listener);
    }

    public void onClick(Game game, GameUser user) {
        this.onClick.accept(game, user);
    }

}
