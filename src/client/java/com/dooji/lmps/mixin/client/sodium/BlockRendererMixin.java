package com.dooji.lmps.mixin.client.sodium;

import com.dooji.lmps.path.PathSupport;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin {
    @Shadow private Vector3f posOffset;
    @Unique private BlockAndTintGetter levelForOffsets;

    @Inject(method = "prepare", at = @At("HEAD"))
    private void storeLevel(ChunkBuildBuffers buffers, LevelSlice levelSlice, TranslucentGeometryCollector collector, CallbackInfo callbackInfo) {
        levelForOffsets = levelSlice;
    }

    @Inject(method = "renderModel", at = @At(value = "INVOKE", target = "Lorg/joml/Vector3f;set(FFF)Lorg/joml/Vector3f;", shift = At.Shift.AFTER))
    private void lowerFriendlyModels(BakedModel bakedModel, BlockState blockState, BlockPos blockPosition, BlockPos modelOffset, CallbackInfo callbackInfo) {
        if (levelForOffsets != null) {
            PathSupport.LoweringOffsets offsets = PathSupport.loweringOffsets(levelForOffsets, blockPosition, 8);
            if (offsets != null && offsets.renderOffset() > 0.0) {
                posOffset.set(posOffset.x(), (float) (posOffset.y() - offsets.renderOffset()), posOffset.z());
            }
        }
    }

    @Unique
    private boolean shouldLower(BlockAndTintGetter level, BlockPos position, int remainingChecks) {
        if (remainingChecks <= 0) {
            return false;
        }

        PathSupport.LoweringOffsets offsets = PathSupport.loweringOffsets(level, position, 1);
        if (offsets != null && offsets.renderOffset() > 0.0) {
            return true;
        }

        return shouldLower(level, position.below(), remainingChecks - 1);
    }
}
