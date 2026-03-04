package com.yigitguven.superionic.mixin;
 
import com.yigitguven.superionic.BenchmarkSystem;
import com.yigitguven.superionic.SuperionicConfig;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
 
/**
 * Implements Fast Leaves optimization.
 *
 * This optimization allows leaf blocks to cull internal faces when they
 * are adjacent to other leaf blocks, reducing the total triangle count.
 */
@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {
 
    /**
     * Injects into the skipRendering check.
     * When Fast Leaves is enabled, we skip rendering faces that are adjacent to
     * other leaf blocks, similar to how opaque blocks work.
     */
    @Inject(method = "skipRendering", at = @At("HEAD"), cancellable = true)
    private void superionic$optimizeLeafRendering(BlockState state, BlockState adjacentState, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (SuperionicConfig.fastLeaves) {
            if (adjacentState.getBlock() instanceof LeavesBlock) {
                BenchmarkSystem.recordCulledLeafFace();
                cir.setReturnValue(true);
            }
        }
    }
 
    /**
     * Injects into the light propagation logic to treat leaves as solid blocks.
     */
    @Inject(method = "propagatesSkylightDown", at = @At("HEAD"), cancellable = true)
    private void superionic$fastLeavesLightMapping(BlockState state, net.minecraft.world.level.BlockGetter level, net.minecraft.core.BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (SuperionicConfig.fastLeaves) {
            cir.setReturnValue(false);
        }
    }
}
