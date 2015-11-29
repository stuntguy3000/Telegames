package me.stuntguy3000.java.telegames.game;

import lombok.Getter;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.Game;
import me.stuntguy3000.java.telegames.object.Player;
import me.stuntguy3000.java.telegames.object.PlayerData;
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
import java.util.HashMap;
import java.util.List;

class BlackjackCard {
    @Getter
    private Card card;
    @Getter
    private int actualValue;
    @Getter
    private boolean modified;

    public BlackjackCard(Card card, int actualValue) {
        this.card = card;
        this.actualValue = actualValue;
        modified = false;
    }

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

// @author Luke Anderson | stuntguy3000
public class Blackjack extends Game {
    private int minPlayers = 2;
    private int maxPlayers = 9;
    private int maxRounds = 5;
    private int currentRound = 1;
    private Deck deck;
    private PlayerData roundDealer;
    private PlayerData currentPlayer;
    private List<PlayerData> toPlay = new ArrayList<>();
    private HashMap<Integer, List<BlackjackCard>> playerCards = new HashMap<>();
    private HashMap<Integer, Integer> playerCardValues = new HashMap<>();
    private int roundDealerIndex = 0;
    private ReplyKeyboardMarkup standardKeyboard;
    private HashMap<Integer, Integer> aceCount = new HashMap<>();
    private ReplyKeyboardMarkup aceKeyboard;
    private Boolean dealerFold = false;

    public Blackjack() {
        setInfo("Blackjack", "A simple card game, closest to 21 wins, but don't bust!");

        standardKeyboard = ReplyKeyboardMarkup.builder()
                .addRow("Knock", "Fold")
                .oneTime(false)
                .build();

        aceKeyboard = ReplyKeyboardMarkup.builder()
                .addRow("Ace is 1", "Ace is 11")
                .oneTime(true)
                .build();
    }

    @Override
    public void onTextMessageReceived(TextMessageReceivedEvent event) {
        if (event.getChat().getType() == ChatType.PRIVATE) {
            User sender = event.getMessage().getSender();
            String message = event.getContent().getContent();

            switch (message) {
                case "Knock":
                    knock(sender, standardKeyboard);
                    break;
                case "Fold":
                    fold(sender);
                    break;
                case "Ace is 1":
                    chooseAceOne(sender);
                    break;
                case "Ace is 11":
                    chooseAceEleven(sender);
                    break;
                default:
                    getLobby().userChat(sender, message);
                    break;
            }
        }
    }

    private void chooseAceEleven(User sender) {
        if (checkAces(currentPlayer)) {
            List<BlackjackCard> cards = playerCards.get(sender.getId());

            int index = 0;
            for (BlackjackCard blackjackCard : new ArrayList<>(cards)) {
                if (blackjackCard.isModified() && blackjackCard.getCard().getRank() == Rank.ACE) {
                    cards.remove(blackjackCard);
                    cards.add(index, new BlackjackCard(blackjackCard, 11));
                }

                index++;
            }

            int userAces = aceCount.get(sender.getId()) - 1;
            if (userAces > 0) {
                aceCount.put(sender.getId(), userAces);
            } else {
                aceCount.remove(sender.getId());
            }
        }
    }

    private void chooseAceOne(User sender) {
        if (checkAces(currentPlayer)) {
            List<BlackjackCard> cards = playerCards.get(sender.getId());

            int index = 0;
            for (BlackjackCard blackjackCard : new ArrayList<>(cards)) {
                if (blackjackCard.isModified() && blackjackCard.getCard().getRank() == Rank.ACE) {
                    cards.remove(blackjackCard);
                    cards.add(index, new BlackjackCard(blackjackCard, 1));
                }

                index++;
            }

            int userAces = aceCount.get(sender.getId()) - 1;
            if (userAces > 0) {
                aceCount.put(sender.getId(), userAces);
            } else {
                aceCount.remove(sender.getId());
            }
        }
    }

    @Override
    public boolean startGame() {
        if (getActivePlayers().size() >= minPlayers) {
            if (getActivePlayers().size() > maxPlayers) {
                getLobby().sendMessage("Too many players! Maximum: " + maxPlayers);
                return false;
            } else {
                setGameState(GameState.INGAME);
                getLobby().sendMessage("Starting the game!");

                maxRounds = getActivePlayers().size() * 3;

                nextRound();
                return true;
            }
        } else {
            return false;
        }
    }

