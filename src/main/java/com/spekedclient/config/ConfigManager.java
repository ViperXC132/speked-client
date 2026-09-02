package com.spekedclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.spekedclient.module.Module;
import com.spekedclient.module.ModuleManager;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path directory = FabricLoader.getInstance().getConfigDir().resolve("pixelforge");
    private final ModuleManager moduleManager;

    public ConfigManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    public void save() {
        try {
            Files.createDirectories(directory);
            for (Module module : moduleManager.all()) saveModule(module);
        } catch (IOException exception) {
            System.err.println("[Speked Client] Could not create config directory: " + exception.getMessage());
        }
    }

    public void load() {
        for (Module module : moduleManager.all()) loadModule(module);
    }

    private void saveModule(Module module) throws IOException {
        JsonObject root = new JsonObject();
        root.addProperty("enabled", module.enabled());
        root.addProperty("keyCode", module.keyCode());
        JsonObject settings = new JsonObject();
        for (var entry : module.settings().entrySet()) {
            JsonElement value = GSON.toJsonTree(entry.getValue());
            settings.add(entry.getKey(), value);
        }
        root.add("settings", settings);
        try (Writer writer = Files.newBufferedWriter(directory.resolve(module.id() + ".json"))) {
            GSON.toJson(root, writer);
        }
    }

    private void loadModule(Module module) {
        Path file = directory.resolve(module.id() + ".json");
        if (!Files.isRegularFile(file)) return;
        try (Reader reader = Files.newBufferedReader(file)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root == null) return;
            if (root.has("keyCode")) module.setKeyCode(root.get("keyCode").getAsInt());
            if (root.has("enabled")) module.setEnabled(root.get("enabled").getAsBoolean());
        } catch (Exception exception) {
            System.err.println("[Speked Client] Invalid config " + file.getFileName() + ": " + exception.getMessage());
        }
    }

    public Path directory() {
        return directory;
    }
}
