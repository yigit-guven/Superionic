package com.yigitguven.superionic.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class DiagnosticMixin {
    private static boolean superionic$probed = false;

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void superionic$probeMethods(CallbackInfo ci) {
        if (superionic$probed) return;
        superionic$probed = true;

        System.out.println("[SUPERIONIC-PROBE] RUNNING FROM LevelRenderer MIXIN (FIELD REFLECTION)");
        try {
            // Traverse fields of LevelRenderer to find interesting objects
            for (java.lang.reflect.Field f : this.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                Object val = f.get(this);
                if (val == null) continue;

                String typeName = val.getClass().getName();
                if (typeName.contains("EntityRenderDispatcher") || 
                    typeName.contains("ParticleEngine") || 
                    typeName.contains("RenderBuffers")) {
                    
                    System.out.println("[SUPERIONIC-PROBE] Found interesting field in LevelRenderer: " + f.getName() + " (" + typeName + ")");
                    System.out.println("  Methods for " + typeName + ":");
                    for (java.lang.reflect.Method m : val.getClass().getDeclaredMethods()) {
                        System.out.println("    " + m.getName() + " " + m.toString());
                    }
                }
            }

            // Also check ChestRenderer
            System.out.println("[SUPERIONIC-PROBE] Probing ChestRenderer...");
            try {
                Class<?> rtClass = this.getClass().getClassLoader().loadClass("net.minecraft.client.renderer.blockentity.ChestRenderer");
                for (java.lang.reflect.Method m : rtClass.getDeclaredMethods()) {
                    System.out.println("    " + m.getName() + " " + m.toString());
                }
            } catch (Exception e) {
                System.out.println("  FAILED to load ChestRenderer: " + e.getMessage());
            }

            // Also check RenderType
            System.out.println("[SUPERIONIC-PROBE] Probing RenderType...");
            try {
                Class<?> rtClass = this.getClass().getClassLoader().loadClass("net.minecraft.client.renderer.RenderType");
                for (java.lang.reflect.Method m : rtClass.getDeclaredMethods()) {
                    System.out.println("    " + m.getName() + " " + m.toString());
                }
            } catch (Exception e) {
                System.out.println("  FAILED to load RenderType: " + e.getMessage());
            }


            // Also check Minecraft instance
            Object mc = net.minecraft.client.Minecraft.getInstance();
            System.out.println("[SUPERIONIC-PROBE] Probing Minecraft instance...");
            for (java.lang.reflect.Field f : mc.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                Object val = f.get(mc);
                if (val == null) continue;
                String typeName = val.getClass().getName();
                if (typeName.contains("ParticleEngine")) {
                    System.out.println("[SUPERIONIC-PROBE] Found ParticleEngine in Minecraft: " + f.getName() + " (" + typeName + ")");
                    for (java.lang.reflect.Method m : val.getClass().getDeclaredMethods()) {
                        System.out.println("    " + m.getName() + " " + m.toString());
                    }
                }
            }
        } catch (Throwable e) {
            System.out.println("[SUPERIONIC-PROBE] CRITICAL FAILURE: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
