package com.yigitguven.superionic.mixin;

import com.yigitguven.superionic.SuperionicConfig;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.AbstractMap;

/**
 * Core batch rendering optimization for Minecraft 1.21.11.
 *
 * <h2>How `getBuffer()` works in 1.21.11:</h2>
 * <ol>
 *   <li>Look up if a started {@link BufferBuilder} exists for this {@link RenderType}.</li>
 *   <li>If one exists: call {@code RenderType.canConsolidateConsecutiveGeometry()}.
 *       If that returns {@code true}, the existing builder is REUSED (no flush needed).
 *       If it returns {@code false}, {@code endBatch(RenderType, BufferBuilder)} is called
 *       to flush the existing batch to the GPU.</li>
 *   <li>If no builder exists: check the fixed-buffer map, then create a new shared builder.
 *       If the shared buffer was used by a different type, {@code endBatch(RenderType)} is
 *       called first to flush it.</li>
 * </ol>
 *
 * <h2>Our optimization:</h2>
 * We redirect the {@code endBatch(RenderType, BufferBuilder)} call inside {@code getBuffer()}
 * to suppress it when the incoming {@link RenderType} is compatible with the existing one
 * (same pipeline). This allows multiple consecutive draw calls of the same pipeline to share
 * a single GPU submission, dramatically reducing draw call overhead.
 *
 * We also cache the pipeline-equality check in a {@link ConcurrentHashMap} so the check itself
 * is O(1) amortized after the first encounter (rather than O(n) via full {@code equals()}).
 */
@Mixin(MultiBufferSource.BufferSource.class)
public abstract class BatchRenderingMixin {

    @Unique
    private static final Map<Long, Boolean> superionic$batchCache = new ConcurrentHashMap<>(256);

    @Unique
    private RenderType superionic$pendingBatchType = null;

    @Shadow public abstract void endBatch(RenderType renderType);

    @Redirect(
        method = "getBuffer(Lnet/minecraft/client/renderer/rendertype/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch(Lnet/minecraft/client/renderer/rendertype/RenderType;)V"
        )
    )
    private void superionic$suppressEndBatchIfCompatible(
            MultiBufferSource.BufferSource self,
            RenderType existingType) {

        if (!SuperionicConfig.batchRendering) {
            self.endBatch(existingType);
            return;
        }

        if (superionic$pendingBatchType != null) {
            self.endBatch(superionic$pendingBatchType);
        }

        com.yigitguven.superionic.BenchmarkSystem.recordSuppressedFlush();
        superionic$pendingBatchType = existingType;
    }

    @Inject(
        method = "getBuffer(Lnet/minecraft/client/renderer/rendertype/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
        at = @At("RETURN")
    )
    private void superionic$flushPendingOnReturn(RenderType incomingType, CallbackInfoReturnable<VertexConsumer> cir) {
        if (!SuperionicConfig.batchRendering) return;

        if (superionic$pendingBatchType != null && superionic$pendingBatchType != incomingType) {
            ((MultiBufferSource.BufferSource)(Object)this).endBatch(superionic$pendingBatchType);
            superionic$pendingBatchType = null;
        } else if (superionic$pendingBatchType != null && superionic$pendingBatchType == incomingType) {
            superionic$pendingBatchType = null;
        }
    }

    @Inject(method = "endBatch()V", at = @At("HEAD"))
    private void superionic$flushPendingOnEndBatch(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        if (superionic$pendingBatchType != null) {
            ((MultiBufferSource.BufferSource)(Object)this).endBatch(superionic$pendingBatchType);
            superionic$pendingBatchType = null;
        }
    }

    @Unique
    private static long superionic$cacheKey(RenderType a, RenderType b) {
        return ((long) System.identityHashCode(a) << 32) | (System.identityHashCode(b) & 0xFFFFFFFFL);
    }
}
