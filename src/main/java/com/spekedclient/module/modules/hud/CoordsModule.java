package com.spekedclient.module.modules.hud;

public final class CoordsModule extends BaseHudModule {
    public CoordsModule() { super("coords", "Coords"); }
    @Override public String value() {
        if (!inWorld()) return "XYZ --";
        var p = mc().player.getBlockPos();
        return "XYZ " + p.getX() + " " + p.getY() + " " + p.getZ();
    }
}
