package se.mickelus.tetra.items.modular.impl.toolbelt.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import se.mickelus.mutil.gui.GuiAttachment;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiString;
import se.mickelus.tetra.items.modular.impl.toolbelt.inventory.PotionsInventory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class OverlayGuiPotionGroup extends GuiElement {
	GuiString focusSlot;
	PotionsInventory inventory;
	private OverlayGuiPotionSlot[] slots = new OverlayGuiPotionSlot[0];

	public OverlayGuiPotionGroup(int x, int y) {
		super(x, y, 0, 0);
		setAttachmentPoint(GuiAttachment.topCenter);

		focusSlot = new GuiString(0, -15, "");
		focusSlot.setAttachmentPoint(GuiAttachment.topCenter);
		focusSlot.setAttachmentAnchor(GuiAttachment.topCenter);
	}

	public void setInventory(PotionsInventory inventory) {
		clearChildren();
		this.inventory = inventory;
		int numSlots = inventory.getContainerSize();
		slots = new OverlayGuiPotionSlot[numSlots];

		focusSlot.setString("");
		addChild(focusSlot);

		width = 66;

		if (numSlots > 5) {
			height = 44;
		} else if (numSlots > 3) {
			height = 33;
		} else {
			width = numSlots * 22;
			height = 22;
		}

		for (int i = 0; i < numSlots; i++) {
			ItemStack itemStack = inventory.getItem(i);
			if (!itemStack.isEmpty()) {
				if (i > 6) {
					slots[i] = new OverlayGuiPotionSlot(22, 22, itemStack, i, true);
				} else if (i > 4) {
					slots[i] = new OverlayGuiPotionSlot((i - 5) * 22 + 11, -11, itemStack, i, true);
				} else if (i > 2) {
					slots[i] = new OverlayGuiPotionSlot((i - 3) * 22 + 11, 11, itemStack, i, true);
				} else {
					slots[i] = new OverlayGuiPotionSlot(i * 22, 0, itemStack, i, true);
				}
				addChild(slots[i]);
			}
		}
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			Arrays.stream(slots)
				.filter(Objects::nonNull)
				.forEach(item -> item.setVisible(true));
		} else {
			Arrays.stream(slots)
				.filter(Objects::nonNull)
				.forEach(item -> item.setVisible(false));
		}
		focusSlot.setVisible(visible);
	}

	@Override
	public void draw(PoseStack matrixStack, int refX, int refY, int screenWidth, int screenHeight, int mouseX, int mouseY, float opacity) {
		super.draw(matrixStack, refX, refY, screenWidth, screenHeight, mouseX, mouseY, opacity);

		int focus = getFocus();
		if (focus != -1) {
			focusSlot.setString(inventory.getItem(focus).getHoverName().getString());
		} else {
			focusSlot.setString("");
		}
	}

	public int getFocus() {
		for (int i = 0; i < slots.length; i++) {
			OverlayGuiPotionSlot element = slots[i];
			if (element != null && element.hasFocus()) {
				return element.getSlot();
			}
		}
		return -1;
	}

	public InteractionHand getHand() {
		return null;
	}
}
