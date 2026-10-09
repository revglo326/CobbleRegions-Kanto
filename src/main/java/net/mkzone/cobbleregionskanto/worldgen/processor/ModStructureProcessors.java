package net.mkzone.cobbleregionskanto.worldgen.processor;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = "cobbleregionskanto")
public class ModStructureProcessors {

    public static StructureProcessorType<TeleporterStructureProcessor> TELEPORTER_PROCESSOR;

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.STRUCTURE_PROCESSOR)) {

            TELEPORTER_PROCESSOR = () -> TeleporterStructureProcessor.CODEC;

            event.register(Registries.STRUCTURE_PROCESSOR,
                    ResourceLocation.fromNamespaceAndPath("cobbleregionskanto", "teleporter_processor"),
                    () -> TELEPORTER_PROCESSOR
            );
        }
    }
}
