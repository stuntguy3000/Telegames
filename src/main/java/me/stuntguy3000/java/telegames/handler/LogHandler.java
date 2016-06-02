/*
 * MIT License
 *
 * Copyright (c) 2016 Luke Anderson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
