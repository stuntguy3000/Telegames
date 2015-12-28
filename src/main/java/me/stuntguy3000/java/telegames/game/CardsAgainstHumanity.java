package me.stuntguy3000.java.telegames.game;

import lombok.Getter;
import lombok.Setter;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.game.Game;
import me.stuntguy3000.java.telegames.object.game.GameState;
import me.stuntguy3000.java.telegames.object.user.TelegramUser;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
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

class CAHDelay extends TimerTask {

    private CardsAgainstHumanity cardsAgainstHumanity;
    private int options;

    public CAHDelay(CardsAgainstHumanity cardsAgainstHumanity, int options) {
        this.cardsAgainstHumanity = cardsAgainstHumanity;
        this.options = options;
        new Timer().schedule(this, 2000);
    }

    @Override
    public void run() {
        cardsAgainstHumanity.tryCzar(new Random().nextInt(options - 1) + 1);
    }
}

// @author Luke Anderson | stuntguy3000
public class CardsAgainstHumanity extends Game {
    private List<CAHCard> blackCards = new ArrayList<>();
    private TelegramUser cardCzar;
    private boolean continueGame = true;
    private CAHCard currentBlackCard;
    private boolean czarChoosing = false;
    private List<CzarOption> czarOptions = new ArrayList<>();
    private HashMap<Integer, LinkedList<CAHCard>> playedCards = new HashMap<>();
    private int playerOrderIndex = 0;
    private boolean robotCzar = false;
    private int round = 1;
    private HashMap<Integer, List<CAHCard>> userCards = new HashMap<>();
    private List<CAHCard> whiteCards = new ArrayList<>();

    // Init Class
    public CardsAgainstHumanity() {
        setGameInfo(Lang.GAME_CAH_NAME, Lang.GAME_CAH_DESCRIPTION);
        setMinPlayers(2);
        setGameState(GameState.WAITING_FOR_PLAYERS);
        loadPacks();
    }

