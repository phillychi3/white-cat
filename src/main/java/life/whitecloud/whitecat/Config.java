package life.whitecloud.whitecat;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = WhiteCat.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
        private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        private static final ForgeConfigSpec.DoubleValue MIN_TPS = BUILDER
                        .comment("Minimum TPS before considering server shutdown")
                        .defineInRange("minTps", 15.0, 0.0, 20.0);

        private static final ForgeConfigSpec.IntValue LOW_TPS_THRESHOLD = BUILDER
                        .comment("Number of consecutive low TPS reports before shutting down")
                        .defineInRange("lowTpsThreshold", 5, 1, Integer.MAX_VALUE);

        private static final ForgeConfigSpec.ConfigValue<String> SHUTDOWN_TIME = BUILDER
                        .comment("Daily server shutdown time (HH:mm)")
                        .define("shutdownTime", "04:00");

        static final ForgeConfigSpec SPEC = BUILDER.build();

        public static double minTps;
        public static int lowTpsThreshold;
        public static String shutdownTime;

        @SubscribeEvent
        static void onLoad(final ModConfigEvent event) {
                minTps = MIN_TPS.get();
                lowTpsThreshold = LOW_TPS_THRESHOLD.get();
                shutdownTime = SHUTDOWN_TIME.get();
        }
}
