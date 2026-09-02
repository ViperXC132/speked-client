package com.spekedclient.util;

public final class ColorUtil {
    private ColorUtil() {}

    public static int withAlpha(int rgb, int alpha) {
        return ((alpha & 0xFF) << 24) | (rgb & 0x00FFFFFF);
    }

    public static int multiplyAlpha(int color, float multiplier) {
        int alpha = (color >>> 24) & 0xFF;
        int scaled = Math.max(0, Math.min(255, Math.round(alpha * multiplier)));
        return (color & 0x00FFFFFF) | (scaled << 24);
    }

    public static int blend(int first, int second, float amount) {
        float t = Math.max(0f, Math.min(1f, amount));
        int a1 = (first >>> 24) & 255, r1 = (first >>> 16) & 255, g1 = (first >>> 8) & 255, b1 = first & 255;
        int a2 = (second >>> 24) & 255, r2 = (second >>> 16) & 255, g2 = (second >>> 8) & 255, b2 = second & 255;
        int a = Math.round(a1 + (a2 - a1) * t);
        int r = Math.round(r1 + (r2 - r1) * t);
        int g = Math.round(g1 + (g2 - g1) * t);
        int b = Math.round(b1 + (b2 - b1) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
