package com.tynicraft.reynsla_eldingr;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Reynsla_eldingr implements ModInitializer {
    public static final String MOD_ID = "reynsla_eldingr";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    // Block and BlockEntity registration
    public static final Block XP_FURNACE_BLOCK = new XpFurnaceBlock(Block.Settings.of(Material.METAL).strength(4.0f));
    public static final BlockEntityType<XpFurnace> XP_FURNACE_ENTITY = FabricBlockEntityTypeBuilder.create(XpFurnace::new, XP_FURNACE_BLOCK).build(null);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Reynsla Eldingr mod");

        // Register the XP Furnace block and item
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "xp_furnace"), XP_FURNACE_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "xp_furnace"), new BlockItem(XP_FURNACE_BLOCK, new Item.Settings().group(ItemGroup.MISC)));

        // Register the XP Furnace block entity
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "xp_furnace"), XP_FURNACE_ENTITY);

        // Set up server tick events for XP radiation
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerWorld world : server.getWorlds()) {
                XpRadiationManager.radiateXpEnergy(world);
            }
        });

        LOGGER.info("Reynsla Eldingr mod initialized");
    }
}