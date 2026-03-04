package com.yigitguven.superionic.mixin;

import com.yigitguven.superionic.SuperionicConfig;
import com.yigitguven.superionic.ui.PerformanceToast;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class PerformanceToastMixin {

    @Unique
    private boolean superionic$toastAdded = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;
        
        if (SuperionicConfig.showPerformanceToast) {
            if (!superionic$toastAdded) {
                client.getToastManager().addToast(new PerformanceToast());
                superionic$toastAdded = true;
            }
        } else {
            // When disabled, the PerformanceToast's render method returns Visibility.HIDE
            // which will eventually remove it from the manager.
            // We reset our flag so it can be re-added later.
            superionic$toastAdded = false;
        }
    }
}
