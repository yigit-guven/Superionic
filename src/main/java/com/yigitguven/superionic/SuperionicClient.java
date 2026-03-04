package com.yigitguven.superionic;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperionicClient implements ClientModInitializer {
	static {
		System.out.println("[SUPERIONIC-STATIC] SuperionicClient class loaded!");
	}
	public static final String MOD_ID = "superionic";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Superionic client...");
		SuperionicConfig.load();

		// Runtime Reflection Probe to find the real method names
		try {
			System.out.println("[SUPERIONIC-PROBE] Searching for classes...");
			Class<?> rtClass = Class.forName("net.minecraft.client.renderer.RenderType");
			System.out.println("[SUPERIONIC-PROBE] Methods for RenderType:");
			for (java.lang.reflect.Method m : rtClass.getDeclaredMethods()) {
				System.out.println("  " + m.getName() + " " + m.toString());
			}
			
			Class<?> erdClass = Class.forName("net.minecraft.client.renderer.entity.EntityRenderDispatcher");
			System.out.println("[SUPERIONIC-PROBE] Methods for EntityRenderDispatcher:");
			for (java.lang.reflect.Method m : erdClass.getDeclaredMethods()) {
				System.out.println("  " + m.getName() + " " + m.toString());
			}
		} catch (Exception e) {
			System.out.println("[SUPERIONIC-PROBE] FAILED: " + e.getMessage());
		}
		
		net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
			BenchmarkSystem.tick();
		});
		
		LOGGER.info("Superionic (Client Only) Initialized!");
	}
}