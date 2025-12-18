package com.dooji.lmps.mixin;

import com.dooji.lmps.registry.LmpsBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DirtPathBlock.class)
public abstract class DirtPathBlockMixin {
    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    private void allowFriendlySupports(BlockState blockState, LevelReader levelReader, BlockPos position, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (levelReader.getBlockState(position.above()).is(LmpsBlockTags.PATH_FRIENDLY_SUPPORTS)) {
            callbackInfoReturnable.setReturnValue(true);
            return;
        }
    }
}
