package me.stuntguy3000.java.telegames.game.scripted;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import lombok.AccessLevel;
import lombok.Getter;
import me.stuntguy3000.java.telegames.TelegramHook;
import me.stuntguy3000.java.telegames.game.Game;
import me.stuntguy3000.java.telegames.game.GameDisplay;
import me.stuntguy3000.java.telegames.game.GameUser;

public class ScriptedGame extends Game {

    public static final ScriptEngineManager ENGINE_MANAGER = new ScriptEngineManager();

    @Getter
    private final String gameName, gameVersion;

    @Getter(AccessLevel.PACKAGE)
    private final ScriptEngine engine;

    private final ScriptedGameListener listener;
    private final GameDisplay display;
    private final Map<String, Object> data;

    public ScriptedGame(File file) {
        Objects.requireNonNull(file, "file cannot be null");
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist");
        }
        this.engine = ScriptedGame.ENGINE_MANAGER.getEngineByName("javascript");
        Bindings bindings = this.engine.createBindings();
        bindings.put("events", this.listener = new ScriptedGameListener());
        bindings.put("display", this.display = new GameDisplay(this));
        bindings.put("data", this.data = new HashMap<>());
        this.engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        try {
            this.engine.eval(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Invalid script file.", e);
        } catch (ScriptException e) {
            throw new IllegalStateException("Invalid script syntax.", e);
        }
        if (this.engine.get("GAME_NAME") == null) {
            throw new IllegalArgumentException("Missing game name.");
        }
        if (this.engine.get("GAME_VERSION") == null) {
            throw new IllegalArgumentException("Missing game version.");
        }
        this.gameName = this.engine.get("GAME_NAME").toString();
        this.gameVersion = this.engine.get("GAME_VERSION").toString();
    }

    public Map<String, Object> getData() {
        return Collections.unmodifiableMap(this.data);
    }

    @Override
    public void start() {
        this.listener.onStart(this);
    }

    @Override
    public void stop() {
        this.listener.onFinish(this);
    }

    @Override
    public void join(GameUser user) {
        this.listener.onJoin(this, user);
        this.display.addChat(TelegramHook.getBot().getChat(user.getUser().getId()));
    }

    @Override
    public void quit(GameUser user) {
        this.listener.onQuit(this, user);
        this.display.removeChat(user.getUser().getId());
    }

}
