package me.stuntguy3000.java.telegames.util;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.Game;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardMarkup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// @author Luke Anderson | stuntguy3000
public class KeyboardUtil {
    public static SendableTextMessage.SendableTextMessageBuilder createCancelMenu() {
        List<List<String>> buttonList = new ArrayList<>();

        buttonList.add(Collections.singletonList(TelegramEmoji.RED_CROSS.getText() + " Cancel"));

        return SendableTextMessage.builder().replyMarkup(new ReplyKeyboardMarkup(buttonList, true, true, false));
    }

    public static SendableTextMessage.SendableTextMessageBuilder createGameSelector() {
        List<List<String>> buttonList = new ArrayList<>();
        List<String> row = new ArrayList<>();

        int index = 1;

        for (Game game : Telegames.getInstance().getGameHandler().getGameMap().values()) {
            if (index > 3) {
                index = 1;
                buttonList.add(new ArrayList<>(row));
                row.clear();
            }

            row.add(TelegramEmoji.BLUE_RIGHT_ARROW.getText() + " " + game.getGameName());
            index++;
        }

        if (row.size() > 0) {
            buttonList.add(new ArrayList<>(row));
        }

        buttonList.add(Collections.singletonList(TelegramEmoji.BACK.getText() + " Back to menu"));

        return SendableTextMessage.builder().replyMarkup(new ReplyKeyboardMarkup(buttonList, true, false, false));
    }

    public static SendableTextMessage.SendableTextMessageBuilder createLobbyCreationMenu() {
        List<List<String>> buttonList = new ArrayList<>();

        buttonList.add(Collections.singletonList(TelegramEmoji.JOYSTICK.getText() + " Create a lobby"));
        buttonList.add(Collections.singletonList(TelegramEmoji.PERSON.getText() + " Join a lobby"));

        return SendableTextMessage.builder().replyMarkup(new ReplyKeyboardMarkup(buttonList, true, false, false));
    }

    public static SendableTextMessage.SendableTextMessageBuilder createLobbyMenu(String previousGame) {
        List<List<String>> buttonList = new ArrayList<>();

        if (previousGame != null) {
            buttonList.add(Collections.singletonList(TelegramEmoji.REPLAY.getText() + " Replay previous game"));
        }

        buttonList.add(Collections.singletonList(TelegramEmoji.JOYSTICK.getText() + " Play a game"));
        buttonList.add(Arrays.asList(TelegramEmoji.END.getText() + " Leave the lobby", TelegramEmoji.METAL_GEAR.getText() + " Lobby options"));
        //                                                                                                       Solid
        buttonList.add(Arrays.asList(TelegramEmoji.STAR.getText() + " Rate this bot", TelegramEmoji.BOOK.getText() + " About"));

        return SendableTextMessage.builder().replyMarkup(new ReplyKeyboardMarkup(buttonList, true, false, false));
    }

    public static SendableTextMessage.SendableTextMessageBuilder createLobbyOptionsMenu() {
        List<List<String>> buttonList = new ArrayList<>();

        buttonList.add(Collections.singletonList(TelegramEmoji.PADLOCK.getText() + " Lock/Unlock lobby"));
        buttonList.add(Collections.singletonList(TelegramEmoji.PENCIL.getText() + " Rename lobby"));
        buttonList.add(Collections.singletonList(TelegramEmoji.BACK.getText() + " Back to menu"));

        return SendableTextMessage.builder().replyMarkup(new ReplyKeyboardMarkup(buttonList, true, false, false));
    }
}
    