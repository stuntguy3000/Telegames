package me.stuntguy3000.java.telegames.util.cards;

import lombok.Getter;
import lombok.Setter;

public class Card {
    @Getter
    @Setter
    private Suit suit;
    @Getter
    @Setter
    private Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    @Override
    public String toString() {
        return this.getRank() + " " + this.suit.getSymbol();
    }

    public int getValue() {
        switch (rank) {
            case ACE: {
                return 1;
            }
            case TWO: {
                return 2;
            }
            case THREE: {
                return 3;
            }
            case FOUR: {
                return 4;
            }
            case FIVE: {
                return 5;
            }
            case SIX: {
                return 6;
            }
            case SEVEN: {
                return 7;
            }
            case EIGHT: {
                return 8;
            }
            case NINE: {
                return 9;
            }
            case TEN: {
                return 10;
            }
            case JACK:
            case KING:
            case QUEEN: {
                return 10;
            }
        }

        return 0;
    }
}