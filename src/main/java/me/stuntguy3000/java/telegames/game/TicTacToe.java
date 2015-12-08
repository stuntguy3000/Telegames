package me.stuntguy3000.java.telegames.game;

import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.Game;
import me.stuntguy3000.java.telegames.object.LobbyMember;
import me.stuntguy3000.java.telegames.util.Direction;
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
import java.util.LinkedHashMap;
import java.util.List;

// @author Luke Anderson | stuntguy3000
public class TicTacToe extends Game {
    private List<LobbyMember> activePlayers = new ArrayList<>();
    private LobbyMember cross;
    private LobbyMember currentPlayer;
    private int currentRound = 1;
    private GameState gameState;
    private LinkedHashMap<Integer, TelegramEmoji> gamepad = new LinkedHashMap<>();
    private int maxPlayers = 2;
    private int maxRounds = 10;
    private int minPlayers = 2;
    private LobbyMember naught;
    private LobbyMember winner;

    public TicTacToe() {
        setGameInfo("TicTacToe", "First player to line three in a row wins.");
    }

    public SendableTextMessage.SendableTextMessageBuilder createKeyboard() {
        List<List<String>> buttonList = new ArrayList<>();
        List<String> row = new ArrayList<>();

        int index = 1;

        for (TelegramEmoji emoji : gamepad.values()) {
            if (index == 4) {
                index = 0;
                buttonList.add(new ArrayList<>(row));
                row.clear();
            }

            row.add(emoji.getText());
            index++;
        }

        if (row.size() > 0) {
            buttonList.add(new ArrayList<>(row));
        }

        return SendableTextMessage.builder().replyMarkup(new ReplyKeyboardMarkup(buttonList, true, true, false));
    }

