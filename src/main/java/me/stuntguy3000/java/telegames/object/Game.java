package me.stuntguy3000.java.telegames.object;

import lombok.Getter;
import lombok.Setter;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;

public abstract class Game {
    @Getter
    private String gameName;
    @Getter
    private String gameDescription;
    @Getter
    @Setter
    private Lobby gameLobby;

    public void setGameInfo(String name, String description) {
        this.gameName = name;
        this.gameDescription = description;
    }

    public abstract void onTextMessageReceived(TextMessageReceivedEvent event);

    public abstract void endGame();

    public abstract boolean tryStartGame();

    public abstract boolean playerJoin(LobbyMember lobbyMember);

    public abstract void playerLeave(String username, int userID);

    public abstract String getGameHelp();
}
