/*
 * This file is part of CannonProfiler, licensed under the MIT License (MIT).
 *
 * Copyright (c) Origin <http://www.originmc.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.originmc.cannondebug.cmd;


import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.originmc.cannondebug.CannonDebugPlugin;
import net.minecraft.server.command.ServerCommandSource;
import org.originmc.cannondebug.User;

import java.util.Objects;


public final class RegionExecute {
	public static boolean perform(CannonDebugPlugin plugin, User user, ServerCommandSource sender) {
		// Do nothing if WorldEdit is not installed.
		if (!FabricLoader.getInstance().isModLoaded("worldedit")) {
			sender.sendMessage(Text.literal("WorldEdit was not found on this server!").formatted(Formatting.RED));
			return true;
		}

		try {
			// Do nothing if selection is not a cuboid.
			WorldEdit worldEdit = WorldEdit.getInstance();
			Player wePlayer = FabricAdapter.adaptPlayer(Objects.requireNonNull(sender.getPlayer()));
			LocalSession session = worldEdit.getSessionManager().get(wePlayer);
			CuboidRegion selection = session.getSelection().getBoundingBox();

			// Do nothing if selection is too large.
			if (selection.getVolume() > CannonDebugPlugin.MAX_WORLDEDIT_VOLUME) {
				sender.sendMessage(
						Text.empty()
								.append(Text.literal("Region selected is too large! ").formatted(Formatting.RED))
								.append(Text.literal("(Max area = " + CannonDebugPlugin.MAX_WORLDEDIT_VOLUME + " blocks)").formatted(Formatting.GRAY))
				);
				return true;
			}

			// Handle selection for all blocks within this region.
			BlockVector3 max = selection.getMaximumPoint();
			BlockVector3 min = selection.getMinimumPoint();
			for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
				for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
					for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
						BlockPos pos = new BlockPos(x, y, z);
						plugin.handleSelection(user, pos, sender.getPlayer().getWorld().getBlockState(pos), sender.getServer());
					}
				}
			}

			// Send complete message.
			sender.sendMessage(Text.literal("All possible selections have been toggled.").formatted(Formatting.YELLOW));
			return true;
		} catch (Exception e) {
			sender.sendMessage(Text.literal("WorldEdit selection incomplete!").formatted(Formatting.RED));
			return true;
		}
	}
}
