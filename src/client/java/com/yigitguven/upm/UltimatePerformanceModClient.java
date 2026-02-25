package com.yigitguven.upm;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UltimatePerformanceModClient implements ClientModInitializer {
	public static final String MOD_ID = "upm";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		UltimatePerformanceModConfig.load();
		LOGGER.info("Ultimate Performance Mod (Client Only) Initialized!");
	}
}