package se.mickelus.tetra.generation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FeatureChild {
	/**
	 * The position offset relative to the parent at which this child feature should be generated.
	 * Json format: [x, y, z]
	 */
	public BlockPos offset = new BlockPos(0, 0, 0);

	/**
	 * The direction in which the child feature should be facing.
	 * Possible json values: "DOWN", "UP", "NORTH", "SOUTH", "WEST", "EAST"
	 */
	public Direction facing = Direction.NORTH;

	/**
	 * The chance for this child feature to generate. Has to be a decimal number between 0.0 and 1.1, where a value of
	 * 1.0 would cause it to always generate.
	 */
	public float chance = 1;

	/**
	 * An array of resource locations for features that can be used as child features at this position.
	 * Json format: ["domain:path"]
	 */
	public ResourceLocation[] features = new ResourceLocation[0];
}
