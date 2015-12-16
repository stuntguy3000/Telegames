package me.stuntguy3000.java.telegames.object.config;

import lombok.Getter;
import me.stuntguy3000.java.telegames.object.Game;
import me.stuntguy3000.java.telegames.object.GameStatistics;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.*;

// @author Luke Anderson | stuntguy3000
public class UserStatistics {
    @Getter
    private HashMap<String, Integer> gamePlayCount = new HashMap<>();
    @Getter
    private HashMap<Integer, Long> knownPlayers = new HashMap<>();

    public void addGame(Game game) {
        int count = 0;
        if (gamePlayCount.containsKey(game.getGameName())) {
            count = gamePlayCount.get(game.getGameName());
        }

        gamePlayCount.put(game.getGameName(), ++count);
    }

    public void addPlayer(User user) {
        knownPlayers.put(user.getId(), System.currentTimeMillis());
    }

    public List<GameStatistics> sortGames() {
        List<GameStatistics> gameStats = new ArrayList<>();

        for (Map.Entry<String, Integer> name : gamePlayCount.entrySet()) {
            gameStats.add(new GameStatistics(name.getKey(), name.getValue()));
        }

        Collections.sort(gameStats);
        return gameStats;
    }
}
    