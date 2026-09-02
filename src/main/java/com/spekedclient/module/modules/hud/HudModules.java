package com.spekedclient.module.modules.hud;

import com.spekedclient.hud.HudModule;

import java.util.List;

/** Factory matching the PixelForge HUD package layout without placeholder modules. */
public final class HudModules {
    private HudModules() {}

    public static List<HudModule> createAll() {
        List<HudModule> modules = List.of(
                new FpsModule(),
                new CpsModule(),
                new CoordsModule(),
                new ArmorStatusModule(),
                new PotionEffectsModule(),
                new SpeedModule(),
                new CompassModule(),
                new PingTpsModule(),
                new KeystrokesModule(),
                new ToggleSprintHudModule(),
                new ModListModule(),
                new BiomeChunkModule(),
                new LightLevelModule(),
                new MemoryEntityModule(),
                new ReachModule()
        );

        // Fresh installs get a readable stack; config-loaded coordinates override these defaults.
        int y = 8;
        for (HudModule module : modules) {
            module.setLayout(8, y);
            y += 13;
        }
        return modules;
    }
}
