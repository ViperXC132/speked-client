package com.spekedclient.hud;

public final class HudElement {
    private final String id;
    private int x;
    private int y;
    private int width;
    private int height;

    public HudElement(String id, int x, int y, int width, int height) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String id() { return id; }
    public int x() { return x; }
    public int y() { return y; }
    public int width() { return width; }
    public int height() { return height; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public void setSize(int width, int height) { this.width = width; this.height = height; }
}
