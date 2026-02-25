package com.yigitguven.upm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class UltimatePerformanceModConfig {
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "ultimateperformancemod.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static boolean batchRendering = true;
    public static boolean hudBatching = true;
    public static boolean entitySorting = true;
    public static boolean showPerformanceToast = false;

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            UltimatePerformanceModConfigData config = GSON.fromJson(reader, UltimatePerformanceModConfigData.class);
            if (config != null) {
                // Only override the Java defaults if the field was present in the JSON
                // (Gson will leave boxed Booleans as null if the key is missing)
                if (config.batchRendering != null) batchRendering = config.batchRendering;
                if (config.hudBatching != null) hudBatching = config.hudBatching;
                if (config.entitySorting != null) entitySorting = config.entitySorting;
                if (config.showPerformanceToast != null) showPerformanceToast = config.showPerformanceToast;
            }
        } catch (IOException e) {
            UltimatePerformanceModClient.LOGGER.error("Failed to load config!", e);
        }
        // Always save after loading to write any new/missing fields with their defaults.
        save();
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(new UltimatePerformanceModConfigData(batchRendering, hudBatching, entitySorting, showPerformanceToast), writer);
        } catch (IOException e) {
            UltimatePerformanceModClient.LOGGER.error("Failed to save config!", e);
        }
    }

    private record UltimatePerformanceModConfigData(
            Boolean batchRendering,
            Boolean hudBatching,
            Boolean entitySorting,
            Boolean showPerformanceToast) {}
}
