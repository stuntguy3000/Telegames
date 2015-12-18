package me.stuntguy3000.java.telegames.handler;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

// @author Luke Anderson | stuntguy3000
public class UpdaterAnnouncerHandler {

    String cachedVersion = "";
    String oldVersion = null;

    public void runUpdater() {
        try {
            String updateContents = "";

            try {
                URL apiUrl = new URL("https://raw.githubusercontent.com/stuntguy3000/Telegames/master/CHANGELOG.md");

                URLConnection apiConnection = apiUrl.openConnection();
                apiConnection.setUseCaches(false);
                apiConnection.setConnectTimeout(2000);
                apiConnection.setReadTimeout(2000);
                apiConnection.connect();

                BufferedReader in = new BufferedReader(new InputStreamReader(apiUrl.openStream()));
                String buffer;

                while ((buffer = in.readLine()) != null) {
                    updateContents += buffer + '\n';
                }

                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (updateContents.isEmpty()) {
                LogHandler.debug("An error occurred whilst retrieving the changelog from GitHub and the contents were empty.");
            } else {
                String newVersion;
                String changelog;

                //Removes first line
                updateContents = updateContents.substring(updateContents.indexOf('\n') + 1);
                //Gets contents of second line
                newVersion = updateContents.substring(updateContents.indexOf(' '), updateContents.indexOf('\n')).trim();
                updateContents = updateContents.substring(updateContents.indexOf('\n') + 1);

                //Gets the latest versions changelog
                changelog = updateContents.substring(updateContents.indexOf('*'), updateContents.indexOf("####")).trim();

                if (!newVersion.equals(cachedVersion)) {

                    if (oldVersion != null && !newVersion.equals(oldVersion)) {
                        //Runs if the oldVersion doesn't match the new version and isn't null.

                        TelegramHook.getBot().sendMessage(TelegramBot.getChat("@telegames"), SendableTextMessage.builder().disableWebPagePreview(true).parseMode(ParseMode.MARKDOWN).message("*New release!*\n" + newVersion + "\n*Features:*\n" + changelog).build());

                        LogHandler.debug(newVersion);
                        LogHandler.debug(changelog);
                    } else {
                        LogHandler.debug("No changes found!");
                    }

                    LogHandler.debug(changelog);

                    cachedVersion = oldVersion;
                    oldVersion = newVersion;

                    //Sleep and check again shortly.
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            LogHandler.log("Update Announcer exception occurred");
            Telegames.getInstance().sendToAdmins("Update Announcer exception occurred");
        }
    }
}
    