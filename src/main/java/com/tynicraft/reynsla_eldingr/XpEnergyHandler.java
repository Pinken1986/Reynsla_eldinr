package com.tynicraft.reynsla_eldingr;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;


public class XpEnergyHandler {
    private static final Map<BlockPos, PriorityQueue<PlayerEntity>> receivers = new HashMap<>();

    public static void requestEnergyTransfer(PlayerEntity player, BlockPos receiverPosition) {
        receivers.computeIfAbsent(receiverPosition, pos -> new PriorityQueue<>(
                Comparator.comparing(PlayerEntity::getScore).reversed())
        ).add(player);
    }

    private static boolean canDrainExperience(PlayerEntity player) {
        return player.experienceLevel > 0;
    }

    public static void processEnergyTransfers(ServerWorld world) {
        receivers.forEach((receiverPosition, playerPriorityQueue) -> {
            if (!playerPriorityQueue.isEmpty()) {
                PlayerEntity player = playerPriorityQueue.peek();

                if (canDrainExperience(player)) {
                    transferExperienceToReceiver(player, receiverPosition, world);
                    playerPriorityQueue.remove(); // experience has been transferred, remove this player
                } else {
                    player.sendMessage(new LiteralText("You do not have enough experience."), false);
                }
            }
        });
        receivers.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    private static void transferExperienceToReceiver(PlayerEntity player, BlockPos receiverPosition, ServerWorld world) {
        XpEnergyEmitter.transferExperienceToEnergy(world);
    }
}