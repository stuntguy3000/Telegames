package me.stuntguy3000.java.telegames.util.string;

import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;

// @author Luke Anderson | stuntguy3000
public class Lang {
    public static String GAME_BLACKJACK_DEALER_QUIT = "*The dealer quit!*";
    public static String GAME_BLACKJACK_HIT = "Hit";
    public static String COMMAND_ADMIN_UNKNOWN_SUBCOMMAND = "*Unknown subcommand! Try /admin help*";
    public static String COMMAND_ADMIN_HELP =
            "*Admin subcommand help menu:" +
            "\n/admin help - Admin help menu" +
            "\n/admin list - List all lobbies" +
            "\n/admin botfather - List all commands" +
            "\n/admin broadcast [message] - Broadcast a message to all known users*";
    public static String COMMAND_ADMIN_LOBBY = "*ID:* %s *Owner:* %s *Members:* %s *Last Active:* %s %s";
    public static String COMMAND_CREATEMENU = "*Here you go:*";
    public static String COMMAND_GAMEHELP = TelegramEmoji.BOOK.getText() + "*Game Information:*";
    public static String COMMAND_KICK_UNKICKABLE = TelegramEmoji.RED_CROSS.getText() + " *You cannot kick this player!*";
    public static String COMMAND_PLAY = TelegramEmoji.JOYSTICK.getText() + " *Please choose a game:*";
    public static String COMMAND_PLAY_RANDOM = TelegramEmoji.JOYSTICK.getText() + " *Choosing a random game!*";
    public static String ERROR_GAME_RUNNING = TelegramEmoji.RED_CROSS.getText() + " *A game is currently running!*";
    public static String ERROR_GAME_NOT_RUNNING = TelegramEmoji.RED_CROSS.getText() + " *No game is currently running!*";
    public static String ERROR_NOT_AUTHORIZIED = TelegramEmoji.RED_CROSS.getText() + " *You cannot do this!*!";
    public static String GAME_BLACKJACK_ACE_ONE = "Ace is 1";
    public static String GAME_BLACKJACK_ACE_ELEVEN = "Ace is 11";
    public static String GAME_BLACKJACK_PLAYER_HIT = "*%s chooses to Hit.*";
    public static String GAME_BLACKJACK_DEALER_SCORE = "*The dealer's score was %s!*\n\n%s";
    public static String GAME_BLACKJACK_PLAYER_SCORE = "*%s's card score was %s. (%s Game Points)";
    public static String GAME_BLACKJACK_PLAYER_STAND = "*%s chooses to Stand.*";
    public static String GAME_BLACKJACK_ALL_PLAYERS_STAND = "*All players have chosen to Stand!*";
    public static String GAME_BLACKJACK_DEALER_BUSTS =
            "*The dealer busted!*\n\n" +
            "All players have been given 1 Game Point!";
    public static String GAME_BLACKJACK_PLAYER_BUSTS = "*You have busted!*";
    public static String GAME_BLACKJACK_PLAYER_HAVE_ACE = "*You have an Ace! Please choose its value.*";
    public static String GAME_BLACKJACK_STAND = "Stand";
    public static String GAME_GENERAL_NEXT_TURN = "*It's your turn, %s.*";
    public static String GAME_GENERAL_PLAYTIMER = "*Please make a play* @%s";
    public static String GAME_GENERAL_TURNSKIP = "*%s ran out of time!*";
    public static String GAME_GENERAL_PLAYER_QUIT = "*The current player quit!*";
    public static String GAME_GENERAL_NOT_TURN = "*It's not your turn!*";
    public static String MISC_HEADER_LOBBYLIST = "*Lobby List:*";
    public static String MISC_HEADER_BOTFATHER = "*Lobby Message:*";
    public static String ERROR_LOBBY_NOT_FOUND = TelegramEmoji.RED_CROSS.getText() + " *No such lobby exists!*";
    public static String ERROR_SYNTAX_INVALID = TelegramEmoji.RED_CROSS.getText() + " *Correct Syntax: /%s %s*";
    public static String ERROR_GAME_NOT_FOUND = TelegramEmoji.RED_CROSS.getText() + " *No such game exists!*";
    public static String ERROR_PLAYER_NOT_FOUND = TelegramEmoji.RED_CROSS.getText() + " *No such player exists!*";
    public static String ERROR_LOBBY_CREATE_MATCHMAKING = TelegramEmoji.RED_CROSS.getText() + " *You cannot create a lobby while in matchmaking!*";
    public static String ERROR_USER_IN_LOBBY = TelegramEmoji.RED_CROSS.getText() + " *You are already in a lobby!*";
    public static String ERROR_USER_NOT_IN_LOBBY = TelegramEmoji.RED_CROSS.getText() + " *You are not in a lobby!*";
    public static String ERROR_COMMAND_PM_ONLY = TelegramEmoji.RED_CROSS.getText() + " *This command can only be executed via a private message to* @" + TelegramHook.getBot().getBotUsername();
    public static String GAME_BLACKJACK_NAME = "Blackjack";
    public static String GAME_BLACKJACK_DESCRIPTION = "A simple card game, closest to 21 wins, but don't bust!";
}
    