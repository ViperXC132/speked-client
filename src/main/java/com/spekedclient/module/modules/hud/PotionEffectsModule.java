package com.spekedclient.module.modules.hud;

public final class PotionEffectsModule extends BaseHudModule {
    public PotionEffectsModule() { super("potion-effects", "Potion Effects"); }
    @Override public String value() { return !inWorld() ? "Effects --" : "Effects " + mc().player.getStatusEffects().size(); }
}
