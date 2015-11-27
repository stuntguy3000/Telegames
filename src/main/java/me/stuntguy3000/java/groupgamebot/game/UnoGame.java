package me.stuntguy3000.java.groupgamebot.game;

import lombok.Getter;
import lombok.Setter;
import me.stuntguy3000.java.groupgamebot.handler.TelegramGame;
import me.stuntguy3000.java.groupgamebot.hook.TelegramHook;
import me.stuntguy3000.java.groupgamebot.util.GameState;
import me.stuntguy3000.java.groupgamebot.util.PlayerScore;
import me.stuntguy3000.java.groupgamebot.util.StringUtil;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardHide;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardMarkup;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.*;
import java.util.stream.Collectors;

enum CardColour {
    RED("\uD83D\uDCD5"), YELLOW("\uD83D\uDCD2"), BLUE("\uD83D\uDCD8"), GREEN("\uD83D\uDCD7"), WILD("\uD83C\uDF08");

    String text;

    CardColour(String text) {
        this.text = text;
    }

    String getText() {
        return text;
    }
}

enum CardValue {
    ZERO("0⃣"), ONE("1⃣"), TWO("2⃣"), THREE("3⃣"), FOUR("4⃣"),
    FIVE("5⃣"), SIX("6⃣"), SEVEN("7⃣"), EIGHT("8⃣"), NINE("9⃣"),
    SKIP("\uD83D\uDEAB"), REVERSE("↩️"), DRAW2("Draw2"), DRAW4("Draw4"), WILD("Wild");

    String text;

    CardValue(String text) {
        this.text = text;
    }

    String getText() {
        return text;
    }
}

// @author Luke Anderson | stuntguy3000
public class UnoGame extends TelegramGame {

    @Getter
    @Setter
    private int round = 1;
    @Getter
    @Setter
    private int minPlayers = 2;
    @Getter
    @Setter
    private List<String> playerOrder = new ArrayList<>();
    @Getter
    @Setter
    private int playerOrderIndex = 0;
    @Getter
    @Setter
    private String currentPlayer;
    @Getter
    @Setter
    private List<Card> entireDeck = new ArrayList<>();
    @Getter
    @Setter
    private List<Card> playedCards = new ArrayList<>();
    @Getter
    @Setter
    private Card activeCard;
    @Getter
    @Setter
    private CardColour nextCardColour;
    @Getter
    @Setter
    private HashMap<String, List<Card>> playerDecks = new HashMap<>();
    @Getter
    @Setter
    private boolean choosingColour = false;
    @Getter
    @Setter
    private boolean increasePlayerIndex = true;

    public UnoGame() {
        setInfo("Uno", "The classic card game Uno.");
    }

    @Override
    public String getHelp() {
        return "Totally have a game help string";
    }

    @Override
    public void onTextMessageReceived(TextMessageReceivedEvent event) {
        User sender = event.getMessage().getSender();
        String message = event.getContent().getContent();

        if (message.startsWith("+")) {
            String[] allArgs = message.substring(1).split(" ");
            String command = allArgs[0];

            if (isPlayer(sender)) {
                if (command.equalsIgnoreCase("help")) {
                    sendMessagePlayer(getChat(), sender, "Uno Command Menu:\n" +
                            "+help - View the help menu\n" +
                            "+deck - View your deck\n" +
                            "+scores - View the scores");
                } else if (command.equalsIgnoreCase("deck")) {
                    if (getGameState() == GameState.INGAME) {
                        sendDeck(getPlayerScore(sender));
                    } else {
                        sendMessagePlayer(getChat(), sender, "The game has not started!");
                    }
                } else if (command.equalsIgnoreCase("scores")) {
                    if (getGameState() == GameState.INGAME) {
                        printScores();
                    } else {
                        sendMessagePlayer(getChat(), sender, "The game has not started!");
                    }
                }
            }
        } else {
            Card clickedCard = Card.getFromString(message);
            if (clickedCard != null) {
                for (Card card : new ArrayList<>(playerDecks.get(sender.getUsername()))) {
                    if (Card.isSame(card, clickedCard)) {
                        playCard(clickedCard, sender);
                        return;
                    }
                }
            } else {
                for (CardColour cardColour : CardColour.values()) {
                    if (message.equals(cardColour.getText())) {
                        chooseColour(sender, cardColour);
                        return;
                    }
                }

                if (event.getChat().getType() == ChatType.PRIVATE && isPlayer(sender)) {
                    getActivePlayers().stream().filter(player -> !player.getUsername().equals(sender.getUsername())).forEach(player -> {
                        sendMessage(TelegramBot.getChat(player.getId()),
                                SendableTextMessage.builder()
                                        .message("*[Chat]* " + sender.getUsername() + ": " + message)
                                        .parseMode(ParseMode.MARKDOWN)
                                        .build()
                        );
                    });
                }
            }
        }
    }

