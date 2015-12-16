package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;
import me.stuntguy3000.java.telegames.object.Command;
import me.stuntguy3000.java.telegames.object.Lobby;
import me.stuntguy3000.java.telegames.object.LobbyMember;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class KickCommand extends Command {
    public KickCommand() {
        super(Telegames.getInstance(), "/kick <name> Remove a player from the lobby.", "kick");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        User sender = event.getMessage().getSender();
        LobbyHandler lobbyHandler = getInstance().getLobbyHandler();
        String[] args = event.getArgs();

        if (event.getChat().getType() == ChatType.PRIVATE) {
            Lobby lobby = lobbyHandler.getLobby(sender);
            if (lobby != null) {
                if (args.length > 0) {
                    LobbyMember lobbyMember = lobby.getLobbyMember(args[0]);
                    if (lobbyMember != null) {
                        if (lobby.getLobbyOwner().getUserID() == sender.getId() && lobbyMember.getUserID() != sender.getId()) {
                            lobby.kickPlayer(lobbyMember);
                        } else {
                            respond(chat, TelegramEmoji.RED_CROSS.getText() + " You cannot kick this player!");
                        }
                    } else {
                        respond(chat, TelegramEmoji.RED_CROSS.getText() + " Player not found!");
                    }
                } else {
                    respond(chat, TelegramEmoji.RED_CROSS.getText() + " Please specify a username!\nUsage: /kick <name>");
                }
            } else {
                respond(chat, TelegramEmoji.RED_CROSS.getText() + " You are not in an lobby!");
            }
        } else {
            respond(chat, TelegramEmoji.RED_CROSS.getText() + " This command can only be executed via a private message to @TelegamesBot");
        }
    }
}