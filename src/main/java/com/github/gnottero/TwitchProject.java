package com.github.gnottero;

import carpet.CarpetServer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TwitchProject implements ModInitializer {

	public static final String Modid = "twitchprj";
	public static final Logger LOGGER = LogManager.getLogger("modid");

	static {
		CarpetServer.manageExtension(new CarpetMain());
	}

	public static final Item AIRDROP = new Item(new Item.Settings().maxCount(1));
	public static final Item POTATO_VILLAGER = new Item(new Item.Settings().maxCount(1));

	@Override
	public void onInitialize() {

		Registry.register(Registry.ITEM, new Identifier(Modid, "airdrop"), AIRDROP);
		Registry.register(Registry.ITEM, new Identifier(Modid, "potato_villager"), POTATO_VILLAGER);

		LOGGER.info("TwitchPrj loaded successfully");
	}
}
