package com.spekedclient.hud;

import com.spekedclient.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/**
 * Renders HUD modules as transparent text only. No panel, framebuffer, shader,
 * or raw OpenGL state is introduced, keeping the renderer VulkanMod-friendly.
 */
public final class HudRenderer {
    private final ModuleManager modules;

    public HudRenderer(ModuleManager modules) {
        this.modules = modules;
    }

    public void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        for (var module : modules.all()) {
            if (!(module instanceof HudModule hud) || !hud.enabled()) continue;
            String value = hud.value();
            if (value == null || value.isBlank()) continue;

            hud.syncLayout();
            int x = hud.element().x();
            int y = hud.element().y();
            float scale = hud.element().scale();
            int color = hud.textColor();

            context.getMatrices().push();
            context.getMatrices().translate(x, y, 0.0f);
            context.getMatrices().scale(scale, scale, 1.0f);
            if (hud.textShadow()) {
                context.drawTextWithShadow(client.textRenderer, Text.literal(value), 0, 0, color);
            } else {
                context.drawText(client.textRenderer, Text.literal(value), 0, 0, color, false);
            }
            context.getMatrices().pop();

            int width = Math.max(1, Math.round(client.textRenderer.getWidth(value) * scale));
            int height = Math.max(1, Math.round(9 * scale));
            hud.element().setSize(width, height);
        }
    }
}
