package com.spekedclient.keybind;

import com.spekedclient.gui.ClickGui;
import com.spekedclient.module.Module;
import com.spekedclient.module.ModuleManager;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public final class KeybindManager {
    private final ModuleManager moduleManager;
    private final Map<String, KeyBinding> bindings = new HashMap<>();
    private KeyBinding clickGuiBinding;

    public KeybindManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    public void register(Module module) {
        if (bindings.containsKey(module.id())) return;
        KeyBinding binding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spekedclient." + module.id(),
                InputUtil.Type.KEYSYM,
                module.keyCode() == 0 ? GLFW.GLFW_KEY_UNKNOWN : module.keyCode(),
                "category.spekedclient"
        ));
        bindings.put(module.id(), binding);
    }

    public void registerClickGui() {
        if (clickGuiBinding != null) return;
        clickGuiBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spekedclient.clickgui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.spekedclient"
        ));
    }

    public void tick() {
        if (clickGuiBinding != null && clickGuiBinding.wasPressed()) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.currentScreen instanceof ClickGui) client.setScreen(null);
            else client.setScreen(new ClickGui());
        }
        for (Map.Entry<String, KeyBinding> entry : bindings.entrySet()) {
            if (!entry.getValue().wasPressed()) continue;
            moduleManager.find(entry.getKey()).ifPresent(Module::toggle);
        }
    }

    public KeyBinding get(String moduleId) { return bindings.get(moduleId); }
    public KeyBinding clickGui() { return clickGuiBinding; }
}
