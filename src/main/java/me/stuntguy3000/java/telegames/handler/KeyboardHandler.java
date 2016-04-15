package me.stuntguy3000.java.telegames.handler;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.game.Game;
import me.stuntguy3000.java.telegames.object.lobby.Lobby;
import me.stuntguy3000.java.telegames.util.string.Emoji;
import me.stuntguy3000.java.telegames.util.string.Lang;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardMarkup;

import java.util.*;

// @author Luke Anderson | stuntguy3000
public class KeyboardHandler {
    public static SendableTextMessage.SendableTextMessageBuilder createCAHExtrasKeyboard(LinkedHashMap<String, Boolean> extrasPacks) {
        ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder replyKeyboardMarkupBuilder = ReplyKeyboardMarkup.builder();
        List<String> optionsRow = new ArrayList<>();

        for (Map.Entry<String, Boolean> extraPack : extrasPacks.entrySet()) {
            optionsRow.add((extraPack.getValue() ? Emoji.BLUE_CIRCLE.getText() : Emoji.RED_CIRCLE.getText()) + " " + extraPack.getKey());
        }

        replyKeyboardMarkupBuilder.addRow(optionsRow);
        replyKeyboardMarkupBuilder.addRow(Collections.singletonList(Lang.KEYBOARD_DONE));

        replyKeyboardMarkupBuilder.resize(true);
        replyKeyboardMarkupBuilder.oneTime(true);
        replyKeyboardMarkupBuilder.selective(false);

        return SendableTextMessage.builder().replyMarkup(replyKeyboardMarkupBuilder.build());
    }

    public static SendableTextMessage.SendableTextMessageBuilder createCAHKeyboard(String... options) {
        ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder replyKeyboardMarkupBuilder = ReplyKeyboardMarkup.builder();
        List<String> optionsRow = new ArrayList<>();

        Collections.addAll(optionsRow, options);

        replyKeyboardMarkupBuilder.addRow(optionsRow);
        replyKeyboardMarkupBuilder.addRow(Collections.singletonList(Lang.KEYBOARD_RANDOM));

        replyKeyboardMarkupBuilder.resize(true);
        replyKeyboardMarkupBuilder.oneTime(true);
        replyKeyboardMarkupBuilder.selective(false);

        return SendableTextMessage.builder().replyMarkup(replyKeyboardMarkupBuilder.build());
    }

    public static SendableTextMessage.SendableTextMessageBuilder createCancelMenu() {
        ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder replyKeyboardMarkupBuilder = ReplyKeyboardMarkup.builder();

        replyKeyboardMarkupBuilder.addRow(Collections.singletonList(Lang.KEYBOARD_CANCEL));

        replyKeyboardMarkupBuilder.resize(true);
        replyKeyboardMarkupBuilder.oneTime(true);
        replyKeyboardMarkupBuilder.selective(false);

        return SendableTextMessage.builder().replyMarkup(replyKeyboardMarkupBuilder.build());
    }

    public static SendableTextMessage.SendableTextMessageBuilder createGameSelector(Lobby lobby) {
        ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder replyKeyboardMarkupBuilder = ReplyKeyboardMarkup.builder();
        
        List<String> row = new ArrayList<>();

        int index = 1;
        int lobbySize = lobby.getTelegramUsers().size();

        for (Game game : Telegames.getInstance().getGameHandler().getGameMap().values()) {
            if ((game.isDevModeOnly() && !Telegames.DEV_MODE)
                    || game.isRestrictedGame() || lobbySize < game.getMinPlayers() || lobbySize > game.getMaxPlayers()) {
                continue;
            }

            if (index > 3) {
                index = 1;
                replyKeyboardMarkupBuilder.addRow(new ArrayList<>(row));
                row.clear();
            }

            row.add(Emoji.BLUE_RIGHT_ARROW.getText() + " " + game.getGameName());
            index++;
        }

        if (row.size() > 0) {
            replyKeyboardMarkupBuilder.addRow(new ArrayList<>(row));
        }

        replyKeyboardMarkupBuilder.addRow(Collections.singletonList(Lang.KEYBOARD_RETURN_MENU));

        replyKeyboardMarkupBuilder.resize(true);
        replyKeyboardMarkupBuilder.oneTime(true);
        replyKeyboardMarkupBuilder.selective(false);

        return SendableTextMessage.builder().replyMarkup(replyKeyboardMarkupBuilder.build());
    }

