package me.stuntguy3000.java.telegames.game;

import me.stuntguy3000.java.telegames.handler.TelegramGame;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class TicTacToe extends TelegramGame {

    public TicTacToe() {
        setInfo("TicTacToe", "TicTacToe, exactly as you love it.");
    }

    @Override
    public void onTextMessageReceived(TextMessageReceivedEvent event) {

    }

    @Override
    public void startGame() {

    }

    @Override
    public void stopGame(boolean silent) {

    }

    @Override
    public void playerJoin(User user) {

    }

    @Override
    public void playerLeave(User user) {

    }

    @Override
    public String getHelp() {
        return "The objective is simple, make your symbol in a row of 3 to win.\n\nMessage @Telegames +help while ingame for a list of commands.";
    }
}
    