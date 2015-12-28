package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.KeyboardHandler;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;
import me.stuntguy3000.java.telegames.object.command.Command;
import me.stuntguy3000.java.telegames.object.game.Game;
import me.stuntguy3000.java.telegames.object.lobby.Lobby;
import me.stuntguy3000.java.telegames.object.user.TelegramUser;
import me.stuntguy3000.java.telegames.util.string.Lang;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class PlayCommand extends Command {
    public PlayCommand() {
        super(Telegames.getInstance(), "/play <game> Play a game.", "play", "startgame");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        User sender = event.getMessage().getSender();
        TelegramUser user = new TelegramUser(sender);
        LobbyHandler lobbyHandler = getInstance().getLobbyHandler();
        String[] args = event.getArgs();

        if (event.getChat().getType() == ChatType.PRIVATE) {
            Lobby lobby = lobbyHandler.getLobby(user);
            if (lobby != null) {
                Game lobbyGame = lobby.getCurrentGame();

                if (lobbyGame == null) {
                    if (args.length > 0) {
                        if (args[0].equalsIgnoreCase("random")) {
                            respond(chat, Lang.COMMAND_PLAY_RANDOM);

                            Game game = getInstance().getGameHandler().getRandomGame();
                            lobby.startGame(game);
                        } else {
                            Game targetGame = getInstance().getGameHandler().getGame(args[0]);

                            if (targetGame != null) {
                                lobby.startGame(targetGame);
                            } else {
                                respond(chat, Lang.ERROR_GAME_NOT_FOUND);
                            }
                        }
                    } else {
                        respond(chat, KeyboardHandler.createGameSelector().message(Lang.COMMAND_PLAY).parseMode(ParseMode.MARKDOWN).build());
                    }
                } else {
                    respond(chat, Lang.ERROR_GAME_RUNNING);
                }
            } else {
                respond(chat, Lang.ERROR_USER_NOT_IN_LOBBY);
            }
        } else {
            respond(chat, Lang.ERROR_COMMAND_PM_ONLY);
        }
    }
}