    private void chooseColour(User sender, CardColour cardColour) {
        if (currentPlayer.equalsIgnoreCase(sender.getUsername()) && choosingColour) {
            choosingColour = false;
            nextCardColour = cardColour;
        }
    }

    private void playCard(Card clickedCard, User sender) {
        if (currentPlayer.equalsIgnoreCase(sender.getUsername())) {
            if (choosingColour) {
                sendMessage(TelegramBot.getChat(getPlayerScore(sender).getId()), "Please choose a colour.");
                return;
            }

            switch (clickedCard.getCardValue()) {
                default: {
                    if (nextCardColour.equals(clickedCard.getCardColour()) ||
                            activeCard.getCardValue().equals(clickedCard.getCardValue())) {
                        sendPlayersMessage(sender.getUsername() + " played: " + clickedCard.getText());
                        activeCard = clickedCard;
                        nextCardColour = clickedCard.getCardColour();

                        if (activeCard.getCardValue() == CardValue.DRAW2) {
                            String punishedPlayer = nextPlayerIndex();
                            nextPlayerIndex();

                            sendPlayersMessage(SendableTextMessage.builder()
                                            .message("*" + punishedPlayer + " has been given four cards!*")
                                            .parseMode(ParseMode.MARKDOWN)
                                            .build()
                            );

                            giveCardsFromDeck(getPlayerScore(punishedPlayer), 4);
                        } else if (activeCard.getCardValue() == CardValue.REVERSE) {
                            sendPlayersMessage(SendableTextMessage.builder()
                                            .message("*Player order has been reversed!*")
                                            .parseMode(ParseMode.MARKDOWN)
                                            .build()
                            );
                            increasePlayerIndex = !increasePlayerIndex;
                        } else if (activeCard.getCardValue() == CardValue.SKIP) {
                            String punishedPlayer = nextPlayerIndex();

                            sendPlayersMessage(SendableTextMessage.builder()
                                            .message("*" + punishedPlayer + " has been given four cards!*")
                                            .parseMode(ParseMode.MARKDOWN)
                                            .build()
                            );

                            nextPlayerIndex();
                        }

                        if (!removeCard(playerDecks.get(sender.getUsername()), activeCard)) {
                            sendPlayersMessage("Card was not removed from deck, contact @stuntguy3000");
                            stopGame();
                        } else {
                            giveCardsFromDeck(getPlayerScore(sender.getUsername()), 1);
                            nextRound();
                        }
                    } else {
                        sendMessage(TelegramBot.getChat(getPlayerScore(sender).getId()), "You have chosen an invalid card!");
                    }
                    return;
                }
                case DRAW4:
                case WILD: {
                    sendPlayersMessage(sender.getUsername() + " played: " + clickedCard.getText());
                    activeCard = clickedCard;
                    nextCardColour = clickedCard.getCardColour();

                    String punishedPlayer = playerOrder.get(playerOrderIndex);
                    nextPlayerIndex();

                    sendPlayersMessage(SendableTextMessage.builder()
                                    .message("*" + punishedPlayer + " has been given four cards!*")
                                    .parseMode(ParseMode.MARKDOWN)
                                    .build()
                    );

                    giveCardsFromDeck(getPlayerScore(punishedPlayer), 4);
                    sendColourPicker(sender);
                    choosingColour = true;
                }
            }
        } else {
            sendMessage(TelegramBot.getChat(getPlayerScore(sender).getId()), "It's not your turn.");
        }
    }

    private void sendColourPicker(User sender) {
        List<List<String>> buttonList = new ArrayList<>();

        buttonList.add(Arrays.asList(
                CardColour.RED.getText(), CardColour.BLUE.getText(), CardColour.GREEN.getText(), CardColour.YELLOW.getText()));

        TelegramBot.getChat(getPlayerScore(sender).getId()).sendMessage(SendableTextMessage
                        .builder()
                        .message("Please choose a colour: " + getActiveCard().getText())
                        .replyMarkup(new ReplyKeyboardMarkup(buttonList, true, true, false))
                        .build(),
                TelegramHook.getBot());
    }

