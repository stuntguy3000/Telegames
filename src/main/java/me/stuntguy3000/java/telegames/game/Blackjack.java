package me.stuntguy3000.java.telegames.game;

import lombok.Getter;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.game.Game;
import me.stuntguy3000.java.telegames.object.game.GameState;
import me.stuntguy3000.java.telegames.object.user.TelegramUser;
import me.stuntguy3000.java.telegames.util.cards.Card;
import me.stuntguy3000.java.telegames.util.cards.Deck;
import me.stuntguy3000.java.telegames.util.cards.Rank;
import me.stuntguy3000.java.telegames.util.string.Lang;
import me.stuntguy3000.java.telegames.util.string.StringUtil;
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

// @author Luke Anderson | stuntguy3000
public class Blackjack extends Game {
    private HashMap<Integer, Integer> aceCount = new HashMap<>();
    private ReplyKeyboardMarkup aceKeyboard;
    private TelegramUser currentPlayer;
    private int currentRound = 1;
    private Boolean dealerStand = false;
    private Deck deck;
    private int maxRounds = 5;
    private HashMap<Integer, Integer> playerCardValues = new HashMap<>();
    private HashMap<Integer, List<BlackjackCard>> playerCards = new HashMap<>();
    private TelegramUser roundDealer;
    private int roundDealerIndex = 0;
    private int secondsSincePlay = 0;
    private List<TelegramUser> toPlay = new ArrayList<>();

    public Blackjack() {
        setGameInfo(Lang.GAME_BLACKJACK_NAME, Lang.GAME_BLACKJACK_DESCRIPTION);
        setMinPlayers(2);
        setMinPlayers(8);
        setGameState(GameState.WAITING_FOR_PLAYERS);

        aceKeyboard = ReplyKeyboardMarkup.builder().addRow(Lang.GAME_BLACKJACK_ACE_ONE, Lang.GAME_BLACKJACK_ACE_ELEVEN).oneTime(true).build();
    }

    private void calculateValue(TelegramUser telegramUser) {
        int value = 0;

        for (BlackjackCard card : playerCards.get(telegramUser.getUserID())) {
            value += card.getActualValue();
        }

        playerCardValues.put(telegramUser.getUserID(), value);
    }

    private boolean checkAces(TelegramUser telegramUser) {
        if (aceCount.containsKey(telegramUser.getUserID())) {
            sendAceKeyboard(telegramUser);
            return false;
        } else {
            return true;
        }
    }

    private void chooseAce(TelegramUser telegramUser, int value) {
        if (aceCount.containsKey(telegramUser.getUserID())) {
            List<BlackjackCard> cards = playerCards.get(telegramUser.getUserID());

            int index = 0;
            for (BlackjackCard blackjackCard : new ArrayList<>(cards)) {
                if (!blackjackCard.isModified() && blackjackCard.getCard().getRank() == Rank.ACE) {
                    cards.remove(blackjackCard);
                    cards.add(index, new BlackjackCard(blackjackCard, value));
                }

                index++;
            }

            int userAces = aceCount.get(telegramUser.getUserID()) - 1;
            if (userAces > 0) {
                aceCount.put(telegramUser.getUserID(), userAces);
            } else {
                aceCount.remove(telegramUser.getUserID());
            }

            stand(telegramUser);
        }
    }

    private void dealerPlay() {
        currentPlayer = roundDealer;

        getGameLobby().sendMessage(SendableTextMessage.builder().message(String.format(Lang.GAME_GENERAL_NEXT_TURN, StringUtil.markdownSafe(currentPlayer.getUsername()))).parseMode(ParseMode.MARKDOWN).build());

        sendHand(currentPlayer, getKeyboard(currentPlayer));
    }

    private void fillDeck() {
        deck = new Deck();
        deck.shuffleCards();
    }

    private void fillHands() {
        for (TelegramUser telegramUser : getActivePlayers()) {
            giveCard(telegramUser, 2);
        }
    }

    @Override
    public String getGameHelp() {
        return Lang.GAME_BLACKJACK_DESCRIPTION;
    }

    @Override
    public void onSecond() {
        secondsSincePlay++;
        if (secondsSincePlay == 30) {
            getGameLobby().sendMessage(SendableTextMessage.builder().message(String.format(Lang.GAME_GENERAL_PLAYTIMER, StringUtil.markdownSafe(currentPlayer.getUsername()))).build());
        } else if (secondsSincePlay == 40) {
            getGameLobby().sendMessage(SendableTextMessage.builder().message(String.format(Lang.GAME_GENERAL_TURNSKIP, StringUtil.markdownSafe(currentPlayer.getUsername()))).parseMode(ParseMode.MARKDOWN).build());
            stand(currentPlayer);
        }
    }

