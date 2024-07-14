package com.tynicraft.reynsla_eldingr.Blocks.GlowstoneLamp;

import com.tynicraft.reynsla_eldingr.XpEnergyReceiver;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class XpGlowstoneLampBlock extends Block implements XpEnergyReceiver {
    public static final IntProperty ENERGY = IntProperty.of("energy", 0, 10);
    private static final int TICKS_PER_ENERGY = 200; // 10 seconds at 20 ticks per second

    public XpGlowstoneLampBlock(Settings settings) {
        super(settings.luminance((state) -> state.get(ENERGY) > 0 ? 15 : 0));
        setDefaultState(getDefaultState().with(ENERGY, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ENERGY);
    }

    @Override
    public boolean canReceiveEnergy(World world, BlockPos pos) {
        return world.getBlockState(pos).get(ENERGY) < 10;
    }

    @Override
    public void receiveEnergy(World world, BlockPos pos, int amount) {
        synchronized (this) {
            BlockState state = world.getBlockState(pos);
            int currentEnergy = state.get(ENERGY);
            int newEnergy = Math.min(currentEnergy + amount, 10);
            world.setBlockState(pos, state.with(ENERGY, newEnergy));
            if (!world.getBlockTickScheduler().isScheduled(pos, this)) {
                world.getBlockTickScheduler().schedule(pos, this, TICKS_PER_ENERGY);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasRandomTicks(BlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock())) {
            this.updateScheduledTicks(world, pos, state);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        this.updateScheduledTicks(world, pos, state);
    }

    private void updateScheduledTicks(World world, BlockPos pos, BlockState state) {
        if (state.get(ENERGY) > 0 && !world.getBlockTickScheduler().isScheduled(pos, this)) {
            world.getBlockTickScheduler().schedule(pos, this, TICKS_PER_ENERGY);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        synchronized(this) {
            int currentEnergy = state.get(ENERGY);
            if (currentEnergy > 0) {
                int newEnergy = Math.max(currentEnergy - 1, 0);
                BlockState newState = state.with(ENERGY, newEnergy);
                world.setBlockState(pos, newState, Block.NOTIFY_ALL);
                if (newEnergy > 0) {
                    world.getBlockTickScheduler().schedule(pos, this, TICKS_PER_ENERGY);
                }
            }
        }
    }
}