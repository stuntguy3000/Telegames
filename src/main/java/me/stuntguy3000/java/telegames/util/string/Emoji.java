package me.stuntguy3000.java.telegames.util.string;

import lombok.Getter;

/**
 * Represents (some) Emojis used by Telegram
 * <p>Needs to be updated</p>
 *
 * @author stuntguy3000
 */
public enum Emoji {
    BACK("\uD83D\uDD19"),
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
    END("\uD83D\uDD1A"),
    STAR("⭐️"),
    PARTY_POPPER("\uD83C\uDF89"),
    OPEN_BOOK("\uD83D\uDCD6"),
    SPACE_INVADER("\uD83D\uDC7E"),
    REPLAY("\uD83D\uDD04"),
    METAL_GEAR("⚙"),
    PADLOCK_UNLOCKED("\uD83D\uDD13"),
    PADLOCK_LOCKED("\uD83D\uDD12"),
    PENCIL("✏️"),
    ALERT("❗️"),
    FINGER("\uD83D\uDC49\uD83C\uDFFB"),
    SAND_CLOCK("⌛️"),
    QUESTION_MARK("❓"),
    JOKER_CARD("\uD83C\uDCCF"),
    HOUSE("\uD83C\uDFDA");

    @Getter
    String text;

    Emoji(String text) {
        this.text = text;
    }

    /**
     * Returns an Emoji based on its ASCII text
     *
     * @param message String the ASCII text to be matched
     *
     * @return Emoji the matched Emoji, null if non existant or invalid
     */
    public static Emoji getMatch(String message) {
        for (Emoji emoji : Emoji.values()) {
            if (emoji.getText().equals(message)) {
                return emoji;
            }
        }

        return null;
    }

    /**
     * Returns the number which represents an Emoji block
     * <p>Any non NUMBER_BLOCK emotes will cause an IllegalArgumentException to be thrown</p>
     *
     * @param emoji Emoji the Emoji to be converted to a Integer
     *
     * @return int the number represented by the emoji
     */
    public static int getNumber(Emoji emoji) {
        switch (emoji) {
            default: {
                throw new IllegalArgumentException();
            }
            case NUMBER_BLOCK_ZERO:
                return 0;
            case NUMBER_BLOCK_ONE:
                return 1;
            case NUMBER_BLOCK_TWO:
                return 2;
            case NUMBER_BLOCK_THREE:
                return 3;
            case NUMBER_BLOCK_FOUR:
                return 4;
            case NUMBER_BLOCK_FIVE:
                return 5;
            case NUMBER_BLOCK_SIX:
                return 6;
            case NUMBER_BLOCK_SEVEN:
                return 7;
            case NUMBER_BLOCK_EIGHT:
                return 8;
            case NUMBER_BLOCK_NINE:
                return 9;
        }
    }

    /**
     * Returns an Emoji number block representing the number
     *
     * @param number Integer the number being represented
     *
     * @return Emoji the emoji representing the number
     */
    public static Emoji getNumberBlock(int number) {
        switch (number) {
            default: {
                return null;
            }
            case 0:
                return NUMBER_BLOCK_ZERO;
            case 1:
                return NUMBER_BLOCK_ONE;
            case 2:
                return NUMBER_BLOCK_TWO;
            case 3:
                return NUMBER_BLOCK_THREE;
            case 4:
                return NUMBER_BLOCK_FOUR;
            case 5:
                return NUMBER_BLOCK_FIVE;
            case 6:
                return NUMBER_BLOCK_SIX;
            case 7:
                return NUMBER_BLOCK_SEVEN;
            case 8:
                return NUMBER_BLOCK_EIGHT;
            case 9:
                return NUMBER_BLOCK_NINE;
        }
    }
}
