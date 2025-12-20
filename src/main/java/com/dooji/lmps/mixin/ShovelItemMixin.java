package com.dooji.lmps.mixin;

import com.dooji.lmps.path.OffsetState;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShovelItem.class)
public abstract class ShovelItemMixin {
    @Redirect(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z"))
    private boolean allowFlatteningThrough(BlockState blockState, UseOnContext useOnContext) {
        if (blockState.isAir()) {
            return true;
        }

        return OffsetState.isEnabled(useOnContext.getLevel(), useOnContext.getClickedPos().above());
    }
}
