package me.stuntguy3000.java.telegames.object.exception;

import lombok.Getter;

// @author Luke Anderson | stuntguy3000
public class GameStartException extends Throwable {
    @Getter
    private final String reason;

    public GameStartException(String reason) {
        this.reason = reason;
    }
}
    