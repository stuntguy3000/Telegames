package me.stuntguy3000.java.telegames.object.config;

import lombok.Getter;
import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.user.TelegramUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// @author Luke Anderson | stuntguy3000
public class LobbyList {
    @Getter
    private HashMap<String, List<Long>> activeLobbies;

    public LobbyList() {
        this.activeLobbies = new HashMap<>();
    }

    public void addLobby(String id, List<TelegramUser> people) {
        List<Long> members = new ArrayList<>();

        for (TelegramUser telegramUser : people) {
            members.add(telegramUser.getUserID());
        }

        this.activeLobbies.put(id, members);

        Telegames.getInstance().getConfigHandler().saveConfig("lobby.json");
    }

    public void addPlayer(String id, long personID) {
        List<Long> playerList = activeLobbies.get(id);
        playerList.add(personID);

        Telegames.getInstance().getConfigHandler().saveConfig("lobby.json");
    }

    public void removeLobby(String id) {
        this.activeLobbies.remove(id);

        Telegames.getInstance().getConfigHandler().saveConfig("lobby.json");
    }

    public void removePlayer(String id, int personID) {
        List<Long> playerList = activeLobbies.get(id);

        int objectID = 0;
        for (long player : new ArrayList<>(playerList)) {
            if (player == personID) {
                playerList.remove(objectID);
                return;
            }
            objectID++;
        }

        Telegames.getInstance().getConfigHandler().saveConfig("lobby.json");
    }
}
    