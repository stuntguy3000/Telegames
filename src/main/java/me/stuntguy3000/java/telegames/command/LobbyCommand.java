package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.command.Command;
import me.stuntguy3000.java.telegames.object.lobby.Lobby;
import me.stuntguy3000.java.telegames.object.user.TelegramUser;
import me.stuntguy3000.java.telegames.util.string.Emoji;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class LobbyCommand extends Command {
    public LobbyCommand() {
        super(Telegames.getInstance(), "/lobby View current lobby information.", "lobby");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        User sender = event.getMessage().getSender();
        TelegramUser user = new TelegramUser(sender);
        LobbyHandler lobbyHandler = getInstance().getLobbyHandler();

        if (event.getChat().getType() == ChatType.PRIVATE) {
            Lobby lobby = lobbyHandler.getLobby(user);
            if (lobby != null) {
                StringBuilder playersList = new StringBuilder();

                for (TelegramUser telegramUser : lobby.getTelegramUsers()) {
                    playersList.append("@");
                    playersList.append(telegramUser.getUsername());
                    playersList.append(", ");
                }

                SendableTextMessage message = SendableTextMessage.builder().message("*Lobby Information:*\n" +
                        "*ID:* " + lobby.getLobbyID() + "\n" +
                        "*Custom Name:* " + (lobby.getCustomName() == null ? "No name set." : lobby.getCustomName()) + "\n" +
                        "*Game:* " + (lobby.getCurrentGame() == null ? "No active game." : lobby.getCurrentGame().getGameName()) + "\n" +
                        "*Players:* " + playersList.substring(0, playersList.length() - 2) + "\n" +
                        "*Join Link:* http://telegram.me/TelegamesBot?start=" + lobby.getLobbyID()).parseMode(ParseMode.MARKDOWN).build();
                respond(chat, message);
            } else {
                respond(chat, Emoji.RED_CROSS.getText() + " You are not in an lobby!");
            }
        } else {
            respond(chat, Emoji.RED_CROSS.getText() + " This command can only be executed via a private message to @" + TelegramHook.getBot().getBotUsername());
        }
    }
}