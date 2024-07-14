package com.tynicraft.reynsla_eldingr;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class XpRadiationManager {
    private static final double RADIATION_RANGE = 5.0;
    private static final int XP_DRAIN_AMOUNT = 1;
    private static final int ENERGY_PER_XP = 10;

    public static void radiateXpEnergy(ServerWorld world) {
        world.getPlayers().forEach(player -> {
            BlockPos playerPos = player.getBlockPos();
            Box radiationBox = new Box(playerPos).expand(RADIATION_RANGE);

            world.getBlockEntities(radiationBox).forEach(blockEntity -> {
                if (blockEntity instanceof XpEnergyConsumer) {
                    XpEnergyConsumer consumer = (XpEnergyConsumer) blockEntity;
                    if (consumer.canReceiveEnergy() && player.totalExperience >= XP_DRAIN_AMOUNT) {
                        player.addExperience(-XP_DRAIN_AMOUNT);
                        consumer.receiveEnergy(XP_DRAIN_AMOUNT * ENERGY_PER_XP);
                    }
                }
            });
        });
    }
}