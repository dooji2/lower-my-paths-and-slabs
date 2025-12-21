package com.dooji.lmps.mixin.client.indigo;

import com.dooji.lmps.path.PathSupport;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRenderInfo.class)
public abstract class BlockRenderInfoMixin {
    @Shadow public BlockAndTintGetter blockView;
    @Shadow public BlockPos blockPos;
    @Shadow private boolean enableCulling;
    @Shadow private int cullCompletionFlags;
    @Shadow private int cullResultFlags;

    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
    private void forceFacesWhenHeightDiffers(Direction side, CallbackInfoReturnable<Boolean> cir) {
        if (side == null || !this.enableCulling) {
            return;
        }

        BlockPos neighborPos = this.blockPos.relative(side);
        double offset = offset(this.blockView, this.blockPos);
        double neighborOffset = offset(this.blockView, neighborPos);
        if (Math.abs(offset - neighborOffset) > 1.0E-6) {
            int mask = 1 << side.get3DDataValue();
            this.cullCompletionFlags |= mask;
            this.cullResultFlags |= mask;
            cir.setReturnValue(true);
        }
    }

    private static double offset(BlockAndTintGetter level, BlockPos pos) {
        PathSupport.LoweringOffsets offsets = PathSupport.loweringOffsets(level, pos);
        return offsets != null ? offsets.renderOffset() : 0.0;
    }
}
