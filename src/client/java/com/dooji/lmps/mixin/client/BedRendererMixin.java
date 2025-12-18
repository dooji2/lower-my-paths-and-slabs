package com.dooji.lmps.mixin.client;

import com.dooji.lmps.path.PathSupport;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BedRenderer.class)
public abstract class BedRendererMixin {
    @Inject(method = "render(Lnet/minecraft/world/level/block/entity/BedBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At("HEAD"))
    private void lowerBed(BedBlockEntity bedBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, CallbackInfo callbackInfo) {
        if (bedBlockEntity.getLevel() == null) {
            return;
        }

        BlockPos position = bedBlockEntity.getBlockPos();
        PathSupport.LoweringOffsets offsets = PathSupport.loweringOffsets(bedBlockEntity.getLevel(), position, 8);
        if (offsets != null && offsets.renderOffset() > 0.0) {
            poseStack.translate(0.0, -offsets.renderOffset(), 0.0);
        }
    }
}
