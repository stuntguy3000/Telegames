package me.stuntguy3000.java.telegames.object.config;

import lombok.Getter;
import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.LobbyMember;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// @author Luke Anderson | stuntguy3000
public class LobbyList {
    @Getter
    private HashMap<String, List<Integer>> activeLobbies;

    public LobbyList() {
        this.activeLobbies = new HashMap<>();
    }

    public void addLobby(String id, List<LobbyMember> people) {
        List<Integer> members = new ArrayList<>();

        for (LobbyMember lobbyMember : people) {
            members.add(lobbyMember.getUserID());
        }

        this.activeLobbies.put(id, members);

        Telegames.getInstance().getConfigHandler().saveConfig("lobby.json");
    }

    public void addPlayer(String id, int personID) {
        List<Integer> playerList = activeLobbies.get(id);
        playerList.add(personID);

        Telegames.getInstance().getConfigHandler().saveConfig("lobby.json");
    }

    public void removeLobby(String id) {
        this.activeLobbies.remove(id);

        Telegames.getInstance().getConfigHandler().saveConfig("lobby.json");
    }

    public void removePlayer(String id, int personID) {
        List<Integer> playerList = activeLobbies.get(id);

        int objectID = 0;
        for (int player : new ArrayList<>(playerList)) {
            if (player == personID) {
                playerList.remove(objectID);
                return;
            }
            objectID++;
        }

        Telegames.getInstance().getConfigHandler().saveConfig("lobby.json");
    }
}
    