package me.stuntguy3000.java.telegames.object.game.matchmaking;

import lombok.Getter;
import lombok.Setter;
import me.stuntguy3000.java.telegames.object.exception.GameInProgressException;
import me.stuntguy3000.java.telegames.object.exception.LobbyFullException;
import me.stuntguy3000.java.telegames.object.game.Game;
import me.stuntguy3000.java.telegames.object.user.TelegramUser;
import me.stuntguy3000.java.telegames.util.string.Lang;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

// @author Luke Anderson | stuntguy3000
public class MatchmakingGame {
    @Getter
    @Setter
    private boolean currentlyIngame = false;
    @Getter
    @Setter
    private Game game;
    @Getter
    @Setter
    private List<TelegramUser> gameUsers;
    @Getter
    @Setter
    private boolean hasStarted = false;
    @Getter
    @Setter
    private int lobbyID;
    @Getter
    @Setter
    private int timeToStart = 30;
    @Getter
    @Setter
    private int timerTask;

    public MatchmakingGame(Game game, int lobbyID, int timeToStart) {
        setGame(game);
        setLobbyID(lobbyID);
        setTimeToStart(timeToStart);
    }

    public void addPlayer(TelegramUser telegramUser) throws GameInProgressException, LobbyFullException {
        if (currentlyIngame) {
            throw new GameInProgressException();
        }

        if (gameUsers.size() == game.getMaxPlayers()) {
            throw new LobbyFullException();
        }

        game.playerJoin(telegramUser);
        gameUsers.add(telegramUser);

        if (!hasStarted) {
            hasStarted = true;
            new MatchmakingGameTask();
        }
    }

    public void endGame() {
        game.endGame();
    }

    private class MatchmakingGameTask extends TimerTask {
        public MatchmakingGameTask() {
            new Timer().schedule(this, 0, 1000);
        }

        @Override
        public void run() {
            if (currentlyIngame) {
                this.cancel();
            }

            if (isHasStarted()) {
                if (gameUsers.size() >= game.getMinPlayers()) {
                    timeToStart--;

                    switch (timeToStart) {
                        case 30:
                        case 15:
                        case 5: {
                            game.getGameLobby().sendMessage(Lang.build(Lang.MATCHMAKING_STARTING, String.valueOf(timeToStart)).build());
                            return;
                        }
                        case 0: {
                            game.getGameLobby().sendMessage(Lang.build(Lang.MATCHMAKING_STARTING_NOW).build());
                        }
                    }

                } else {
                    timeToStart = 30;
                }
            }
        }
    }
}
    