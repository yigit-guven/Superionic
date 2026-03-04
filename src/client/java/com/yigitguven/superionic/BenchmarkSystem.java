package com.yigitguven.superionic;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.client.Minecraft;

/**
 * Superionic Benchmark System
 * 
 * Collects real-time metrics from optimization modules and periodically
 * logs them to a CSV file for performance evidence.
 */
public class BenchmarkSystem {
    private static final AtomicLong suppressedFlushes = new AtomicLong(0);
    private static final AtomicLong culledParticles = new AtomicLong(0);
    private static final AtomicLong culledShadows = new AtomicLong(0);
    private static final AtomicLong culledLeafFaces = new AtomicLong(0);
    private static final AtomicLong skippedAiTicks = new AtomicLong(0);
    private static final AtomicLong skippedAllocations = new AtomicLong(0);

    private static final long LOG_INTERVAL_MS = 60000; // Log every 60 seconds
    private static long lastLogTime = System.currentTimeMillis();

    public static void recordSuppressedFlush() { suppressedFlushes.incrementAndGet(); }
    public static void recordCulledParticle() { culledParticles.incrementAndGet(); }
    public static void recordCulledShadow() { culledShadows.incrementAndGet(); }
    public static void recordCulledLeafFace() { culledLeafFaces.incrementAndGet(); }
    public static void recordSkippedAiTick() { skippedAiTicks.incrementAndGet(); }
    public static void recordSkippedAllocation() { skippedAllocations.incrementAndGet(); }

    public static long getSuppressedFlushes() { return suppressedFlushes.get(); }
    public static long getCulledParticles() { return culledParticles.get(); }
    public static long getCulledShadows() { return culledShadows.get(); }
    public static long getCulledLeafFaces() { return culledLeafFaces.get(); }
    public static long getSkippedAiTicks() { return skippedAiTicks.get(); }
    public static long getSkippedAllocations() { return skippedAllocations.get(); }

    public static void tick() {
        if (!SuperionicConfig.enableBenchmarks) return;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastLogTime >= LOG_INTERVAL_MS) {
            saveMetrics();
            lastLogTime = currentTime;
        }
    }

    private static void saveMetrics() {
        Path logPath = Minecraft.getInstance().gameDirectory.toPath().resolve("config/superionic_bench.csv");
        boolean exists = logPath.toFile().exists();

        try (FileWriter writer = new FileWriter(logPath.toFile(), true)) {
            if (!exists) {
                writer.write("Timestamp,SuppressedFlushes,CulledParticles,CulledShadows,CulledLeafFaces,SkippedAiTicks,SkippedAllocations\n");
            }
            writer.write(String.format("%d,%d,%d,%d,%d,%d,%d\n",
                System.currentTimeMillis(),
                suppressedFlushes.getAndSet(0),
                culledParticles.getAndSet(0),
                culledShadows.getAndSet(0),
                culledLeafFaces.getAndSet(0),
                skippedAiTicks.getAndSet(0),
                skippedAllocations.getAndSet(0)
            ));
        } catch (IOException e) {
            SuperionicClient.LOGGER.error("Failed to save benchmark metrics!", e);
        }
    }
}
