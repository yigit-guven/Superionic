package com.yigitguven.superionic.mixin;

import com.yigitguven.superionic.BenchmarkSystem;
import com.yigitguven.superionic.SuperionicConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Implements AI Pathfinding Throttling.
 * 
 * Mob AI, especially pathfinding, is one of the most expensive parts of the
 * Minecraft tick. This mixin reduces the update frequency of AI for mobs
 * that are far from the player, saving significant CPU cycles.
 */
@Mixin(Mob.class)
public abstract class MobAiMixin {

    /**
     * Skip AI logic if the mob is far away and the config is enabled.
     * Distant mobs don't need update every single tick.
     */
    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void superionic$throttleDistantAi(CallbackInfo ci) {
        if (!SuperionicConfig.enabled) return;
        if (!SuperionicConfig.aiThrottling) return;

        Mob self = (Mob) (Object) this;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Throttled AI logic based on config distance and rate
        double distanceSq = self.distanceToSqr(mc.player);
        if (distanceSq > (SuperionicConfig.aiThrottlingDistance * SuperionicConfig.aiThrottlingDistance)) {
            if (self.tickCount % SuperionicConfig.aiThrottlingRate != 0) {
                BenchmarkSystem.recordSkippedAiTick();
                ci.cancel();
            }
        }
    }
}