    public static SendableTextMessage.SendableTextMessageBuilder createLobbyCreationMenu() {
        ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder replyKeyboardMarkupBuilder = ReplyKeyboardMarkup.builder();

        replyKeyboardMarkupBuilder.addRow(Collections.singletonList(Lang.KEYBOARD_CREATE_LOBBY));
        replyKeyboardMarkupBuilder.addRow(Collections.singletonList(Lang.KEYBOARD_JOIN_LOBBY));
        //replyKeyboardMarkupBuilder.addRow(Collections.singletonList(Lang.KEYBOARD_JOIN_MATCHMAKING));

        replyKeyboardMarkupBuilder.resize(true);
        replyKeyboardMarkupBuilder.oneTime(true);
        replyKeyboardMarkupBuilder.selective(false);

        return SendableTextMessage.builder().replyMarkup(replyKeyboardMarkupBuilder.build());
    }

    public static SendableTextMessage.SendableTextMessageBuilder createLobbyMenu(String previousGame) {
        ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder replyKeyboardMarkupBuilder = ReplyKeyboardMarkup.builder();

        if (previousGame != null) {
            replyKeyboardMarkupBuilder.addRow(Collections.singletonList(Lang.KEYBOARD_REPLAY));
        }

        replyKeyboardMarkupBuilder.addRow(Collections.singletonList(Lang.KEYBOARD_PLAY));
        replyKeyboardMarkupBuilder.addRow(Arrays.asList(Lang.KEYBOARD_LEAVE_LOBBY, Lang.KEYBOARD_LOBBY_OPTIONS));
        replyKeyboardMarkupBuilder.addRow(Arrays.asList(Lang.KEYBOARD_RATE, Lang.KEYBOARD_ABOUT));

        replyKeyboardMarkupBuilder.resize(true);
        replyKeyboardMarkupBuilder.oneTime(true);
        replyKeyboardMarkupBuilder.selective(false);

        return SendableTextMessage.builder().replyMarkup(replyKeyboardMarkupBuilder.build());
    }

    public static SendableTextMessage.SendableTextMessageBuilder createLobbyOptionsMenu(boolean isLocked) {
        ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder replyKeyboardMarkupBuilder = ReplyKeyboardMarkup.builder();

        if (isLocked) {
            replyKeyboardMarkupBuilder.addRow(Collections.singletonList(Lang.KEYBOARD_LOBBY_UNLOCK));
        } else {
            replyKeyboardMarkupBuilder.addRow(Collections.singletonList(Lang.KEYBOARD_LOBBY_LOCK));
        }

        replyKeyboardMarkupBuilder.addRow(Collections.singletonList(Lang.KEYBOARD_RENAME));
        replyKeyboardMarkupBuilder.addRow(Collections.singletonList(Lang.KEYBOARD_RETURN_MENU));

        replyKeyboardMarkupBuilder.resize(true);
        replyKeyboardMarkupBuilder.oneTime(true);
        replyKeyboardMarkupBuilder.selective(false);

        return SendableTextMessage.builder().replyMarkup(replyKeyboardMarkupBuilder.build());
    }

    public static SendableTextMessage.SendableTextMessageBuilder createMatchmakingMenu(List<String> includedGames) {
        ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder replyKeyboardMarkupBuilder = ReplyKeyboardMarkup.builder();
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
                replyKeyboardMarkupBuilder.addRow(new ArrayList<>(row));
                row.clear();
            }

            row.add((includedGames.contains(game.getGameName()) ? Emoji.BLUE_CIRCLE.getText() : Emoji.RED_CIRCLE.getText()) + " " + game.getGameName());
            index++;
        }

        replyKeyboardMarkupBuilder.addRow(Collections.singletonList(Lang.KEYBOARD_QUIT_MATCHMAKING));

        replyKeyboardMarkupBuilder.resize(true);
        replyKeyboardMarkupBuilder.oneTime(true);
        replyKeyboardMarkupBuilder.selective(false);

        return SendableTextMessage.builder().replyMarkup(replyKeyboardMarkupBuilder.build());
    }
}
    