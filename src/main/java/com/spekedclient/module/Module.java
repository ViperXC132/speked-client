package com.spekedclient.module;

import com.google.gson.JsonElement;
import com.spekedclient.config.ConfigManager;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Module {
    private final String id;
    private final String name;
    private final Category category;
    private boolean enabled;
    private int keyCode;
    private final Map<String, Object> settings = new LinkedHashMap<>();

    protected Module(String id, String name, Category category) {
        this(id, name, category, 0);
    }

    protected Module(String id, String name, Category category, int keyCode) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.keyCode = keyCode;
    }

    public final void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) onEnable(); else onDisable();
    }

    public final void toggle() { setEnabled(!enabled); }

    protected void onEnable() {}
    protected void onDisable() {}
    public void onTick() {}

    public String id() { return id; }
    public String name() { return name; }
    public Category category() { return category; }
    public boolean enabled() { return enabled; }
    public int keyCode() { return keyCode; }
    public void setKeyCode(int keyCode) { this.keyCode = keyCode; }

    public Map<String, Object> settings() { return Collections.unmodifiableMap(settings); }

    protected final <T> void setting(String key, T value) { settings.put(key, value); }

    @SuppressWarnings("unchecked")
    protected final <T> T getSetting(String key, Class<T> type, T fallback) {
        Object value = settings.get(key);
        return type.isInstance(value) ? (T) value : fallback;
    }

    /** Applies one JSON value while preserving the setting's declared Java type. */
    public final void loadSetting(String key, JsonElement json) {
        if (!settings.containsKey(key) || json == null || json.isJsonNull()) return;
        Object current = settings.get(key);
        try {
            if (current instanceof Boolean) settings.put(key, json.getAsBoolean());
            else if (current instanceof Integer) settings.put(key, json.getAsInt());
            else if (current instanceof Long) settings.put(key, json.getAsLong());
            else if (current instanceof Float) settings.put(key, json.getAsFloat());
            else if (current instanceof Double) settings.put(key, json.getAsDouble());
            else if (current instanceof String) settings.put(key, json.getAsString());
        } catch (RuntimeException ignored) {
            // A malformed individual setting must never prevent the rest of the client loading.
        }
    }

    /** Updates a setting from the GUI and immediately persists the module. */
    public final void setSetting(String key, Object value) {
        if (!settings.containsKey(key)) return;
        Object current = settings.get(key);
        if (current == null || value == null || current.getClass().isInstance(value)) {
            settings.put(key, value);
            ConfigManager.saveModuleNow(this);
        }
    }

    public void resetSettings() {}
}
