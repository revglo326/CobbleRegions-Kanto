package net.mkzone.cobbleregionskanto.util;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.mkzone.cobbleregionskanto.CobbleRegionsKanto;
import net.mkzone.cobbleregionskanto.item.ModItems;

public class ModItemProperties {
    public static void addCustomItemProperties() {
        ItemProperties.register(ModItems.LINKING_TOOL.get(), ResourceLocation.fromNamespaceAndPath(CobbleRegionsKanto.MOD_ID, "used"),
                (stack, level, entity, seed) -> 1f);
    }
}
