package me.stuntguy3000.java.telegames.game;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import me.stuntguy3000.java.telegames.TelegramHook;
import me.stuntguy3000.java.telegames.game.keyboard.Keyboard;
import me.stuntguy3000.java.telegames.game.keyboard.KeyboardButton;
import me.stuntguy3000.java.telegames.game.keyboard.KeyboardRow;
import me.stuntguy3000.java.telegames.util.string.ConsecutiveId;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.inline.InlineReplyMarkup;
import pro.zackpollard.telegrambot.api.chat.message.ReplyMarkup;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage.SendableTextMessageBuilder;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardButton;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardMarkup;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardMarkup.InlineKeyboardMarkupBuilder;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardMarkup;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder;

public class GameDisplay {

    private Game game;
    private Map<String, Chat> chats;
    private List<String> inlineIds;
    private String text, lastKeyboard;
    private Keyboard keyboard;
    private BiConsumer<Game, String> onMessage;

    public GameDisplay(Game game) {
        this.game = game;
        this.chats = new HashMap<>();
        this.inlineIds = new LinkedList<>();
        this.onMessage = (g, s) -> {};
    }

    public Game getGame() {
        return this.game;
    }

    public void addChat(Chat chat) {
        this.chats.put(chat.getId(), chat);
    }

    public void removeChat(String id) {
        this.chats.remove(id);
    }

    public void removeChat(long id) {
        this.removeChat(String.valueOf(id));
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
        this.update();
    }

    public Keyboard getKeyboard() {
        return this.keyboard;
    }

    public void setKeyboard(Keyboard keyboard) {
        this.keyboard = keyboard;
        this.update();
    }

    public String getLastKeyboard() {
        return this.lastKeyboard;
    }

    public void onMessage(BiConsumer<Game, String> listener) {
        this.onMessage = this.onMessage.andThen(Objects.requireNonNull(listener, "listener"));
    }

    public void message(String message) {
        this.onMessage.accept(this.game, message);
    }

    protected void update() {
        if (!this.chats.isEmpty()) {
            SendableTextMessageBuilder msg = SendableTextMessage.builder();
            msg.message(this.text);
            msg.replyMarkup(this.toKeyboard(this.keyboard));
            msg.disableWebPagePreview(true);
            msg.parseMode(ParseMode.MARKDOWN);
            for (Chat chat : this.chats.values()) {
                chat.sendMessage(msg.build());
            }
        }
        if (!this.inlineIds.isEmpty()) {
            TelegramBot bot = TelegramHook.getBot();
            for (String id : this.inlineIds) {
                bot.editInlineMessageText(id, this.getText(), ParseMode.MARKDOWN, true, this.toInlineKeyboard(this.getKeyboard()));
            }
        }
    }

    private ReplyMarkup toKeyboard(Keyboard keyboard) {
        ReplyKeyboardMarkupBuilder kb = ReplyKeyboardMarkup.builder();
        for (KeyboardRow row : keyboard) {
            kb.addRow(this.toRow(row));
        }
        return kb.build();
    }

    private List<pro.zackpollard.telegrambot.api.keyboards.KeyboardButton> toRow(KeyboardRow row) {
        List<pro.zackpollard.telegrambot.api.keyboards.KeyboardButton> buttons = new LinkedList<>();
        for (KeyboardButton button : row) {
            buttons.add(this.toButton(button));
        }
        return buttons;
    }

    private pro.zackpollard.telegrambot.api.keyboards.KeyboardButton toButton(KeyboardButton button) {
        return pro.zackpollard.telegrambot.api.keyboards.KeyboardButton.builder().text(button.getText()).build();
    }

    private InlineReplyMarkup toInlineKeyboard(Keyboard keyboard) {
        if (keyboard == null) {
            return null;
        }
        String kbid = String.format("gamekb-%s-%s", this.getGame().getID(), ConsecutiveId.next("gamekb-" + this.getGame().getID()));
        InlineKeyboardMarkupBuilder kb = InlineKeyboardMarkup.builder();
        for (int i = 0, j = keyboard.size(); i < j; i++) {
            kb.addRow(this.toInlineRow(kbid, i, keyboard.getRow(i)));
        }
        this.lastKeyboard = kbid;
        return kb.build();
    }

    private List<InlineKeyboardButton> toInlineRow(String kbid, int r, KeyboardRow row) {
        List<InlineKeyboardButton> buttons = new LinkedList<>();
        for (int i = 0, j = row.size(); i < j; i++) {
            buttons.add(this.toInlineButton(kbid, r, i, row.getButton(i)));
        }
        return buttons;
    }

    private InlineKeyboardButton toInlineButton(String kbid, int r, int c, KeyboardButton button) {
        String cb = String.format("%s-%d:%d", kbid, r, c);
        return InlineKeyboardButton.builder().text(button.getText()).callbackData(cb).build();
    }

}
