package com.github.gnottero;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.script.CarpetExpression;
import carpet.script.CarpetScriptServer;
import carpet.script.bundled.BundledModule;
import com.github.gnottero.mixins.GameRenderInvoker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class TwitchProject implements ModInitializer, CarpetExtension {
    public static final String MODID = "twitchprj";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final Item AIRDROP = new Item(new Item.Properties().stacksTo(1));
    public static final Item POTATO_VILLAGER = new Item(new Item.Properties().stacksTo(1));

    public static void loadScripts(MinecraftServer server) {
        //CarpetServer.scriptServer.addScriptHost(server.getCommandSource(), "master_script", null, true, true, false, null);
    }

    private static BundledModule TwitchPrjDefaultScript(String scriptName, boolean isLibrary) {
        BundledModule module = new BundledModule(scriptName.toLowerCase(Locale.ROOT), null, false);
        try {
            module = new BundledModule(scriptName.toLowerCase(Locale.ROOT),
                    IOUtils.toString(
                            Objects.requireNonNull(BundledModule.class.getClassLoader().getResourceAsStream("assets/" + MODID + "/scripts/" + scriptName + (isLibrary ? ".scl" : ".sc"))),
                            StandardCharsets.UTF_8
                    ), isLibrary
            );
        } catch (NullPointerException | IOException ignored) {
        }
        return module;
    }

    @Override
    public void scarpetApi(CarpetExpression expression) {
        TwitchProjectScarpet.apply(expression.getExpr());
    }

    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(TwitchProjectSettings.class);
        CarpetScriptServer.registerBuiltInScript(TwitchPrjDefaultScript("master_script", false));
    }

    @Override
    public void onServerLoadedWorlds(MinecraftServer server) {
        loadScripts(server);
    }

    @Override
    public void onReload(MinecraftServer server) {
        loadScripts(server);
    }

    @Override
    public void onInitialize() {
        CarpetServer.manageExtension(this);

        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation("carpet", "noise"), (server, player, handler, buf, responseSender) -> {
            double noise = buf.readDouble();
            if (!player.level.isClientSide
                    && TwitchProjectSettings.microphoneEmitVibration >= 0
                    && noise > TwitchProjectSettings.microphoneEmitVibration) {
                server.execute(() -> {
                    Vec3 pos = player.position().add(0, player.getEyeHeight(), 0);
                    player.level.gameEvent(player, GameEvent.ENTITY_SHAKE, pos);
                });
            }
            if(TwitchProjectSettings.microphoneNoiseLogger) {
                String message = String.format("Microphone Noise: %.02f", noise);
                TwitchProject.LOGGER.log(Level.DEBUG, message);
                player.displayClientMessage(new TextComponent(message), true);
            }
        });

        Registry.register(Registry.ITEM, new ResourceLocation(MODID, "airdrop"), AIRDROP);
        Registry.register(Registry.ITEM, new ResourceLocation(MODID, "potato_villager"), POTATO_VILLAGER);

        LOGGER.info("TwitchPrj loaded successfully");
    }
}
