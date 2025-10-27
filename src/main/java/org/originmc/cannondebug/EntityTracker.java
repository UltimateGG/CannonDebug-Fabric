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

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;

public final class EntityTracker {
    private final EntityType<?> entityType;

    private final long spawnTick;

    private final ArrayList<Vec3d> locationHistory;

    private final ArrayList<Vec3d> velocityHistory;

    private long deathTick;

    private Entity entity;

    public EntityTracker(EntityType<?> entityType, long spawnTick) {
        this.locationHistory = new ArrayList<>();
        this.velocityHistory = new ArrayList<>();
        this.deathTick = -1L;
        this.entityType = entityType;
        this.spawnTick = spawnTick;
    }

    public void setDeathTick(long deathTick) {
        this.deathTick = deathTick;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public EntityType<?> getEntityType() {
        return this.entityType;
    }

    public long getSpawnTick() {
        return this.spawnTick;
    }

    public ArrayList<Vec3d> getLocationHistory() {
        return this.locationHistory;
    }

    public ArrayList<Vec3d> getVelocityHistory() {
        return this.velocityHistory;
    }

    public long getDeathTick() {
        return this.deathTick;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
