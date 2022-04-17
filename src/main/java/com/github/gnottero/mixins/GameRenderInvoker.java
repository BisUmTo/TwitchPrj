package com.github.gnottero.mixins;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRenderInvoker {
	@Invoker(value = "loadShader")
	void invokeLoadShader(Identifier identifier);
}