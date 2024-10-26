package net.jcm.vsch.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VSCHConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Boolean> THRUSTER_TOGGLE;
    public static final ForgeConfigSpec.ConfigValue<String> THRUSTER_MODE;
    private static final Collection<String> modes = new ArrayList<String>(2);
    static {
        modes.add("POSITION");
        modes.add("GLOBAL");
        BUILDER.push("Thruster Configs");
        THRUSTER_TOGGLE = BUILDER.comment("Thruster Mode Toggling").define("thruster_mode_toggle", true);
        THRUSTER_MODE = BUILDER.comment("Default Thruster Mode").defineInList("thruster_default_mode","POSITION",modes);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
    public static void register(ModLoadingContext context){
        context.registerConfig(ModConfig.Type.SERVER, VSCHConfig.SPEC, "vsch-config.toml");
    }
}
