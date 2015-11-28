package me.stuntguy3000.java.telegames.game;

import lombok.Getter;
import lombok.Setter;
import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.TelegramGame;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.util.GameState;
import me.stuntguy3000.java.telegames.util.PlayerData;
import me.stuntguy3000.java.telegames.util.StringUtil;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
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

    @Getter
    String text;

    CardColour(String text) {
        this.text = text;
    }
}

enum CardValue {
    ZERO("0⃣", 0), ONE("1⃣", 1), TWO("2⃣", 2), THREE("3⃣", 3), FOUR("4⃣", 4),
    FIVE("5⃣", 5), SIX("6⃣", 6), SEVEN("7⃣", 7), EIGHT("8⃣", 8), NINE("9⃣", 9),
    SKIP("\uD83D\uDEAB", 20), REVERSE("↩️", 20), DRAW2("Draw2", 20), DRAW4("Draw4", 50), WILD("Wild", 50);

    @Getter
    String text;
    @Getter
    int scoreValue;

    CardValue(String text, int scoreValue) {
        this.text = text;
        this.scoreValue = scoreValue;
    }
}

class LobbyTimer extends TimerTask {

    private Uno instance;

    public LobbyTimer(Uno instance) {
        this.instance = instance;
    }

    @Override
    public void run() {
        int timeToStart = instance.timeToStart;

        if (timeToStart == 30 || timeToStart == 15 || timeToStart == 5) {
            SendableTextMessage message = SendableTextMessage
                    .builder()
                    .message("*Starting game in " + timeToStart + " seconds...*")
                    .parseMode(ParseMode.MARKDOWN)
                    .build();
            instance.announceTime(message);
        }

        instance.timeToStart--;

        if (timeToStart == 0) {
            instance.checkPlayers();
            this.cancel();
        }
    }
}

// @author Luke Anderson | stuntguy3000
public class Uno extends TelegramGame {

    public int timeToStart = 30;
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

    public Uno() {
        setInfo("Uno", "The classic card game Uno.");
    }

    @Override
    public String getHelp() {
        return "The classic card game Uno brought to Telegram.\n\nMessage @Telegames +help while ingame for a list of commands.";
    }

