package me.stuntguy3000.java.telegames.util.cards;

import lombok.Getter;

public enum Suit {
    SPADES("♠️"),
    HEARTS("♥️"),
    DIAMONDS("♦️"),
    CLUBS("♣️");

    @Getter
    String symbol;

    Suit(String symbol) {
        this.symbol = symbol;
    }
}