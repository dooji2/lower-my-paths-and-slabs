package com.dooji.lmps.mixin.client;

import com.dooji.lmps.path.PathSupport;
import com.dooji.lmps.render.ILoweringState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrameRenderer.class)
public abstract class ItemFrameRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/decoration/ItemFrame;Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;F)V", at = @At("RETURN"))
    private void captureLowering(ItemFrame itemFrame, ItemFrameRenderState state, float tickDelta, CallbackInfo callbackInfo) {
        if (!(state instanceof ILoweringState loweringState)) {
            return;
        }

        Level level = itemFrame.level();
        if (level == null) {
            loweringState.setRenderOffset(0.0);
            return;
        }

        Direction direction = itemFrame.getDirection();
        BlockPos attachedPos = itemFrame.blockPosition().relative(direction.getOpposite());
        PathSupport.LoweringOffsets offsets = PathSupport.loweringOffsets(level, attachedPos);
        double renderOffset = offsets != null ? offsets.renderOffset() : 0.0;
        loweringState.setRenderOffset(Math.max(renderOffset, 0.0));
    }

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V", at = @At("HEAD"))
    private void lowerItemFrame(ItemFrameRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState, CallbackInfo callbackInfo) {
        if (state instanceof ILoweringState loweringState && loweringState.renderOffset() > 0.0) {
            poseStack.translate(0.0, -loweringState.renderOffset(), 0.0);
        }
    }
}
