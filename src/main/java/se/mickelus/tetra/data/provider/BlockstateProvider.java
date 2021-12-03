package se.mickelus.tetra.data.provider;

import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import se.mickelus.tetra.blocks.forged.ForgedVentBlock;

import javax.annotation.ParametersAreNonnullByDefault;

import static se.mickelus.tetra.TetraMod.MOD_ID;

@ParametersAreNonnullByDefault
public class BlockstateProvider extends BlockStateProvider {
	public BlockstateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
		super(gen, modid, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
//        slabBlock(BlockForgedPlatformSlab.instance,
//                new ResourceLocation(MOD_ID, "block/forged_platform"),
//                new ResourceLocation(MOD_ID, "blocks/forged_platform_side"),
//                new ResourceLocation(MOD_ID, "blocks/forged_platform_bottom"),
//                new ResourceLocation(MOD_ID, "blocks/forged_platform_alternate"));

		setupVent();
	}

	private ConfiguredModel[] directionalBlock(BlockState state, ModelFile model) {
		Direction dir = state.getValue(BlockStateProperties.FACING);
		return ConfiguredModel.builder()
			.modelFile(model)
			.rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
			.rotationY(dir.getAxis().isVertical() ? 0 : (int) dir.toYRot() % 360)
			.build();
	}

	private void setupVent() {
		VariantBlockStateBuilder builder = getVariantBuilder(ForgedVentBlock.instance);

		builder.partialState()
			.with(ForgedVentBlock.propRotation, 0)
			.with(ForgedVentBlock.propBroken, false)
			.addModels(new ConfiguredModel(models().getExistingFile(new ResourceLocation(MOD_ID, "block/forged_vent0"))));

		builder.partialState()
			.with(ForgedVentBlock.propRotation, 1)
			.with(ForgedVentBlock.propBroken, false)
			.addModels(new ConfiguredModel(models().getExistingFile(new ResourceLocation(MOD_ID, "block/forged_vent1"))));

		builder.partialState()
			.with(ForgedVentBlock.propRotation, 2)
			.with(ForgedVentBlock.propBroken, false)
			.addModels(new ConfiguredModel(models().getExistingFile(new ResourceLocation(MOD_ID, "block/forged_vent2"))));

		builder.partialState()
			.with(ForgedVentBlock.propRotation, 3)
			.with(ForgedVentBlock.propBroken, false)
			.addModels(new ConfiguredModel(models().getExistingFile(new ResourceLocation(MOD_ID, "block/forged_vent3"))));

		builder.partialState()
			.with(ForgedVentBlock.propRotation, 0)
			.with(ForgedVentBlock.propBroken, true)
			.addModels(new ConfiguredModel(models().getExistingFile(new ResourceLocation(MOD_ID, "block/forged_vent0_broken"))));

		builder.partialState()
			.with(ForgedVentBlock.propRotation, 1)
			.with(ForgedVentBlock.propBroken, true)
			.addModels(new ConfiguredModel(models().getExistingFile(new ResourceLocation(MOD_ID, "block/forged_vent1_broken"))));

		builder.partialState()
			.with(ForgedVentBlock.propRotation, 2)
			.with(ForgedVentBlock.propBroken, true)
			.addModels(new ConfiguredModel(models().getExistingFile(new ResourceLocation(MOD_ID, "block/forged_vent2_broken"))));

		builder.partialState()
			.with(ForgedVentBlock.propRotation, 3)
			.with(ForgedVentBlock.propBroken, true)
			.addModels(new ConfiguredModel(models().getExistingFile(new ResourceLocation(MOD_ID, "block/forged_vent3_broken"))));
	}
}
