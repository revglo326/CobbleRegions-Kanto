package net.mkzone.cobbleregionskanto.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.Map;
import java.util.Set;

public class TownsSavedData extends SavedData {

    private static final java.util.Map<String, ChunkPos> TRACKED_TOWNS = new java.util.concurrent.ConcurrentHashMap<>();
    private static final Set<ChunkPos> BLOCKED_TOWNS_BY_POS = new java.util.HashSet<>();

    public static final SavedData.Factory<TownsSavedData> FACTORY = new SavedData.Factory<>(
            TownsSavedData::new,
            TownsSavedData::load,
            null // DataFixerType can be null
    );

    public TownsSavedData() {}

    public static TownsSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        TownsSavedData data = new TownsSavedData();
        if (tag.contains("Towns", Tag.TAG_LIST)) {
            ListTag list = tag.getList("Towns", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag entry = list.getCompound(i);
                String id = entry.getString("Id");
                int x = entry.getInt("X");
                int z = entry.getInt("Z");
                data.TRACKED_TOWNS.put(id, new ChunkPos(x, z));
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        ListTag list = new ListTag();
        for (Map.Entry<String, ChunkPos> entry : TRACKED_TOWNS.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("Id", entry.getKey());
            entryTag.putInt("X", entry.getValue().x);
            entryTag.putInt("Z", entry.getValue().z);
            list.add(entryTag);
        }
        compoundTag.put("Towns", list);
        return compoundTag;
    }

    public boolean trackTown(String structureId, ChunkPos origin) {
        if (!TRACKED_TOWNS.containsKey(structureId)) {
            TRACKED_TOWNS.put(structureId, origin);
            this.setDirty(); // Crucial: Tells the game to save this data
            return true;
        }
        return false;
    }

    public boolean blockTown(ChunkPos origin) {
        return BLOCKED_TOWNS_BY_POS.add(origin);
    }

    public ChunkPos getPosOfTown(String structureId) {
        return TRACKED_TOWNS.get(structureId);
    }

    public static TownsSavedData get(ServerLevel level) {
        // We always tie this global data to the Overworld so it's uniform across dimensions
        ServerLevel overworld = level.getServer().overworld();
        DimensionDataStorage storage = overworld.getDataStorage();
        return storage.computeIfAbsent(FACTORY, "cobbleregionskanto_towns");
    }

    public static java.util.Map<String, ChunkPos> getTrackedTowns() {
        return TRACKED_TOWNS;
    }

    public static Set<ChunkPos> getBlockedTowns() {
        return BLOCKED_TOWNS_BY_POS;
    }
}
