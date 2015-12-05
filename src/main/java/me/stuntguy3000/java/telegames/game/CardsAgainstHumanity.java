package me.stuntguy3000.java.telegames.game;

import lombok.Getter;
import lombok.Setter;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.Game;
import me.stuntguy3000.java.telegames.object.LobbyMember;
import me.stuntguy3000.java.telegames.util.GameState;
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
import java.util.HashMap;
import java.util.List;

enum CAHCardType {
    WHITE,
    BLACK
}

enum CAHPackProperty {
    METADATA,
    WHITECARDS,
    BLACKCARDS
}

class CAHCard {
    @Setter
    @Getter
    private CAHCardType cahCardType;
    @Setter
    @Getter
    private String text;

    public CAHCard(String text, CAHCardType cahCardType) {
        this.text = text;
        this.cahCardType = cahCardType;
    }
}

class CAHCardPack {

    @Getter
    @Setter
    private List<CAHCard> cards;

    @Getter
    @Setter
    private HashMap<String, String> metadata;

    public CAHCardPack() {
        cards = new ArrayList<>();
        metadata = new HashMap<>();
    }

    public void addCard(String cardText, CAHCardType cahCardType) {
        cards.add(new CAHCard(cardText, cahCardType));
    }

    public void addMetadata(String dataName, String dataValue) {
        metadata.put(dataName, dataValue);
    }
}

// @author Luke Anderson | stuntguy3000
public class CardsAgainstHumanity extends Game {

    private List<LobbyMember> activePlayers = new ArrayList<>();
    private CAHCard blackCard;
    private List<CAHCard> blackCards = new ArrayList<>();
    private LobbyMember cardCzar;
    private boolean continueGame = true;
    private GameState gameState;
    private GameTimer gameTimer;
    private boolean increasePlayerIndex = true;
    private int maxPlayers = 8;
    private int minPlayers = 3;
    private HashMap<Integer, List<CAHCard>> playerCards = new HashMap<>();
    private List<String> playerOrder = new ArrayList<>();
    private int playerOrderIndex = 0;
    private int round = 1;
    private int secondsSincePlay = 0;
    private List<CAHCard> whiteCards = new ArrayList<>();

    // Init Class
    public CardsAgainstHumanity() {
        setGameInfo("CardsAgainstHumanity", "The most fun and offensive card game ever known.");
        gameState = GameState.WAITING_FOR_PLAYERS;
        loadPacks();
    }

    public boolean checkPlayers() {
        if (minPlayers > activePlayers.size()) {
            SendableTextMessage message = SendableTextMessage.builder().message("*There are not enough players to continue!*").parseMode(ParseMode.MARKDOWN).build();
            getGameLobby().sendMessage(message);
            getGameLobby().stopGame();
            return false;
        }
        return true;
    }

    @Override
    public void endGame() {
        gameTimer.cancel();

        SendableTextMessage.SendableTextMessageBuilder messageBuilder = SendableTextMessage.builder().message("The game of CardsAgainstHumanity has ended!").replyMarkup(ReplyKeyboardHide.builder().build());

        getGameLobby().sendMessage(messageBuilder.build());
        printScores();
    }

    @Override
    public String getGameHelp() {
        return "The most fun and offensive card game ever known.\n\nMessage @Telegames +help while ingame for a list of commands.";
    }

    @Override
    public void onTextMessageReceived(TextMessageReceivedEvent event) {
        if (event.getChat().getType() == ChatType.PRIVATE) {
            User sender = event.getMessage().getSender();
            String message = event.getContent().getContent();
            LobbyMember lobbyMember = getGameLobby().getLobbyMember(sender.getUsername());

            if (message.startsWith("+")) {
                String[] allArgs = message.substring(1).split(" ");
                String command = allArgs[0];

                if (command.equalsIgnoreCase("help")) {
                    getGameLobby().sendMessage("CardsAgainstHumanity Command Menu:\n" +
                            "+help - View the help menu\n" +
                            "+cards - View your cards");
                } else if (command.equalsIgnoreCase("cards")) {
                    if (gameState == GameState.INGAME) {
                        sendCards(lobbyMember);
                    } else {
                        getGameLobby().sendMessage("The game has not started!");
                    }
                }
            } else {
                getGameLobby().userChat(sender, message);
            }
        }
    }

