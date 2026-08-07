package com.example.imeslash;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * クライアントエントリポイントと IME 状態の保存 / 復元を担当。
 */
public class ImeSlashClient implements ClientModInitializer {
    public static final String MOD_ID = "imeslash";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /** true にすると Enter で送信したときだけ日本語入力に戻す（Esc で閉じた場合は戻さない）。 */
    public static boolean RESTORE_ONLY_ON_ENTER = false;

    private static boolean turnedOffByMod = false;
    private static int savedConversion = -1;
    private static int savedSentence = -1;
    private static boolean enterPressed = false;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[{}] initialized. native IME available = {}", MOD_ID, WinIme.isAvailable());
    }

    /** コマンドモードへ：現在の IME 状態を覚えて IME を OFF にする。 */
    public static void enterCommandMode() {
        if (!WinIme.isAvailable() || turnedOffByMod) {
            return;
        }
        if (!WinIme.isOpen()) {
            return; // もともと直接入力なら何もしない
        }
        int[] status = WinIme.getConversionStatus();
        if (status != null) {
            savedConversion = status[0];
            savedSentence = status[1];
        }
        WinIme.setOpen(false);
        turnedOffByMod = true;
        enterPressed = false;
        LOGGER.debug("[{}] IME off (saved conversion={}, sentence={})", MOD_ID, savedConversion, savedSentence);
    }

    /** Enter が押されたことを記録。 */
    public static void markEnter() {
        enterPressed = true;
    }

    /** チャットを閉じたときに元の IME 状態へ戻す。 */
    public static void restoreIme() {
        if (!turnedOffByMod) {
            return;
        }
        if (RESTORE_ONLY_ON_ENTER && !enterPressed) {
            reset();
            return;
        }
        WinIme.setOpen(true);
        if (savedConversion >= 0) {
            WinIme.setConversionStatus(savedConversion, savedSentence);
        }
        LOGGER.debug("[{}] IME restored", MOD_ID);
        reset();
    }

    private static void reset() {
        turnedOffByMod = false;
        savedConversion = -1;
        savedSentence = -1;
        enterPressed = false;
    }
}
