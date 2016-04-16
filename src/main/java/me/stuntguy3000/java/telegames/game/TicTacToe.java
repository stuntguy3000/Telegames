package me.stuntguy3000.java.telegames.game;

import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.game.Game;
import me.stuntguy3000.java.telegames.object.game.GameState;
import me.stuntguy3000.java.telegames.object.user.TelegramUser;
import me.stuntguy3000.java.telegames.util.string.Emoji;
import me.stuntguy3000.java.telegames.util.string.Lang;
import me.stuntguy3000.java.telegames.util.string.StringUtil;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardHide;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardMarkup;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// @author Luke Anderson | stuntguy3000
public class TicTacToe extends Game {
    private Emoji[][] board = new Emoji[3][3];
    private TelegramUser cross;
    private TelegramUser currentPlayer;
    private TelegramUser naught;
    private List<Emoji> numbers = new ArrayList<>();
    private TelegramUser winner;

    public TicTacToe() {
        setGameInfo(Lang.GAME_TICTACTOE_NAME, Lang.GAME_TICTACTOE_DESCRIPTION);
        setMinPlayers(2);
        setMaxPlayers(2);
        setGameState(GameState.WAITING_FOR_PLAYERS);
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
    private boolean checkRowCol(Emoji c1, Emoji c2, Emoji c3) {
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

    private SendableTextMessage.SendableTextMessageBuilder createKeyboard() {
        ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder replyKeyboardMarkupBuilder = ReplyKeyboardMarkup.builder();
        List<String> row = new ArrayList<>();

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                row.add(board[r][c].getText());
            }

            replyKeyboardMarkupBuilder.addRow(new ArrayList<>(row));
            row.clear();
        }

        replyKeyboardMarkupBuilder.resize(true);
        replyKeyboardMarkupBuilder.oneTime(true);
        replyKeyboardMarkupBuilder.selective(false);

        return SendableTextMessage.builder().replyMarkup(replyKeyboardMarkupBuilder.build());
    }

    @Override
    public void endGame() {
        StringBuilder message = new StringBuilder(Lang.GAME_TICTACTOE_END);

        if (winner != null) {
            message.append("\n\n*").append(String.format(Lang.GAME_GENERAL_WINNER, StringUtil.markdownSafe(winner.getUsername())));
        } else {
            message.append("\n\n").append(Lang.GAME_GENERAL_DRAW);
        }

        message.append("\n\n");

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                message.append(board[r][c].getText());
            }
            message.append("\n");
        }

        getGameLobby().sendMessage(SendableTextMessage.builder().message(message.toString()).parseMode(ParseMode.MARKDOWN).replyMarkup(ReplyKeyboardHide.builder().build()).build());
    }

    @Override
    public String getGameHelp() {
        return Lang.GAME_TICTACTOE_DESCRIPTION;
    }

    @Override
    public void onTextMessageReceived(TextMessageReceivedEvent event) {
        if (event.getChat().getType() == ChatType.PRIVATE) {
            User sender = event.getMessage().getSender();
            String message = event.getContent().getContent();
            TelegramUser telegramUser = getGameLobby().getTelegramUser(sender.getUsername());

            Emoji emoji = Emoji.getMatch(message);

            if (emoji != null) {
                if (currentPlayer.getUserID() == telegramUser.getUserID()) {
                    playTurn(currentPlayer, emoji);
                    return;
                } else {
                    if (Emoji.getNumber(emoji) > -1) {
                        return;
                    }
                }
            }

            getGameLobby().userChat(telegramUser, message);
        }
    }

    @Override
    public void startGame() {
        setGameState(GameState.INGAME);

        numbers.add(Emoji.NUMBER_BLOCK_ONE);
        numbers.add(Emoji.NUMBER_BLOCK_TWO);
        numbers.add(Emoji.NUMBER_BLOCK_THREE);
        numbers.add(Emoji.NUMBER_BLOCK_FOUR);
        numbers.add(Emoji.NUMBER_BLOCK_FIVE);
        numbers.add(Emoji.NUMBER_BLOCK_SIX);
        numbers.add(Emoji.NUMBER_BLOCK_SEVEN);
        numbers.add(Emoji.NUMBER_BLOCK_EIGHT);
        numbers.add(Emoji.NUMBER_BLOCK_NINE);

        board[0][0] = Emoji.NUMBER_BLOCK_ONE;
        board[0][1] = Emoji.NUMBER_BLOCK_TWO;
        board[0][2] = Emoji.NUMBER_BLOCK_THREE;
        board[1][0] = Emoji.NUMBER_BLOCK_FOUR;
        board[1][1] = Emoji.NUMBER_BLOCK_FIVE;
        board[1][2] = Emoji.NUMBER_BLOCK_SIX;
        board[2][0] = Emoji.NUMBER_BLOCK_SEVEN;
        board[2][1] = Emoji.NUMBER_BLOCK_EIGHT;
        board[2][2] = Emoji.NUMBER_BLOCK_NINE;

        Collections.shuffle(getActivePlayers());

        nextRound();
    }

    private void nextRound() {
        if (currentPlayer == null) {
            currentPlayer = getActivePlayers().get(0);
            cross = currentPlayer;
            naught = getActivePlayers().get(1);
        } else {
            if (naught.getUserID() == currentPlayer.getUserID()) {
                currentPlayer = cross;
            } else {
                currentPlayer = naught;
            }
        }

        getGameLobby().sendMessage(createKeyboard().message(String.format(Lang.GAME_GENERAL_NEXT_TURN, StringUtil.markdownSafe(currentPlayer.getUsername()))).parseMode(ParseMode.MARKDOWN).build());
    }

    private void playTurn(TelegramUser currentPlayer, Emoji emoji) {
        if (emoji != null) {
            Emoji character = (currentPlayer.getUserID() == naught.getUserID() ? Emoji.RED_CIRCLE : Emoji.RED_CROSS);

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
                    TelegramBot.getChat(currentPlayer.getUserID()).sendMessage(SendableTextMessage.builder().message(Lang.ERROR_INVALID_SELECTION).parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                    return;
                }
            }

            if (!checkForWin()) {
                boolean containsNumberSquares = false;

                for (int r = 0; r < 3; r++) {
                    for (int c = 0; c < 3; c++) {
                        if (numbers.contains(board[r][c])) {
                            containsNumberSquares = true;
                            break;
                        }
                    }
                }

                if (containsNumberSquares) {
                    nextRound();
                    return;
                }
            } else {
                winner = currentPlayer;
            }

            getGameLobby().stopGame();
        }
    }
}
    