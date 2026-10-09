package net.mkzone.cobbleregionskanto.block.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.mkzone.cobbleregionskanto.CobbleRegionsKanto;
import net.mkzone.cobbleregionskanto.block.ModBlocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CobbleRegionsKanto.MOD_ID);

    public static final Supplier<BlockEntityType<TeleporterBlockEntity>> TELEPORTER_BE =
            BLOCK_ENTITIES.register("teleporter_be", () -> BlockEntityType.Builder.of(
                    TeleporterBlockEntity::new, ModBlocks.TELEPORTER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
