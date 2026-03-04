package com.yigitguven.superionic.mixin;

import com.yigitguven.superionic.SuperionicConfig;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Expands the native batching consolidation check in Minecraft 1.21.11.
 *
 * <h2>Background:</h2>
 * {@code BufferSource.getBuffer()} calls {@code RenderType.canConsolidateConsecutiveGeometry()}
 * before deciding whether to flush (endBatch) or reuse the current buffer. Vanilla only returns
 * {@code true} for {@link com.mojang.blaze3d.vertex.VertexFormat.Mode}s where
 * {@code connectedPrimitives == true} (e.g. TRIANGLE_STRIP, TRIANGLE_FAN).
 *
 * <h2>Our enhancement:</h2>
 * We override this to also return {@code true} when the same RenderType instance is being
 * checked for the second time in a row (identity check). This covers the case where the same
 * RenderType is used consecutively but the vertex format's mode doesn't set connectedPrimitives.
 *
 * This is safe because if it's the EXACT same RenderType instance, the pipeline, shader,
 * textures, blend mode, and buffer are all guaranteed to be identical.
 */
@Mixin(RenderType.class)
public class RenderTypeMixin {

    @Unique
    private static final ThreadLocal<RenderType> superionic$lastQueried = new ThreadLocal<>();

    /**
     * Intercept canConsolidateConsecutiveGeometry() at RETURN.
     * If it would return false, check if we've seen this exact type consecutively —
     * if so, override to return true to skip the redundant endBatch() call.
     */
    @Inject(method = "canConsolidateConsecutiveGeometry", at = @At("RETURN"), cancellable = true, require = 0)
    private void superionic$expandConsolidation(CallbackInfoReturnable<Boolean> cir) {
        if (!SuperionicConfig.batchRendering) return;
        if (cir.getReturnValue()) return; // Already true, nothing to override

        RenderType self = (RenderType)(Object)this;
        RenderType last = superionic$lastQueried.get();

        if (last == self) {
            // Same RenderType instance queried consecutively — safe to consolidate
            cir.setReturnValue(true);
        } else {
            // Different RenderType — remember this one for next time
            superionic$lastQueried.set(self);
        }
    }
}
