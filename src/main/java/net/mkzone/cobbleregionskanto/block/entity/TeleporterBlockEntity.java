package net.mkzone.cobbleregionskanto.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.mkzone.cobbleregionskanto.core.TeleporterManager;
import java.util.UUID;

public class TeleporterBlockEntity extends BlockEntity {
    private UUID uuid;
    private BlockPos structureOffset = null;
    private boolean isGeneratedByStructure = false;

    public TeleporterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.TELEPORTER_BE.get(), pos, blockState);
    }

    public UUID getUUID() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
        return this.uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
        this.setChanged();
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (this.level != null && !this.level.isClientSide && this.level instanceof ServerLevel serverLevel) {
            if (this.isGeneratedByStructure || this.uuid == null) {
                this.uuid = UUID.randomUUID();
                this.setChanged();

                if (this.structureOffset != null) {
                    BlockPos targetWorldPos = this.worldPosition.offset(this.structureOffset);
                    TeleporterManager manager = TeleporterManager.get(serverLevel);

                    GlobalPos destination = GlobalPos.of(serverLevel.dimension(), targetWorldPos);
                    manager.setDestination(this.getUUID(), destination);
                }

                this.isGeneratedByStructure = false;
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putUUID("teleportBlockEntityUUID", getUUID());

        if (this.structureOffset != null) {
            tag.putInt("OffsetX", this.structureOffset.getX());
            tag.putInt("OffsetY", this.structureOffset.getY());
            tag.putInt("OffsetZ", this.structureOffset.getZ());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.hasUUID("teleportBlockEntityUUID")) {
            this.uuid = tag.getUUID("teleportBlockEntityUUID");
        }

        if (tag.contains("OffsetX") && tag.contains("OffsetY") && tag.contains("OffsetZ")) {
            this.structureOffset = new BlockPos(
                    tag.getInt("OffsetX"),
                    tag.getInt("OffsetY"),
                    tag.getInt("OffsetZ")
            );
        }

        if (tag.contains("forge:structure_piece") || tag.contains("minecraft:structure") || this.structureOffset != null) {
            this.isGeneratedByStructure = true;
        }
    }

    public void setStructureOffset(BlockPos offset) {
        this.structureOffset = offset;
        this.setChanged();
    }
}
