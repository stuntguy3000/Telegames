package me.stuntguy3000.java.telegames.handler;

import lombok.Getter;
import lombok.Setter;
import me.stuntguy3000.java.telegames.Telegames;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// @author Luke Anderson | stuntguy3000
public class MatchmakingHandler {
    private Telegames instance;
    @Getter
    private HashMap<MatchmakingUser, List<String>> matchmakingQueue = new HashMap<>();
    private Thread thread;

    public MatchmakingHandler() {
        this.instance = Telegames.getInstance();
        thread = new Thread(new MatchmakingTask());
    }

    /**
     * Add a game to the user's matchmaking preferences
     *
     * @param user
     * @param gameName
     */
    public void addGame(User user, String gameName) {
        MatchmakingUser matchmakingUser = getUserFromQueue(user);
        List<String> games = new ArrayList<>();

        if (matchmakingUser.getGames() != null) {
            games = matchmakingUser.getGames();
        }

        if (!games.contains(gameName)) {
            games.add(gameName);
            runMatchmaking();
        }
    }

    public void addNewUser(User user) {
        matchmakingQueue.put(new MatchmakingUser(user.getId(), user.getUsername(), null), new ArrayList<>());
        runMatchmaking();
    }

    public int getQueueCount() {
        return matchmakingQueue.size();
    }

    private MatchmakingUser getUserFromQueue(User user) {
        for (MatchmakingUser matchmakingUser : matchmakingQueue.keySet()) {
            if (matchmakingUser.getId() == user.getId()) {
                return matchmakingUser;
            }
        }

        return null;
    }

    public List<String> getUserOptions(User user) {
        for (MatchmakingUser matchmakingUser : matchmakingQueue.keySet()) {
            if (matchmakingUser.getId() == user.getId()) {
                return matchmakingUser.getGames();
            }
        }

        return null;
    }

    /**
     * Returns if the user is in a Matchmaking queue
     *
     * @param user
     * @return
     */
    public boolean isInQueue(User user) {
        for (MatchmakingUser matchmakingUser : matchmakingQueue.keySet()) {
            if (matchmakingUser.getId() == user.getId()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Remove a game from the user's matchmaking preferences
     *
     * @param user
     * @param gameName
     */
    public void removeGame(User user, String gameName) {
        if (isInQueue(user)) {
            MatchmakingUser matchmakingUser = getUserFromQueue(user);
            List<String> games = new ArrayList<>();

            if (matchmakingUser.getGames() != null) {
                games = matchmakingUser.getGames();
            }

            if (games.remove(gameName)) {
                runMatchmaking();
            }
        }
    }

    public void removeUser(User user) {
        for (MatchmakingUser matchmakingUser : new ArrayList<>(matchmakingQueue.keySet())) {
            if (matchmakingUser.getId() == user.getId()) {
                matchmakingQueue.remove(matchmakingUser);
            }
        }

        runMatchmaking();
    }

    /**
     * Runs the matchmaking method
     */
    public void runMatchmaking() {
        thread.run();
    }
}

class MatchmakingTask implements Runnable {
    private final Telegames instance;

    public MatchmakingTask() {
        this.instance = Telegames.getInstance();
    }

    @Override
    public void run() {
        if (instance == null) {
            LogHandler.debug("Instance is null for MatchmakingHandler");
        }
    }
}

class MatchmakingUser {

    @Getter
    private final int id;
    @Getter
    private final String username;
    @Getter
    @Setter
    private List<String> games;

    MatchmakingUser(int id, String username, List<String> games) {
        this.id = id;
        this.username = username;
        this.games = games;
    }
}
    