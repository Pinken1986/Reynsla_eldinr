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
            if (player.experienceLevel > 0 || player.experienceProgress > 0) {
                BlockPos playerPos = player.getBlockPos();
                Box searchBox = new Box(playerPos).expand(EMISSION_RANGE);
                List<BlockPos> receiverPositions = findXpReceiverPositionsNearPlayer(world, searchBox);

                if (!receiverPositions.isEmpty()) {
                    float xpToDrain = calculateExperienceDrain(player.experienceProgress, player.experienceLevel);

                    if (xpToDrain <= 0 && player.experienceLevel > 0) {
                        player.addExperienceLevels(-1);
                        player.addExperience((int)((1.0f - XP_DRAIN_AMOUNT) * player.getNextLevelExperience()));
                    } else {
                        int xpPoints = (int)(xpToDrain * player.getNextLevelExperience());
                        player.addExperience(-xpPoints);
                    }

                    for (BlockPos receiverPos : receiverPositions) {
                        BlockState blockState = world.getBlockState(receiverPos);
                        if (blockState.getBlock() instanceof XpGlowstoneLampBlock) {
                            XpGlowstoneLampBlock lamp = (XpGlowstoneLampBlock) blockState.getBlock();
                            if (lamp.canReceiveEnergy(world, receiverPos)) {
                                lamp.receiveEnergy(world, receiverPos, 1);
                            }
                        }
                    }

                    Reynsla_eldingr.LOGGER.info("Player {} emitted XP energy to {} receivers",
                            player.getName().getString(), receiverPositions.size());
                }
            }
        }
    }

    private static float calculateExperienceDrain(float experienceProgress, int experienceLevel) {
        float xpToDrain = Math.min(XP_DRAIN_AMOUNT, experienceProgress);
        if (xpToDrain <= 0 && experienceLevel > 0) {
            xpToDrain = 1.0f;
        }
        return xpToDrain;
    }

    private static List<BlockPos> findXpReceiverPositionsNearPlayer(ServerWorld world, Box searchBox) {
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
}