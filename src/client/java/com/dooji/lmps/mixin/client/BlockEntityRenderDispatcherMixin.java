package com.dooji.lmps.mixin.client;

import com.dooji.lmps.path.PathSupport;
import com.mojang.blaze3d.vertex.PoseStack;
import com.dooji.lmps.render.ILoweringState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin {
    @Inject(method = "submit(Lnet/minecraft/client/renderer/blockentity/state/BlockEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V", at = @At("HEAD"))
    private <S extends BlockEntityRenderState> void lowerAll(S state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState, CallbackInfo callbackInfo) {
        if (state instanceof ILoweringState loweringState && loweringState.renderOffset() > 0.0) {
            poseStack.translate(0.0, -loweringState.renderOffset(), 0.0);
        }
    }

    @Inject(method = "tryExtractRenderState(Lnet/minecraft/world/level/block/entity/BlockEntity;FLnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)Lnet/minecraft/client/renderer/blockentity/state/BlockEntityRenderState;", at = @At("RETURN"))
    private <E extends BlockEntity, S extends BlockEntityRenderState> void captureLowering(E blockEntity, float tickDelta, ModelFeatureRenderer.CrumblingOverlay overlay, CallbackInfoReturnable<S> cir) {
        S state = cir.getReturnValue();
        if (state instanceof ILoweringState loweringState && blockEntity.getLevel() != null) {
            PathSupport.LoweringOffsets offsets = PathSupport.loweringOffsets(blockEntity.getLevel(), blockEntity.getBlockPos());
            double renderOffset = offsets != null ? offsets.renderOffset() : 0.0;
            loweringState.setRenderOffset(Math.max(renderOffset, 0.0));
        }
    }
}
