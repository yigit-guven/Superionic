package com.yigitguven.upm;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

public class UltimatePerformanceModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Ultimate Performance Mod Config"));

            ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Batch Rendering"), UltimatePerformanceModConfig.batchRendering)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> UltimatePerformanceModConfig.batchRendering = newValue)
                .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Performance Toast"), UltimatePerformanceModConfig.showPerformanceToast)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> UltimatePerformanceModConfig.showPerformanceToast = newValue)
                .build());

            return builder.setSavingRunnable(UltimatePerformanceModConfig::save).build();
        };
    }
}
