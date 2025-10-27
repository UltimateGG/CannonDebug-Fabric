package me.ultimate.cannondebugfabric;

import org.originmc.cannondebug.CannonDebugPlugin;
import net.fabricmc.api.ModInitializer;


public class CannonDebugFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		new CannonDebugPlugin().init();
	}
}