    @Override
    public void endGame() {
        SendableTextMessage.SendableTextMessageBuilder messageBuilder = SendableTextMessage.builder().message("The game of Uno has ended!").replyMarkup(ReplyKeyboardHide.builder().build());

        if (winner != null) {
            messageBuilder.message("\n\n*The winner is " + winner.getUsername() + "*").parseMode(ParseMode.MARKDOWN);
        }

        getGameLobby().sendMessage(messageBuilder.build());
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

    private TelegramEmoji getSquare(int squareID) {
        return gamepad.get(squareID);
    }

    // Make less ugly
    private int getSquareID(int squareID, Direction direction, int jumps) {
        switch (direction) {
            case UP: {
                if (jumps == 1) {
                    switch (squareID) {
                        default:
                            return 0;
                        case 4:
                            return 1;
                        case 5:
                            return 2;
                        case 6:
                            return 3;
                        case 7:
                            return 4;
                        case 8:
                            return 5;
                        case 9:
                            return 6;
                    }
                } else {
                    switch (squareID) {
                        default:
                            return 0;
                        case 7:
                            return 1;
                        case 8:
                            return 2;
                        case 9:
                            return 3;
                    }
                }
            }
            case DOWN: {
                if (jumps == 1) {
                    switch (squareID) {
                        case 1:
                            return 4;
                        case 2:
                            return 5;
                        case 3:
                            return 6;
                        case 4:
                            return 7;
                        case 5:
                            return 8;
                        case 6:
                            return 9;
                        default:
                            return 0;
                    }
                } else {
                    switch (squareID) {
                        case 1:
                            return 7;
                        case 2:
                            return 8;
                        case 3:
                            return 9;
                        default:
                            return 0;
                    }
                }
            }
            case RIGHT: {
                if (jumps == 1) {
                    switch (squareID) {
                        case 1:
                            return 2;
                        case 2:
                            return 3;
                        case 3:
                            return 0;
                        case 4:
                            return 5;
                        case 5:
                            return 6;
                        case 6:
                            return 0;
                        case 7:
                            return 8;
                        case 8:
                            return 9;
                        case 9:
                            return 0;
                    }
                } else {
                    switch (squareID) {
                        case 1:
                            return 3;
                        case 4:
                            return 6;
                        case 7:
                            return 9;
                        default:
                            return 0;
                    }
                }
            }
            case LEFT: {
                if (jumps == 1) {
                    switch (squareID) {
                        case 1:
                            return 0;
                        case 2:
                            return 1;
                        case 3:
                            return 2;
                        case 4:
                            return 0;
                        case 5:
                            return 4;
                        case 6:
                            return 5;
                        case 7:
                            return 0;
                        case 8:
                            return 7;
                        case 9:
                            return 8;
                    }
                } else {
                    switch (squareID) {
                        case 3:
                            return 1;
                        case 6:
                            return 4;
                        case 9:
                            return 7;
                        default:
                            return 0;
                    }
                }
            }
            case LEFT_DOWN: {
                if (jumps == 1) {
                    switch (squareID) {
                        default:
                            return 0;
                        case 2:
                            return 4;
                        case 3:
                            return 5;
                        case 5:
                            return 7;
                        case 6:
                            return 8;
                    }
                } else {
                    switch (squareID) {
                        case 3:
                            return 7;
                        default:
                            return 0;
                    }
                }
            }
            case LEFT_UP: {
                if (jumps == 1) {
                    switch (squareID) {
                        default:
                            return 0;
                        case 5:
                            return 1;
                        case 6:
                            return 2;
                        case 8:
                            return 4;
                        case 9:
                            return 5;
                    }
                } else {
                    switch (squareID) {
                        case 9:
                            return 1;
                        default:
                            return 0;
                    }
                }
            }
            case RIGHT_DOWN: {
                if (jumps == 1) {
                    switch (squareID) {
                        default:
                            return 0;
                        case 1:
                            return 5;
                        case 2:
                            return 6;
                        case 4:
                            return 8;
                        case 5:
                            return 9;
                    }
                } else {
                    switch (squareID) {
                        case 1:
                            return 9;
                        default:
                            return 0;
                    }
                }
            }
            case RIGHT_UP: {
                if (jumps == 1) {
                    switch (squareID) {
                        default:
                            return 0;
                        case 4:
                            return 2;
                        case 5:
                            return 3;
                        case 7:
                            return 5;
                        case 8:
                            return 6;
                    }
                } else {
                    switch (squareID) {
                        case 7:
                            return 3;
                        default:
                            return 0;
                    }
                }
            }
        }

        return 0;
    }

    private boolean hasMatches(TelegramEmoji emoji, int tempSquareID) {
        for (Direction direction : Direction.values()) {
            int newSquareID = getSquareID(tempSquareID, direction, 1);
            TelegramEmoji newSquare = getSquare(newSquareID);
            if (newSquare != null && newSquare == emoji) {
                TelegramEmoji secondSquare = getSquare(getSquareID(tempSquareID, direction, 2));
                if (secondSquare != null && secondSquare == newSquare) {
                    // WINNER WINNER CHICKEN DINNER
                    return true;
                }
            }
        }

        return false;
    }

    private void nextRound() {
        if (naught.getUserID() == currentPlayer.getUserID()) {
            currentPlayer = cross;
        } else {
            currentPlayer = naught;
        }

        getGameLobby().sendMessage(createKeyboard().message("It is your turn, " + currentPlayer.getUsername()).parseMode(ParseMode.MARKDOWN).build());
    }

    private void playTurn(LobbyMember currentPlayer, TelegramEmoji emoji) {
        if (emoji != null) {
            int squareID = 0;
            switch (emoji) {
                case NUMBER_BLOCK_ONE: {
                    squareID = 1;
                    break;
                }
                case NUMBER_BLOCK_TWO: {
                    squareID = 2;
                    break;
                }
                case NUMBER_BLOCK_THREE: {
                    squareID = 3;
                    break;
                }
                case NUMBER_BLOCK_FOUR: {
                    squareID = 4;
                    break;
                }
                case NUMBER_BLOCK_FIVE: {
                    squareID = 5;
                    break;
                }
                case NUMBER_BLOCK_SIX: {
                    squareID = 6;
                    break;
                }
                case NUMBER_BLOCK_SEVEN: {
                    squareID = 7;
                    break;
                }
                case NUMBER_BLOCK_EIGHT: {
                    squareID = 8;
                    break;
                }
                case NUMBER_BLOCK_NINE: {
                    squareID = 9;
                    break;
                }
            }

            if (squareID > 0) {
                TelegramEmoji character = (currentPlayer.getUserID() == naught.getUserID() ? TelegramEmoji.RED_CIRCLE : TelegramEmoji.RED_CROSS);
                gamepad.put(squareID, character);

                int tempSquareID = 1;
                for (TelegramEmoji telegramEmoji : gamepad.values()) {
                    if (hasMatches(telegramEmoji, tempSquareID)) {
                        winner = currentPlayer;
                        getGameLobby().stopGame();
                    }
                }
            }
        }

        TelegramBot.getChat(currentPlayer.getUserID()).sendMessage(SendableTextMessage.builder().message("*Please play a valid option!*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
    }

    private void startGame() {
        gameState = GameState.INGAME;
        getGameLobby().sendMessage("Starting the game!");

        gamepad.put(1, TelegramEmoji.NUMBER_BLOCK_ONE);
        gamepad.put(2, TelegramEmoji.NUMBER_BLOCK_TWO);
        gamepad.put(3, TelegramEmoji.NUMBER_BLOCK_THREE);
        gamepad.put(4, TelegramEmoji.NUMBER_BLOCK_FOUR);
        gamepad.put(5, TelegramEmoji.NUMBER_BLOCK_FIVE);
        gamepad.put(6, TelegramEmoji.NUMBER_BLOCK_SIX);
        gamepad.put(7, TelegramEmoji.NUMBER_BLOCK_SEVEN);
        gamepad.put(8, TelegramEmoji.NUMBER_BLOCK_EIGHT);
        gamepad.put(9, TelegramEmoji.NUMBER_BLOCK_NINE);

        nextRound();
    }
}
    