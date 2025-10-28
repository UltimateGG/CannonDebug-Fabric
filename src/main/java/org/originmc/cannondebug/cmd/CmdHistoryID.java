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
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugPlugin;
import org.originmc.cannondebug.EntityTracker;
import org.originmc.cannondebug.FancyPager;
import org.originmc.cannondebug.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.text.ClickEvent.Action.RUN_COMMAND;
import static net.minecraft.text.HoverEvent.Action.SHOW_TEXT;

public final class CmdHistoryID extends CommandExecutor {

    public CmdHistoryID(CannonDebugPlugin plugin, ServerCommandSource sender, String[] args, String permission) {
        super(plugin, sender, args, permission);
    }

    @Override
    public boolean perform() {
        // Do nothing if the command has invalid arguments.
        if (args.length == 2) return false;

        // Do nothing if the user input an invalid id.
        int id = Math.abs(NumberUtils.parseInt(args[2]));
        BlockSelection selection = user.getSelection(id);
        if (selection == null) {
            sender.sendMessage(Text.literal("You have input an invalid id!").formatted(Formatting.RED));
            return true;
        }

        // Generate a new fancy message line to add to the pager.
        List<Text> lines = new ArrayList<>();
        EntityTracker tracker = selection.getTracker();
        int lifespan = tracker.getLocationHistory().size();
        Vec3d initial = tracker.getLocationHistory().get(0);

        for (int i = 0; i < lifespan; i++) {
            Vec3d location = tracker.getLocationHistory().get(i);
            Vec3d velocity = tracker.getVelocityHistory().get(i);

            // --- Build hover tooltip for tick info ---
            Text tickTooltip = Text.empty()
                    .append(Text.literal("Click for all history on this tick.\n").formatted(Formatting.DARK_AQUA, Formatting.BOLD))
                    .append(Text.literal("Server tick: ").formatted(Formatting.YELLOW))
                    .append(Text.literal(String.valueOf(tracker.getSpawnTick() + i)).formatted(Formatting.LIGHT_PURPLE))
                    .append(Text.literal("\nSpawned tick: ").formatted(Formatting.YELLOW))
                    .append(Text.literal(String.valueOf(tracker.getSpawnTick())).formatted(Formatting.AQUA))
                    .append(Text.literal("\nDeath tick: ").formatted(Formatting.YELLOW))
                    .append(Text.literal(tracker.getDeathTick() == -1 ? "Still alive" : String.valueOf(tracker.getDeathTick()))
                            .formatted(Formatting.RED))
                    .append(Text.literal("\nCached tick: ").formatted(Formatting.YELLOW))
                    .append(Text.literal(String.valueOf(plugin.getCurrentTick())).formatted(Formatting.GREEN))
                    .append(Text.literal("\nInitial Location: ").formatted(Formatting.YELLOW))
                    .append(Text.literal((int)initial.getX() + " " + (int)initial.getY() + " " + (int)initial.getZ())
                            .formatted(Formatting.GRAY));

            // --- Build hover tooltip for location + velocity ---
            Text hoverLocVel = Text.empty()
                    .append(Text.literal("Click to teleport to location.\n").formatted(Formatting.DARK_AQUA, Formatting.BOLD))
                    .append(Text.literal("LOCATION\n").formatted(Formatting.YELLOW, Formatting.BOLD))
                    .append(Text.literal("X: ").formatted(Formatting.WHITE))
                    .append(Text.literal(String.valueOf(location.getX())).formatted(Formatting.RED))
                    .append(Text.literal("\nY: ").formatted(Formatting.WHITE))
                    .append(Text.literal(String.valueOf(location.getY())).formatted(Formatting.RED))
                    .append(Text.literal("\nZ: ").formatted(Formatting.WHITE))
                    .append(Text.literal(String.valueOf(location.getZ())).formatted(Formatting.RED))
                    .append(Text.literal("\n\nVELOCITY\n").formatted(Formatting.YELLOW, Formatting.BOLD))
                    .append(Text.literal("X: ").formatted(Formatting.WHITE))
                    .append(Text.literal(String.valueOf(velocity.getX())).formatted(Formatting.RED))
                    .append(Text.literal("\nY: ").formatted(Formatting.WHITE))
                    .append(Text.literal(String.valueOf(velocity.getY())).formatted(Formatting.RED))
                    .append(Text.literal("\nZ: ").formatted(Formatting.WHITE))
                    .append(Text.literal(String.valueOf(velocity.getZ())).formatted(Formatting.RED));

            // --- Build main interactive message ---
            int finalI = i;
            Text line = Text.empty()
                    .append(Text.literal("Tick: " + i + " ")
                            .formatted(Formatting.GRAY)
                            .styled(s -> s
                                    .withClickEvent(new ClickEvent(RUN_COMMAND, "/cannondebug h t " + (tracker.getSpawnTick() + finalI)))
                                    .withHoverEvent(new HoverEvent(SHOW_TEXT, tickTooltip))
                    ))

                    // Label for hoverable section
                    .append(
                            Text.empty()
                            // Entity name
                            .append(tracker.getEntityType().getName().copy().formatted(Formatting.YELLOW))

                            // Separator
                            .append(Text.literal(" | ").formatted(Formatting.DARK_GRAY))
                            .append(
                                Text.literal("Hover for location and velocity")
                                .formatted(Formatting.WHITE)
                            )
                            .styled(s -> s
                                    .withClickEvent(new ClickEvent(RUN_COMMAND, "/cannondebug tp " + id + " " + finalI))
                                    .withHoverEvent(new HoverEvent(SHOW_TEXT, hoverLocVel))
                            ))
                    ;

            lines.add(line);
        }

        FancyPager pager = new FancyPager(
                "History for selection ID: " + id,
                lines.toArray(Text[]::new)
        );
        send(pager, 0);
        return true;
    }

}
