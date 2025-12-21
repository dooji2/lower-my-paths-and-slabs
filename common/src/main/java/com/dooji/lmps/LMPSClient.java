package com.dooji.lmps;

import com.dooji.lmps.networking.ClientPayloadRegistrar;
import com.dooji.lmps.networking.payloads.OffsetOverridesPayload;
import com.dooji.lmps.networking.payloads.OffsetTogglePayload;
import com.dooji.lmps.path.OffsetClientState;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public final class LMPSClient {
    private static final Logger LOGGER = LMPS.LOGGER;

    private LMPSClient() {
    }

    public static void onInitializeClient(ClientPayloadRegistrar networking) {
        networking.register(OffsetOverridesPayload.TYPE, OffsetOverridesPayload.STREAM_CODEC, payload -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level == null) {
                return;
            }

            OffsetClientState.replace(minecraft.level, payload.overrides());
            LOGGER.info("Received {} offset overrides from server", payload.overrides().size());
        });

        networking.register(OffsetTogglePayload.TYPE, OffsetTogglePayload.STREAM_CODEC, payload -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level == null) {
                return;
            }

            BlockPos position = payload.position();
            OffsetClientState.applyToggle(minecraft.level, position, payload.override());
            BlockState state = minecraft.level.getBlockState(position);
            minecraft.level.sendBlockUpdated(position, state, state, 3);
            minecraft.level.setBlocksDirty(position, state, state);

            BlockPos abovePos = position.above();
            BlockState aboveState = minecraft.level.getBlockState(abovePos);
            minecraft.level.sendBlockUpdated(abovePos, aboveState, aboveState, 3);
            minecraft.level.setBlocksDirty(abovePos, aboveState, aboveState);
        });
    }
}
