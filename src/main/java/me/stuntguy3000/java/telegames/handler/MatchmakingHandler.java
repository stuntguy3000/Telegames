package me.stuntguy3000.java.telegames.handler;

import lombok.Getter;
import me.stuntguy3000.java.telegames.Telegames;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// @author Luke Anderson | stuntguy3000
public class MatchmakingHandler {
    private Telegames instance;
    @Getter
    private HashMap<User, List<String>> matchmakingQueue = new HashMap<>();
    private Thread thread = new Thread(new MatchmakingTask(instance));

    public MatchmakingHandler() {
        this.instance = Telegames.getInstance();
    }

    /**
     * Add a game to the user's matchmaking preferences
     *
     * @param user
     * @param gameName
     */
    public void addGame(User user, String gameName) {
        List<String> games = new ArrayList<>();

        if (matchmakingQueue.containsKey(user)) {
            games = matchmakingQueue.get(user);
        }

        if (!games.contains(gameName)) {
            games.add(gameName);
            matchmakingQueue.put(user, games);
            runMatchmaking();
        }
    }

    public int getQueueCount() {
        return matchmakingQueue.size();
    }

    /**
     * Returns the user's matchmaking options
     */
    public List<String> getUserOptions(User user) {
        return matchmakingQueue.get(user);
    }

    /**
     * Returns if the user is in a Matchmaking queue
     *
     * @param user
     * @return
     */
    public boolean isInQueue(User user) {
        return matchmakingQueue.containsKey(user);
    }

    /**
     * Remove a game from the user's matchmaking preferences
     *
     * @param user
     * @param gameName
     */
    public void removeGame(User user, String gameName) {
        if (matchmakingQueue.containsKey(user)) {
            List<String> games = matchmakingQueue.get(user);

            if (games.contains(gameName)) {
                games.remove(gameName);
                matchmakingQueue.put(user, games);
                runMatchmaking();
            }
        }
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
    private HashMap<User, List<String>> matchmakingQueue;

    public MatchmakingTask(Telegames instance) {
        this.instance = instance;
    }

    @Override
    public void run() {
        matchmakingQueue = instance.getMatchmakingHandler().getMatchmakingQueue();
    }
}
    