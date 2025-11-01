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

package org.originmc.cannondebug;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.originmc.cannondebug.cmd.CommandType;
import org.originmc.cannondebug.listener.PlayerListener;
import org.originmc.cannondebug.listener.WorldListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class CannonDebugPlugin {
    // Arbitrary and most users will never bump into these
    public static final int MAX_SELECTIONS = 300;
    public static final int MAX_WORLDEDIT_VOLUME = 5_000;

    private final Map<UUID, User> users = new HashMap<>();

    private final List<EntityTracker> activeTrackers = new ArrayList<>();

    private long currentTick = 0;

    public Map<UUID, User> getUsers() {
        return users;
    }

    public User getUser(UUID playerId) {
        // Return null if the player id has no user profile attached.
        if (!users.containsKey(playerId)) return null;

        // Return the user.
        return users.get(playerId);
    }

    public List<EntityTracker> getActiveTrackers() {
        return activeTrackers;
    }

    public long getCurrentTick() {
        return currentTick;
    }

    public void init() {
        new PlayerListener(this);
        new WorldListener(this);

        ServerTickEvents.END_SERVER_TICK.register(server -> onServerTick());

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    CommandManager.literal("c")
                            .executes(ctx -> runLegacyCommand(ctx.getSource(), "c", new String[0]))
                            .then(CommandManager.argument("args", StringArgumentType.greedyString())
                                    .executes(ctx -> {
                                        String allArgs = StringArgumentType.getString(ctx, "args");
                                        String[] splitArgs = allArgs.split(" ");
                                        return runLegacyCommand(ctx.getSource(), "c", splitArgs);
                                    })
                            )
            );
            dispatcher.register(
                    CommandManager.literal("cannondebug")
                            .executes(ctx -> runLegacyCommand(ctx.getSource(), "c", new String[0]))
                            .then(CommandManager.argument("args", StringArgumentType.greedyString())
                                    .executes(ctx -> {
                                        String allArgs = StringArgumentType.getString(ctx, "args");
                                        String[] splitArgs = allArgs.split(" ");
                                        return runLegacyCommand(ctx.getSource(), "c", splitArgs);
                                    })
                            )
            );
        }));
    }

    public void onServerTick() {
        // Loop through every active tracker.
        Iterator<EntityTracker> iterator = activeTrackers.iterator();
        while (iterator.hasNext()) {
            // Add new location and velocity to the tracker histories.
            EntityTracker tracker = iterator.next();
            tracker.getLocationHistory().add(tracker.getEntity().getPos());
            tracker.getVelocityHistory().add(tracker.getEntity().getVelocity());

            // Remove dead entities from tracker.
            if (tracker.getEntity().isRemoved()) {
                tracker.setDeathTick(currentTick);
                tracker.setEntity(null);
                iterator.remove();
            }
        }

        // Increment the tick counter.
        currentTick++;
    }

    public int runLegacyCommand(ServerCommandSource sender, String commandLabel, String[] args) {
        boolean success = CommandType.fromCommand(this, sender, args).execute();

        if (!success)
            sender.sendMessage(Text.literal("Incorrect syntax. For help use /cannondebug").formatted(Formatting.RED));

        return success ? 1 : 0;
    }

    /**
     * Attempts to either add or remove a selection depending on whether or not
     * the user already had this position set.
     *
     * @param user  the user that is adding to their selection.
     * @param block the block to select.
     */
    public void handleSelection(User user, BlockPos pos, BlockState block, MinecraftServer server) {
        // Do nothing if block is not selectable.
        if (!BlockSelection.isSelectable(block.getBlock())) return;

        // Attempt to deselect block if it is already selected.
        BlockSelection selection = user.getSelection(pos);
        ServerPlayerEntity player = user.getPlayer();
        if (selection != null) {
            // Inform the player.
            player.sendMessage(Text.empty()
                    .append(Text.literal("REM ").formatted(Formatting.RED, Formatting.BOLD))
                    .append(Text.literal(block.getBlock().getName().getString() + " @ ").formatted(Formatting.WHITE))
                    .append(Text.literal(pos.getX() + " " + pos.getY() + " " + pos.getZ() + " ").formatted(Formatting.WHITE))
                    .append(Text.literal("ID: " + selection.getId()).formatted(Formatting.GRAY))
            );

            // Remove the clicked location.
            user.getSelections().remove(selection);

            // Update users preview.
            if (user.isPreviewing()) {
                BlockSelection finalSelection = selection;
                server.execute(() -> {
                    player.networkHandler.sendPacket(
                        new BlockUpdateS2CPacket(finalSelection.getLocation(), player.getWorld().getBlockState(finalSelection.getLocation()))
                    );
                });
            }
            return;
        }

        // Do nothing if the user has too many selections.
        if (user.getSelections().size() >= MAX_SELECTIONS) {
            player.sendMessage(
                    Text.literal("You have too many selections! ")
                            .formatted(Formatting.RED)
                            .append(Text.literal("(Max = " + MAX_SELECTIONS + ")").formatted(Formatting.GRAY))
            );
            return;
        }

        // Update users preview.
        if (user.isPreviewing()) {
            server.execute(() -> {
                 player.networkHandler.sendPacket(
                    new BlockUpdateS2CPacket(pos, Blocks.EMERALD_BLOCK.getDefaultState())
                );
            });
        }

        // Add the selected location.
        selection = user.addSelection(pos);

        // Inform the player.
        player.sendMessage(Text.empty()
                .append(Text.literal("ADD ").formatted(Formatting.GREEN, Formatting.BOLD))
                .append(Text.literal(block.getBlock().getName().getString() + " @ ").formatted(Formatting.WHITE))
                .append(Text.literal(pos.getX() + " " + pos.getY() + " " + pos.getZ() + " ").formatted(Formatting.WHITE))
                .append(Text.literal("ID: " + selection.getId()).formatted(Formatting.GRAY))
        );
    }

}
