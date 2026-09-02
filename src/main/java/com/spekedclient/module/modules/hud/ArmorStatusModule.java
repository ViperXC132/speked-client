package com.spekedclient.module.modules.hud;

import net.minecraft.item.ItemStack;

public final class ArmorStatusModule extends BaseHudModule {
    public ArmorStatusModule() { super("armor-status", "Armor Status"); }
    @Override public String value() {
        if (!inWorld()) return "Armor --";
        int total = 0, max = 0;
        for (int i = 0; i < 4; i++) {
            ItemStack stack = mc().player.getInventory().getArmorStack(i);
            if (!stack.isEmpty() && stack.isDamageable()) {
                total += stack.getMaxDamage() - stack.getDamage();
                max += stack.getMaxDamage();
            }
        }
        return max == 0 ? "Armor --" : "Armor " + Math.round(total * 100f / max) + "%";
    }
}
