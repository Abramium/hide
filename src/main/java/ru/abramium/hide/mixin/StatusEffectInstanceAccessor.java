package ru.abramium.hide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.SERVER)
@Mixin(StatusEffectInstance.class)
public interface StatusEffectInstanceAccessor {

    @Accessor("showParticles")
    void setShowParticles(boolean showParticles);
}
