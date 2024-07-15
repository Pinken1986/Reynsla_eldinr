package com.tynicraft.reynsla_eldingr;

import com.tynicraft.reynsla_eldingr.Blocks.GlowstoneLamp.XpGlowstoneLampBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

public class XpEnergyEmitter {
    private static final double EMISSION_RANGE = 1.0;
    private static final int XP_PER_ENERGY = 2; // 2 XP points per 1 energy unit
    private static final int MAX_ENERGY_TRANSFER = 10; // Max 10 energy units per transfer

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
                    Block block = world.getBlockState(pos).getBlock();
                    if (block instanceof XpGlowstoneLampBlock && ((XpGlowstoneLampBlock) block).canReceiveEnergy(world, pos)) {
                        receiverPositions.add(pos);
                    }
                }
            }
        }
        return receiverPositions;
    }

    private static void transferExperienceToReceivers(PlayerEntity player, List<BlockPos> receiverPositions, ServerWorld world) {
        int totalEnergyTransferred = 0;

        for (BlockPos receiverPos : receiverPositions) {
            BlockState blockState = world.getBlockState(receiverPos);
            if (blockState.getBlock() instanceof XpGlowstoneLampBlock) {
                XpGlowstoneLampBlock lamp = (XpGlowstoneLampBlock) blockState.getBlock();
                int currentEnergy = blockState.get(XpGlowstoneLampBlock.ENERGY);
                int energyToTransfer = Math.min(MAX_ENERGY_TRANSFER - currentEnergy, player.totalExperience / XP_PER_ENERGY);

                if (energyToTransfer > 0) {
                    lamp.receiveEnergy(world, receiverPos, energyToTransfer);
                    player.addExperience(-energyToTransfer * XP_PER_ENERGY);
                    totalEnergyTransferred += energyToTransfer;
                }
            }
        }

        if (totalEnergyTransferred > 0) {
            System.out.println("Player " + player.getName().getString() + " XP drained: " + (totalEnergyTransferred * XP_PER_ENERGY) +
                    ", Energy emitted: " + totalEnergyTransferred + " to " + receiverPositions.size() + " receivers");
        }
    }
}