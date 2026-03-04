package com.yigitguven.superionic.mixin;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;

/**
 * HUD rendering optimization hook for Minecraft 1.21.11.
 *
 * The old GuiGraphics.flush() API was completely removed in Minecraft 1.21.11 as part
 * of a major rendering pipeline modernization effort:
 * - GuiGraphics now uses the new submit/RenderState model internally.
 * - Draw call batching is managed automatically by the rendering backend.
 * - The explicit flush() mechanism no longer exists or is needed at the user level.
 *
 * This mixin is intentionally left as a clean stub to prevent crashes.
 * Future optimizations targeting the new rendering pipeline will be added here
 * once we identify actionable interception points in the 1.21.11 API.
 */
@Mixin(GuiGraphics.class)
public class HudBatchingMixin {
    // Intentionally empty — see class Javadoc for explanation.
}
