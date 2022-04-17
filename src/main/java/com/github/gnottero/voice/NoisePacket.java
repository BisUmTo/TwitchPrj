package com.github.gnottero.voice;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import static com.github.gnottero.voice.Microphone.LAST_PEAK;

public class NoisePacket implements Runnable {
    Identifier identifier = new Identifier("carpet", "noise");
    @Override
    public void run() {
        try {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeDouble(LAST_PEAK);
            ClientPlayNetworking.send(identifier, buf);
        } catch (Exception ignored){}
    }
}
