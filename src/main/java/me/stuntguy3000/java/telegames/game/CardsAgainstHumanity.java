package me.stuntguy3000.java.telegames.game;

import lombok.Getter;
import lombok.Setter;
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
import java.util.*;

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
    @Getter
    @Setter
    private int blanks = 0;
    @Setter
    @Getter
    private CAHCardType cahCardType;
    @Setter
    private String text;

    public CAHCard(String text, CAHCardType cahCardType) {
        this.text = text;
        this.cahCardType = cahCardType;

        for (char character : text.toCharArray()) {
            if (character == '_') {
                ++blanks;
            }
        }
    }

    public String getRawText() {
        return text;
    }

    public String getText() {
        return text.replace("_", "______");
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
    private List<CAHCard> blackCards = new ArrayList<>();
    private LobbyMember cardCzar;
    private boolean continueGame = true;
    private CAHCard currentBlackCard;
    private boolean czarChoosing = false;
    private List<CzarOption> czarOptions = new ArrayList<>();
    private GameState gameState;
    private int maxPlayers = 8;
    private int minPlayers = 3;
    private HashMap<Integer, LinkedList<CAHCard>> playedCards = new HashMap<>();
    private int playerOrderIndex = 0;
    private int round = 1;
    private HashMap<Integer, List<CAHCard>> userCards = new HashMap<>();
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

        if (gameState == GameState.CHOOSING) {
            if (playedCards.size() == activePlayers.size() - 1) {
                gameState = GameState.INGAME;

                String[] blackCardSplit = currentBlackCard.getRawText().split("_");

                for (Map.Entry<Integer, LinkedList<CAHCard>> playerCards : playedCards.entrySet()) {
                    int segmentID = 0;
                    CzarOption czarOption = new CzarOption(getGameLobby().getLobbyMember(playerCards.getKey()));
                    StringBuilder modifiedBlackCard = new StringBuilder();

                    modifiedBlackCard.append(blackCardSplit[segmentID]);

                    for (CAHCard playerCard : playerCards.getValue()) {
                        segmentID++;
                        modifiedBlackCard.append("*");
                        modifiedBlackCard.append(playerCard.getText());
                        modifiedBlackCard.append("*");
                        if (segmentID < blackCardSplit.length) {
                            modifiedBlackCard.append(blackCardSplit[segmentID]);
                        }
                        modifiedBlackCard.append("\n");
                    }

                    czarOption.setText(modifiedBlackCard.toString());
                    czarOptions.add(czarOption);
                }

                StringBuilder options = new StringBuilder();

                Collections.shuffle(czarOptions);

                int index = 1;
                for (CzarOption czarOption : czarOptions) {
                    czarOption.setOptionNumber(index);
                    options.append(TelegramEmoji.getNumberBlock(index).getText());
                    options.append(" ");
                    options.append(czarOption.getText());
                    index++;
                }

                for (LobbyMember lobbyMember : getGameLobby().getLobbyMembers()) {
                    if (cardCzar.getUserID() == lobbyMember.getUserID()) {
                        czarChoosing = true;
                        TelegramBot.getChat(lobbyMember.getUserID()).sendMessage(createCzarKeyboard().message(TelegramEmoji.GREEN_BOX_TICK.getText() + " *All users have played.*\n*Please choose a winner!*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                    } else {
                        TelegramBot.getChat(lobbyMember.getUserID()).sendMessage(SendableTextMessage.builder().message(TelegramEmoji.GREEN_BOX_TICK.getText() + "*All users have played.*").parseMode(ParseMode.MARKDOWN).replyMarkup(new ReplyKeyboardHide()).build(), TelegramHook.getBot());
                    }
                }

                getGameLobby().sendMessage(SendableTextMessage.builder().message(options.toString()).parseMode(ParseMode.MARKDOWN).build());
            }
        }
        return true;
    }

    private SendableTextMessage.SendableTextMessageBuilder createCzarKeyboard() {
        List<List<String>> buttonList = new ArrayList<>();
        List<String> row = new ArrayList<>();

        int index = 1;

        for (int i = 1; i <= czarOptions.size(); i++) {
            if (index == 5) {
                index = 1;
                buttonList.add(new ArrayList<>(row));
                row.clear();
            }

            row.add(TelegramEmoji.getNumberBlock(index).getText());
            index++;
        }

        if (row.size() > 0) {
            buttonList.add(new ArrayList<>(row));
        }

        return SendableTextMessage.builder().replyMarkup(new ReplyKeyboardMarkup(buttonList, true, true, false));
    }

    private SendableTextMessage.SendableTextMessageBuilder createUserKeyboard(LobbyMember lobbyMember) {
        List<List<String>> buttonList = new ArrayList<>();
        List<CAHCard> cards = userCards.get(lobbyMember.getUserID());
        List<String> row = new ArrayList<>();

        int index = 1;

        for (CAHCard card : cards) {
            if (index > 2) {
                index = 0;
                buttonList.add(new ArrayList<>(row));
                row.clear();
            }

            buttonList.add(new ArrayList<>(Collections.singletonList(card.getText())));
            index++;
        }

        if (row.size() > 0) {
            buttonList.add(new ArrayList<>(row));
        }

        return SendableTextMessage.builder().replyMarkup(new ReplyKeyboardMarkup(buttonList, true, true, false));
    }

    @Override
    public void endGame() {
        SendableTextMessage.SendableTextMessageBuilder messageBuilder = SendableTextMessage.builder().message("The game of CardsAgainstHumanity has ended!").replyMarkup(ReplyKeyboardHide.builder().build());

        getGameLobby().sendMessage(messageBuilder.build());
        printScores();
    }

    @Override
    public String getGameHelp() {
        return "The most fun and offensive card game ever known.\n\nMessage @" + TelegramHook.getBot().getBotUsername() + "+help while in-game for a list of commands.";
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
                            "+cards - View your cards\n" +
                            "+score - View your cards");
                } else if (command.equalsIgnoreCase("cards")) {
                    if (gameState != GameState.WAITING_FOR_PLAYERS) {
                        getGameLobby().sendMessage(createUserKeyboard(lobbyMember).message("Here are your cards:").build());
                    } else {
                        getGameLobby().sendMessage("The game has not started!");
                    }
                } else if (command.equalsIgnoreCase("score")) {
                    if (gameState != GameState.WAITING_FOR_PLAYERS) {
                        getGameLobby().sendMessage("Your score is " + lobbyMember.getGameScore());
                    } else {
                        getGameLobby().sendMessage("The game has not started!");
                    }
                }
            } else {
                if (!playCard(sender, message)) {
                    getGameLobby().userChat(sender, message);
                }
            }
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
        removePlayer(username);

        if (cardCzar.getUsername().equals(username) && checkPlayers()) {
            nextRound();
        }
    }

    // Required Methods
    @Override
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
            giveWhiteCard(lobbyMember, 10);
        }
    }

    private void giveWhiteCard(LobbyMember lobbyMember, int amount) {
        List<CAHCard> playerCardDeck = userCards.get(lobbyMember.getUserID());

        if (playerCardDeck == null) {
            playerCardDeck = new ArrayList<>();
        }

        for (int i = 0; i < amount; i++) {
            playerCardDeck.add(whiteCards.remove(0));
        }

        userCards.put(lobbyMember.getUserID(), playerCardDeck);
    }

    private boolean isPlaying(LobbyMember lobbyMember) {
        for (LobbyMember gamePlayer : activePlayers) {
            if (gamePlayer.getUserID() == lobbyMember.getUserID()) {
                return true;
            }
        }

        return false;
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
                                    continue;
                                }
                                case BLACKCARDS: {
                                    cahCardPack.addCard(packLine.replaceAll("~", "\n"), CAHCardType.BLACK);
                                    continue;
                                }
                                case WHITECARDS: {
                                    cahCardPack.addCard(packLine.replaceAll("~", "\n"), CAHCardType.WHITE);
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
            for (LobbyMember lobbyMember : activePlayers) {
                int size = userCards.get(lobbyMember.getUserID()).size();
                giveWhiteCard(lobbyMember, 10 - size);
            }

            playerOrderIndex++;

            if (playerOrderIndex >= activePlayers.size()) {
                playerOrderIndex = 0;
            }

            czarOptions.clear();
            playedCards.clear();
            czarChoosing = false;
            blackCards.add(currentBlackCard);
            cardCzar = activePlayers.get(playerOrderIndex);
            currentBlackCard = blackCards.remove(0);

            getGameLobby().sendMessage(SendableTextMessage.builder().message(TelegramEmoji.BOOK.getText() + "* Starting Round " + round + "*\n" +
                    "*Card Czar:* " + cardCzar.getUsername()).parseMode(ParseMode.MARKDOWN).replyMarkup(new ReplyKeyboardHide()).build());

            StringBuilder extraCards = new StringBuilder();

            if (currentBlackCard.getBlanks() > 1) {
                extraCards.append("\nPlease play ").append(currentBlackCard.getBlanks()).append(" white cards.");
            }

            gameState = GameState.CHOOSING;

            for (LobbyMember lobbyMember : getGameLobby().getLobbyMembers()) {
                if (isPlaying(lobbyMember) && !(cardCzar.getUserID() == lobbyMember.getUserID())) {
                    if (extraCards.toString().isEmpty()) {
                        TelegramBot.getChat(lobbyMember.getUserID()).sendMessage(createUserKeyboard(lobbyMember).message(currentBlackCard.getText()).build(), TelegramHook.getBot());
                    } else {
                        TelegramBot.getChat(lobbyMember.getUserID()).sendMessage(createUserKeyboard(lobbyMember).message(currentBlackCard.getText() + extraCards.toString()).build(), TelegramHook.getBot());
                    }
                } else {
                    TelegramBot.getChat(lobbyMember.getUserID()).sendMessage(SendableTextMessage.builder().message(currentBlackCard.getText()).build(), TelegramHook.getBot());
                }
            }

            round++;
        }
    }

    private boolean playCard(User sender, String message) {
        if (cardCzar.getUserID() != sender.getId()) {
            CAHCard cahCard = null;

            for (CAHCard playerCard : userCards.get(sender.getId())) {
                if (playerCard.getText().equalsIgnoreCase(message)) {
                    cahCard = playerCard;
                }
            }

            if (cahCard != null && !czarChoosing) {
                LinkedList<CAHCard> cards = new LinkedList<>();

                if (playedCards.containsKey(sender.getId())) {
                    cards = playedCards.get(sender.getId());
                }

                int cardsNeeded = currentBlackCard.getBlanks();

                if (cards.size() < cardsNeeded) {
                    for (CAHCard userCard : cards) {
                        if (userCard.getText().equals(cahCard.getText())) {
                            TelegramBot.getChat(sender.getId()).sendMessage("You cannot play this card again!", TelegramHook.getBot());
                            return true;
                        }
                    }

                    cards.add(cahCard);
                    playedCards.put(sender.getId(), cards);

                    // Remove from players hand
                    List<CAHCard> userCards = this.userCards.get(sender.getId());

                    for (CAHCard userCard : new ArrayList<>(userCards)) {
                        if (userCard.getText().equals(cahCard.getText())) {
                            userCards.remove(userCard);
                        }
                    }

                    if (cards.size() == cardsNeeded) {
                        getGameLobby().sendMessage(SendableTextMessage.builder().message(TelegramEmoji.GREEN_BOX_TICK.getText() + " *" + StringUtil.markdownSafe(sender.getUsername()) + " has played.*").parseMode(ParseMode.MARKDOWN).build());
                        checkPlayers();
                    } else {
                        TelegramBot.getChat(sender.getId()).sendMessage(createUserKeyboard(getGameLobby().getLobbyMember(sender.getUsername())).message("*Please play " + (cardsNeeded - cards.size()) + " more card(s).*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                    }
                } else {
                    TelegramBot.getChat(sender.getId()).sendMessage(TelegramEmoji.RED_CROSS.getText() + "You cannot play a card now!", TelegramHook.getBot());
                }
            } else {
                return false;
            }

            return true;
        } else if (czarChoosing) {
            if (sender.getId() == cardCzar.getUserID()) {
                LobbyMember winner = null;

                int number = TelegramEmoji.getNumber(TelegramEmoji.fromString(message));

                for (CzarOption czarOption : czarOptions) {
                    if (czarOption.getOptionNumber() == number) {
                        winner = czarOption.getOwner();
                    }
                }

                if (winner != null) {
                    getGameLobby().sendMessage(SendableTextMessage.builder().message("*" + StringUtil.markdownSafe(winner.getUsername()) + " won the round!*").parseMode(ParseMode.MARKDOWN).build());
                    winner.setGameScore(winner.getGameScore() + 1);

                    LobbyMember gameWinner = null;
                    for (LobbyMember lobbyMember : activePlayers) {
                        if (lobbyMember.getGameScore() >= 10) {
                            gameWinner = lobbyMember;
                        }
                    }

                    if (gameWinner != null) {
                        continueGame = false;
                        getGameLobby().stopGame();
                    } else {
                        nextRound();
                    }
                } else {
                    TelegramBot.getChat(sender.getId()).sendMessage("You have chosen an invalid card!", TelegramHook.getBot());
                }
                return true;
            }
        }

        return false;
    }

    private void printScores() {
        Collections.sort(activePlayers);
        StringBuilder wholeMessage = new StringBuilder();
        int playerPos = 1;
        for (LobbyMember lobbyMember : activePlayers) {
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


    private void startGame() {
        gameState = GameState.INGAME;

        Collections.shuffle(whiteCards);
        Collections.shuffle(blackCards);

        fillHands();
        nextRound();
    }
}

class CzarOption {
    @Getter
    @Setter
    private int optionNumber;
    @Getter
    private LobbyMember owner;
    @Getter
    @Setter
    private String text;

    CzarOption(LobbyMember owner) {
        this.owner = owner;
    }
}