    @Override
    public void onTextMessageReceived(TextMessageReceivedEvent event) {
        if (event.getChat().getType() == ChatType.PRIVATE) {
            User sender = event.getMessage().getSender();
            String message = event.getContent().getContent();
            TelegramUser telegramUser = getGameLobby().getTelegramUser(sender.getUsername());

            if (message.equalsIgnoreCase(Lang.GAME_BLACKJACK_HIT)) {
                hit(telegramUser, getKeyboard(getGameLobby().getTelegramUser(sender.getUsername())));
            } else if (message.equalsIgnoreCase(Lang.GAME_BLACKJACK_STAND)) {
                stand(telegramUser);
            } else if (message.equalsIgnoreCase(Lang.GAME_BLACKJACK_ACE_ONE)) {
                chooseAce(telegramUser, 1);
            } else if (message.equalsIgnoreCase(Lang.GAME_BLACKJACK_ACE_ELEVEN)) {
                chooseAce(telegramUser, 11);
            } else {
                getGameLobby().userChat(telegramUser, message);
            }
        }
    }

    @Override
    public void playerLeave(String username, int userID) {
        playerCardValues.remove(userID);
        playerCards.remove(userID);
        removePlayer(username);
        checkPlayers();

        if (currentPlayer.getUserID() == userID) {
            SendableTextMessage message = SendableTextMessage.builder().message(Lang.GAME_GENERAL_PLAYER_QUIT).parseMode(ParseMode.MARKDOWN).build();
            getGameLobby().sendMessage(message);

            nextRound();
        } else if (roundDealer.getUserID() == userID) {
            SendableTextMessage message = SendableTextMessage.builder().message(Lang.GAME_BLACKJACK_DEALER_QUIT).parseMode(ParseMode.MARKDOWN).build();
            getGameLobby().sendMessage(message);

            nextRound();
        }

    }

    public void startGame() {
        setGameState(GameState.INGAME);

        maxRounds = getActivePlayers().size() * 3;

        nextRound();
    }

    public ReplyMarkup getKeyboard(TelegramUser telegramUser) {
        int score = 0;

        if (playerCardValues.containsKey(telegramUser.getUserID())) {
            score = playerCardValues.get(telegramUser.getUserID());

        }

        if (score < 21) {
            return ReplyKeyboardMarkup.builder().addRow(Lang.GAME_BLACKJACK_HIT, Lang.GAME_BLACKJACK_STAND).oneTime(false).build();
        } else {
            return ReplyKeyboardMarkup.builder().addRow(Lang.GAME_BLACKJACK_STAND).oneTime(false).build();
        }
    }

    private void giveCard(TelegramUser telegramUser, int amount) {
        List<BlackjackCard> playerCardDeck = playerCards.get(telegramUser.getUserID());

        if (playerCardDeck == null) {
            playerCardDeck = new ArrayList<>();
        }

        for (int i = 0; i < amount; i++) {
            Card card = deck.getCard();

            if (card.getRank() == Rank.ACE) {
                int playerAceCount = 0;

                if (aceCount.containsKey(telegramUser.getUserID())) {
                    playerAceCount = aceCount.get(telegramUser.getUserID());
                }

                aceCount.put(telegramUser.getUserID(), ++playerAceCount);
            }

            playerCardDeck.add(new BlackjackCard(card));
        }

        playerCards.put(telegramUser.getUserID(), playerCardDeck);
        calculateValue(telegramUser);
    }

