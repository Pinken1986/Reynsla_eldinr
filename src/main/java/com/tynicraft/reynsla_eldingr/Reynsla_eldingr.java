package com.tynicraft.reynsla_eldingr;

import com.tynicraft.reynsla_eldingr.Blocks.GlowstoneLamp.XpGlowstoneLampBlock;
import com.tynicraft.reynsla_eldingr.Blocks.GlowstoneLamp.XpGlowstoneLampBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Reynsla_eldingr implements ModInitializer {

    public static final String MOD_ID = "reynsla_eldingr";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    // Block
    public static final XpGlowstoneLampBlock XP_GLOWSTONE_LAMP = createXpGlowstoneLampBlock();

    // Block Entity
    public static BlockEntityType<XpGlowstoneLampBlockEntity> XP_GLOWSTONE_LAMP_BLOCK_ENTITY = createXpGlowstoneLampBlockEntity();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Reynsla Eldingr mod");
        registerXpGlowstoneLamp();
        LOGGER.info("Reynsla Eldingr mod initialized");
    }

    private static XpGlowstoneLampBlock createXpGlowstoneLampBlock() {
        return new XpGlowstoneLampBlock(
                FabricBlockSettings.of(Material.REDSTONE_LAMP)
                        .strength(0.3F)
                        .ticksRandomly()
        );
    }

    private static BlockEntityType<XpGlowstoneLampBlockEntity> createXpGlowstoneLampBlockEntity() {
        return FabricBlockEntityTypeBuilder.create(XpGlowstoneLampBlockEntity::new, XP_GLOWSTONE_LAMP).build(null);
    }

    private void registerXpGlowstoneLamp() {
        // Register Block
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "xp_glowstone_lamp"), XP_GLOWSTONE_LAMP);

        // Register Block Item
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "xp_glowstone_lamp"),
                new BlockItem(XP_GLOWSTONE_LAMP, new FabricItemSettings().group(ItemGroup.REDSTONE)));

        // Register Block Entity
        XP_GLOWSTONE_LAMP_BLOCK_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(MOD_ID, "xp_glowstone_lamp"),
                XP_GLOWSTONE_LAMP_BLOCK_ENTITY);

        // Register Server Tick Event for XP Energy Emission
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            server.getWorlds().forEach(XpEnergyEmitter::transferExperienceToEnergy);
        });
    }
}