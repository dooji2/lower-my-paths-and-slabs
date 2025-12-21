package com.dooji.lmps;

import com.dooji.lmps.permission.LmpsPermissions;
import com.dooji.lmps.registry.LmpsItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LMPS {
    public static final String MOD_ID = "lmps";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private LMPS() {
    }

    public static void onInitialize() {
        LmpsPermissions.load();
    }
}
