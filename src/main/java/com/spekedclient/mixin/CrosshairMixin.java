package com.spekedclient.mixin;

import com.spekedclient.SpekedClient;
import com.spekedclient.module.modules.visual.CustomCrosshairModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class CrosshairMixin {
    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void speked$replaceCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        var module = SpekedClient.get().modules().find("custom-crosshair").orElse(null);
        if (module instanceof CustomCrosshairModule crosshair && crosshair.enabled() && crosshair.replaceVanilla()) {
            crosshair.render(context);
            ci.cancel();
        }
    }
}
