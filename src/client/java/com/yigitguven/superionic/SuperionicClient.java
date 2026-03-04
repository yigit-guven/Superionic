package com.yigitguven.superionic;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperionicClient implements ClientModInitializer {
	public static final String MOD_ID = "superionic";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Superionic client...");
		SuperionicConfig.load();
		
		net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
			BenchmarkSystem.tick();
		});
		
		LOGGER.info("Superionic (Client Only) Initialized!");
	}
}