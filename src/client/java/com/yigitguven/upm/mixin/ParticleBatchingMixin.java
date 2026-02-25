package com.yigitguven.upm.mixin;

import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Reserved for particle rendering optimizations.
 *
 * In Minecraft 1.21.11, the ParticleEngine rendering pipeline was refactored to
 * use the new RenderState/extract/submit model. The old approach of redirecting
 * endBatch() inside render() is no longer applicable as particle rendering no
 * longer calls endBatch() directly.
 *
 * Future optimizations could hook into the new particle render state pipeline
 * using the extract() method to sort or cull particles before submission.
 */
@Mixin(ParticleEngine.class)
public class ParticleBatchingMixin {
    // Particle rendering optimizations for 1.21.11 would go here.
    // The new API uses extract(ParticlesRenderState, Frustum, Camera, float)
    // for the extraction phase, which could be a future optimization target.
}
