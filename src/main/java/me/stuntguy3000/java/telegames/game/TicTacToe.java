package me.stuntguy3000.java.telegames.game;

import me.stuntguy3000.java.telegames.handler.LogHandler;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// @author Luke Anderson | stuntguy3000
public class TicTacToe extends Game {
    private List<LobbyMember> activePlayers = new ArrayList<>();
    private TelegramEmoji[][] board = new TelegramEmoji[3][3];
    private LobbyMember cross;
    private LobbyMember currentPlayer;
    private GameState gameState;
    private int maxPlayers = 2;
    private int minPlayers = 2;
    private LobbyMember naught;
    private List<TelegramEmoji> numbers = new ArrayList<>();
    private LobbyMember winner;

    public TicTacToe() {
        setGameInfo("TicTacToe", "First player to line three in a row wins.");

        gameState = GameState.WAITING_FOR_PLAYERS;
    }

    // Loop through columns and see if any are winners.
    private boolean checkColumnsForWin() {
        for (int i = 0; i < 3; i++) {
            if (checkRowCol(board[0][i], board[1][i], board[2][i])) {
                return true;
            }
        }
        return false;
    }

    // Check the two diagonals to see if either is a win. Return true if either wins.
    private boolean checkDiagonalsForWin() {
        return ((checkRowCol(board[0][0], board[1][1], board[2][2])) || (checkRowCol(board[0][2], board[1][1], board[2][0])));
    }

    // Credit for methods below: http://www.coderslexicon.com/a-beginner-tic-tac-toe-class-for-java/
    //
    // Returns true if there is a win, false otherwise.
    // This calls our other win check functions to check the entire board.
    public boolean checkForWin() {
        return (checkRowsForWin() || checkColumnsForWin() || checkDiagonalsForWin());
    }

    // Check to see if all three values are the same (and not empty) indicating a win.
    private boolean checkRowCol(TelegramEmoji c1, TelegramEmoji c2, TelegramEmoji c3) {
        return (!numbers.contains(c1) && (c1 == c2) && (c2 == c3));
    }

    // Loop through rows and see if any are winners.
    private boolean checkRowsForWin() {
        for (int i = 0; i < 3; i++) {
            if (checkRowCol(board[i][0], board[i][1], board[i][2])) {
                return true;
            }
        }
        return false;
    }

    public SendableTextMessage.SendableTextMessageBuilder createKeyboard() {
        List<List<String>> buttonList = new ArrayList<>();
        List<String> row = new ArrayList<>();

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                row.add(board[r][c].getText());
            }

