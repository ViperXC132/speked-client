package com.spekedclient.hud;

import com.spekedclient.module.Category;
import com.spekedclient.module.Module;

public abstract class HudModule extends Module {
    private final HudElement element;

    protected HudModule(String id, String name) {
        super(id, name, Category.HUD);
        this.element = new HudElement(id, 8, 8, 0, 12);
    }

    public HudElement element() { return element; }
    public abstract String value();
}
