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
    static {
        System.out.println("[SUPERIONIC-STATIC] AllocationMixin class loaded!");
    }
 
    /**
     * Skip the entire rendering stack (including PoseStack allocations)
     * for distant entities when the optimization is enabled.
     */
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void superionic$cullEntity(Entity entity, net.minecraft.client.renderer.culling.Frustum frustum, double x, double y, double z, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> cir) {
        if (!SuperionicConfig.reduceAllocations && !SuperionicConfig.entityShadowCulling) return;
        
        double distSq = entity.distanceToSqr(net.minecraft.client.Minecraft.getInstance().player);

        // Shadow Culling
        if (SuperionicConfig.entityShadowCulling && distSq > (SuperionicConfig.shadowCullingDistance * SuperionicConfig.shadowCullingDistance)) { 
            BenchmarkSystem.recordCulledShadow();
        }

        // Entity Culling / Allocation Reduction
        if (SuperionicConfig.reduceAllocations && distSq > (SuperionicConfig.entityCullingDistance * SuperionicConfig.entityCullingDistance)) { 
             BenchmarkSystem.recordSkippedAllocation();
             cir.setReturnValue(false);
        }
    }
}
