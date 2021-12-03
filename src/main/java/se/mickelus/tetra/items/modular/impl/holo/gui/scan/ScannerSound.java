package se.mickelus.tetra.items.modular.impl.holo.gui.scan;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import se.mickelus.tetra.TetraSounds;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class ScannerSound extends AbstractTickableSoundInstance {
	private int activeCounter;
	private final Minecraft mc;
	private boolean hasStarted;

	public ScannerSound(Minecraft mc) {
		super(TetraSounds.scannerLoop, SoundSource.BLOCKS);

		this.mc = mc;

		// priority = true;
		looping = true;
		attenuation = Attenuation.NONE;

		volume = 0;
		pitch = 0.5f;
	}

	public void activate() {
		if (!hasStarted) {
			mc.getSoundManager().play(this);
			hasStarted = true;
		}

		activeCounter = 2;
	}

	@Override
	public boolean canStartSilent() {
		return true;
	}

	@Override
	public void tick() {
		if (activeCounter > 0) {
			volume = Math.min(volume + 0.03f, 0.5f);

			activeCounter--;
		} else {
			volume = Math.max(volume - 0.027f, 0);
		}
	}
}