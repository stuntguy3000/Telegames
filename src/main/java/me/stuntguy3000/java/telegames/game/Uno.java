package me.stuntguy3000.java.telegames.game;

import lombok.Getter;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.game.Game;
import me.stuntguy3000.java.telegames.object.game.GameState;
import me.stuntguy3000.java.telegames.object.user.TelegramUser;
import me.stuntguy3000.java.telegames.util.string.StringUtil;
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
    RED("\uD83D\uDCD5"),
    YELLOW("\uD83D\uDCD2"),
    BLUE("\uD83D\uDCD8"),
    GREEN("\uD83D\uDCD7"),
    WILD("\uD83C\uDF08");

    @Getter
    String text;

    CardColour(String text) {
        this.text = text;
    }
}

enum CardValue {
    ZERO("0⃣", 0),
    ONE("1⃣", 1),
    TWO("2⃣", 2),
    THREE("3⃣", 3),
    FOUR("4⃣", 4),
    FIVE("5⃣", 5),
    SIX("6⃣", 6),
    SEVEN("7⃣", 7),
    EIGHT("8⃣", 8),
    NINE("9⃣", 9),
    SKIP("\uD83D\uDEAB", 20),
    REVERSE("↩️", 20),
    DRAW2("Draw2", 20),
    DRAW4("Draw4", 50),
    WILD("Wild", 50);

    @Getter
    int scoreValue;
    @Getter
    String text;

    CardValue(String text, int scoreValue) {
        this.text = text;
        this.scoreValue = scoreValue;
    }
}

public class Uno extends Game {
    @Getter
    private UnoCard activeUnoCard;
    @Getter
    private boolean choosingColour = false;
    @Getter
    private String currentPlayer;
    @Getter
    private List<UnoCard> entireDeck = new ArrayList<>();
    @Getter
    private boolean increasePlayerIndex = true;
    @Getter
    private CardColour nextCardColour;
    @Getter
    private List<UnoCard> playedUnoCards = new ArrayList<>();
    @Getter
    private HashMap<Integer, List<UnoCard>> playerDecks = new HashMap<>();
    @Getter
    private List<String> playerOrder = new ArrayList<>();
    @Getter
    private int playerOrderIndex = 0;
    @Getter
    private int round = 1;
    @Getter
    private int secondsSincePlay = 0;

    public Uno() {
        setGameInfo("Uno", "The classic card game Uno.");
        setMinPlayers(2);
        setGameState(GameState.WAITING_FOR_PLAYERS);
    }

    private void chooseColour(User sender, CardColour cardColour) {
        if (currentPlayer.equalsIgnoreCase(sender.getUsername()) && choosingColour) {
            secondsSincePlay = 0;
            choosingColour = false;
            nextCardColour = cardColour;
            nextPlayerIndex();
            nextRound();
        }
    }

    private void drawCard(String username) {
        if (currentPlayer.equalsIgnoreCase(username)) {
            secondsSincePlay = 0;
            getGameLobby().sendMessage(SendableTextMessage.builder().message("*" + StringUtil.markdownSafe(username) + " drew from the deck!*").parseMode(ParseMode.MARKDOWN).replyMarkup(new ReplyKeyboardHide()).build());
            giveCardsFromDeck(getGameLobby().getLobbyMember(username), 1);
            nextPlayerIndex();
            nextRound();
        }
    }

    @Override
    public void endGame() {
        SendableTextMessage.SendableTextMessageBuilder messageBuilder = SendableTextMessage.builder().message("The game of Uno has ended!").replyMarkup(ReplyKeyboardHide.builder().build());

        getGameLobby().sendMessage(messageBuilder.build());
        printScores();
    }

    @Override
    public String getGameHelp() {
        return "The classic card game Uno brought to Telegram.\n\nMessage @" + TelegramHook.getBot().getBotUsername() + " +help while in-game for a list of commands.";
    }

    @Override
    public void onSecond() {
        secondsSincePlay++;

        if (!choosingColour) {
            if (secondsSincePlay == 30) {
                getGameLobby().sendMessage(SendableTextMessage.builder().message("Please play a card @" + currentPlayer).build());
            } else if (secondsSincePlay == 40) {
                getGameLobby().sendMessage(SendableTextMessage.builder().message("*" + StringUtil.markdownSafe(currentPlayer) + " ran out of time!*").parseMode(ParseMode.MARKDOWN).build());
                drawCard(currentPlayer);
            }
        }
    }

