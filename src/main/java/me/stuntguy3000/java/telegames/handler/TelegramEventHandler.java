package me.stuntguy3000.java.telegames.handler;

import me.stuntguy3000.java.telegames.Telegames;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

/**
 * Handles various Telegram events
 *
 * @author stuntguy3000
 */
public class TelegramEventHandler implements Listener {

    /**
     * Represents when a Command Message is received
     *
     * @param event CommandMessageReceivedEvent the event which was received
     */
    @Override
    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        Telegames.getInstance().getCommandHandler().executeCommand(event.getCommand(), event);
    }
}
