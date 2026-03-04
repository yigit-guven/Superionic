package com.yigitguven.superionic.mixin;

import com.yigitguven.superionic.SuperionicConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Optimizes entity shadow rendering.
 *
 * Entity shadows can be surprisingly expensive in dense areas.
 * This mixin adds a distance-based cull and a global toggle.
 */
@Mixin(EntityRenderer.class)
public class EntityShadowMixin<T extends Entity> {

    @Inject(method = "shouldRenderShadow", at = @At("HEAD"), cancellable = true)
    private void superionic$cullEntityShadows(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (!SuperionicConfig.entityShadowCulling) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Distance check: If entity is more than 32 blocks away, skip shadow
        // This is a common performance optimization in many mods.
        double distanceSq = entity.distanceToSqr(mc.player);
        if (distanceSq > 1024) { // 32 * 32
            cir.setReturnValue(false);
        }
    }
}
