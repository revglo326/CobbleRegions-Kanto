package net.mkzone.cobbleregionskanto.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.mkzone.cobbleregionskanto.block.entity.TeleporterBlockEntity;
import net.mkzone.cobbleregionskanto.core.TeleporterManager;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TeleporterBlock extends BaseEntityBlock {
    public static final MapCodec<TeleporterBlock> CODEC = simpleCodec(TeleporterBlock::new);

    public TeleporterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TeleporterBlockEntity(blockPos, blockState);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof TeleporterBlockEntity teleporterBlockEntity) {
            teleporterBlockEntity.setUUID(UUID.randomUUID());
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
                if (level.getBlockEntity(pos) instanceof TeleporterBlockEntity teleporter) {
                    UUID id = teleporter.getUUID();
                    if (id != null) {
                        TeleporterManager.get(serverLevel).removeTeleporter(id);
                    }
                }
            }

            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }
}
