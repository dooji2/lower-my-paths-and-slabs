package com.dooji.lmps;

import com.dooji.lmps.networking.payloads.PathRulesPayload;
import com.dooji.lmps.registry.PathRules;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.slf4j.Logger;

public class LMPSClient implements ClientModInitializer {
    private static final Logger LOGGER = LMPS.LOGGER;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(PathRulesPayload.TYPE, (payload, clientPlayNetworkingContext) ->
            clientPlayNetworkingContext.client().execute(() -> {
                PathRules.replaceWith(payload.pathRuleResourceLocations());
                LOGGER.info("Received {} path friendly supports from server", payload.pathRuleResourceLocations().size());
            })
        );
    }
}
