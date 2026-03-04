package com.yigitguven.superionic.ui;

import com.yigitguven.superionic.SuperionicConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class PerformanceToast implements Toast {
    private static final int WIDTH = 160;
    private static final int HEIGHT = 52; // Increased for extra row

    @Override
    public Object getToken() {
        return "superionic_performance_toast";
    }

    @Override
    public Visibility getWantedVisibility() {
        return SuperionicConfig.showPerformanceToast ? Visibility.SHOW : Visibility.HIDE;
    }

    @Override
    public void update(ToastManager manager, long startTime) {
    }

    @Override
    public void render(GuiGraphics graphics, Font font, long startTime) {
        if (!SuperionicConfig.showPerformanceToast) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        int fps = client.getFps();
        double ms = 1000.0 / Math.max(1, fps);
        
        long maxMem = Runtime.getRuntime().maxMemory();
        long totalMem = Runtime.getRuntime().totalMemory();
        long freeMem = Runtime.getRuntime().freeMemory();
        long usedMem = totalMem - freeMem;
        int memPercent = (int) (usedMem * 100 / maxMem);

        int entities = (client.level != null) ? client.level.getEntityCount() : 0;
        // Optimizations
        long culledParticles = com.yigitguven.superionic.BenchmarkSystem.getCulledParticles();
        long aiSavings = com.yigitguven.superionic.BenchmarkSystem.getSkippedAiTicks();
        long batchSavings = com.yigitguven.superionic.BenchmarkSystem.getSuppressedFlushes();

        // --- Premium Glassmorphism UI ---
        // Semi-transparent main plate
        graphics.fill(0, 0, WIDTH, HEIGHT, 0x99111111);
        // Cyan gradient border (bottom)
        graphics.fill(0, HEIGHT - 1, WIDTH, HEIGHT, 0xFF00F0FF);
        // Orange accent bar (left)
        graphics.fill(0, 0, 2, HEIGHT - 1, 0xFFFFB800);

        // Header
        graphics.drawString(font, Component.literal("SUPERIONIC ENGINE").withStyle(ChatFormatting.BOLD), 10, 5, 0xFF00F0FF, false);

        // Grid Layout
        // Column 1
        drawMetric(graphics, font, "FPS", String.valueOf(fps), 10, 18, 0xFFFFB800, 0xFFFFFFFF);
        drawMetric(graphics, font, "MEM", memPercent + "%", 10, 28, 0xFFFFB800, 0xFFFFFFFF);

        // Column 2
        drawMetric(graphics, font, "MS ", String.format("%.1f", ms), 85, 18, 0xFFFFB800, 0xFFFFFFFF);
        drawMetric(graphics, font, "ENT", String.valueOf(entities), 85, 28, 0xFFFFB800, 0xFFFFFFFF);

        // Row 3: Optimizations (Real-time proof)
        drawMetric(graphics, font, "OPT", (culledParticles + aiSavings + batchSavings) + "", 10, 38, 0xFF00F0FF, 0xFF00F0FF);
    }

    private void drawMetric(GuiGraphics graphics, Font font, String label, String value, int x, int y, int labelColor, int valueColor) {
        graphics.drawString(font, label + ":", x, y, labelColor, false);
        int offset = font.width(label + ":") + 4;
        graphics.drawString(font, value, x + offset, y, valueColor, false);
    }
}
