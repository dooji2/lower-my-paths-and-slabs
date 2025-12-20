package com.dooji.lmps;

import com.dooji.lmps.networking.payloads.OffsetOverridesPayload;
import com.dooji.lmps.networking.payloads.OffsetTogglePayload;
import com.dooji.lmps.path.OffsetClientState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class LMPSClient implements ClientModInitializer {
    private static final Logger LOGGER = LMPS.LOGGER;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(OffsetOverridesPayload.TYPE, (payload, clientPlayNetworkingContext) ->
            clientPlayNetworkingContext.client().execute(() -> {
                if (clientPlayNetworkingContext.client().level == null) {
                    return;
                }

                OffsetClientState.replace(clientPlayNetworkingContext.client().level, payload.overrides());
                LOGGER.info("Received {} offset overrides from server", payload.overrides().size());
            })
        );

        ClientPlayNetworking.registerGlobalReceiver(OffsetTogglePayload.TYPE, (payload, clientPlayNetworkingContext) ->
            clientPlayNetworkingContext.client().execute(() -> {
                if (clientPlayNetworkingContext.client().level == null) {
                    return;
                }

                BlockPos position = payload.position();
                OffsetClientState.applyToggle(clientPlayNetworkingContext.client().level, position, payload.override());
                BlockState state = clientPlayNetworkingContext.client().level.getBlockState(position);
                clientPlayNetworkingContext.client().level.sendBlockUpdated(position, state, state, 3);
                clientPlayNetworkingContext.client().level.setBlocksDirty(position, state, state);

                BlockPos abovePos = position.above();
                BlockState aboveState = clientPlayNetworkingContext.client().level.getBlockState(abovePos);
                clientPlayNetworkingContext.client().level.sendBlockUpdated(abovePos, aboveState, aboveState, 3);
                clientPlayNetworkingContext.client().level.setBlocksDirty(abovePos, aboveState, aboveState);
            })
        );
    }
}
