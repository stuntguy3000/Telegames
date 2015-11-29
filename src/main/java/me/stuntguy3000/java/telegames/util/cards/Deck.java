package me.stuntguy3000.java.telegames.util.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private List<Card> deck = new ArrayList<Card>();

    public Deck() {

		/* Create the contents of the deck */
        for (Suit s : Suit.values()) {
            for (Rank r : Rank.values()) {
                Card c = new Card(s, r);
                deck.add(c);
            }
        }
    }

    public Card getCard() {
        return deck.remove(0);
    }

    public void shuffleCards() {
        Collections.shuffle(this.deck);
    }
}