package com.spekedclient.gui.screens;

import com.spekedclient.SpekedClient;
import com.spekedclient.module.modules.visual.CustomCrosshairModule;
import com.spekedclient.util.RenderUtil;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class CrosshairScreen extends Screen {
    private final Screen parent;
    private final CustomCrosshairModule module;

    public CrosshairScreen(Screen parent) {
        super(Text.literal("Custom Crosshair"));
        this.parent = parent;
        this.module = (CustomCrosshairModule) SpekedClient.get().modules().find("custom-crosshair").orElseThrow();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.fill(0, 0, width, height, 0xFF070A12);
        RenderUtil.dim(context, width, height);
        int panelX = width / 2 - 260;
        int panelY = height / 2 - 190;
        RenderUtil.panel(context, panelX, panelY, 520, 380);
        context.drawTextWithShadow(textRenderer, Text.literal("CUSTOM CROSSHAIR"), panelX + 20, panelY + 18, RenderUtil.TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("Live preview · click a setting to cycle"), panelX + 20, panelY + 34, RenderUtil.DIM);

        int previewX = panelX + 390;
        int previewY = panelY + 130;
        RenderUtil.panelSoft(context, panelX + 320, panelY + 55, 170, 150);
        context.drawTextWithShadow(textRenderer, Text.literal("PREVIEW"), panelX + 335, panelY + 68, RenderUtil.MUTED);
        context.fill(previewX - 70, previewY - 50, previewX + 70, previewY + 70, 0xFF090D16);
        module.draw(context, previewX, previewY);

        row(context, panelX + 20, panelY + 62, "Style", module.style().name());
        row(context, panelX + 20, panelY + 94, "Size", Integer.toString(module.size()));
        row(context, panelX + 20, panelY + 126, "Thickness", Integer.toString(module.thickness()));
        row(context, panelX + 20, panelY + 158, "Gap", Integer.toString(module.gap()));
        row(context, panelX + 20, panelY + 190, "Opacity", Integer.toString(module.opacity()));
        row(context, panelX + 20, panelY + 222, "Outline", module.outline() ? "ON" : "OFF");
        row(context, panelX + 20, panelY + 254, "Replace vanilla", module.replaceVanilla() ? "ON" : "OFF");
        row(context, panelX + 20, panelY + 286, "Enabled", module.enabled() ? "ON" : "OFF");
        row(context, panelX + 20, panelY + 318, "Preset color", String.format("#%06X", module.color() & 0xFFFFFF));

        context.drawTextWithShadow(textRenderer, Text.literal("CUSTOM arms: T " + module.top() + "  B " + module.bottom() + "  L " + module.left() + "  R " + module.right() + "  Dot " + (module.centerDot() ? "ON" : "OFF")), panelX + 20, panelY + 348, RenderUtil.DIM);
    }

    private void row(DrawContext context, int x, int y, String label, String value) {
        RenderUtil.pill(context, x, y, 275, 26, 0xB0101420, 0x241E2540);
        context.drawTextWithShadow(textRenderer, Text.literal(label), x + 9, y + 8, RenderUtil.TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal(value), x + 175, y + 8, RenderUtil.DIM);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.buttonInfo().button() != GLFW.GLFW_MOUSE_BUTTON_LEFT) return super.mouseClicked(click, doubled);
        int px = width / 2 - 260;
        int py = height / 2 - 190;
        int y = (int) click.y();
        int x = (int) click.x();
        if (x < px + 20 || x > px + 295) return super.mouseClicked(click, doubled);
        int index = Math.floorDiv(y - (py + 62), 32);
        switch (index) {
            case 0 -> module.setStyle(CustomCrosshairModule.Style.values()[(module.style().ordinal() + 1) % CustomCrosshairModule.Style.values().length]);
            case 1 -> module.setSize(module.size() >= 20 ? 1 : module.size() + 1);
            case 2 -> module.setThickness(module.thickness() >= 6 ? 1 : module.thickness() + 1);
            case 3 -> module.setGap(module.gap() >= 12 ? 0 : module.gap() + 1);
            case 4 -> module.setOpacity(module.opacity() >= 255 ? 32 : module.opacity() + 32);
            case 5 -> module.setOutline(!module.outline());
            case 6 -> module.setReplaceVanilla(!module.replaceVanilla());
            case 7 -> module.setEnabled(!module.enabled());
            case 8 -> module.setColor(module.color() == 0xFFFFFFFF ? 0xFF3B5BDB : module.color() == 0xFF3B5BDB ? 0xFFFF5B6E : 0xFFFFFFFF);
            default -> { return super.mouseClicked(click, doubled); }
        }
        SpekedClient.get().config().save();
        return true;
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}
