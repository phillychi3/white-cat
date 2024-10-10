package life.whitecloud.whitecat.cats;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import life.whitecloud.whitecat.WhiteCat;
import life.whitecloud.whitecat.Config;

@Mod.EventBusSubscriber(modid = WhiteCat.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ScheduledShutdown {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        LocalTime now = LocalTime.now();
        LocalTime shutdownTime = LocalTime.parse(Config.shutdownTime, TIME_FORMATTER);

        if (now.equals(shutdownTime)) {
            WhiteCat.LOGGER.info("Scheduled shutdown time reached. Shutting down server.");
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            server.saveEverything(false, false, false);
            server.stopServer();
            System.exit(0);
        }
    }
}