package me.stuntguy3000.java.telegames.object.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Represents the various configuration options for this bot
 *
 * @author stuntguy3000
 */
@Data
public class BotSettings {
    private Boolean autoUpdater;
    private Boolean devMode;
    private List<Integer> telegramAdmins;
    private String telegramKey;

    public BotSettings() {
        this.telegramKey = "";
        this.telegramAdmins = new ArrayList<>();
        this.autoUpdater = true;
        this.devMode = false;
    }
}
