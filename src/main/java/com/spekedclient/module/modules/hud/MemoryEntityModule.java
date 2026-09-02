package com.spekedclient.module.modules.hud;

public final class MemoryEntityModule extends BaseHudModule {
    public MemoryEntityModule() { super("memory-entity", "Memory + Entity Count"); }
    @Override public String value() {
        long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024L * 1024L);
        int entities = inWorld() ? mc().world.getOtherEntities(mc().player, mc().player.getBoundingBox().expand(64), entity -> true).size() : 0;
        return "RAM " + used + "MB · Entities " + entities;
    }
}
