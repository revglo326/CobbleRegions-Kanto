package net.mkzone.cobbleregionskanto.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.mkzone.cobbleregionskanto.CobbleRegionsKanto;
import net.mkzone.cobbleregionskanto.block.ModBlocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, CobbleRegionsKanto.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.TELEPORTER.get())
                ;

        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.TELEPORTER.get())
                ;
    }
}
