package se.mickelus.tetra.blocks.forged.chthonic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.ObjectHolder;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.blocks.TetraBlock;
import se.mickelus.tetra.blocks.forged.extractor.CoreExtractorBaseTile;
import se.mickelus.tetra.blocks.forged.extractor.SeepingBedrockBlock;
import se.mickelus.tetra.util.TickProvider;
import se.mickelus.tetra.util.TileEntityOptional;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FracturedBedrockBlock extends TetraBlock implements EntityBlock {
	public static final TickProvider<FracturedBedrockTile> TILE_TICK_PROVIDER = new TickProvider<>(FracturedBedrockTile.type, FracturedBedrockTile::new);
	public static final String unlocalizedName = "fractured_bedrock";

	@ObjectHolder(TetraMod.MOD_ID + ":" + unlocalizedName)
	public static FracturedBedrockBlock instance;

	public FracturedBedrockBlock() {
		super(BlockBehaviour.Properties.of(Material.STONE).strength(-1.0F, 3600000.0F).noDrops());
		setRegistryName(unlocalizedName);
	}

	public static boolean canPierce(Level world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		return Blocks.BEDROCK.equals(blockState.getBlock())
			|| (SeepingBedrockBlock.instance.equals(blockState.getBlock()) && !SeepingBedrockBlock.isActive(blockState));
	}

	public static void pierce(Level world, BlockPos pos, int amount) {
		FracturedBedrockTile tile = TileEntityOptional.from(world, pos, FracturedBedrockTile.class).orElse(null);

		if (tile == null && canPierce(world, pos)) {
			BlockState blockState = world.getBlockState(pos);
			world.setBlock(pos, instance.defaultBlockState(), 2);
			tile = TileEntityOptional.from(world, pos, FracturedBedrockTile.class).orElse(null);

			if (!world.isClientSide) {
				tile.updateLuck(SeepingBedrockBlock.instance.equals(blockState.getBlock()));
			}
		}

		if (tile != null) {
			tile.activate(amount);
		}
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return TILE_TICK_PROVIDER.create(pos, state);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
		return TILE_TICK_PROVIDER.forTileType(entityType).orElseGet(() -> EntityBlock.super.getTicker(level, state, entityType));
	}
}
