// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public final class Clipboard {
    private static Thread MainThread = null;
    private static String PreviousKnownValue = null;
    private static String DelaySetMainThread = null;

    public static void initMainThread() {
        MainThread = Thread.currentThread();
        PreviousKnownValue = getClipboard();
    }

    public static void rememberCurrentValue() {
        if (Thread.currentThread() == MainThread) {
            GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback(null);

            try {
                PreviousKnownValue = new String(GLFW.glfwGetClipboardString(0L));
            } catch (Throwable throwable) {
                PreviousKnownValue = "";
            } finally {
                GLFW.glfwSetErrorCallback(gLFWErrorCallback);
            }
        }
    }

    public static synchronized String getClipboard() {
        if (Thread.currentThread() == MainThread) {
            GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback(null);

            String string;
            try {
                return PreviousKnownValue = new String(GLFW.glfwGetClipboardString(0L));
            } catch (Throwable throwable) {
                PreviousKnownValue = "";
                string = "";
            } finally {
                GLFW.glfwSetErrorCallback(gLFWErrorCallback);
            }

            return string;
        } else {
            return PreviousKnownValue;
        }
    }

    public static synchronized void setClipboard(String str) {
        PreviousKnownValue = str;
        if (Thread.currentThread() == MainThread) {
            GLFW.glfwSetClipboardString(0L, str);
        } else {
            DelaySetMainThread = str;
        }
    }

    public static synchronized void updateMainThread() {
        if (DelaySetMainThread != null) {
            setClipboard(DelaySetMainThread);
            DelaySetMainThread = null;
        }
    }
}
