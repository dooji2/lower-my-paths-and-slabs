package com.dooji.lmps.networking.payloads;

import com.dooji.lmps.LMPS;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OffsetTogglePayload(BlockPos position, byte overrideState) implements CustomPacketPayload {
    public static final byte CLEAR_OVERRIDE = 0;
    public static final byte ENABLED_OVERRIDE = 1;
    public static final byte DISABLED_OVERRIDE = 2;

    public static final Type<OffsetTogglePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LMPS.MOD_ID, "offset_toggle"));

    public static final StreamCodec<FriendlyByteBuf, OffsetTogglePayload> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        OffsetTogglePayload::position,
        ByteBufCodecs.BYTE,
        OffsetTogglePayload::overrideState,
        OffsetTogglePayload::new
    );

    public static OffsetTogglePayload of(BlockPos position, Boolean override) {
        byte overrideState = CLEAR_OVERRIDE;
        if (override != null) {
            overrideState = override ? ENABLED_OVERRIDE : DISABLED_OVERRIDE;
        }

        return new OffsetTogglePayload(position, overrideState);
    }

    public Boolean override() {
        if (overrideState == CLEAR_OVERRIDE) {
            return null;
        }

        return overrideState == ENABLED_OVERRIDE;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
