package com.tynicraft.reynsla_eldingr;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

public class XpEnergyEmitter {

    private static final double EMISSION_RANGE = 1.0;
    private static final float XP_FRACTION_TO_TRANSFER = 0.1F;

    public static void transferExperienceToEnergy(ServerWorld world) {
        for (PlayerEntity player : world.getPlayers()) {
            if (player.totalExperience > 0) {
                BlockPos playerPos = player.getBlockPos();
                Box searchBox = getSearchBox(playerPos);
                List<BlockPos> receiverPositions = findReadyXpReceiverPositions(world, searchBox);
                if (!receiverPositions.isEmpty()) {
                    transferExperienceToReceivers(player, receiverPositions, world);
                }
            }
        }
    }

    private static Box getSearchBox(BlockPos playerPos) {
        return new Box(playerPos).expand(EMISSION_RANGE);
    }

    private static List<BlockPos> findReadyXpReceiverPositions(ServerWorld world, Box searchBox) {
        List<BlockPos> receiverPositions = new ArrayList<>();
        for (int x = (int) searchBox.minX; x <= (int) searchBox.maxX; x++) {
            for (int y = (int) searchBox.minY; y <= (int) searchBox.maxY; y++) {
                for (int z = (int) searchBox.minZ; z <= (int) searchBox.maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState blockState = world.getBlockState(pos);
                    if (blockState.getBlock() instanceof XpEnergyReceiver && ((XpEnergyReceiver) blockState.getBlock()).canReceiveEnergy(world, pos)) {
                        receiverPositions.add(pos);
                    }
                }
            }
        }
        return receiverPositions;
    }

    private static void transferExperienceToReceivers(PlayerEntity player, List<BlockPos> receiverPositions, ServerWorld world) {
        int totalExperienceTransferred = 0;

        for (BlockPos receiverPos : receiverPositions) {
            BlockState blockState = world.getBlockState(receiverPos);
            if (blockState.getBlock() instanceof XpEnergyReceiver) {
                XpEnergyReceiver energyReceiver = (XpEnergyReceiver) blockState.getBlock();
                int experienceToTransfer = (int) (player.totalExperience * XP_FRACTION_TO_TRANSFER);

                if (experienceToTransfer > 0) {
                    energyReceiver.receiveEnergy(world, receiverPos, experienceToTransfer);
                    totalExperienceTransferred += experienceToTransfer;
                    player.addExperience(-experienceToTransfer); // subtract transferred XP from player
                }
            }
        }

        System.out.println("Player " + player.getName().getString() + " lost " + totalExperienceTransferred +
                " XP. Energy emitted: " + totalExperienceTransferred + " to " + receiverPositions.size() + " receivers.");
    }
}