package se.mickelus.tetra.module.schematic;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolAction;
import se.mickelus.tetra.blocks.workbench.WorkbenchTile;
import se.mickelus.tetra.module.data.GlyphData;
import se.mickelus.tetra.module.data.MaterialMultiplier;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

public interface UpgradeSchematic {

	String getKey();

	String getName();

	String getDescription(@Nullable ItemStack itemStack);

	/**
	 * Used to display some information about how material properties translate into stats for the module or improvement that the schematic crafts.
	 *
	 * @return
	 */
	@Nullable
	default MaterialMultiplier getMaterialTranslation() {
		return null;
	}

	/**
	 * Used to display hints about which materials that can be used for this schematic, strings starting with # are considered as materials, others
	 * as item IDs.
	 */
	@Nullable
	default String[] getApplicableMaterials() {
		return null;
	}

	int getNumMaterialSlots();

	String getSlotName(final ItemStack itemStack, int index);

	default ItemStack[] getSlotPlaceholders(final ItemStack itemStack, int index) {
		return new ItemStack[0];
	}

	int getRequiredQuantity(final ItemStack itemStack, int index, ItemStack materialStack);

	/**
	 * Returns true if the provided materialStack is accepted in the slot at the given index, for the given upgrade
	 * target.
	 * A slot may accept a material without a craft actually being possible using the material.
	 *
	 * @param itemStack     the itemstack that is to be upgraded
	 * @param itemSlot
	 * @param index         the index of the slot
	 * @param materialStack the upgrade material
	 * @return
	 */
	boolean acceptsMaterial(ItemStack itemStack, String itemSlot, int index, ItemStack materialStack);

	/**
	 * Returns true if all material slot contain valid material of enough quantity. Craft is possible using the provided
	 * materials.
	 *
	 * @param itemStack the itemstack that is to be upgraded
	 * @param itemSlot
	 * @param materials the materials to be used for the upgrade
	 * @return
	 */
	boolean isMaterialsValid(ItemStack itemStack, String itemSlot, ItemStack[] materials);

	/**
	 * Returns true if this upgrade can be applied to the given item.
	 *
	 * @param itemStack the itemstack that is to be upgraded
	 * @return
	 */
	boolean isApplicableForItem(ItemStack itemStack);

	/**
	 * Returns true if this upgrade can be applied to the given slot on the given item.
	 *
	 * @param slot      the slot on which the schematic will be applied
	 * @param itemStack the itemstack that is to be upgraded
	 * @return
	 */
	boolean isApplicableForSlot(String slot, ItemStack itemStack);

	/**
	 * This is a final check if the player should be able to see the schematic in schematic listings, based on the player or its surroundings.
	 *
	 * @param player      The player
	 * @param tile
	 * @param targetStack The target itemstack for the schematic
	 * @return true if it should be visible, otherwise false
	 */
	default boolean isVisibleForPlayer(Player player, @Nullable WorkbenchTile tile, ItemStack targetStack) {
		return true;
	}

	/**
	 * Returns true if all criterias are met (e.g. correct materials & tools) and the upgrade can be performed.
	 *
	 * @param player         the player performing the upgrade
	 * @param itemStack      the itemstack that is to be upgraded
	 * @param materials      the materials to be used for the upgrade
	 * @param slot           the slot on which the schematic will be applied
	 * @param availableTools The tools that are available for use
	 * @return
	 */
	boolean canApplyUpgrade(Player player, ItemStack itemStack, ItemStack[] materials, String slot, Map<ToolAction, Integer> availableTools);

	boolean isIntegrityViolation(Player player, ItemStack itemStack, ItemStack[] materials, String slot);

	ItemStack applyUpgrade(ItemStack itemStack, ItemStack[] materials, boolean consumeMaterials, String slot, Player player);

	boolean checkTools(final ItemStack targetStack, final ItemStack[] materials, Map<ToolAction, Integer> availableTools);

	Map<ToolAction, Integer> getRequiredToolLevels(final ItemStack targetStack, final ItemStack[] materials);

	default Collection<ToolAction> getRequiredTools(final ItemStack targetStack, final ItemStack[] materials) {
		return getRequiredToolLevels(targetStack, materials).keySet();
	}

	default int getRequiredToolLevel(final ItemStack targetStack, final ItemStack[] materials, ToolAction toolAction) {
		return getRequiredToolLevels(targetStack, materials).getOrDefault(toolAction, 0);
	}

	default int getExperienceCost(final ItemStack targetStack, final ItemStack[] materials, String slot) {
		return 0;
	}

	/**
	 * Returns true if this is a honing schematic and should have it's usage regulated by honing rules.
	 *
	 * @return
	 */
	default boolean isHoning() {
		return false;
	}

	SchematicType getType();

	default SchematicRarity getRarity() {
		return SchematicRarity.basic;
	}

	GlyphData getGlyph();

	OutcomePreview[] getPreviews(ItemStack targetStack, String slot);

	/**
	 * @param itemStack
	 * @param materials
	 * @param slot
	 * @return The severity of the upgrade, e.g. replacing a major module with lots of improvements using a high tier tool would be more
	 * severe than applying simple binding improvement to a handle. Used by things like destabilization as multiplier for the probability
	 * of a destabilization to occur.
	 */
	default float getSeverity(ItemStack itemStack, ItemStack[] materials, String slot) {
		return 1;
	}

	/**
	 * Denotes if this upgrade will replace the current module. True even if there currently is no module in the slot.
	 *
	 * @param itemStack
	 * @param materials
	 * @param slot
	 * @return
	 */
	default boolean willReplace(ItemStack itemStack, ItemStack[] materials, String slot) {
		return false;
	}
}
