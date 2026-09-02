package com.spekedclient.module.modules.hud;

import com.spekedclient.hud.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class HudModules {
    private HudModules() {}

    public static List<HudModule> createAll() {
        return List.of(new FPS(), new CPS(), new Coords(), new ArmorStatus(), new PotionEffects(), new Speed(),
                new CompassClock(), new PingTPS(), new Keystrokes(), new ToggleSprintIndicator(), new ModList(),
                new BiomeChunk(), new LightLevel(), new MemoryEntity(), new Reach());
    }

    private static MinecraftClient client() { return MinecraftClient.getInstance(); }

    private abstract static class Base extends HudModule {
        protected Base(String id, String name) { super(id, name); }
        protected boolean inWorld() { return client().player != null && client().world != null; }
    }

    public static final class FPS extends Base {
        public FPS() { super("fps", "FPS"); }
        @Override public String value() { return "FPS " + client().getCurrentFps(); }
    }

    public static final class CPS extends Base {
        private final Deque<Long> clicks = new ArrayDeque<>();
        private boolean lastDown;
        public CPS() { super("cps", "CPS"); }
        @Override public void onTick() {
            long now = System.currentTimeMillis();
            boolean down = client().getWindow() != null && GLFW.glfwGetMouseButton(client().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            if (down && !lastDown) clicks.addLast(now);
            lastDown = down;
            while (!clicks.isEmpty() && now - clicks.peekFirst() > 1000) clicks.removeFirst();
        }
        @Override public String value() { return "CPS " + clicks.size(); }
    }

    public static final class Coords extends Base {
        public Coords() { super("coords", "Coords"); }
        @Override public String value() {
            if (!inWorld()) return "XYZ --";
            var p = client().player.getBlockPos();
            return "XYZ " + p.getX() + " " + p.getY() + " " + p.getZ();
        }
    }

    public static final class ArmorStatus extends Base {
        public ArmorStatus() { super("armor-status", "Armor Status"); }
        @Override public String value() {
            if (!inWorld()) return "Armor --";
            int total = 0, max = 0;
            for (int i = 0; i < 4; i++) {
                ItemStack stack = client().player.getInventory().getArmorStack(i);
                if (!stack.isEmpty() && stack.isDamageable()) {
                    total += stack.getMaxDamage() - stack.getDamage();
                    max += stack.getMaxDamage();
                }
            }
            return max == 0 ? "Armor --" : "Armor " + Math.round(total * 100f / max) + "%";
        }
    }

    public static final class PotionEffects extends Base {
        public PotionEffects() { super("potion-effects", "Potion Effects"); }
        @Override public String value() { return !inWorld() ? "Effects --" : "Effects " + client().player.getStatusEffects().size(); }
    }

    public static final class Speed extends Base {
        public Speed() { super("speed", "Speed"); }
        @Override public String value() {
            if (!inWorld()) return "Speed --";
            double horizontal = Math.sqrt(client().player.getVelocity().x * client().player.getVelocity().x + client().player.getVelocity().z * client().player.getVelocity().z);
            return String.format("Speed %.2f m/s", horizontal * 20.0);
        }
    }

    public static final class CompassClock extends Base {
        public CompassClock() { super("compass-clock", "Compass + Clock"); }
        @Override public String value() {
            if (!inWorld()) return "N --:--";
            long day = Math.floorMod(client().world.getTimeOfDay(), 24000L);
            int hour = (int) ((day / 1000 + 6) % 24);
            int minute = (int) ((day % 1000) * 60 / 1000);
            float yaw = MathHelper.wrapDegrees(client().player.getYaw());
            Direction dir = Direction.fromRotation(yaw);
            return String.format("%s %02d:%02d", dir.asString().toUpperCase(), hour, minute);
        }
    }

    public static final class PingTPS extends Base {
        public PingTPS() { super("ping-tps", "Ping + TPS"); }
        @Override public String value() {
            if (!inWorld()) return "Ping -- · TPS --";
            int ping = -1;
            if (client().getNetworkHandler() != null) {
                PlayerListEntry entry = client().getNetworkHandler().getPlayerListEntry(client().player.getUuid());
                if (entry != null) ping = entry.getLatency();
            }
            return "Ping " + (ping < 0 ? "--" : ping + "ms") + " · TPS 20";
        }
    }

    public static final class Keystrokes extends Base {
        public Keystrokes() { super("keystrokes", "Keystrokes"); }
        @Override public String value() {
            KeyBinding w = client().options.forwardKey, a = client().options.leftKey, s = client().options.backKey, d = client().options.rightKey;
            return "W" + down(w) + " A" + down(a) + " S" + down(s) + " D" + down(d);
        }
        private static String down(KeyBinding key) { return key.isPressed() ? "*" : "·"; }
    }

    public static final class ToggleSprintIndicator extends Base {
        public ToggleSprintIndicator() { super("toggle-sprint-indicator", "ToggleSprint Indicator"); }
        @Override public String value() { return inWorld() && client().player.isSprinting() ? "SPRINT" : ""; }
    }

    public static final class ModList extends Base {
        public ModList() { super("mod-list", "ModList"); }
        @Override public String value() {
            long count = com.spekedclient.SpekedClient.get().modules().all().stream().filter(m -> m.enabled()).count();
            return "Mods " + count;
        }
    }

    public static final class BiomeChunk extends Base {
        public BiomeChunk() { super("biome-chunk", "Biome + Chunk"); }
        @Override public String value() {
            if (!inWorld()) return "Biome -- · Chunk --";
            var pos = client().player.getBlockPos();
            String biome = client().world.getBiome(pos).getKey().map(key -> key.getValue().getPath()).orElse("unknown");
            return "Biome " + biome + " · Chunk " + (pos.getX() >> 4) + "," + (pos.getZ() >> 4);
        }
    }

    public static final class LightLevel extends Base {
        public LightLevel() { super("light-level", "Light Level"); }
        @Override public String value() { return !inWorld() ? "Light --" : "Light " + client().world.getLightLevel(client().player.getBlockPos()); }
    }

    public static final class MemoryEntity extends Base {
        public MemoryEntity() { super("memory-entity", "Memory + Entity Count"); }
        @Override public String value() {
            long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
            int entities = inWorld() ? client().world.getOtherEntities(client().player, client().player.getBoundingBox().expand(64), entity -> true).size() : 0;
            return "RAM " + used + "MB · Entities " + entities;
        }
    }

    public static final class Reach extends Base {
        public Reach() { super("reach", "Reach"); }
        @Override public String value() {
            if (client().crosshairTarget == null || client().player == null) return "Reach --";
            double distance = client().player.getEyePos().distanceTo(client().crosshairTarget.getPos());
            return String.format("Reach %.2fm", distance);
        }
    }
}
