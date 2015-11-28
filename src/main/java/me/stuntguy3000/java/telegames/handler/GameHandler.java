package me.stuntguy3000.java.telegames.handler;

import me.stuntguy3000.java.telegames.object.Game;

import java.util.HashMap;

// @author Luke Anderson | stuntguy3000
public class GameHandler {
    private HashMap<String, Game> gameList;

    public GameHandler() {
        gameList = new HashMap<>();
    }

    /**
     * Register a game class
     *
     * @param game Game the game to be registered
     */
    public void registerGame(Game game) {
        gameList.put(game.getName().toLowerCase(), game);
        LogHandler.log("Registered game %s.", game.getName());
    }

    /**
     * Returns a String with a list of games
     * <p>Used for sending messages to a Lobby or User, and not for obtaining a list of games.</p>
     *
     * @return String a list of games
     */
    public String getGameList() {
        StringBuilder sb = new StringBuilder();
        sb.append("Game List:\n");
        for (Game game : gameList.values()) {
            sb.append(game.getName()).append(": ").append(game.getDescription()).append("\n");
        }

        return sb.toString();
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
}
    