package com.dooji.lmps.mixin.client;

import com.dooji.lmps.path.PathSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateOffsetMixin {
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

    @Inject(method = "getOffset(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"), cancellable = true)
    private void lowerFriendlyBlocks(BlockGetter level, BlockPos position, CallbackInfoReturnable<Vec3> callbackInfoReturnable) {
        PathSupport.LoweringOffsets offsets = PathSupport.loweringOffsets(level, position, 8);
        if (offsets != null && offsets.renderOffset() > 0.0) {
            Vec3 originalOffset = callbackInfoReturnable.getReturnValue();
            callbackInfoReturnable.setReturnValue(originalOffset.add(0.0, -offsets.renderOffset(), 0.0));
        }
    }
}
