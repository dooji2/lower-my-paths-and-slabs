package com.dooji.lmps;

import com.dooji.lmps.networking.LmpsNetworking;
import com.dooji.lmps.path.OffsetSupports;
import com.dooji.lmps.permission.LmpsPermissions;
import com.dooji.lmps.registry.LmpsItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LMPS implements ModInitializer {
    public static final String MOD_ID = "lmps";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        OffsetSupports.load();
        LmpsPermissions.load();
        LmpsNetworking.register();

        LmpsItems.register();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            LmpsNetworking.sendSupports(handler.player);
            LmpsNetworking.sendSnapshot(handler.player);
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((serverPlayer, serverLevel, serverLevel1) -> {
            LmpsNetworking.sendSupports(serverPlayer);
            LmpsNetworking.sendSnapshot(serverPlayer);
        });

        UseBlockCallback.EVENT.register((player, level, hand, blockHitResult) -> {
            if (!player.getItemInHand(hand).is(LmpsItems.OFFSET_TOOL)) {
                return InteractionResult.PASS;
            }

            return LmpsItems.OFFSET_TOOL.useOn(new UseOnContext(player, hand, blockHitResult));
        });
    }
}
