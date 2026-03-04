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

            return builder.setSavingRunnable(SuperionicConfig::save).build();
        };
    }
}
