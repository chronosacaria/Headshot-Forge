package chronosacaria.headshot.config;

import chronosacaria.headshot.Headshot;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;

public class HeadshotConfig {
    private static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec.DoubleValue HEADSHOT_DAMAGE_MULTIPLIER;
    public static ForgeConfigSpec.DoubleValue HEADSHOT_PROJECTILE_PROTECTION_DAMAGE_REDUCTION;
    public static ForgeConfigSpec.ConfigValue<Boolean> DO_BLINDNESS;
    public static ForgeConfigSpec.ConfigValue<Boolean> DO_NAUSEA;
    public static ForgeConfigSpec.ConfigValue<Integer> BLIND_TICKS;
    public static ForgeConfigSpec.ConfigValue<Integer> NAUSEA_TICKS;
    public static ForgeConfigSpec.ConfigValue<Boolean> HELMET_MITIGATION;

    public HeadshotConfig(){
        CommentedFileConfig cfg = CommentedFileConfig
                .builder(new File(FMLPaths.CONFIGDIR.get().toString(), Headshot.MOD_ID + "-common.toml")).sync()
                .autosave().build();
        cfg.load();
        initConfig();
        ForgeConfigSpec spec = builder.build();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, spec, cfg.getFile().getName());
        spec.setConfig(cfg);
    }

    private void initConfig(){
        builder.comment("Headshot Mod Configuration").push("headshot_mod_configuration");
        HEADSHOT_DAMAGE_MULTIPLIER = builder
                .comment("Choose the damage multiplier done on a headshot. [default 1.5]")
                .defineInRange("damageMultiplier", 1.5, 1.0, 999.0);
        HEADSHOT_PROJECTILE_PROTECTION_DAMAGE_REDUCTION = builder
                .comment("Choose the percentage of damage reduction for Projectile Protection from a headshot. " +
                        "Note, 0.5 is 50%. [0.0 - 1.0, default 0.5]")
                .defineInRange("headshotProjectileProtectionDamageReduction", 0.50, 0.0, 1.0);
        DO_BLINDNESS = builder
                .comment("Enable blindness effect after being headshot. [true / false, default, false]")
                .define("doBlindness", false);
        BLIND_TICKS = builder
                .comment("The number of ticks that the blindness effect lasts for. [0 - 9999, default 35]")
                .defineInRange("blindnessTicks", 35, 0, 9999);
        DO_NAUSEA = builder
                .comment("Enable nausea effect after being headshot. [true / false, default, false]]")
                .define("doNausea", false);
        NAUSEA_TICKS = builder
                .comment("The number of ticks that the nausea effect lasts for. [0 - 9999, default 35]")
                .defineInRange("nauseaTicks", 35, 0, 9999);
        HELMET_MITIGATION = builder
                .comment("Choose whether or not wearing a helmet can prevent a headshot. [true / false, default false]")
                .define("doHelmetMitigation", false);
        builder.pop();
    }
}
