package com.betterf3;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class BetterF3Client implements ClientModInitializer {
    public static boolean enabled = false;

    // Позиция/поворот свободной камеры
    public static double x, y, z, prevX, prevY, prevZ;
    public static float yaw, pitch;

    public static double speed = 0.6; // блоков за тик

    private static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.betterf3.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F6,
                KeyBinding.Category.create(Identifier.of("betterf3", "main"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                toggle(client);
            }
            if (enabled) {
                if (client.player == null) {
                    enabled = false;
                    return;
                }
                tickMovement(client);
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> enabled = false);
    }

    private static void toggle(MinecraftClient client) {
        if (client.player == null) return;
        enabled = !enabled;
        if (enabled) {
            Vec3d eye = client.player.getEyePos();
            x = prevX = eye.x;
            y = prevY = eye.y;
            z = prevZ = eye.z;
            yaw = client.player.getYaw();
            pitch = client.player.getPitch();
        }
        client.player.sendMessage(Text.literal("Better F3: " + (enabled ? "ON" : "OFF")), true);
    }

    private static void tickMovement(MinecraftClient client) {
        prevX = x; prevY = y; prevZ = z;

        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        // вектор взгляда
        double lx = -Math.sin(yawRad) * Math.cos(pitchRad);
        double ly = -Math.sin(pitchRad);
        double lz = Math.cos(yawRad) * Math.cos(pitchRad);
        // вектор "вправо" (горизонтальный)
        double rx = -Math.cos(yawRad);
        double rz = -Math.sin(yawRad);

        double mx = 0, my = 0, mz = 0;
        var o = client.options;
        if (o.forwardKey.isPressed()) { mx += lx; my += ly; mz += lz; }
        if (o.backKey.isPressed())    { mx -= lx; my -= ly; mz -= lz; }
        if (o.rightKey.isPressed())   { mx += rx; mz += rz; }
        if (o.leftKey.isPressed())    { mx -= rx; mz -= rz; }
        if (o.jumpKey.isPressed())    { my += 1; }
        if (o.sneakKey.isPressed())   { my -= 1; }

        double len = Math.sqrt(mx * mx + my * my + mz * mz);
        if (len > 0) {
            double s = speed * (o.sprintKey.isPressed() ? 2.0 : 1.0) / len;
            x += mx * s; y += my * s; z += mz * s;
        }
    }

    public static void rotate(double dx, double dy) {
        yaw += (float) dx * 0.15f;
        pitch = MathHelper.clamp(pitch + (float) dy * 0.15f, -90f, 90f);
    }
}