    @Override
    public void onTextMessageReceived(TextMessageReceivedEvent event) {
        if (event.getChat().getType() == ChatType.PRIVATE) {
            User sender = event.getMessage().getSender();
            String message = event.getContent().getContent();
            Chat senderChat = TelegramBot.getChat(sender.getId());

            if (message.startsWith("+")) {
                String[] allArgs = message.substring(1).split(" ");
                String command = allArgs[0];

                if (isPlayer(sender)) {
                    if (command.equalsIgnoreCase("help")) {
                        sendMessage(senderChat, "Uno Command Menu:\n" +
                                "+help - View the help menu\n" +
                                "+deck - View your deck\n" +
                                "+scores - View the scores\n" +
                                "+colour - View the colour picker");
                    } else if (command.equalsIgnoreCase("deck")) {
                        if (getGameState() == GameState.INGAME) {
                            sendDeck(getPlayerData(sender));
                        } else {
                            sendMessage(senderChat, "The game has not started!");
                        }
                    } else if (command.equalsIgnoreCase("scores")) {
                        if (getGameState() == GameState.INGAME) {
                            printScores();
                        } else {
                            sendMessage(senderChat, "The game has not started!");
                        }
                    } else if (command.equalsIgnoreCase("colour") || command.equalsIgnoreCase("color")) {
                        if (getGameState() == GameState.INGAME) {
                            sendColourPicker(sender);
                        } else {
                            sendMessage(senderChat, "The game has not started!");
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
                    if (isPlayer(sender)) {
                        for (CardColour cardColour : CardColour.values()) {
                            if (message.equals(cardColour.getText())) {
                                chooseColour(sender, cardColour);
                                return;
                            }
                        }

                        if (message.equals("Draw from deck")) {
                            drawCard(sender);
                            return;
                        }

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
    }

    private void drawCard(User sender) {
        sendPlayersMessage(SendableTextMessage.builder()
                        .message("*" + sender.getUsername() + " drew from the deck!*")
                        .parseMode(ParseMode.MARKDOWN)
                        .build()
        );
        giveCardsFromDeck(getPlayerData(sender), 1);
        nextPlayerIndex();
        nextRound();
    }

    private void chooseColour(User sender, CardColour cardColour) {
        if (currentPlayer.equalsIgnoreCase(sender.getUsername()) && choosingColour) {
            choosingColour = false;
            nextCardColour = cardColour;
            nextPlayerIndex();
            nextRound();
        }
    }

    private void playCard(Card clickedCard, User sender) {
        if (currentPlayer.equalsIgnoreCase(sender.getUsername())) {
            if (choosingColour) {
                sendMessage(TelegramBot.getChat(sender.getId()), "Please choose a colour.");
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
                                            .message("*" + punishedPlayer + " has been given two cards!*")
                                            .parseMode(ParseMode.MARKDOWN)
                                            .build()
                            );

                            giveCardsFromDeck(getPlayerData(punishedPlayer), 2);
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
                                            .message("*" + punishedPlayer + " has been skipped!*")
                                            .parseMode(ParseMode.MARKDOWN)
                                            .build()
                            );

                            nextPlayerIndex();
                        } else {
                            nextPlayerIndex();
                        }

                        if (!removeCard(playerDecks.get(sender.getUsername()), activeCard)) {
                            sendPlayersMessage("Card was not removed from deck, contact @stuntguy3000");
                            stopGame(false);
                        } else {
                            updateScore(getPlayerData(sender));

                            if (playerDecks.get(sender.getUsername()).size() == 0) {
                                // WINNER WINNER CHICKEN DINNER
                                winner(sender.getUsername());
                                return;
                            }

                            nextRound();
                        }
                    } else {
                        sendMessage(TelegramBot.getChat(sender.getId()), "You have chosen an invalid card!");
                        sendDeck(getPlayerData(sender));
                    }
                    return;
                }
                case DRAW4:
                case WILD: {
                    sendPlayersMessage(sender.getUsername() + " played: " + clickedCard.getText());
                    activeCard = clickedCard;
                    nextCardColour = clickedCard.getCardColour();


                    if (clickedCard.getCardValue() == CardValue.DRAW4) {
                        nextPlayerIndex();
                        String punishedPlayer = playerOrder.get(playerOrderIndex);

                        sendPlayersMessage(SendableTextMessage.builder()
                                        .message("*" + punishedPlayer + " has been given four cards!*")
                                        .parseMode(ParseMode.MARKDOWN)
                                        .build()
                        );
                        giveCardsFromDeck(getPlayerData(punishedPlayer), 4);
                    }

                    if (!removeCard(playerDecks.get(sender.getUsername()), activeCard)) {
                        sendPlayersMessage("Card was not removed from deck, contact @stuntguy3000");
                        stopGame(false);
                    } else {
                        updateScore(getPlayerData(sender));
                        sendColourPicker(sender);
                        choosingColour = true;
                    }
                }
            }
        } else {
            sendMessage(TelegramBot.getChat(sender.getId()), "It's not your turn.");
        }
    }

    private void winner(String username) {
        getActivePlayers().forEach(this::updateScore);

        SendableTextMessage.SendableTextMessageBuilder message = SendableTextMessage.builder()
                .message("*===== GAME OVER =====*")
                .message("*" + username + "* is the winner!")
                .parseMode(ParseMode.MARKDOWN);

        sendPlayersMessage(message
                        .replyMarkup(new ReplyKeyboardHide())
                        .build()
        );
        sendMessage(getChat(), message.build());
        printScores();

        Telegames.getInstance().getGameHandler().stopGame(getChat(), true);
    }

    private void updateScore(PlayerData playerData) {
        int score = 0;

        for (Card card : getPlayerDecks().get(playerData.getUsername())) {
            score += card.getCardValue().getScoreValue();
        }

        playerData.setScore(score);
    }

    private void sendColourPicker(User sender) {
        List<List<String>> buttonList = new ArrayList<>();

        buttonList.add(Arrays.asList(
                CardColour.RED.getText(), CardColour.BLUE.getText(), CardColour.GREEN.getText(), CardColour.YELLOW.getText()));

        TelegramBot.getChat(sender.getId()).sendMessage(SendableTextMessage
                        .builder()
                        .message("Please choose a colour.")
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

    private void giveCardsFromDeck(PlayerData playerData, int amount) {
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
            givenCardsMessage.append(card.getText()).append(" | ");
            playerDecks.get(playerData.getUsername()).add(card);
        }

        sendMessage(TelegramBot.getChat(playerData.getId()), "Picked up cards: %s",
                givenCardsMessage.toString().substring(0, givenCardsMessage.length() - 3));
    }

    private void printScores() {
        sortScores();
        StringBuilder wholeMessage = new StringBuilder();
        int playerPos = 1;
        for (int i = getActivePlayers().size() - 1; i >= 0; --i) {
            PlayerData playerData = getActivePlayers().get(i);
            wholeMessage.append(String.format("#%d - %s (Score: %d)\n", playerPos++, playerData.getUsername(), playerData.getScore()));
        }
        sendMessage(getChat(), wholeMessage.toString());
        sendPlayersMessage(wholeMessage.toString());
    }

    @Override
    public void startGame() {
        sendMessage(getChat(), "A game of Uno has been started!\nType /joingame to participate.");
        setGameState(GameState.WAITING_FOR_PLAYERS);
        LobbyTimer lobbyTimer = new LobbyTimer(this);
        new Timer().schedule(lobbyTimer, 0, 1000);
    }

    @Override
    public void stopGame(boolean silent) {
        if (!silent) {
            SendableTextMessage.SendableTextMessageBuilder messageBuilder = SendableTextMessage.builder()
                    .message("The game of Uno has ended!")
                    .replyMarkup(ReplyKeyboardHide.builder().build());

            for (PlayerData playerData : getActivePlayers()) {
                sendMessage(TelegramBot.getChat(playerData.getId()), messageBuilder.build());
            }

            getChat().sendMessage(messageBuilder.build(), TelegramHook.getBot());
            printScores();
        }
    }

    @Override
    public void playerJoin(User user) {
        if (getGameState() == GameState.WAITING_FOR_PLAYERS) {
            if (isPlayer(user)) {
                sendMessagePlayer(getChat(), user, "You have already joined the game!");
            } else {
                addPlayer(user);
                int playersNeeded = minPlayers - getActivePlayers().size();
                if (playersNeeded > 0) {
                    sendMessagePlayer(getChat(), user, "You have joined the game! (Waiting for %d player%s)", playersNeeded, StringUtil.isPlural(playersNeeded));
                    Chat userChat = TelegramBot.getChat(user.getId());
                } else {
                    sendMessagePlayer(getChat(), user, "You have joined the game!");
                }
            }
        } else {
            sendMessagePlayer(getChat(), user, "You cannot join mid-game!");
        }
    }

    public void checkPlayers() {
        switch (getGameState()) {
            case WAITING_FOR_PLAYERS: {
                if (getMinPlayers() <= getActivePlayers().size()) {
                    startIngame();
                } else {
                    SendableTextMessage message = SendableTextMessage
                            .builder()
                            .message("*There are not enough players to continue!*")
                            .parseMode(ParseMode.MARKDOWN)
                            .build();
                    sendMessage(getChat(), (message));
                    Telegames.getInstance().getGameHandler().stopGame(getChat(), true);
                }
                return;
            }
            case INGAME: {
                if (getMinPlayers() > getActivePlayers().size()) {
                    SendableTextMessage message = SendableTextMessage
                            .builder()
                            .message("*There are not enough players to continue!*")
                            .parseMode(ParseMode.MARKDOWN)
                            .build();
                    sendMessage(getChat(), (message));
                    Telegames.getInstance().getGameHandler().stopGame(getChat(), false);
                }
            }
        }
    }

    private void startIngame() {
        setGameState(GameState.INGAME);
        sendMessage(getChat(), "Starting the game!");
        sendPlayersMessage("Starting the game!");

        playerOrder.addAll(getActivePlayers().stream().map(PlayerData::getUsername).collect(Collectors.toList()));
        Collections.shuffle(playerOrder);

        fillDeck();
        fillHands();
        setActiveCard(entireDeck.remove(0));
        setNextCardColour(getActiveCard().getCardColour());
        nextRound();
    }

    private void fillHands() {
        for (PlayerData playerData : getActivePlayers()) {
            List<Card> deck = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                deck.add(entireDeck.remove(0));
            }
            getPlayerDecks().put(playerData.getUsername(), deck);
            updateScore(playerData);
        }
    }

    private void sendDeck(PlayerData playerData) {
        List<List<String>> buttonList = new ArrayList<>();
        List<String> row = new ArrayList<>();
        List<Card> deck = playerDecks.get(playerData.getUsername());

        int index = 1;
        for (Card card : deck) {
            if (index == 8) {
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

        buttonList.add(Arrays.asList("Draw from deck", "Your Score: " + playerData.getScore()));

        TelegramBot.getChat(playerData.getId()).sendMessage(SendableTextMessage
                        .builder()
                        .message("Here are your cards.")
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
        checkPlayers();
    }

    public void nextRound() {
        currentPlayer = playerOrder.get(playerOrderIndex);

        String cardText = getActiveCard().getText();

        if (!getActiveCard().getCardColour().equals(nextCardColour)) {
            cardText = "Any " + nextCardColour.text + " card";
        }

        sendPlayersMessage(
                SendableTextMessage.builder()
                        .message("*====== Starting Round " + round + " ======*\n" +
                                " *Current Card:* " + cardText + "\n" +
                                " *Current Player:* " + currentPlayer)
                        .parseMode(ParseMode.MARKDOWN)
                        .build()
        );

        round++;

        sendDeck(getPlayerData(currentPlayer));
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

    public void announceTime(SendableTextMessage message) {
        sendMessage(getChat(), message);
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