package com.dooji.lmps.mixin.client;

import com.dooji.lmps.render.ILoweringState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockEntityRenderState.class)
public abstract class BlockEntityRenderStateMixin implements ILoweringState {
    @Unique
    private double renderOffset;

    @Override
    public double renderOffset() {
        return this.renderOffset;
    }

    @Override
    public void setRenderOffset(double offset) {
        this.renderOffset = offset;
    }
}
