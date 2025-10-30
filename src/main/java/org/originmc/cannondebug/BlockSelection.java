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

import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.util.math.BlockPos;


public final class BlockSelection {
    private final int id;

    private final BlockPos location;

    private EntityTracker tracker;

    public BlockSelection(int id, BlockPos location) {
        this.tracker = null;
        this.id = id;
        this.location = location;
    }

    public void setTracker(EntityTracker tracker) {
        this.tracker = tracker;
    }

    public int getId() {
        return this.id;
    }

    public BlockPos getLocation() {
        return this.location;
    }

    public EntityTracker getTracker() {
        return this.tracker;
    }

    /**
     * Identifies whether or not a material can be selected using the block
     * selection mode.
     *
     * @param block the material to identify.
     * @return true if material can be selected.
     */
    public static boolean isSelectable(Block block) {
        return block instanceof FallingBlock || block instanceof DispenserBlock;
    }
}
