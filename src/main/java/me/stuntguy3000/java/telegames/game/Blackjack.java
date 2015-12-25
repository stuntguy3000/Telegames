package me.stuntguy3000.java.telegames.game;

import lombok.Getter;
import lombok.Setter;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.Game;
import me.stuntguy3000.java.telegames.object.LobbyMember;
import me.stuntguy3000.java.telegames.object.exception.GameStartException;
import me.stuntguy3000.java.telegames.util.GameState;
import me.stuntguy3000.java.telegames.util.StringUtil;
import me.stuntguy3000.java.telegames.util.cards.Card;
import me.stuntguy3000.java.telegames.util.cards.Deck;
import me.stuntguy3000.java.telegames.util.cards.Rank;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.ReplyMarkup;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardHide;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardMarkup;
import pro.zackpollard.telegrambot.api.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

// @author Luke Anderson | stuntguy3000
public class Blackjack extends Game {
    private HashMap<Integer, Integer> aceCount = new HashMap<>();
    private ReplyKeyboardMarkup aceKeyboard;
    @Getter
    private List<LobbyMember> activePlayers = new ArrayList<>();
    private LobbyMember currentPlayer;
    private int currentRound = 1;
    private Boolean dealerStand = false;
    private Deck deck;
    @Getter
    @Setter
    private GameState gameState;
    private int maxPlayers = 9;
    private int maxRounds = 5;
    private int minPlayers = 2;
    private HashMap<Integer, Integer> playerCardValues = new HashMap<>();
    private HashMap<Integer, List<BlackjackCard>> playerCards = new HashMap<>();
    private LobbyMember roundDealer;
    private int roundDealerIndex = 0;
    private int secondsSincePlay = 0;
    private List<LobbyMember> toPlay = new ArrayList<>();

    public Blackjack() {
        setGameInfo("Blackjack", "A simple card game, closest to 21 wins, but don't bust!");

        aceKeyboard = ReplyKeyboardMarkup.builder().addRow("Ace is 1", "Ace is 11").oneTime(true).build();

        setGameState(GameState.WAITING_FOR_PLAYERS);
    }

    private void calculateValue(LobbyMember lobbyMember) {
        int value = 0;

        for (BlackjackCard card : playerCards.get(lobbyMember.getUserID())) {
            value += card.getActualValue();
        }

        playerCardValues.put(lobbyMember.getUserID(), value);
    }

    private boolean checkAces(LobbyMember lobbyMember) {
        if (aceCount.containsKey(lobbyMember.getUserID())) {
            sendAceKeyboard(lobbyMember);
            return false;
        } else {
            return true;
        }
    }

    private void checkPlayers() {
        switch (getGameState()) {
            case INGAME: {
                if (minPlayers > getActivePlayers().size()) {
                    SendableTextMessage message = SendableTextMessage.builder().message("*There are not enough players to continue!*").parseMode(ParseMode.MARKDOWN).build();
                    getGameLobby().sendMessage(message);
                    getGameLobby().stopGame();
                }
            }
        }
    }

    private void chooseAce(LobbyMember lobbyMember, int value) {
        if (aceCount.containsKey(lobbyMember.getUserID())) {
            List<BlackjackCard> cards = playerCards.get(lobbyMember.getUserID());

            int index = 0;
            for (BlackjackCard blackjackCard : new ArrayList<>(cards)) {
                if (!blackjackCard.isModified() && blackjackCard.getCard().getRank() == Rank.ACE) {
                    cards.remove(blackjackCard);
                    cards.add(index, new BlackjackCard(blackjackCard, value));
                }

                index++;
            }

            int userAces = aceCount.get(lobbyMember.getUserID()) - 1;
            if (userAces > 0) {
                aceCount.put(lobbyMember.getUserID(), userAces);
            } else {
                aceCount.remove(lobbyMember.getUserID());
            }

            stand(lobbyMember);
        }
    }

    private void dealerPlay() {
        currentPlayer = roundDealer;

        getGameLobby().sendMessage(SendableTextMessage.builder().message("*It's your turn, " + StringUtil.markdownSafe(currentPlayer.getUsername()) + "*").parseMode(ParseMode.MARKDOWN).build());

        sendHand(currentPlayer, getKeyboard(currentPlayer));
    }

