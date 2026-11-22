package com.betterf3.mixin;

import com.betterf3.BetterF3Client;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void setRotation(float yaw, float pitch);
    @Shadow protected abstract void setPos(double x, double y, double z);

    @Inject(method = "update", at = @At("TAIL"))
    private void betterf3$update(CallbackInfo ci) {
        if (!BetterF3Client.enabled) return;
        float t = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(true);
        double px = BetterF3Client.prevX + (BetterF3Client.x - BetterF3Client.prevX) * t;
        double py = BetterF3Client.prevY + (BetterF3Client.y - BetterF3Client.prevY) * t;
        double pz = BetterF3Client.prevZ + (BetterF3Client.z - BetterF3Client.prevZ) * t;
        this.setRotation(BetterF3Client.yaw, BetterF3Client.pitch);
        this.setPos(px, py, pz);
    }

    // чтобы тело игрока отображалось, пока камера отдельно
    @Inject(method = "isThirdPerson", at = @At("HEAD"), cancellable = true)
    private void betterf3$thirdPerson(CallbackInfoReturnable<Boolean> cir) {
        if (BetterF3Client.enabled) cir.setReturnValue(true);
    }
}
