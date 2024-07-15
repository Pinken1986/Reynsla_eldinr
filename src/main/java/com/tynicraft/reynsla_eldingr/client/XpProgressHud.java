package com.tynicraft.reynsla_eldingr.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class XpProgressHud implements HudRenderCallback {
    private static final int TEXT_COLOR = 0xFFFFFF; // White color
    private static final int BACKGROUND_COLOR = 0x80000000; // Semi-transparent black

    @Override
    public void onHudRender(MatrixStack matrixStack, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.player != null && client.currentScreen == null) {
            TextRenderer textRenderer = client.textRenderer;
            float xpProgress = client.player.experienceProgress;
            int xpLevel = client.player.experienceLevel;

            String displayText = String.format("XP Progress: %.2f%% (Level %d)", xpProgress * 100, xpLevel);
            int textWidth = textRenderer.getWidth(displayText);
            int textHeight = textRenderer.fontHeight;

            int x = 5; // 5 pixels from the left edge
            int y = 5; // 5 pixels from the top edge

            // Set up rendering
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            // Draw background
            DrawableHelper.fill(matrixStack, x - 2, y - 2, x + textWidth + 2, y + textHeight + 2, BACKGROUND_COLOR);

            // Draw text
            textRenderer.draw(matrixStack, Text.of(displayText), x, y, TEXT_COLOR);

            // Clean up rendering
            RenderSystem.disableBlend();
        }
    }
}