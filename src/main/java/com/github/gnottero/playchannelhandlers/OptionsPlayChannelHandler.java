package com.github.gnottero.playchannelhandlers;

import carpet.script.value.Value;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import org.slf4j.Logger;

import java.util.function.BiConsumer;

import static com.github.gnottero.utils.ClientOptionsManager.OPTIONS;

public class OptionsPlayChannelHandler implements ClientPlayNetworking.PlayChannelHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ResourceLocation option = buf.readResourceLocation();
        var value = OPTIONS.get(option);
        if(value != null)
            value.getA().accept(client, buf);
        else
            LOGGER.warn("Failed to change option: {}", option);

    }
}
