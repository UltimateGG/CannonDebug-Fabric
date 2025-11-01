package org.originmc.cannondebug.listener;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugPlugin;
import org.originmc.cannondebug.EntityTracker;
import org.originmc.cannondebug.User;


public class WorldListener {

    private final CannonDebugPlugin plugin;

    public WorldListener(CannonDebugPlugin plugin) {
        this.plugin = plugin;

        DispenserBlock.registerBehavior(Blocks.TNT, new ItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                ServerWorld world = pointer.getWorld();
                BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                TntEntity tntEntity = new TntEntity(world, (double) blockPos.getX() + 0.5, blockPos.getY(), (double) blockPos.getZ() + 0.5, null);
                onTNTDispensed(pointer.getPos(), tntEntity);
                world.spawnEntity(tntEntity);
                world.playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.emitGameEvent(null, GameEvent.ENTITY_PLACE, blockPos);
                stack.decrement(1);
                return stack;
            }
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof FallingBlockEntity)
                onFallingBlockSpawn((FallingBlockEntity) entity, world);
        });
    }

    private void onTNTDispensed(BlockPos dispenser, TntEntity entity) {
        // Loop through each user profile.
        BlockSelection selection;
        EntityTracker tracker = null;
        for (User user : plugin.getUsers().values()) {
            // Do nothing if user is not attempting to profile current block.
            selection = user.getSelection(dispenser);
            if (selection == null) {
                continue;
            }

            // Build a new tracker due to it being used.
            if (tracker == null) {
                tracker = new EntityTracker((ServerWorld) entity.getWorld(), entity.getType(), plugin.getCurrentTick());
                tracker.setEntity(entity);
                plugin.getActiveTrackers().add(tracker);
            }

            // Add block tracker to user.
            selection.setTracker(tracker);
        }
    }

    private void onFallingBlockSpawn(FallingBlockEntity entity, ServerWorld world) {
        // Loop through each user profile.
        BlockSelection selection;
        EntityTracker tracker = null;
        for (User user : plugin.getUsers().values()) {
            // Do nothing if user is not attempting to profile current block.
            selection = user.getSelection(entity.getBlockPos());
            if (selection == null) {
                continue;
            }

            // Build a new tracker due to it being used.
            if (tracker == null) {
                tracker = new EntityTracker(world, entity.getType(), plugin.getCurrentTick());
                tracker.setEntity(entity);
                plugin.getActiveTrackers().add(tracker);
            }

            // Add block tracker to user.
            selection.setTracker(tracker);
        }
    }
}
