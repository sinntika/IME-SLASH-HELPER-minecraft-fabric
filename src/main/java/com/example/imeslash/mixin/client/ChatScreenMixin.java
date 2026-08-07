package com.example.imeslash.mixin.client;

import com.example.imeslash.ImeSlashClient;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * チャット画面の開閉を拾うだけの軽い Mixin。
 * init / removed はどのバージョンでも ChatScreen 自身が持つので、
 * バージョン分岐なしで全バージョンに適用できる。
 */
@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {

    @Shadow
    protected EditBox input;

    @Inject(method = "init", at = @At("TAIL"))
    private void imeslash$onInit(CallbackInfo ci) {
        ImeSlashClient.onChatOpened(this.input);
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void imeslash$onRemoved(CallbackInfo ci) {
        ImeSlashClient.onChatClosed();
    }
}
