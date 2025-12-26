package com.dooji.lmps.mixin.client;

import com.dooji.lmps.path.PathSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityBoundingBoxMixin {
    @Inject(method = "getBoundingBox()Lnet/minecraft/world/phys/AABB;", at = @At("RETURN"), cancellable = true)
    private void lowerItemFrameHitbox(CallbackInfoReturnable<AABB> cir) {
        Entity entity = (Entity) (Object) this;
        if (!(entity instanceof ItemFrame itemFrame)) {
            return;
        }

        Level level = itemFrame.level();
        if (level == null || !level.isClientSide()) {
            return;
        }

        Direction direction = itemFrame.getDirection();
        BlockPos attachedPos = itemFrame.blockPosition().relative(direction.getOpposite());
        PathSupport.LoweringOffsets offsets = PathSupport.loweringOffsets(level, attachedPos);
        if (offsets != null && offsets.renderOffset() > 0.0) {
            AABB box = cir.getReturnValue();
            cir.setReturnValue(box.move(0.0, -offsets.renderOffset(), 0.0));
        }
    }
}
