package me.ultimate.cannondebugfabric;

import net.fabricmc.api.ModInitializer;
import org.originmc.cannondebug.CannonDebugPlugin;


public class CannonDebugFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        new CannonDebugPlugin().init();
    }
}
