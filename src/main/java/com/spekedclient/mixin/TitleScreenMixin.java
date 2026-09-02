package com.spekedclient.mixin;

import com.spekedclient.gui.TitleScreenOverride;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class TitleScreenMixin {
    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void speked$replaceTitleScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof TitleScreen) {
            MinecraftClient client = (MinecraftClient) (Object) this;
            client.setScreen(new TitleScreenOverride());
            ci.cancel();
        }
    }
}
