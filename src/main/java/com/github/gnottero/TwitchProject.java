package com.github.gnottero;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.script.CarpetExpression;
import carpet.script.CarpetScriptServer;
import carpet.script.bundled.BundledModule;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;
import net.minecraft.world.event.GameEvent;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

public class TwitchProject implements ModInitializer, CarpetExtension {
	public static final String MODID = "twitchprj";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	@Override
	public void scarpetApi(CarpetExpression expression) {
		TwitchProjectScarpet.apply(expression.getExpr());
	}

	@Override
	public void onGameStarted() {
		CarpetServer.settingsManager.parseSettingsClass(TwitchProjectSettings.class);
		CarpetScriptServer.registerBuiltInScript(TwitchPrjDefaultScript("master_script", false));
	}

	public static void loadScripts(MinecraftServer server) {
		//CarpetServer.scriptServer.addScriptHost(server.getCommandSource(), "master_script", null, true, true, false, null);
	}

	@Override
	public void onServerLoadedWorlds(MinecraftServer server) {
		loadScripts(server);
	}

	@Override
	public void onReload(MinecraftServer server) {
		loadScripts(server);
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

	public static final Item AIRDROP = new Item(new Item.Settings().maxCount(1));
	public static final Item POTATO_VILLAGER = new Item(new Item.Settings().maxCount(1));

	@Override
	public void onInitialize() {
		CarpetServer.manageExtension(this);

		ServerPlayNetworking.registerGlobalReceiver(new Identifier("carpet", "noise"), (server, player, handler, buf, responseSender) -> {
			if(!player.world.isClient) {
				double noise = buf.readDouble();
				if (noise > TwitchProjectSettings.microphoneEmitVibration) {
					player.emitGameEvent(GameEvent.ENTITY_SHAKE);
					player.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 1, 1);
					player.changeGameMode(GameMode.SPECTATOR);
				}
			}
		});

		Registry.register(Registry.ITEM, new Identifier(MODID, "airdrop"), AIRDROP);
		Registry.register(Registry.ITEM, new Identifier(MODID, "potato_villager"), POTATO_VILLAGER);

		LOGGER.info("TwitchPrj loaded successfully");
	}
}
