package com.tynicraft.reynsla_eldingr.Blocks.GlowstoneLamp;

import com.tynicraft.reynsla_eldingr.Reynsla_eldingr;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class XpGlowstoneLampBlockEntity extends BlockEntity {
    public XpGlowstoneLampBlockEntity(BlockPos pos, BlockState state) {
        super(Reynsla_eldingr.XP_GLOWSTONE_LAMP_BLOCK_ENTITY, pos, state);
    }

    public int getEnergy() {
        return this.getCachedState().get(XpGlowstoneLampBlock.ENERGY);
    }
}