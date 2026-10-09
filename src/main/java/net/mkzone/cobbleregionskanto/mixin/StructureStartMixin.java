package net.mkzone.cobbleregionskanto.mixin;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.mkzone.cobbleregionskanto.world.TownsSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(StructureStart.class)
public abstract class StructureStartMixin {

    @Shadow
    public abstract Structure getStructure();

    @Inject(method = "placeInChunk", at = @At("HEAD"), cancellable = true)
    private void onStructureLayoutGenerate(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, CallbackInfo ci) {
        Structure structure = this.getStructure();
        ResourceLocation id = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(structure);

        ServerLevel overworld = level.getServer().overworld();
        TownsSavedData data = TownsSavedData.get(overworld);

        assert id != null;
        if (id.toString().contains("cobbleregionskanto:")) {
            String structurePath = id.toString();
            structurePath = structurePath.substring(structurePath.lastIndexOf('/') + 1);

            //System.out.println("=== SUCCESS: INSTANCE OF " + structurePath.toUpperCase() + " FOUND AND LOGGED! ===");

            StructureStart bridge = (StructureStart) (Object) this;
            String structureKey = id.toString();
            ChunkPos origin = bridge.getChunkPos();

            if (data.getNextTown().equals("null")) {
                if (data.trackTown(structureKey, origin)) {
                    System.out.println("=== SUCCESS: FIRST " + structurePath.toUpperCase() + " INSTANCE FOUND AND LOGGED! ===");
                    System.out.println("Location Origin: " + origin);
                    System.out.println("Next town: " + data.getNextTown().toUpperCase());
                }
            } else if (data.getNextTown().equals(structurePath) || data.getTrackedTowns().containsKey(structureKey)) {
                if (data.trackTown(structureKey, origin)) {
                    System.out.println("=== SUCCESS: FIRST " + structurePath.toUpperCase() + " INSTANCE FOUND AND LOGGED! ===");
                    System.out.println("Location Origin: " + origin);
                    System.out.println("Next town: " + data.getNextTown().toUpperCase());
                    return;
                } else if (data.getTownNeighbors().size() == data.getTrackedTowns().size()) {
                    // Check if number of towns is equal between Neighbor map and Tracked towns
                    data.resetTrackedTowns();
                    data.trackTown(structureKey, origin);
                    System.out.println("=== SUCCESS: CYCLE RESTARTED WITH SECOND " + structurePath.toUpperCase() + " INSTANCE FOUND AND LOGGED! ===");
                    System.out.println("Location Origin: " + origin);
                    System.out.println("Next town: " + data.getNextTown().toUpperCase());
                    return;
                }

                // Check if duplicate town is generating before remaining towns
                ChunkPos savedOrigin = data.getTrackedTowns().get(structureKey);
                if (savedOrigin != null && !origin.equals(savedOrigin)) {
                    System.out.println("=== BLOCKED DUPLICATE SPAWN: " + structurePath.toLowerCase() + " deleted at chunk " + chunkPos + " ===");
                    data.blockTown(origin);
                    ci.cancel();
                }
            } else {
                System.out.println("=== BLOCKED INCORRECT SPAWN: " + structurePath.toLowerCase() + " deleted at chunk " + chunkPos + " ===");
                data.blockTown(origin);
                ci.cancel();
            }
        }

        /*
        if (structurePath != null && id.toString().equals("cobbleregionskanto:pewtercity/pewtercity")) {
            StructureStart bridge = (StructureStart) (Object) this;
            String structureKey = id.toString();
            ChunkPos origin = bridge.getChunkPos();

            if (data.trackTown(structureKey, origin)) {
                System.out.println("=== SUCCESS: FIRST PEWTER CITY INSTANCE FOUND AND LOGGED! ===");
                System.out.println("Location Origin: " + origin);
                return; // Let the initial town generate normally
            }

            ChunkPos savedOrigin = data.getTrackedTowns().get(structureKey);
            if (savedOrigin != null && !origin.equals(savedOrigin)) {
                System.out.println("=== BLOCKED DUPLICATE SPAWN: Secondary city deleted at chunk " + chunkPos + " ===");
                data.blockTown(origin);
                ci.cancel(); // Terminate layout block execution entirely
            }
        }
         */
    }
}
