package me.stuntguy3000.java.telegames.object;

import org.apache.commons.lang3.StringEscapeUtils;

// @author Luke Anderson | stuntguy3000
public class StringUtil {
    /**
     * Workaround to allow usernames with underscores
     *
     * @param message String the original message
     * @return String the "cleaned" string
     */
    public static String cleanString(String message) {
        return StringEscapeUtils.escapeJava(message);
    }
}
    