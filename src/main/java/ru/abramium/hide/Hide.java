package ru.abramium.hide;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import static net.minecraft.entity.effect.StatusEffects.INVISIBILITY;
import static net.minecraft.scoreboard.AbstractTeam.VisibilityRule.NEVER;

@Environment(EnvType.SERVER)
public class Hide implements DedicatedServerModInitializer {

    private static final String TEAM_NAME = "ru.abramium.hide";

    @Override
    public void onInitializeServer() {
        registerInitTeam();
        registerAddPlayerToTeamOnJoin();
        registerShowNameTagOnClick();
    }

    private void registerInitTeam() {
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            var team = server.getScoreboard().getTeam(TEAM_NAME);
            if (team == null || team.shouldShowFriendlyInvisibles()) {
                team = server.getScoreboard().addTeam(TEAM_NAME);
                team.setShowFriendlyInvisibles(false);
                team.setNameTagVisibilityRule(NEVER);
            }
        });
    }

    private void registerAddPlayerToTeamOnJoin() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            if (player.getScoreboardTeam() == null || !player.getScoreboardTeam().getName().equals(TEAM_NAME))
                server.getScoreboard().addScoreHolderToTeam(player.getNameForScoreboard(), server.getScoreboard().getTeam(TEAM_NAME));
        });
    }

    private void registerShowNameTagOnClick() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof PlayerEntity targetPlayer && !targetPlayer.hasStatusEffect(INVISIBILITY)) {
                player.sendMessage(Text.of(targetPlayer.getName().getString()), true);
                return ActionResult.SUCCESS;
            } else return ActionResult.PASS;
        });
    }
}
