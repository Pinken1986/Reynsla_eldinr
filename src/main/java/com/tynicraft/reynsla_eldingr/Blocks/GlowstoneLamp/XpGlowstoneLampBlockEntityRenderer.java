package com.tynicraft.reynsla_eldingr.Blocks.GlowstoneLamp;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;

public class XpGlowstoneLampBlockEntityRenderer implements BlockEntityRenderer<XpGlowstoneLampBlockEntity> {

    public XpGlowstoneLampBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(XpGlowstoneLampBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        BlockPos blockPos = entity.getPos();

        if (player != null && player.squaredDistanceTo(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5) <= 4) {
            matrices.push();
            matrices.translate(0.5, 1.5, 0.5);
            matrices.multiply(MinecraftClient.getInstance().getEntityRenderDispatcher().getRotation());
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));

            float scale = 0.025f;
            matrices.scale(scale, scale, scale);

            int energy = entity.getEnergy();
            String text = "Energy: " + energy + "/10";
            int color = 0xFFFFFF;

            MinecraftClient.getInstance().textRenderer.draw(matrices, text, -MinecraftClient.getInstance().textRenderer.getWidth(text) / 2f, 0, color);

            matrices.pop();
        }
    }
}