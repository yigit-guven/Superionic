package com.yigitguven.superionic;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

public class SuperionicMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Superionic Config"));

            ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Batch Rendering"), SuperionicConfig.batchRendering)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Reduces draw calls by tracking and caching RenderType compatibility."))
                .setSaveConsumer(newValue -> SuperionicConfig.batchRendering = newValue)
                .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("HUD Batching"), SuperionicConfig.hudBatching)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Reserved for future HUD batch optimizations (1.21.11 changed the rendering API)."))
                .setSaveConsumer(newValue -> SuperionicConfig.hudBatching = newValue)
                .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Entity Sorting"), SuperionicConfig.entitySorting)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Sorts entities by type before rendering to reduce draw call switches."))
                .setSaveConsumer(newValue -> SuperionicConfig.entitySorting = newValue)
                .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Performance Toast"), SuperionicConfig.showPerformanceToast)
                .setDefaultValue(false)
                .setTooltip(Component.literal("Shows an on-screen overlay with FPS, memory, entity and particle counts."))
                .setSaveConsumer(newValue -> SuperionicConfig.showPerformanceToast = newValue)
                .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Particle Culling"), SuperionicConfig.particleCulling)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Skips rendering particles that are outside the camera view."))
                .setSaveConsumer(newValue -> SuperionicConfig.particleCulling = newValue)
                .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Entity Shadows"), SuperionicConfig.entityShadowCulling)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Optimizes entity shadow rendering to reduce overhead."))
                .setSaveConsumer(newValue -> SuperionicConfig.entityShadowCulling = newValue)
                .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Fast Leaves"), SuperionicConfig.fastLeaves)
                .setDefaultValue(false)
                .setTooltip(Component.literal("Uses a more efficient rendering path for leaf blocks."))
                .setSaveConsumer(newValue -> SuperionicConfig.fastLeaves = newValue)
                .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Fast Chests"), SuperionicConfig.fastChestRendering)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Optimizes chest rendering by reducing model complexity and animations."))
                .setSaveConsumer(newValue -> SuperionicConfig.fastChestRendering = newValue)
                .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("AI Throttling"), SuperionicConfig.aiThrottling)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Reduces AI update frequency for distant mobs to save CPU."))
                .setSaveConsumer(newValue -> SuperionicConfig.aiThrottling = newValue)
                .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Reduce Allocations"), SuperionicConfig.reduceAllocations)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Minimizes temporary object creation to reduce Garbage Collection pressure."))
                .setSaveConsumer(newValue -> SuperionicConfig.reduceAllocations = newValue)
                .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Fast Chunk Loading"), SuperionicConfig.fastChunkLoading)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Optimizes thread priorities for faster world generation and loading."))
                .setSaveConsumer(newValue -> SuperionicConfig.fastChunkLoading = newValue)
                .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Network Tuning"), SuperionicConfig.packetCompressionTuning)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Optimizes packet compression thresholds for smoother multiplayer."))
                .setSaveConsumer(newValue -> SuperionicConfig.packetCompressionTuning = newValue)
                .build());

            return builder.setSavingRunnable(SuperionicConfig::save).build();
        };
    }
}
