package com.dooji.lmps.mixin;

import com.dooji.lmps.path.PathSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateShapeMixin {
    @Inject(method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"), cancellable = true)
    private void lowerOutline(BlockGetter level, BlockPos position, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {
        if (PathSupport.isShapeGuardActive()) {
            return;
        }

        if (collisionContext == CollisionContext.empty()) {
            return;
        }

        PathSupport.LoweringOffsets offsets = PathSupport.loweringOffsets(level, position);
        if (offsets != null && offsets.renderOffset() > 0.0) {
            cir.setReturnValue(cir.getReturnValue().move(0.0, -offsets.renderOffset(), 0.0));
        }
    }

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"), cancellable = true)
    private void lowerCollision(BlockGetter level, BlockPos position, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {
        if (PathSupport.isShapeGuardActive()) {
            return;
        }

        PathSupport.LoweringOffsets offsets = PathSupport.loweringOffsets(level, position);
        if (offsets != null && offsets.collisionOffset() > 0.0) {
            VoxelShape original = cir.getReturnValue();
            if (original.isEmpty()) {
                return;
            }

            cir.setReturnValue(original.move(0.0, -offsets.collisionOffset(), 0.0));
        }
    }

    @Inject(method = "getVisualShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"), cancellable = true)
    private void lowerVisual(BlockGetter level, BlockPos position, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {
        if (PathSupport.isShapeGuardActive()) {
            return;
        }

        PathSupport.LoweringOffsets offsets = PathSupport.loweringOffsets(level, position);
        if (offsets != null && offsets.renderOffset() > 0.0) {
            cir.setReturnValue(cir.getReturnValue().move(0.0, -offsets.renderOffset(), 0.0));
        }
    }
}
