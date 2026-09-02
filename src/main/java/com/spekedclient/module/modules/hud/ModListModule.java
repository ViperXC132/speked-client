package com.spekedclient.module.modules.hud;

import com.spekedclient.SpekedClient;

public final class ModListModule extends BaseHudModule {
    public ModListModule() { super("mod-list", "ModList"); }
    @Override public String value() {
        long enabled = SpekedClient.get().modules().all().stream().filter(m -> m.enabled()).count();
        return "Mods " + enabled;
    }
}
