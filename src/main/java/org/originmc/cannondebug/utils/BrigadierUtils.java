package org.originmc.cannondebug.utils;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Locale;


public class BrigadierUtils {
    public static LiteralCommandNode<ServerCommandSource> buildRedirect(
        final String alias, final LiteralCommandNode<ServerCommandSource> destination) {
        // Redirects only work for nodes with children, but break the top argument-less command.
        // Manually adding the root command after setting the redirect doesn't fix it.
        // See https://github.com/Mojang/brigadier/issues/46). Manually clone the node instead.
        LiteralArgumentBuilder<ServerCommandSource> builder = LiteralArgumentBuilder
            .<ServerCommandSource>literal(alias.toLowerCase(Locale.ENGLISH))
            .requires(destination.getRequirement())
            .forward(
                destination.getRedirect(), destination.getRedirectModifier(), destination.isFork())
            .executes(destination.getCommand());
        for (CommandNode<ServerCommandSource> child : destination.getChildren()) {
            builder.then(child);
        }
        return builder.build();
    }
}
