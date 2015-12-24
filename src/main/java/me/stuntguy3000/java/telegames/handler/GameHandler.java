package me.stuntguy3000.java.telegames.handler;

import me.stuntguy3000.java.telegames.object.Game;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardMarkup;

import java.util.*;

// @author Luke Anderson | stuntguy3000
public class GameHandler {
    private HashMap<String, Game> gameList = new HashMap<>();

    public SendableTextMessage.SendableTextMessageBuilder createGameSelector() {
        List<List<String>> buttonList = new ArrayList<>();
        List<String> row = new ArrayList<>();

        int index = 1;

        for (Game game : gameList.values()) {
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

    /**
     * Returns a Game associated with gameName
     *
     * @param gameName String the game's name
     * @return Game associated with gameName
     */
    public Game getGame(String gameName) {
        return gameList.get(gameName.toLowerCase());
    }

    /**
     * Returns a String with a list of games <p>Used for sending messages to a Lobby or User, and not for obtaining a
     * list of games.</p>
     *
     * @return String a list of games
     */
    public String getGameList() {
        StringBuilder sb = new StringBuilder();
        sb.append("Game List:\n");
        for (Game game : gameList.values()) {
            sb.append("*").append(game.getGameName()).append("*: ").append(game.getGameDescription()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Returns a randomly chosen Game
     *
     * @return Game a randomly chosen Game
     */
    public Game getRandomGame() {
        Random random = new Random();
        List<Game> gameNewList = new ArrayList<>(gameList.values());

        return gameNewList.get(random.nextInt(gameNewList.size() - 1));
    }

    /**
     * Register a game class
     *
     * @param game Game the game to be registered
     */
    public void registerGame(Game game) {
        gameList.put(game.getGameName().toLowerCase(), game);
        LogHandler.log("Registered game %s.", game.getGameName());
    }
}