package com.spekedclient.hud;

public final class HudElement {
    private final String id;
    private int x;
    private int y;
    private int width;
    private int height;
    private float scale;
    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;

    public HudElement(String id, int x, int y, int width, int height) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.scale = 1.0f;
    }

    public String id() { return id; }
    public int x() { return x; }
    public int y() { return y; }
    public int width() { return width; }
    public int height() { return height; }
    public float scale() { return scale; }

    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public void setSize(int width, int height) { this.width = width; this.height = height; }
    public void setScale(float scale) { this.scale = Math.max(0.25f, Math.min(4.0f, scale)); }

    public boolean contains(double mouseX, double mouseY) {
        int scaledWidth = Math.max(1, Math.round(width * scale));
        int scaledHeight = Math.max(1, Math.round(height * scale));
        return mouseX >= x && mouseX <= x + scaledWidth && mouseY >= y && mouseY <= y + scaledHeight;
    }

    public void startDrag(double mouseX, double mouseY) {
        dragging = true;
        dragOffsetX = (int) Math.round(mouseX - x);
        dragOffsetY = (int) Math.round(mouseY - y);
    }

    public void updateDrag(double mouseX, double mouseY) {
        if (!dragging) return;
        x = (int) Math.round(mouseX) - dragOffsetX;
        y = (int) Math.round(mouseY) - dragOffsetY;
    }

    public void stopDrag() { dragging = false; }
    public boolean dragging() { return dragging; }
}
