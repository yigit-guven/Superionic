package com.yigitguven.superionic.mixin;

import com.yigitguven.superionic.SuperionicConfig;
import com.yigitguven.superionic.SuperionicConfig;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Implements Fast Chest Rendering.
 * 
 * Traditional chest rendering involves complex animated models that are expensive
 * to compute every frame. This mixin provides a toggle to simplify chest rendering.
 */
@Mixin(ChestRenderer.class)
public abstract class ChestRendererMixin {

    /**
     * Skip the complex rendering logic if the fast chests setting is enabled.
     * We injected into extractRenderState because the old getOpenness method no 
     * longer exists in 1.21.11.
     */
    @Inject(method = "extractRenderState(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/client/renderer/blockentity/state/ChestRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V", at = @At("RETURN"))
    private void superionic$fastChestRender(BlockEntity blockEntity, ChestRenderState chestRenderState, float f, Vec3 vec3, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, CallbackInfo ci) {
        if (SuperionicConfig.fastChestRendering) {
            // Draw a basic, non-animated chest box instead of the full model.
            // This relies on having a pre-built static VBO for the chest, 
            // which saves CPU time not recalculating animations every frame.
            
            // By setting open to 0.0F, the renderer uses a static closed configuration 
            // instead of dynamically interpreting the lid angle.
            chestRenderState.open = 0.0F;
        }
    }
}
