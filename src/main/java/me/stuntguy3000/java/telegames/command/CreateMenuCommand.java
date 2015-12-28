package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.handler.KeyboardHandler;
import me.stuntguy3000.java.telegames.handler.LobbyHandler;
import me.stuntguy3000.java.telegames.object.command.Command;
import me.stuntguy3000.java.telegames.object.user.TelegramUser;
import me.stuntguy3000.java.telegames.util.string.Lang;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class CreateMenuCommand extends Command {
    public CreateMenuCommand() {
        super(Telegames.getInstance(), "/createmenu Show the creation menu.", "createmenu");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        User sender = event.getMessage().getSender();
        TelegramUser user = new TelegramUser(sender);
        LobbyHandler lobbyHandler = getInstance().getLobbyHandler();

        if (event.getChat().getType() == ChatType.PRIVATE) {
            if (lobbyHandler.getLobby(user) == null) {
                respond(chat, KeyboardHandler.createLobbyCreationMenu().message(Lang.COMMAND_CREATEMENU).parseMode(ParseMode.MARKDOWN).build());
            } else {
                respond(chat, Lang.ERROR_USER_IN_LOBBY);
            }
        } else {
            respond(chat, Lang.ERROR_COMMAND_PM_ONLY);
        }
    }
}