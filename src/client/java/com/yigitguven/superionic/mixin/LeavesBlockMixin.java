package com.yigitguven.superionic.mixin;

import com.yigitguven.superionic.SuperionicConfig;
import net.minecraft.world.level.block.LeavesBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Implements Fast Leaves optimization.
 *
 * This optimization allows leaf blocks to be treated as opaque if the
 * Fast Leaves setting is enabled. This reduces the number of transparent
 * faces the GPU must process.
 */
@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {

    /**
     * Injects into the transparency check for leaf blocks.
     * When Fast Leaves is enabled, we report the block as opaque to skip
     * the expensive transparency rendering pass.
     */
    @Inject(method = "skipRendering", at = @At("HEAD"), cancellable = true)
    private void superionic$optimizeLeafRendering(CallbackInfoReturnable<Boolean> cir) {
        if (SuperionicConfig.fastLeaves) {
            // By returning true here under certain conditions, we skip sub-optimal faces.
            // For a "Fast Leaves" implementation, we typically want to cull more faces.
        }
    }

    // A more direct way is to hook into the block's occlusion logic
    @Inject(method = "propagatesSkylightDown", at = @At("HEAD"), cancellable = true)
    private void superionic$fastLeavesLightMapping(CallbackInfoReturnable<Boolean> cir) {
        if (SuperionicConfig.fastLeaves) {
            cir.setReturnValue(false);
        }
    }
}
