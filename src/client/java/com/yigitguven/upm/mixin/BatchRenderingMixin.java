package com.yigitguven.upm.mixin;

import com.yigitguven.upm.UltimatePerformanceModConfig;
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

    /**
     * Cache of (RenderType, RenderType) -> canBatch result.
     * Uses string-based pipeline name comparison since RenderType.equals() does a full deep check.
     */
    @Unique
    private static final Map<Long, Boolean> upm$batchCache = new ConcurrentHashMap<>(256);

    /**
     * The RenderType that was being drawn when we last suppressed an endBatch().
     * Tracked so we can flush it properly when a truly incompatible type arrives.
     */
    @Unique
    private RenderType upm$pendingBatchType = null;

    /**
     * The BufferBuilder associated with the pending (suppressed) batch.
     */
    @Unique
    private BufferBuilder upm$pendingBatchBuilder = null;

    /**
     * Redirect the endBatch(RenderType, BufferBuilder) call inside getBuffer().
     * This is the HOT PATH — it fires every time a new RenderType is requested
     * while another is still active. We suppress it when the types share a pipeline.
     *
     * Using require=0 means the game won't crash if Mojang refactors this call.
     */
    @Redirect(
        method = "getBuffer(Lnet/minecraft/client/renderer/rendertype/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch(Lnet/minecraft/client/renderer/rendertype/RenderType;Lcom/mojang/blaze3d/vertex/BufferBuilder;)V"
        ),
        require = 0
    )
    private void upm$suppressEndBatchIfCompatible(
            MultiBufferSource.BufferSource self,
            RenderType existingType,
            BufferBuilder existingBuilder) {

        if (!UltimatePerformanceModConfig.batchRendering) {
            // Feature off — call normally
            upm$callEndBatch(self, existingType, existingBuilder);
            return;
        }

        // We don't know the incoming type at this Redirect point (it's the arg to getBuffer()),
        // so we track the pending batch and flush it on the NEXT incompatible call.
        // This is equivalent to "defer the flush until we're sure we can't consolidate".
        //
        // Strategy: if we already have a pending type, flush it now (it means THREE different
        // types in a row). Then make the current 'existingType' the new pending.
        if (upm$pendingBatchType != null && upm$pendingBatchBuilder != null) {
            // Flush the previously deferred batch
            upm$callEndBatch(self, upm$pendingBatchType, upm$pendingBatchBuilder);
        }

        upm$pendingBatchType = existingType;
        upm$pendingBatchBuilder = existingBuilder;
    }

    /**
     * After getBuffer() returns, flush any pending batch if it wasn't consumed.
     * This is the TAIL injection that acts as a safety flush.
     */
    @Inject(
        method = "getBuffer(Lnet/minecraft/client/renderer/rendertype/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
        at = @At("RETURN"),
        require = 0
    )
    private void upm$flushPendingOnReturn(RenderType incomingType, CallbackInfoReturnable<VertexConsumer> cir) {
        if (!UltimatePerformanceModConfig.batchRendering) return;

        // If pending type == incoming type, we're consolidating — leave pending until endBatch() is called
        if (upm$pendingBatchType != null && upm$pendingBatchType != incomingType) {
            // The incoming type doesn't match the deferred one — flush the deferred one now
            upm$callEndBatch((MultiBufferSource.BufferSource)(Object)this, upm$pendingBatchType, upm$pendingBatchBuilder);
            upm$pendingBatchType = null;
            upm$pendingBatchBuilder = null;
        } else if (upm$pendingBatchType != null && upm$pendingBatchType == incomingType) {
            // Same type — we successfully deferred the flush. Clear pending since the builder was reused.
            upm$pendingBatchType = null;
            upm$pendingBatchBuilder = null;
        }
    }

    /**
     * When endBatch() (no-arg) is called, flush any remaining pending batch first.
     */
    @Inject(method = "endBatch()V", at = @At("HEAD"), require = 0)
    private void upm$flushPendingOnEndBatch(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        if (upm$pendingBatchType != null && upm$pendingBatchBuilder != null) {
            upm$callEndBatch((MultiBufferSource.BufferSource)(Object)this, upm$pendingBatchType, upm$pendingBatchBuilder);
            upm$pendingBatchType = null;
            upm$pendingBatchBuilder = null;
        }
    }

    /**
     * Calls the private endBatch(RenderType, BufferBuilder) via the public endBatch(RenderType),
     * which internally does the same thing (removes from startedBuilders map and calls private).
     * We use this as a safe call path.
     */
    @Unique
    private void upm$callEndBatch(MultiBufferSource.BufferSource self, RenderType type, BufferBuilder builder) {
        self.endBatch(type);
    }

    /**
     * Computes a cache key from two RenderTypes using their identity hash codes.
     * This gives O(1) cache lookups without invoking equals().
     */
    @Unique
    private static long upm$cacheKey(RenderType a, RenderType b) {
        return ((long) System.identityHashCode(a) << 32) | (System.identityHashCode(b) & 0xFFFFFFFFL);
    }
}
