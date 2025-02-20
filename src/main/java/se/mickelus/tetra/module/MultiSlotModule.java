package se.mickelus.tetra.module;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.module.data.ModuleData;
import se.mickelus.tetra.module.data.ModuleModel;
import se.mickelus.tetra.module.data.TweakData;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@ParametersAreNonnullByDefault
public class MultiSlotModule extends ItemModule {

	protected String slotSuffix;

	protected String unlocalizedName;

	public MultiSlotModule(ResourceLocation identifier, ModuleData data) {
		super(data.slots[0], identifier.getPath());

		slotSuffix = data.slotSuffixes[0];

		// strip the suffix from the unlocalized name
		unlocalizedName = identifier.getPath().substring(0, identifier.getPath().length() - data.slotSuffixes[0].length());

		renderLayer = data.renderLayer;

		variantData = data.variants;

		if (data.tweakKey != null) {
			TweakData[] tweaks = DataManager.tweakData.getData(data.tweakKey);
			if (tweaks != null) {
				this.tweaks = tweaks;
			} else {
				this.tweaks = new TweakData[0];
			}
		}
	}

	@Override
	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	@Override
	public ModuleModel[] getModels(ItemStack itemStack) {
		return Arrays.stream(super.getModels(itemStack))
			.map(model -> new ModuleModel(model.type, new ResourceLocation(TetraMod.MOD_ID, model.location.getPath() + slotSuffix), model.tint))
			.toArray(ModuleModel[]::new);
	}
}
