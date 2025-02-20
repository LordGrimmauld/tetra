package se.mickelus.tetra.gui.stats.getter;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.effect.ItemEffect;
import se.mickelus.tetra.items.modular.impl.bow.ModularBowItem;
import se.mickelus.tetra.properties.TetraAttributes;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TooltipGetterVelocity implements ITooltipGetter {
	public static final IStatGetter drawStrengthGetter = new StatGetterAttribute(TetraAttributes.drawStrength.get());
	private static final IStatGetter velocityGetter = new StatGetterEffectLevel(ItemEffect.velocity, 1);

	@Override
	public String getTooltipBase(Player player, ItemStack itemStack) {
		double velocityBonus = velocityGetter.getValue(player, itemStack);
		double drawStrength = drawStrengthGetter.getValue(player, itemStack);
		return I18n.get("tetra.stats.velocity.tooltip",
			String.format("%.0f%%", velocityBonus),
			String.format("%.1f",
				3 * (ModularBowItem.getArrowVelocity(20, drawStrength, (float) velocityBonus / 100f, false)
					- ModularBowItem.getArrowVelocity(20, drawStrength, 0, false))));
	}
}
