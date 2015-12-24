package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.Command;
import me.stuntguy3000.java.telegames.object.Lobby;
import me.stuntguy3000.java.telegames.object.LobbyMember;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
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
        LobbyHandler lobbyHandler = getInstance().getLobbyHandler();

        if (event.getChat().getType() == ChatType.PRIVATE) {
            Lobby lobby = lobbyHandler.getLobby(sender);
            if (lobby != null) {
                StringBuilder playersList = new StringBuilder();

                for (LobbyMember lobbyMember : lobby.getLobbyMembers()) {
                    playersList.append("@");
                    playersList.append(lobbyMember.getUsername());
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
                respond(chat, TelegramEmoji.RED_CROSS.getText() + " You are not in an lobby!");
            }
        } else {
            respond(chat, TelegramEmoji.RED_CROSS.getText() + " This command can only be executed via a private message to @" + TelegramHook.getBot().getBotUsername());
        }
    }
}