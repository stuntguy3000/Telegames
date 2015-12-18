package me.stuntguy3000.java.telegames.util;

import lombok.Getter;

public enum TelegramEmoji {
    NUMBER_BLOCK_ZERO("0⃣"),
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
    PERSON("\uD83D\uDC64"),
    PERSON_SPEAKING("\uD83D\uDDE3"),
    CHECKBOX("☑️"),
    CIRCLE_CHECKBOX("\uD83D\uDD18"),
    WHITE_CIRCLE("⚪️"),
    BLACK_CIRCLE("⚫️"),
    RED_CIRCLE("\uD83D\uDD34"),
    BLUE_CIRCLE("\uD83D\uDD35"),
    WHITE_SQUARE("◻️"),
    BLACK_SQUARE("◼️"),
    GREEN_BOX_TICK("✅"),
    GREEN_BOX_CROSS("❎"),
    MONKEY_HIDING("\uD83D\uDE48"),
    JOYSTICK("\uD83D\uDD79"),
    HAPPY_FACE("\uD83D\uDE00"),
    SAD_FACE("\uD83D\uDE41"),
    BLUE_RIGHT_ARROW("▶️"),
    HEART("❤️"),
    BOOK("\uD83D\uDCD4"),
    GHOST("\uD83D\uDC7B"),
    END("\uD83D\uDD1A");

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
