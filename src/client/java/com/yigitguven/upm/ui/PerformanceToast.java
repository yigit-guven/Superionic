package com.yigitguven.upm.ui;

import com.yigitguven.upm.UltimatePerformanceModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class PerformanceToast implements Toast {
    private static final int WIDTH = 160;
    private static final int HEIGHT = 42; // Compact but enough for 3 rows

    @Override
    public Object getToken() {
        return "upm_performance_toast";
    }

    @Override
    public Visibility getWantedVisibility() {
        return UltimatePerformanceModConfig.showPerformanceToast ? Visibility.SHOW : Visibility.HIDE;
    }

    @Override
    public void update(ToastManager manager, long startTime) {
    }

    @Override
    public void render(GuiGraphics graphics, Font font, long startTime) {
        if (!UltimatePerformanceModConfig.showPerformanceToast) {
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
        String particles = (client.particleEngine != null) ? client.particleEngine.countParticles() : "0";
        // Clean up particle string if it contains "Particles: "
        if (particles.startsWith("Particles: ")) {
            particles = particles.substring(11);
        }

        // Modern Background
        graphics.fill(0, 0, WIDTH, HEIGHT, 0xAA222222);
        graphics.fill(0, 0, 2, HEIGHT, 0xFF55FF55);

        // Row 1: FPS & MS
        graphics.drawString(font, Component.literal("FPS: ").append(Component.literal(String.valueOf(fps)).withStyle(getFpsStyle(fps))), 10, 5, 0xFFFFFFFF, false);
        graphics.drawString(font, Component.literal("MS: ").append(Component.literal(String.format("%.1f", ms)).withStyle(ChatFormatting.WHITE)), 80, 5, 0xFFFFFFFF, false);
        
        // Row 2: Memory & Entities
        graphics.drawString(font, Component.literal("MEM: ").append(Component.literal(memPercent + "%").withStyle(getMemoryStyle(memPercent))), 10, 18, 0xFFFFFFFF, false);
        graphics.drawString(font, Component.literal("E: ").append(Component.literal(String.valueOf(entities)).withStyle(ChatFormatting.WHITE)), 80, 18, 0xFFFFFFFF, false);

        // Row 3: Particles
        graphics.drawString(font, Component.literal("P: ").append(Component.literal(particles).withStyle(ChatFormatting.WHITE)), 10, 31, 0xFFFFFFFF, false);
    }

    private ChatFormatting getFpsStyle(int fps) {
        if (fps >= 60) return ChatFormatting.GREEN;
        if (fps >= 30) return ChatFormatting.YELLOW;
        return ChatFormatting.RED;
    }

    private ChatFormatting getMemoryStyle(int percent) {
        if (percent < 70) return ChatFormatting.GREEN;
        if (percent < 90) return ChatFormatting.YELLOW;
        return ChatFormatting.RED;
    }
}
