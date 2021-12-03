package se.mickelus.tetra.gui.stats.getter;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.Tooltips;

public interface ITooltipGetter {
	default String getTooltip(Player player, ItemStack itemStack) {
		if (hasExtendedTooltip(player, itemStack)) {
			return getTooltipBase(player, itemStack) + "\n \n" + Tooltips.expand.getString();
		}

		return getTooltipBase(player, itemStack);
	}

	/**
	 * Used for showing extended tooltips when shift is held down
	 *
	 * @param player
	 * @param itemStack
	 * @return
	 */
	default String getTooltipExtended(Player player, ItemStack itemStack) {
		if (hasExtendedTooltip(player, itemStack)) {
			return getTooltipBase(player, itemStack) + "\n \n" + Tooltips.expanded.getString() + "\n"
				+ ChatFormatting.GRAY + getTooltipExtension(player, itemStack);
		}

		return getTooltip(player, itemStack);
	}

	String getTooltipBase(Player player, ItemStack itemStack);

	default boolean hasExtendedTooltip(Player player, ItemStack itemStack) {
		return false;
	}

	default String getTooltipExtension(Player player, ItemStack itemStack) {
		return null;
	}
}
