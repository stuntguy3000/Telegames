package me.stuntguy3000.java.telegames.game.keyboard;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;

public class Keyboard implements Iterable<KeyboardRow> {

    private final List<KeyboardRow> rows;

    public Keyboard() {
        this.rows = new LinkedList<>();
    }

    public KeyboardRow getRow(int x) {
        return this.rows.get(x);
    }

    public void addRow(KeyboardRow row) {
        this.rows.add(row);
    }

    public int size() {
        return this.rows.size();
    }

    public boolean isEmpty() {
        return this.rows.isEmpty();
    }

    @Override
    public Iterator<KeyboardRow> iterator() {
        return this.rows.iterator();
    }

    @Override
    public Spliterator<KeyboardRow> spliterator() {
        return Spliterators.spliterator(this.iterator(), this.rows.size(), Spliterator.SIZED | Spliterator.NONNULL | Spliterator.ORDERED);
    }

}
