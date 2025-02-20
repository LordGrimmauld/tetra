package se.mickelus.tetra.effect.howling;

import se.mickelus.mutil.gui.GuiTexture;
import se.mickelus.mutil.gui.animation.Applier;
import se.mickelus.mutil.gui.animation.KeyframeAnimation;
import se.mickelus.tetra.gui.GuiTextures;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class HowlingIndicatorGui extends GuiTexture {
	private final KeyframeAnimation animation;

	private final int originX;
	private final int originY;

	public HowlingIndicatorGui(int x, int y, int width, int height, int textureX, int textureY, int transitionOffset, boolean horizontalTransition) {
		super(x, y, width, height, textureX, textureY, GuiTextures.hud);

		setOpacity(0);

		if (horizontalTransition) {
			animation = new KeyframeAnimation(60, this)
				.applyTo(new Applier.Opacity(0.5f), new Applier.TranslateX(x));
			originX = x + transitionOffset;
			originY = y;
			setX(x + transitionOffset);
		} else {
			animation = new KeyframeAnimation(60, this)
				.applyTo(new Applier.Opacity(0.5f), new Applier.TranslateY(y));
			originX = x;
			originY = y + transitionOffset;
			setY(y + transitionOffset);
		}
	}

	public void show() {
		if (!animation.isActive() && getOpacity() < 0.5) {
			animation.start();
		}
	}

	public void reset() {
		animation.stop();
		setOpacity(0);
		setX(originX);
		setY(originY);
	}
}
