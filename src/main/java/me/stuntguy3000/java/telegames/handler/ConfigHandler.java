package me.stuntguy3000.java.telegames.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import lombok.Data;
import me.stuntguy3000.java.telegames.object.config.BotSettings;

/**
 * Handles configuration loading and saving using GSON
 *
 * @author stuntguy3000
 */
@Data
public class ConfigHandler {
    /*
        Standard bot configuration options
     */
    private BotSettings botSettings = new BotSettings();

    /**
     * Initialize the class by loading all related configuration files
     */
    public ConfigHandler() {
        loadFile("config.json");
    }

    /**
     * Load a configuration file by file name (excluding file type)
     *
     * @param fileName String the name of the config file to be loaded (excluding file type)
     */
    private void loadFile(String fileName) {
        Gson gson = new Gson();
        File configFile = new File(fileName);

        if (configFile.exists()) {
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(configFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }

            switch (fileName.split(".json")[0].toLowerCase()) {
                case "config": {
                    botSettings = gson.fromJson(br, BotSettings.class);
                    return;
                }
            }
        } else {
            saveConfig(fileName);
        }
    }

    /**
     * Save a configuration file by file name (excluding file type)
     * <p>If the file cannot be found, it will be created with default options.</p>
     *
     * @param fileName String the name of the config file to be saved (excluding file type)
     */
    private void saveConfig(String fileName) {
        File configFile = new File(fileName);
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        Gson gson = builder.create();
        String json = null;

        switch (fileName.split(".json")[0].toLowerCase()) {
            case "config": {
                json = gson.toJson(botSettings);
                break;
            }
        }

        FileOutputStream outputStream;

        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            outputStream = new FileOutputStream(configFile);
            assert json != null;
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogHandler.log("The config could not be saved as the file couldn't be found on the storage device. Please check the directories read/write permissions and contact the developer!");
        } catch (IOException e) {
            e.printStackTrace();
            LogHandler.log("The config could not be written to as an sendError occurred. Please check the directories read/write permissions and contact the developer!");
        } catch (NullPointerException e) {
            e.printStackTrace();
            LogHandler.log("Invalid Config Specified! Please check the directories read/write permissions and contact the developer!");
        }
    }
}

    