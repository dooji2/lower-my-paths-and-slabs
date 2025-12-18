package com.dooji.lmps.mixin.client;

import com.dooji.lmps.path.PathSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockShouldRenderFaceMixin {
    @Unique
    private static boolean shouldLower(BlockGetter level, BlockPos position, int remainingChecks) {
        if (remainingChecks <= 0) {
            return false;
        }

        PathSupport.LoweringOffsets offsets = PathSupport.loweringOffsets(level, position, 1);
        if (offsets != null && offsets.renderOffset() > 0.0) {
            return true;
        }

        return shouldLower(level, position.below(), remainingChecks - 1);
    }

    @Inject(method = "shouldRenderFace(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
    private static void forceFacesWhenHeightDiffers(BlockState blockState, BlockGetter level, BlockPos position, Direction direction, BlockPos neighborPosition, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        boolean isLowered = shouldLower(level, position, 8);
        boolean isNeighborLowered = shouldLower(level, neighborPosition, 8);
        if (isLowered != isNeighborLowered) {
            callbackInfoReturnable.setReturnValue(true);
        }
    }
}
