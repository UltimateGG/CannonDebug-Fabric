package org.originmc.cannondebug.cmd;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugPlugin;
import org.originmc.cannondebug.EntityTracker;
import org.originmc.cannondebug.utils.NumberUtils;

import java.util.Objects;


public class CmdTP extends CommandExecutor {

	public CmdTP(CannonDebugPlugin plugin, ServerCommandSource sender, String[] args, String permission) {
		super(plugin, sender, args, permission);
	}

	@Override
	public boolean perform() {
		if (args.length != 3) return false;

		// Do nothing if the user input an invalid id.
		int id = Math.abs(NumberUtils.parseInt(args[1]));
		BlockSelection selection = user.getSelection(id);
		if (selection == null) {
			sender.sendMessage(Text.literal("You have input an invalid id!").formatted(Formatting.RED));
			return true;
		}

		EntityTracker tracker = selection.getTracker();
		if (tracker == null) {
			sender.sendMessage(Text.literal("No data tracked for that id yet!").formatted(Formatting.RED));
			return true;
		}

		// Do nothing if the user input an invalid tick.
		int tick = Math.abs(NumberUtils.parseInt(args[2]));
		Vec3d location;
		try {
		 location = tracker.getLocationHistory().get(tick);
		} catch (IndexOutOfBoundsException e) {
			sender.sendMessage(Text.literal("You have input an invalid tick!").formatted(Formatting.RED));
			return true;
		}

		Objects.requireNonNull(sender.getPlayer()).teleport(
				tracker.getWorld(),
				location.x,
				location.y,
				location.z,
				sender.getPlayer().getYaw(),
				sender.getPlayer().getPitch()
		);

		return true;
	}

}