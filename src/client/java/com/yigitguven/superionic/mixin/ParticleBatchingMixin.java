package com.yigitguven.superionic.mixin;
 
import com.yigitguven.superionic.BenchmarkSystem;
import com.yigitguven.superionic.SuperionicConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.world.phys.Vec3;

/**
 * Implements Particle Culling for Minecraft 1.21.11.
 *
 * In 1.21.11, particles are processed and culled during the extraction phase.
 * We hook into the particle iteration to skip those that are outside the frustum.
 */
@Mixin(ParticleEngine.class)
public class ParticleBatchingMixin {
    @Shadow protected ClientLevel level;

    /**
     * Redirect the iterator in the particle rendering to skip particles that are not visible.
     * This reduces the amount of data sent to the GPU and CPU processing time per particle.
     */
    @Redirect(
        method = "render",
        at = @At(value = "INVOKE", target = "Ljava/util/Collection;iterator()Ljava/util/Iterator;")
    )
    private Iterator<Particle> superionic$cullParticles(Collection<Particle> collection, com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.client.renderer.MultiBufferSource vertexConsumers, net.minecraft.client.renderer.LightTexture lightTexture, Camera camera, float tickDelta) {
        Iterator<Particle> original = collection.iterator();
        if (!SuperionicConfig.particleCulling) return original;

        return new Iterator<Particle>() {
            private Particle next;
            private boolean checked;

            @Override
            public boolean hasNext() {
                if (!checked) {
                    while (original.hasNext()) {
                        Particle p = original.next();
                        Vec3 cameraPos = ((CameraAccessor)camera).getPosition();
                        double dx = ((ParticleAccessor)p).getX() - cameraPos.x;
                        double dy = ((ParticleAccessor)p).getY() - cameraPos.y;
                        double dz = ((ParticleAccessor)p).getZ() - cameraPos.z;
                        double distSq = dx * dx + dy * dy + dz * dz;
                        double maxDistSq = SuperionicConfig.particleRenderDistance * SuperionicConfig.particleRenderDistance;

                        if (distSq > maxDistSq) {
                            BenchmarkSystem.recordCulledParticle();
                            continue;
                        }
                        
                        next = p;
                        break;
                    }
                    checked = true;
                }
                return next != null;
            }

            @Override
            public Particle next() {
                if (!hasNext()) throw new java.util.NoSuchElementException();
                Particle p = next;
                next = null;
                checked = false;
                return p;
            }
        };
    }
}
