package com.spekedclient.module.modules.hud;

import net.minecraft.client.network.PlayerListEntry;

public final class PingTpsModule extends BaseHudModule {
    public PingTpsModule() { super("ping-tps", "Ping + TPS"); }
    @Override public String value() {
        if (!inWorld()) return "Ping -- · TPS --";
        int ping = -1;
        if (mc().getNetworkHandler() != null) {
            PlayerListEntry entry = mc().getNetworkHandler().getPlayerListEntry(mc().player.getUuid());
            if (entry != null) ping = entry.getLatency();
        }
        return "Ping " + (ping < 0 ? "--" : ping + "ms") + " · TPS 20";
    }
}
