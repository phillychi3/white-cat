package life.whitecloud.whitecat.cats;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

import life.whitecloud.whitecat.WhiteCat;
import life.whitecloud.whitecat.util;

@Mod.EventBusSubscriber(modid = WhiteCat.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VoteShutdown {
    private static Set<String> votedYes = new HashSet<>();
    private static Set<String> votedNo = new HashSet<>();
    private static boolean voteInProgress = false;
    private static MinecraftServer currentServer = null;

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("voteshutdown")
                        .then(Commands.literal("yes").executes(context -> executeVote(context.getSource(), true)))
                        .then(Commands.literal("no").executes(context -> executeVote(context.getSource(), false)))
                        .executes(context -> initiateVote(context.getSource())));
    }

    private static int initiateVote(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command can only be used by players."));
            return 0;
        }

        if (voteInProgress) {
            source.sendFailure(Component.literal("A vote is already in progress."));
            return 0;
        }

        voteInProgress = true;
        votedYes.clear();
        votedNo.clear();
        currentServer = source.getServer();

        Component message = Component.literal("A vote to shut down server ")
                .append(Component.literal("[YES]")
                        .setStyle(Style.EMPTY
                                .withColor(0x00FF00)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/voteshutdown yes"))
                                .withHoverEvent(
                                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Vote Yes")))))
                .append(Component.literal(" or "))
                .append(Component.literal("[NO]")
                        .setStyle(Style.EMPTY
                                .withColor(0xFF0000)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/voteshutdown no"))
                                .withHoverEvent(
                                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Vote No")))));

        ;
        currentServer.getPlayerList().broadcastSystemMessage(util.broadcastPrefixedMessage(message), false);
        WhiteCat.LOGGER.info("A vote to shut down the server has started.");

        return 1;
    }

    private static int executeVote(CommandSourceStack source, boolean voteYes) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command can only be used by players."));
            return 0;
        }

        if (!voteInProgress) {
            source.sendFailure(Component.literal("There is no active vote."));
            return 0;
        }

        String playerName = player.getGameProfile().getName();
        Set<String> targetSet = voteYes ? votedYes : votedNo;
        Set<String> oppositeSet = voteYes ? votedNo : votedYes;

        if (oppositeSet.contains(playerName)) {
            oppositeSet.remove(playerName);
        }

        if (targetSet.add(playerName)) {
            String voteType = voteYes ? "YES" : "NO";

            currentServer.getPlayerList().broadcastSystemMessage(
                    util.broadcastPrefixedMessage(Component.literal(playerName + " has voted " + voteType + ".")),
                    false);

            if (votedNo.size() > 0) {
                endVote(false);
            } else if (votedYes.size() >= (currentServer.getPlayerCount() / 2) + 1) {
                endVote(true);
            }
        } else {
            source.sendFailure(Component.literal("You have already voted."));
        }

        return 1;
    }

    private static void endVote(boolean passed) {
        voteInProgress = false;
        if (passed) {

            currentServer.getPlayerList().broadcastSystemMessage(util.broadcastPrefixedMessage(
                    Component.literal("Vote passed. Server shutting down in 10 seconds.")), false);
            WhiteCat.LOGGER.info("Vote passed. Server shutting down in 10 seconds.");
            currentServer.execute(() -> {
                for (int i = 10; i > 0; i--) {
                    final int secondsLeft = i;

                    currentServer.getPlayerList().broadcastSystemMessage(util.broadcastPrefixedMessage(
                            Component.literal("Server shutting down in " + secondsLeft + " seconds.")), false);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        WhiteCat.LOGGER.error("Shutdown countdown interrupted", e);
                    }
                }
                currentServer.halt(false);
            });
        } else {

            currentServer.getPlayerList().broadcastSystemMessage(
                    util.broadcastPrefixedMessage(Component.literal("Vote failed. The server will not shut down.")),
                    false);
            WhiteCat.LOGGER.info("Vote failed. The server will not shut down.");
        }
    }
}