package com.spekedclient.module.modules.hud;

import org.lwjgl.glfw.GLFW;
import java.util.ArrayDeque;
import java.util.Deque;

public final class CpsModule extends BaseHudModule {
    private final Deque<Long> clicks = new ArrayDeque<>();
    private boolean lastDown;
    public CpsModule() { super("cps", "CPS"); }
    @Override public void onTick() {
        long now = System.currentTimeMillis();
        boolean down = mc().getWindow() != null && GLFW.glfwGetMouseButton(mc().getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        if (down && !lastDown) clicks.addLast(now);
        lastDown = down;
        while (!clicks.isEmpty() && now - clicks.peekFirst() > 1000L) clicks.removeFirst();
    }
    @Override public String value() { return "CPS " + clicks.size(); }
}
