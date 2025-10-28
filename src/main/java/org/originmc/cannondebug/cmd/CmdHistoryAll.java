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

import net.minecraft.server.command.ServerCommandSource;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugPlugin;
import org.originmc.cannondebug.EntityTracker;
import org.originmc.cannondebug.FancyPager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.text.ClickEvent.Action.RUN_COMMAND;
import static net.minecraft.text.HoverEvent.Action.SHOW_TEXT;


public final class CmdHistoryAll extends CommandExecutor {

    public CmdHistoryAll(CannonDebugPlugin plugin, ServerCommandSource sender, String[] args, String permission) {
        super(plugin, sender, args, permission);
    }

    @Override
    public boolean perform() {
        // Generate fancy message lines for all new message data.
        List<Text> lines = new ArrayList<>();
        for (BlockSelection selection : user.getSelections()) {
            // Do nothing if tracker has not been spawned for this selection yet.
            EntityTracker tracker = selection.getTracker();
            if (tracker == null) continue;

            // Generate a new fancy message line to add to the pager.
            Vec3d initial = tracker.getLocationHistory().get(0);
            int latestTick = tracker.getLocationHistory().size() - 1;
            Vec3d latest = tracker.getLocationHistory().get(latestTick);

            // === Build hover tooltip ===
            Text tooltip = Text.empty()
                    .append(Text.literal("Click for all history on this ID.\n").formatted(Formatting.DARK_AQUA, Formatting.BOLD))
                    .append(Text.literal("Spawned tick: ").formatted(Formatting.YELLOW))
                    .append(Text.literal(String.valueOf(tracker.getSpawnTick())).formatted(Formatting.AQUA))
                    .append(Text.literal("\nDeath tick: ").formatted(Formatting.YELLOW))
                    .append(Text.literal(
                            tracker.getDeathTick() == -1 ? "Still alive" : String.valueOf(tracker.getDeathTick())
                    ).formatted(Formatting.RED))
                    .append(Text.literal("\nCached tick: ").formatted(Formatting.YELLOW))
                    .append(Text.literal(String.valueOf(plugin.getCurrentTick())).formatted(Formatting.GREEN))
                    .append(Text.literal("\nInitial Location: ").formatted(Formatting.YELLOW))
                    .append(Text.literal((int)initial.getX() + " " + (int)initial.getY() + " " + (int)initial.getZ())
                            .formatted(Formatting.GRAY));

            Text tpTooltip = Text.literal("Click to teleport to location.").formatted(Formatting.DARK_AQUA, Formatting.BOLD);

            // === Build main clickable line ===
            Text line = Text.literal("ID: " + selection.getId() + " ")
                    .formatted(Formatting.GRAY)
                    .styled(s -> s
                            .withClickEvent(new ClickEvent(RUN_COMMAND, "/cannondebug h i " + selection.getId()))
                            .withHoverEvent(new HoverEvent(SHOW_TEXT, tooltip))
                    )

                    .append(
                            Text.empty()
                            // Entity name
                            .append(tracker.getEntityType().getName().copy()
                                    .formatted(Formatting.YELLOW))

                            // Separator
                            .append(Text.literal(" | ").formatted(Formatting.DARK_GRAY))

                            // Label
                            .append(
                                Text.literal("Last location: ").formatted(Formatting.WHITE)

                                // Coordinates
                                .append(
                                    Text.literal((int)latest.getX() + " " + (int)latest.getY() + " " + (int)latest.getZ())
                                    .formatted(Formatting.RED)
                                ).styled(s -> s
                                    .withHoverEvent(new HoverEvent(SHOW_TEXT, tpTooltip))
                                    .withClickEvent(new ClickEvent(RUN_COMMAND, "/cannondebug tp " + selection.getId() + " " + latestTick))
                                )
                            )
                    );

            lines.add(line);
        }

        // === Create the pager ===
        FancyPager pager = new FancyPager("All Latest History", lines.toArray(Text[]::new));
        send(pager, 0);
        return true;
    }

}
