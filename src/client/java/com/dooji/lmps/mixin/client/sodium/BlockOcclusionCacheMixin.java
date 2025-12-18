package com.dooji.lmps.mixin.client.sodium;

import com.dooji.lmps.path.PathSupport;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockOcclusionCache.class)
public abstract class BlockOcclusionCacheMixin {
    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
    private void forceFacesWhenLowered(BlockState state, BlockGetter level, BlockPos position, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        boolean isLowered = shouldLower(level, position, 8);
        boolean neighborLowered = shouldLower(level, position.relative(direction), 8);
        if (isLowered != neighborLowered) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private boolean shouldLower(BlockGetter level, BlockPos position, int remainingChecks) {
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
