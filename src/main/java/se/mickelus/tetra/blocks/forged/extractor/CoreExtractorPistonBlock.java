package se.mickelus.tetra.blocks.forged.extractor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ObjectHolder;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.blocks.TetraWaterloggedBlock;
import se.mickelus.tetra.blocks.forged.ForgedBlockCommon;
import se.mickelus.mutil.network.PacketHandler;
import se.mickelus.tetra.util.TickProvider;
import se.mickelus.mutil.util.TileEntityOptional;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;
@ParametersAreNonnullByDefault
public class CoreExtractorPistonBlock extends TetraWaterloggedBlock implements EntityBlock {
	public static final String unlocalizedName = "extractor_piston";
	public static final TickProvider<CoreExtractorPistonTile> TILE_TICK_PROVIDER = new TickProvider<>(() -> CoreExtractorPistonTile.type, CoreExtractorPistonTile::new);

	@ObjectHolder(TetraMod.MOD_ID + ":" + unlocalizedName)
	public static CoreExtractorPistonBlock instance;

	public static final net.minecraft.world.level.block.state.properties.BooleanProperty hackProp = BooleanProperty.create("hack");

	public static final VoxelShape boundingBox = box(5, 0, 5, 11, 16, 11);

	public CoreExtractorPistonBlock() {
		super(ForgedBlockCommon.propertiesNotSolid);

		setRegistryName(unlocalizedName);
	}

	@Override
	public void init(PacketHandler packetHandler) {
		super.init(packetHandler);

		packetHandler.registerPacket(CoreExtractorPistonUpdatePacket.class, CoreExtractorPistonUpdatePacket::new);
	}

	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
		TileEntityOptional.from(worldIn, pos, CoreExtractorPistonTile.class)
			.ifPresent(te -> {
				if (te.isActive()) {
					float random = rand.nextFloat();

					if (random < 0.6f) {
						worldIn.addParticle(ParticleTypes.SMOKE,
							pos.getX() + 0.4 + rand.nextGaussian() * 0.2,
							pos.getY() + rand.nextGaussian(),
							pos.getZ() + 0.4 + rand.nextGaussian() * 0.2,
							0.0D, 0.0D, 0.0D);
					}
				}
			});
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
		if (Direction.DOWN.equals(facing) && !CoreExtractorBaseBlock.instance.equals(facingState.getBlock())) {
			return state.getValue(BlockStateProperties.WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
		}

		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return boundingBox;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(hackProp);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return TILE_TICK_PROVIDER.create(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
		return TILE_TICK_PROVIDER.forTileType(entityType).orElseGet(() -> EntityBlock.super.getTicker(level, state, entityType));
	}
}