package life.whitecloud.whitecat;

import com.mojang.logging.LogUtils;

import life.whitecloud.whitecat.cats.ScheduledShutdown;
import life.whitecloud.whitecat.cats.TPSMonitor;
import life.whitecloud.whitecat.cats.VoteShutdown;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(WhiteCat.MODID)
public class WhiteCat {
    public static final String MODID = "whitecat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WhiteCat(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(TPSMonitor.class);
        MinecraftForge.EVENT_BUS.register(ScheduledShutdown.class);
        MinecraftForge.EVENT_BUS.register(VoteShutdown.class);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the
        // config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Successfully loaded White Cat!");
    }
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("White Cat is starting up!");
    }
}
