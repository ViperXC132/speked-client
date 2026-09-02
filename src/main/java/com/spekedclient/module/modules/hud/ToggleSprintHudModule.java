package com.spekedclient.module.modules.hud;

public final class ToggleSprintHudModule extends BaseHudModule {
    public ToggleSprintHudModule() { super("toggle-sprint-indicator", "ToggleSprint Indicator"); }
    @Override public String value() { return inWorld() && mc().player.isSprinting() ? "SPRINT" : ""; }
}
