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

package me.stuntguy3000.java.telegames.util.string;

import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;

// @author Luke Anderson | stuntguy3000
public class Lang {

    public static final String ERROR = Emoji.ALERT.getText() + " ";
    public static final String SUCCESS = Emoji.GREEN_BOX_TICK.getText() + " ";
    public static final String LOBBY_USER_JOIN = Emoji.PERSON.getText() + " @%s *has joined the lobby.*";
    public static final String LOBBY_USER_LEAVE = Emoji.PERSON.getText() + " @%s *has left the lobby.*";
    public static final String LOBBY_CANNOT_JOIN_LOCKED = ERROR + " *You cannot join a locked lobby!*";
    public static final String LOBBY_INFO_TITLE = Emoji.SPACE_INVADER.getText() + " *Telegames Lobby (#%s)* " + Emoji.SPACE_INVADER.getText();
    public static final String LOBBY_INFO_LOCKED = ERROR + " *This lobby is locked!*";
    public static final String LOBBY_INFO_OWNER = Emoji.PERSON.getText() + " *Lobby Owner:* %s";
    public static final String LOBBY_INFO_PLAYERS = Emoji.PEOPLE.getText() + " *Lobby Players:* %s";
    public static final String LOBBY_INFO_WAITING = "*Ready to begin a game...*";
    public static final String LOBBY_INFO_INGAME = "*Currently playing %s*";

    public static SendableTextMessage.SendableTextMessageBuilder build(String text, Object... variables) {
        return SendableTextMessage.builder().message(String.format(text, variables)).parseMode(ParseMode.MARKDOWN);
    }
}
    