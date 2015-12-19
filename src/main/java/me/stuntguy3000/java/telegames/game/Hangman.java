package me.stuntguy3000.java.telegames.game;

import me.stuntguy3000.java.telegames.handler.LogHandler;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.Game;
import me.stuntguy3000.java.telegames.object.LobbyMember;
import me.stuntguy3000.java.telegames.object.StringUtil;
import me.stuntguy3000.java.telegames.util.GameState;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardHide;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.ArrayList;
import java.util.List;

public class Hangman extends Game {

    private List<LobbyMember> activePlayers = new ArrayList<>();
    private List<Character> censoredWord = new ArrayList<>();
    private GameState gameState;
    private List<Character> guesses = new ArrayList<>();
    private int guessesLeft = 9;
    private int minPlayers = 2;
    private int roundsLeft;
    private LobbyMember selector; //cringe
    private String word;

    public Hangman() {
        setGameInfo("Hangman", "The classic game of hangman. Try to guess the phrase before its too late!");
        setDevModeOnly(true);

        gameState = GameState.WAITING_FOR_PLAYERS;
    }

    @Override
    public void endGame() {
        SendableTextMessage.SendableTextMessageBuilder messageBuilder = SendableTextMessage.builder().message("The game of Hangman has ended!").replyMarkup(ReplyKeyboardHide.builder().build());

        getGameLobby().sendMessage(messageBuilder.build());
    }

    @Override
    public void onTextMessageReceived(TextMessageReceivedEvent event) {
        if (event.getChat().getType() == ChatType.PRIVATE) {
            User sender = event.getMessage().getSender();
            String message = event.getContent().getContent();
            LobbyMember lobbyMember = getGameLobby().getLobbyMember(sender.getUsername());

            if (isPlayer(lobbyMember)) {
                if (word != null && message.length() == 1 && sender.getId() != selector.getUserID()) {
                    LogHandler.debug("1");
                    if (isAlphaCharactersOnly(message)) {
                        char letter = message.toCharArray()[0];
                        getGameLobby().sendMessage(SendableTextMessage.builder().message("*" + StringUtil.markdownSafe(sender.getUsername()) + " guessed " + letter + ".*").parseMode(ParseMode.MARKDOWN).build());
                        boolean guessedCorrectly = guessLetter(letter);

                        if (wordCompleted()) {
                            getGameLobby().sendMessage(SendableTextMessage.builder().message("*The word was guessed correctly!*\n\n*Word: " + word + "*").parseMode(ParseMode.MARKDOWN).build());
                            nextRound();
                        } else {
                            if (guessedCorrectly) {
                                getGameLobby().sendMessage(SendableTextMessage.builder().message("*Correct guess! Remaining: " + guessesLeft + "*\n\n*" + getCensoredWord() + "*").parseMode(ParseMode.MARKDOWN).build());
                            } else {
                                --guessesLeft;
                                if (guessesLeft > 0) {
                                    getGameLobby().sendMessage(SendableTextMessage.builder().message("*Incorrect guess! Remaining: " + guessesLeft + "*\n\n*" + getCensoredWord() + "*").parseMode(ParseMode.MARKDOWN).build());
                                } else {
                                    getGameLobby().sendMessage(SendableTextMessage.builder().message("*Out of guesses!*\n\n*Word: " + word + "*").parseMode(ParseMode.MARKDOWN).build());
                                    nextRound();
                                    return;
                                }
                            }
                        }
                    } else {
                        TelegramBot.getChat(sender.getId()).sendMessage("Only Alpha characters are valid!", TelegramHook.getBot());
                    }
                    return;
                } else {
                    if (sender.getId() == selector.getUserID() && word == null) {
                        LogHandler.debug("2");
                        if (isAlphaCharactersOnly(message)) {
                            LogHandler.debug("3");
                            word = message;

                            for (int i = 0; i < word.length(); i++) {
                                censoredWord.add(i, '-');
                            }

                            getGameLobby().sendMessage(SendableTextMessage.builder().message("*The word has been chosen!\n\n" + getCensoredWord() + "*").parseMode(ParseMode.MARKDOWN).build());
                        } else {
                            TelegramBot.getChat(selector.getUserID()).sendMessage("Only Alpha characters are valid!", TelegramHook.getBot());
                        }
                        return;
                    }
                }

                getGameLobby().userChat(sender, message);
            }
        }
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
            roundsLeft = activePlayers.size() * 3;
            nextRound();
            return true;
        }
        return false;
    }

    private String getCensoredWord() {
        StringBuilder word = new StringBuilder();

        for (char letter : censoredWord) {
            word.append(letter);
        }

        return word.toString();
    }

    private boolean guessLetter(char letter) {
        boolean guessed = false;
        int index = 0;

        letter = Character.toLowerCase(letter);
        for (char wordCharacter : word.toCharArray()) {
            if (wordCharacter == letter) {
                censoredWord.add(index, letter);
                guessed = true;
            }
            index++;
        }

        return guessed;
    }

    private boolean isAlphaCharactersOnly(String message) {
        char[] chars = message.toCharArray();

        for (char c : chars) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }

    private boolean isPlayer(LobbyMember lobbyMember) {
        for (LobbyMember player : activePlayers) {
            if (lobbyMember.getUserID() == player.getUserID()) {
                return true;
            }
        }

        return false;
    }

    public void nextRound() {
        if (roundsLeft > 0) {
            selector = activePlayers.get(roundsLeft % activePlayers.size());
            word = null;
            getGameLobby().sendMessage(selector.getUsername() + " is selecting a word...");
            TelegramBot.getChat(selector.getUserID()).sendMessage("Please send a phrase...", TelegramHook.getBot());
            roundsLeft--;
        } else {
            getGameLobby().stopGame();
        }
    }

    public boolean wordCompleted() {
        for (char wordCharacter : censoredWord) {
            if (wordCharacter == '_') {
                return true;
            }
        }
        return true;
    }
}