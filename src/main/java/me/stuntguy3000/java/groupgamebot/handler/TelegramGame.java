package me.stuntguy3000.java.groupgamebot.handler;

import lombok.Getter;
import lombok.Setter;
import me.stuntguy3000.java.groupgamebot.hook.TelegramHook;
import me.stuntguy3000.java.groupgamebot.util.GameState;
import me.stuntguy3000.java.groupgamebot.util.PlayerScore;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TelegramGame {
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private Chat chat;
    @Getter
    @Setter
    private List<PlayerScore> activePlayers;
    @Getter
    @Setter
    private GameState gameState;

    public void setInfo(String name, String description) {
        this.name = name;
        this.description = description;
        this.activePlayers = new ArrayList<>();
    }

    public abstract void onTextMessageReceived(TextMessageReceivedEvent event);
    public abstract void startGame();
    public abstract void stopGame();
    public abstract void playerJoin(User user);
    public abstract void playerLeave(User user);
    public abstract String getHelp();

    public void sendMessage(Chat chat, String message, Object... stringFormat) {
        chat.sendMessage(String.format(message, stringFormat), TelegramHook.getBot());
    }

    public void sendMessage(Chat chat, SendableTextMessage sendableTextMessage) {
        chat.sendMessage(sendableTextMessage, TelegramHook.getBot());
    }

    public void sendMessagePlayer(Chat chat, User user, String message, Object... stringFormat) {
        chat.sendMessage("[" + user.getUsername() + "] " + String.format(message, stringFormat), TelegramHook.getBot());
    }

    public void addPlayer(User user) {
        activePlayers.add(new PlayerScore(user, 0));
    }

    public void removePlayer(User user) {
        for (PlayerScore playerScore : new ArrayList<>(activePlayers)) {
            if (playerScore.getUsername().equalsIgnoreCase(user.getUsername())) {
                activePlayers.remove(playerScore);
                return;
            }
        }
    }

    public int getScore(User user) {
        for (PlayerScore playerScore : activePlayers) {
            if (playerScore.getUsername().equalsIgnoreCase(user.getUsername())) {
                return playerScore.getScore();
            }
        }
        return -1;
    }

    public PlayerScore getPlayerScore(User user) {
        for (PlayerScore playerScore : activePlayers) {
            if (playerScore.getUsername().equalsIgnoreCase(user.getUsername())) {
                return playerScore;
            }
        }
        return null;
    }

    public PlayerScore getPlayerScore(String username) {
        for (PlayerScore playerScore : activePlayers) {
            if (username.equalsIgnoreCase(playerScore.getUsername())) {
                return playerScore;
            }
        }
        return null;
    }

    public void setScore(User user, int score) {
        activePlayers.stream().filter(playerScore -> playerScore.getUsername().equalsIgnoreCase(user.getUsername())).forEach(playerScore -> {
            playerScore.setScore(score);
        });
    }

    public Boolean isPlayer(User user) {
        for (PlayerScore playerScore : activePlayers) {
            if (playerScore.getUsername().equalsIgnoreCase(user.getUsername())) {
                return true;
            }
        }
        return false;
    }

    public void sortScores() {
        Collections.sort(activePlayers);
    }

    public void sendPlayersMessage(String message) {
        for (PlayerScore playerScore : activePlayers) {
            TelegramBot.getChat(playerScore.getId()).sendMessage(message, TelegramHook.getBot());
        }
    }

    public void sendPlayersMessage(SendableTextMessage message) {
        for (PlayerScore playerScore : activePlayers) {
            TelegramBot.getChat(playerScore.getId()).sendMessage(message, TelegramHook.getBot());
        }
    }
}
