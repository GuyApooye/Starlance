package net.jcm.vsch.blocks.custom;


import net.jcm.vsch.config.VSCHConfig;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.jcm.vsch.ship.VSCHForceInducedShips;
import net.jcm.vsch.blocks.entity.AirThrusterBlockEntity;
import net.jcm.vsch.blocks.entity.ThrusterBlockEntity;
import net.jcm.vsch.ship.ThrusterData;
import net.jcm.vsch.util.rot.DirectionalShape;
import net.jcm.vsch.util.rot.RotShape;
import net.jcm.vsch.util.rot.RotShapes;
import net.lointain.cosmos.init.CosmosModItems;


public class AirThrusterBlock extends ThrusterBlock {

	public static final int MULT = 100;

	public AirThrusterBlock(Properties properties) {
		super(properties);
		// TODO Auto-generated constructor stub
	}

	@Override
	public float getThrottle(BlockState state, int signal) {
		return signal * MULT;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AirThrusterBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (!level.isClientSide()) {
			return null;
		} else {
			return (level0, pos0, state0, blockEntity) -> ((AirThrusterBlockEntity)blockEntity).clientTick(level0, pos0, state0, (AirThrusterBlockEntity) blockEntity);
		}
	}


}