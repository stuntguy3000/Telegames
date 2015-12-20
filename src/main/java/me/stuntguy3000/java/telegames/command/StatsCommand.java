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

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("*Total Users:* ").append(userStatistics.getKnownPlayers().size()).append("\n");
        stringBuilder.append("*Active Lobbys:* ").append(getInstance().getLobbyHandler().getActiveLobbies().size()).append("\n");

        stringBuilder.append("\n*Games Play Count:*\n");

        for (GameStatistics gameStatistics : gameStats) {
            stringBuilder.append("* - ");
            stringBuilder.append(gameStatistics.getName());
            stringBuilder.append(": ");
            stringBuilder.append(gameStatistics.getCount());
            stringBuilder.append("*\n");
        }

        respond(chat, SendableTextMessage.builder().message("*Telegames Statistics:*\n" + stringBuilder.toString()).parseMode(ParseMode.MARKDOWN).build());
    }
}