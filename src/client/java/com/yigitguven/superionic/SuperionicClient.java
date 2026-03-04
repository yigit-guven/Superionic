package com.yigitguven.superionic;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperionicClient implements ClientModInitializer {
	public static final String MOD_ID = "superionic";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		SuperionicConfig.load();
		LOGGER.info("Superionic (Client Only) Initialized!");
	}
}