    public boolean checkPlayers() {
        if (getMinPlayers() > getActivePlayers().size()) {
            SendableTextMessage message = SendableTextMessage.builder().message(Lang.ERROR_NOT_ENOUGH_PLAYERS).parseMode(ParseMode.MARKDOWN).build();
            getGameLobby().sendMessage(message);
            getGameLobby().stopGame();
            return false;
        }

        if (getGameState() == GameState.CHOOSING) {
            int toPlay = (robotCzar ? getActivePlayers().size() : getActivePlayers().size() - 1);
            if (playedCards.size() == toPlay) {
                for (Map.Entry<Integer, LinkedList<CAHCard>> cardPlay : playedCards.entrySet()) {
                    if (cardPlay.getValue().size() < currentBlackCard.getBlanks()) {
                        return false;
                    }
                }

                setGameState(GameState.INGAME);

                String[] blackCardSplit = currentBlackCard.getRawText().split("_");

                for (Map.Entry<Integer, LinkedList<CAHCard>> playerCards : playedCards.entrySet()) {
                    int segmentID = 0;
                    CzarOption czarOption = new CzarOption(getGameLobby().getTelegramUser(playerCards.getKey()));
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
                    }
                    modifiedBlackCard.append("\n");

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

                if (!robotCzar) {
                    for (TelegramUser telegramUser : getGameLobby().getTelegramUsers()) {
                        if (cardCzar.getUserID() == telegramUser.getUserID()) {
                            czarChoosing = true;
                            TelegramBot.getChat(telegramUser.getUserID()).sendMessage(createCzarKeyboard().message(Lang.GAME_CAH_ALLPLAYED_CZAR).parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                        } else {
                            TelegramBot.getChat(telegramUser.getUserID()).sendMessage(SendableTextMessage.builder().message(Lang.GAME_CAH_ALLPLAYED).parseMode(ParseMode.MARKDOWN).replyMarkup(new ReplyKeyboardHide()).build(), TelegramHook.getBot());
                        }
                    }
                    getGameLobby().sendMessage(SendableTextMessage.builder().message(options.toString()).parseMode(ParseMode.MARKDOWN).build());
                } else {
                    getGameLobby().sendMessage(SendableTextMessage.builder().message(String.format(Lang.GAME_CAH_ALLPLAYED_CHOOSING, options.toString())).parseMode(ParseMode.MARKDOWN).build());
                    new CAHDelay(this, index);
                }
            }
        }
        return true;
    }

    @Override
    public String getGameHelp() {
        return Lang.GAME_CAH_DESCRIPTION;
    }

    @Override
    public void onTextMessageReceived(TextMessageReceivedEvent event) {
        if (event.getChat().getType() == ChatType.PRIVATE) {
            User sender = event.getMessage().getSender();
            String message = event.getContent().getContent();
            TelegramUser telegramUser = getGameLobby().getTelegramUser(sender.getUsername());

            if (message.startsWith("+")) {
                String[] allArgs = message.substring(1).split(" ");
                String command = allArgs[0];

                if (command.equalsIgnoreCase("help")) {
                    getGameLobby().sendMessage(Lang.GAME_CAH_COMMANDHELP);
                } else if (command.equalsIgnoreCase("cards")) {
                    if (getGameState() != GameState.WAITING_FOR_PLAYERS) {
                        getGameLobby().sendMessage(createUserKeyboard(telegramUser).message(Lang.GAME_GENERAL_PLAYER_CARDS).build());
                    } else {
                        TelegramBot.getChat(sender.getId()).sendMessage(Lang.ERROR_GAME_NOT_STARTED, TelegramHook.getBot());
                    }
                } else if (command.equalsIgnoreCase("score")) {
                    if (getGameState() != GameState.WAITING_FOR_PLAYERS) {
                        TelegramBot.getChat(sender.getId()).sendMessage(String.format(Lang.GAME_GENERAL_PLAYER_SCORE, telegramUser.getGameScore()), TelegramHook.getBot());
                    } else {
                        TelegramBot.getChat(sender.getId()).sendMessage(Lang.ERROR_GAME_NOT_STARTED, TelegramHook.getBot());
                    }
                }
            } else {
                if (!playCard(sender, message)) {
                    getGameLobby().userChat(telegramUser, message);
                }
            }
        }
    }

    @Override
    public void playerLeave(String username, int userID) {
        removePlayer(username);

        if (cardCzar.getUsername().equals(username) && checkPlayers()) {
            nextRound();
        }
    }

    @Override
    public void startGame() {
        setGameState(GameState.INGAME);

        Collections.shuffle(whiteCards);
        Collections.shuffle(blackCards);

        fillHands();

        if (getActivePlayers().size() == 2) {
            robotCzar = true;
        }

        nextRound();
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

    private SendableTextMessage.SendableTextMessageBuilder createUserKeyboard(TelegramUser telegramUser) {
        List<List<String>> buttonList = new ArrayList<>();
        List<CAHCard> cards = userCards.get(telegramUser.getUserID());
        List<String> row = new ArrayList<>();

        int index = 1;

        for (CAHCard card : cards) {
            if (index > 3) {
                index = 1;
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

    private void fillHands() {
        for (TelegramUser telegramUser : getActivePlayers()) {
            giveWhiteCard(telegramUser, 10);
        }
    }

    private void giveWhiteCard(TelegramUser telegramUser, int amount) {
        List<CAHCard> playerCardDeck = userCards.get(telegramUser.getUserID());

        if (playerCardDeck == null) {
            playerCardDeck = new ArrayList<>();
        }

        for (int i = 0; i < amount; i++) {
            playerCardDeck.add(whiteCards.remove(0));
        }

        userCards.put(telegramUser.getUserID(), playerCardDeck);
    }

    private boolean isPlaying(TelegramUser telegramUser) {
        for (TelegramUser gamePlayer : getActivePlayers()) {
            if (gamePlayer.getUserID() == telegramUser.getUserID()) {
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
            for (TelegramUser telegramUser : getActivePlayers()) {
                int size = userCards.get(telegramUser.getUserID()).size();
                giveWhiteCard(telegramUser, 10 - size);
            }

            playerOrderIndex++;

            if (playerOrderIndex >= getActivePlayers().size()) {
                playerOrderIndex = 0;
            }

            czarOptions.clear();
            playedCards.clear();
            czarChoosing = false;
            blackCards.add(currentBlackCard);
            currentBlackCard = blackCards.remove(0);

            if (robotCzar) {
                getGameLobby().sendMessage(SendableTextMessage.builder().message(String.format(Lang.GAME_GENERAL_STARTROUND_NUMBER, round)).parseMode(ParseMode.MARKDOWN).replyMarkup(new ReplyKeyboardHide()).build());
            } else {
                cardCzar = getActivePlayers().get(playerOrderIndex);
                getGameLobby().sendMessage(SendableTextMessage.builder().message(String.format(Lang.GAME_CAH_STARTROUND_CZAR, round, cardCzar.getUsername())).parseMode(ParseMode.MARKDOWN).replyMarkup(new ReplyKeyboardHide()).build());
            }

            setGameState(GameState.CHOOSING);

            for (TelegramUser telegramUser : getGameLobby().getTelegramUsers()) {
                if (isPlaying(telegramUser)) {
                    if (cardCzar != null && cardCzar.getUserID() == telegramUser.getUserID()) {
                        TelegramBot.getChat(telegramUser.getUserID()).sendMessage(SendableTextMessage.builder().message(TelegramEmoji.BLUE_RIGHT_ARROW.getText() + " " + currentBlackCard.getText()).build(), TelegramHook.getBot());
                        continue;
                    }
                    TelegramBot.getChat(telegramUser.getUserID()).sendMessage(createUserKeyboard(telegramUser).message(TelegramEmoji.BLUE_RIGHT_ARROW.getText() + " " + currentBlackCard.getText()).build(), TelegramHook.getBot());
                } else {
                    TelegramBot.getChat(telegramUser.getUserID()).sendMessage(SendableTextMessage.builder().message(TelegramEmoji.BLUE_RIGHT_ARROW.getText() + " " + currentBlackCard.getText()).build(), TelegramHook.getBot());
                }
            }

            if (currentBlackCard.getBlanks() > 1) {
                getGameLobby().sendMessage(SendableTextMessage.builder().message(String.format(Lang.GAME_CAH_WHITECARDS, currentBlackCard.getBlanks())).parseMode(ParseMode.MARKDOWN).build());
            }

            round++;
        }
    }

    private boolean playCard(User sender, String message) {
        if (czarChoosing) {
            if (sender.getId() == cardCzar.getUserID()) {
                try {
                    int number = TelegramEmoji.getNumber(TelegramEmoji.fromString(message));
                    if (!tryCzar(number)) {
                        TelegramBot.getChat(sender.getId()).sendMessage(Lang.ERROR_INVALID_SELECTION, TelegramHook.getBot());
                    }
                } catch (Exception ex) {
                }
                return true;
            }
        }

        if (!robotCzar) {
            if (sender.getId() == cardCzar.getUserID()) {
                return false;
            }
        }

        CAHCard cahCard = null;

        for (CAHCard playerCard : userCards.get(sender.getId())) {
            if (playerCard.getText().equalsIgnoreCase(message)) {
                cahCard = playerCard;
            }
        }

        if (cahCard != null) {
            LinkedList<CAHCard> cards = new LinkedList<>();

            if (playedCards.containsKey(sender.getId())) {
                cards = playedCards.get(sender.getId());
            }

            int cardsNeeded = currentBlackCard.getBlanks();

            if (cards.size() < cardsNeeded) {
                for (CAHCard userCard : cards) {
                    if (userCard.getText().equals(cahCard.getText())) {
                        TelegramBot.getChat(sender.getId()).sendMessage(Lang.ERROR_ALREADY_PLAYED_CARD, TelegramHook.getBot());
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
                    getGameLobby().sendMessage(SendableTextMessage.builder().message(Lang.GAME_CAH_USERPLAY).parseMode(ParseMode.MARKDOWN).build());
                    checkPlayers();
                } else {
                    TelegramBot.getChat(sender.getId()).sendMessage(createUserKeyboard(getGameLobby().getTelegramUser(sender.getUsername())).message(String.format(Lang.GAME_CAH_PLAY_MORE, cardsNeeded - cards.size())).parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
                }
            } else {
                TelegramBot.getChat(sender.getId()).sendMessage(Lang.GAME_GENERAL_NOT_TURN, TelegramHook.getBot());
            }
            return true;
        }

        return false;
    }

    boolean tryCzar(int number) {
        TelegramUser winner = null;
        for (CzarOption czarOption : czarOptions) {
            if (czarOption.getOptionNumber() == number) {
                winner = czarOption.getOwner();
            }
        }

        if (winner != null) {
            getGameLobby().sendMessage(SendableTextMessage.builder().message(String.format(Lang.GAME_GENERAL_WIN_ROUND, StringUtil.markdownSafe(winner.getUsername()))).parseMode(ParseMode.MARKDOWN).build());
            winner.setGameScore(winner.getGameScore() + 1);

            TelegramUser gameWinner = null;
            for (TelegramUser telegramUser : getActivePlayers()) {
                if (telegramUser.getGameScore() >= 10) {
                    gameWinner = telegramUser;
                }
            }

            if (gameWinner != null) {
                continueGame = false;
                getGameLobby().stopGame();
            } else {
                nextRound();
            }
            return true;
        } else {
            return false;
        }
    }
}

class CzarOption {
    @Getter
    @Setter
    private int optionNumber;
    @Getter
    private TelegramUser owner;
    @Getter
    @Setter
    private String text;

    CzarOption(TelegramUser owner) {
        this.owner = owner;
    }
}
