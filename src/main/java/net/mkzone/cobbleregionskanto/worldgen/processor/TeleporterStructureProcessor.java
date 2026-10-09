package net.mkzone.cobbleregionskanto.worldgen.processor;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.mkzone.cobbleregionskanto.block.ModBlocks;
import org.jetbrains.annotations.Nullable;

public class TeleporterStructureProcessor extends StructureProcessor {
    public static final MapCodec<TeleporterStructureProcessor> CODEC = MapCodec.unit(() -> TeleporterStructureProcessor.INSTANCE);
    public static final TeleporterStructureProcessor INSTANCE = new TeleporterStructureProcessor();

    private TeleporterStructureProcessor() {}

    @Nullable
    @Override
    public StructureBlockInfo processBlock(LevelReader level, BlockPos structureBitPos, BlockPos pivot,
                                           StructureBlockInfo originalBlockInfo, StructureBlockInfo currentBlockInfo,
                                           StructurePlaceSettings settings) {

        CompoundTag nbt = currentBlockInfo.nbt();

        if (nbt != null && currentBlockInfo.state().is(ModBlocks.TELEPORTER.get()) && nbt.contains("OffsetX")) {
            int x = nbt.getInt("OffsetX");
            int y = nbt.getInt("OffsetY");
            int z = nbt.getInt("OffsetZ");

            Mirror mirror = settings.getMirror();
            if (mirror == Mirror.LEFT_RIGHT) {
                z = -z;
            } else if (mirror == Mirror.FRONT_BACK) {
                x = -x;
            }

            Rotation rotation = settings.getRotation();
            int rotatedX = x;
            int rotatedZ = z;

            switch (rotation) {
                case CLOCKWISE_90 -> {
                    rotatedX = -z;
                    rotatedZ = x;
                }
                case CLOCKWISE_180 -> {
                    rotatedX = -x;
                    rotatedZ = -z;
                }
                case COUNTERCLOCKWISE_90 -> {
                    rotatedX = z;
                    rotatedZ = -x;
                }
                default -> {}
            }

            nbt.putInt("OffsetX", rotatedX);
            nbt.putInt("OffsetY", y);
            nbt.putInt("OffsetZ", rotatedZ);
        }

        return currentBlockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return ModStructureProcessors.TELEPORTER_PROCESSOR;
    }
}
