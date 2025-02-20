package se.mickelus.tetra.util;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ItemHandlerWrapper implements Container {

	protected final IItemHandler inv;

	public ItemHandlerWrapper(IItemHandler inv) {
		this.inv = inv;
	}

	/**
	 * Returns the size of this inventory.
	 */
	@Override
	public int getContainerSize() {
		return inv.getSlots();
	}

	/**
	 * Returns the stack in this slot.  This stack should be a modifiable reference, not a copy of a stack in your inventory.
	 */
	@Override
	public ItemStack getItem(int slot) {
		return inv.getStackInSlot(slot);
	}

	/**
	 * Attempts to remove n items from the specified slot.  Returns the split stack that was removed.  Modifies the inventory.
	 */
	@Override
	public ItemStack removeItem(int slot, int count) {
		ItemStack stack = inv.getStackInSlot(slot);
		return stack.isEmpty() ? ItemStack.EMPTY : stack.split(count);
	}

	/**
	 * Sets the contents of this slot to the provided stack.
	 */
	@Override
	public void setItem(int slot, ItemStack stack) {
		inv.insertItem(slot, stack, false);
	}

	/**
	 * Removes the stack contained in this slot from the underlying handler, and returns it.
	 */
	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack s = getItem(index);
		if (s.isEmpty()) return ItemStack.EMPTY;
		setItem(index, ItemStack.EMPTY);
		return s;
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < inv.getSlots(); i++) {
			if (!inv.getStackInSlot(i).isEmpty()) return false;
		}
		return true;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return inv.isItemValid(slot, stack);
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < inv.getSlots(); i++) {
			inv.extractItem(i, 64, false);
		}
	}

	//The following methods are never used by vanilla in crafting.  They are defunct as mods need not override them.
	@Override
	public int getMaxStackSize() {
		return 0;
	}

	@Override
	public void setChanged() {
	}

	@Override
	public boolean stillValid(Player player) {
		return false;
	}

	@Override
	public void startOpen(Player player) {
	}

	@Override
	public void stopOpen(Player player) {
	}
}