    private void nextRound() {
        roundDealerIndex++;

        if (roundDealerIndex >= getActivePlayers().size()) {
            roundDealerIndex = 0;
        }

        roundDealer = getActivePlayers().get(roundDealerIndex);

        for (PlayerData playerData : getActivePlayers()) {
            if (!(roundDealer.getId() == (playerData.getId()))) {
                toPlay.add(playerData);
            }
        }

        getLobby().sendMessage(
                SendableTextMessage.builder()
                        .message("*Starting Round " + currentRound + "/" + maxRounds + "*\n" +
                                "*Dealer:* " + roundDealer.getUsername())
                        .parseMode(ParseMode.MARKDOWN)
                        .build()
        );

        fillDeck();
        fillHands();
        nextPlayer();

        currentRound++;
    }

    private void nextPlayer() {
        if (dealerFold) {
            getLobby().sendMessage(
                    SendableTextMessage.builder()
                            .message("*All players have folded!*")
                            .parseMode(ParseMode.MARKDOWN)
                            .build()
            );

            calculateValue(roundDealer);
            int dealerScore = playerCardValues.get(roundDealer.getId());

            if (dealerScore > 21) {
                getLobby().sendMessage(
                        SendableTextMessage.builder()
                                .message("*The dealer busted!*\n\nAll players have been given 1 Game Point!")
                                .parseMode(ParseMode.MARKDOWN)
                                .build()
                );

                for (PlayerData playerData : getActivePlayers()) {
                    if (!(playerData.getId() == roundDealer.getId())) {
                        int score = getScore(playerData.getUsername());
                        setScore(playerData.getUsername(), score + 1);
                    }
                }
            } else {
                StringBuilder playerScoreList = new StringBuilder();

                for (PlayerData playerData : getActivePlayers()) {
                    if (!(playerData.getId() == roundDealer.getId())) {
                        int cardScore = playerCardValues.get(playerData.getId());
                        int score = getScore(playerData.getUsername());

                        if (cardScore <= 21) {
                            playerScoreList.append("@")
                                    .append(playerData.getUsername())
                                    .append("'s card score was ").append(cardScore)
                                    .append(". (+1 Game Points)");
                            setScore(playerData.getUsername(), score + 1);
                        } else {
                            playerScoreList.append("@")
                                    .append(playerData.getUsername())
                                    .append("'s card score was ").append(cardScore)
                                    .append(". (0 Game Points)");
                        }
                    }
                }

                getLobby().sendMessage(
                        SendableTextMessage.builder()
                                .message("*The dealer's score was " + dealerScore + "!*\n\n" + playerScoreList)
                                .parseMode(ParseMode.MARKDOWN)
                                .build()
                );
            }

            nextRound();
        } else {
            if (toPlay.size() > 0) {
                currentPlayer = toPlay.remove(0);

                getLobby().sendMessage(
                        SendableTextMessage.builder()
                                .message("*It is your turn, " + currentPlayer.getUsername() + "*")
                                .parseMode(ParseMode.MARKDOWN)
                                .build()
                );

                sendHand(currentPlayer, standardKeyboard);
            } else {
                dealerPlay();
            }
        }
    }

    private void dealerPlay() {
        currentPlayer = roundDealer;

        getLobby().sendMessage(
                SendableTextMessage.builder()
                        .message("*It is your turn, " + currentPlayer.getUsername() + "*")
                        .parseMode(ParseMode.MARKDOWN)
                        .build()
        );

        sendHand(currentPlayer, standardKeyboard);
    }

    private void sendHand(PlayerData player, ReplyMarkup replyMarkup) {
        StringBuilder stringBuilder = new StringBuilder();

        for (BlackjackCard card : playerCards.get(player.getId())) {
            stringBuilder.append(card.getCard().toString());
            stringBuilder.append(" ");
        }

        TelegramBot.getChat(player.getId()).sendMessage(
                SendableTextMessage.builder()
                        .message("*Here is your hand: *\n" + stringBuilder.toString())
                        .parseMode(ParseMode.MARKDOWN)
                        .replyMarkup(replyMarkup)
                        .build(), TelegramHook.getBot()
        );
    }

    private void fillHands() {
        for (PlayerData playerData : getActivePlayers()) {
            giveCard(playerData, 2);
        }
    }

    private boolean checkAces(PlayerData player) {
        if (aceCount.containsKey(player.getId())) {
            sendAceKeyboard(player);

            int aces = aceCount.get(player.getId());

            if (aces > 1) {
                aceCount.put(player.getId(), aces - 1);
            } else {
                aceCount.remove(player.getId());
            }
            return false;
        } else {
            return true;
        }
    }

    private void sendAceKeyboard(PlayerData playerData) {
        TelegramBot.getChat(playerData.getId()).sendMessage(
                SendableTextMessage.builder()
                        .message("*You have an Ace! Please choose its value.*")
                        .parseMode(ParseMode.MARKDOWN)
                        .replyMarkup(aceKeyboard)
                        .build(), TelegramHook.getBot()
        );
    }

