package com.yigitguven.upm.mixin;

import com.yigitguven.upm.UltimatePerformanceModConfig;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Optimizes HUD rendering by reducing the number of flushing operations.
 * This allows many HUD elements to be drawn in a single batch if they share the same state.
 */
@Mixin(GuiGraphics.class)
public class HudBatchingMixin {

    @Redirect(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V", 
              at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;flush()V"))
    private void onRenderItemFlush(GuiGraphics instance) {
        if (!UltimatePerformanceModConfig.hudBatching) {
            instance.flush();
        }
        // Skip flush to allow batching with other items/elements
    }

    @Redirect(method = "drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/FormattedText;IIIZ)I", 
              at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;flush()V"))
    private void onDrawStringFlush(GuiGraphics instance) {
        if (!UltimatePerformanceModConfig.hudBatching) {
            instance.flush();
        }
        // Skip flush to group text calls
    }
}
