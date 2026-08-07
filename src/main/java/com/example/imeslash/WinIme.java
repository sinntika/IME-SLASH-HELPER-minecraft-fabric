package com.example.imeslash;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Windows の IMM32 API を JNA 経由で叩くラッパー。
 * Windows 以外や JNA をロードできない環境では isAvailable() が false になり、
 * 全メソッドが安全な no-op になる。
 *
 * ウィンドウハンドルの取得には Minecraft の Window クラスを使わず、
 * GLFW の glfwGetCurrentContext() を使う（全バージョンで API が変わらないため）。
 */
public final class WinIme {

    public static final int WM_IME_CONTROL = 0x0283;
    public static final int IMC_GETOPENSTATUS = 0x0005;
    public static final int IMC_SETOPENSTATUS = 0x0006;

    public static final int IME_CMODE_NATIVE = 0x0001;
    public static final int IME_CMODE_KATAKANA = 0x0002;
    public static final int IME_CMODE_FULLSHAPE = 0x0008;
    public static final int IME_CMODE_ROMAN = 0x0010;

    public interface Imm32 extends StdCallLibrary {
        Pointer ImmGetContext(Pointer hWnd);
        boolean ImmReleaseContext(Pointer hWnd, Pointer hIMC);
        boolean ImmGetOpenStatus(Pointer hIMC);
        boolean ImmSetOpenStatus(Pointer hIMC, boolean open);
        boolean ImmGetConversionStatus(Pointer hIMC, IntByReference conversion, IntByReference sentence);
        boolean ImmSetConversionStatus(Pointer hIMC, int conversion, int sentence);
        Pointer ImmGetDefaultIMEWnd(Pointer hWnd);
    }

    public interface User32Ex extends StdCallLibrary {
        long SendMessageW(Pointer hWnd, int msg, long wParam, long lParam);
    }

    private static Imm32 IMM32;
    private static User32Ex USER32;
    private static boolean available = false;

    static {
        try {
            String os = System.getProperty("os.name", "").toLowerCase();
            if (os.contains("win")) {
                IMM32 = Native.load("imm32", Imm32.class, W32APIOptions.DEFAULT_OPTIONS);
                USER32 = Native.load("user32", User32Ex.class, W32APIOptions.DEFAULT_OPTIONS);
                available = true;
            }
        } catch (Throwable t) {
            ImeSlashClient.LOGGER.warn("[imeslash] native IME unavailable: {}", t.toString());
            available = false;
        }
    }

    private WinIme() {
    }

    public static boolean isAvailable() {
        return available;
    }

    private static Pointer hwnd() {
        try {
            long glfwWindow = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
            if (glfwWindow == 0L) {
                return null;
            }
            long handle = org.lwjgl.glfw.GLFWNativeWin32.glfwGetWin32Window(glfwWindow);
            return handle == 0L ? null : new Pointer(handle);
        } catch (Throwable t) {
            return null;
        }
    }

    public static boolean isOpen() {
        if (!available) {
            return false;
        }
        Pointer hWnd = hwnd();
        if (hWnd == null) {
            return false;
        }
        Pointer hIMC = IMM32.ImmGetContext(hWnd);
        if (hIMC != null && Pointer.nativeValue(hIMC) != 0L) {
            try {
                return IMM32.ImmGetOpenStatus(hIMC);
            } finally {
                IMM32.ImmReleaseContext(hWnd, hIMC);
            }
        }
        Pointer ime = IMM32.ImmGetDefaultIMEWnd(hWnd);
        if (ime == null || Pointer.nativeValue(ime) == 0L) {
            return false;
        }
        return USER32.SendMessageW(ime, WM_IME_CONTROL, IMC_GETOPENSTATUS, 0L) != 0L;
    }

    public static void setOpen(boolean open) {
        if (!available) {
            return;
        }
        Pointer hWnd = hwnd();
        if (hWnd == null) {
            return;
        }
        boolean ok = false;
        Pointer hIMC = IMM32.ImmGetContext(hWnd);
        if (hIMC != null && Pointer.nativeValue(hIMC) != 0L) {
            try {
                ok = IMM32.ImmSetOpenStatus(hIMC, open);
            } finally {
                IMM32.ImmReleaseContext(hWnd, hIMC);
            }
        }
        if (!ok) {
            Pointer ime = IMM32.ImmGetDefaultIMEWnd(hWnd);
            if (ime != null && Pointer.nativeValue(ime) != 0L) {
                USER32.SendMessageW(ime, WM_IME_CONTROL, IMC_SETOPENSTATUS, open ? 1L : 0L);
            }
        }
    }

    /** @return {conversion, sentence} または null */
    public static int[] getConversionStatus() {
        if (!available) {
            return null;
        }
        Pointer hWnd = hwnd();
        if (hWnd == null) {
            return null;
        }
        Pointer hIMC = IMM32.ImmGetContext(hWnd);
        if (hIMC == null || Pointer.nativeValue(hIMC) == 0L) {
            return null;
        }
        try {
            IntByReference conv = new IntByReference();
            IntByReference sent = new IntByReference();
            if (IMM32.ImmGetConversionStatus(hIMC, conv, sent)) {
                return new int[] { conv.getValue(), sent.getValue() };
            }
            return null;
        } finally {
            IMM32.ImmReleaseContext(hWnd, hIMC);
        }
    }

    public static void setConversionStatus(int conversion, int sentence) {
        if (!available) {
            return;
        }
        Pointer hWnd = hwnd();
        if (hWnd == null) {
            return;
        }
        Pointer hIMC = IMM32.ImmGetContext(hWnd);
        if (hIMC == null || Pointer.nativeValue(hIMC) == 0L) {
            return;
        }
        try {
            IMM32.ImmSetConversionStatus(hIMC, conversion, sentence);
        } finally {
            IMM32.ImmReleaseContext(hWnd, hIMC);
        }
    }
}
