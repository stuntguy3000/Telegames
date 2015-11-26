package me.stuntguy3000.java.groupgamebot.util;

// @author Luke Anderson | stuntguy3000
public class StringUtil {
    public static String isPlural(Number amount) {
        return amount.toString().equalsIgnoreCase("1") ? "" : "s";
    }
}
    