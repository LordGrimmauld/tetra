package se.mickelus.tetra.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import se.mickelus.tetra.ConfigHandler;
import se.mickelus.tetra.blocks.ITetraBlock;
import se.mickelus.tetra.blocks.salvage.InteractiveBlockOverlay;
import se.mickelus.tetra.blocks.scroll.ScrollItem;
import se.mickelus.tetra.blocks.scroll.ScrollRenderer;
import se.mickelus.tetra.blocks.scroll.ScrollTile;
import se.mickelus.tetra.blocks.workbench.WorkbenchTESR;
import se.mickelus.tetra.blocks.workbench.WorkbenchTile;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchScreen;
import se.mickelus.tetra.compat.botania.BotaniaCompat;
import se.mickelus.tetra.effect.gui.AbilityOverlays;
import se.mickelus.tetra.effect.howling.HowlingOverlay;
import se.mickelus.tetra.generation.ExtendedStructureTESR;
import se.mickelus.tetra.items.ITetraItem;
import se.mickelus.tetra.properties.ReachEntityFix;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@ParametersAreNonnullByDefault
public class ClientProxy implements IProxy {

	@Override
	public void preInit(ITetraItem[] items, ITetraBlock[] blocks) {
	}

	@Override
	public void init(FMLCommonSetupEvent event, ITetraItem[] items, ITetraBlock[] blocks) {
		Arrays.stream(items).forEach(ITetraItem::clientInit);
		Arrays.stream(blocks).forEach(ITetraBlock::clientInit);

		// these are registered here as there are multiple instances of workbench blocks
		BlockEntityRenderers.register(WorkbenchTile.type, WorkbenchTESR::new);
		MenuScreens.register(WorkbenchTile.containerType, WorkbenchScreen::new);
		BlockEntityRenderers.register(ScrollTile.type, ScrollRenderer::new);

		MinecraftForge.EVENT_BUS.register(new HowlingOverlay(Minecraft.getInstance()));
		MinecraftForge.EVENT_BUS.register(new AbilityOverlays(Minecraft.getInstance()));

		if (ConfigHandler.development.get()) {
			BlockEntityRenderers.register(BlockEntityType.STRUCTURE_BLOCK, ExtendedStructureTESR::new);
		}

		BotaniaCompat.clientInit();

		MinecraftForge.EVENT_BUS.register(ReachEntityFix.class);
	}

	@Override
	public void postInit() {
		MinecraftForge.EVENT_BUS.register(new InteractiveBlockOverlay());
		ScrollItem.clientPostInit();
	}

	@Override
	public Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}
}
