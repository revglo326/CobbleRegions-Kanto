package net.mkzone.cobbleregionskanto.item;

import net.minecraft.world.item.Item;
import net.mkzone.cobbleregionskanto.CobbleRegionsKanto;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CobbleRegionsKanto.MOD_ID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