    @Override
    public void endGame() {
        SendableTextMessage.SendableTextMessageBuilder messageBuilder = SendableTextMessage.builder().message("The game of Blackjack has ended!").replyMarkup(ReplyKeyboardHide.builder().build());

        getGameLobby().sendMessage(messageBuilder.build());
        printScores();
    }

    @Override
    public String getGameHelp() {
        return "A simple card game, closest to 21 wins, but don't bust!";
    }

    @Override
    public void onSecond() {
        secondsSincePlay++;
        if (secondsSincePlay == 30) {
            getGameLobby().sendMessage(SendableTextMessage.builder().message("Please make a play @" + StringUtil.markdownSafe(currentPlayer.getUsername())).build());
        } else if (secondsSincePlay == 40) {
            getGameLobby().sendMessage(SendableTextMessage.builder().message("*" + StringUtil.markdownSafe(currentPlayer.getUsername()) + " ran out of time!*").parseMode(ParseMode.MARKDOWN).build());
            stand(currentPlayer);
        }
    }

    @Override
    public void onTextMessageReceived(TextMessageReceivedEvent event) {
        if (event.getChat().getType() == ChatType.PRIVATE) {
            User sender = event.getMessage().getSender();
            String message = event.getContent().getContent();
            LobbyMember lobbyMember = getGameLobby().getLobbyMember(sender.getUsername());

            switch (message) {
                case "Hit":
                    hit(lobbyMember, getKeyboard(getGameLobby().getLobbyMember(sender.getUsername())));
                    break;
                case "Stand":
                    stand(lobbyMember);
                    break;
                case "Ace is 1":
                    chooseAce(lobbyMember, 1);
                    break;
                case "Ace is 11":
                    chooseAce(lobbyMember, 11);
                    break;
                default:
                    getGameLobby().userChat(sender, message);
                    break;
            }
        }
    }

