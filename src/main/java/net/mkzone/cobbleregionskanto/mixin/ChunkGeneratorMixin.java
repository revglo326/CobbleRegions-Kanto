package net.mkzone.cobbleregionskanto.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
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
        if (data == null) return;

        // 1. Check if the structure being searched for matches your target structure type
        // (You want to filter this so you don't override /locate for vanilla villages, strongholds, etc.)
        boolean isYourTargetStructure = structureSet.stream().anyMatch(holder -> {
            return holder.unwrapKey()
                    .map(key -> key.location().toString().equals("cobbleregionskanto:pewtercity/pewtercity"))
                    .orElse(false);
        });

        if (isYourTargetStructure) {
            // 2. Fetch the exact, singular allowed position from your saved data
            BlockPos allowedInstancePos = data.getPosOfTown("cobbleregionskanto:pewtercity/pewtercity").getWorldPosition();

            if (allowedInstancePos != null) {
                // 3. Find the holder reference for the structure to pack into the pair
                Holder<Structure> structureHolder = structureSet.stream().findFirst().orElse(null);

                if (structureHolder != null) {
                    System.out.println("Redirecting /locate directly to the valid instance at " + allowedInstancePos);

                    // 4. Instantly return the coordinates, completely bypassing the native search logic
                    cir.setReturnValue(Pair.of(allowedInstancePos, structureHolder));
                }
            }
        }
    }
}
