package com.tynicraft.reynsla_eldingr.Blocks.GlowstoneLamp;

import com.tynicraft.reynsla_eldingr.XpEnergyReceiver;
import com.tynicraft.reynsla_eldingr.interaction.XpDeviceInteractionHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;

import java.util.Random;

public class XpGlowstoneLampBlock extends Block implements XpEnergyReceiver {
    public static final IntProperty ENERGY = IntProperty.of("energy", 0, 2);
    public static final int TICKS_PER_ENERGY = 1200;  // changing TICKS_PER_ENERGY to 1 minute
    public static final int MAX_ENERGY = 2;

    public XpGlowstoneLampBlock(Settings settings) {
        super(settings.luminance((state) -> state.get(ENERGY) > 0 ? 15 : 0));
        setDefaultState(getStateManager().getDefaultState().with(ENERGY, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ENERGY);
    }

    @Override
    public boolean canReceiveEnergy(World world, BlockPos pos) {
        return world.getBlockState(pos).get(ENERGY) < MAX_ENERGY;
    }

    @Override
    public void receiveEnergy(World world, BlockPos pos, int amount) {
        synchronized (this) {
            BlockState state = world.getBlockState(pos);
            int currentEnergy = state.get(ENERGY);
            int newEnergy = Math.min(currentEnergy + (amount * 2), MAX_ENERGY);
            world.setBlockState(pos, state.with(ENERGY, newEnergy));
            if (!world.getBlockTickScheduler().isScheduled(pos, this)) {
                world.getBlockTickScheduler().schedule(pos, this, TICKS_PER_ENERGY);
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        XpDeviceInteractionHandler.handleRightClick(state, world, pos, player, state.get(ENERGY), MAX_ENERGY, TICKS_PER_ENERGY);
        return ActionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
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
                int newEnergy = currentEnergy - 1;
                BlockState newState = state.with(ENERGY, newEnergy);
                world.setBlockState(pos, newState, Block.NOTIFY_ALL);
                if (newEnergy > 0) {
                    world.getBlockTickScheduler().schedule(pos, this, TICKS_PER_ENERGY);
                }
            }
        }
    }
}