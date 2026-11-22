package com.betterf3.mixin;

import com.betterf3.BetterF3Client;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    // Мышь крутит камеру, а не игрока
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void betterf3$look(double dx, double dy, CallbackInfo ci) {
        if (BetterF3Client.enabled && (Object) this == MinecraftClient.getInstance().player) {
            BetterF3Client.rotate(dx, dy);
            ci.cancel();
        }
    }
}
