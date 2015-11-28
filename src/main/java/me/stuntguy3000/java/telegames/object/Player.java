package me.stuntguy3000.java.telegames.object;

import lombok.Getter;
import lombok.Setter;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class Player {
    @Getter
    @Setter
    private int userID;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String firstname;
    @Getter
    @Setter
    private String lastname;
    @Getter
    @Setter
    private String fullname;

    public Player(User user) {
        setUsername(user.getUsername());
        setUserID(user.getId());
        setFirstname(user.getFirstName());
        setLastname(user.getLastName());
        setFirstname(user.getLastName());
    }
}
    