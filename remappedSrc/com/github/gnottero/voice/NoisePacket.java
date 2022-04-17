package com.github.gnottero.voice;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import static com.github.gnottero.voice.Microphone.LAST_PEAK;

public class NoisePacket implements Runnable {
    ResourceLocation identifier = new ResourceLocation("carpet", "noise");
    @Override
    public void run() {
        try {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeDouble(LAST_PEAK);
            ClientPlayNetworking.send(identifier, buf);
        } catch (Exception ignored){}
    }
}
