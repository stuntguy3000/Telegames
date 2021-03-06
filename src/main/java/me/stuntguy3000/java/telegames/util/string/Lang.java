package me.stuntguy3000.java.telegames.util.string;

import me.stuntguy3000.java.telegames.hook.TelegramHook;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;

// @author Luke Anderson | stuntguy3000
public class Lang {
    public static String COMMAND_ADMIN_HELP = "*Admin subcommand help menu:" +
            "\n/admin help - Admin help menu" +
            "\n/admin list - List all lobbies" +
            "\n/admin botfather - List all commands" +
            "\n/admin broadcast [message] - Broadcast a message to all known users*";
    public static String COMMAND_ADMIN_LOBBY = "*ID:* %s *Owner:* %s *Members:* %s *Last Active:* %s %s";
    public static String COMMAND_ADMIN_UNKNOWN_SUBCOMMAND = "*Unknown subcommand! Try /admin help*";
    public static String COMMAND_CREATEMENU = "*Here you go:*";
    public static String COMMAND_GAMEHELP = Emoji.BOOK.getText() + "*Game Information:*";
    public static String COMMAND_KICK_UNKICKABLE = Emoji.RED_CROSS.getText() + " *You cannot kick this player!*";
    public static String COMMAND_PLAY = Emoji.JOYSTICK.getText() + " *Please choose a game:*";
    public static String COMMAND_PLAY_RANDOM = Emoji.JOYSTICK.getText() + " *Choosing a random game!*";
    public static String ERROR_ALPHA_ONLY = Emoji.RED_CROSS.getText() + " Only Alpha characters are valid!";
    public static String ERROR_ALREADY_PLAYED_CARD = Emoji.RED_CROSS.getText() + " *You cannot play this card again!*";
    public static String ERROR_CANNOT_JOIN_LOBBY = Emoji.RED_CROSS.getText() + " *You cannot join this lobby.*";
    public static String ERROR_COMMAND_PM_ONLY = Emoji.RED_CROSS.getText() + " *This command can only be executed via a private message to* @" + TelegramHook.getBot().getBotUsername();
    public static String ERROR_GAME_NOT_FOUND = Emoji.RED_CROSS.getText() + " *No such game exists!*";
    public static String ERROR_GAME_NOT_RUNNING = Emoji.RED_CROSS.getText() + " *No game is currently running!*";
    public static String ERROR_GAME_NOT_STARTED = Emoji.RED_CROSS.getText() + " *The game has not started yet!*";
    public static String ERROR_GAME_RUNNING = Emoji.RED_CROSS.getText() + " *A game is currently running!*";
    public static String ERROR_INVALID_SELECTION = Emoji.RED_CROSS.getText() + " *That selection is invalid!*";
    public static String ERROR_LOBBY_CREATE_MATCHMAKING = Emoji.RED_CROSS.getText() + " *You cannot create a lobby while in matchmaking!*";
    public static String ERROR_LOBBY_NOT_FOUND = Emoji.RED_CROSS.getText() + " *No such lobby exists!*";
    public static String ERROR_NOT_AUTH = Emoji.RED_CROSS.getText() + " *You cannot do this!*!";
    public static String ERROR_NOT_ENOUGH_PLAYERS = Emoji.RED_CROSS.getText() + " *There are not enough players to continue!*";
    public static String ERROR_PLAYER_NOT_FOUND = Emoji.RED_CROSS.getText() + " *No such player exists!*";
    public static String ERROR_SYNTAX_INVALID = Emoji.RED_CROSS.getText() + " *Correct Syntax: /%s %s*";
    public static String ERROR_TOO_SHORT_3 = Emoji.RED_CROSS.getText() + " The word has to be longer than three characters!";
    public static String ERROR_USER_IN_LOBBY = Emoji.RED_CROSS.getText() + " *You are already in a lobby!*";
    public static String ERROR_USER_NOT_IN_LOBBY = Emoji.RED_CROSS.getText() + " *You are not in a lobby!*";
    public static String GAME_BLACKJACK_ACE_ELEVEN = "Ace is 11";
    public static String GAME_BLACKJACK_ACE_ONE = "Ace is 1";
    public static String GAME_BLACKJACK_ALL_PLAYERS_STAND = "*All players have chosen to Stand!*";
    public static String GAME_BLACKJACK_DEALER_BUSTS = "*The dealer busted!*\n\n" + "All players have been given 1 Game Point!";
    public static String GAME_BLACKJACK_DEALER_QUIT = "*The dealer quit!*";
    public static String GAME_BLACKJACK_DEALER_SCORE = "*The dealer's score was %s!*\n\n%s";
    public static String GAME_BLACKJACK_DESCRIPTION = "A simple card game, closest to 21 wins, but don't bust!";
    public static String GAME_BLACKJACK_HIT = "Hit";
    public static String GAME_BLACKJACK_NAME = "Blackjack";
    public static String GAME_BLACKJACK_PLAYER_BUSTS = "*You have busted!*";
    public static String GAME_BLACKJACK_PLAYER_HAVE_ACE = "*You have an Ace! Please choose its value.*";
    public static String GAME_BLACKJACK_PLAYER_HIT = "*%s chooses to Hit.*";
    public static String GAME_BLACKJACK_PLAYER_SCORE = "*%s's card score was %s. (%s Game Points)";
    public static String GAME_BLACKJACK_PLAYER_STAND = "*%s chooses to Stand.*";
    public static String GAME_BLACKJACK_STAND = "Stand";
    public static String GAME_BLACKJACK_STARTING_ROUND = "*Starting round %d/%d\nDealer: %s*";
    public static String GAME_CAH_ACTIVECARDS = Emoji.JOKER_CARD.getText() + " *The decks have been chosen!\n\nDecks: %s*";
    public static String GAME_CAH_ALLPLAYED = Emoji.FINGER.getText() + " *The czar is choosing a winner.*";
    public static String GAME_CAH_ALLPLAYED_CHOOSING = "*All users have played. A winner will be chosen shortly...*\n\n%s";
    public static String GAME_CAH_ALLPLAYED_CZAR = Emoji.GREEN_BOX_TICK.getText() + " *All users have played.*\n*Please choose a winner!*";
    public static String GAME_CAH_CHOOSE_EXTRAS = Emoji.PENCIL.getText() + " *Which expansion card packs would you like?\n\nClick done when you have made your selections.*";
    public static String GAME_CAH_CHOOSE_VERSION = Emoji.PENCIL.getText() + " *Which card deck would you like?*";
    public static String GAME_CAH_COMMANDHELP = "CardsAgainstHumanity Command Menu:\n" +
            "+help - View the help menu\n" +
            "+cards - View your cards\n" +
            "+score - View your cards";
    public static String GAME_CAH_DESCRIPTION = "The most fun and offensive card game ever known.";
    public static String GAME_CAH_NAME = "CardsAgainstHumanity";
    public static String GAME_CAH_NOPLAYERS = "*Nobody played! Skipping...*";
    public static String GAME_CAH_PLAY_MORE = "*Please play %s more card(s).*";
    public static String GAME_CAH_STARTROUND_CZAR = Emoji.BOOK.getText() + " *Starting Round %s\nCzar: %s*";
    public static String GAME_CAH_TIMENOTICE = Emoji.SAD_FACE.getText() + " *Not everybody has played!*";
    public static String GAME_CAH_TIMEWARNING = Emoji.ALERT.getText() + " *All players have 10 seconds to play all cards.*";
    public static String GAME_CAH_USERPLAY = Emoji.GREEN_BOX_TICK.getText() + " *%s has played.*";
    public static String GAME_CAH_WAITING = Emoji.SAND_CLOCK.getText() + " *Please wait while the %s chooses the card decks...*";
    public static String GAME_CAH_WHITECARDS = "_Please play %s white cards._";
    public static String GAME_CAH_WIN_ROUND = "*%s %s won the round!*";
    public static String GAME_CARDPICKUP = "Picked up cards: %s";
    public static String GAME_GENERAL_DRAW = "*The match was a draw!*";
    public static String GAME_GENERAL_NEXT_TURN = "*It's your turn, %s.*";
    public static String GAME_GENERAL_NOT_TURN = "*It's not your turn!*";
    public static String GAME_GENERAL_OVER = "*GAME OVER!*";
    public static String GAME_GENERAL_PLAYER_CARDS = "*Here are your cards: *\n" + "%s";
    public static String GAME_GENERAL_PLAYER_CARDS_MESSAGE = "*Here are your cards: *";
    public static String GAME_GENERAL_PLAYER_HAND = "*Here is your hand: *\n%s";
    public static String GAME_GENERAL_PLAYER_QUIT = "*The current player quit!*";
    public static String GAME_GENERAL_PLAYER_SCORE = "*Score: %s*";
    public static String GAME_GENERAL_PLAYTIMER = Emoji.ALERT.getText() + " *Please make a play* @%s";
    public static String GAME_GENERAL_STARTROUND_NUMBER = Emoji.BOOK.getText() + " *Starting round %d.*";
    public static String GAME_GENERAL_TURNSKIP = Emoji.ALERT.getText() + " *%s ran out of time!*";
    public static String GAME_GENERAL_WINNER = "*The winner is %s.*";
    public static String GAME_GENERAL_WIN_ROUND = "*%s won the round!*";
    public static String GAME_HANGMAN_DESCRIPTION = "The classic game of hangman. Try to guess the phrase before its too late!";
    public static String GAME_HANGMAN_GUESS_ALREADY_GUESSED = Emoji.RED_CROSS.getText() + " *Letter already guessed!\nRemaining: %s\n\nThe word: %s\nGuessed letters: %s*";
    public static String GAME_HANGMAN_GUESS_CORRECT = Emoji.GREEN_BOX_TICK.getText() + " *Correct guess!\nRemaining: %s\n\nThe word: %s*";
    public static String GAME_HANGMAN_GUESS_INCORRECT = Emoji.RED_CROSS.getText() + " *Incorrect guess!\nRemaining: %s\n\nThe word: %s\nGuessed letters: %s*";
    public static String GAME_HANGMAN_GUESS_LETTER = "%s guessed %s.";
    public static String GAME_HANGMAN_GUESS_LOSE = Emoji.RED_CROSS.getText() + " *Out of guesses!\n\nThe Word: %s*";
    public static String GAME_HANGMAN_GUESS_WORD = Emoji.PARTY_POPPER.getText() + " *The word was guessed correctly!\n\nThe word was \"%s\"*";
    public static String GAME_HANGMAN_KEYBOARD_RANDOM = Emoji.OPEN_BOOK.getText() + " Choose a random word";
    public static String GAME_HANGMAN_NAME = "Hangman";
    public static String GAME_HANGMAN_RANDOM_CHOSEN = Emoji.GREEN_BOX_TICK.getText() + " *The word \"%s\" has been randomly chosen.*";
    public static String GAME_HANGMAN_SELECTING = "*%s is choosing a word...*";
    public static String GAME_HANGMAN_SELECTING_ASK = "Please choose a word...";
    public static String GAME_HANGMAN_WORD_CHOSEN = Emoji.BOOK.getText() + " *The word has been chosen!\n\nTo guess, send your guess as a message!\nYou can only guess one letter at a time.\n\nThe word: %s*";
    public static String GAME_SCORE = "*#%d - %s (Score: %d)*";
    public static String GAME_TICTACTOE_DESCRIPTION = "First player to line three in a row wins.";
    public static String GAME_TICTACTOE_END = "The game of TicTacToe has ended!";
    public static String GAME_TICTACTOE_NAME = "TicTacToe";
    public static String GAME_UNO_BUTTON_DRAW = "Draw from deck";
    public static String GAME_UNO_BUTTON_SCORE = "Your score is %d";
    public static String GAME_UNO_CHOOSE_COLOUR = "Please choose a colour.";
    public static String GAME_UNO_DESCRIPTION = "The classic card game Uno.";
    public static String GAME_UNO_DRAW2 = "*%s has been given two cards!";
    public static String GAME_UNO_DRAW4 = "*%s has been given four cards!";
    public static String GAME_UNO_DREW = "*%s drew from the deck.*";
    public static String GAME_UNO_HELP = "Uno Command Menu:\n" +
            "+help - View the help menu\n" +
            "+deck - View your deck\n" +
            "+scores - View the scores\n" +
            "+colour - View the colour picker";
    public static String GAME_UNO_NAME = "Uno";
    public static String GAME_UNO_PLAYCOLOUR = "_Please play any %s card_";
    public static String GAME_UNO_PLAYED = "%s played %s";
    public static String GAME_UNO_REVERSE = "*Player order has been reversed!*";
    public static String GAME_UNO_ROUNDINFO = Emoji.BLUE_RIGHT_ARROW.getText() + " *Current Card:* %s\n" + Emoji.PERSON.getText() + " *Current Player:* %s";
    public static String GAME_UNO_SHUFFLING = "_Shuffling deck..._";
    public static String GAME_UNO_SKIP = "*%s has been skipped.";
    public static String GENERAL_ENTER_LOBBY_NAME = Emoji.PENCIL.getText() + " *Enter the name or ID of the lobby:*";
    public static String GENERAL_RETURNING_MENU = Emoji.HOUSE.getText() + " *Returning to lobby menu:*";
    public static String KEYBOARD_ABOUT = Emoji.BOOK.getText() + " About";
    public static String KEYBOARD_CANCEL = Emoji.RED_CROSS.getText() + " Cancel";
    public static String KEYBOARD_CREATE_LOBBY = Emoji.JOYSTICK.getText() + " Create a lobby";
    public static String KEYBOARD_DONE = Emoji.GREEN_BOX_TICK.getText() + " Done";
    public static String KEYBOARD_JOIN_LOBBY = Emoji.PERSON.getText() + " Join a lobby";
    public static String KEYBOARD_JOIN_MATCHMAKING = Emoji.BLUE_RIGHT_ARROW.getText() + " Enter matchmaking";
    public static String KEYBOARD_LEAVE_LOBBY = Emoji.HOUSE.getText() + " Leave the lobby";
    public static String KEYBOARD_LOBBY_LOCK = Emoji.PADLOCK_LOCKED.getText() + " Lock lobby";
    public static String KEYBOARD_LOBBY_OPTIONS = Emoji.METAL_GEAR.getText() + " Lobby options";
    public static String KEYBOARD_LOBBY_UNLOCK = Emoji.PADLOCK_UNLOCKED.getText() + " Unlock lobby";
    public static String KEYBOARD_PLAY = Emoji.JOYSTICK.getText() + " Play a game";
    public static String KEYBOARD_QUIT_MATCHMAKING = Emoji.RED_CROSS.getText() + " Quit matchmaking";
    public static String KEYBOARD_RANDOM = Emoji.QUESTION_MARK.getText() + " Random";
    public static String KEYBOARD_RATE = Emoji.STAR.getText() + " Rate this bot";
    public static String KEYBOARD_RENAME = Emoji.PENCIL.getText() + " Rename lobby";
    public static String KEYBOARD_REPLAY = Emoji.REPLAY.getText() + " Replay previous game";
    public static String KEYBOARD_RETURN_MENU = Emoji.HOUSE.getText() + " Back to menu";
    public static String MATCHMAKING_DESCRIPTION = Emoji.PERSON.getText() + " *Welcome to Telegames Matchmaking!*\n\n" +
            "Matchmaking is a simple feature allowing players to quickly play a game with random people " +
            "around the world, with no lobbies required.\n\n" +
            "To begin matchmaking, simply click on a game's name in the menu below to toggle if " +
            "you want to include that game in the matchmaking search. All games are disabled by default.\n\n" +
            "*Players in matchmaking queue: %d*";
    public static String MATCHMAKING_STARTING = Emoji.SAND_CLOCK.getText() + " *The game is starting in %s second(s).*";
    public static String MATCHMAKING_STARTING_NOW = Emoji.STAR.getText() + " *The game is starting!*";
    public static String MISC_HEADER_BOTFATHER = "*Lobby Message:*";
    public static String MISC_HEADER_LOBBYLIST = "*Lobby List:*";

    public static SendableTextMessage.SendableTextMessageBuilder build(String text, String... variables) {
        return SendableTextMessage.builder().message(String.format(text, variables)).parseMode(ParseMode.MARKDOWN);
    }
}
    