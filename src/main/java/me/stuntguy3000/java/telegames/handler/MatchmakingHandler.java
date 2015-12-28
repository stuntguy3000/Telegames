package me.stuntguy3000.java.telegames.handler;

import lombok.Getter;
import lombok.Setter;
import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.exception.LobbyFullException;
import me.stuntguy3000.java.telegames.object.exception.LobbyLockedException;
import me.stuntguy3000.java.telegames.object.exception.UserBannedException;
import me.stuntguy3000.java.telegames.object.game.Game;
import me.stuntguy3000.java.telegames.object.lobby.Lobby;
import me.stuntguy3000.java.telegames.object.user.TelegramUser;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// @author Luke Anderson | stuntguy3000
public class MatchmakingHandler {
    @Getter
    private HashMap<String, String> gamesStarting = new HashMap<>();
    private Telegames instance;
    @Getter
    private HashMap<MatchmakingUser, List<String>> matchmakingQueue = new HashMap<>();
    private Thread thread;

    public MatchmakingHandler() {
        this.instance = Telegames.getInstance();
        thread = new Thread(new MatchmakingTask(instance));
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

    public void addGameStarting(Lobby matchmakingLobby, Game userGame) {
        gamesStarting.put(userGame.getGameName(), matchmakingLobby.getLobbyID());
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
}

class MatchmakingTask implements Runnable {
    private final Telegames instance;

    public MatchmakingTask(Telegames instance) {
        this.instance = instance;
    }

    @Override
    public void run() {
        if (instance == null) {
            LogHandler.debug("Instance is null for MatchmakingHandler");
        }

        // Loop through all players and their selections
        for (MatchmakingUser matchmakingUser : instance.getMatchmakingHandler().getMatchmakingQueue().keySet()) {
            LogHandler.debug("Scanning user " + matchmakingUser.getUsername());
            for (String game : matchmakingUser.getGames()) {
                LogHandler.debug("Checking game " + matchmakingUser.getUsername());
                Game userGame = instance.getGameHandler().getGame(game);
                String correctGameName = userGame.getGameName();

                if (userGame != null) {
                    String existingLobbyID = instance.getMatchmakingHandler().getGamesStarting().get(correctGameName);
                    if (existingLobbyID != null) {
                        Lobby lobby = instance.getLobbyHandler().getLobby(existingLobbyID);

                        if (lobby != null && lobby.getCurrentGame() == null) {
                            LogHandler.debug("Adding user to " + lobby.getLobbyID());
                            try {
                                lobby.userJoin(matchmakingUser.getUser());
                                LogHandler.debug("Added user to " + lobby.getLobbyID());
                                break;
                            } catch (LobbyLockedException | UserBannedException | LobbyFullException ignore) {
                                LogHandler.debug("Failed adding user to " + lobby.getLobbyID());
                                instance.getMatchmakingHandler().getGamesStarting().remove(correctGameName);
                            }
                        } else {
                            instance.getMatchmakingHandler().getGamesStarting().remove(correctGameName);
                        }
                    }

                    try {
                        Lobby matchmakingLobby = instance.getLobbyHandler().createMatchmakingLobby(userGame);
                        matchmakingLobby.userJoin(matchmakingUser.getUser());
                        instance.getMatchmakingHandler().addGameStarting(matchmakingLobby, userGame);
                        LogHandler.debug("Creating new lobby, adding user to " + matchmakingLobby.getLobbyID());
                    } catch (LobbyLockedException | UserBannedException | LobbyFullException ignore) {

                    }
                }
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
    