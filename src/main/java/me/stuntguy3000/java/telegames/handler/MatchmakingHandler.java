package me.stuntguy3000.java.telegames.handler;

import lombok.Getter;
import lombok.Setter;
import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.game.Game;
import me.stuntguy3000.java.telegames.object.game.matchmaking.MatchmakingGame;
import me.stuntguy3000.java.telegames.object.user.TelegramUser;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @author Luke Anderson | stuntguy3000
public class MatchmakingHandler {
    private Telegames instance;
    @Getter
    private HashMap<MatchmakingUser, List<String>> matchmakingQueue = new HashMap<>();
    @Getter
    private List<MatchmakingGame> startingGames = new ArrayList<>();
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
    public void addGame(TelegramUser user, String gameName) {
        MatchmakingUser matchmakingUser = getUserFromQueue(user);
        List<String> games = new ArrayList<>();

        if (matchmakingUser.getGames() != null) {
            games = matchmakingUser.getGames();
        }

        if (!games.contains(gameName)) {
            games.add(gameName);
            matchmakingUser.setGames(games);
            runMatchmaking();
        }
    }

    public void addNewUser(TelegramUser user) {
        matchmakingQueue.put(new MatchmakingUser(user, new ArrayList<>()), new ArrayList<>());
        runMatchmaking();
    }

    public int getQueueCount() {
        return matchmakingQueue.size();
    }

    private MatchmakingUser getUserFromQueue(TelegramUser user) {
        for (MatchmakingUser matchmakingUser : matchmakingQueue.keySet()) {
            if (matchmakingUser.getId() == user.getUserID()) {
                return matchmakingUser;
            }
        }

        return null;
    }

    public List<String> getUserOptions(TelegramUser user) {
        for (MatchmakingUser matchmakingUser : matchmakingQueue.keySet()) {
            if (matchmakingUser.getId() == user.getUserID()) {
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
    public boolean isInQueue(TelegramUser user) {
        for (MatchmakingUser matchmakingUser : matchmakingQueue.keySet()) {
            if (matchmakingUser.getId() == user.getUserID()) {
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
    public void removeGame(TelegramUser user, String gameName) {
        if (isInQueue(user)) {
            MatchmakingUser matchmakingUser = getUserFromQueue(user);
            List<String> games = new ArrayList<>();

            if (matchmakingUser.getGames() != null) {
                games = matchmakingUser.getGames();
            }

            if (games.remove(gameName)) {
                matchmakingUser.setGames(games);
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

    class MatchmakingTask implements Runnable {

        private void checkUsers(Game game) {
            /*
		if any games are starting
			if player can join
				add player
				return

		while playercount above min
		create new game lobby
				if lobby is not full
					add player to lobby
				else
					#createGameLobby
             */

            for (MatchmakingGame matchmakingGame : startingGames) {
                if (matchmakingGame.getGame().getGameName().equals(game.getGameName())) {
                    if (matchmakingGame.isHasStarted() && matchmakingGame.getGameUsers().size() < game.getMaxPlayers()) {
                        //matchmakingGame.addPlayer();
                    }
                }
            }
        }

        @Override
        public void run() {
            if (instance == null) {
                LogHandler.debug("Instance is null for MatchmakingHandler");
            }

            HashMap<Game, List<TelegramUser>> gameCounts = new HashMap<>();

            // Count the users
            for (List<String> games : getMatchmakingQueue().values()) {
                for (String gameName : games) {
                    Game game = instance.getGameHandler().getGame(gameName);

                    if (game != null) {
                        if (!gameCounts.containsKey(game)) {
                            //gameCounts.put(game, 1);
                        } else {
                            //gameCounts.put(game, gameCounts.get(game) + 1);
                        }
                    }
                }
            }

            for (Map.Entry<MatchmakingUser, List<String>> game :  getMatchmakingQueue().entrySet()) {
                //checkUsers(game);
            }
        }
    }
}

class MatchmakingUser {

    @Getter
    private final int id;
    @Getter
    private final TelegramUser user;
    @Getter
    private final String username;
    @Getter
    @Setter
    private List<String> games;

    MatchmakingUser(TelegramUser user, List<String> games) {
        this.id = user.getUserID();
        this.username = user.getUsername();
        this.user = user;
        this.games = games;
    }
}
    