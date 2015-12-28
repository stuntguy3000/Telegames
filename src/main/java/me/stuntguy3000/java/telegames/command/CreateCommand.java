package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.KeyboardHandler;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.command.Command;
import me.stuntguy3000.java.telegames.object.exception.*;
import me.stuntguy3000.java.telegames.object.lobby.Lobby;
import me.stuntguy3000.java.telegames.object.user.TelegramUser;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
import me.stuntguy3000.java.telegames.util.string.Lang;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class CreateCommand extends Command {
    public CreateCommand() {
        super(Telegames.getInstance(), "/create Create a lobby.", "create", "start");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        User sender = event.getMessage().getSender();
        TelegramUser user = new TelegramUser(sender);
        LobbyHandler lobbyHandler = getInstance().getLobbyHandler();
        String[] args = event.getArgs();

        if (lobbyHandler.getLobby(user) == null) {
            if (args.length > 0) {
                String id = args[0];
                Lobby targetLobby = lobbyHandler.getLobby(id);

                if (targetLobby == null) {
                    respond(chat, Lang.ERROR_LOBBY_NOT_FOUND);
                } else {
                    try {
                        targetLobby.userJoin(user);
                    } catch (LobbyLockedException | UserBannedException | LobbyFullException e) {
                        SendableTextMessage sendableTextMessage = KeyboardHandler.createLobbyCreationMenu().message(TelegramEmoji.RED_CROSS.getText() + " *You cannot join this lobby.*").parseMode(ParseMode.MARKDOWN).build();
                        respond(TelegramBot.getChat(sender.getId()), sendableTextMessage);
                    }
                }
            } else {
                Lobby lobby;
                try {
                    lobby = lobbyHandler.tryCreateLobby(user);
                } catch (UserIsMatchmakingException e) {
                    respond(chat, Lang.ERROR_LOBBY_CREATE_MATCHMAKING);
                    return;
                } catch (UserHasLobbyException e) {
                    respond(chat, Lang.ERROR_USER_IN_LOBBY);
                    return;
                }

                if (!(event.getChat().getType() == ChatType.PRIVATE)) {
                    SendableTextMessage sendableTextMessage = SendableTextMessage.builder().message(TelegramEmoji.JOYSTICK.getText() + " [Click here to join the lobby!](http://telegram.me/" + TelegramHook.getBot().getBotUsername() + "?start=" + lobby.getLobbyID() + ")").parseMode(ParseMode.MARKDOWN).build();
                    respond(chat, sendableTextMessage);
                }
            }
        } else {
            respond(chat, Lang.ERROR_USER_IN_LOBBY);
        }
    }
}