package com.spekedclient.module.modules.hud;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public final class CompassModule extends BaseHudModule {
    public CompassModule() { super("compass-clock", "Compass + Clock"); }
    @Override public String value() {
        if (!inWorld()) return "N --:--";
        long day = Math.floorMod(mc().world.getTimeOfDay(), 24000L);
        int hour = (int) ((day / 1000 + 6) % 24);
        int minute = (int) ((day % 1000) * 60 / 1000);
        Direction dir = Direction.fromRotation(MathHelper.wrapDegrees(mc().player.getYaw()));
        return String.format("%s %02d:%02d", dir.asString().toUpperCase(), hour, minute);
    }
}
