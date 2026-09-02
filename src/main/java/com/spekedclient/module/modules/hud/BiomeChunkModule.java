package com.spekedclient.module.modules.hud;

public final class BiomeChunkModule extends BaseHudModule {
    public BiomeChunkModule() { super("biome-chunk", "Biome + Chunk"); }
    @Override public String value() {
        if (!inWorld()) return "Biome -- · Chunk --";
        var pos = mc().player.getBlockPos();
        String biome = mc().world.getBiome(pos).getKey().map(key -> key.getValue().getPath()).orElse("unknown");
        return "Biome " + biome + " · Chunk " + (pos.getX() >> 4) + "," + (pos.getZ() >> 4);
    }
}
