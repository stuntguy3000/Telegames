package me.stuntguy3000.java.telegames.handler;

import lombok.Getter;
import lombok.Setter;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.util.GameState;
import me.stuntguy3000.java.telegames.util.PlayerData;
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
    private List<PlayerData> activePlayers;
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

    public abstract void stopGame(boolean silent);
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
        activePlayers.add(new PlayerData(user, 0));
    }

    public void removePlayer(User user) {
        for (PlayerData playerData : new ArrayList<>(activePlayers)) {
            if (playerData.getUsername().equalsIgnoreCase(user.getUsername())) {
                activePlayers.remove(playerData);
                return;
            }
        }
    }

    public int getScore(User user) {
        for (PlayerData playerData : activePlayers) {
            if (playerData.getUsername().equalsIgnoreCase(user.getUsername())) {
                return playerData.getScore();
            }
        }
        return -1;
    }

    public PlayerData getPlayerData(User user) {
        for (PlayerData playerData : activePlayers) {
            if (playerData.getUsername().equalsIgnoreCase(user.getUsername())) {
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

    public void setScore(User user, int score) {
        activePlayers.stream().filter(playerData -> playerData.getUsername().equalsIgnoreCase(user.getUsername())).forEach(playerData -> {
            playerData.setScore(score);
        });
    }

    public Boolean isPlayer(User user) {
        for (PlayerData playerData : activePlayers) {
            if (playerData.getUsername().equalsIgnoreCase(user.getUsername())) {
                return true;
            }
        }
        return false;
    }

    public void sortScores() {
        Collections.sort(activePlayers);
    }

    public void sendPlayersMessage(String message) {
        for (PlayerData playerData : activePlayers) {
            TelegramBot.getChat(playerData.getId()).sendMessage(message, TelegramHook.getBot());
        }
    }

    public void sendPlayersMessage(SendableTextMessage message) {
        for (PlayerData playerData : activePlayers) {
            TelegramBot.getChat(playerData.getId()).sendMessage(message, TelegramHook.getBot());
        }
    }
}
