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
        
        long maxMem = Runtime.getRuntime().maxMemory();
        long totalMem = Runtime.getRuntime().totalMemory();
        long freeMem = Runtime.getRuntime().freeMemory();
        long usedMem = totalMem - freeMem;
        int memPercent = (int) (usedMem * 100 / maxMem);

        // Optimization Data
        int enabledFeatures = SuperionicConfig.getEnabledFeatureCount();
        long totalSavings = com.yigitguven.superionic.BenchmarkSystem.getTotalSessionSavings();

        // --- Professional UI Theme ---
        // Solid Dark Charcoal background for a minimal look
        graphics.fill(0, 0, WIDTH, HEIGHT, 0xDD111111);
        
        // Subtle top accent line (Dark Gold/Slate)
        graphics.fill(0, 0, WIDTH, 1, 0xFF444444);
        // Subtle left bar
        graphics.fill(0, 0, 1, HEIGHT, 0xFF555555);

        // Header: Clean and professional
        graphics.drawString(font, Component.literal("Performance Toast").withStyle(ChatFormatting.BOLD), 8, 6, 0xFFCCCCCC, false);

        // Column 1: System Metrics
        drawProfessionalMetric(graphics, font, "FPS", String.valueOf(fps), 8, 20, 0xFF888888, 0xFFFFFFFF);
        drawProfessionalMetric(graphics, font, "RAM", memPercent + "%", 8, 32, 0xFF888888, 0xFFFFFFFF);

        // Column 2: Engine Metrics
        drawProfessionalMetric(graphics, font, "ACTIVE ", enabledFeatures + "/11", 80, 20, 0xFF888888, 0xFFFFFFFF);
        drawProfessionalMetric(graphics, font, "SAVINGS ", formatSavings(totalSavings), 80, 32, 0xFF888888, 0xFF55FF55);
    }

    private void drawProfessionalMetric(GuiGraphics graphics, Font font, String label, String value, int x, int y, int labelColor, int valueColor) {
        graphics.drawString(font, label + ":", x, y, labelColor, false);
        int offset = font.width(label + ":") + 4;
        graphics.drawString(font, value, x + offset, y, valueColor, false);
    }

    private String formatSavings(long savings) {
        if (savings < 1000) return String.valueOf(savings);
        if (savings < 1000000) return String.format("%.1fK", savings / 1000.0);
        return String.format("%.2fM", savings / 1000000.0);
    }
}
