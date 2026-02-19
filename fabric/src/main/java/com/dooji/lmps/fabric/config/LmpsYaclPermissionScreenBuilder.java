package com.dooji.lmps.fabric.config;

import com.dooji.lmps.LMPSClient;
import com.dooji.lmps.networking.payloads.OffsetSupportsUpdatePayload;
import com.dooji.lmps.networking.payloads.PermissionLevelUpdatePayload;
import com.dooji.lmps.path.OffsetSupports;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class LmpsYaclPermissionScreenBuilder {
    public static Screen build(Screen parent) {
        int currentLevel = LMPSClient.requiredPermissionLevel();
        List<String> currentSupports = OffsetSupports.currentEntries();
        AtomicInteger pendingLevel = new AtomicInteger(currentLevel);
        List<String> pendingSupports = new ArrayList<>(currentSupports);

        return YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("text.lmps.config.title"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("text.lmps.config.category.general"))
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("text.lmps.config.group.permissions"))
                    .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("text.lmps.config.required_permission_level"))
                        .description(OptionDescription.of(Component.translatable("text.lmps.config.required_permission_level.description")))
                        .binding(currentLevel, pendingLevel::get, pendingLevel::set)
                        .controller(option -> IntegerSliderControllerBuilder.create(option)
                            .range(0, 4)
                            .step(1)
                            .formatValue(value -> Component.literal(Integer.toString(value))))
                        .build())
                    .build())
                .group(ListOption.<String>createBuilder()
                    .name(Component.translatable("text.lmps.config.supports_entries"))
                    .description(OptionDescription.of(Component.translatable("text.lmps.config.supports_entries.description")))
                    .binding(currentSupports, () -> pendingSupports, value -> {
                        pendingSupports.clear();
                        pendingSupports.addAll(value);
                    })
                    .controller(StringControllerBuilder::create)
                    .initial("")
                    .build())
                .build())
            .save(() -> {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.player == null || minecraft.getConnection() == null) {
                    OffsetSupports.setEntries(pendingSupports);
                    return;
                }

                ClientPlayNetworking.send(new PermissionLevelUpdatePayload(pendingLevel.get()));
                LMPSClient.setRequiredPermissionLevel(pendingLevel.get());
                ClientPlayNetworking.send(new OffsetSupportsUpdatePayload(pendingSupports));
                OffsetSupports.applyFromNetwork(pendingSupports);
            })
            .build()
            .generateScreen(parent);
    }
}
