package com.github.gnottero.mixins;

import carpet.CarpetSettings;
import com.github.gnottero.TwitchProjectSettings;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends Input {
    @Shadow @Final
    private GameOptions settings;

    /**
     * @author BisUmTo (and Gnottero)
     * @reason Carpet overrides the default keybinds
     */
    @Overwrite
    @Override
    public void tick(boolean slowDown, float f) {

        List<Boolean> lb = new ArrayList<>();
        lb.add(this.settings.forwardKey.isPressed());
        lb.add(this.settings.rightKey.isPressed());
        lb.add(this.settings.backKey.isPressed());
        lb.add(this.settings.leftKey.isPressed());

        int i = 0;
        this.pressingForward = lb.get((TwitchProjectSettings.invertControlsDirection + i++)%4);
        this.pressingRight = lb.get((TwitchProjectSettings.invertControlsDirection + i++)%4);
        this.pressingBack = lb.get((TwitchProjectSettings.invertControlsDirection + i++)%4);
        this.pressingLeft = lb.get((TwitchProjectSettings.invertControlsDirection + i)%4);

        this.movementForward = this.pressingForward == this.pressingBack ? 0.0F : (this.pressingForward ? 1.0F : -1.0F);
        this.movementSideways = this.pressingLeft == this.pressingRight ? 0.0F : (this.pressingLeft ? 1.0F : -1.0F);
        this.jumping = this.settings.jumpKey.isPressed();
        this.sneaking = this.settings.jumpKey.isPressed();
        if (slowDown) {
            this.movementSideways = (float)((double)this.movementSideways * 0.3D);
            this.movementForward = (float)((double)this.movementForward * 0.3D);
        }
    }
}
