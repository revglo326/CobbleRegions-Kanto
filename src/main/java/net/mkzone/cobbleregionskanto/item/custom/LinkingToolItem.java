package net.mkzone.cobbleregionskanto.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.mkzone.cobbleregionskanto.block.ModBlocks;
import net.mkzone.cobbleregionskanto.block.entity.TeleporterBlockEntity;
import net.mkzone.cobbleregionskanto.core.TeleporterManager;

import java.util.Optional;
import java.util.UUID;

public class LinkingToolItem extends Item {

    public LinkingToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Block clickedBlock = level.getBlockState(clickedPos).getBlock();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player == null) return InteractionResult.PASS;

        // Reset teleporter or save block as destination
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                if (clickedBlock == ModBlocks.TELEPORTER.get() && level.getBlockEntity(clickedPos) instanceof TeleporterBlockEntity teleporter) {
                    if (TeleporterManager.get((ServerLevel) level).getDestination(teleporter.getUUID()) != null) {
                        TeleporterManager.get((ServerLevel) level).removeTeleporter(teleporter.getUUID());
                        player.sendSystemMessage(Component.literal("=== TELEPORTER UNLINKED SUCCESSFULLY! ==="));

                        return InteractionResult.SUCCESS;
                    }
                }

                GlobalPos newLink = GlobalPos.of(level.dimension(), clickedPos);
                saveLinkedPos(stack, newLink);
                player.sendSystemMessage(Component.literal("=== LINKING TOOL LINKED TO POSITION " + clickedPos.toShortString() + "! ==="));
            }

            return InteractionResult.SUCCESS;
        }

        Optional<GlobalPos> linkedPosOpt = getLinkedPos(stack, level);

        // Check teleporter info
        if (linkedPosOpt.isEmpty()) {
            if (!level.isClientSide && clickedBlock == ModBlocks.TELEPORTER.get() && level.getBlockEntity(clickedPos) instanceof TeleporterBlockEntity teleporter) {
                TeleporterManager teleporterManager = TeleporterManager.get((ServerLevel) level);
                GlobalPos dest = teleporterManager.getDestination(teleporter.getUUID());

                if (dest != null) {
                    BlockPos localOffset = dest.pos().subtract(clickedPos);
                    player.sendSystemMessage(Component.literal("=== TELEPORTER DESTINATION IS " + dest.pos().toShortString() + " (" + localOffset.toShortString() + " BLOCKS AWAY) ==="));
                } else {
                    player.sendSystemMessage(Component.literal("=== THIS TELEPORTER HAS NO DESTINATION SET ==="));
                }

                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        }

        // Link teleporter to saved position
        GlobalPos linkedPos = linkedPosOpt.get();

        if (clickedBlock == ModBlocks.TELEPORTER.get()) {
            if (!level.isClientSide && level.getBlockEntity(clickedPos) instanceof TeleporterBlockEntity teleporter) {
                ServerLevel serverLevel = (ServerLevel) level;
                TeleporterManager teleporterManager = TeleporterManager.get(serverLevel);
                UUID currentId = teleporter.getUUID();
                GlobalPos currentPos = GlobalPos.of(serverLevel.dimension(), clickedPos);

                teleporterManager.setDestination(currentId, linkedPos);

                BlockPos offsetForA = linkedPos.pos().subtract(clickedPos);
                teleporter.setStructureOffset(offsetForA);

                //System.out.println("LINKED TELEPORTER TO POSITION: " + linkedPos.pos().toShortString());
                //System.out.println("LINKED POSITION IS " + offsetForA.toShortString() + " BLOCKS AWAY");

                ServerLevel targetLevel = serverLevel.getServer().getLevel(linkedPos.dimension());
                if (targetLevel != null && targetLevel.getBlockEntity(linkedPos.pos()) instanceof TeleporterBlockEntity targetTeleporter) {
                    UUID targetId = targetTeleporter.getUUID();
                    teleporterManager.setDestination(targetId, currentPos);

                    BlockPos offsetForB = clickedPos.subtract(linkedPos.pos());
                    targetTeleporter.setStructureOffset(offsetForB);

                    //System.out.println("TARGET IS A TELEPORTER. TWO-WAY CONNECTION ESTABLISHED");
                }// else { System.out.println("TARGET IS NOT A TELEPORTER"); }

                clearLinkedPos(stack);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private void saveLinkedPos(ItemStack stack, GlobalPos globalPos) {
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> customData.update(tag -> {
            tag.put("LinkedPos", NbtUtils.writeBlockPos(globalPos.pos()));
            tag.putString("LinkedDim", globalPos.dimension().location().toString());
        }));
    }

    private Optional<GlobalPos> getLinkedPos(ItemStack stack, Level level) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return Optional.empty();

        CompoundTag tag = customData.getUnsafe();
        if (tag.contains("LinkedPos") && tag.contains("LinkedDim")) {
            return NbtUtils.readBlockPos(tag, "LinkedPos").map(pos -> {
                var dimKey = net.minecraft.core.registries.Registries.DIMENSION;
                var dimLocation = net.minecraft.resources.ResourceLocation.parse(tag.getString("LinkedDim"));
                var dim = net.minecraft.resources.ResourceKey.create(dimKey, dimLocation);
                return GlobalPos.of(dim, pos);
            });
        }
        return Optional.empty();
    }

    private void clearLinkedPos(ItemStack stack) {
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> customData.update(tag -> {
            tag.remove("LinkedPos");
            tag.remove("LinkedDim");
        }));
    }
}