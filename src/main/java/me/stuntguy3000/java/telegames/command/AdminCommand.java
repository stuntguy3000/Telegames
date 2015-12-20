package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.Command;
import me.stuntguy3000.java.telegames.util.TelegramEmoji;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

// @author Luke Anderson | stuntguy3000
public class AdminCommand extends Command {
    public AdminCommand() {
        super(Telegames.getInstance(), "/admin Admin use only.", "admin");
    }

    private void broadcast(int user, String message) {
        try {
            TelegramBot.getChat(user).sendMessage(SendableTextMessage.builder().message(TelegramEmoji.GHOST.getText() + " *Broadcast*\n" + message).parseMode(ParseMode.MARKDOWN).disableWebPagePreview(true).build(), TelegramHook.getBot());
        } catch (Exception ignore) {

        }
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        User sender = event.getMessage().getSender();

        if (getInstance().getConfigHandler().getBotSettings().getTelegramAdmins().contains(sender.getId())) {
            String[] args = event.getArgs();

            switch (args.length) {
                case 1: {
                    if (args[0].equalsIgnoreCase("help")) {
                        respond(chat, SendableTextMessage.builder().message("*Admin subcommand help menu:" +
                                "\n/admin help - Admin help menu" +
                                "\n/admin broadcast [message] - Broadcast a message to all known users*").parseMode(ParseMode.MARKDOWN).build());
                        return;
                    }
                    break;
                }
                default: {
                    if (args.length > 1) {
                        if (args[0].equalsIgnoreCase("broadcast")) {
                            StringBuilder broadcastMessage = new StringBuilder();

                            for (int i = 1; i < args.length; i++) {
                                broadcastMessage.append(args[i]).append(" ");
                            }

                            for (int user : getInstance().getConfigHandler().getUserStatistics().getKnownPlayers().keySet()) {
                                broadcast(user, broadcastMessage.toString().replaceAll("~", "\n"));
                            }
                            return;
                        }
                    }
                }
            }

            respond(chat, SendableTextMessage.builder().message("*Unknown subcommand! Try /admin help*").parseMode(ParseMode.MARKDOWN).build());
        }
    }
}