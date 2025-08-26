package com.eventclubmod.command;

import com.eventclubmod.EventClubMod;
import com.eventclubmod.config.ModConfig;
import com.eventclubmod.features.shadow.ShadowNpcManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public final class ModCommands {

    private ModCommands() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, EventClubMod mod, ShadowNpcManager shadowNpcManager) {
        LiteralArgumentBuilder<ServerCommandSource> root = CommandManager.literal("eventclub")
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(() -> Text.literal("EventClub: мод загружен и работает! v1.0.0"), false);
                    return 1;
                })
                .then(CommandManager.literal("status")
                        .executes(ctx -> {
                            ModConfig cfg = mod.getConfigOrCreate(ctx.getSource().getServer());
                            boolean note = cfg.enableWelcomeNote;
                            boolean shadow = cfg.enableShadowNpc;
                            ctx.getSource().sendFeedback(() -> Text.literal(
                                    "WelcomeNote: " + (note ? "ON" : "OFF") + ", ShadowNPC: " + (shadow ? "ON" : "OFF")), false);
                            return 1;
                        }))
                .then(CommandManager.literal("note")
                        .then(CommandManager.literal("on")
                                .requires(mod::isAllowedOperator)
                                .executes(ctx -> {
                                    mod.toggleWelcome(true, ctx.getSource());
                                    return 1;
                                }))
                        .then(CommandManager.literal("off")
                                .requires(mod::isAllowedOperator)
                                .executes(ctx -> {
                                    mod.toggleWelcome(false, ctx.getSource());
                                    return 1;
                                })))
                .then(CommandManager.literal("shadow")
                        .then(CommandManager.literal("on")
                                .requires(mod::isAllowedOperator)
                                .executes(ctx -> {
                                    ModConfig cfg = mod.getConfigOrCreate(ctx.getSource().getServer());
                                    shadowNpcManager.toggleShadow(true, ctx.getSource(), cfg);
                                    return 1;
                                }))
                        .then(CommandManager.literal("off")
                                .requires(mod::isAllowedOperator)
                                .executes(ctx -> {
                                    ModConfig cfg = mod.getConfigOrCreate(ctx.getSource().getServer());
                                    shadowNpcManager.toggleShadow(false, ctx.getSource(), cfg);
                                    return 1;
                                }))
                );

        dispatcher.register(root);
    }
}
