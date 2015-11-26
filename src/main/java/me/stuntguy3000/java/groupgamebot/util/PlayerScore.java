package me.stuntguy3000.java.groupgamebot.util;

import lombok.Getter;
import lombok.Setter;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class PlayerScore implements Comparable {
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private Integer id;
    @Getter
    @Setter
    private int score;

    public PlayerScore(User user, int score) {
        this.username = user.getUsername();
        this.id = user.getId();
        this.score = score;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof PlayerScore) {
            PlayerScore playerScore = (PlayerScore) o;

            if (playerScore.getScore() == getScore()) {
                return 0;
            }

            if (playerScore.getScore() > getScore()) {
                return 1;
            }

            if (playerScore.getScore() < getScore()) {
                return -1;
            }
        } else {
            throw new IllegalArgumentException("Comparable object is not PlayerScore");
        }

        return 0;
    }
}
    