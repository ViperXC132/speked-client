package com.spekedclient.module.modules.hud;

import com.spekedclient.hud.HudModule;
import net.minecraft.client.MinecraftClient;

abstract class BaseHudModule extends HudModule {
    protected BaseHudModule(String id, String name) { super(id, name); }
    protected final MinecraftClient mc() { return MinecraftClient.getInstance(); }
    protected final boolean inWorld() { return mc().player != null && mc().world != null; }
}
