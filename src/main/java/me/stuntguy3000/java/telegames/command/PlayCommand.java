package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;
import me.stuntguy3000.java.telegames.object.Command;
import me.stuntguy3000.java.telegames.object.Game;
import me.stuntguy3000.java.telegames.object.Lobby;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class PlayCommand extends Command {
    public PlayCommand() {
        super(Telegames.getInstance(), "play", "/play <game> Play a game.");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        User sender = event.getMessage().getSender();
        LobbyHandler lobbyHandler = getInstance().getLobbyHandler();
        String[] args = event.getArgs();

        if (event.getChat().getType() == ChatType.PRIVATE) {
            Lobby lobby = lobbyHandler.getLobby(sender);
            if (lobby != null) {
                Game lobbyGame = lobby.getCurrentGame();

                if (lobbyGame == null) {
                    if (args.length > 0) {
                        if (args[0].equalsIgnoreCase("random")) {
                            respond(chat, TelegramEmoji.JOYSTICK.getText() + " Choosing a random game!");

                            Game game = getInstance().getGameHandler().getRandomGame();
                            lobby.startGame(game);
                        } else {
                            Game targetGame = getInstance().getGameHandler().getGame(args[0]);

                            if (targetGame != null) {
                                lobby.startGame(targetGame);
                            } else {
                                respond(chat, TelegramEmoji.RED_CROSS.getText() + " Unknown game!\nUse /gamelist for help.");
                            }
                        }
                    } else {
                        respond(chat, getInstance().getGameHandler().createGameKeyboard().message(TelegramEmoji.JOYSTICK.getText() + "*Please choose a game:*").parseMode(ParseMode.MARKDOWN).build());
                    }
                } else {
                    respond(chat, TelegramEmoji.RED_CROSS.getText() + " A game is already running!");
                }
            } else {
                respond(chat, TelegramEmoji.RED_CROSS.getText() + " You are not in a Lobby!");
            }
        } else {
            respond(chat, TelegramEmoji.RED_CROSS.getText() + " This command can only be executed via a private message to @TelegamesBot");
        }
    }
}