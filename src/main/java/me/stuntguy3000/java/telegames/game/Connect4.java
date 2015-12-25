package me.stuntguy3000.java.telegames.game;

import me.stuntguy3000.java.telegames.object.game.Game;
import me.stuntguy3000.java.telegames.object.game.GameState;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;

// @author Luke Anderson | stuntguy3000
public class Connect4 extends Game {
    private GameState gameState;

    public Connect4() {
        setGameInfo("Connect4", "Description");
        setDevModeOnly(true);

        gameState = GameState.WAITING_FOR_PLAYERS;
    }

    @Override
    public void onTextMessageReceived(TextMessageReceivedEvent event) {

    }

    @Override
    public void startGame() {

    }
}
    