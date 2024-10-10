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

        private static final ForgeConfigSpec.ConfigValue<String> SHUTDOWN_REMIND = BUILDER
                        .comment("Message to remind players of the shutdown")
                        .define("shutdownremind", "Server will restart in 1 minute");

        private static final ForgeConfigSpec.BooleanValue AUTORESTART = BUILDER
                        .comment("on/off autorestart")
                        .define("autorestart", false);

        private static final ForgeConfigSpec.ConfigValue<String> PREFIX = BUILDER
                        .comment("Prefix")
                        .define("prefix", "[白貓]");

        private static final ForgeConfigSpec.ConfigValue<String> VOTE_STARTED = BUILDER
                        .comment("Message when a vote is started")
                        .define("voteStarted", "A vote to restart the server! Click");

        private static final ForgeConfigSpec.ConfigValue<String> VOTE_YES = BUILDER
                        .comment("Text for voting yes")
                        .define("voteYes", "YES");

        private static final ForgeConfigSpec.ConfigValue<String> VOTE_NO = BUILDER
                        .comment("Text for voting no")
                        .define("voteNo", "NO");

        private static final ForgeConfigSpec.ConfigValue<String> PLAYER_VOTED = BUILDER
                        .comment("Message when a player votes")
                        .define("playerVoted", "{player} has voted {vote}.");

        private static final ForgeConfigSpec.ConfigValue<String> VOTE_PASSED = BUILDER
                        .comment("Message when the vote passes")
                        .define("votePassed", "Vote passed. Server shutting down in {seconds} seconds.");

        private static final ForgeConfigSpec.ConfigValue<String> VOTE_FAILED = BUILDER
                        .comment("Message when the vote fails")
                        .define("voteFailed", "Vote failed. The server will not shut down.");

        private static final ForgeConfigSpec.ConfigValue<String> SHUTDOWN_COUNTDOWN = BUILDER
                        .comment("Message for shutdown countdown")
                        .define("shutdownCountdown", "Server shutting down in {seconds} seconds.");

        private static final ForgeConfigSpec.ConfigValue<String> SHUTDOWN_CANCELLED = BUILDER
                        .comment("Message when shutdown is cancelled")
                        .define("shutdownCancelled", "tps is back to normal. Shutdown cancelled.");

        static final ForgeConfigSpec SPEC = BUILDER.build();

        public static double minTps;
        public static int lowTpsThreshold;
        public static boolean autorestart;
        public static String shutdownTime;
        public static String shutdownremind;
        public static String prefix;
        public static String voteStarted;
        public static String voteYes;
        public static String voteNo;
        public static String playerVoted;
        public static String votePassed;
        public static String voteFailed;
        public static String shutdownCountdown;
        public static String shutdownCancelled;

        @SubscribeEvent
        static void onLoad(final ModConfigEvent event) {
                minTps = MIN_TPS.get();
                lowTpsThreshold = LOW_TPS_THRESHOLD.get();
                autorestart = AUTORESTART.get();
                shutdownTime = SHUTDOWN_TIME.get();
                shutdownremind = SHUTDOWN_REMIND.get();
                prefix = PREFIX.get();
                voteStarted = VOTE_STARTED.get();
                voteYes = VOTE_YES.get();
                voteNo = VOTE_NO.get();
                playerVoted = PLAYER_VOTED.get();
                votePassed = VOTE_PASSED.get();
                voteFailed = VOTE_FAILED.get();
                shutdownCountdown = SHUTDOWN_COUNTDOWN.get();
                shutdownCancelled = SHUTDOWN_CANCELLED.get();
        }
}
