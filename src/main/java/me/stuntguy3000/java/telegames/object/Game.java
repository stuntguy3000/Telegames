package me.stuntguy3000.java.telegames.object;

import lombok.Getter;
import lombok.Setter;
import me.stuntguy3000.java.telegames.object.exception.GameStartException;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;

public abstract class Game {
    @Getter
    @Setter
    private boolean devModeOnly = false;
    @Getter
    private String gameDescription;
    @Getter
    @Setter
    private Lobby gameLobby;
    @Getter
    private String gameName;
    @Getter
    @Setter
    private boolean restrictedGame = false;

    public abstract void endGame();

    public String getGameHelp() {
        return "No help available";
    }

    public void onSecond() {
        // Do nothing
    }

    public abstract void onTextMessageReceived(TextMessageReceivedEvent event);

    public abstract boolean playerJoin(LobbyMember lobbyMember);

    public abstract void playerLeave(String username, int userID);

    public void setGameInfo(String name, String description) {
        this.gameName = name;
        this.gameDescription = description;
    }

    public abstract void tryStartGame() throws GameStartException;
}
