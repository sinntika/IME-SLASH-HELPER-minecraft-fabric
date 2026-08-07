package com.example.imeslash;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.components.EditBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * クライアントエントリポイントと IME 状態の保存 / 復元を担当。
 *
 * charTyped / keyPressed への Mixin はバージョン差が大きいため使わず、
 * 毎 tick でチャット入力欄の内容を見て判定する。
 */
public class ImeSlashClient implements ClientModInitializer {
    public static final String MOD_ID = "imeslash";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /** 全角スラッシュ相当の文字（IME や入力方式で変わる）。 */
    private static final String[] SLASH_LIKE = { "/", "／", "・" };

    private static EditBox chatInput;
    private static String lastValue = "";
    private static boolean turnedOffByMod = false;
    private static int savedConversion = -1;
    private static int savedSentence = -1;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[{}] initialized. native IME available = {}", MOD_ID, WinIme.isAvailable());
    }

    /** ChatScreen#init から呼ばれる。 */
    public static void onChatOpened(EditBox box) {
        chatInput = box;
        lastValue = "";
    }

    /** ChatScreen#removed から呼ばれる（Enter 送信・Esc どちらも）。 */
    public static void onChatClosed() {
        chatInput = null;
        lastValue = "";
        restoreIme();
    }

    /** 毎 tick。入力欄がスラッシュ 1 文字だけになったら半角化して IME を OFF。 */
    public static void clientTick() {
        EditBox box = chatInput;
        if (box == null) {
            return;
        }
        String value;
        try {
            value = box.getValue();
        } catch (Throwable t) {
            return;
        }
        if (value == null || value.equals(lastValue)) {
            return;
        }
        lastValue = value;
        if (turnedOffByMod) {
            return;
        }
        for (String s : SLASH_LIKE) {
            if (value.equals(s)) {
                if (!"/".equals(value)) {
                    box.setValue("/");
                    lastValue = "/";
                }
                enterCommandMode();
                return;
            }
        }
    }

    /** コマンドモードへ：現在の IME 状態を覚えて IME を OFF にする。 */
    public static void enterCommandMode() {
        if (!WinIme.isAvailable() || turnedOffByMod) {
            return;
        }
        if (!WinIme.isOpen()) {
            return;
        }
        int[] status = WinIme.getConversionStatus();
        if (status != null) {
            savedConversion = status[0];
            savedSentence = status[1];
        }
        WinIme.setOpen(false);
        turnedOffByMod = true;
        LOGGER.debug("[{}] IME off (saved conversion={}, sentence={})", MOD_ID, savedConversion, savedSentence);
    }

    /** チャットを閉じたときに元の IME 状態へ戻す。 */
    public static void restoreIme() {
        if (!turnedOffByMod) {
            return;
        }
        WinIme.setOpen(true);
        if (savedConversion >= 0) {
            WinIme.setConversionStatus(savedConversion, savedSentence);
        }
        LOGGER.debug("[{}] IME restored", MOD_ID);
        turnedOffByMod = false;
        savedConversion = -1;
        savedSentence = -1;
    }
}
