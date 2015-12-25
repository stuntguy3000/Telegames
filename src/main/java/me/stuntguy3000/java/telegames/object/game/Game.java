package me.stuntguy3000.java.telegames.object.game;

import lombok.Getter;
import lombok.Setter;
import me.stuntguy3000.java.telegames.object.exception.GameInProgressException;
import me.stuntguy3000.java.telegames.object.exception.GameStartException;
import me.stuntguy3000.java.telegames.object.lobby.Lobby;
import me.stuntguy3000.java.telegames.object.lobby.LobbyMember;
import me.stuntguy3000.java.telegames.util.StringUtil;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardHide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Game {
    @Getter
    @Setter
    private List<LobbyMember> activePlayers = new ArrayList<>();
    @Getter
    @Setter
    private boolean devModeOnly = false;
    @Getter
    private String gameDescription;
    @Getter
    @Setter
    private Lobby gameLobby;
    @Getter
    private String gameName;
    @Getter
    @Setter
    private GameState gameState;
    @Getter
    @Setter
    private int maxPlayers = 8;
    @Getter
    @Setter
    private int minPlayers = 3;
    @Getter
    @Setter
    private boolean restrictedGame = false;

    public boolean checkPlayers() {
        if (getMinPlayers() > getActivePlayers().size()) {
            SendableTextMessage message = SendableTextMessage.builder().message("*There are not enough players to continue!*").parseMode(ParseMode.MARKDOWN).build();
            getGameLobby().sendMessage(message);
            getGameLobby().stopGame();
            return false;
        }
        return true;
    }

    public void endGame() {
        SendableTextMessage.SendableTextMessageBuilder messageBuilder = SendableTextMessage.builder().message("The game has ended!").replyMarkup(ReplyKeyboardHide.builder().build());

        getGameLobby().sendMessage(messageBuilder.build());
        printScores();
    }

    public String getGameHelp() {
        return "No help available";
    }

    public void onSecond() {
        // Do nothing
    }

    public abstract void onTextMessageReceived(TextMessageReceivedEvent event);

    public void playerJoin(LobbyMember player) throws GameInProgressException {
        if (gameState == GameState.WAITING_FOR_PLAYERS) {
            getActivePlayers().add(player);
        } else {
            throw new GameInProgressException();
        }
    }

    public void playerLeave(String username, int userID) {
        for (LobbyMember member : new ArrayList<>(activePlayers)) {
            if (member.getUserID() == userID) {
                activePlayers.remove(userID);
                return;
            }
        }

        if (activePlayers.size() < minPlayers) {
            getGameLobby().stopGame();
        }
    }

    public void printScores() {
        Collections.sort(getActivePlayers());
        StringBuilder wholeMessage = new StringBuilder();
        int playerPos = 1;
        for (int i = 0; i < getActivePlayers().size(); i++) {
            LobbyMember lobbyMember = getActivePlayers().get(i);
            wholeMessage.append(String.format("#%d - %s (Score: %d)\n", playerPos++, StringUtil.markdownSafe(lobbyMember.getUsername()), lobbyMember.getGameScore()));
        }
        getGameLobby().sendMessage(wholeMessage.toString());
    }

    public void removePlayer(String username) {
        for (LobbyMember lobbyMember : new ArrayList<>(getActivePlayers())) {
            if (lobbyMember.getUsername().equals(username)) {
                getActivePlayers().remove(lobbyMember);
            }
        }
    }

    public void setGameInfo(String name, String description) {
        this.gameName = name;
        this.gameDescription = description;
    }

    public abstract void startGame();

    public void tryStartGame() throws GameStartException {
        if (activePlayers.size() >= getMinPlayers()) {
            if (activePlayers.size() > getMaxPlayers()) {
                throw new GameStartException("Too many players! Maximum: " + getMaxPlayers());
            } else {
                startGame();
            }
        } else {
            throw new GameStartException("Not enough players! Required: " + getMaxPlayers());
        }
    }
}
