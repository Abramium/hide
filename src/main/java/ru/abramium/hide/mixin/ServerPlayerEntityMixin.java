package ru.abramium.hide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.abramium.hide.Hide;

import static java.util.Optional.ofNullable;
import static net.minecraft.entity.effect.StatusEffects.INVISIBILITY;

@Environment(EnvType.SERVER)
@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayerEntityMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onClientCommand", at = @At(value = "RETURN"))
    private void modifySneaking(ClientCommandC2SPacket packet, CallbackInfo ci) {
        if ("true".equals(Hide.PROPERTIES.get("hideParticlesWhenSneaking")))
            ofNullable(player.getStatusEffect(INVISIBILITY)).ifPresent(effect -> {
                effect.showParticles = !player.isSneaking();
                player.setStatusEffect(effect, player);
            });
    }
}