    private void hit(TelegramUser telegramUser, ReplyMarkup replyMarkup) {
        if (currentPlayer.getUserID() == telegramUser.getUserID()) {
            if (playerCardValues.get(currentPlayer.getUserID()) >= 21) {
                TelegramBot.getChat(telegramUser.getUserID()).sendMessage(SendableTextMessage.builder().message(Lang.GAME_BLACKJACK_PLAYER_BUSTS).parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());

                stand(telegramUser);
            } else {
                getGameLobby().sendMessage(SendableTextMessage.builder().message(String.format(Lang.GAME_BLACKJACK_PLAYER_HIT, StringUtil.markdownSafe(telegramUser.getUsername()))).parseMode(ParseMode.MARKDOWN).build());

                giveCard(telegramUser, 1);
                sendHand(telegramUser, replyMarkup);
            }
        } else {
            TelegramBot.getChat(telegramUser.getUserID()).sendMessage(SendableTextMessage.builder().message(Lang.GAME_GENERAL_NOT_TURN).parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
        }
    }

    private void nextPlayer() {
        if (dealerStand) {
            getGameLobby().sendMessage(SendableTextMessage.builder().message(Lang.GAME_BLACKJACK_ALL_PLAYERS_STAND).parseMode(ParseMode.MARKDOWN).build());

            calculateValue(roundDealer);
            int dealerScore = playerCardValues.get(roundDealer.getUserID());

            if (dealerScore > 21) {
                getGameLobby().sendMessage(SendableTextMessage.builder().message(Lang.GAME_BLACKJACK_DEALER_BUSTS).parseMode(ParseMode.MARKDOWN).build());

                for (TelegramUser telegramUser : getActivePlayers()) {
                    if (!(telegramUser.getUserID() == roundDealer.getUserID())) {
                        int score = telegramUser.getGameScore();
                        telegramUser.setGameScore(score + 1);
                    }
                }
            } else {
                StringBuilder playerScoreList = new StringBuilder();

                for (TelegramUser telegramUser : getActivePlayers()) {
                    if (!(telegramUser.getUserID() == roundDealer.getUserID())) {
                        int cardScore = playerCardValues.get(telegramUser.getUserID());
                        int score = telegramUser.getGameScore();

                        if (cardScore >= dealerScore && cardScore <= 21) {
                            playerScoreList.append(String.format(Lang.GAME_BLACKJACK_PLAYER_SCORE, StringUtil.markdownSafe(telegramUser.getUsername()), cardScore, "+1"));
                            telegramUser.setGameScore(score + 1);
                        } else {
                            playerScoreList.append(String.format(Lang.GAME_BLACKJACK_PLAYER_SCORE, StringUtil.markdownSafe(telegramUser.getUsername()), cardScore, "0"));
                        }
                    }
                }

                getGameLobby().sendMessage(SendableTextMessage.builder().message(String.format(Lang.GAME_BLACKJACK_DEALER_SCORE, dealerScore, playerScoreList)).parseMode(ParseMode.MARKDOWN).build());
            }

            nextRound();
        } else {
            if (toPlay.size() > 0) {
                currentPlayer = toPlay.remove(0);

                getGameLobby().sendMessage(SendableTextMessage.builder().message(String.format(Lang.GAME_GENERAL_NEXT_TURN, StringUtil.markdownSafe(currentPlayer.getUsername()))).parseMode(ParseMode.MARKDOWN).build());

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

            for (TelegramUser telegramUser : getActivePlayers()) {
                if (!(roundDealer.getUserID() == (telegramUser.getUserID()))) {
                    toPlay.add(telegramUser);
                }
            }

            getGameLobby().sendMessage(SendableTextMessage.builder().message(String.format(Lang.GAME_BLACKJACK_STARTING_ROUND, currentRound, maxRounds, StringUtil.markdownSafe(roundDealer.getUsername()))).parseMode(ParseMode.MARKDOWN).build());

            fillDeck();
            fillHands();
            nextPlayer();

            currentRound++;
        }
    }

    private void sendAceKeyboard(TelegramUser telegramUser) {
        secondsSincePlay = 0;
        TelegramBot.getChat(telegramUser.getUserID()).sendMessage(SendableTextMessage.builder().message(Lang.GAME_BLACKJACK_PLAYER_HAVE_ACE).parseMode(ParseMode.MARKDOWN).replyMarkup(aceKeyboard).build(), TelegramHook.getBot());
    }

    private void sendHand(TelegramUser player, ReplyMarkup replyMarkup) {
        secondsSincePlay = 0;
        StringBuilder stringBuilder = new StringBuilder();

        for (BlackjackCard card : playerCards.get(player.getUserID())) {
            stringBuilder.append(card.getCard().toString());
            stringBuilder.append(" ");
        }

        TelegramBot.getChat(player.getUserID()).sendMessage(SendableTextMessage.builder().message(String.format(Lang.GAME_GENERAL_PLAYER_HAND, stringBuilder.toString())).parseMode(ParseMode.MARKDOWN).replyMarkup(replyMarkup).build(), TelegramHook.getBot());
    }

    private void stand(TelegramUser telegramUser) {
        if (currentPlayer.getUserID() == telegramUser.getUserID()) {
            secondsSincePlay = 0;
            if (checkAces(currentPlayer)) {
                getGameLobby().sendMessage(SendableTextMessage.builder().message(String.format(Lang.GAME_BLACKJACK_PLAYER_STAND, StringUtil.markdownSafe(telegramUser.getUsername()))).parseMode(ParseMode.MARKDOWN).build());

                calculateValue(currentPlayer);

                TelegramBot.getChat(telegramUser.getUserID()).sendMessage(SendableTextMessage.builder().message(String.format(Lang.GAME_GENERAL_PLAYER_SCORE, playerCardValues.get(currentPlayer.getUserID()))).parseMode(ParseMode.MARKDOWN).replyMarkup(ReplyKeyboardHide.builder().build()).build(), TelegramHook.getBot());

                if (roundDealer.getUserID() == telegramUser.getUserID()) {
                    dealerStand = true;
                }

                nextPlayer();
            }
        } else {
            TelegramBot.getChat(telegramUser.getUserID()).sendMessage(SendableTextMessage.builder().message(Lang.GAME_GENERAL_NOT_TURN).parseMode(ParseMode.MARKDOWN).build(), TelegramHook.getBot());
        }
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
    