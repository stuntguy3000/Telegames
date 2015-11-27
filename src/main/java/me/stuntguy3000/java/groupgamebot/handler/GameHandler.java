package me.stuntguy3000.java.groupgamebot.handler;

import me.stuntguy3000.java.groupgamebot.util.LogHandler;
import me.stuntguy3000.java.groupgamebot.util.PlayerData;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.HashMap;

// @author Luke Anderson | stuntguy3000
public class GameHandler {
    private HashMap<String, TelegramGame> gameList;
    // ChatID, GameName
    private HashMap<String, TelegramGame> activeGames;
    // UserID, GameName
    private HashMap<Integer, TelegramGame> userGames;

    public GameHandler() {
        gameList = new HashMap<>();
        activeGames = new HashMap<>();
        userGames = new HashMap<>();
    }

    public void registerGame(TelegramGame game) {
        gameList.put(game.getName().toLowerCase(), game);
        LogHandler.log("Registered game %s.", game.getName());
    }

    public String getGameList() {
        StringBuilder sb = new StringBuilder();
        sb.append("Game List:\n");
        for (TelegramGame game : gameList.values()) {
            sb.append(game.getName()).append(": ").append(game.getDescription()).append("\n");
        }

        return sb.toString();
    }

    public TelegramGame getGame(String gameName) {
        return gameList.get(gameName.toLowerCase());
    }

    public TelegramGame getGame(Chat chat) {
        if (activeGames.containsKey(chat.getId())) {
            return activeGames.get(chat.getId());
        } else {
            return null;
        }
    }

    public void startGame(TelegramGame game, Chat chat) {
        try {
            TelegramGame newGame = game.getClass().newInstance();
            newGame.setChat(chat);
            newGame.startGame();
            activeGames.put(chat.getId(), newGame);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void stopGame(Chat chat) {
        TelegramGame game = activeGames.remove(chat.getId());
        for (PlayerData playerData : game.getActivePlayers()) {
            userGames.remove(playerData.getId());
        }
        game.stopGame();
    }

    public void joinGame(Chat chat, User user) {
        TelegramGame game = getGame(chat);
        game.playerJoin(user);
        userGames.put(user.getId(), game);
    }

    public void leaveGame(Chat chat, User user) {
        TelegramGame game = getGame(chat);
        game.playerLeave(user);
        userGames.remove(user.getId());
    }

    public TelegramGame getGame(User sender) {
        return userGames.get(sender.getId());
    }
}
    