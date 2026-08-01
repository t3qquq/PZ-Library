// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.core;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import zombie.UsedFromLua;

@UsedFromLua
public final class Clipboard {
    private static Thread mainThread;
    private static String previousKnownValue;
    private static String delaySetMainThread;

    public static void initMainThread() {
        mainThread = Thread.currentThread();
        previousKnownValue = getClipboard();
    }

    public static void rememberCurrentValue() {
        if (Thread.currentThread() == mainThread) {
            GLFWErrorCallback errorCallback = GLFW.glfwSetErrorCallback(null);

            try {
                previousKnownValue = new String(GLFW.glfwGetClipboardString(0L));
            } catch (Throwable t) {
                previousKnownValue = "";
            } finally {
                GLFW.glfwSetErrorCallback(errorCallback);
            }
        }
    }

    public static synchronized String getClipboard() {
        if (Thread.currentThread() == mainThread) {
            GLFWErrorCallback errorCallback = GLFW.glfwSetErrorCallback(null);

            try {
                return previousKnownValue = new String(GLFW.glfwGetClipboardString(0L));
            } catch (Throwable t) {
                previousKnownValue = "";
                return "";
            } finally {
                GLFW.glfwSetErrorCallback(errorCallback);
            }
        } else {
            return previousKnownValue;
        }
    }

    public static synchronized void setClipboard(String str) {
        previousKnownValue = str;
        if (Thread.currentThread() == mainThread) {
            GLFW.glfwSetClipboardString(0L, str);
        } else {
            delaySetMainThread = str;
        }
    }

    public static synchronized void updateMainThread() {
        if (delaySetMainThread != null) {
            setClipboard(delaySetMainThread);
            delaySetMainThread = null;
        }
    }
}
