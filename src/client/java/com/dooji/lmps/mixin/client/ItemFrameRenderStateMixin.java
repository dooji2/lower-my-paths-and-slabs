package com.dooji.lmps.mixin.client;

import com.dooji.lmps.render.ILoweringState;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemFrameRenderState.class)
public abstract class ItemFrameRenderStateMixin implements ILoweringState {
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
