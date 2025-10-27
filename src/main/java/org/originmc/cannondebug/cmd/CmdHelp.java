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

import org.originmc.cannondebug.FancyPager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.originmc.cannondebug.CannonDebugPlugin;

public final class CmdHelp extends CommandExecutor {

    private static final FancyPager HELP_PAGER = new FancyPager(
            "Help for command \"/c\"",
            Text.literal("/c c,clear ").formatted(Formatting.AQUA)
                    .append(Text.literal("[history,h,selections,s] ").formatted(Formatting.DARK_AQUA))
                    .append(Text.literal("Clear history or selections.").formatted(Formatting.YELLOW)),

            Text.literal("/c ?,help ").formatted(Formatting.AQUA)
                    .append(Text.literal("Displays this plugin's main help page.").formatted(Formatting.YELLOW)),

            Text.literal("/c h,l,history,lookup ").formatted(Formatting.AQUA)
                    .append(Text.literal("[?,params] ").formatted(Formatting.DARK_AQUA))
                    .append(Text.literal("Lists latest profiling history.").formatted(Formatting.YELLOW)),

            Text.literal("/c p,page ").formatted(Formatting.AQUA)
                    .append(Text.literal("[page] ").formatted(Formatting.DARK_AQUA))
                    .append(Text.literal("Go to specific page for current pager.").formatted(Formatting.YELLOW)),

            Text.literal("/c v,pre,view,preview ").formatted(Formatting.AQUA)
                    .append(Text.literal("Preview all selected blocks.").formatted(Formatting.YELLOW)),

            Text.literal("/c r,region ").formatted(Formatting.AQUA)
                    .append(Text.literal("Select all available blocks in WorldEdit region.").formatted(Formatting.YELLOW)),

            Text.literal("/c s,select ").formatted(Formatting.AQUA)
                    .append(Text.literal("Bind block selector tool to hand.").formatted(Formatting.YELLOW)),

            Text.empty(),
            Text.literal("This mod provides an easy way to profile cannons.").formatted(Formatting.GREEN),
            Text.empty(),
            Text.literal("It lets you select sand or dispensers around your cannon. The falling or fired entities are profiled each tick.")
                    .formatted(Formatting.GREEN),
            Text.empty(),
            Text.literal("All data like velocities and locations can be accessed via lookup commands. The results are indexed for clarity.")
                    .formatted(Formatting.GREEN)
    );

    public CmdHelp(CannonDebugPlugin plugin, ServerCommandSource sender, String[] args, String permission) {
        super(plugin, sender, args, permission);
    }

    @Override
    public boolean perform() {
        // Send the sender this plugins' help message.
        send(HELP_PAGER, 0);
        return true;
    }

}
