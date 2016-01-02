package me.stuntguy3000.java.telegames.command;

import me.stuntguy3000.java.telegames.Telegames;
import me.stuntguy3000.java.telegames.hook.TelegramHook;
import me.stuntguy3000.java.telegames.object.command.Command;
import me.stuntguy3000.java.telegames.object.lobby.Lobby;
import me.stuntguy3000.java.telegames.util.string.Emoji;
import me.stuntguy3000.java.telegames.util.string.Lang;
import me.stuntguy3000.java.telegames.util.string.StringUtil;
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
            TelegramBot.getChat(user).sendMessage(SendableTextMessage.builder().message(Emoji.GHOST.getText() + " *Broadcast*\n" + message).parseMode(ParseMode.MARKDOWN).disableWebPagePreview(true).build(), TelegramHook.getBot());
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
                        respond(chat, SendableTextMessage.builder().message(Lang.COMMAND_ADMIN_HELP).parseMode(ParseMode.MARKDOWN).build());
                        return;
                    } else if (args[0].equalsIgnoreCase("list")) {
                        StringBuilder lobbylist = new StringBuilder();
                        lobbylist.append(Lang.MISC_HEADER_LOBBYLIST);
                        lobbylist.append("\n");

                        for (Lobby lobby : getInstance().getLobbyHandler().getActiveLobbies().values()) {
                            lobbylist.append(String.format(Lang.COMMAND_ADMIN_LOBBY, lobby.getLobbyID(), StringUtil.markdownSafe(lobby.getLobbyOwner().getUsername()), lobby.getTelegramUsers().size(), StringUtil.millisecondsToHumanReadable(System.currentTimeMillis() - lobby.getLastLobbyAction()), lobby.getCurrentGame() != null ? "Playing " + lobby.getCurrentGame().getGameName() : ""));
                            lobbylist.append("\n");
                        }

                        respond(chat, SendableTextMessage.builder().message(lobbylist.toString()).parseMode(ParseMode.MARKDOWN).build());
                        return;
                    } else if (args[0].equalsIgnoreCase("botfather")) {
                        respond(chat, SendableTextMessage.builder().message(Lang.MISC_HEADER_BOTFATHER + "\n" + getInstance().getCommandHandler().getBotFatherString()).parseMode(ParseMode.MARKDOWN).build());
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

            respond(chat, SendableTextMessage.builder().message(Lang.COMMAND_ADMIN_UNKNOWN_SUBCOMMAND).parseMode(ParseMode.MARKDOWN).build());
        }
    }
}