package net.jcm.vsch;

import net.jcm.vsch.blocks.ModBlocks;
import net.jcm.vsch.commands.ModCommands;
import net.jcm.vsch.items.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(VSCHMod.MODID)
public class VSCHMod {
	public static final String MODID = "vsch";
	
	// Register blocks (thruster)
    //private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, VSCHConfig.MOD_ID);
    //public static final RegistryObject<Block> SAD_BLOCK = BLOCKS.register("thruster",
    //        () -> new ThrusterBlock(Block.Properties.copy(Blocks.IRON_BLOCK)));
    
    //public static final Logger logger = LogManager.getLogger(VSCHConfig.MOD_ID);
    
    public VSCHMod() {
        // Initialize logic here
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        //BLOCKS.register(modBus);
        ModItems.register(modBus);
        ModBlocks.register(modBus);
	ModBlockEntities.register(modBus);
        
        // Register commands (I took this code from another one of my mods, can't be bothered to make it consistent with the rest of this)
        MinecraftForge.EVENT_BUS.register(ModCommands.class);
    }
}
