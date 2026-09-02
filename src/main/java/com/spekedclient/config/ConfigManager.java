package com.spekedclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.spekedclient.module.Module;
import com.spekedclient.module.ModuleManager;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Per-instance client configuration. Fabric's config directory belongs to the
 * active Minecraft instance, so this never writes into a global user directory.
 */
public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String DIRECTORY_NAME = "pixelforge";
    private final Path directory = FabricLoader.getInstance().getConfigDir().resolve(DIRECTORY_NAME);
    private final ModuleManager moduleManager;

    public ConfigManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
        ensureDirectory();
    }

    private void ensureDirectory() {
        try {
            Files.createDirectories(directory);
        } catch (IOException exception) {
            System.err.println("[Speked Client] Could not create " + directory + ": " + exception.getMessage());
        }
    }

    public void save() {
        ensureDirectory();
        for (Module module : moduleManager.all()) saveModule(module);
    }

    public void load() {
        ensureDirectory();
        for (Module module : moduleManager.all()) loadModule(module);
    }

    private void saveModule(Module module) {
        try {
            saveModuleFile(module);
        } catch (IOException exception) {
            System.err.println("[Speked Client] Could not save " + module.id() + ": " + exception.getMessage());
        }
    }

    /** Public hook used by GUI setting changes so changes are written immediately. */
    public static void saveModuleNow(Module module) {
        // The active client owns the canonical config manager.
        try {
            ConfigManager manager = com.spekedclient.SpekedClient.get().config();
            manager.ensureDirectory();
            manager.saveModuleFile(module);
        } catch (Exception exception) {
            System.err.println("[Speked Client] Could not save setting for " + module.id() + ": " + exception.getMessage());
        }
    }

    private void saveModuleFile(Module module) throws IOException {
        JsonObject root = new JsonObject();
        root.addProperty("enabled", module.enabled());
        root.addProperty("keyCode", module.keyCode());

        JsonObject settings = new JsonObject();
        for (var entry : module.settings().entrySet()) {
            settings.add(entry.getKey(), GSON.toJsonTree(entry.getValue()));
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
            if (root.has("settings") && root.get("settings").isJsonObject()) {
                JsonObject settings = root.getAsJsonObject("settings");
                for (var entry : settings.entrySet()) module.loadSetting(entry.getKey(), entry.getValue());
            }
            if (root.has("enabled")) module.setEnabled(root.get("enabled").getAsBoolean());
        } catch (Exception exception) {
            System.err.println("[Speked Client] Invalid config " + file.getFileName() + ": " + exception.getMessage());
        }
    }

    public Path directory() { return directory; }
}
