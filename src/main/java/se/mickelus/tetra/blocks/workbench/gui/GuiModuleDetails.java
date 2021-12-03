package se.mickelus.tetra.blocks.workbench.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import se.mickelus.mutil.gui.*;
import se.mickelus.tetra.gui.*;
import se.mickelus.tetra.module.ItemModule;
import se.mickelus.tetra.module.ItemModuleMajor;
import se.mickelus.tetra.module.RepairRegistry;
import se.mickelus.tetra.module.data.GlyphData;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@ParametersAreNonnullByDefault
public class GuiModuleDetails extends GuiElement {

	private final GuiElement glyph;
	private final GuiString title;
	private final GuiTextSmall description;
	private final GuiString emptyLabel;

	private final GuiMagicUsage magicBar;
	private final GuiSettleProgress settleBar;

	private final GuiSynergyIndicator synergyIndicator;

	private final GuiElement repairGroup;
	private final GuiStringSmall repairTitle;
	private final GuiItemRolling repairMaterial;
	private final GuiStringSmall noRepairLabel;

	public GuiModuleDetails(int x, int y) {
		super(x, y, 224, 67);

		glyph = new GuiElement(3, 3, 16, 16);
		addChild(glyph);

		title = new GuiString(20, 7, 105, "");
		addChild(title);

		description = new GuiTextSmall(5, 19, 105, "");
		addChild(description);

		emptyLabel = new GuiString(0, -3, ChatFormatting.DARK_GRAY + I18n.get("tetra.workbench.module_detail.empty"));
		emptyLabel.setAttachment(GuiAttachment.middleCenter);
		addChild(emptyLabel);

		synergyIndicator = new GuiSynergyIndicator(130, 8);
		addChild(synergyIndicator);

		repairGroup = new GuiElement(150, 5, 60, 16);
		addChild(repairGroup);

		repairTitle = new GuiStringSmall(0, 7, I18n.get("item.tetra.modular.repair_material.label"));
		repairGroup.addChild(repairTitle);

		noRepairLabel = new GuiStringSmall(0, 7, ChatFormatting.GRAY + I18n.get("item.tetra.modular.repair_material.empty"));
		noRepairLabel.setAttachment(GuiAttachment.topCenter);
		noRepairLabel.setVisible(false);
		repairGroup.addChild(noRepairLabel);

		repairMaterial = new GuiItemRolling(-2, 0);
		repairMaterial.setAttachment(GuiAttachment.topRight);
		repairGroup.addChild(repairMaterial);

		magicBar = new GuiMagicUsage(130, 30, 80);
		addChild(magicBar);

		settleBar = new GuiSettleProgress(130, 45, 80);
		addChild(settleBar);
	}

	public void update(ItemModule module, ItemStack itemStack) {
		glyph.clearChildren();
		if (module != null) {
			title.setString(module.getName(itemStack));
			description.setString(ChatFormatting.GRAY + module.getDescription(itemStack).replace(ChatFormatting.RESET.toString(), ChatFormatting.GRAY.toString()));

			GlyphData glyphData = module.getVariantData(itemStack).glyph;

			if (module instanceof ItemModuleMajor) {
				glyph.addChild(new GuiTexture(0, 0, 15, 15, 52, 0, GuiTextures.workbench));
				glyph.addChild(new GuiModuleGlyph(-1, 0, 16, 16, glyphData).setShift(false));

				settleBar.update(itemStack, (ItemModuleMajor) module);
			} else {
				glyph.addChild(new GuiTexture(3, 2, 11, 11, 68, 0, GuiTextures.workbench));
				glyph.addChild(new GuiModuleGlyph(5, 4, 8, 8, glyphData).setShift(false));
			}

			magicBar.update(itemStack, ItemStack.EMPTY, module.getSlot());

			synergyIndicator.update(itemStack, module);

			ItemStack[] repairItemStacks = RepairRegistry.instance.getDefinitions(module.getVariantData(itemStack).key).stream()
				.map(definition -> definition.material.getApplicableItemStacks())
				.flatMap(Arrays::stream)
				.toArray(ItemStack[]::new);
			repairMaterial.setItems(repairItemStacks);

			boolean canRepair = repairItemStacks.length > 0;
			repairTitle.setVisible(canRepair);
			repairMaterial.setVisible(canRepair);
			noRepairLabel.setVisible(!canRepair);
		}

		synergyIndicator.setVisible(module != null);
		title.setVisible(module != null);
		description.setVisible(module != null);
		settleBar.setVisible(module instanceof ItemModuleMajor);
		magicBar.setVisible(module instanceof ItemModuleMajor);
		emptyLabel.setVisible(module == null);
		repairGroup.setVisible(module != null);
	}
}
