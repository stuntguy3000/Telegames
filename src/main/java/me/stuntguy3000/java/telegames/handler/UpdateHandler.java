package me.stuntguy3000.java.telegames.handler;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.Lobby;
import org.apache.commons.io.FileUtils;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by bo0tzz
 */
public class UpdateHandler implements Runnable {

    private final String fileName;
    private final Telegames instance;
    private final String projectName;

    public UpdateHandler(Telegames instance, String projectName, String fileName) {
        this.instance = instance;
        this.projectName = projectName;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        File build = new File("build");
        File jar = new File(fileName + ".new");
        int currentBuild = Telegames.BUILD;
        int newBuild = 0;

        while (true) {
            try {
                HttpResponse<String> response = Unirest.get("http://ci.zackpollard.pro/job/" + projectName + "/lastSuccessfulBuild/buildNumber").asString();

                if (response.getStatus() == 200) {
                    newBuild = Integer.parseInt(response.getBody());
                } else {
                    LogHandler.log("[ERROR] Updater status code: " + response.getStatus());
                    instance.sendToAdmins("[ERROR] Updater status code: " + response.getStatus() + "\n\nUpdater stopped.");
                    instance.stopUpdater();
                }
            } catch (UnirestException e) {
                e.printStackTrace();
                instance.stopUpdater();
            }

            if (newBuild > currentBuild) {
                LogHandler.log("Downloading build #" + newBuild);
                instance.sendToAdmins("Downloading build #" + newBuild);
                try {
                    FileUtils.writeStringToFile(build, String.valueOf(newBuild));
                    FileUtils.copyURLToFile(new URL("http://ci.zackpollard.pro/job/" + projectName + "/lastSuccessfulBuild/artifact/target/" + fileName + ".jar"), jar);
                    LogHandler.log("Build #" + newBuild + " downloaded. Restarting...");
                    instance.getConfigHandler().saveConfig("stats.json");
                    if (!Telegames.DEV_MODE) {
                        Telegames.getInstance().getUpdaterAnnouncerHandler().runUpdater();
                    }
                    instance.sendToAdmins("Build #" + newBuild + " downloaded. Restarting...");

                    for (Lobby lobby : Telegames.getInstance().getLobbyHandler().getActiveLobbies().values()) {
                        lobby.sendMessage(Telegames.getInstance().getLobbyHandler().createLobbyCreationMenu().message("*A new software update for the bot has been released.\n" + "Please re-create the lobby to continue.*").parseMode(ParseMode.MARKDOWN).build());
                    }
                } catch (IOException e) {
                    instance.sendToAdmins("Updater failed!");
                    e.printStackTrace();
                    break;
                }
                
                System.exit(0);
            }
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}