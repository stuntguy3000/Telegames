package me.stuntguy3000.java.telegames.util;

import lombok.Getter;

public enum TelegramEmoji {

    NUMBER_BLOCK_ZERO("1⃣"),
    NUMBER_BLOCK_ONE("1⃣"),
    NUMBER_BLOCK_TWO("2⃣"),
    NUMBER_BLOCK_THREE("3⃣"),
    NUMBER_BLOCK_FOUR("4⃣"),
    NUMBER_BLOCK_FIVE("5⃣"),
    NUMBER_BLOCK_SIX("6⃣"),
    NUMBER_BLOCK_SEVEN("7⃣"),
    NUMBER_BLOCK_EIGHT("8⃣"),
    NUMBER_BLOCK_NINE("9⃣"),
    NUMBER_BLOCK_TEN("\uD83D\uDD1F"),
    RED_CROSS("❌"),
    RED_CIRCLE("⭕️");

    @Getter
    String text;

    TelegramEmoji(String text) {
        this.text = text;
    }

    public static TelegramEmoji getMatch(String message) {
        for (TelegramEmoji emoji : TelegramEmoji.values()) {
            if (emoji.getText().equals(message)) {
                return emoji;
            }
        }

        return null;
    }
}
