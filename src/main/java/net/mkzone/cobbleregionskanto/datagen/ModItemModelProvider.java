package net.mkzone.cobbleregionskanto.datagen;

import net.minecraft.data.PackOutput;
import net.mkzone.cobbleregionskanto.CobbleRegionsKanto;
import net.mkzone.cobbleregionskanto.item.ModItems;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CobbleRegionsKanto.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.LINKING_TOOL.get());
    }
}
