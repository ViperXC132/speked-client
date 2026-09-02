package com.spekedclient.module.modules.hud;

public final class FpsModule extends BaseHudModule {
    public FpsModule() { super("fps", "FPS"); }
    @Override public String value() { return "FPS " + mc().getCurrentFps(); }
}
