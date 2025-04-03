package net.jcm.vsch;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.valkyrienskies.core.impl.hooks.VSEvents;

import net.jcm.vsch.blocks.VSCHBlocks;
import net.jcm.vsch.blocks.entity.VSCHBlockEntities;
import net.jcm.vsch.commands.ModCommands;
import net.jcm.vsch.compat.CompatMods;
import net.jcm.vsch.compat.create.ponder.VSCHPonderRegistrateBlocks;
import net.jcm.vsch.compat.create.ponder.VSCHPonderRegistry;
import net.jcm.vsch.compat.create.ponder.VSCHPonderTags;
import net.jcm.vsch.config.VSCHConfig;
import net.jcm.vsch.entity.VSCHEntities;
import net.jcm.vsch.event.GravityInducer;
import net.jcm.vsch.items.VSCHItems;

@Mod(VSCHMod.MODID)
public class VSCHMod {
	public static final String MODID = "vsch";
	public static final String VERSION = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString();
	private static final ResourceLocation BLOOM_PIPELINE = new ResourceLocation("vsch", "bloom");
	private static final ResourceLocation BLOOM_PARTICLE_PIPELINE = new ResourceLocation("vsch", "bloom_particle");

	public VSCHMod() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		VSCHBlockEntities.register(modBus);
		VSCHBlocks.register(modBus);
		VSCHConfig.register(ModLoadingContext.get());
		VSCHEntities.register(modBus);
		VSCHItems.register(modBus);
		VSCHTab.register(modBus);
		VSCHTags.register();

		// Register commands (I took this code from another one of my mods, can't be bothered to make it consistent with the rest of this)
		MinecraftForge.EVENT_BUS.register(ModCommands.class);

		VSEvents.ShipLoadEvent.Companion.on((shipLoadEvent) -> {
			GravityInducer.getOrCreate(shipLoadEvent.getShip());
		});

		modBus.addListener(this::onClientSetup);
		modBus.addListener(this::registerRenderers);

		if (CompatMods.CREATE.isLoaded()) {
			VSCHPonderRegistrateBlocks.register();
		}
	}

	// Idk why but this doesn't work in VSCHEvents (prob its only a server-side event listener)
	private void onClientSetup(FMLClientSetupEvent event) {
		if (CompatMods.CREATE.isLoaded()) {
			VSCHPonderRegistry.register();
			VSCHPonderTags.register();
		}
		VeilEventPlatform.INSTANCE.onVeilRenderTypeStageRender((stage, levelRenderer, bufferSource, poseStack, matrix4f, i, v, camera, frustum) -> {
			Minecraft minecraft = Minecraft.getInstance();
			ClientLevel level = minecraft.level;
			PostProcessingManager postManager = VeilRenderSystem.renderer().getPostProcessingManager();
			if (level != null) {
				if (stage == VeilRenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
					PostPipeline bloomParticlePipeLine = postManager.getPipeline(BLOOM_PARTICLE_PIPELINE);
					if (bloomParticlePipeLine != null) postManager.runPipeline(bloomParticlePipeLine);
					AdvancedFbo bloomParticleFbo = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(BLOOM_PARTICLE_PIPELINE);
					if (bloomParticleFbo != null) {
						bloomParticleFbo.bind(false);
						bloomParticleFbo.clear();
						AdvancedFbo.getMainFramebuffer().bind(true);
					}
					PostPipeline bloomPipeLine = postManager.getPipeline(BLOOM_PIPELINE);
					if (bloomPipeLine != null) postManager.runPipeline(bloomPipeLine);
				}
			}
		});
//		VeilRenderSystem.renderer().getPostProcessingManager().add(BLOOM_PIPELINE);
	}

	public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		// event.registerEntityRenderer(VSCHEntities.MAGNET_ENTITY.get(), NoopRenderer::new);
	}
}





