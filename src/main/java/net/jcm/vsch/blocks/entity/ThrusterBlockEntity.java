package net.jcm.vsch.blocks.entity;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.quasar.particle.ParticleEmitter;
import foundry.veil.api.quasar.particle.ParticleSystemManager;
import net.jcm.vsch.VSCHMod;
import net.jcm.vsch.blocks.thruster.AbstractThrusterBlockEntity;
import net.jcm.vsch.blocks.thruster.ThrusterEngine;
import net.jcm.vsch.blocks.thruster.ThrusterEngineContext;
import net.jcm.vsch.config.VSCHConfig;

import net.jcm.vsch.particles.modules.init.ThrusterInitModule;
import net.jcm.vsch.particles.modules.update.ThrusterUpdateModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Map;

public class ThrusterBlockEntity extends AbstractThrusterBlockEntity {

	private static final ResourceLocation PARTICLE = new ResourceLocation(VSCHMod.MODID, "thruster_red");

	public ThrusterBlockEntity(BlockPos pos, BlockState state) {
		super("thruster", VSCHBlockEntities.THRUSTER_BLOCK_ENTITY.get(), pos, state,
			new NormalThrusterEngine(
				VSCHConfig.THRUSTER_ENERGY_CONSUME_RATE.get().intValue(),
				VSCHConfig.THRUSTER_STRENGTH.get().intValue(),
				VSCHConfig.getThrusterFuelConsumeRates()
			)
		);
	}

	private static class NormalThrusterEngine extends ThrusterEngine {
		private final Map<String, Integer> fuelConsumeRates;

		public NormalThrusterEngine(int energyConsumeRate, float maxThrottle, Map<String, Integer> fuelConsumeRates) {
			super(1, energyConsumeRate, maxThrottle);
			this.fuelConsumeRates = fuelConsumeRates;
		}

		@Override
		public boolean isValidFuel(int tank, Fluid fluid) {
			String fluidName = BuiltInRegistries.FLUID.getKey(fluid).toString();
			return this.fuelConsumeRates.containsKey(fluidName);
		}

		@Override
		public void tick(ThrusterEngineContext context) {
			super.tick(context);
			if (this.fuelConsumeRates.size() == 0) {
				return;
			}
			double power = context.getPower();
			if (power == 0) {
				return;
			}
			Fluid fluid = context.getFluidHandler().getFluidInTank(0).getFluid();
			if (fluid == Fluids.EMPTY) {
				context.setPower(0);
				return;
			}
			String fluidName = BuiltInRegistries.FLUID.getKey(fluid).toString();
			int consumeRate = this.fuelConsumeRates.get(fluidName);
			if (consumeRate == 0) {
				return;
			}

			int needsFuel = (int)(Math.ceil(consumeRate * power));
			int avaliableFuel = context.getFluidHandler().drain(new FluidStack(fluid, needsFuel), IFluidHandler.FluidAction.SIMULATE).getAmount();
			context.setPower((double)(avaliableFuel) / consumeRate);
			context.addConsumer((ctx) -> {
				ctx.getFluidHandler().drain(new FluidStack(fluid, (int)(Math.ceil(consumeRate * ctx.getPower()))), IFluidHandler.FluidAction.EXECUTE);
			});
		}
	}

	@Override
	protected void spawnParticles(Vector3d pos, Vector3d direction, Vector3f rotation) {
		try {

			Vector3d speed = new Vector3d(direction).mul(-this.getCurrentPower());

			speed.mul(0.6);

			ParticleSystemManager manager = VeilRenderSystem.renderer().getParticleManager();
			ParticleEmitter emitter = manager.createEmitter(PARTICLE);
			emitter.setPosition(pos.fma(-0.75, direction, new Vector3d()));
			emitter.addCodeModule(builder -> {
				builder.addModule(new ThrusterInitModule(speed, rotation));
				builder.addModule(new ThrusterUpdateModule());
			});
			manager.addParticleSystem(emitter);
		} catch (Exception e) {
			super.spawnParticles(pos, direction, rotation);
		}
	}
}
