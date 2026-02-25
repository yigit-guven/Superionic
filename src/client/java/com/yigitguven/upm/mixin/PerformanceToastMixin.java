package com.yigitguven.upm.mixin;

import com.yigitguven.upm.UltimatePerformanceModConfig;
import com.yigitguven.upm.ui.PerformanceToast;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class PerformanceToastMixin {

    @Unique
    private boolean upm$toastAdded = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;
        
        if (UltimatePerformanceModConfig.showPerformanceToast) {
            if (!upm$toastAdded) {
                client.getToastManager().addToast(new PerformanceToast());
                upm$toastAdded = true;
            }
        } else {
            // When disabled, the PerformanceToast's render method returns Visibility.HIDE
            // which will eventually remove it from the manager.
            // We reset our flag so it can be re-added later.
            upm$toastAdded = false;
        }
    }
}
