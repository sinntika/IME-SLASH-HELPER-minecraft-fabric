package com.example.imeslash.mixin.client;

import com.example.imeslash.ImeSlashClient;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 毎 tick でチャット入力欄を監視するための Mixin。
 * Minecraft#tick() は全バージョンで ()V のままなので安全。
 */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void imeslash$onTick(CallbackInfo ci) {
        ImeSlashClient.clientTick();
    }
}
