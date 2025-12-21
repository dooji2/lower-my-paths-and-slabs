package com.dooji.lmps.item;

import com.dooji.lmps.networking.LmpsNetworking;
import com.dooji.lmps.path.OffsetSavedData;
import com.dooji.lmps.path.OffsetState;
import com.dooji.lmps.path.MultipartHelper;
import com.dooji.lmps.permission.LmpsPermissions;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.List;

public class OffsetToolItem extends Item {
    public OffsetToolItem(ResourceKey<Item> key, Properties properties) {
        super(properties.setId(key));
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        if (useOnContext.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel serverLevel = (ServerLevel) useOnContext.getLevel();
        ServerPlayer serverPlayer = (ServerPlayer) useOnContext.getPlayer();
        if (serverPlayer == null || !LmpsPermissions.canToggle(serverPlayer)) {
            if (serverPlayer != null) {
                serverPlayer.displayClientMessage(Component.translatable("text.lmps.offset_tool.no_permission"), true);
            }

            return InteractionResult.FAIL;
        }

        BlockPos position = useOnContext.getClickedPos();
        BlockState blockState = serverLevel.getBlockState(position);
        List<BlockPos> positions = new ArrayList<>();

        positions.add(position);
        collectLinkedPositions(serverLevel, blockState, position, positions);

        OffsetSavedData.ToggleResult toggleResult = OffsetState.toggle(serverLevel, position);
        LmpsNetworking.broadcastToggle(serverLevel, position, toggleResult.override());
        applyUpdates(serverLevel, position);

        for (BlockPos linkedPos : positions) {
            if (linkedPos.equals(position)) {
                continue;
            }

            Boolean override = OffsetState.set(serverLevel, linkedPos, toggleResult.enabled());
            LmpsNetworking.broadcastToggle(serverLevel, linkedPos, override);
            applyUpdates(serverLevel, linkedPos);
        }

        useOnContext.getPlayer().displayClientMessage(Component.translatable(toggleResult.enabled() ? "text.lmps.offset_tool.enabled" : "text.lmps.offset_tool.disabled"), true);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    private static void collectLinkedPositions(ServerLevel level, BlockState blockState, BlockPos position, List<BlockPos> positions) {
        positions.addAll(MultipartHelper.collectLinked(level, position));
    }

    private static void applyUpdates(ServerLevel level, BlockPos position) {
        BlockState state = level.getBlockState(position);
        level.sendBlockUpdated(position, state, state, 3);
        level.updateNeighborsAt(position, state.getBlock());

        BlockPos abovePos = position.above();
        BlockState aboveState = level.getBlockState(abovePos);
        level.sendBlockUpdated(abovePos, aboveState, aboveState, 3);
        level.updateNeighborsAt(abovePos, aboveState.getBlock());
    }
}
