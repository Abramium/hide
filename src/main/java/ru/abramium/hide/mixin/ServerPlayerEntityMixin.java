package ru.abramium.hide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static net.minecraft.entity.effect.StatusEffects.INVISIBILITY;

@Environment(EnvType.SERVER)
@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayerEntityMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onClientCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setSneaking(Z)V"))
    private void modifySneaking(ClientCommandC2SPacket packet, CallbackInfo ci) {
        applyHiddenEffect(player, player.isSneaking());
    }

    @Unique
    private void applyHiddenEffect(ServerPlayerEntity player, boolean hide) {
        if (player.hasStatusEffect(INVISIBILITY)) {
            var effect = player.getStatusEffect(INVISIBILITY);
            var hiddenEffect = (StatusEffectInstanceAccessor) effect;
            Objects.requireNonNull(hiddenEffect).setShowParticles(hide);
            player.setStatusEffect(effect, player);
        }
    }
}
