package me.stuntguy3000.java.telegames.util.string;

import java.util.Random;

// @author http://stackoverflow.com/a/41156
public class RandomString {
    private static final char[] symbols;
    private final char[] buf;
    private final Random random = new Random();

    public RandomString(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length < 1: " + length);
        }
        buf = new char[length];
    }

    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }
        return new String(buf);
    }

    static {
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ++ch) {
            tmp.append(ch);
        }
        symbols = tmp.toString().toCharArray();
    }
}