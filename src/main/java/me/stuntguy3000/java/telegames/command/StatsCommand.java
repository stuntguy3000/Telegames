package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.object.Command;
import me.stuntguy3000.java.telegames.object.GameStatistics;
import me.stuntguy3000.java.telegames.object.config.UserStatistics;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

import java.util.List;

// @author Luke Anderson | stuntguy3000
public class StatsCommand extends Command {
    private List<GameStatistics> gameStats;

    public StatsCommand() {
        super(Telegames.getInstance(), "/stats View the bot's statistics", "stats", "statistics");
    }

    private int getCount(String gameName) {
        for (GameStatistics gameStatistics : gameStats) {
            if (gameStatistics.getName().equals(gameName)) {
                return gameStatistics.getCount();
            }
        }

        return 0;
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();

        UserStatistics userStatistics = getInstance().getConfigHandler().getUserStatistics();
        gameStats = userStatistics.sortGames();

        String mostPopularGame = (gameStats.size() > 0 ? gameStats.get(0).getName() : "No Game!");
        String leastPopularGame = (gameStats.size() > 0 ? gameStats.get(gameStats.size() - 1).getName() : "No Game!");
        int userCount = userStatistics.getKnownPlayers().size();

        respond(chat, SendableTextMessage.builder().message("*Telegames Statistics:*\n" +
                "*Most Popular Game:* " + mostPopularGame + " (Play Count: " + getCount(mostPopularGame) + ")\n" +
                "*Least Popular Game:* " + leastPopularGame + " (Play Count: " + getCount(leastPopularGame) + ")\n" +
                "*User Count:* " + userCount).parseMode(ParseMode.MARKDOWN).build());
    }
}