package me.stuntguy3000.java.telegames.game;

import me.stuntguy3000.java.telegames.object.Game;
import me.stuntguy3000.java.telegames.object.Player;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;

// @author Luke Anderson | stuntguy3000
public class TicTacToe extends Game {

    public TicTacToe() {
        setInfo("TicTacToe", "TicTacToe, exactly as you love it.");
    }

    @Override
    public void onTextMessageReceived(TextMessageReceivedEvent event) {

    }

    @Override
    public boolean startGame() {
        return false;
    }

    @Override
    public void stopGame(boolean silent) {

    }

    @Override
    public boolean playerJoin(Player player, boolean silent) {
        return false;
    }

    @Override
    public void playerLeave(Player player, boolean silent) {

    }

    @Override
    public String getGameHelp() {
        return "The objective is simple, make your symbol in a row of 3 to win.\n\nMessage @Telegames +help while ingame for a list of commands.";
    }
}
    