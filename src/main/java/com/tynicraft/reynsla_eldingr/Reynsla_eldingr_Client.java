package com.tynicraft.reynsla_eldingr;

import com.tynicraft.reynsla_eldingr.Blocks.GlowstoneLamp.XpGlowstoneLampBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public class Reynsla_eldingr_Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(Reynsla_eldingr.XP_GLOWSTONE_LAMP_BLOCK_ENTITY, XpGlowstoneLampBlockEntityRenderer::new);
    }
}