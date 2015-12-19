package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.Command;
import me.stuntguy3000.java.telegames.object.Lobby;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class JoinCommand extends Command {
    public JoinCommand() {
        super(Telegames.getInstance(), "/join <ID> Join a lobby.", "join");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        User sender = event.getMessage().getSender();
        LobbyHandler lobbyHandler = getInstance().getLobbyHandler();
        String[] args = event.getArgs();

        if (event.getChat().getType() == ChatType.PRIVATE) {
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
                    respond(chat, TelegramEmoji.RED_CROSS.getText() + " Please specify a lobby ID!\nUsage: /joinlobby <ID>");
                }
            } else {
                respond(chat, TelegramEmoji.RED_CROSS.getText() + " You are already in a lobby!");
            }
        } else {
            respond(chat, TelegramEmoji.RED_CROSS.getText() + " This command can only be executed via a private message to @" + TelegramHook.getBot().getBotUsername());
        }
    }
}