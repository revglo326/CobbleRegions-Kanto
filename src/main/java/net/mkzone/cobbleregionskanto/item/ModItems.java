package net.mkzone.cobbleregionskanto.item;

import net.minecraft.world.item.Item;
import net.mkzone.cobbleregionskanto.CobbleRegionsKanto;
import net.mkzone.cobbleregionskanto.item.custom.LinkingToolItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CobbleRegionsKanto.MOD_ID);

    public static final DeferredItem<Item> LINKING_TOOL = ITEMS.register("linking_tool",
            () -> new LinkingToolItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
