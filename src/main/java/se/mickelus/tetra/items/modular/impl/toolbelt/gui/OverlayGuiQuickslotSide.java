package se.mickelus.tetra.items.modular.impl.toolbelt.gui;

import se.mickelus.mutil.gui.GuiAttachment;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiTexture;
import se.mickelus.mutil.gui.animation.Applier;
import se.mickelus.mutil.gui.animation.KeyframeAnimation;
import se.mickelus.tetra.gui.GuiColors;
import se.mickelus.tetra.gui.GuiTextures;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class OverlayGuiQuickslotSide extends GuiElement {

	private final GuiTexture arrow;

	private final KeyframeAnimation showAnimation;
	private final KeyframeAnimation hideAnimation;

	public OverlayGuiQuickslotSide(int x, int y, int width, int height, boolean right) {
		super(x, y, width, height);

		if (right) {
			arrow = new GuiTexture(7, -1, 5, 7, 5, 42, GuiTextures.toolbelt);
			arrow.setAttachment(GuiAttachment.middleLeft);
		} else {
			arrow = new GuiTexture(-7, -1, 5, 7, 0, 42, GuiTextures.toolbelt);
			arrow.setAttachment(GuiAttachment.middleRight);
		}
		arrow.setColor(GuiColors.mutedStrong);
		arrow.setOpacity(0);
		addChild(arrow);

		showAnimation = new KeyframeAnimation(100, arrow)
			.applyTo(new Applier.TranslateX(arrow.getX() + (right ? 1 : -1)), new Applier.Opacity(1));
		hideAnimation = new KeyframeAnimation(200, arrow)
			.applyTo(new Applier.TranslateX(arrow.getX()), new Applier.Opacity(0));
	}

	public void animateIn() {
		hideAnimation.stop();
		showAnimation.start();
	}

	public void animateOut() {
		arrow.setColor(GuiColors.mutedStrong);
		showAnimation.stop();
		hideAnimation.start();
	}

	@Override
	protected void onFocus() {
		arrow.setColor(GuiColors.hover);
	}

	@Override
	protected void onBlur() {
		arrow.setColor(GuiColors.mutedStrong);
	}
}
