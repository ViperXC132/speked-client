package com.spekedclient.hud;

import com.spekedclient.module.Category;
import com.spekedclient.module.Module;

public abstract class HudModule extends Module {
    private final HudElement element;

    protected HudModule(String id, String name) {
        super(id, name, Category.HUD);
        this.element = new HudElement(id, 8, 8, 0, 12);

        // Every HUD module gets its own persistent layout/settings namespace.
        setting("x", 8);
        setting("y", 8);
        setting("scale", 1.0f);
        setting("color", 0xFFC8D0E0);
        setting("shadow", true);
    }

    public HudElement element() { return element; }

    public float scale() {
        return getSetting("scale", Float.class, 1.0f);
    }

    public int textColor() {
        return getSetting("color", Integer.class, 0xFFC8D0E0);
    }

    public boolean textShadow() {
        return getSetting("shadow", Boolean.class, true);
    }

    public void syncLayout() {
        element.setPosition(
                getSetting("x", Integer.class, element.x()),
                getSetting("y", Integer.class, element.y())
        );
        element.setScale(scale());
    }

    public void setLayout(int x, int y) {
        element.setPosition(x, y);
        setting("x", x);
        setting("y", y);
    }

    public void setScale(float scale) {
        float clamped = Math.max(0.25f, Math.min(4.0f, scale));
        element.setScale(clamped);
        setting("scale", clamped);
    }

    public abstract String value();
}
