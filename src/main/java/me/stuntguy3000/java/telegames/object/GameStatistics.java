package me.stuntguy3000.java.telegames.object;

import lombok.Getter;

public class GameStatistics implements Comparable {
    @Getter
    int count;
    @Getter
    String name;

    public GameStatistics(String name, int count) {
        this.name = name;
        this.count = count;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof GameStatistics) {
            GameStatistics gameStatistics = (GameStatistics) o;

            if (gameStatistics.getCount() == count) {
                return 0;
            }

            if (gameStatistics.getCount() > count) {
                return 1;
            }

            if (gameStatistics.getCount() < count) {
                return -1;
            }
        } else {
            throw new IllegalArgumentException("Comparable object is not GameStatistics");
        }

        return 0;
    }
}