package org.originmc.cannondebug.listener;

import org.originmc.cannondebug.BlockSelection;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.originmc.cannondebug.CannonDebugPlugin;
import org.originmc.cannondebug.User;

public class PlayerListener {

    private final CannonDebugPlugin plugin;

    public PlayerListener(CannonDebugPlugin plugin) {
        this.plugin = plugin;

        ServerPlayConnectionEvents.JOIN.register(this::createUser);
        ServerPlayConnectionEvents.DISCONNECT.register(this::deleteUser);
        UseBlockCallback.EVENT.register(this::onUseBlock);
        AttackBlockCallback.EVENT.register(this::onAttackBlock);
    }

    public void createUser(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        plugin.getUsers().put(handler.getPlayer().getUuid(), new User(handler.getPlayer()));
    }

    public void deleteUser(ServerPlayNetworkHandler handler, MinecraftServer server) {
        plugin.getUsers().remove(handler.getPlayer().getUuid());
    }

    public ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult blockHitResult) {
        // Do nothing if the player has no user profile attached.
        User user = plugin.getUser(player.getUuid());
        if (user == null) return ActionResult.PASS;

        // Do nothing if the user is not selecting.
        if (!user.isSelecting()) return ActionResult.PASS;

        // Fail to prevent opening dispenser in preview mode
        if (hand != Hand.MAIN_HAND) return ActionResult.FAIL;

        // Do nothing if not a selectable block.
        if (!BlockSelection.isSelectable(world.getBlockState(blockHitResult.getBlockPos()).getBlock()))
            return ActionResult.PASS;

        BlockState block = world.getBlockState(blockHitResult.getBlockPos());
        plugin.handleSelection(user, blockHitResult.getBlockPos(), block, world.getServer());

        // Cancel the event.
        return ActionResult.SUCCESS;
    }

    public ActionResult onAttackBlock(PlayerEntity player, World world, Hand hand, BlockPos blockPos, Direction direction) {
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;

        // Do nothing if not a selectable block.
        if (!BlockSelection.isSelectable(world.getBlockState(blockPos).getBlock())) return ActionResult.PASS;

        // Do nothing if the player has no user profile attached.
        User user = plugin.getUser(player.getUuid());
        if (user == null) return ActionResult.PASS;

        // Do nothing if the user is not selecting.
        if (!user.isSelecting()) return ActionResult.PASS;

        BlockState block = world.getBlockState(blockPos);
        plugin.handleSelection(user, blockPos, block, world.getServer());

        // Cancel the event.
        return ActionResult.SUCCESS;
    }

}
