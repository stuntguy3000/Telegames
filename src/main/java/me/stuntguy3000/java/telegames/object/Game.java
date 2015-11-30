package me.stuntguy3000.java.telegames.object;

import lombok.Getter;
import lombok.Setter;
import me.stuntguy3000.java.telegames.util.GameState;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;

public abstract class Game {
    @Getter
    private String gameName;
    @Getter
    private String gameDescription;
    @Getter
    @Setter
    private Lobby gameLobby;
    @Getter
    private GameState gameState;

    public void setGameInfo(String name, String description) {
        this.gameName = name;
        this.gameDescription = description;
        this.gameState = GameState.WAITING_FOR_PLAYERS;
    }

    public abstract void onTextMessageReceived(TextMessageReceivedEvent event);

    public abstract void endGame();

    public abstract boolean tryStartGame();

    public abstract boolean playerJoin(LobbyMember lobbyMember);

    public abstract void playerLeave(String username);

    public abstract String getGameHelp();
}
