package net.mkzone.cobbleregionskanto.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.mkzone.cobbleregionskanto.block.ModBlocks;
import net.mkzone.cobbleregionskanto.block.entity.TeleporterBlockEntity;
import net.mkzone.cobbleregionskanto.core.TeleporterManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

@EventBusSubscriber(modid = "cobbleregionskanto")
public class TeleporterTriggerHandler {

    @SubscribeEvent
    public static void onPlayerJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();

        if (level.isClientSide() || !(entity instanceof ServerPlayer player)) {
            return;
        }

        BlockPos posUnderPlayer = player.blockPosition().below();
        if (level.getBlockState(posUnderPlayer).getBlock() == ModBlocks.TELEPORTER.get()) {

            if (level.getBlockEntity(posUnderPlayer) instanceof TeleporterBlockEntity teleporter) {
                TeleporterManager manager = TeleporterManager.get((ServerLevel) level);
                GlobalPos destination = manager.getDestination(teleporter.getUUID());

                if (destination != null) {
                    executeTeleport(player, (ServerLevel) level, destination);
                }
            }
        }
    }

    private static void executeTeleport(ServerPlayer player, ServerLevel currentLevel, GlobalPos destination) {
        ServerLevel targetLevel = player.getServer().getLevel(destination.dimension());
        if (targetLevel == null) return;

        BlockPos targetPos = destination.pos();

        currentLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PORTAL_TRAVEL, SoundSource.BLOCKS, 0.25F, 1.0F);

        player.teleportTo(targetLevel, targetPos.getX() + 0.5D, targetPos.getY() + 1.0D, targetPos.getZ() + 0.5D,
                player.getYRot(), player.getXRot());

        targetLevel.playSound(null, targetPos.getX() + 0.5D, targetPos.getY() + 1.0D, targetPos.getZ() + 0.5D,
                SoundEvents.PORTAL_TRAVEL, SoundSource.BLOCKS, 0.25F, 1.0F);

        player.resetFallDistance();
    }
}
