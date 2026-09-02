package com.spekedclient.gui;

import com.spekedclient.SpekedClient;
import com.spekedclient.hud.HudModule;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Drag/drop HUD editor. The guides are editor-only; gameplay HUD remains text-only
 * and transparent.
 */
public final class HudEditor extends Screen {
    private final Screen parent;
    private final List<HudModule> hudModules = new ArrayList<>();
    private HudModule dragging;

    public HudEditor(Screen parent) {
        super(Text.literal("Speked HUD Editor"));
        this.parent = parent;
        for (var module : SpekedClient.get().modules().all()) {
            if (module instanceof HudModule hud) hudModules.add(hud);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0x88060A14);
        context.drawTextWithShadow(textRenderer, Text.literal("HUD EDITOR"), 14, 12, 0xFFC8D0E0);
        context.drawTextWithShadow(textRenderer, Text.literal("Drag modules · wheel = scale · LMB settings · ESC back"), 14, 26, 0xFF8892A8);

        for (HudModule hud : hudModules) {
            hud.syncLayout();
            String value = hud.value();
            if (value == null || value.isBlank()) continue;

            int x = hud.element().x();
            int y = hud.element().y();
            float scale = hud.element().scale();
            int w = Math.max(20, Math.round(textRenderer.getWidth(value) * scale) + 4);
            int h = Math.max(14, Math.round(11 * scale) + 4);
            boolean selected = hud == dragging || hud.element().contains(mouseX, mouseY);

            // Editor guide only. This is not used by the in-game renderer.
            context.fill(x - 2, y - 2, x + w + 2, y + h + 2, selected ? 0x483B5BDB : 0x181E2540);
            context.drawTextWithShadow(textRenderer, Text.literal(value), x, y, hud.textColor());
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        int mouseX = (int) click.x();
        int mouseY = (int) click.y();
        if (click.buttonInfo().button() == 0) {
            for (int i = hudModules.size() - 1; i >= 0; i--) {
                HudModule hud = hudModules.get(i);
                if (!hud.enabled() || !hud.element().contains(mouseX, mouseY)) continue;
                if (doubled) {
                    client.setScreen(new ModuleSettingsScreen(this, hud));
                } else {
                    dragging = hud;
                    hud.element().startDrag(mouseX, mouseY);
                }
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        if (dragging != null) {
            dragging.element().updateDrag(click.x(), click.y());
            dragging.setLayout(dragging.element().x(), dragging.element().y());
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (dragging != null && click.buttonInfo().button() == 0) {
            dragging.element().stopDrag();
            SpekedClient.get().config().save();
            dragging = null;
            return true;
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (int i = hudModules.size() - 1; i >= 0; i--) {
            HudModule hud = hudModules.get(i);
            if (!hud.enabled() || !hud.element().contains(mouseX, mouseY)) continue;
            hud.setScale(hud.element().scale() + (verticalAmount > 0 ? 0.1f : -0.1f));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

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
