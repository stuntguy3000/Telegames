package me.stuntguy3000.java.telegames.object;

import lombok.Getter;
import lombok.Setter;

// @author Luke Anderson | stuntguy3000
public class LobbyOptions {
    @Getter
    @Setter
    private boolean discoverable = true;
    @Getter
    @Setter
    private boolean locked = false;
}
    