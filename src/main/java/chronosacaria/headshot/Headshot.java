package chronosacaria.headshot;

import chronosacaria.headshot.config.HeadshotConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(Headshot.MOD_ID)
public class Headshot {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "headshot";

    public Headshot() {
        new HeadshotConfig();

        MinecraftForge.EVENT_BUS.register(this);
    }
}
