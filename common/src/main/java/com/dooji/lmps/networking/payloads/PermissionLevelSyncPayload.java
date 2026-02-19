package com.dooji.lmps.networking.payloads;

import com.dooji.lmps.LMPS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PermissionLevelSyncPayload(int permissionLevel) implements CustomPacketPayload {
    public static final Type<PermissionLevelSyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LMPS.MOD_ID, "permission_level_sync"));

    public static final StreamCodec<FriendlyByteBuf, PermissionLevelSyncPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        PermissionLevelSyncPayload::permissionLevel,
        PermissionLevelSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
