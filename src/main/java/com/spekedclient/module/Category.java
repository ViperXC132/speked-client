package com.spekedclient.module;

public enum Category {
    HUD("HUD"),
    VISUALS("Visuals"),
    UTILITY("Utility"),
    PERFORMANCE("Performance"),
    MOVEMENT("Movement"),
    TRAINER("PvP Trainer"),
    SYSTEM("System");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
