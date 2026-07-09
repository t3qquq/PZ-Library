// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;

public class Controllers {
    public static final int MAX_AXES = 6;
    public static final int MAX_BUTTONS = 15;
    public static final int MAX_CONTROLLERS = 16;
    private static final Controller[] controllers = new Controller[16];
    private static boolean isCreated = false;
    private static Consumer<Integer> controllerConnectedCallback = null;
    private static Consumer<Integer> controllerDisconnectedCallback = null;
    private static int debugToggleControllerPluggedIn = -1;

    public static void create() {
        readGameControllerDB();
        GLFW.glfwSetJoystickCallback(Controllers::updateControllersCount);

        for (int int0 = 0; int0 < 16; int0++) {
            if (GLFW.glfwJoystickPresent(int0)) {
                controllers[int0] = new Controller(int0);
            }
        }

        isCreated = true;
    }

    private static void readGameControllerDB() {
        File file = new File("./media/gamecontrollerdb.txt").getAbsoluteFile();
        if (file.exists()) {
            readGameControllerDB(file);
        }

        String string = ZomboidFileSystem.instance.getCacheDirSub("joypads" + File.separator + "gamecontrollerdb.txt");
        file = new File(string);
        if (file.exists()) {
            readGameControllerDB(file);
        }
    }

    private static void readGameControllerDB(File file) {
        try (
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
        ) {
            StringBuilder stringBuilder = new StringBuilder();

            String string;
            while ((string = bufferedReader.readLine()) != null) {
                if (!string.startsWith("#")) {
                    stringBuilder.append(string);
                    stringBuilder.append(System.lineSeparator());
                }
            }

            ByteBuffer byteBuffer = MemoryUtil.memUTF8(stringBuilder.toString());
            if (GLFW.glfwUpdateGamepadMappings(byteBuffer)) {
            }

            MemoryUtil.memFree(byteBuffer);
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }
    }

    public static void setControllerConnectedCallback(Consumer<Integer> consumer) {
        controllerConnectedCallback = consumer;
    }

    public static void setControllerDisconnectedCallback(Consumer<Integer> consumer) {
        controllerDisconnectedCallback = consumer;
    }

    public static int getControllerCount() {
        if (!isCreated()) {
            throw new RuntimeException("Before calling 'getJoypadCount()' you should call 'create()' method");
        } else {
            return controllers.length;
        }
    }

    public static Controller getController(int int0) {
        if (!isCreated()) {
            throw new RuntimeException("Before calling 'getJoypad(int)' you should call 'create()' method");
        } else {
            return controllers[int0];
        }
    }

    public static boolean isCreated() {
        return isCreated;
    }

    public static void poll(GamepadState[] gamepadStates) {
        if (!isCreated()) {
            throw new RuntimeException("Before calling 'poll()' you should call 'create()' method");
        } else {
            if (Core.bDebug && debugToggleControllerPluggedIn >= 0 && debugToggleControllerPluggedIn < 16) {
                int int0 = debugToggleControllerPluggedIn;
                debugToggleControllerPluggedIn = -1;
                if (controllers[int0] != null) {
                    updateControllersCount(int0, 262146);
                } else if (GLFW.glfwJoystickIsGamepad(int0)) {
                    updateControllersCount(int0, 262145);
                }
            }

            for (int int1 = 0; int1 < controllers.length; int1++) {
                Controller controller = controllers[int1];
                if (controller != null) {
                    controller.poll(gamepadStates[int1]);
                }
            }
        }
    }

    private static void updateControllersCount(int int1, int int0) {
        if (int0 == 262145) {
            Controller controller = new Controller(int1);
            controllers[int1] = controller;
            if (controllerConnectedCallback != null) {
                controllerConnectedCallback.accept(int1);
            }
        } else if (int0 == 262146) {
            controllers[int1] = null;
            if (controllerDisconnectedCallback != null) {
                controllerDisconnectedCallback.accept(int1);
            }
        }
    }

    public static void setDebugToggleControllerPluggedIn(int int0) {
        debugToggleControllerPluggedIn = int0;
    }
}