    @Override
    public boolean playerJoin(LobbyMember player) {
        if (gameState == GameState.WAITING_FOR_PLAYERS) {
            activePlayers.add(player);
            player.getChat().sendMessage("You have joined the game!", TelegramHook.getBot());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void playerLeave(String username, int id) {
        removePlayer(username);

        if (cardCzar.equals(username) && checkPlayers()) {
            nextRound();
        }
    }

    // Required Methods
    public boolean tryStartGame() {
        if (activePlayers.size() >= minPlayers) {
            if (activePlayers.size() > maxPlayers) {
                getGameLobby().sendMessage("Too many players! Maximum: " + maxPlayers);
                return false;
            } else {
                startGame();
                return true;
            }
        } else {
            return false;
        }
    }

    private void fillHands() {
        for (LobbyMember lobbyMember : activePlayers) {
            giveCard(lobbyMember, 10);
        }
    }

    private void giveCard(LobbyMember lobbyMember, int amount) {
        List<CAHCard> playerCardDeck = playerCards.get(lobbyMember.getUserID());

        if (playerCardDeck == null) {
            playerCardDeck = new ArrayList<>();
        }

        for (int i = 0; i < amount; i++) {
            playerCardDeck.add(whiteCards.remove(0));
        }

        playerCards.put(lobbyMember.getUserID(), playerCardDeck);
    }

    // Load Card Packs from Jar resources
    public void loadPacks() {
        InputStream is = getClass().getResourceAsStream("/cah.v3.cards");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        CAHCardPack cahCardPack = new CAHCardPack();
        String packLine;
        CAHPackProperty cahPackProperty = null;

        try {
            while ((packLine = reader.readLine()) != null) {
                switch (packLine) {
                    case "___METADATA___": {
                        cahPackProperty = CAHPackProperty.METADATA;
                        continue;
                    }
                    case "___BLACK___": {
                        cahPackProperty = CAHPackProperty.BLACKCARDS;
                        continue;
                    }
                    case "___WHITE___": {
                        cahPackProperty = CAHPackProperty.WHITECARDS;
                        continue;
                    }
                    default: {
                        if (cahPackProperty != null) {
                            switch (cahPackProperty) {
                                case METADATA: {
                                    if (packLine.contains(": ")) {
                                        String[] packData = packLine.split(": ");
                                        cahCardPack.addMetadata(packData[0], packData[1]);
                                    }
                                }
                                case BLACKCARDS: {
                                    cahCardPack.addCard(packLine.replaceAll(" | ", "\n"), CAHCardType.BLACK);
                                }
                                case WHITECARDS: {
                                    cahCardPack.addCard(packLine.replaceAll(" | ", "\n"), CAHCardType.WHITE);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (CAHCard cahCard : cahCardPack.getCards()) {
            if (cahCard.getCahCardType() == CAHCardType.BLACK) {
                blackCards.add(cahCard);
            } else if (cahCard.getCahCardType() == CAHCardType.WHITE) {
                whiteCards.add(cahCard);
            }
        }
    }

    // Play the next round
    private void nextRound() {
        if (!continueGame) {
            getGameLobby().stopGame();
        } else {
            playerOrderIndex++;

            if (playerOrderIndex >= activePlayers.size()) {
                playerOrderIndex = 0;
            }

            blackCards.add(blackCard);
            cardCzar = activePlayers.get(0);
            blackCard = blackCards.get(0);

            getGameLobby().sendMessage(SendableTextMessage.builder().message("*Starting Round " + round + "*\n" +
                    "*Card Czar:* " + cardCzar.getUsername() + "\n" +
                    "_The black card is_").parseMode(ParseMode.MARKDOWN).build());

            getGameLobby().sendMessage(SendableTextMessage.builder().message(blackCard.getText()).build());
        }
    }

    private void printScores() {
        sortScores();
        StringBuilder wholeMessage = new StringBuilder();
        int playerPos = 1;
        for (int i = activePlayers.size() - 1; i >= 0; --i) {
            LobbyMember lobbyMember = activePlayers.get(i);
            wholeMessage.append(String.format("#%d - %s (Score: %d)\n", playerPos++, lobbyMember.getUsername(), lobbyMember.getGameScore()));
        }
        getGameLobby().sendMessage(wholeMessage.toString());
    }

    private void removePlayer(String username) {
        for (LobbyMember lobbyMember : new ArrayList<>(activePlayers)) {
            if (lobbyMember.getUsername().equals(username)) {
                activePlayers.remove(lobbyMember);
            }
        }
    }

    private void sendCards(LobbyMember lobbyMember) {
        if (cardCzar.getUserID() != lobbyMember.getUserID()) {
            List<List<String>> buttonList = new ArrayList<>();
            List<String> row = new ArrayList<>();
            List<CAHCard> cards = playerCards.get(lobbyMember.getUserID());

            int index = 1;
            for (CAHCard card : cards) {
                if (index == 5) {
                    index = 0;
                    buttonList.add(new ArrayList<>(row));
                    row.clear();
                }

                row.add(card.getText());
                index++;
            }

            if (row.size() > 0) {
                buttonList.add(new ArrayList<>(row));
            }

            TelegramBot.getChat(lobbyMember.getUserID()).sendMessage(SendableTextMessage.builder().message("Here are your cards.").replyMarkup(new ReplyKeyboardMarkup(buttonList, true, true, false)).build(), TelegramHook.getBot());

        }
    }

    private void sortScores() {
        Collections.sort(activePlayers);
    }

    private void startGame() {
        gameState = GameState.INGAME;
        getGameLobby().sendMessage("Starting the game!");

        Collections.shuffle(whiteCards);
        Collections.shuffle(blackCards);

        fillHands();
        nextRound();
    }
}
