package se.mickelus.tetra.blocks.salvage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;

import java.util.Collection;

public interface IInteractiveBlock {
	BlockInteraction[] getPotentialInteractions(Level world, BlockPos pos, BlockState blockState, Direction face, Collection<ToolAction> tools);
}
