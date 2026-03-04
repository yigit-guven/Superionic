package com.yigitguven.superionic.mixin;

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
        if (!SuperionicConfig.aiThrottling) return;

        Mob self = (Mob) (Object) this;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // If mob is more than 48 blocks away, only tick AI every 4th tick
        double distanceSq = self.distanceToSqr(mc.player);
        if (distanceSq > 2304) { // 48 * 48
            if (self.tickCount % 4 != 0) {
                ci.cancel();
            }
        }
    }
}
