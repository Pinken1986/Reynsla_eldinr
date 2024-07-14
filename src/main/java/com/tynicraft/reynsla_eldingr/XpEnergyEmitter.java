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
    private static final float XP_DRAIN_AMOUNT = 1.0f / 4.0f; // 1/4th of a level

    public static void transferExperienceToEnergy(ServerWorld world) {
        for (PlayerEntity player : world.getPlayers()) {
            if (hasExperience(player)) {
                BlockPos playerPos = player.getBlockPos();
                Box searchBox = getSearchBox(playerPos);
                List<BlockPos> receiverPositions = findReadyXpReceiverPositions(world, searchBox);
                if (!receiverPositions.isEmpty()) {
                    if (canDrainExperience(player)) {
                        transferExperienceToReceivers(player, receiverPositions, world);
                    }
                }
            }
        }
    }

    private static boolean hasExperience(PlayerEntity player) {
        return player.experienceLevel > 0 || player.experienceProgress > 0;
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
                    if (block instanceof XpGlowstoneLampBlock) {
                        receiverPositions.add(pos);
                    }
                }
            }
        }
        return receiverPositions;
    }

    private static boolean canDrainExperience(PlayerEntity player) {
        float xpToDrain = getCurrentXpDrainAmount(player);
        return (xpToDrain <= player.experienceLevel);
    }

    private static float getCurrentXpDrainAmount(PlayerEntity player) {
        float xpToDrain = Math.min(XP_DRAIN_AMOUNT, player.experienceProgress);
        if (xpToDrain <= 0 && player.experienceLevel > 0) xpToDrain = 1.0f;
        return xpToDrain;
    }

    private static void transferExperienceToReceivers(PlayerEntity player, List<BlockPos> receiverPositions, ServerWorld world) {
        float xpToDrain = getCurrentXpDrainAmount(player);

        // Calculate XP points in the current level.
        int currentLevelXp = Math.round(xpToDrain * player.getNextLevelExperience());

        // If player has 'unspent' XP, we should use it first.
        if (xpToDrain < 1) {
            player.addExperience(-currentLevelXp);
        } else {
            // Draining more than player's current level. We need to decrease player level and correctly calculate remaining xp.
            player.addExperienceLevels(-1);
            // Calculate XP which is left from the level above.
            int residual = Math.round((player.experienceProgress + (1 - xpToDrain)) * player.getNextLevelExperience());
            // Set remaining XP as 'unspent' XP for the new current level.
            player.addExperience(residual);
        }

        for (BlockPos receiverPos : receiverPositions) {
            transferEnergyToLamp(world, receiverPos);
        }

        System.out.println("Player " + player.getName().getString() + " emitted XP energy to " + receiverPositions.size() + " receivers");
    }

    private static void transferEnergyToLamp(ServerWorld world, BlockPos receiverPos) {
        BlockState blockState = world.getBlockState(receiverPos);
        if (blockState.getBlock() instanceof XpGlowstoneLampBlock) {
            XpGlowstoneLampBlock lamp = (XpGlowstoneLampBlock) blockState.getBlock();
            if (lamp.canReceiveEnergy(world, receiverPos)) {
                lamp.receiveEnergy(world, receiverPos, 1);
            }
        }
    }

}