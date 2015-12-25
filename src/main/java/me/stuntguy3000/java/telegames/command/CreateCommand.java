package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.Command;
import me.stuntguy3000.java.telegames.object.Lobby;
import me.stuntguy3000.java.telegames.object.exception.UserHasLobbyException;
import me.stuntguy3000.java.telegames.object.exception.UserIsMatchmakingException;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
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
        LobbyHandler lobbyHandler = getInstance().getLobbyHandler();
        String[] args = event.getArgs();

        if (lobbyHandler.getLobby(sender) == null) {
            if (args.length > 0) {
                String id = args[0];
                Lobby targetLobby = lobbyHandler.getLobby(id);

                if (targetLobby == null) {
                    respond(chat, TelegramEmoji.RED_CROSS.getText() + " No such lobby exists!");
                } else {
                    targetLobby.userJoin(sender);
                }
            } else {
                Lobby lobby;
                try {
                    lobby = lobbyHandler.tryCreateLobby(sender);
                } catch (UserIsMatchmakingException e) {
                    respond(chat, TelegramEmoji.RED_CROSS.getText() + " You cannot create a lobby while in matchmaking!");
                    return;
                } catch (UserHasLobbyException e) {
                    respond(chat, TelegramEmoji.RED_CROSS.getText() + " You are already have a lobby!");
                    return;
                }

                if (!(event.getChat().getType() == ChatType.PRIVATE)) {
                    SendableTextMessage sendableTextMessage = SendableTextMessage.builder().message(TelegramEmoji.JOYSTICK.getText() + " [Click here to join the lobby!](http://telegram.me/" + TelegramHook.getBot().getBotUsername() + "?start=" + lobby.getLobbyID() + ")").parseMode(ParseMode.MARKDOWN).build();

                    event.getChat().sendMessage(sendableTextMessage, TelegramHook.getBot());
                }
            }
        } else {
            respond(chat, TelegramEmoji.RED_CROSS.getText() + " You are already in a lobby!");
        }
    }
}