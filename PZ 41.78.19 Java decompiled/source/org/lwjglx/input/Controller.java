// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.input;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public final class Controller {
    private final String joystickName;
    private final String gamepadName;
    private final int buttonsCount;
    private final int axisCount;
    private final int hatCount;
    private final int id;
    private final boolean isGamepad;
    private final String guid;
    private final float[] deadZone;
    public GamepadState gamepadState = null;
    private static final String[] axisNames = new String[]{"left stick X", "left stick Y", "right stick X", "right stick Y", "left trigger", "right trigger"};
    private static final String[] buttonNames = new String[]{
        "A",
        "B",
        "X",
        "Y",
        "left bumper",
        "right bumper",
        "back",
        "start",
        "guide",
        "left stick",
        "right stick",
        "d-pad up",
        "d-pad right",
        "d-pad down",
        "d-pad left"
    };

    public Controller(int arg0) {
        this.id = arg0;
        String string0 = GLFW.glfwGetJoystickName(arg0);
        if (string0 == null) {
            string0 = "ControllerName" + arg0;
        }

        this.joystickName = string0;
        String string1 = GLFW.glfwGetGamepadName(arg0);
        if (string1 == null) {
            string1 = "GamepadName" + arg0;
        }

        this.gamepadName = string1;
        this.isGamepad = GLFW.glfwJoystickIsGamepad(arg0);
        if (this.isGamepad) {
            this.axisCount = 6;
            this.buttonsCount = 15;
        } else {
            FloatBuffer floatBuffer = GLFW.glfwGetJoystickAxes(arg0);
            this.axisCount = floatBuffer == null ? 0 : floatBuffer.remaining();
            ByteBuffer byteBuffer0 = GLFW.glfwGetJoystickButtons(arg0);
            this.buttonsCount = byteBuffer0 == null ? 0 : byteBuffer0.remaining();
        }

        ByteBuffer byteBuffer1 = GLFW.glfwGetJoystickHats(arg0);
        this.hatCount = byteBuffer1 == null ? 0 : byteBuffer1.remaining();
        this.guid = GLFW.glfwGetJoystickGUID(arg0);
        this.deadZone = new float[this.axisCount];
        Arrays.fill(this.deadZone, 0.2F);
    }

    public int getID() {
        return this.id;
    }

    public String getGUID() {
        return this.guid;
    }

    public boolean isGamepad() {
        return this.isGamepad;
    }

    public String getJoystickName() {
        return this.joystickName;
    }

    public String getGamepadName() {
        return this.gamepadName;
    }

    public int getAxisCount() {
        return this.axisCount;
    }

    public float getAxisValue(int arg0) {
        if (this.gamepadState == null || !this.gamepadState.bPolled) {
            return 0.0F;
        } else {
            return arg0 >= 0 && arg0 < 15 ? this.gamepadState.axesButtons.axes(arg0) : 0.0F;
        }
    }

    public int getButtonCount() {
        return this.buttonsCount;
    }

    public int getHatCount() {
        return this.hatCount;
    }

    public int getHatState() {
        return this.gamepadState != null && this.gamepadState.bPolled ? this.gamepadState.hatState : 0;
    }

    public ByteBuffer getJoystickHats(int arg0, ByteBuffer arg1) {
        MemoryStack memoryStack = MemoryStack.stackGet();
        int int0 = memoryStack.getPointer();
        IntBuffer intBuffer = memoryStack.callocInt(1);

        ByteBuffer byteBuffer;
        try {
            long long0 = GLFW.nglfwGetJoystickHats(arg0, MemoryUtil.memAddress(intBuffer));
            arg1.clear();
            arg1.limit(intBuffer.get(0));
            if (long0 != 0L) {
                MemoryUtil.memCopy(long0, MemoryUtil.memAddress(arg1), intBuffer.get(0));
            }

            byteBuffer = arg1;
        } finally {
            memoryStack.setPointer(int0);
        }

        return byteBuffer;
    }

    public String getAxisName(int arg0) {
        return axisNames[arg0];
    }

    public float getXAxisValue() {
        return this.getAxisValue(0);
    }

    public float getYAxisValue() {
        return this.getAxisValue(1);
    }

    public float getDeadZone(int arg0) {
        return this.deadZone[arg0];
    }

    public void setDeadZone(int arg0, float arg1) {
        this.deadZone[arg0] = arg1;
    }

    public float getPovX() {
        if (this.gamepadState == null || !this.gamepadState.bPolled) {
            return 0.0F;
        } else if ((this.gamepadState.hatState & 8) != 0) {
            return -1.0F;
        } else {
            return (this.gamepadState.hatState & 2) != 0 ? 1.0F : 0.0F;
        }
    }

    public float getPovY() {
        if (this.gamepadState == null || !this.gamepadState.bPolled) {
            return 0.0F;
        } else if ((this.gamepadState.hatState & 1) != 0) {
            return -1.0F;
        } else {
            return (this.gamepadState.hatState & 4) != 0 ? 1.0F : 0.0F;
        }
    }

    public boolean isButtonPressed(int arg0) {
        if (this.gamepadState == null || !this.gamepadState.bPolled) {
            return false;
        } else {
            return arg0 >= 0 && arg0 < 15 ? this.gamepadState.axesButtons.buttons(arg0) == 1 : false;
        }
    }

    public boolean isButtonRelease(int arg0) {
        if (this.gamepadState == null || !this.gamepadState.bPolled) {
            return false;
        } else {
            return arg0 >= 0 && arg0 < 15 ? this.gamepadState.axesButtons.buttons(arg0) == 0 : false;
        }
    }

    public String getButtonName(int arg0) {
        return arg0 >= buttonNames.length ? "Extra button " + (arg0 - buttonNames.length + 1) : buttonNames[arg0];
    }

    public void poll(GamepadState arg0) {
        if (GLFW.glfwGetGamepadState(this.id, arg0.axesButtons)) {
            arg0.bPolled = true;
            ByteBuffer byteBuffer = this.getJoystickHats(this.id, arg0.hats);
            arg0.hatState = byteBuffer.remaining() == 0 ? 0 : byteBuffer.get(0);
        } else {
            arg0.bPolled = false;
        }
    }
}
