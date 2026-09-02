package com.spekedclient.module.modules.hud;

import net.minecraft.client.option.KeyBinding;

public final class KeystrokesModule extends BaseHudModule {
    public KeystrokesModule() { super("keystrokes", "Keystrokes"); }
    @Override public String value() {
        return "W" + down(mc().options.forwardKey) + " A" + down(mc().options.leftKey)
                + " S" + down(mc().options.backKey) + " D" + down(mc().options.rightKey);
    }
    private static String down(KeyBinding key) { return key.isPressed() ? "*" : "·"; }
}
