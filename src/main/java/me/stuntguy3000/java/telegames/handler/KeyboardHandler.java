package me.stuntguy3000.java.telegames.handler;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.game.Game;
import me.stuntguy3000.java.telegames.util.string.Emoji;
import me.stuntguy3000.java.telegames.util.string.Lang;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardMarkup;

import java.util.*;

// @author Luke Anderson | stuntguy3000
public class KeyboardHandler {
    public static SendableTextMessage.SendableTextMessageBuilder createCAHExtrasKeyboard(LinkedHashMap<String, Boolean> extrasPacks) {
        List<List<String>> buttonList = new ArrayList<>();
        List<String> optionsRow = new ArrayList<>();

        for (Map.Entry<String, Boolean> extraPack : extrasPacks.entrySet()) {
            optionsRow.add((extraPack.getValue() ? Emoji.BLUE_CIRCLE.getText() : Emoji.RED_CIRCLE.getText()) + " " + extraPack.getKey());
        }

        buttonList.add(optionsRow);
        buttonList.add(Collections.singletonList(Lang.KEYBOARD_DONE));
        return SendableTextMessage.builder().replyMarkup(new ReplyKeyboardMarkup(buttonList, true, true, false));
    }

    public static SendableTextMessage.SendableTextMessageBuilder createCAHKeyboard(String... options) {
        List<List<String>> buttonList = new ArrayList<>();
        List<String> optionsRow = new ArrayList<>();

        Collections.addAll(optionsRow, options);

        buttonList.add(optionsRow);
        buttonList.add(Collections.singletonList(Lang.KEYBOARD_RANDOM));

        return SendableTextMessage.builder().replyMarkup(new ReplyKeyboardMarkup(buttonList, true, true, false));
    }

    public static SendableTextMessage.SendableTextMessageBuilder createCancelMenu() {
        List<List<String>> buttonList = new ArrayList<>();

        buttonList.add(Collections.singletonList(Lang.KEYBOARD_CANCEL));

        return SendableTextMessage.builder().replyMarkup(new ReplyKeyboardMarkup(buttonList, true, true, false));
    }

    public static SendableTextMessage.SendableTextMessageBuilder createGameSelector() {
        List<List<String>> buttonList = new ArrayList<>();
        List<String> row = new ArrayList<>();

        int index = 1;

        for (Game game : Telegames.getInstance().getGameHandler().getGameMap().values()) {
            if ((game.isDevModeOnly() && !Telegames.DEV_MODE) || game.isRestrictedGame()) {
                continue;
            }

            if (index > 3) {
                index = 1;
                buttonList.add(new ArrayList<>(row));
                row.clear();
            }

            row.add(Emoji.BLUE_RIGHT_ARROW.getText() + " " + game.getGameName());
            index++;
        }

        if (row.size() > 0) {
            buttonList.add(new ArrayList<>(row));
        }

        buttonList.add(Collections.singletonList(Lang.KEYBOARD_RETURN_MENU));

        return SendableTextMessage.builder().replyMarkup(new ReplyKeyboardMarkup(buttonList, true, false, false));
    }

    public static SendableTextMessage.SendableTextMessageBuilder createLobbyCreationMenu() {
        List<List<String>> buttonList = new ArrayList<>();

        buttonList.add(Collections.singletonList(Lang.KEYBOARD_CREATE_LOBBY));
        buttonList.add(Collections.singletonList(Lang.KEYBOARD_JOIN_LOBBY));
        //buttonList.add(Collections.singletonList(Lang.KEYBOARD_JOIN_MATCHMAKING));

        return SendableTextMessage.builder().replyMarkup(new ReplyKeyboardMarkup(buttonList, true, false, false));
    }

    public static SendableTextMessage.SendableTextMessageBuilder createLobbyMenu(String previousGame) {
        List<List<String>> buttonList = new ArrayList<>();

        if (previousGame != null) {
            buttonList.add(Collections.singletonList(Lang.KEYBOARD_REPLAY));
        }

        buttonList.add(Collections.singletonList(Lang.KEYBOARD_PLAY));
        buttonList.add(Arrays.asList(Lang.KEYBOARD_LEAVE_LOBBY, Lang.KEYBOARD_LOBBY_OPTIONS));
        buttonList.add(Arrays.asList(Lang.KEYBOARD_RATE, Lang.KEYBOARD_ABOUT));

        return SendableTextMessage.builder().replyMarkup(new ReplyKeyboardMarkup(buttonList, true, false, false));
    }

    public static SendableTextMessage.SendableTextMessageBuilder createLobbyOptionsMenu(boolean isLocked) {
        List<List<String>> buttonList = new ArrayList<>();

        if (isLocked) {
            buttonList.add(Collections.singletonList(Lang.KEYBOARD_LOBBY_UNLOCK));
        } else {
            buttonList.add(Collections.singletonList(Lang.KEYBOARD_LOBBY_LOCK));
        }

        buttonList.add(Collections.singletonList(Lang.KEYBOARD_RENAME));
        buttonList.add(Collections.singletonList(Lang.KEYBOARD_RETURN_MENU));

        return SendableTextMessage.builder().replyMarkup(new ReplyKeyboardMarkup(buttonList, true, false, false));
    }

    public static SendableTextMessage.SendableTextMessageBuilder createMatchmakingMenu(List<String> includedGames) {
        List<List<String>> buttonList = new ArrayList<>();
        List<String> row = new ArrayList<>();

        if (includedGames == null) {
            includedGames = new ArrayList<>();
        }

        int index = 1;

        for (Game game : Telegames.getInstance().getGameHandler().getGameMap().values()) {
            if ((game.isDevModeOnly() && !Telegames.DEV_MODE) || game.isRestrictedGame()) {
                continue;
            }

            if (index > 3) {
                index = 1;
                buttonList.add(new ArrayList<>(row));
                row.clear();
            }

            row.add((includedGames.contains(game.getGameName()) ? Emoji.BLUE_CIRCLE.getText() : Emoji.RED_CIRCLE.getText()) + " " + game.getGameName());
            index++;
        }

        buttonList.add(Collections.singletonList(Lang.KEYBOARD_QUIT_MATCHMAKING));

        return SendableTextMessage.builder().replyMarkup(new ReplyKeyboardMarkup(buttonList, true, false, false));
    }
}
    