package com.spekedclient.hud;

import com.spekedclient.module.ModuleManager;
import com.spekedclient.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class HudRenderer {
    private final ModuleManager modules;

    public HudRenderer(ModuleManager modules) {
        this.modules = modules;
    }

    public void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        for (var module : modules.all()) {
            if (!(module instanceof HudModule hud) || !hud.enabled()) continue;
            String value = hud.value();
            if (value == null || value.isBlank()) continue;
            int x = hud.element().x();
            int y = hud.element().y();
            int width = client.textRenderer.getWidth(value) + 10;
            hud.element().setSize(width, 14);
            context.fill(x, y, x + width, y + 14, 0xB0101420);
            RenderUtil.border(context, x, y, width, 14, 0x481E2540);
            context.drawTextWithShadow(client.textRenderer, value, x + 5, y + 3, RenderUtil.TEXT);
        }
    }
}
