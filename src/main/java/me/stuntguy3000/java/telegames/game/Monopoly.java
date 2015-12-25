package me.stuntguy3000.java.telegames.game;

import lombok.Getter;
import lombok.Setter;
import me.stuntguy3000.java.telegames.object.game.Game;
import me.stuntguy3000.java.telegames.object.game.GameState;
import me.stuntguy3000.java.telegames.object.lobby.LobbyMember;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;

import java.util.HashMap;

enum MonopolySquareType {
    // Standard Property
    PROPERTY,
    // Free Parking
    FREE_PARKING,
    // Jail Square
    JAIL,
    // Go to Jail
    GOTO_JAIL,
    // Draw from Chance
    CHANCE,
    // Draw from Community Chest
    COMMUNITY_CHEST,
    // e.g. Railway stations, Airports
    PROPERTY_SPECIAL,
    // Start square
    START,
    // Tax square
    TAX,
    // Service, e.g. internet service, water works, mobile phone service
    UTILITY
}

// @author Luke Anderson | stuntguy3000
// What am I doing with my life
public class Monopoly extends Game {

    public Monopoly() {
        setGameInfo("Monopoly", "Description");
        setDevModeOnly(true);
        setGameState(GameState.WAITING_FOR_PLAYERS);
    }

    @Override
    public void onTextMessageReceived(TextMessageReceivedEvent event) {

    }

    @Override
    public void startGame() {

    }
}

class MonopolyBoard {
    private HashMap<Integer, MonopolySquare> monopolySquares = new HashMap<>();
}

class MonopolySquare {
    /**
     * The position on the board, with 1 being Go and following counter clockwise
     */
    @Getter
    private final int boardPosition;
    /**
     * The value of the square, only applies for special squares where necessary
     */
    @Getter
    private final int costToBuy;
    /**
     * If the square is buyable
     */
    @Getter
    private final boolean isBuyable;
    /**
     * The type of Monopoly Square
     */
    @Getter
    private final MonopolySquareType monopolySquareType;
    /**
     * The value of the property if mortgaged
     */
    @Getter
    private final int mortgagedValue;
    /**
     * Any other special properties, e.g. utility or special squares
     */
    @Getter
    private final HashMap<String, String> specialProperties = new HashMap<>();
    /**
     * If the square is mortgaged
     */
    @Getter
    @Setter
    private boolean isMortgaged = false;
    /**
     * The owner of this square
     */
    @Getter
    @Setter
    private LobbyMember owner = null;

    /**
     * @param monopolySquareType
     * @param boardPosition
     * @param costToBuy
     * @param buyable
     * @param mortgagedValue
     */
    MonopolySquare(MonopolySquareType monopolySquareType, int boardPosition, int costToBuy, boolean buyable, int mortgagedValue) {
        this.monopolySquareType = monopolySquareType;
        this.boardPosition = boardPosition;
        this.costToBuy = costToBuy;
        this.isBuyable = buyable;
        this.mortgagedValue = mortgagedValue;
    }
}
    