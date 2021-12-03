package se.mickelus.tetra.blocks.workbench;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import se.mickelus.tetra.network.BlockPosPacket;
import se.mickelus.tetra.util.CastOptional;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
public class WorkbenchPacketTweak extends BlockPosPacket {

	String slot;
	Map<String, Integer> tweaks;

	public WorkbenchPacketTweak() {
		tweaks = new HashMap<>();
	}

	public WorkbenchPacketTweak(BlockPos pos, String slot, Map<String, Integer> tweaks) {
		super(pos);

		this.slot = slot;
		this.tweaks = tweaks;
	}

	@Override
	public void toBytes(FriendlyByteBuf buffer) {
		super.toBytes(buffer);

		try {
			writeString(slot, buffer);
		} catch (IOException e) {
			System.err.println("An error occurred when writing tweak packet to buffer");
		}
		buffer.writeInt(tweaks.size());
		tweaks.forEach((tweakKey, step) -> {
			try {
				writeString(tweakKey, buffer);
				buffer.writeInt(step);
			} catch (IOException e) {
				System.err.println("An error occurred when writing tweak packet to buffer");
			}
		});
	}

	@Override
	public void fromBytes(FriendlyByteBuf buffer) {
		super.fromBytes(buffer);

		try {
			slot = readString(buffer);
			int size = buffer.readInt();
			for (int i = 0; i < size; i++) {
				tweaks.put(readString(buffer), buffer.readInt());
			}
		} catch (IOException e) {
			System.err.println("An error occurred when reading tweak packet from buffer");
		}
	}

	@Override
	public void handle(Player player) {
		CastOptional.cast(player.level.getBlockEntity(pos), WorkbenchTile.class)
			.ifPresent(workbench -> workbench.tweak(player, slot, tweaks));
	}
}
