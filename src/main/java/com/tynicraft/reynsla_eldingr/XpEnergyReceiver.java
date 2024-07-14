package com.tynicraft.reynsla_eldingr;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface XpEnergyReceiver {
    boolean canReceiveEnergy(World world, BlockPos pos);
    void receiveEnergy(World world, BlockPos pos, int amount);
}