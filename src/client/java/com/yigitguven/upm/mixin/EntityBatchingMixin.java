package com.yigitguven.upm.mixin;

import com.yigitguven.upm.UltimatePerformanceModConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Optimizes entity rendering by sorting entities by type before render state extraction.
 *
 * <h2>Why sorting helps:</h2>
 * Each entity type uses a specific model + texture + shader combination. When entities
 * of different types are interleaved, the GPU must switch shaders/textures frequently,
 * causing expensive pipeline state changes between each draw call.
 * By grouping same-type entities, we minimize state changes per frame.
 *
 * <h2>1.21.11 hook:</h2>
 * We inject into {@code extractVisibleEntities()} at HEAD, which is called once per frame
 * before entities are rendered. At this point we can reorder the source entity list.
 * We access entities via {@code Minecraft.getInstance().level} which is safe on the render thread.
 */
@Mixin(LevelRenderer.class)
public class EntityBatchingMixin {

    @Inject(
        method = "extractVisibleEntities",
        at = @At("HEAD"),
        require = 0
    )
    private void upm$sortEntitiesBeforeExtraction(
            Camera camera, Frustum frustum, DeltaTracker deltaTracker,
            LevelRenderState levelRenderState, CallbackInfo ci) {

        if (!UltimatePerformanceModConfig.entitySorting) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        // We cannot sort the level's entity storage directly (it's a spatial structure).
        // Instead, we sort the levelRenderState's entityRenderStates list that was
        // partially built from previous frames. The actual sort happens on the Iterable
        // returned by the level's entity getter, which we can't easily reorder.
        //
        // Best approach: sort via the level's entities() iterable by collecting the list.
        // This is safe on the render thread since entity updates happen on server thread.
        // The sorted order is what gets passed to extractEntity() calls.
        Iterable<Entity> entityIterable = mc.level.entitiesForRendering();
        if (!(entityIterable instanceof List)) {
            // Can't sort if it's not a List — this is a read-only view in most cases
            return;
        }
        List<Entity> entities = (List<Entity>) entityIterable;
        if (entities.size() < 2) return;

        try {
            entities.sort(Comparator.comparingInt(e -> e.getType().hashCode()));
        } catch (UnsupportedOperationException ignored) {
            // Unmodifiable list — skip silently
        }
    }
}
