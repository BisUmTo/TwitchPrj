package com.github.gnottero;

import com.github.gnottero.playchannelhandlers.OptionsPlayChannelHandler;
import com.github.gnottero.playchannelhandlers.ShadersPlayChannelHandler;
import com.github.gnottero.voice.Microphone;
import com.github.gnottero.voice.NoisePacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TwitchProjectClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Microphone(), 0, 50, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(new NoisePacket(), 25, 50, TimeUnit.MILLISECONDS);

        ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation("purple_carpet", "shaders"),
                new ShadersPlayChannelHandler()
        );

        ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation("purple_carpet", "options"),
                new OptionsPlayChannelHandler()
        );
    }
}
