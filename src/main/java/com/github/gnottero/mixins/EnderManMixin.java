package com.github.gnottero.mixins;

import com.github.gnottero.TwitchProjectSettings;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public abstract class EnderManMixin extends Monster {
    protected EnderManMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    // Passeggeri dell'enderman davanti a lui
    @Override
    public void positionRider(@NotNull Entity entity) {
        super.positionRider(entity);
        float f = Mth.sin(this.yBodyRot * ((float)Math.PI / 180));
        float g = Mth.cos(this.yBodyRot * ((float)Math.PI / 180));
        entity.setPos(this.getX() - (double)(0.8f * f), this.getY(0.25) + entity.getMyRidingOffset() + 0.0, this.getZ() + (double)(0.8f * g));
        if (entity instanceof LivingEntity) {
            ((LivingEntity)entity).yBodyRot = this.yBodyRot;
        }
    }
}
