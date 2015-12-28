package me.stuntguy3000.java.telegames.object.user;

import lombok.Getter;
import lombok.Setter;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class TelegramUser implements Comparable {
    @Getter
    private String firstName;
    @Getter
    private String fullName;
    @Getter
    @Setter
    private int gameScore = 0;
    @Getter
    private String lastName;
    @Getter
    private int userID;
    @Getter
    private String username;

    public TelegramUser(User user) {
        this.username = user.getUsername();
        this.userID = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.fullName = user.getFullName();
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof TelegramUser) {
            TelegramUser telegramUser = (TelegramUser) o;

            if (telegramUser.getGameScore() == getGameScore()) {
                return 0;
            }

            if (telegramUser.getGameScore() > getGameScore()) {
                return 1;
            }

            if (telegramUser.getGameScore() < getGameScore()) {
                return -1;
            }
        } else {
            throw new IllegalArgumentException("Comparable object is not TelegramUser");
        }

        return 0;
    }

    public Chat getChat() {
        return TelegramBot.getChat(getUserID());
    }
}
    