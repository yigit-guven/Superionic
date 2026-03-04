package com.yigitguven.superionic.mixin;

import com.yigitguven.superionic.SuperionicConfig;
import net.minecraft.client.renderer.blockentity.ChestBlockEntityRenderer;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Implements Fast Chest Rendering.
 * 
 * Traditional chest rendering involves complex animated models that are expensive
 * to compute every frame. This mixin provides a toggle to simplify chest rendering.
 */
@Mixin(ChestBlockEntityRenderer.class)
public class ChestRendererMixin {

    /**
     * Disable chest lid animations when Fast Chests is enabled.
     * This saves CPU time on animation interpolation.
     */
    @Inject(method = "getOpenness", at = @At("HEAD"), cancellable = true)
    private void superionic$disableChestAnimation(ChestBlockEntity chest, CallbackInfoReturnable<Float> cir) {
        if (SuperionicConfig.fastChestRendering) {
            cir.setReturnValue(0.0f); // Keep chest closed to skip animation logic
        }
    }
}
