package me.stuntguy3000.java.telegames.handler;

import me.stuntguy3000.java.telegames.object.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

// @author Luke Anderson | stuntguy3000
public class GameHandler {
    private HashMap<String, Game> gameList = new HashMap<>();

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
            sb.append(game.getGameName()).append(": ").append(game.getGameDescription()).append("\n");
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