    private boolean removeCard(List<Card> cards, Card activeCard) {
        int index = 0;
        for (Card card : new ArrayList<>(cards)) {
            if (Card.isSame(card, activeCard)) {
                cards.remove(index);
                return true;
            }
            index++;
        }

        return false;
    }

    private void giveCardsFromDeck(PlayerScore playerScore, int amount) {
        List<Card> givenCards = new ArrayList<>();
        for (int i = 1; i <= amount; i++) {
            if (entireDeck.size() == 0) {
                sendPlayersMessage("Shuffling deck...");
                entireDeck.addAll(playedCards.stream().collect(Collectors.toList()));
                playedCards.clear();
                Collections.shuffle(entireDeck);
            }

            givenCards.add(entireDeck.remove(0));
        }

        StringBuilder givenCardsMessage = new StringBuilder();
        for (Card card : givenCards) {
            givenCardsMessage.append(card.getText()).append(" ");
            playerDecks.get(playerScore.getUsername()).add(card);
        }

        sendMessage(TelegramBot.getChat(playerScore.getId()), "Picked up cards: %s", givenCardsMessage.toString());
    }

    private void printScores() {
        sortScores();
        StringBuilder wholeMessage = new StringBuilder();
        for (int i = 0; i < getActivePlayers().size(); i++) {
            PlayerScore playerScore = getActivePlayers().get(i);
            wholeMessage.append(String.format("#%d - %s (Score: %d)\n", i + 1, playerScore.getUsername(), playerScore.getScore()));
        }
        sendMessage(getChat(), wholeMessage.toString());
        sendPlayersMessage(wholeMessage.toString());
    }

    @Override
    public void startGame() {
        sendMessage(getChat(), "A game of Uno has been started!\nType /joingame to participate.");
        setGameState(GameState.WAITING_FOR_PLAYERS);
    }

    @Override
    public void stopGame() {
        SendableTextMessage.SendableTextMessageBuilder messageBuilder = SendableTextMessage.builder()
                .message("The game of Uno has ended!")
                .replyMarkup(ReplyKeyboardHide.builder().build());

        for (PlayerScore playerScore : getActivePlayers()) {
            sendMessage(TelegramBot.getChat(playerScore.getId()), messageBuilder.build());
        }

        getChat().sendMessage(messageBuilder.build(), TelegramHook.getBot());
        printScores();
        setGameState(GameState.INGAME);
    }

    @Override
    public void playerJoin(User user) {
        if (getGameState() == GameState.WAITING_FOR_PLAYERS) {
            if (isPlayer(user)) {
                sendMessagePlayer(getChat(), user, "You have already joined the game!");
            } else {
                addPlayer(user);
                int playersNeeded = minPlayers - getActivePlayers().size();
                sendMessagePlayer(getChat(), user, "You have joined the game! (Waiting for %d player%s)", playersNeeded, StringUtil.isPlural(playersNeeded));
                checkPlayers();
            }
        } else {
            sendMessagePlayer(getChat(), user, "You cannot join mid-game!");
        }
    }

    private void checkPlayers() {
        switch (getGameState()) {
            case WAITING_FOR_PLAYERS: {
                if (getMinPlayers() <= getActivePlayers().size()) {
                    startIngame();
                }
                return;
            }
            case INGAME: {
                if (getMinPlayers() > getActivePlayers().size()) {
                    sendMessage(getChat(), "There are not enough players to continue!");
                    stopGame();
                }
            }
        }
    }

    private void startIngame() {
        setGameState(GameState.INGAME);
        sendMessage(getChat(), "Starting the game!");
        sendPlayersMessage("Starting the game!");

        playerOrder.addAll(getActivePlayers().stream().map(PlayerScore::getUsername).collect(Collectors.toList()));
        Collections.shuffle(playerOrder);

        fillDeck();
        fillHands();
        setActiveCard(entireDeck.remove(0));
        setNextCardColour(getActiveCard().getCardColour());
        nextRound();
    }

