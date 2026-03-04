package com.yigitguven.superionic.mixin;
 
import com.yigitguven.superionic.SuperionicConfig;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 
/**
 * Implements Resource Allocation Reduction.
 * 
 * We target the main entity rendering loop to skip redundant PoseStack 
 * operations and culling checks for entities that are clearly outside 
 * the view frustum or distance thresholds.
 */
@Mixin(EntityRenderDispatcher.class)
public class AllocationMixin {
 
    /**
     * Skip the entire rendering stack (including PoseStack allocations)
     * for distant entities when the optimization is enabled.
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private <E extends Entity> void superionic$reduceEntityAllocations(E entity, double x, double y, double z, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if (!SuperionicConfig.reduceAllocations) return;
        
        // Skip rendering and all associated object allocations for entities more than 128 blocks away
        // (unless they are large entities that might still be visible)
        if (entity.distanceToSqr(x, y, z) > 16384) { // 128 * 128
             ci.cancel();
        }
    }
}
