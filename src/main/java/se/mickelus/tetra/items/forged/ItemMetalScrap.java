package se.mickelus.tetra.items.forged;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ObjectHolder;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.blocks.forged.ForgedBlockCommon;
import se.mickelus.tetra.items.TetraItem;
import se.mickelus.tetra.items.TetraItemGroup;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ItemMetalScrap extends TetraItem {
	private static final String unlocalizedName = "metal_scrap";
	@ObjectHolder(TetraMod.MOD_ID + ":" + unlocalizedName)
	public static ItemMetalScrap instance;

	public ItemMetalScrap() {
		super(new Properties().tab(TetraItemGroup.instance));
		setRegistryName(unlocalizedName);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(new TranslatableComponent("item.tetra.metal_scrap.description").withStyle(ChatFormatting.GRAY));
		tooltip.add(new TextComponent(" "));
		tooltip.add(ForgedBlockCommon.locationTooltip);
	}
}
