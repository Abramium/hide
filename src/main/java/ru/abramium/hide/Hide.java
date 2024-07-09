package ru.abramium.hide;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import static net.minecraft.scoreboard.AbstractTeam.VisibilityRule.NEVER;

@Environment(EnvType.SERVER)
public class Hide implements DedicatedServerModInitializer {

    private static final String TEAM_NAME = "ru.abramium.hide";

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStart);
        ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof PlayerEntity targetPlayer) {
                var name = targetPlayer.getName().getString();
                player.sendMessage(Text.of(name), true);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }

    private void onServerStart(MinecraftServer server) {
        if (server.getScoreboard().getTeam(TEAM_NAME) == null) {
            server.getScoreboard().addTeam(TEAM_NAME)
                    .setNameTagVisibilityRule(NEVER);
        }
    }

    private void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.getPlayer();
        if (player.getScoreboardTeam() == null || !player.getScoreboardTeam().getName().equals(TEAM_NAME)) {
            server.getScoreboard().addScoreHolderToTeam(
                    player.getNameForScoreboard(),
                    server.getScoreboard().getTeam(TEAM_NAME)
            );
        }
    }
}