    private void giveCard(PlayerData playerData, int amount) {
        List<BlackjackCard> playerCardDeck = playerCards.get(playerData.getId());

        if (playerCardDeck == null) {
            playerCardDeck = new ArrayList<>();
        }

        for (int i = 0; i < amount; i++) {
            Card card = deck.getCard();
            if (card.getRank() == Rank.ACE) {
                int playerAceCount = 0;

                if (aceCount.containsKey(playerData.getId())) {
                    playerAceCount = aceCount.get(playerData.getId());
                }

                aceCount.put(playerData.getId(), ++playerAceCount);
            }

            playerCardDeck.add(new BlackjackCard(deck.getCard()));
        }

        playerCards.put(playerData.getId(), playerCardDeck);
    }

    private void fillDeck() {
        deck = new Deck();
        deck.shuffleCards();
    }

    private void knock(User user, ReplyMarkup replyMarkup) {
        PlayerData playerData = getPlayerData(user);

        if (currentPlayer.getId() == playerData.getId()) {
            getLobby().sendMessage(
                    SendableTextMessage.builder()
                            .message("*" + playerData.getUsername() + " chose to knock.*")
                            .parseMode(ParseMode.MARKDOWN)
                            .build()
            );

            giveCard(playerData, 1);
            sendHand(playerData, replyMarkup);
        } else {
            TelegramBot.getChat(playerData.getId()).sendMessage(
                    SendableTextMessage.builder()
                            .message("*It is not your turn!*")
                            .parseMode(ParseMode.MARKDOWN)
                            .build(), TelegramHook.getBot()
            );
        }
    }

    private void fold(User user) {
        PlayerData playerData = getPlayerData(user);

        if (currentPlayer.getId() == playerData.getId()) {
            getLobby().sendMessage(
                    SendableTextMessage.builder()
                            .message("*" + playerData.getUsername() + " chose to fold.*")
                            .parseMode(ParseMode.MARKDOWN)
                            .build()
            );

            if (checkAces(currentPlayer)) {
                calculateValue(currentPlayer);

                TelegramBot.getChat(playerData.getId()).sendMessage(
                        SendableTextMessage.builder()
                                .message("Card Score: " + playerCardValues.get(currentPlayer.getId()))
                                .parseMode(ParseMode.MARKDOWN)
                                .replyMarkup(ReplyKeyboardHide.builder().build())
                                .build(), TelegramHook.getBot()
                );

                if (roundDealer.getId() == user.getId()) {
                    dealerFold = true;
                }

                nextPlayer();
            }
        } else {
            TelegramBot.getChat(playerData.getId()).sendMessage(
                    SendableTextMessage.builder()
                            .message("*It is not your turn!(")
                            .parseMode(ParseMode.MARKDOWN)
                            .build(), TelegramHook.getBot()
            );
        }
    }

    private void calculateValue(PlayerData player) {
        int value = 0;

        for (BlackjackCard card : playerCards.get(player.getId())) {
            value += card.getActualValue();
        }

        playerCardValues.put(player.getId(), value);
    }

    @Override
    public void stopGame(boolean silent) {

    }

    @Override
    public boolean playerJoin(Player player, boolean silent) {
        if (getGameState() == GameState.WAITING_FOR_PLAYERS) {
            addPlayer(player);
            if (!silent) {
                int playersNeeded = minPlayers - getActivePlayers().size();
                if (playersNeeded > 0) {
                    getLobby().sendMessage(player.getUserID(), String.format(
                            "You have joined the game! (Waiting for %d player%s)",
                            playersNeeded, StringUtil.isPlural(playersNeeded)
                    ));
                } else {
                    getLobby().sendMessage(player.getUserID(), "You have joined the game!");
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void playerLeave(Player player, boolean silent) {
        removePlayer(player);
        if (!silent) {
            // We can assume silent will be true ONLY when the game ends
            getLobby().sendMessage(player.getUserID(), "You have left the game. (Score " + getScore(player.getUsername() + ")"));
            checkPlayers();
        }
    }

    private void checkPlayers() {
        switch (getGameState()) {
            case INGAME: {
                if (minPlayers > getActivePlayers().size()) {
                    SendableTextMessage message = SendableTextMessage
                            .builder()
                            .message("*There are not enough players to continue!*")
                            .parseMode(ParseMode.MARKDOWN)
                            .build();
                    getLobby().sendMessage(message);
                    getLobby().stopGame(false);
                }
            }
        }
    }

    @Override
    public String getGameHelp() {
        return "A simple card game, closest to 21 wins, but don't bust!";
    }
}
    