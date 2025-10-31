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

import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugPlugin;

public final class CmdPreview extends CommandExecutor {

    public CmdPreview(CannonDebugPlugin plugin, ServerCommandSource sender, String[] args, String permission) {
        super(plugin, sender, args, permission);
    }

    @Override
    public boolean perform() {
        // Check if user is previewing block.
        boolean preview = !user.isPreviewing();
        if (args.length > 1) {
            preview = Boolean.parseBoolean(args[1]);
        }

        // Set the users previewing state.
        user.setPreviewing(preview);

        // Show player blocks if the preview is on, otherwise reveal all previewed blocks.
        if (preview) {
            previewOn();
        } else {
            previewOff();
        }
        return true;
    }

    private void previewOn() {
        ServerPlayerEntity player = sender.getPlayer();
        assert player != null;

        for (BlockSelection selection : user.getSelections()) {
			player.networkHandler.sendPacket(
                new BlockUpdateS2CPacket(selection.getLocation(), Blocks.EMERALD_BLOCK.getDefaultState())
            );
        }
        sender.sendMessage(Text.literal("Preview mode now enabled.").formatted(Formatting.YELLOW));
    }

    private void previewOff() {
        ServerPlayerEntity player = sender.getPlayer();
        assert player != null;

        for (BlockSelection selection : user.getSelections()) {
            player.networkHandler.sendPacket(
                new BlockUpdateS2CPacket(selection.getLocation(), player.getWorld().getBlockState(selection.getLocation()))
            );
        }
        sender.sendMessage(Text.literal("Preview mode now disabled.").formatted(Formatting.YELLOW));
    }

}
