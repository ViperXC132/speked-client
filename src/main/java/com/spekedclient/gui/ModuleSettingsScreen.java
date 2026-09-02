package com.spekedclient.gui;

import com.spekedclient.module.Module;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/** In-game editor for every registered module's real settings. */
public final class ModuleSettingsScreen extends Screen {
    private final Screen parent;
    private final Module module;
    private final List<String> keys = new ArrayList<>();

    public ModuleSettingsScreen(Screen parent, Module module) {
        super(Text.literal(module.name() + " Settings"));
        this.parent = parent;
        this.module = module;
        keys.addAll(module.settings().keySet());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0x88060A14);
        int panelW = Math.min(430, width - 32);
        int x = (width - panelW) / 2;
        int y = 28;
        int h = 24 + keys.size() * 24 + 34;

        context.fill(x, y, x + panelW, y + h, 0xD0101424);
        context.fill(x, y, x + 2, y + h, 0xFF3B5BDB);
        context.drawTextWithShadow(textRenderer, Text.literal(module.name()), x + 12, y + 9, 0xFFC8D0E0);
        context.drawTextWithShadow(textRenderer, Text.literal(module.enabled() ? "ON" : "OFF"), x + panelW - 36, y + 9,
                module.enabled() ? 0xFF6DFF9A : 0xFF8892A8);

        int rowY = y + 27;
        for (String key : keys) {
            Object value = module.settings().get(key);
            boolean hover = mouseX >= x + 8 && mouseX <= x + panelW - 8 && mouseY >= rowY && mouseY < rowY + 20;
            context.fill(x + 7, rowY, x + panelW - 7, rowY + 20, hover ? 0xC01A2040 : 0xB0121622);
            context.drawTextWithShadow(textRenderer, Text.literal(key), x + 13, rowY + 6, 0xFFC8D0E0);
            String shown = String.valueOf(value);
            context.drawTextWithShadow(textRenderer, Text.literal(shown), x + panelW - 13 - textRenderer.getWidth(shown), rowY + 6, 0xFF8892A8);
            rowY += 24;
        }

        context.drawTextWithShadow(textRenderer, Text.literal("LMB change  ·  RMB reverse  ·  ESC back"), x + 12, y + h - 22, 0xFF3D4A6A);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        int mouseX = (int) click.x();
        int mouseY = (int) click.y();
        int panelW = Math.min(430, width - 32);
        int x = (width - panelW) / 2;
        int y = 28;
        int rowY = y + 27;

        for (String key : keys) {
            if (mouseX >= x + 7 && mouseX <= x + panelW - 7 && mouseY >= rowY && mouseY < rowY + 20) {
                if (click.buttonInfo().button() == 0) change(key, 1);
                else if (click.buttonInfo().button() == 1) change(key, -1);
                return true;
            }
            rowY += 24;
        }
        return super.mouseClicked(click, doubled);
    }

    private void change(String key, int direction) {
        Object value = module.settings().get(key);
        Object next = value;
        if (value instanceof Boolean b) next = !b;
        else if (value instanceof Float f) next = clamp(f + direction * 0.25f, 0.25f, 4.0f);
        else if (value instanceof Double d) next = clamp(d + direction * 0.25d, 0.0d, 4.0d);
        else if (value instanceof Integer i) next = i + direction;
        else if (value instanceof Long l) next = l + direction;
        if (next != value) module.setSetting(key, next);
    }

    private static float clamp(float value, float min, float max) { return Math.max(min, Math.min(max, value)); }
    private static double clamp(double value, double min, double max) { return Math.max(min, Math.min(max, value)); }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            client.setScreen(parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() { return false; }
}
