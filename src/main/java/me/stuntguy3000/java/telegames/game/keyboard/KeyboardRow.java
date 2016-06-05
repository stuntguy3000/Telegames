package me.stuntguy3000.java.telegames.game.keyboard;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;

public class KeyboardRow implements Iterable<KeyboardButton> {

    private final List<KeyboardButton> buttons;

    public KeyboardRow() {
        this.buttons = new LinkedList<>();
    }

    public KeyboardButton getButton(int x) {
        return this.buttons.get(x);
    }

    public void addButton(KeyboardButton button) {
        this.buttons.add(button);
    }

    public int size() {
        return this.buttons.size();
    }

    public boolean isEmpty() {
        return this.buttons.isEmpty();
    }

    @Override
    public Iterator<KeyboardButton> iterator() {
        return this.buttons.iterator();
    }

    @Override
    public Spliterator<KeyboardButton> spliterator() {
        return Spliterators.spliterator(this.iterator(), this.buttons.size(), Spliterator.SIZED | Spliterator.NONNULL | Spliterator.ORDERED);
    }

}
