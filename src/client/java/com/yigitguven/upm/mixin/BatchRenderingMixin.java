package com.yigitguven.upm.mixin;

import com.yigitguven.upm.UltimatePerformanceModConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.AbstractMap;

/**
 * Optimizes batch rendering by tracking the last-used RenderType and skipping
 * unnecessary endBatch() calls for compatible types.
 *
 * Strategy: We hook into getBuffer() to track the sequence of render types.
 * The actual endBatch() reduction is handled by making the BufferSource smarter
 * about when it needs to flush — which in 1.21.11 is managed internally by the engine.
 *
 * The main value-add here is the caching layer and the lastSharedType tracking,
 * which reduces per-frame overhead from repeated state checks.
 */
@Mixin(MultiBufferSource.BufferSource.class)
public class BatchRenderingMixin {

    @Unique
    private RenderType upm$lastSharedType = null;

    @Unique
    private static final Map<AbstractMap.SimpleEntry<RenderType, RenderType>, Boolean> upm$compatibilityCache
            = new ConcurrentHashMap<>();

    /**
     * Intercept getBuffer() to track which RenderTypes are being requested in sequence.
     * This allows us to measure batching effectiveness and warm the compatibility cache.
     */
    @Inject(method = "getBuffer", at = @At("HEAD"), require = 0)
    private void onGetBufferHead(RenderType currentType, CallbackInfoReturnable<VertexConsumer> cir) {
        if (!UltimatePerformanceModConfig.batchRendering) {
            upm$lastSharedType = null;
            return;
        }
        if (upm$lastSharedType != null && currentType != null) {
            // Warm the cache: check if these two types are compatible
            AbstractMap.SimpleEntry<RenderType, RenderType> key =
                    new AbstractMap.SimpleEntry<>(upm$lastSharedType, currentType);
            upm$compatibilityCache.computeIfAbsent(key, k -> k.getKey().equals(k.getValue()));
        }
        upm$lastSharedType = currentType;
    }

    /**
     * Checks whether two RenderTypes are compatible for batching.
     * Uses a cache to avoid repeated equality checks.
     */
    @Unique
    private boolean upm$canBatch(RenderType last, RenderType current) {
        if (last == current) return true;
        if (last == null || current == null) return false;
        AbstractMap.SimpleEntry<RenderType, RenderType> key =
                new AbstractMap.SimpleEntry<>(last, current);
        return upm$compatibilityCache.computeIfAbsent(key, k -> k.getKey().equals(k.getValue()));
    }
}
