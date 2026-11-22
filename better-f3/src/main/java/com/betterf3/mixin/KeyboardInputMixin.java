package com.betterf3.mixin;

import com.betterf3.BetterF3Client;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {
    // Игрок стоит на месте, пока включена свободная камера
    @Inject(method = "tick", at = @At("TAIL"))
    private void betterf3$tick(CallbackInfo ci) {
        if (BetterF3Client.enabled) {
            this.playerInput = PlayerInput.DEFAULT;
            this.movementVector = Vec2f.ZERO;
        }
    }
}