            buttonList.add(new ArrayList<>(row));
            row.clear();
        }

        return SendableTextMessage.builder().replyMarkup(new ReplyKeyboardMarkup(buttonList, false, false, false));
    }

    @Override
    public void endGame() {
        StringBuilder message = new StringBuilder("The game of TicTacToe has ended!");

        if (winner != null) {
            message.append("\n\n*The winner is ").append(StringUtil.markdownSafe(winner.getUsername())).append("*");
        }

        message.append("\n\n");

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                message.append(board[r][c].getText());
            }
            message.append("\n");
        }

        getGameLobby().sendMessage(SendableTextMessage.builder().message(message.toString()).parseMode(ParseMode.MARKDOWN).replyMarkup(new ReplyKeyboardHide()).build());
    }

    @Override
    public String getGameHelp() {
        return "First player to line three in a row wins.";
    }

    @Override
    public void onTextMessageReceived(TextMessageReceivedEvent event) {
        if (event.getChat().getType() == ChatType.PRIVATE) {
            User sender = event.getMessage().getSender();
            String message = event.getContent().getContent();
            LobbyMember lobbyMember = getGameLobby().getLobbyMember(sender.getUsername());

            if (currentPlayer.getUserID() == lobbyMember.getUserID()) {
                TelegramEmoji emoji = TelegramEmoji.getMatch(message);

                if (emoji != null) {
                    playTurn(currentPlayer, emoji);
                    return;
                }
            }

            getGameLobby().userChat(sender, message);
        }
    }

    @Override
    public boolean playerJoin(LobbyMember player) {
        if (gameState == GameState.WAITING_FOR_PLAYERS) {
            activePlayers.add(player);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void playerLeave(String username, int userID) {
        getGameLobby().stopGame();
    }

    @Override
    public boolean tryStartGame() {
        if (activePlayers.size() >= minPlayers) {
            startGame();
            return true;
        } else {
            getGameLobby().sendMessage("Not enough players to start!");
            return false;
        }
    }

    private void nextRound() {
        if (currentPlayer == null) {
            currentPlayer = activePlayers.get(0);
            cross = currentPlayer;
            naught = activePlayers.get(1);
        } else {
            if (naught.getUserID() == currentPlayer.getUserID()) {
                currentPlayer = cross;
            } else {
                currentPlayer = naught;
            }
        }

        getGameLobby().sendMessage(createKeyboard().message("It is your turn, " + currentPlayer.getUsername()).parseMode(ParseMode.MARKDOWN).build());
    }

    private void playTurn(LobbyMember currentPlayer, TelegramEmoji emoji) {
        if (emoji != null) {
            TelegramEmoji character = (currentPlayer.getUserID() == naught.getUserID() ? TelegramEmoji.RED_CIRCLE : TelegramEmoji.RED_CROSS);

            switch (emoji) {
                case NUMBER_BLOCK_ONE: {
                    board[0][0] = character;
                    break;
                }
                case NUMBER_BLOCK_TWO: {
                    board[0][1] = character;
                    break;
                }
                case NUMBER_BLOCK_THREE: {
                    board[0][2] = character;
                    break;
                }
                case NUMBER_BLOCK_FOUR: {
                    board[1][0] = character;
                    break;
                }
                case NUMBER_BLOCK_FIVE: {
                    board[1][1] = character;
                    break;
                }
                case NUMBER_BLOCK_SIX: {
                    board[1][2] = character;
                    break;
                }
                case NUMBER_BLOCK_SEVEN: {
                    board[2][0] = character;
                    break;
                }
                case NUMBER_BLOCK_EIGHT: {
                    board[2][1] = character;
                    break;
                }
                case NUMBER_BLOCK_NINE: {
                    board[2][2] = character;
                    break;
                }
                default: {
                    TelegramBot.getChat(currentPlayer.getUserID()).sendMessage(SendableTextMessage.builder().message("*Please play a valid option!*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                    return;
                }
            }

            if (!checkForWin()) {
                nextRound();
            } else {
                winner = currentPlayer;
                getGameLobby().stopGame();
                return;
            }

            LogHandler.debug(Arrays.deepToString(board));
        }
    }

    private void startGame() {
        gameState = GameState.INGAME;

        numbers.add(TelegramEmoji.NUMBER_BLOCK_ONE);
        numbers.add(TelegramEmoji.NUMBER_BLOCK_TWO);
        numbers.add(TelegramEmoji.NUMBER_BLOCK_THREE);
        numbers.add(TelegramEmoji.NUMBER_BLOCK_FOUR);
        numbers.add(TelegramEmoji.NUMBER_BLOCK_FIVE);
        numbers.add(TelegramEmoji.NUMBER_BLOCK_SIX);
        numbers.add(TelegramEmoji.NUMBER_BLOCK_SEVEN);
        numbers.add(TelegramEmoji.NUMBER_BLOCK_EIGHT);
        numbers.add(TelegramEmoji.NUMBER_BLOCK_NINE);

        board[0][0] = TelegramEmoji.NUMBER_BLOCK_ONE;
        board[0][1] = TelegramEmoji.NUMBER_BLOCK_TWO;
        board[0][2] = TelegramEmoji.NUMBER_BLOCK_THREE;
        board[1][0] = TelegramEmoji.NUMBER_BLOCK_FOUR;
        board[1][1] = TelegramEmoji.NUMBER_BLOCK_FIVE;
        board[1][2] = TelegramEmoji.NUMBER_BLOCK_SIX;
        board[2][0] = TelegramEmoji.NUMBER_BLOCK_SEVEN;
        board[2][1] = TelegramEmoji.NUMBER_BLOCK_EIGHT;
        board[2][2] = TelegramEmoji.NUMBER_BLOCK_NINE;

        Collections.shuffle(activePlayers);

        nextRound();
    }
}
    