package life.whitecloud.whitecat.cats;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.fml.common.Mod;
import life.whitecloud.whitecat.WhiteCat;
import life.whitecloud.whitecat.Config;

@Mod.EventBusSubscriber(modid = WhiteCat.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TPSMonitor {
    private static final int TPS_SAMPLE_INTERVAL = 100;
    private static final int TPS_REPORT_INTERVAL = 1000;
    private static long lastTickTime = 0;
    private static int tickCount = 0;
    private static float tps = 20.0f;
    private static int lowTpsCount = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        long now = System.nanoTime();
        tickCount++;

        if (lastTickTime == 0) {
            lastTickTime = now;
            return;
        }

        if (tickCount % TPS_SAMPLE_INTERVAL == 0) {
            long elapsed = now - lastTickTime;
            float currentTps = (TPS_SAMPLE_INTERVAL * 1000000000.0f) / elapsed;
            tps = (tps * 0.8f) + (currentTps * 0.2f);

            lastTickTime = now;
        }

        if (tickCount % TPS_REPORT_INTERVAL == 0) {
            WhiteCat.LOGGER.info("Current TPS: " + tps);
            if (tps < Config.minTps) {
                lowTpsCount++;
                if (lowTpsCount >= Config.lowTpsThreshold) {
                    WhiteCat.LOGGER.warn("TPS too low for too long. Shutting down server.");
                    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                    server.stopServer();
                }
            } else {
                lowTpsCount = 0;
            }
        }
    }
}