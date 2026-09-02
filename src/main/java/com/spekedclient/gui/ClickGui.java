package com.spekedclient.gui;

import com.spekedclient.SpekedClient;
import com.spekedclient.module.Category;
import com.spekedclient.module.Module;
import com.spekedclient.util.RenderUtil;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

/** Lunar-style in-game module browser with real per-module setting controls. */
public final class ClickGui extends Screen {
    private final Set<Category> collapsed = new HashSet<>();
    private TextFieldWidget search;

    public ClickGui() {
        super(Text.literal("Speked Client ClickGUI"));
    }

    @Override
    protected void init() {
        search = new TextFieldWidget(textRenderer, width / 2 - 170, 18, 340, 24, Text.literal("Search modules"));
        search.setMaxLength(64);
        addDrawableChild(search);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.fill(0, 0, width, height, 0x99060A14);
        context.drawTextWithShadow(textRenderer, Text.literal("SPEKED CLIENT"), 18, 20, RenderUtil.TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("LMB toggle · RMB settings/collapse · HUD EDITOR"), 18, 34, RenderUtil.DIM);
        RenderUtil.panelSoft(context, width / 2 - 175, 14, 350, 32);

        int editorX = width - 110;
        RenderUtil.pill(context, editorX, 18, 96, 24, 0xD0121622, 0x481E2540);
        context.drawTextWithShadow(textRenderer, Text.literal("HUD EDITOR"), editorX + 11, 25, RenderUtil.TEXT);

        String query = search == null ? "" : search.getText().trim().toLowerCase();
        Category[] categories = Category.values();
        int columnWidth = Math.max(150, (width - 48) / 4);
        for (int i = 0; i < categories.length; i++) {
            int col = i % 4;
            int row = i / 4;
            int x = 12 + col * columnWidth;
            int y = 62 + row * 220;
            drawCategory(context, categories[i], x, y, columnWidth - 10, query, mouseX, mouseY);
        }
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    private void drawCategory(DrawContext context, Category category, int x, int y, int w, String query, int mouseX, int mouseY) {
        var modules = SpekedClient.get().modules().byCategory(category).stream()
                .filter(module -> query.isEmpty() || module.name().toLowerCase().contains(query) || module.id().contains(query))
                .toList();
        boolean isCollapsed = collapsed.contains(category);
        int h = 30 + (isCollapsed ? 0 : Math.min(modules.size(), 7) * 24);
        RenderUtil.panel(context, x, y, w, h);
        context.drawTextWithShadow(textRenderer, Text.literal(category.displayName()), x + 10, y + 10, RenderUtil.TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal(isCollapsed ? "+" : "−"), x + w - 18, y + 10, RenderUtil.DIM);
        if (isCollapsed) return;
        int row = 0;
        for (Module module : modules) {
            if (row >= 7) break;
            int ry = y + 30 + row * 24;
            boolean hover = mouseX >= x + 6 && mouseX < x + w - 6 && mouseY >= ry && mouseY < ry + 22;
            int fill = hover ? 0xE0181D2B : 0xA0101420;
            RenderUtil.pill(context, x + 6, ry, w - 12, 22, fill, 0x201E2540);
            context.drawTextWithShadow(textRenderer, Text.literal(module.name()), x + 12, ry + 7,
                    module.enabled() ? RenderUtil.ACCENT_HOVER : RenderUtil.TEXT);
            context.drawTextWithShadow(textRenderer, Text.literal(module.enabled() ? "ON" : "OFF"), x + w - 42, ry + 7,
                    module.enabled() ? RenderUtil.ACCENT_HOVER : RenderUtil.MUTED);
            context.drawTextWithShadow(textRenderer, Text.literal("⚙"), x + w - 18, ry + 7, RenderUtil.DIM);
            row++;
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (search != null && search.mouseClicked(click, doubled)) return true;
        int button = click.buttonInfo().button();
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT && button != GLFW.GLFW_MOUSE_BUTTON_RIGHT) return super.mouseClicked(click, doubled);

        int editorX = width - 110;
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && inside(click.x(), click.y(), editorX, 18, 96, 24)) {
            client.setScreen(new HudEditor(this));
            return true;
        }

        String query = search == null ? "" : search.getText().trim().toLowerCase();
        Category[] categories = Category.values();
        int columnWidth = Math.max(150, (width - 48) / 4);
        for (int i = 0; i < categories.length; i++) {
            Category category = categories[i];
            int x = 12 + (i % 4) * columnWidth;
            int y = 62 + (i / 4) * 220;
            int w = columnWidth - 10;
            if (inside(click.x(), click.y(), x, y, w, 30)) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    if (!collapsed.add(category)) collapsed.remove(category);
                }
                return true;
            }
            if (collapsed.contains(category)) continue;

            var modules = SpekedClient.get().modules().byCategory(category).stream()
                    .filter(module -> query.isEmpty() || module.name().toLowerCase().contains(query) || module.id().contains(query))
                    .limit(7).toList();
            for (int row = 0; row < modules.size(); row++) {
                if (!inside(click.x(), click.y(), x + 6, y + 30 + row * 24, w - 12, 22)) continue;
                Module module = modules.get(row);
                if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    module.toggle();
                    SpekedClient.get().config().save();
                } else {
                    client.setScreen(new ModuleSettingsScreen(this, module));
                }
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    private static boolean inside(double px, double py, int x, int y, int w, int h) {
        return px >= x && px < x + w && py >= y && py < y + h;
    }

    @Override
    public boolean shouldPause() { return false; }

    @Override
    public void close() { client.setScreen(null); }
}
