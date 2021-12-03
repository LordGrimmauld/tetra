package se.mickelus.tetra.gui.stats.getter;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.module.ItemModuleMajor;
import se.mickelus.tetra.properties.AttributeHelper;
import se.mickelus.tetra.util.CastOptional;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class StatGetterAttributeAddition implements IStatGetter {
	private final Attribute attribute;

	public StatGetterAttributeAddition(Attribute attribute) {
		this.attribute = attribute;
	}

	@Override
	public boolean shouldShow(Player player, ItemStack currentStack, ItemStack previewStack) {
		double baseValue = attribute.getDefaultValue();
		return getValue(player, currentStack) != baseValue || getValue(player, previewStack) != baseValue;
	}

	@Override
	public double getValue(Player player, ItemStack itemStack) {
		return CastOptional.cast(itemStack.getItem(), IModularItem.class)
			.map(item -> item.getAttributeModifiers(itemStack))
			.map(map -> map.get(attribute))
			.map(AttributeHelper::getAdditionAmount)
			.orElseGet(attribute::getDefaultValue);
	}

	@Override
	public double getValue(Player player, ItemStack itemStack, String slot) {
		return CastOptional.cast(itemStack.getItem(), IModularItem.class)
			.map(item -> item.getModuleFromSlot(itemStack, slot))
			.map(module -> module.getAttributeModifiers(itemStack))
			.map(map -> map.get(attribute))
			.map(AttributeHelper::getAdditionAmount)
			.orElse(0d);
	}

	@Override
	public double getValue(Player player, ItemStack itemStack, String slot, String improvement) {
		return CastOptional.cast(itemStack.getItem(), IModularItem.class)
			.flatMap(item -> CastOptional.cast(item.getModuleFromSlot(itemStack, slot), ItemModuleMajor.class))
			.map(module -> module.getImprovement(itemStack, improvement))
			.map(improvementData -> improvementData.attributes)
			.map(map -> map.get(attribute))
			.map(AttributeHelper::getAdditionAmount)
			.orElse(0d);
	}
}
