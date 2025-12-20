package com.dooji.lmps.mixin.client;

import com.dooji.lmps.path.PathSupport;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin {
    @Inject(method = "render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V", at = @At("HEAD"))
    private <T extends BlockEntity> void lowerAll(T blockEntity, float tickDelta, PoseStack poseStack, MultiBufferSource buffers, CallbackInfo callbackInfo) {
        if (blockEntity.getLevel() == null) {
            return;
        }

        PathSupport.LoweringOffsets offsets = PathSupport.loweringOffsets(blockEntity.getLevel(), blockEntity.getBlockPos());
        if (offsets != null && offsets.renderOffset() > 0.0) {
            poseStack.translate(0.0, -offsets.renderOffset(), 0.0);
        }
    }
}
