package com.tynicraft.reynsla_eldingr;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class XpFurnace extends BlockEntity implements XpEnergyConsumer {
    private final XpEnergy energy = new XpEnergy();
    private static final int ENERGY_PER_TICK = 1;
    private int cookTime = 0;
    private static final int COOK_TIME_TOTAL = 200; // 10 seconds at 20 ticks per second

    public XpFurnace(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick() {
        if (energy.consumeEnergy(ENERGY_PER_TICK)) {
            cookTime++;
            if (cookTime >= COOK_TIME_TOTAL) {
                // Finish cooking
                cookTime = 0;
            }
        } else {
            // Not enough energy, reset progress
            cookTime = 0;
        }
    }

    @Override
    public void receiveEnergy(int amount) {
        energy.addEnergy(amount);
    }

    @Override
    public boolean canReceiveEnergy() {
        return energy.getEnergy() < energy.getMaxEnergy();
    }

    @Override
    public int getEnergyCapacity() {
        return energy.getMaxEnergy();
    }

    @Override
    public int getCurrentEnergy() {
        return energy.getEnergy();
    }

    // Additional methods for inventory management, etc.
}