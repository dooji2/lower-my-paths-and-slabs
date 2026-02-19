package com.dooji.lmps.networking.payloads;

import com.dooji.lmps.LMPS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record PermissionLevelUpdatePayload(int permissionLevel) implements CustomPacketPayload {
    public static final Type<PermissionLevelUpdatePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(LMPS.MOD_ID, "permission_level_update"));

    public static final StreamCodec<FriendlyByteBuf, PermissionLevelUpdatePayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        PermissionLevelUpdatePayload::permissionLevel,
        PermissionLevelUpdatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
