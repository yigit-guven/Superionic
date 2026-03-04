package com.yigitguven.superionic.mixin;
 
import com.yigitguven.superionic.SuperionicConfig;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
 
/**
 * Implements Network Optimization and Packet Tuning.
 * 
 * We tune the packet compression threshold to reduce CPU overhead 
 * on smaller packets that don't benefit much from compression.
 */
@Mixin(Connection.class)
public class NetworkMixin {
 
    /**
     * Boost the compression threshold to 512 (from vanilla 256) 
     * to skip compression for medium-sized packets, saving CPU.
     */
    @ModifyVariable(method = "setupCompression", at = @At("HEAD"), argsOnly = true)
    private int superionic$optimizeCompressionThreshold(int threshold) {
        if (SuperionicConfig.enabled && SuperionicConfig.packetCompressionTuning && threshold > 0 && threshold < 512) {
            return 512;
        }
        return threshold;
    }
}
