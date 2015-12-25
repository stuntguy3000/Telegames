package me.stuntguy3000.java.telegames.handler;

import me.stuntguy3000.java.telegames.Telegames;

/**
 * Created by amir on 2015-11-25.
 * Modified by stuntguy3000.
 */
public class LogHandler {

    public static void debug(String s, Object... format) {
        if (Telegames.DEV_MODE) {
            log("[DEBUG] " + s, format);
        }
    }

    public static void error(String s) {
        s = "[ERROR] " + s;
        log(s);
        Telegames.getInstance().sendToAdmins(s);
    }

    public static void log(String s, Object... format) {
        System.out.println(String.format(s, format));
    }


}
