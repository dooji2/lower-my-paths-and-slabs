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

@Mixin(value = BlockOcclusionCache.class, remap = false)
public abstract class BlockOcclusionCacheMixin {
    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
    private void forceFacesWhenLowered(BlockState state, BlockGetter level, BlockPos position, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        boolean lowered = isLowered(level, position);
        boolean neighborLowered = isLowered(level, position.relative(direction));
        if (lowered != neighborLowered) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private boolean isLowered(BlockGetter level, BlockPos position) {
        PathSupport.LoweringOffsets offsets = PathSupport.loweringOffsets(level, position);
        return offsets != null && offsets.renderOffset() > 0.0;
    }
}
