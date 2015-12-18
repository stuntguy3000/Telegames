package me.stuntguy3000.java.telegames.game;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.Game;
import me.stuntguy3000.java.telegames.object.Lobby;
import me.stuntguy3000.java.telegames.object.LobbyMember;
import me.stuntguy3000.java.telegames.util.GameState;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class Hangman extends Game {

    private GameState gameState;
    private int minPlayers = 2;
    private List<LobbyMember> activePlayers = new ArrayList<LobbyMember>();
    private LobbyMember chooser; //cringe
    private List<LobbyMember> choosers = new ArrayList<LobbyMember>();
    private int turns;

    public Hangman() {
        setGameInfo("Hangman", "The classic game of hangman. Try to guess the phrase before its too late!");
        setDevModeOnly(true);

        gameState = GameState.WAITING_FOR_PLAYERS;
    }

    @Override
    public void endGame() {

    }

    @Override
    public void onTextMessageReceived(TextMessageReceivedEvent event) {

    }

    @Override
    public boolean playerJoin(LobbyMember lobbyMember) {
        if (gameState == GameState.WAITING_FOR_PLAYERS) {
            activePlayers.add(lobbyMember);
            return true;
        }
        return false;
    }

    @Override
    public void playerLeave(String username, int userID) {
        for (LobbyMember member : activePlayers) {
            if (member.getUserID() == userID) {
                activePlayers.remove(userID);
                getGameLobby().sendMessage(username + " has left the game!");
                return;
            }
        }
    }

    @Override
    public boolean tryStartGame() {
        if (activePlayers.size() >= minPlayers) {
            gameState = GameState.INGAME;
            turns = activePlayers.size() * 3;
            doChooserTurn();

            return true;
        }
        return false;
    }

    public void doChooserTurn() {
        if (turns > 0) {
            chooser = activePlayers.get(turns % activePlayers.size());
            getGameLobby().sendMessage(chooser.getUsername() + " is selecting a word...");
            TelegramBot.getChat(chooser.getUserID()).sendMessage("Please send a phrase...", TelegramHook.getBot());
            turns--;
        }
        else {
            doGameEnd();
        }
    }

    public void goSelectorTurn() {

    }

    public void doGameEnd() {

    }
}