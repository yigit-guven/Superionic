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

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            // --- GENERAL CATEGORY ---
            ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));
            
            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Master Toggle"), SuperionicConfig.enabled)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Master switch to enable or disable all Superionic optimizations at once."))
                .setSaveConsumer(newValue -> SuperionicConfig.enabled = newValue)
                .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Performance Toast"), SuperionicConfig.showPerformanceToast)
                .setDefaultValue(false)
                .setTooltip(Component.literal("Shows an on-screen overlay with FPS, memory, entity and particle counts."))
                .setSaveConsumer(newValue -> SuperionicConfig.showPerformanceToast = newValue)
                .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enable Benchmarks"), SuperionicConfig.enableBenchmarks)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Collects performance metrics during gameplay for the benchmarking CSV."))
                .setSaveConsumer(newValue -> SuperionicConfig.enableBenchmarks = newValue)
                .build());

            // --- RENDERING CATEGORY ---
            ConfigCategory rendering = builder.getOrCreateCategory(Component.literal("Rendering"));

            rendering.addEntry(entryBuilder.startBooleanToggle(Component.literal("Batch Rendering"), SuperionicConfig.batchRendering)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Reduces draw calls by tracking and caching RenderType compatibility."))
                .setSaveConsumer(newValue -> SuperionicConfig.batchRendering = newValue)
                .build());

            rendering.addEntry(entryBuilder.startBooleanToggle(Component.literal("Entity Sorting"), SuperionicConfig.entitySorting)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Sorts entities by type before rendering to reduce draw call switches."))
                .setSaveConsumer(newValue -> SuperionicConfig.entitySorting = newValue)
                .build());

            rendering.addEntry(entryBuilder.startBooleanToggle(Component.literal("Particle Culling"), SuperionicConfig.particleCulling)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Skips rendering particles that are outside the camera view."))
                .setSaveConsumer(newValue -> SuperionicConfig.particleCulling = newValue)
                .build());

            rendering.addEntry(entryBuilder.startIntSlider(Component.literal("Particle Culling Distance"), SuperionicConfig.particleCullingDistance, 16, 256)
                .setDefaultValue(64)
                .setTooltip(Component.literal("Max distance (in blocks) to render particles. Lower is faster."))
                .setSaveConsumer(newValue -> SuperionicConfig.particleCullingDistance = newValue)
                .build());

            rendering.addEntry(entryBuilder.startBooleanToggle(Component.literal("Entity Shadows"), SuperionicConfig.entityShadowCulling)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Optimizes entity shadow rendering to reduce overhead."))
                .setSaveConsumer(newValue -> SuperionicConfig.entityShadowCulling = newValue)
                .build());

            rendering.addEntry(entryBuilder.startIntSlider(Component.literal("Shadow Culling Distance"), SuperionicConfig.shadowCullingDistance, 8, 128)
                .setDefaultValue(64)
                .setTooltip(Component.literal("Max distance (in blocks) to render entity shadows."))
                .setSaveConsumer(newValue -> SuperionicConfig.shadowCullingDistance = newValue)
                .build());

            rendering.addEntry(entryBuilder.startIntSlider(Component.literal("Entity Culling Distance"), SuperionicConfig.entityCullingDistance, 32, 512)
                .setDefaultValue(128)
                .setTooltip(Component.literal("Max distance (in blocks) to render entities completely."))
                .setSaveConsumer(newValue -> SuperionicConfig.entityCullingDistance = newValue)
                .build());

            rendering.addEntry(entryBuilder.startBooleanToggle(Component.literal("Fast Leaves"), SuperionicConfig.fastLeaves)
                .setDefaultValue(false)
                .setTooltip(Component.literal("Uses a more efficient rendering path for leaf blocks."))
                .setSaveConsumer(newValue -> SuperionicConfig.fastLeaves = newValue)
                .build());

            rendering.addEntry(entryBuilder.startBooleanToggle(Component.literal("Fast Chests"), SuperionicConfig.fastChestRendering)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Optimizes chest rendering by reducing model complexity and animations."))
                .setSaveConsumer(newValue -> SuperionicConfig.fastChestRendering = newValue)
                .build());

            // --- AI & PHYSICS CATEGORY ---
            ConfigCategory aiPhysics = builder.getOrCreateCategory(Component.literal("AI & Physics"));

            aiPhysics.addEntry(entryBuilder.startBooleanToggle(Component.literal("AI Throttling"), SuperionicConfig.aiThrottling)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Reduces AI update frequency for distant mobs to save CPU."))
                .setSaveConsumer(newValue -> SuperionicConfig.aiThrottling = newValue)
                .build());

            aiPhysics.addEntry(entryBuilder.startIntSlider(Component.literal("AI Throttling Distance"), SuperionicConfig.aiThrottlingDistance, 16, 256)
                .setDefaultValue(64)
                .setTooltip(Component.literal("Max distance (in blocks) before AI updates are throttled."))
                .setSaveConsumer(newValue -> SuperionicConfig.aiThrottlingDistance = newValue)
                .build());

            aiPhysics.addEntry(entryBuilder.startIntSlider(Component.literal("AI Throttling Rate"), SuperionicConfig.aiThrottlingRate, 1, 20)
                .setDefaultValue(4)
                .setTooltip(Component.literal("Only updates AI every N ticks for distant mobs. Higher saves more CPU."))
                .setSaveConsumer(newValue -> SuperionicConfig.aiThrottlingRate = newValue)
                .build());

            aiPhysics.addEntry(entryBuilder.startBooleanToggle(Component.literal("Reduce Allocations"), SuperionicConfig.reduceAllocations)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Minimizes temporary object creation to reduce Garbage Collection pressure."))
                .setSaveConsumer(newValue -> SuperionicConfig.reduceAllocations = newValue)
                .build());

            aiPhysics.addEntry(entryBuilder.startBooleanToggle(Component.literal("Fast Chunk Loading"), SuperionicConfig.fastChunkLoading)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Optimizes thread priorities for faster world generation and loading."))
                .setSaveConsumer(newValue -> SuperionicConfig.fastChunkLoading = newValue)
                .build());

            // --- NETWORK CATEGORY ---
            ConfigCategory network = builder.getOrCreateCategory(Component.literal("Network"));

            network.addEntry(entryBuilder.startBooleanToggle(Component.literal("Network Tuning"), SuperionicConfig.packetCompressionTuning)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Optimizes packet compression thresholds for smoother multiplayer."))
                .setSaveConsumer(newValue -> SuperionicConfig.packetCompressionTuning = newValue)
                .build());

            return builder.setSavingRunnable(SuperionicConfig::save).build();
        };
    }
}