    private void fillHands() {
        for (PlayerScore playerScore : getActivePlayers()) {
            List<Card> deck = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                deck.add(entireDeck.remove(0));
            }
            getPlayerDecks().put(playerScore.getUsername(), deck);
        }
    }

    private void sendDeck(PlayerScore playerScore) {
        List<List<String>> buttonList = new ArrayList<>();
        List<String> row = new ArrayList<>();
        List<Card> deck = playerDecks.get(playerScore.getUsername());

        row.addAll(deck.stream().map(Card::getText).collect(Collectors.toList()));
        buttonList.add(row);
        buttonList.add(Collections.singletonList("Draw from deck"));

        TelegramBot.getChat(playerScore.getId()).sendMessage(SendableTextMessage
                        .builder()
                        .message("Here are your cards.\nCurrent card: " + getActiveCard().getText())
                        .replyMarkup(new ReplyKeyboardMarkup(buttonList, true, true, false))
                        .build(),
                TelegramHook.getBot());
    }

    private void fillDeck() {
        /**
         * The deck consists of 108 cards, of which there are twenty-five of each color (red, green, blue, and yellow),
         * each color having two of each rank except zero. The ranks in each color are zero to nine,
         * "Skip", "Draw Two" and "Reverse" (the last three of these being classified as "action cards").
         * In addition, the deck contains four each of "Wild" and "Wild Draw Four" cards.
         *
         * https://en.wikipedia.org/wiki/Uno_(card_game) - 26/11/15
         */
        for (CardColour cardColor : CardColour.values()) {
            for (CardValue cardValue : CardValue.values()) {
                switch (cardValue) {
                    default: {
                        if (cardColor != CardColour.WILD) {
                            if (cardValue != CardValue.ZERO) {
                                entireDeck.add(new Card(cardColor, cardValue));
                            }
                            entireDeck.add(new Card(cardColor, cardValue));
                        }
                        continue;
                    }
                    case WILD:
                    case DRAW4: {
                        if (cardColor != CardColour.WILD) {
                            entireDeck.add(new Card(CardColour.WILD, cardValue));
                        }
                    }
                }
            }
        }

        Collections.shuffle(entireDeck);
    }

    @Override
    public void playerLeave(User user) {
        removePlayer(user);
        sendMessagePlayer(getChat(), user, "You have left the game. (Score %d)" + getScore(user));
    }

    public void nextRound() {
        if (playerOrderIndex >= playerOrder.size()) {
            playerOrderIndex = 0;
        }

        if (playerOrderIndex < 0) {
            playerOrderIndex = playerOrder.size() - 1;
        }

        currentPlayer = playerOrder.get(playerOrderIndex);

        sendPlayersMessage(
                SendableTextMessage.builder()
                        .message("*====== Starting Round " + round + " ======*\n" +
                                " *Current Card: " + getActiveCard().getText() + "*\n" +
                                " *Current Player: " + currentPlayer + "*")
                        .parseMode(ParseMode.MARKDOWN)
                        .build()
        );

        nextPlayerIndex();
        round++;

        sendDeck(getPlayerScore(currentPlayer));
    }

    public String nextPlayerIndex() {
        if (increasePlayerIndex) {
            playerOrderIndex++;
        } else {
            playerOrderIndex--;
        }

        if (playerOrderIndex >= playerOrder.size()) {
            playerOrderIndex = 0;
        }

        if (playerOrderIndex < 0) {
            playerOrderIndex = playerOrder.size() - 1;
        }

        return playerOrder.get(playerOrderIndex);
    }
}

class Card {
    @Getter
    private CardColour cardColour;
    @Getter
    private CardValue cardValue;

    public Card(CardColour cardColor, CardValue cardValue) {
        this.cardColour = cardColor;
        this.cardValue = cardValue;
    }

    public static Card getFromString(String message) {
        CardColour cardColour = null;
        CardValue cardValue = null;

        if (message.contains(" ")) {
            String[] args = message.split(" ");
            if (args.length == 2) {
                for (CardColour cardColourLoop : CardColour.values()) {
                    if (cardColourLoop.getText().equalsIgnoreCase(args[0])) {
                        cardColour = cardColourLoop;
                        break;
                    }
                }

                for (CardValue cardValueLoop : CardValue.values()) {
                    if (cardValueLoop.getText().equalsIgnoreCase(args[1])) {
                        cardValue = cardValueLoop;
                        break;
                    }
                }

                if (cardColour != null && cardValue != null) {
                    return new Card(cardColour, cardValue);
                }
            }
        }

        return null;
    }

    public static boolean isSame(Card card1, Card card2) {
        return card1.getCardColour().equals(card2.getCardColour()) && card1.getCardValue().equals(card2.getCardValue());
    }

    public String getText() {
        return getCardColour().getText() + " " + getCardValue().getText();
    }

    @Override
    public String toString() {
        return String.format("[Colour: %S Value: %s]", cardColour.toString(), cardValue.toString());
    }
}