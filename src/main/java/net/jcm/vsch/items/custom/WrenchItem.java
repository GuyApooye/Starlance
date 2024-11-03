package net.jcm.vsch.items.custom;

import net.jcm.vsch.ship.ThrusterData.ThrusterMode;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class WrenchItem extends Item {

	public static final EnumProperty<ThrusterMode> MODE = EnumProperty.create("mode", ThrusterMode.class);

	public WrenchItem(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {

		if (!isSelected) {
			return;
		}

		// Perform a ray trace (with a range of 5 blocks)
		HitResult hitResult = entity.pick(5.0, 0.0F, false);

		// Check if the ray trace hit a block
		if (hitResult.getType() == HitResult.Type.BLOCK) {
			// Get block state
			BlockHitResult blockHit = (BlockHitResult) hitResult;
			BlockPos blockPos = blockHit.getBlockPos();
			BlockState blockState = level.getBlockState(blockPos);

			// Does it have a thruster mode property
			// This seems wrong, but oh well
			if (blockState.hasProperty(MODE)) {
				// If our entity is a player
				if (entity instanceof Player player) {
					// Send actionbar of its state
					player.displayClientMessage(Component.translatable("vsch.mode_message").append(Component.translatable("vsch."+blockState.getValue(MODE).toString().toLowerCase())), true);
				}
			}
		}

	}
}