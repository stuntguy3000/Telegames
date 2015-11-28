package me.stuntguy3000.java.telegames.util;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

// @author Luke Anderson | stuntguy3000
public class BotSettings {
    @Getter
    private String telegramKey;
    @Getter
    private List<Integer> telegramAdmins;
    @Getter
    private Boolean autoUpdater;

    public BotSettings() {
        this.telegramKey = "";
        this.telegramAdmins = new ArrayList<>();
        this.autoUpdater = true;
    }
}
    