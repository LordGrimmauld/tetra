package se.mickelus.tetra.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiItem;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
public class GuiItemRolling extends GuiElement {
	private boolean showTooltip = true;
	private GuiItem.CountMode countMode = GuiItem.CountMode.normal;

	private GuiItem[] items = new GuiItem[0];

	public GuiItemRolling(int x, int y) {
		super(x, y, 16, 16);
	}

	public GuiItemRolling setTooltip(boolean showTooltip) {
		this.showTooltip = showTooltip;
		return this;
	}

	public GuiItemRolling setCountVisibility(GuiItem.CountMode mode) {
		this.countMode = mode;
		return this;
	}

	public GuiItemRolling setItems(ItemStack[] itemStacks) {
		items = Arrays.stream(itemStacks)
			.map(itemStack -> new GuiItem(0, 0).setItem(itemStack).setCountVisibility(countMode))
			.toArray(GuiItem[]::new);

		return this;
	}

	@Override
	protected void drawChildren(PoseStack matrixStack, int refX, int refY, int screenWidth, int screenHeight, int mouseX, int mouseY, float opacity) {
		if (items.length > 0) {
			int offset = (int) (System.currentTimeMillis() / 1000) % items.length;
			items[offset].draw(matrixStack, refX, refY, screenWidth, screenHeight, mouseX, mouseY, opacity * getOpacity());
		}
	}

	public List<String> getTooltipLines() {
		if (showTooltip && items.length > 0) {
			int offset = (int) (System.currentTimeMillis() / 1000) % items.length;
			return items[offset].getTooltipLines();
		}

		return null;
	}
}
