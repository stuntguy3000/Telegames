package me.stuntguy3000.java.telegames.game;

import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.Game;
import me.stuntguy3000.java.telegames.object.LobbyMember;
import me.stuntguy3000.java.telegames.object.StringUtil;
import me.stuntguy3000.java.telegames.util.GameState;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardHide;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardMarkup;
import pro.zackpollard.telegrambot.api.user.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hangman extends Game {

    private List<LobbyMember> activePlayers = new ArrayList<>();
    private Character censoredChar = '-';
    private List<Character> censoredWord = new ArrayList<>();
    private GameState gameState;
    private List<Character> guesses = new ArrayList<>();
    private int guessesLeft = 9;
    private int minPlayers = 2;
    private List<String> predefinedWords = new ArrayList<>();
    private int roundsLeft;
    private LobbyMember selector; //cringe
    private String word;

    public Hangman() {
        setGameInfo("Hangman", "The classic game of hangman. Try to guess the phrase before its too late!");

        gameState = GameState.WAITING_FOR_PLAYERS;
    }

    private SendableTextMessage.SendableTextMessageBuilder createChooserKeyboard() {
        List<List<String>> buttonList = new ArrayList<>();
        buttonList.add(new ArrayList<>(Collections.singletonList(TelegramEmoji.OPEN_BOOK.getText() + " Choose a random word")));

        return SendableTextMessage.builder().replyMarkup(new ReplyKeyboardMarkup(buttonList, true, true, false));
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
                    if (isAlphaCharactersOnly(message)) {
                        char letter = message.toCharArray()[0];
                        getGameLobby().sendMessage(SendableTextMessage.builder().message("*" + StringUtil.markdownSafe(sender.getUsername()) + " guessed " + letter + ".*").parseMode(ParseMode.MARKDOWN).build());
                        boolean guessedCorrectly = guessLetter(letter);

                        if (wordCompleted()) {
                            getGameLobby().sendMessage(SendableTextMessage.builder().message(TelegramEmoji.PARTY_POPPER.getText() + " *The word was guessed correctly!\n\nThe word: " + word + "*").parseMode(ParseMode.MARKDOWN).build());
                            nextRound();
                        } else {
                            if (guessedCorrectly) {
                                getGameLobby().sendMessage(SendableTextMessage.builder().message(TelegramEmoji.GREEN_BOX_TICK.getText() + " *Correct guess!\nRemaining: " + guessesLeft + "\n\nThe word: " + getCensoredWord() + "*").parseMode(ParseMode.MARKDOWN).build());
                            } else {
                                --guessesLeft;
                                if (guessesLeft > 0) {
                                    guesses.add(letter);
                                    getGameLobby().sendMessage(SendableTextMessage.builder().message(TelegramEmoji.RED_CROSS.getText() + " *Incorrect guess!\nRemaining: " + guessesLeft + "\n\n" +
                                            "The word: " + getCensoredWord() + "\nGuessed letters: " + getGuessedLetters() + "*").parseMode(ParseMode.MARKDOWN).build());
                                } else {
                                    getGameLobby().sendMessage(SendableTextMessage.builder().message(TelegramEmoji.RED_CROSS.getText() + " *Out of guesses!\n\nThe Word: " + word + "*").parseMode(ParseMode.MARKDOWN).build());
                                    nextRound();
                                    return;
                                }
                            }
                        }
                    } else {
                        TelegramBot.getChat(sender.getId()).sendMessage(TelegramEmoji.RED_CROSS.getText() + " Only Alpha characters are valid!", TelegramHook.getBot());
                    }
                    return;
                } else {
                    if (sender.getId() == selector.getUserID() && word == null) {
                        if (message.equals(TelegramEmoji.OPEN_BOOK.getText() + " Choose a random word")) {
                            message = predefinedWords.get(0);
                            TelegramBot.getChat(selector.getUserID()).sendMessage(TelegramEmoji.GREEN_BOX_TICK.getText() + " Chosen random word: " + message, TelegramHook.getBot());
                        }

                        if (isAlphaCharactersOnly(message)) {
                            if (message.length() >= 3) {
                                word = message.toLowerCase();

                                for (int i = 0; i < word.length(); i++) {
                                    censoredWord.add(i, censoredChar);
                                }

                                getGameLobby().sendMessage(SendableTextMessage.builder().message(TelegramEmoji.BOOK.getText() + " *The word has been chosen!\n\nTo guess, send your guess as a message!\nYou can only guess one letter at a time.\n\nThe word: " + getCensoredWord() + "*").parseMode(ParseMode.MARKDOWN).replyMarkup(new ReplyKeyboardHide()).build());
                            } else {
                                TelegramBot.getChat(selector.getUserID()).sendMessage(TelegramEmoji.RED_CROSS.getText() + " Words have to be longer than three characters!", TelegramHook.getBot());
                            }
                        } else {
                            TelegramBot.getChat(selector.getUserID()).sendMessage(TelegramEmoji.RED_CROSS.getText() + " Only Alpha characters are valid!", TelegramHook.getBot());
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
        for (LobbyMember member : new ArrayList<>(activePlayers)) {
            if (member.getUserID() == userID) {
                activePlayers.remove(userID);
                return;
            }
        }

        if (activePlayers.size() < minPlayers) {
            getGameLobby().stopGame();
        }
    }

    @Override
    public boolean tryStartGame() {
        if (activePlayers.size() >= minPlayers) {
            gameState = GameState.INGAME;
            roundsLeft = activePlayers.size() * 3;

            loadWords();

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

    private String getGuessedLetters() {
        StringBuilder stringBuilder = new StringBuilder();

        for (char guess : guesses) {
            stringBuilder.append(guess).append(" ");
        }

        return stringBuilder.toString();
    }

    private boolean guessLetter(char letter) {
        boolean guessed = false;
        int index = 0;

        letter = Character.toLowerCase(letter);
        for (char wordCharacter : word.toCharArray()) {
            if (wordCharacter == letter) {
                censoredWord.remove(index);
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

    private void loadWords() {
        InputStream in = getClass().getResourceAsStream("/hangmanwords.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty() && line.matches("[A-z][A-z]+")) {
                    predefinedWords.add(line.toLowerCase().replace(" ", ""));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.shuffle(predefinedWords);
    }

    public void nextRound() {
        if (roundsLeft > 0) {
            selector = activePlayers.get(roundsLeft % activePlayers.size());
            word = null;
            censoredWord.clear();
            guesses.clear();
            guessesLeft = 9;
            getGameLobby().sendMessage(StringUtil.markdownSafe(selector.getUsername()) + " is selecting a word...");
            TelegramBot.getChat(selector.getUserID()).sendMessage(createChooserKeyboard().message("Please choose word...").build(), TelegramHook.getBot());
            roundsLeft--;
        } else {
            getGameLobby().stopGame();
        }
    }

    public boolean wordCompleted() {
        for (char wordCharacter : censoredWord) {
            if (wordCharacter == censoredChar) {
                return false;
            }
        }
        return true;
    }
}