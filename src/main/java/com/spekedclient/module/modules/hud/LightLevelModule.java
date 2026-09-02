package com.spekedclient.module.modules.hud;

public final class LightLevelModule extends BaseHudModule {
    public LightLevelModule() { super("light-level", "Light Level"); }
    @Override public String value() { return !inWorld() ? "Light --" : "Light " + mc().world.getLightLevel(mc().player.getBlockPos()); }
}