    @Override
    public boolean playerJoin(LobbyMember player) {
        if (getGameState() == GameState.WAITING_FOR_PLAYERS) {
            activePlayers.add(player);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void playerLeave(String username, int userID) {
        playerCardValues.remove(userID);
        playerCards.remove(userID);
        removePlayer(username);
        checkPlayers();

        if (currentPlayer.getUserID() == userID) {
            SendableTextMessage message = SendableTextMessage.builder().message("*The current player quit!*").parseMode(ParseMode.MARKDOWN).build();
            getGameLobby().sendMessage(message);

            nextRound();
        } else if (roundDealer.getUserID() == userID) {
            SendableTextMessage message = SendableTextMessage.builder().message("*The dealer quit!*").parseMode(ParseMode.MARKDOWN).build();
            getGameLobby().sendMessage(message);

            nextRound();
        }

    }

    @Override
    public void tryStartGame() throws GameStartException {
        if (activePlayers.size() >= minPlayers) {
            if (activePlayers.size() > maxPlayers) {
                throw new GameStartException("Too many players! Maximum: " + maxPlayers);
            } else {
                startGame();
            }
        } else {
            throw new GameStartException("Not enough players! Required: " + minPlayers);
        }
    }

    private void fillDeck() {
        deck = new Deck();
        deck.shuffleCards();
    }

    private void fillHands() {
        for (LobbyMember lobbyMember : getActivePlayers()) {
            giveCard(lobbyMember, 2);
        }
    }

    public ReplyMarkup getKeyboard(LobbyMember lobbyMember) {
        int score = 0;

        if (playerCardValues.containsKey(lobbyMember.getUserID())) {
            score = playerCardValues.get(lobbyMember.getUserID());

        }

        if (score < 21) {
            return ReplyKeyboardMarkup.builder().addRow("Hit", "Stand").oneTime(false).build();
        } else {
            return ReplyKeyboardMarkup.builder().addRow("Stand").oneTime(false).build();
        }
    }

    private void giveCard(LobbyMember lobbyMember, int amount) {
        List<BlackjackCard> playerCardDeck = playerCards.get(lobbyMember.getUserID());

        if (playerCardDeck == null) {
            playerCardDeck = new ArrayList<>();
        }

        for (int i = 0; i < amount; i++) {
            Card card = deck.getCard();

            if (card.getRank() == Rank.ACE) {
                int playerAceCount = 0;

                if (aceCount.containsKey(lobbyMember.getUserID())) {
                    playerAceCount = aceCount.get(lobbyMember.getUserID());
                }

                aceCount.put(lobbyMember.getUserID(), ++playerAceCount);
            }

            playerCardDeck.add(new BlackjackCard(card));
        }

        playerCards.put(lobbyMember.getUserID(), playerCardDeck);
        calculateValue(lobbyMember);
    }

    private void hit(LobbyMember lobbyMember, ReplyMarkup replyMarkup) {
        if (currentPlayer.getUserID() == lobbyMember.getUserID()) {
            if (playerCardValues.get(currentPlayer.getUserID()) >= 21) {
                TelegramBot.getChat(lobbyMember.getUserID()).sendMessage(SendableTextMessage.builder().message("*You have busted!*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());

                stand(lobbyMember);
            } else {
                getGameLobby().sendMessage(SendableTextMessage.builder().message("*" + StringUtil.markdownSafe(lobbyMember.getUsername()) + " chose to hit.*").parseMode(ParseMode.MARKDOWN).build());

                giveCard(lobbyMember, 1);
                sendHand(lobbyMember, replyMarkup);
            }
        } else {
            TelegramBot.getChat(lobbyMember.getUserID()).sendMessage(SendableTextMessage.builder().message("*It's not your turn!*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
        }
    }

    private void nextPlayer() {
        if (dealerStand) {
            getGameLobby().sendMessage(SendableTextMessage.builder().message("*All players have chosen to stand!*").parseMode(ParseMode.MARKDOWN).build());

            calculateValue(roundDealer);
            int dealerScore = playerCardValues.get(roundDealer.getUserID());

            if (dealerScore > 21) {
                getGameLobby().sendMessage(SendableTextMessage.builder().message("*The dealer busted!*\n\nAll players have been given 1 Game Point!").parseMode(ParseMode.MARKDOWN).build());

                for (LobbyMember lobbyMember : getActivePlayers()) {
                    if (!(lobbyMember.getUserID() == roundDealer.getUserID())) {
                        int score = lobbyMember.getGameScore();
                        lobbyMember.setGameScore(score + 1);
                    }
                }
            } else {
                StringBuilder playerScoreList = new StringBuilder();

                for (LobbyMember lobbyMember : getActivePlayers()) {
                    if (!(lobbyMember.getUserID() == roundDealer.getUserID())) {
                        int cardScore = playerCardValues.get(lobbyMember.getUserID());
                        int score = lobbyMember.getGameScore();

                        if (cardScore >= dealerScore && cardScore <= 21) {
                            playerScoreList.append(StringUtil.markdownSafe(lobbyMember.getUsername())).append("'s card score was ").append(cardScore).append(". (+1 Game Points)\n");
                            lobbyMember.setGameScore(score + 1);
                        } else {
                            playerScoreList.append(StringUtil.markdownSafe(lobbyMember.getUsername())).append("'s card score was ").append(cardScore).append(". (0 Game Points)\n");
                        }
                    }
                }

                getGameLobby().sendMessage(SendableTextMessage.builder().message("*The dealer's score was " + dealerScore + "!*\n\n" + playerScoreList).parseMode(ParseMode.MARKDOWN).build());
            }

            nextRound();
        } else {
            if (toPlay.size() > 0) {
                currentPlayer = toPlay.remove(0);

                getGameLobby().sendMessage(SendableTextMessage.builder().message("*It's your turn, " + StringUtil.markdownSafe(currentPlayer.getUsername()) + "*").parseMode(ParseMode.MARKDOWN).build());

                sendHand(currentPlayer, getKeyboard(currentPlayer));
            } else {
                dealerPlay();
            }
        }
    }

    private void nextRound() {
        secondsSincePlay = 0;
        if (currentRound > maxRounds) {
            getGameLobby().stopGame();
        } else {
            dealerStand = false;
            roundDealerIndex++;

            playerCards.clear();
            playerCardValues.clear();

            if (roundDealerIndex >= getActivePlayers().size()) {
                roundDealerIndex = 0;
            }

            roundDealer = getActivePlayers().get(roundDealerIndex);

            for (LobbyMember lobbyMember : getActivePlayers()) {
                if (!(roundDealer.getUserID() == (lobbyMember.getUserID()))) {
                    toPlay.add(lobbyMember);
                }
            }

            getGameLobby().sendMessage(SendableTextMessage.builder().message("*Starting Round " + currentRound + "/" + maxRounds + "*\n" +
                    "*Dealer:* " + StringUtil.markdownSafe(roundDealer.getUsername())).parseMode(ParseMode.MARKDOWN).build());

            fillDeck();
            fillHands();
            nextPlayer();

            currentRound++;
        }
    }

    private void printScores() {
        Collections.sort(activePlayers);
        StringBuilder wholeMessage = new StringBuilder();
        int playerPos = 1;
        for (int i = 0; i < getActivePlayers().size(); i++) {
            LobbyMember lobbyMember = getActivePlayers().get(i);
            wholeMessage.append(String.format("#%d - %s (Score: %d)\n", playerPos++, StringUtil.markdownSafe(lobbyMember.getUsername()), lobbyMember.getGameScore()));
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

    private void sendAceKeyboard(LobbyMember lobbyMember) {
        secondsSincePlay = 0;
        TelegramBot.getChat(lobbyMember.getUserID()).sendMessage(SendableTextMessage.builder().message("*You have an Ace! Please choose its value.*").parseMode(ParseMode.MARKDOWN).replyMarkup(aceKeyboard).build(), TelegramHook.getBot());
    }

    private void sendHand(LobbyMember player, ReplyMarkup replyMarkup) {
        secondsSincePlay = 0;
        StringBuilder stringBuilder = new StringBuilder();

        for (BlackjackCard card : playerCards.get(player.getUserID())) {
            stringBuilder.append(card.getCard().toString());
            stringBuilder.append(" ");
        }

        TelegramBot.getChat(player.getUserID()).sendMessage(SendableTextMessage.builder().message("*Here is your hand: *\n" + stringBuilder.toString()).parseMode(ParseMode.MARKDOWN).replyMarkup(replyMarkup).build(), TelegramHook.getBot());
    }

    private void stand(LobbyMember lobbyMember) {
        if (currentPlayer.getUserID() == lobbyMember.getUserID()) {
            secondsSincePlay = 0;
            if (checkAces(currentPlayer)) {
                getGameLobby().sendMessage(SendableTextMessage.builder().message("*" + StringUtil.markdownSafe(lobbyMember.getUsername()) + " chose to stand.*").parseMode(ParseMode.MARKDOWN).build());

                calculateValue(currentPlayer);

                TelegramBot.getChat(lobbyMember.getUserID()).sendMessage(SendableTextMessage.builder().message("Card Score: " + playerCardValues.get(currentPlayer.getUserID()) + "\n").parseMode(ParseMode.MARKDOWN).replyMarkup(ReplyKeyboardHide.builder().build()).build(), TelegramHook.getBot());

                if (roundDealer.getUserID() == lobbyMember.getUserID()) {
                    dealerStand = true;
                }

                nextPlayer();
            }
        } else {
            TelegramBot.getChat(lobbyMember.getUserID()).sendMessage(SendableTextMessage.builder().message("*It's not your turn!*").parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
        }
    }

    public void startGame() {
        setGameState(GameState.INGAME);

        maxRounds = getActivePlayers().size() * 3;

        nextRound();
    }
}

class BlackjackCard {
    @Getter
    private int actualValue;
    @Getter
    private Card card;
    @Getter
    private boolean modified;

    public BlackjackCard(Card card) {
        this.card = card;
        this.actualValue = card.getValue();
        modified = false;
    }

    public BlackjackCard(BlackjackCard card, int value) {
        this.card = card.getCard();
        this.actualValue = value;
        modified = true;
    }

    public void setActualValue(int value) {
        actualValue = value;
        modified = true;
    }
}
    