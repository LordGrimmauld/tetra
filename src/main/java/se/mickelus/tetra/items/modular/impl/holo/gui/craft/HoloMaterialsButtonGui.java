package se.mickelus.tetra.items.modular.impl.holo.gui.craft;

import net.minecraft.client.resources.language.I18n;
import se.mickelus.mutil.gui.*;
import se.mickelus.mutil.gui.animation.Applier;
import se.mickelus.mutil.gui.animation.GuiAnimation;
import se.mickelus.mutil.gui.animation.KeyframeAnimation;
import se.mickelus.tetra.gui.GuiColors;
import se.mickelus.tetra.gui.GuiTextures;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class HoloMaterialsButtonGui extends GuiClickable {

	private final GuiTexture backdrop;
	private final GuiTexture icon;
	private final GuiString label;
	private final List<GuiAnimation> hoverAnimations;
	private final List<GuiAnimation> blurAnimations;
	private final KeyframeAnimation showAnimation;
	private final KeyframeAnimation hideAnimation;

	public HoloMaterialsButtonGui(int x, int y, Runnable onClickHandler) {
		super(x, y, 64, 64, onClickHandler);

		hoverAnimations = new ArrayList<>();
		blurAnimations = new ArrayList<>();

		backdrop = new GuiTexture(0, 0, 52, 52, GuiTextures.workbench);
		backdrop.setAttachment(GuiAttachment.middleCenter);
		addChild(backdrop);

		icon = new GuiTexture(0, 0, 38, 38, 0, 180, GuiTextures.workbench);
		icon.setAttachment(GuiAttachment.middleCenter);
		addChild(icon);

		label = new GuiStringOutline(0, -1, I18n.get("tetra.holo.craft.materials"));
		label.setAttachment(GuiAttachment.middleCenter);
		label.setOpacity(0);
		addChild(label);

		showAnimation = new KeyframeAnimation(80, this)
			.applyTo(new Applier.Opacity(1));

		hideAnimation = new KeyframeAnimation(80, this)
			.applyTo(new Applier.Opacity(0))
			.onStop(complete -> this.isVisible = false);


		hoverAnimations.add(new KeyframeAnimation(80, label)
			.applyTo(new Applier.Opacity(1), new Applier.TranslateY(-2, 0)));

		blurAnimations.add(new KeyframeAnimation(120, label)
			.applyTo(new Applier.Opacity(0), new Applier.TranslateY(0, 2)));
	}

	@Override
	protected void onFocus() {
		backdrop.setColor(GuiColors.hover);
		label.setColor(GuiColors.hover);
		icon.setColor(GuiColors.muted);

		blurAnimations.forEach(GuiAnimation::stop);
		hoverAnimations.forEach(GuiAnimation::start);
	}

	@Override
	protected void onBlur() {
		backdrop.setColor(GuiColors.normal);
		label.setColor(GuiColors.normal);
		icon.setColor(GuiColors.normal);

		hoverAnimations.forEach(GuiAnimation::stop);
		blurAnimations.forEach(GuiAnimation::start);
	}

	@Override
	protected void onShow() {
		super.onShow();
		hideAnimation.stop();
		showAnimation.start();
	}

	@Override
	protected boolean onHide() {
		super.onHide();
		showAnimation.stop();
		hideAnimation.start();

		return false;
	}

	@Override
	protected void calculateFocusState(int refX, int refY, int mouseX, int mouseY) {
		mouseX -= refX + x;
		mouseY -= refY + y;
		boolean gainFocus = mouseX + mouseY >= 44;

		if (mouseX + mouseY > 84) {
			gainFocus = false;
		}

		if (mouseX - mouseY > 16) {
			gainFocus = false;
		}

		if (mouseY - mouseX > 19) {
			gainFocus = false;
		}

		if (gainFocus != hasFocus) {
			hasFocus = gainFocus;
			if (hasFocus) {
				onFocus();
			} else {
				onBlur();
			}
		}
	}
}
