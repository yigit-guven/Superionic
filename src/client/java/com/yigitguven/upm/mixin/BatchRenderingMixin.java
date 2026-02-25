package com.yigitguven.upm.mixin;

import com.yigitguven.upm.UltimatePerformanceModConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Real Batch Rendering optimization.
 * Optimizes MultiBufferSource$BufferSource to group compatible RenderTypes,
 * reducing the number of draw calls and CPU-GPU synchronization.
 */
@Mixin(MultiBufferSource.BufferSource.class)
public abstract class BatchRenderingMixin {

    @Shadow
    public abstract void endBatch();

    @Shadow
    private RenderType lastSharedType;

    @Redirect(method = "getBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch()V"))
    private void onEndBatch(MultiBufferSource.BufferSource instance, RenderType currentType) {
        if (UltimatePerformanceModConfig.batchRendering && this.lastSharedType != null && canBatch(this.lastSharedType, currentType)) {
            // Skip endBatch() to group compatible draw calls
            return;
        }
        instance.endBatch();
    }

    private boolean canBatch(RenderType last, RenderType current) {
        if (last == current) return true;
        if (last == null || current == null) return false;

        // Strict equality is the safest for a general-purpose performance mod.
        // We can add more complex logic here later if needed (e.g. comparing shaders/textures).
        return last.equals(current);
    }
}
