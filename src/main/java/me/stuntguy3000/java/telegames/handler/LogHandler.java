package me.stuntguy3000.java.telegames.handler;

import me.stuntguy3000.java.telegames.Telegames;

/**
 * @author stuntguy3000
 */
public class LogHandler {
    /**
     * Send a message intended for debugging purposes.
     * <p>Will only be outputted to console if developmentMode is enabled</p>
     *
     * @param message String the message to be outputted to console
     * @param format  Object[] formatting tags which will be applied to message
     */
    public static void debug(String message, Object... format) {
        if (Telegames.getInstance().isDevelopmentMode()) {
            log("[DEBUG] " + message, format);
        }
    }

    /**
     * Log an error message to console
     * <p>All messages will have an error prefix</p>
     *
     * @param message String the message to be outputted to console
     * @param format  Object[] formatting tags which will be applied to message
     */
    static void sendError(String message, Object... format) {
        message = "[ERROR] " + message;
        log(message, format);
        Telegames.getInstance().sendToAdmins(message);
    }

    /**
     * Log a message to console
     *
     * @param message String the message to be outputted to console
     * @param format  Object[] formatting tags which will be applied to message
     */
    public static void log(String message, Object... format) {
        System.out.println(String.format(message, format));
    }
}
