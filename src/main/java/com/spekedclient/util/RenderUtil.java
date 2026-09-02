package com.spekedclient.util;

import net.minecraft.client.gui.DrawContext;

public final class RenderUtil {
    public static final int PANEL = 0xD0101424;
    public static final int PANEL_ALT = 0xCC0E1117;
    public static final int BORDER = 0xFF1E2540;
    public static final int SOFT_BORDER = 0x18FFFFFF;
    public static final int ACCENT = 0xFF3B5BDB;
    public static final int ACCENT_HOVER = 0xFF748FFF;
    public static final int TEXT = 0xFFC8D0E0;
    public static final int DIM = 0xFF8892A8;
    public static final int MUTED = 0xFF3D4A6A;
    public static final int OVERLAY = 0x99060A14;

    private RenderUtil() {}

    public static void panel(DrawContext context, int x, int y, int width, int height) {
        context.fill(x, y, x + width, y + height, PANEL);
        border(context, x, y, width, height, BORDER);
    }

    public static void panelSoft(DrawContext context, int x, int y, int width, int height) {
        context.fill(x, y, x + width, y + height, PANEL_ALT);
        border(context, x, y, width, height, SOFT_BORDER);
    }

    public static void border(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y, x + 1, y + height, color);
        context.fill(x + width - 1, y, x + width, y + height, color);
    }

    public static void dim(DrawContext context, int width, int height) {
        context.fill(0, 0, width, height, OVERLAY);
    }

    public static void dot(DrawContext context, int x, int y, int color) {
        context.fill(x, y, x + 1, y + 1, color);
    }
}
