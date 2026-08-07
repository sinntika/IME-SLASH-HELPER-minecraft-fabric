package com.example.imeslash.mixin.client;

import com.example.imeslash.ImeSlashClient;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >=1.21.9 {
/*import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
*///?}

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {

    @Shadow
    protected EditBox input;

    @Inject(method = "init", at = @At("TAIL"))
    private void imeslash$onInit(CallbackInfo ci) {
        // チャットを開いた時点では何もしない（日本語をそのまま打てるように）。
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void imeslash$onRemoved(CallbackInfo ci) {
        ImeSlashClient.restoreIme();
    }

    //? if <1.21.9 {
    @Inject(method = "charTyped(CI)Z", at = @At("HEAD"), cancellable = true)
    private void imeslash$onCharTyped(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (imeslash$handleChar(chr)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "keyPressed(III)Z", at = @At("HEAD"))
    private void imeslash$onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        imeslash$handleKey(keyCode);
    }
    //?} else {
    /*@Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void imeslash$onCharTyped(CharacterEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (imeslash$handleChar((char) event.codepoint())) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void imeslash$onKeyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        imeslash$handleKey(event.key());
    }
    *///?}

    /**
     * 入力欄が空のときに "/" 系の文字が来たら、半角 "/" を入れて IME を OFF にする。
     *
     * @return true なら元の charTyped をキャンセルする
     */
    private boolean imeslash$handleChar(char chr) {
        if (this.input == null) {
            return false;
        }
        if (chr != '/' && chr != '／' && chr != '・') {
            return false;
        }
        if (!this.input.getValue().isEmpty()) {
            return false;
        }
        this.input.setValue("/");
        ImeSlashClient.enterCommandMode();
        return true;
    }

    private void imeslash$handleKey(int keyCode) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            ImeSlashClient.markEnter();
            return;
        }
        // IME 変換中で charTyped が来ないケースの保険
        if (keyCode == GLFW.GLFW_KEY_SLASH && this.input != null && this.input.getValue().isEmpty()) {
            ImeSlashClient.enterCommandMode();
        }
    }
}