    @Override
    public void onTextMessageReceived(TextMessageReceivedEvent event) {
        if (event.getChat().getType() == ChatType.PRIVATE) {
            User sender = event.getMessage().getSender();
            String message = event.getContent().getContent();
            TelegramUser telegramUser = getGameLobby().getLobbyMember(sender.getUsername());

            if (message.startsWith("+")) {
                String[] allArgs = message.substring(1).split(" ");
                String command = allArgs[0];

                if (command.equalsIgnoreCase("help")) {
                    getGameLobby().sendMessage("Uno Command Menu:\n" +
                            "+help - View the help menu\n" +
                            "+deck - View your deck\n" +
                            "+scores - View the scores\n" +
                            "+colour - View the colour picker");
                } else if (command.equalsIgnoreCase("deck")) {
                    if (getGameState() == GameState.INGAME) {
                        sendDeck(telegramUser);
                    } else {
                        TelegramBot.getChat(sender.getId()).sendMessage("The game has not started!", TelegramHook.getBot());
                    }
                } else if (command.equalsIgnoreCase("scores")) {
                    if (getGameState() == GameState.INGAME) {
                        printScores();
                    } else {
                        TelegramBot.getChat(sender.getId()).sendMessage("The game has not started!", TelegramHook.getBot());
                    }
                } else if (command.equalsIgnoreCase("colour") || command.equalsIgnoreCase("color")) {
                    if (getGameState() == GameState.INGAME) {
                        sendColourPicker(sender);
                    } else {
                        TelegramBot.getChat(sender.getId()).sendMessage("The game has not started!", TelegramHook.getBot());
                    }
                }
            } else {
                UnoCard clickedUnoCard = UnoCard.getFromString(message);
                if (clickedUnoCard != null) {
                    for (UnoCard unoCard : new ArrayList<>(playerDecks.get(sender.getId()))) {
                        if (UnoCard.isSame(unoCard, clickedUnoCard)) {
                            playCard(clickedUnoCard, sender);
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

                    if (message.equals("Draw from deck")) {
                        drawCard(sender.getUsername());
                        return;
                    }

                    if (message.startsWith("Your Score:")) {
                        telegramUser.getChat().sendMessage("Please choose a card.", TelegramHook.getBot());
                        sendDeck(telegramUser);
                        return;
                    }

                    getGameLobby().userChat(sender, message);
                }
            }
        }
    }

    @Override
    public void playerLeave(String username, int userID) {
        removePlayer(username);

        if (currentPlayer.equals(username) && checkPlayers()) {
            nextRound();
        }
    }

    @Override
    public void printScores() {
        Collections.sort(getActivePlayers());
        StringBuilder wholeMessage = new StringBuilder();
        int playerPos = 1;
        for (int i = getActivePlayers().size() - 1; i >= 0; --i) {
            TelegramUser telegramUser = getActivePlayers().get(i);
            wholeMessage.append(String.format("#%d - %s (Score: %d)\n", playerPos++, StringUtil.markdownSafe(telegramUser.getUsername()), telegramUser.getGameScore()));
        }
        getGameLobby().sendMessage(wholeMessage.toString());
    }

    public void startGame() {
        setGameState(GameState.INGAME);

        playerOrder.addAll(getActivePlayers().stream().map(TelegramUser::getUsername).collect(Collectors.toList()));
        Collections.shuffle(playerOrder);

        fillDeck();
        fillHands();
        activeUnoCard = entireDeck.remove(0);

        while (activeUnoCard.getCardColour() == CardColour.WILD || activeUnoCard.getCardValue() == CardValue.SKIP || activeUnoCard.getCardValue() == CardValue.DRAW2 || activeUnoCard.getCardValue() == CardValue.DRAW4 || activeUnoCard.getCardValue() == CardValue.REVERSE) {
            playedUnoCards.add(activeUnoCard);
            activeUnoCard = entireDeck.remove(0);
        }

        nextCardColour = activeUnoCard.getCardColour();

        nextRound();
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
                                entireDeck.add(new UnoCard(cardColor, cardValue));
                            }
                            entireDeck.add(new UnoCard(cardColor, cardValue));
                        }
                        continue;
                    }
                    case WILD:
                    case DRAW4: {
                        if (cardColor != CardColour.WILD) {
                            entireDeck.add(new UnoCard(CardColour.WILD, cardValue));
                        }
                    }
                }
            }
        }

        Collections.shuffle(entireDeck);
    }

    private void fillHands() {
        for (TelegramUser TelegramUser : getActivePlayers()) {
            List<UnoCard> deck = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                deck.add(entireDeck.remove(0));
            }
            playerDecks.put(TelegramUser.getUserID(), deck);
            updateScore(TelegramUser);
        }
    }

    private void giveCardsFromDeck(TelegramUser telegramUser, int amount) {
        List<UnoCard> givenUnoCards = new ArrayList<>();
        for (int i = 1; i <= amount; i++) {
            if (entireDeck.size() == 0) {
                getGameLobby().sendMessage("Shuffling deck...");
                entireDeck.addAll(playedUnoCards.stream().collect(Collectors.toList()));
                playedUnoCards.clear();
                Collections.shuffle(entireDeck);
            }

            givenUnoCards.add(entireDeck.remove(0));
        }

        StringBuilder givenCardsMessage = new StringBuilder();
        for (UnoCard unoCard : givenUnoCards) {
            givenCardsMessage.append(unoCard.getText()).append(" | ");
            playerDecks.get(telegramUser.getUserID()).add(unoCard);
        }

        telegramUser.getChat().sendMessage("Picked up cards: " + givenCardsMessage.toString().substring(0, givenCardsMessage.length() - 3), TelegramHook.getBot());
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

    public void nextRound() {
        secondsSincePlay = 0;

        for (TelegramUser telegramUser : getActivePlayers()) {
            if (playerDecks.get(telegramUser.getUserID()).size() == 0) {
                // WINNER WINNER CHICKEN DINNER
                winner(telegramUser.getUsername());
                return;
            }
        }

        currentPlayer = playerOrder.get(playerOrderIndex);

        String cardText = activeUnoCard.getText();

        if (!activeUnoCard.getCardColour().equals(nextCardColour)) {
            cardText = "Any " + nextCardColour.text + " card";
        }

        getGameLobby().sendMessage(SendableTextMessage.builder().message("➡️ *Current Card:* " + cardText + "\n" +
                "\uD83D\uDC49\uD83C\uDFFB *Current Player:* " + StringUtil.markdownSafe(currentPlayer)).parseMode(ParseMode.MARKDOWN).build());

        round++;

        sendDeck(getGameLobby().getLobbyMember(currentPlayer));
    }

    private void playCard(UnoCard card, User sender) {
        TelegramUser telegramUser = getGameLobby().getLobbyMember(sender.getUsername());
        if (currentPlayer.equalsIgnoreCase(sender.getUsername())) {
            secondsSincePlay = 0;
            if (choosingColour) {
                telegramUser.getChat().sendMessage("Please choose a colour.", TelegramHook.getBot());
                return;
            }

            switch (card.getCardValue()) {
                default: {
                    if (card.getCardColour() == CardColour.WILD) {
                        getGameLobby().sendMessage(sender.getUsername() + " played: " + card.getText());
                        activeUnoCard = card;
                        nextCardColour = card.getCardColour();
                        nextPlayerIndex();

                        if (!removeCard(playerDecks.get(sender.getId()), activeUnoCard)) {
                            getGameLobby().sendMessage("Card was not removed from deck, contact @stuntguy3000");
                            getGameLobby().stopGame();
                        } else {
                            playedUnoCards.add(activeUnoCard);
                            updateScore(getGameLobby().getLobbyMember(sender.getUsername()));
                            nextRound();
                        }
                    } else if (nextCardColour.equals(card.getCardColour()) || activeUnoCard.getCardValue().equals(card.getCardValue())) {
                        getGameLobby().sendMessage(StringUtil.markdownSafe(sender.getUsername()) + " played: " + card.getText());
                        activeUnoCard = card;
                        nextCardColour = card.getCardColour();

                        if (activeUnoCard.getCardValue() == CardValue.DRAW2) {
                            String punishedPlayer = nextPlayerIndex();
                            nextPlayerIndex();

                            getGameLobby().sendMessage(SendableTextMessage.builder().message("*" + StringUtil.markdownSafe(punishedPlayer) + " has been given two cards!*").parseMode(ParseMode.MARKDOWN).build());
                            giveCardsFromDeck(getGameLobby().getLobbyMember(punishedPlayer), 2);
                        } else if (activeUnoCard.getCardValue() == CardValue.REVERSE) {
                            getGameLobby().sendMessage(SendableTextMessage.builder().message("*Player order has been reversed!*").parseMode(ParseMode.MARKDOWN).build());
                            increasePlayerIndex = !increasePlayerIndex;

                            nextPlayerIndex();
                        } else if (activeUnoCard.getCardValue() == CardValue.SKIP) {
                            String punishedPlayer = nextPlayerIndex();

                            getGameLobby().sendMessage(SendableTextMessage.builder().message("*" + StringUtil.markdownSafe(punishedPlayer) + " has been skipped!*").parseMode(ParseMode.MARKDOWN).build());

                            nextPlayerIndex();
                        } else {
                            nextPlayerIndex();
                        }

                        if (!removeCard(playerDecks.get(sender.getId()), activeUnoCard)) {
                            getGameLobby().sendMessage("Card was not removed from deck, contact @stuntguy3000");
                            getGameLobby().stopGame();
                        } else {
                            playedUnoCards.add(activeUnoCard);
                            updateScore(getGameLobby().getLobbyMember(sender.getUsername()));
                            nextRound();
                        }
                    } else {
                        telegramUser.getChat().sendMessage("You have chosen an invalid card!", TelegramHook.getBot());
                        sendDeck(getGameLobby().getLobbyMember(sender.getUsername()));
                    }
                    return;
                }
                case DRAW4:
                case WILD: {
                    getGameLobby().sendMessage(StringUtil.markdownSafe(sender.getUsername()) + " played: " + card.getText());
                    activeUnoCard = card;
                    nextCardColour = card.getCardColour();


                    if (card.getCardValue() == CardValue.DRAW4) {
                        nextPlayerIndex();
                        String punishedPlayer = playerOrder.get(playerOrderIndex);

                        getGameLobby().sendMessage(SendableTextMessage.builder().message("*" + StringUtil.markdownSafe(punishedPlayer) + " has been given four cards!*").parseMode(ParseMode.MARKDOWN).build());
                        giveCardsFromDeck(getGameLobby().getLobbyMember(punishedPlayer), 4);
                    }

                    if (!removeCard(playerDecks.get(sender.getId()), activeUnoCard)) {
                        getGameLobby().sendMessage("Card was not removed from deck, contact @stuntguy3000");
                        getGameLobby().stopGame();
                    } else {
                        updateScore(getGameLobby().getLobbyMember(sender.getUsername()));
                        sendColourPicker(sender);
                        choosingColour = true;
                    }
                }
            }
        } else {
            telegramUser.getChat().sendMessage("It's not your turn.", TelegramHook.getBot());
        }
    }

    private boolean removeCard(List<UnoCard> unoCards, UnoCard activeUnoCard) {
        int index = 0;
        for (UnoCard unoCard : new ArrayList<>(unoCards)) {
            if (UnoCard.isSame(unoCard, activeUnoCard)) {
                unoCards.remove(index);
                return true;
            }
            index++;
        }

        return false;
    }

    private void sendColourPicker(User sender) {
        List<List<String>> buttonList = new ArrayList<>();

        buttonList.add(Arrays.asList(CardColour.RED.getText(), CardColour.BLUE.getText(), CardColour.GREEN.getText(), CardColour.YELLOW.getText()));

        TelegramBot.getChat(sender.getId()).sendMessage(SendableTextMessage.builder().message("Please choose a colour.").replyMarkup(new ReplyKeyboardMarkup(buttonList, true, true, false)).build(), TelegramHook.getBot());
    }

    private void sendDeck(TelegramUser telegramUser) {
        List<List<String>> buttonList = new ArrayList<>();
        List<String> row = new ArrayList<>();
        List<UnoCard> deck = playerDecks.get(telegramUser.getUserID());

        int index = 1;
        for (UnoCard unoCard : deck) {
            if (index == 4) {
                index = 1;
                buttonList.add(new ArrayList<>(row));
                row.clear();
            }

            row.add(unoCard.getText());
            index++;
        }

        if (row.size() > 0) {
            buttonList.add(new ArrayList<>(row));
        }

        buttonList.add(Arrays.asList("Draw from deck", "Your Score: " + telegramUser.getGameScore()));

        TelegramBot.getChat(telegramUser.getUserID()).sendMessage(SendableTextMessage.builder().message("Here are your cards.").replyMarkup(new ReplyKeyboardMarkup(buttonList, true, true, false)).build(), TelegramHook.getBot());
    }

    private void updateScore(TelegramUser telegramUser) {
        int score = 0;

        for (UnoCard unoCard : playerDecks.get(telegramUser.getUserID())) {
            score += unoCard.getCardValue().getScoreValue();
        }

        telegramUser.setGameScore(score);
    }

    private void winner(String username) {
        getActivePlayers().forEach(this::updateScore);

        SendableTextMessage.SendableTextMessageBuilder message = SendableTextMessage.builder().message("*GAME OVER!*").message("*" + StringUtil.markdownSafe(username) + "* is the winner!").parseMode(ParseMode.MARKDOWN);

        getGameLobby().sendMessage(message.replyMarkup(new ReplyKeyboardHide()).build());

        getGameLobby().stopGame();
    }
}

class UnoCard {
    @Getter
    private CardColour cardColour;
    @Getter
    private CardValue cardValue;

    public UnoCard(CardColour cardColor, CardValue cardValue) {
        this.cardColour = cardColor;
        this.cardValue = cardValue;
    }

    public static UnoCard getFromString(String message) {
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
                    return new UnoCard(cardColour, cardValue);
                }
            }
        }

        return null;
    }

    public String getText() {
        return getCardColour().getText() + " " + getCardValue().getText();
    }

    public static boolean isSame(UnoCard unoCard1, UnoCard unoCard2) {
        return unoCard1.getCardColour().equals(unoCard2.getCardColour()) && unoCard1.getCardValue().equals(unoCard2.getCardValue());
    }

    @Override
    public String toString() {
        return String.format("[Colour: %S Value: %s]", cardColour.toString(), cardValue.toString());
    }
}