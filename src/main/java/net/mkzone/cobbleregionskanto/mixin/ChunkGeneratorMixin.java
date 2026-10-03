package net.mkzone.cobbleregionskanto.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.mkzone.cobbleregionskanto.world.TownsSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.mojang.datafixers.util.Pair;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {

    @Inject(method = "findNearestMapStructure", at = @At("HEAD"), cancellable = true)
    private void onBeforeFindNearestStructure(
            ServerLevel level,
            HolderSet<Structure> structureSet,
            BlockPos originPos,
            int radius,
            boolean skipExistingChunks,
            CallbackInfoReturnable<Pair<BlockPos, Holder<Structure>>> cir) {

        ServerLevel overworld = level.getServer().overworld();
        TownsSavedData data = TownsSavedData.get(overworld);

        // Filter out non-Kanto structures
        boolean isKantoStructure = structureSet.stream().anyMatch(holder -> {
            return holder.unwrapKey()
                    .map(key -> key.location().toString().contains("cobbleregionskanto:"))
                    .orElse(false);
        });

        if (isKantoStructure) {
            Holder<Structure> structureHolder = structureSet.stream().findFirst().orElse(null);

            assert structureHolder != null;
            String town = structureHolder.getRegisteredName();
            System.out.println("=== REGISTERED NAME: " + town + " ===");
            ChunkPos chunkPos = data.getPosOfTown(town);
            if (chunkPos == null) {
                return;
            }

            //System.out.println("=== NEXT TOWN: " + town + " ===");
            BlockPos allowedInstancePos = chunkPos.getWorldPosition();
            System.out.println("Redirecting /locate directly to the valid instance at " + allowedInstancePos);
            cir.setReturnValue(Pair.of(allowedInstancePos, structureHolder));
        }
    }
}
