package com.spekedclient;

import com.spekedclient.config.ConfigManager;
import com.spekedclient.config.ProfileManager;
import com.spekedclient.event.EventBus;
import com.spekedclient.hud.HudRenderer;
import com.spekedclient.keybind.KeybindManager;
import com.spekedclient.module.ModuleManager;
import com.spekedclient.module.modules.hud.HudModules;
import com.spekedclient.module.modules.visual.CustomCrosshairModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class SpekedClient implements ClientModInitializer {
    public static final String MOD_ID = "spekedclient";
    public static final String VERSION = "1.0.0";
    public static final String AUTHOR = "ViperXC132";

    private static SpekedClient instance;
    private final ModuleManager moduleManager = new ModuleManager();
    private final EventBus eventBus = new EventBus();
    private final KeybindManager keybindManager = new KeybindManager(moduleManager);
    private final ConfigManager configManager = new ConfigManager(moduleManager);
    private final ProfileManager profileManager = new ProfileManager(moduleManager);
    private final HudRenderer hudRenderer = new HudRenderer(moduleManager);

    @Override
    public void onInitializeClient() {
        instance = this;
        for (var module : HudModules.createAll()) register(module);
        register(new CustomCrosshairModule());
        keybindManager.registerClickGui();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            keybindManager.tick();
            moduleManager.tick();
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> configManager.save());
        profileManager.ensureBuiltIns();
        configManager.load();
    }

    private void register(com.spekedclient.module.Module module) {
        moduleManager.register(module);
        keybindManager.register(module);
    }

    public static SpekedClient get() {
        if (instance == null) throw new IllegalStateException("Speked Client has not initialized");
        return instance;
    }

    public ModuleManager modules() { return moduleManager; }
    public EventBus events() { return eventBus; }
    public KeybindManager keybinds() { return keybindManager; }
    public ConfigManager config() { return configManager; }
    public ProfileManager profiles() { return profileManager; }
    public HudRenderer hud() { return hudRenderer; }
}
