package net.mkzone.cobbleregionskanto.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.mkzone.cobbleregionskanto.CobbleRegionsKanto;
import net.mkzone.cobbleregionskanto.block.ModBlocks;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CobbleRegionsKanto.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.TELEPORTER);
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
