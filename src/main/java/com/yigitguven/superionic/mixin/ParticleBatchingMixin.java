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
    @org.spongepowered.asm.mixin.injection.Inject(method = "createParticle", at = @At("HEAD"), cancellable = true)
    private void superionic$cullNewParticle(net.minecraft.core.particles.ParticleOptions options, double x, double y, double z, double dx, double dy, double dz, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Particle> cir) {
        if (!SuperionicConfig.particleCulling) return;
        
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc == null || mc.player == null) return;
        
        double px = mc.player.getX();
        double py = mc.player.getY();
        double pz = mc.player.getZ();
        double dxSqr = x - px;
        double dySqr = y - py;
        double dzSqr = z - pz;
        double distSq = dxSqr * dxSqr + dySqr * dySqr + dzSqr * dzSqr;
        
        // Use configuration distance for particle culling
        if (distSq > SuperionicConfig.particleCullingDistance * SuperionicConfig.particleCullingDistance) {
            BenchmarkSystem.recordCulledParticle();
            cir.setReturnValue(null);
        }
    }
}
