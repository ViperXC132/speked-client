package com.spekedclient.module.modules.visual;

import com.spekedclient.module.Category;
import com.spekedclient.module.Module;
import com.spekedclient.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class CustomCrosshairModule extends Module {
    public enum Style { CROSS, DOT, CIRCLE, CROSS_DOT, GAP, CUSTOM }

    private Style style = Style.CROSS;
    private int size = 5;
    private int thickness = 2;
    private int gap = 3;
    private int opacity = 255;
    private int color = 0xFFFFFFFF;
    private boolean outline = true;
    private boolean replaceVanilla = true;
    private int top = 5, bottom = 5, left = 5, right = 5;
    private boolean centerDot;

    public CustomCrosshairModule() {
        super("custom-crosshair", "Custom Crosshair", Category.VISUALS);
    }

    public void render(DrawContext context) {
        if (!enabled()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        draw(context, client.getWindow().getScaledWidth() / 2, client.getWindow().getScaledHeight() / 2);
    }

    public void draw(DrawContext context, int centerX, int centerY) {
        int main = (color & 0x00FFFFFF) | ((opacity & 0xFF) << 24);
        int outlineColor = (0x000000 | (((Math.min(255, opacity + 80)) & 0xFF) << 24));
        switch (style) {
            case DOT -> rect(context, centerX - thickness / 2, centerY - thickness / 2, thickness, thickness, main, outlineColor);
            case CIRCLE -> drawCircle(context, centerX, centerY, size, main);
            case CROSS_DOT -> { drawCross(context, centerX, centerY, gap, size, thickness, main, outlineColor); rect(context, centerX - thickness / 2, centerY - thickness / 2, thickness, thickness, main, outlineColor); }
            case GAP -> drawCross(context, centerX, centerY, gap, size, thickness, main, outlineColor);
            case CUSTOM -> {
                arm(context, centerX, centerY, 0, -top, thickness, main, outlineColor);
                arm(context, centerX, centerY, 0, bottom, thickness, main, outlineColor);
                arm(context, centerX, centerY, -left, 0, thickness, main, outlineColor);
                arm(context, centerX, centerY, right, 0, thickness, main, outlineColor);
                if (centerDot) rect(context, centerX - thickness / 2, centerY - thickness / 2, thickness, thickness, main, outlineColor);
            }
            default -> drawCross(context, centerX, centerY, gap, size, thickness, main, outlineColor);
        }
    }

    private void drawCross(DrawContext context, int x, int y, int gap, int length, int thick, int main, int outline) {
        arm(context, x, y, 0, -(gap + length), thick, main, outline);
        arm(context, x, y, 0, gap + length, thick, main, outline);
        arm(context, x, y, -(gap + length), 0, thick, main, outline);
        arm(context, x, y, gap + length, 0, thick, main, outline);
    }

    private void arm(DrawContext context, int cx, int cy, int dx, int dy, int thick, int main, int outlineColor) {
        if (dx != 0) {
            int x = dx < 0 ? cx + dx : cx + 1;
            int width = Math.max(1, Math.abs(dx));
            rect(context, x, cy - thick / 2, width, thick, main, outlineColor);
        } else if (dy != 0) {
            int y = dy < 0 ? cy + dy : cy + 1;
            int height = Math.max(1, Math.abs(dy));
            rect(context, cx - thick / 2, y, thick, height, main, outlineColor);
        }
    }

    private void rect(DrawContext context, int x, int y, int w, int h, int main, int outlineColor) {
        if (outline) {
            context.fill(x - 1, y - 1, x + w + 1, y + h + 1, outlineColor);
        }
        context.fill(x, y, x + w, y + h, main);
    }

    private void drawCircle(DrawContext context, int cx, int cy, int radius, int main) {
        int r = Math.max(2, radius);
        for (int y = -r; y <= r; y++) {
            for (int x = -r; x <= r; x++) {
                int d = x * x + y * y;
                if (d >= (r - 1) * (r - 1) && d <= r * r) RenderUtil.dot(context, cx + x, cy + y, main);
            }
        }
    }

    public Style style() { return style; }
    public void setStyle(Style style) { this.style = style == null ? Style.CROSS : style; }
    public int size() { return size; }
    public void setSize(int value) { size = Math.max(1, Math.min(30, value)); }
    public int thickness() { return thickness; }
    public void setThickness(int value) { thickness = Math.max(1, Math.min(10, value)); }
    public int gap() { return gap; }
    public void setGap(int value) { gap = Math.max(0, Math.min(20, value)); }
    public int opacity() { return opacity; }
    public void setOpacity(int value) { opacity = Math.max(0, Math.min(255, value)); }
    public int color() { return color; }
    public void setColor(int value) { color = value | 0xFF000000; }
    public boolean outline() { return outline; }
    public void setOutline(boolean value) { outline = value; }
    public boolean replaceVanilla() { return replaceVanilla; }
    public void setReplaceVanilla(boolean value) { replaceVanilla = value; }
    public int top() { return top; }
    public int bottom() { return bottom; }
    public int left() { return left; }
    public int right() { return right; }
    public void setCustomArms(int top, int bottom, int left, int right) { this.top = Math.max(0, top); this.bottom = Math.max(0, bottom); this.left = Math.max(0, left); this.right = Math.max(0, right); }
    public boolean centerDot() { return centerDot; }
    public void setCenterDot(boolean value) { centerDot = value; }
}
