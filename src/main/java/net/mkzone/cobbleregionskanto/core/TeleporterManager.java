package net.mkzone.cobbleregionskanto.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleporterManager extends SavedData {
    private final Map<UUID, GlobalPos> teleporters = new HashMap<>();

    TeleporterManager() {}

    public static TeleporterManager get(ServerLevel level) {
        DimensionDataStorage storage = level.getServer().overworld().getDataStorage();
        return storage.computeIfAbsent(new SavedData.Factory<>(
                TeleporterManager::new,
                TeleporterManager::load,
                null
        ), "teleporter_manager");
    }

    public void setDestination(UUID teleporterId, GlobalPos destination) {
        teleporters.put(teleporterId, destination);
        setDirty();
    }

    public GlobalPos getDestination(UUID teleporterId) {
        return teleporters.get(teleporterId);
    }

    public void removeTeleporter(UUID teleporterId) {
        if (teleporters.remove(teleporterId) != null) {
            setDirty();
        }
    }

    public static TeleporterManager load(CompoundTag tag, HolderLookup.Provider registries) {
        TeleporterManager manager = new TeleporterManager();
        if (tag.contains("Teleporters", Tag.TAG_LIST)) {
            ListTag list = tag.getList("Teleporters", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag entry = list.getCompound(i);
                UUID id = entry.getUUID("ID");

                BlockPos pos = NbtUtils.readBlockPos(entry, "Pos").orElse(BlockPos.ZERO);
                ResourceKey<Level> dim = ResourceKey.create(Registries.DIMENSION,
                        ResourceLocation.parse(entry.getString("Dim")));

                manager.teleporters.put(id, GlobalPos.of(dim, pos));
            }
        }

        return manager;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (Map.Entry<UUID, GlobalPos> entry : teleporters.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putUUID("ID", entry.getKey());
            entryTag.put("Pos", NbtUtils.writeBlockPos(entry.getValue().pos()));
            entryTag.putString("Dim", entry.getValue().dimension().location().toString());

            list.add(entryTag);
        }

        tag.put("Teleporters", list);
        return tag;
    }
}
