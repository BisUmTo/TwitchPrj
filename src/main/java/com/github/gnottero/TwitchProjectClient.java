package com.github.gnottero;

import com.github.gnottero.mixins.GameRenderInvoker;
import com.github.gnottero.voice.Microphone;
import com.github.gnottero.voice.NoisePacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TwitchProjectClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Microphone(), 0, 50, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(new NoisePacket(), 25, 50, TimeUnit.MILLISECONDS);

        ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation("carpet", "shaders"),
                (client, handler, buf, responseSender) -> {
                    ResourceLocation shaderResLoc = buf.readResourceLocation();
                    client.execute(
                            () -> {
                                try {
                                    ((GameRenderInvoker) client.gameRenderer).invokeLoadEffect(shaderResLoc);
                                } catch (Exception e)
                                {
                                    // Invoker not working FIX
                                    GameRenderer gr = client.gameRenderer;
                                    Class<?> c = gr.getClass();
                                    Optional<Method> m = Arrays.stream(c.getDeclaredMethods())
                                            .filter(m1 -> m1.getParameterCount() == 1)
                                            .filter(m1 -> m1.getParameterTypes()[0] == ResourceLocation.class)
                                            .findFirst();
                                    if(m.isPresent()){
                                        m.get().setAccessible(true);
                                        try {
                                            m.get().invoke(gr, shaderResLoc);
                                        } catch (IllegalAccessException | InvocationTargetException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                            }
                    );
                }
        );
    }
}
