package com.yigitguven.upm.mixin;

import com.yigitguven.upm.UltimatePerformanceModConfig;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;

/**
 * Optimizes entity rendering by sorting entities by type before the render pass.
 * Sorting ensures entities of the same type (and thus the same RenderType/texture)
 * are rendered consecutively, allowing the GPU to batch them into fewer draw calls.
 *
 * Note: In Minecraft 1.21.11, the rendering pipeline was refactored to use a
 * RenderState/submit model. The entity sorting @Inject at HEAD remains a valid
 * optimization regardless of the pipeline version.
 */
@Mixin(LevelRenderer.class)
public class EntityBatchingMixin {

    @Inject(method = "renderLevel", at = @At("HEAD"), require = 0)
    private void onRenderLevelStart(CallbackInfo ci) {
        if (!UltimatePerformanceModConfig.entitySorting) return;

        // Access entities via the LevelRendererAccessor
        List<Entity> entities = ((LevelRendererAccessor) this).getEntitiesForRendering();
        if (entities != null && entities.size() > 1) {
            // Sort entities by type name to group same models/textures together
            // This is thread-safe as we're on the render thread and the list is local
            try {
                entities.sort(Comparator.comparing(e -> e.getType().toString()));
            } catch (UnsupportedOperationException ignored) {
                // Some entity lists may be unmodifiable; skip sorting gracefully
            }
        }
    }
}
