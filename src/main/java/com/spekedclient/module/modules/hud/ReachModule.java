package com.spekedclient.module.modules.hud;

public final class ReachModule extends BaseHudModule {
    public ReachModule() { super("reach", "Reach"); }
    @Override public String value() {
        if (mc().player == null || mc().crosshairTarget == null) return "Reach --";
        return String.format("Reach %.2fm", mc().player.getEyePos().distanceTo(mc().crosshairTarget.getPos()));
    }
}
