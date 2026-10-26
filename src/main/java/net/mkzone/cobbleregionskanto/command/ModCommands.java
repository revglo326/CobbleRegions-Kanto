package net.mkzone.cobbleregionskanto.command;

import com.cobblemon.mod.common.CobblemonItems;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class ModCommands {

    private static final SimpleCommandExceptionType ERROR_PLAYER_NOT_FOUND = new SimpleCommandExceptionType(
            Component.literal("§cNot a valid player name!")
    );

    private static final SimpleCommandExceptionType ERROR_ADVANCEMENT_NOT_FOUND = new SimpleCommandExceptionType(
            Component.literal("§cQuest with the given id does not exist!")
    );

    private static final SimpleCommandExceptionType ERROR_COMMAND_NOT_COMPLETE = new SimpleCommandExceptionType(
            Component.literal("§cCommand is missing arguments!")
    );

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("cobbleregionskanto")
                        .requires(cs -> !(cs.getEntity() instanceof ServerPlayer) || cs.hasPermission(2))
                        .then(Commands.literal("completequest")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> {
                                            throw ERROR_COMMAND_NOT_COMPLETE.create();
                                        })
                                        .then(Commands.argument("advancementid", StringArgumentType.string())
                                                .executes(context -> {
                                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                                    String advancementid = StringArgumentType.getString(context, "advancementid");

                                                    if (player instanceof ServerPlayer) {
                                                        ResourceLocation advancementPath = ResourceLocation.fromNamespaceAndPath("cobbleregionskanto", "progression/" + advancementid);
                                                        AdvancementHolder advancement = player.getServer().getAdvancements().get(advancementPath);

                                                        if (advancement != null) {
                                                            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);

                                                            if (!progress.isDone()) {
                                                                for (String criteria : progress.getRemainingCriteria()) {
                                                                    player.getAdvancements().award(advancement, criteria);
                                                                }

                                                                switch (advancementid) {
                                                                    case "receive_good_rod":
                                                                        ItemStack good_rod = new ItemStack(CobblemonItems.GREAT_ROD);

                                                                        Component customName = Component.literal("Good Rod")
                                                                                .withStyle(style -> style.withItalic(false));

                                                                        good_rod.set(DataComponents.CUSTOM_NAME, customName);

                                                                        if (!player.getInventory().add(good_rod)) {
                                                                            player.drop(good_rod, false);
                                                                        }

                                                                        return Command.SINGLE_SUCCESS;
                                                                    case "trade_for_farfetchd":
                                                                    case "trade_for_jynx":
                                                                        return Command.SINGLE_SUCCESS;
                                                                    default:
                                                                        throw ERROR_ADVANCEMENT_NOT_FOUND.create();
                                                                }
                                                            }

                                                            return Command.SINGLE_SUCCESS;
                                                        }

                                                        throw ERROR_ADVANCEMENT_NOT_FOUND.create();
                                                    }

                                                    throw ERROR_PLAYER_NOT_FOUND.create();
                                                })
                                        )
                                )
                        )
        );
        dispatcher.register(
                Commands.literal("cobbleregionskanto")
                        .requires(cs -> !(cs.getEntity() instanceof ServerPlayer) || cs.hasPermission(2))
                        .then(Commands.literal("checkquest")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> {
                                            throw ERROR_COMMAND_NOT_COMPLETE.create();
                                        })
                                        .then(Commands.argument("advancementid", StringArgumentType.string())
                                                .executes(context -> {
                                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                                    String advancementid = StringArgumentType.getString(context, "advancementid");

                                                    if (player instanceof ServerPlayer) {
                                                        ResourceLocation advancementPath = ResourceLocation.fromNamespaceAndPath("cobbleregionskanto", "progression/" + advancementid);
                                                        AdvancementHolder advancement = player.getServer().getAdvancements().get(advancementPath);

                                                        if (advancement != null) {
                                                            PlayerAdvancements playerAdvancements = player.getAdvancements();
                                                            AdvancementProgress progress = playerAdvancements.getOrStartProgress(advancement);

                                                            player.sendSystemMessage(Component.literal("§cQuest, " + advancementid.toUpperCase() + ", is marked -> " + (progress.isDone() ? "COMPLETED!" : "INCOMPLETE!")));
                                                            return Command.SINGLE_SUCCESS;
                                                        }

                                                        throw ERROR_ADVANCEMENT_NOT_FOUND.create();
                                                    }

                                                    throw ERROR_PLAYER_NOT_FOUND.create();
                                                })
                                        )
                                )
                        )
        );
        dispatcher.register(
                Commands.literal("cobbleregionskanto")
                        .requires(cs -> !(cs.getEntity() instanceof ServerPlayer) || cs.hasPermission(2))
                        .then(Commands.literal("resetquest")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> {
                                            throw ERROR_COMMAND_NOT_COMPLETE.create();
                                        })
                                        .then(Commands.argument("advancementid", StringArgumentType.string())
                                                .executes(context -> {
                                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                                    String advancementid = StringArgumentType.getString(context, "advancementid");

                                                    if (player instanceof ServerPlayer) {
                                                        ResourceLocation advancementPath = ResourceLocation.fromNamespaceAndPath("cobbleregionskanto", "progression/" + advancementid);
                                                        AdvancementHolder advancement = player.getServer().getAdvancements().get(advancementPath);

                                                        if (advancement != null) {
                                                            PlayerAdvancements playerAdvancements = player.getAdvancements();
                                                            AdvancementProgress progress = playerAdvancements.getOrStartProgress(advancement);

                                                            for (String criteria : progress.getCompletedCriteria()) {
                                                                playerAdvancements.revoke(advancement, criteria);
                                                            }

                                                            return Command.SINGLE_SUCCESS;
                                                        }

                                                        throw ERROR_ADVANCEMENT_NOT_FOUND.create();
                                                    }

                                                    throw ERROR_PLAYER_NOT_FOUND.create();
                                                })
                                        )
                                )
                        )
        );
    }
}
