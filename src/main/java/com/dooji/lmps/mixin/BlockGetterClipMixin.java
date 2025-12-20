package com.dooji.lmps.mixin;

import com.dooji.lmps.path.PathSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockGetter.class)
public interface BlockGetterClipMixin {
    @Inject(method = "clip(Lnet/minecraft/world/level/ClipContext;)Lnet/minecraft/world/phys/BlockHitResult;", at = @At("RETURN"), cancellable = true)
    private void lowerClip(ClipContext clipContext, CallbackInfoReturnable<BlockHitResult> cir) {
        BlockGetter level = (BlockGetter) (Object) this;
        BlockHitResult fallback = BlockGetter.traverseBlocks(clipContext.getFrom(), clipContext.getTo(), clipContext, (context, position) -> {
            BlockPos abovePos = position.above();
            PathSupport.LoweringOffsets offsets = PathSupport.loweringOffsets(level, abovePos);
            if (offsets == null || offsets.renderOffset() <= 0.0) {
                return null;
            }

            BlockState aboveState = level.getBlockState(abovePos);
            VoxelShape loweredShape = context.getBlockShape(aboveState, level, abovePos);
            return loweredShape.clip(context.getFrom(), context.getTo(), abovePos);
        }, context -> {
            Vec3 delta = context.getFrom().subtract(context.getTo());
            return BlockHitResult.miss(context.getTo(), Direction.getNearest(delta.x, delta.y, delta.z), BlockPos.containing(context.getTo()));
        });

        BlockHitResult original = cir.getReturnValue();
        BlockHitResult chosen = original;
        if (fallback != null && fallback.getType() != HitResult.Type.MISS) {
            if (chosen == null || chosen.getType() == HitResult.Type.MISS) {
                chosen = fallback;
            } else {
                double fromX = clipContext.getFrom().x();
                double fromY = clipContext.getFrom().y();
                double fromZ = clipContext.getFrom().z();
                double originalDist = chosen.getLocation().distanceToSqr(fromX, fromY, fromZ);
                double fallbackDist = fallback.getLocation().distanceToSqr(fromX, fromY, fromZ);
                if (fallbackDist <= originalDist) {
                    chosen = fallback;
                }
            }
        }

        if (chosen == null || chosen.getType() == HitResult.Type.MISS) {
            return;
        }

        PathSupport.LoweringOffsets offsets = PathSupport.loweringOffsets(level, chosen.getBlockPos());
        if (offsets != null && offsets.renderOffset() > 0.0) {
            Vec3 adjustedLocation = chosen.getLocation().add(0.0, offsets.renderOffset(), 0.0);
            chosen = new BlockHitResult(adjustedLocation, chosen.getDirection(), chosen.getBlockPos(), chosen.isInside());
        }

        cir.setReturnValue(chosen);
    }
}
