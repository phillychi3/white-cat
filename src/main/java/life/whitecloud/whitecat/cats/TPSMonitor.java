package life.whitecloud.whitecat.cats;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.fml.common.Mod;
import life.whitecloud.whitecat.WhiteCat;
import life.whitecloud.whitecat.Config;
import life.whitecloud.whitecat.util;

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
            if (tps < Config.minTps) {
                lowTpsCount++;
                if (lowTpsCount >= Config.lowTpsThreshold) {
                    WhiteCat.LOGGER.warn("TPS too low for too long. Shutting down server.");
                    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                    for (int i = 10; i > 0; i--) {
                        server.getPlayerList().broadcastSystemMessage(util.broadcastPrefixedMessage(
                                Component.literal(
                                        Config.shutdownCountdown.replace("{seconds}", String.valueOf(i)))),
                                false);
                        if (tps > Config.minTps) {
                            server.getPlayerList().broadcastSystemMessage(util.broadcastPrefixedMessage(
                                    Component.literal(Config.shutdownCancelled)), false);
                            lowTpsCount = 0;
                            break;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    server.saveEverything(false, false, false);
                    server.stopServer();
                    System.exit(0);
                }
            } else {
                lowTpsCount = 0;
            }
        }
    }
}