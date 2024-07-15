package com.tynicraft.reynsla_eldingr.interaction;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class XpDeviceInteractionHandler {

    public static void handleRightClick(BlockState state, World world, BlockPos pos, PlayerEntity player, int currentEnergy, int maxEnergy, int ticksPerEnergy) {
        if (!world.isClient) {
            String energyInfo = String.format("Charge: %d/%d", currentEnergy, maxEnergy);
            String timeInfo = String.format("Time remaining: %d seconds", (currentEnergy * ticksPerEnergy) / 20);

            player.sendMessage(Text.of(energyInfo), false);
            player.sendMessage(Text.of(timeInfo), false);
        }
    }
}