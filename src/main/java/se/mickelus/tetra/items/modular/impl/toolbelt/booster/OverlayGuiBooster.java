package se.mickelus.tetra.items.modular.impl.toolbelt.booster;

import net.minecraft.client.Minecraft;
import se.mickelus.mutil.gui.GuiRoot;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class OverlayGuiBooster extends GuiRoot {

	private final GuiBarBooster barElement;

	public OverlayGuiBooster(Minecraft mc) {
		super(mc);

		barElement = new GuiBarBooster(50, 100, 0, 0);
		addChild(barElement);
	}

	public void setFuel(float fuel) {
		barElement.setFuel(fuel);
	}
}
