package me.stuntguy3000.java.telegames.object;

import lombok.Getter;
import lombok.Setter;

// @author Luke Anderson | stuntguy3000
public class PlayerData implements Comparable {
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private int id;
    @Getter
    @Setter
    private int score;

    public PlayerData(Player player, int score) {
        this.username = player.getUsername();
        this.id = player.getUserID();
        this.score = score;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof PlayerData) {
            PlayerData playerData = (PlayerData) o;

            if (playerData.getScore() == getScore()) {
                return 0;
            }

            if (playerData.getScore() > getScore()) {
                return 1;
            }

            if (playerData.getScore() < getScore()) {
                return -1;
            }
        } else {
            throw new IllegalArgumentException("Comparable object is not PlayerData");
        }

        return 0;
    }
}
    