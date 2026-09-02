package com.spekedclient.gui;

import com.spekedclient.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.input.MouseInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Random;

public final class TitleScreenOverride extends Screen {
    private static final int BUTTON_W = 150;
    private static final int BUTTON_H = 34;
    private static final String[] BUTTONS = {"Singleplayer", "Multiplayer", "Mod menu", "Cosmetics", "Options", "Quit"};
    private final int[] starX = new int[100];
    private final int[] starY = new int[100];
    private final int[] starAlpha = new int[100];
    private long ticks;

    public TitleScreenOverride() {
        super(Text.literal("Speked Client"));
        Random random = new Random(0x5EEDC0DE);
        for (int i = 0; i < starX.length; i++) {
            starX[i] = random.nextInt(1920);
            starY[i] = random.nextInt(1080);
            starAlpha[i] = 18 + random.nextInt(38);
        }
    }

    @Override
    protected void init() {
        // The menu is intentionally drawn directly with DrawContext so VulkanMod never sees custom GL state.
    }

    @Override
    public void tick() {
        ticks++;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        renderBackground(context, mouseX, mouseY, deltaTicks);
        drawStars(context);

        int left = 0;
        int buttonX = Math.max(24, width / 2 - 310);
        int startY = height / 2 - 112;
        for (int i = 0; i < BUTTONS.length; i++) {
            int y = startY + i * 38;
            boolean hover = inside(mouseX, mouseY, buttonX, y, BUTTON_W, BUTTON_H);
            int fill = i == 0 ? (hover ? RenderUtil.ACCENT_HOVER : RenderUtil.ACCENT) : (hover ? 0xE0181D2B : 0xD0121622);
            RenderUtil.pill(context, buttonX, y, BUTTON_W, BUTTON_H, fill, hover ? RenderUtil.ACCENT_HOVER : RenderUtil.SOFT_BORDER);
            context.drawTextWithShadow(textRenderer, Text.literal(iconFor(i)), buttonX + 13, y + 12, RenderUtil.TEXT);
            context.drawTextWithShadow(textRenderer, Text.literal(BUTTONS[i]), buttonX + 35, y + 12, RenderUtil.TEXT);
            left++;
        }

        int centerX = width / 2;
        int centerY = height / 2 - 8;
        context.drawTextWithShadow(textRenderer, Text.literal("+"), centerX - 5, centerY - 9, 0xDDE7ECFF);
        String splash = "No hacks. Just vibes.";
        context.drawTextWithShadow(textRenderer, Text.literal(splash), centerX - textRenderer.getWidth(splash) / 2, centerY + 25, RenderUtil.DIM);

        int rightX = Math.min(width - 205, width / 2 + 185);
        drawAccounts(context, rightX, height / 2 - 145, 205, 108);
        drawQuickConnect(context, rightX, height / 2 - 23, 205, 125);

        String footer = "Speked Client v1.0.0  ·  Fabric 1.21.11  ·  Java 21";
        context.drawTextWithShadow(textRenderer, Text.literal(footer), 16, height - 18, RenderUtil.MUTED);
        context.drawTextWithShadow(textRenderer, Text.literal("Discord   GitHub   Report bug"), width - 170, height - 18, RenderUtil.MUTED);
    }

    private void drawStars(DrawContext context) {
        context.fill(0, 0, width, height, 0xFF070A12);
        for (int i = 0; i < starX.length; i++) {
            int x = Math.floorMod(starX[i] + (int) (ticks * (i % 3 == 0 ? 0 : 1) / 30), Math.max(1, width));
            int y = Math.floorMod(starY[i] + (int) (ticks * (i % 5 == 0 ? 1 : 0) / 45), Math.max(1, height));
            RenderUtil.dot(context, x, y, (starAlpha[i] << 24) | 0x00B8C7E8);
        }
    }

    private void drawAccounts(DrawContext context, int x, int y, int w, int h) {
        RenderUtil.panelSoft(context, x, y, w, h);
        context.drawTextWithShadow(textRenderer, Text.literal("ACCOUNTS"), x + 10, y + 9, RenderUtil.MUTED);
        context.drawTextWithShadow(textRenderer, Text.literal("MANAGE"), x + w - 48, y + 9, RenderUtil.MUTED);
        context.drawTextWithShadow(textRenderer, Text.literal("▦"), x + 10, y + 30, RenderUtil.TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("ViperXC132"), x + 30, y + 27, RenderUtil.TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("Microsoft · Premium"), x + 30, y + 40, RenderUtil.DIM);
        context.fill(x + w - 14, y + 33, x + w - 10, y + 37, 0xFF52D67A);
        context.drawTextWithShadow(textRenderer, Text.literal("▦"), x + 10, y + 61, RenderUtil.TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("CrackPlayer1"), x + 30, y + 58, RenderUtil.TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("ely.by · Cracked"), x + 30, y + 71, RenderUtil.DIM);
        context.drawTextWithShadow(textRenderer, Text.literal("+  Add account"), x + 10, y + 92, RenderUtil.DIM);
    }

    private void drawQuickConnect(DrawContext context, int x, int y, int w, int h) {
        RenderUtil.panelSoft(context, x, y, w, h);
        context.drawTextWithShadow(textRenderer, Text.literal("QUICK CONNECT"), x + 10, y + 9, RenderUtil.MUTED);
        context.drawTextWithShadow(textRenderer, Text.literal("ADD SERVER"), x + w - 63, y + 9, RenderUtil.MUTED);
        drawServer(context, x, y + 25, "Hypixel", "mc.hypixel.net", "34ms", true);
        drawServer(context, x, y + 55, "CubeCraft", "play.cubecraft.net", "72ms", true);
        drawServer(context, x, y + 85, "My SMP", "play.mysmp.net", "offline", false);
    }

    private void drawServer(DrawContext context, int x, int y, String name, String address, String ping, boolean online) {
        context.fill(x + 10, y + 8, x + 14, y + 12, online ? 0xFF52D67A : 0xFFE15B68);
        context.drawTextWithShadow(textRenderer, Text.literal(name), x + 22, y + 3, RenderUtil.TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal(address), x + 22, y + 15, RenderUtil.DIM);
        context.drawTextWithShadow(textRenderer, Text.literal(ping), x + 166, y + 9, online ? RenderUtil.DIM : 0xFFE15B68);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.buttonInfo().button() != GLFW.GLFW_MOUSE_BUTTON_LEFT) return super.mouseClicked(click, doubled);
        int x = (int) click.x();
        int y = (int) click.y();
        int buttonX = Math.max(24, width / 2 - 310);
        int startY = height / 2 - 112;
        for (int i = 0; i < BUTTONS.length; i++) {
            if (!inside(x, y, buttonX, startY + i * 38, BUTTON_W, BUTTON_H)) continue;
            MinecraftClient client = MinecraftClient.getInstance();
            switch (i) {
                case 0 -> client.setScreen(new SelectWorldScreen(this));
                case 1 -> client.setScreen(new MultiplayerScreen(this));
                case 4 -> client.setScreen(new OptionsScreen(this, client.options));
                case 5 -> client.scheduleStop();
                default -> { }
            }
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    private static boolean inside(int mouseX, int mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    private static String iconFor(int index) {
        return switch (index) {
            case 0 -> "⌕";
            case 1 -> "◉";
            case 2 -> "⚙";
            case 3 -> "◇";
            case 4 -> "⊙";
            default -> "↪";
        };
    }
}
