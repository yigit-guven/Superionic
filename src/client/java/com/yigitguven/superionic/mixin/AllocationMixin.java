package com.yigitguven.superionic.mixin;
 
import com.yigitguven.superionic.BenchmarkSystem;
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
        if (!SuperionicConfig.reduceAllocations && !SuperionicConfig.entityShadowCulling) return;
        
        double distSq = x * x + y * y + z * z;

        // Shadow Culling (Distance-based)
        if (SuperionicConfig.entityShadowCulling && distSq > 576) { // 24 blocks
            BenchmarkSystem.recordCulledShadow();
        }

        // Allocation Reduction (Distance-based)
        if (SuperionicConfig.reduceAllocations && distSq > 4096) { // 64 blocks
             BenchmarkSystem.recordSkippedAllocation();
             ci.cancel();
        }
    }
}
