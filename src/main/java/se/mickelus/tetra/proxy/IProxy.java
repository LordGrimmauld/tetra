package se.mickelus.tetra.proxy;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import se.mickelus.tetra.blocks.ITetraBlock;
import se.mickelus.tetra.items.ITetraItem;

public interface IProxy {

	void preInit(ITetraItem[] items, ITetraBlock[] blocks);

	void init(FMLCommonSetupEvent event, ITetraItem[] items, ITetraBlock[] blocks);

	void postInit();

	Player getClientPlayer();
}
