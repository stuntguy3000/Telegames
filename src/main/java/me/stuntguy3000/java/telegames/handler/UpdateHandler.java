/*
 * MIT License
 *
 * Copyright (c) 2016 Luke Anderson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.stuntguy3000.java.telegames.handler;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import me.stuntguy3000.java.telegames.Telegames;

/**
 * Automatically update this plugin
 *
 * @author bo0tzz
 * @author stuntguy3000
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

        int currentBuild = instance.getCurrentBuild();
        int newBuild;

        while (true) {
            try {
                HttpResponse<String> response = Unirest.get("http://ci.zackpollard.pro/job/" + projectName + "/lastSuccessfulBuild/buildNumber").asString();

                if (response.getStatus() == 200) {
                    newBuild = Integer.parseInt(response.getBody());
                } else {
                    LogHandler.log("[ERROR] Updater status code: " + response.getStatus());
                    instance.sendToAdmins("[ERROR] Updater status code: " + response.getStatus() + "\n\nUpdater stopped.");
                    return;
                }
            } catch (UnirestException e) {
                e.printStackTrace();
                return;
            }

            if (newBuild > currentBuild) {
                LogHandler.log("Downloading build #" + newBuild);
                instance.sendToAdmins("Downloading build #" + newBuild);
                try {
                    FileUtils.writeStringToFile(build, String.valueOf(newBuild));
                    FileUtils.copyURLToFile(new URL("http://ci.zackpollard.pro/job/" + projectName + "/lastSuccessfulBuild/artifact/target/" + fileName + ".jar"), jar);
                    LogHandler.log("Build #" + newBuild + " downloaded. Restarting...");
                    instance.getConfigHandler().saveConfig("config");
                    if (!instance.isDevelopmentMode()) {
                        Telegames.getInstance().getUpdaterAnnouncerHandler().runUpdater();
                    }
                    instance.sendToAdmins("Build #" + newBuild + " downloaded. Restarting...");
                } catch (IOException e) {
                    instance.sendToAdmins("Updater failed!");
                    e.printStackTrace();
                    return;
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