package me.stuntguy3000.java.telegames.util;

// @author Luke Anderson | stuntguy3000
public class StringUtil {
    public static String isPlural(Number amount) {
        return amount.toString().equalsIgnoreCase("1") ? "" : "s";
    }
}
    