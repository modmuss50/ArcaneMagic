package com.raphydaphy.arcanemagic.core.client;

import com.raphydaphy.arcanemagic.client.particle.ParticleRenderer;
import net.minecraft.class_4184;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin
{
	@Shadow @Final private class_4184 field_18765; // Camera

	@Inject(at = @At(value = "INVOKE_STRING", args = "ldc=hand", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"), method = "renderCenter")
	private void renderCenter(float partialTicks, long finishTimeNano, CallbackInfo info)
	{
		ParticleRenderer.INSTANCE.render(partialTicks, field_18765);
	}
}
