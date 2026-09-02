package com.spekedclient.module.modules.hud;

public final class SpeedModule extends BaseHudModule {
    public SpeedModule() { super("speed", "Speed"); }
    @Override public String value() {
        if (!inWorld()) return "Speed --";
        double x = mc().player.getVelocity().x;
        double z = mc().player.getVelocity().z;
        return String.format("Speed %.2f m/s", Math.sqrt(x * x + z * z) * 20.0);
    }
}
