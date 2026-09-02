package com.spekedclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.spekedclient.module.Category;
import com.spekedclient.module.Module;
import com.spekedclient.module.ModuleManager;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

public final class ProfileManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Set<String> BUILT_INS = Set.of("PvP", "Survival", "Minigames", "UHC", "SMP");
    private final ModuleManager modules;
    private final Path directory = FabricLoader.getInstance().getConfigDir().resolve("pixelforge").resolve("profiles");

    public ProfileManager(ModuleManager modules) {
        this.modules = modules;
    }

    public void ensureBuiltIns() {
        try {
            Files.createDirectories(directory);
            for (String name : BUILT_INS) {
                Path file = file(name);
                if (Files.notExists(file)) writePreset(name);
            }
        } catch (IOException exception) {
            System.err.println("[Speked Client] Profile setup failed: " + exception.getMessage());
        }
    }

    public void save(String name) throws IOException {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Profile name is blank");
        Files.createDirectories(directory);
        JsonObject root = new JsonObject();
        JsonArray entries = new JsonArray();
        for (Module module : modules.all()) {
            JsonObject item = new JsonObject();
            item.addProperty("id", module.id());
            item.addProperty("enabled", module.enabled());
            item.addProperty("keyCode", module.keyCode());
            entries.add(item);
        }
        root.add("modules", entries);
        try (Writer writer = Files.newBufferedWriter(file(name))) {
            GSON.toJson(root, writer);
        }
    }

    public boolean load(String name) throws IOException {
        Path path = file(name);
        if (!Files.isRegularFile(path)) return false;
        try (Reader reader = Files.newBufferedReader(path)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root == null || !root.has("modules")) return false;
            for (var element : root.getAsJsonArray("modules")) {
                JsonObject item = element.getAsJsonObject();
                String id = item.get("id").getAsString();
                modules.find(id).ifPresent(module -> {
                    if (item.has("keyCode")) module.setKeyCode(item.get("keyCode").getAsInt());
                    module.setEnabled(item.has("enabled") && item.get("enabled").getAsBoolean());
                });
            }
            return true;
        }
    }

    private void writePreset(String name) throws IOException {
        String profile = name.toLowerCase(Locale.ROOT);
        JsonObject root = new JsonObject();
        JsonArray entries = new JsonArray();
        for (Module module : modules.all()) {
            boolean enabled = switch (profile) {
                case "pvp", "minigames", "uhc" -> module.category() == Category.HUD || module.category() == Category.MOVEMENT;
                case "survival", "smp" -> module.category() == Category.HUD;
                default -> false;
            };
            JsonObject item = new JsonObject();
            item.addProperty("id", module.id());
            item.addProperty("enabled", enabled);
            item.addProperty("keyCode", module.keyCode());
            entries.add(item);
        }
        root.add("modules", entries);
        try (Writer writer = Files.newBufferedWriter(file(name))) {
            GSON.toJson(root, writer);
        }
    }

    private Path file(String name) {
        return directory.resolve(name.replaceAll("[^A-Za-z0-9._-]", "_") + ".json");
    }
}
