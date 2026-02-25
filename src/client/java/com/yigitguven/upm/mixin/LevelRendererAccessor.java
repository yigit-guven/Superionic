package com.yigitguven.upm.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
    @Accessor("entitiesForRendering")
    List<Entity> getEntitiesForRendering();
}
