package me.stuntguy3000.java.telegames.object;

import lombok.Getter;
import lombok.Setter;
import me.stuntguy3000.java.telegames.util.GameState;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Game {
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private Lobby lobby;
    @Getter
    @Setter
    private List<PlayerData> activePlayers;
    @Getter
    @Setter
    private GameState gameState;

    public void setInfo(String name, String description) {
        this.name = name;
        this.description = description;
        this.activePlayers = new ArrayList<>();
        this.gameState = GameState.WAITING_FOR_PLAYERS;
    }

    public abstract void onTextMessageReceived(TextMessageReceivedEvent event);

    public abstract boolean startGame();

    public abstract void stopGame(boolean silent);

    public abstract boolean playerJoin(Player player, boolean silent);

    public abstract void playerLeave(Player player, boolean silent);

    public abstract String getGameHelp();

    public void sortScores() {
        Collections.sort(activePlayers);
    }

    public int getScore(String username) {
        for (PlayerData playerData : activePlayers) {
            if (playerData.getUsername().equalsIgnoreCase(username)) {
                return playerData.getScore();
            }
        }
        return -1;
    }

    public PlayerData getPlayerData(User user) {
        for (PlayerData playerData : activePlayers) {
            if (playerData.getId() == user.getId()) {
                return playerData;
            }
        }
        return null;
    }

    public PlayerData getPlayerData(int id) {
        for (PlayerData playerData : activePlayers) {
            if (playerData.getId() == id) {
                return playerData;
            }
        }
        return null;
    }

    public PlayerData getPlayerData(String username) {
        for (PlayerData playerData : activePlayers) {
            if (username.equalsIgnoreCase(playerData.getUsername())) {
                return playerData;
            }
        }
        return null;
    }

    public void setScore(String username, int score) {
        activePlayers.stream().filter(playerData -> playerData.getUsername().equalsIgnoreCase(username)).forEach(playerData -> {
            playerData.setScore(score);
        });
    }

    public void addPlayer(Player player) {
        activePlayers.add(new PlayerData(player, 0));
    }

    public void removePlayer(Player player) {
        for (PlayerData playerData : new ArrayList<>(activePlayers)) {
            if (playerData.getUsername().equalsIgnoreCase(player.getUsername())) {
                activePlayers.remove(playerData);
                return;
            }
        }
    }
}
