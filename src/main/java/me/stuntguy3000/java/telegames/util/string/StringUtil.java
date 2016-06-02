package me.stuntguy3000.java.telegames.util.string;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.concurrent.TimeUnit;

// @author Luke Anderson | stuntguy3000
public class StringUtil {
    /**
     * Workaround to allow usernames with underscores
     *
     * @param message String the original message
     *
     * @return String the "cleaned" string
     */
    public static String markdownSafe(String message) {
        return StringEscapeUtils.escapeJava(message);
    }

    /**
     * Converts time (in milliseconds) to human-readable format "<dd:><hh:>mm:ss"
     */
    public static String millisecondsToHumanReadable(long duration) {
        String res;
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
        if (days == 0) {
            if (hours == 0) {
                res = String.format("%02d:%02d", minutes, seconds);
            } else {
                res = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            }
        } else {
            res = String.format("%dd%02d:%02d:%02d", days, hours, minutes, seconds);
        }
        return res;
    }
}
    