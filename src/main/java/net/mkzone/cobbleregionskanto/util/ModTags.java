package net.mkzone.cobbleregionskanto.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.mkzone.cobbleregionskanto.CobbleRegionsKanto;

public class ModTags {

    private static final TagKey<Structure> KANTO_TOWNS_TAG = TagKey.create(
            Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(CobbleRegionsKanto.MOD_ID, "kanto_towns")
    );
}
