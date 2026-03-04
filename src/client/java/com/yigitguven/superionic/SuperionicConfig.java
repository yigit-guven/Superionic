package com.yigitguven.superionic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SuperionicConfig {
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "superionic.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static {
        load();
    }

    public static boolean batchRendering = true;
    public static boolean hudBatching = true;
    public static boolean entitySorting = true;
    public static boolean showPerformanceToast = false;
    public static boolean particleCulling = true;
    public static boolean entityShadowCulling = true;
    public static boolean fastLeaves = false;
    public static boolean fastChestRendering = true;
    public static boolean aiThrottling = true;
    public static boolean reduceAllocations = true;
    public static boolean fastChunkLoading = true;
    public static boolean packetCompressionTuning = true;
    public static double particleRenderDistance = 64.0;
    public static boolean enableBenchmarks = false;

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            SuperionicConfigData config = GSON.fromJson(reader, SuperionicConfigData.class);
            if (config != null) {
                // Only override the Java defaults if the field was present in the JSON
                // (Gson will leave boxed Booleans as null if the key is missing)
                if (config.batchRendering != null) batchRendering = config.batchRendering;
                if (config.hudBatching != null) hudBatching = config.hudBatching;
                if (config.entitySorting != null) entitySorting = config.entitySorting;
                if (config.showPerformanceToast != null) showPerformanceToast = config.showPerformanceToast;
                if (config.particleCulling != null) particleCulling = config.particleCulling;
                if (config.entityShadowCulling != null) entityShadowCulling = config.entityShadowCulling;
                if (config.fastLeaves != null) fastLeaves = config.fastLeaves;
                if (config.fastChestRendering != null) fastChestRendering = config.fastChestRendering;
                if (config.aiThrottling != null) aiThrottling = config.aiThrottling;
                if (config.reduceAllocations != null) reduceAllocations = config.reduceAllocations;
                if (config.fastChunkLoading != null) fastChunkLoading = config.fastChunkLoading;
                if (config.packetCompressionTuning != null) packetCompressionTuning = config.packetCompressionTuning;
                if (config.particleRenderDistance != null) particleRenderDistance = config.particleRenderDistance;
                if (config.enableBenchmarks != null) enableBenchmarks = config.enableBenchmarks;
            }
        } catch (IOException e) {
            SuperionicClient.LOGGER.error("Failed to load config!", e);
        }
        // Always save after loading to write any new/missing fields with their defaults.
        save();
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(new SuperionicConfigData(
                batchRendering, hudBatching, entitySorting, showPerformanceToast, 
                particleCulling, entityShadowCulling, fastLeaves, 
                fastChestRendering, aiThrottling, reduceAllocations, 
                fastChunkLoading, packetCompressionTuning, particleRenderDistance,
                enableBenchmarks), writer);
        } catch (IOException e) {
            SuperionicClient.LOGGER.error("Failed to save config!", e);
        }
    }

    private record SuperionicConfigData(
            Boolean batchRendering,
            Boolean hudBatching,
            Boolean entitySorting,
            Boolean showPerformanceToast,
            Boolean particleCulling,
            Boolean entityShadowCulling,
            Boolean fastLeaves,
            Boolean fastChestRendering,
            Boolean aiThrottling,
            Boolean reduceAllocations,
            Boolean fastChunkLoading,
            Boolean packetCompressionTuning,
            Double particleRenderDistance,
            Boolean enableBenchmarks) {}
}
