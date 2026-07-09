// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.input;

import java.util.ArrayList;
import org.lwjglx.input.Controller;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.input.ControllerState;
import zombie.input.ControllerStateCache;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;

/**
 * A wrapped for all keyboard, mouse and controller input
 */
public final class Input {
    /**
     * The controller index to pass to check all controllers
     */
    public static final int ANY_CONTROLLER = -1;
    private final Controller[] controllers = new Controller[16];
    private final ArrayList<Controller> newlyConnected = new ArrayList<>();
    private final ArrayList<Controller> newlyDisconnected = new ArrayList<>();
    private final boolean[][] controllerPressed = new boolean[16][15];
    private final boolean[][] controllerWasPressed = new boolean[16][15];
    private final float[][] controllerPov = new float[16][2];
    private final ControllerStateCache m_controllerStateCache = new ControllerStateCache();

    /**
     * get the character representation of the key identified by the specified code
     * 
     * @param code The key code of the key to retrieve the name of
     * @return The name or character representation of the key requested
     */
    public static String getKeyName(int code) {
        String string = Keyboard.getKeyName(code);
        if ("LSHIFT".equals(string)) {
            string = "Left SHIFT";
        }

        if ("RSHIFT".equals(string)) {
            string = "Right SHIFT";
        }

        if ("LMENU".equals(string)) {
            string = "Left ALT";
        } else if ("RMENU".equals(string)) {
            string = "Right ALT";
        }

        return string;
    }

    public static int getKeyCode(String keyName) {
        if ("Right SHIFT".equals(keyName)) {
            return 54;
        } else if ("Left SHIFT".equals(keyName)) {
            return 42;
        } else if ("Left ALT".equals(keyName)) {
            return 56;
        } else {
            return "Right ALT".equals(keyName) ? 184 : Keyboard.getKeyIndex(keyName);
        }
    }

    /**
     * get a count of the number of controllers available
     * @return The number of controllers available
     */
    public int getControllerCount() {
        return this.controllers.length;
    }

    /**
     * get the number of axis that are avaiable on a given controller
     * 
     * @param index The index of the controller to check
     * @return The number of axis available on the controller
     */
    public int getAxisCount(int index) {
        Controller controller = this.getController(index);
        return controller == null ? 0 : controller.getAxisCount();
    }

    /**
     * get the value of the axis with the given index
     * 
     * @param index The index of the controller to check
     * @param axis The index of the axis to read
     * @return The axis value at time of reading
     */
    public float getAxisValue(int index, int axis) {
        Controller controller = this.getController(index);
        return controller == null ? 0.0F : controller.getAxisValue(axis);
    }

    /**
     * get the name of the axis with the given index
     * 
     * @param index The index of the controller to check
     * @param axis The index of the axis to read
     * @return The name of the specified axis
     */
    public String getAxisName(int index, int axis) {
        Controller controller = this.getController(index);
        return controller == null ? null : controller.getAxisName(axis);
    }

    /**
     * Check if the controller has the left direction pressed
     * 
     * @param index The index of the controller to check
     * @return True if the controller is pressed to the left
     */
    public boolean isControllerLeftD(int index) {
        if (index == -1) {
            for (int int0 = 0; int0 < this.controllers.length; int0++) {
                if (this.isControllerLeftD(int0)) {
                    return true;
                }
            }

            return false;
        } else {
            Controller controller = this.getController(index);
            return controller == null ? false : controller.getPovX() < -0.5F;
        }
    }

    /**
     * Check if the controller has the right direction pressed
     * 
     * @param index The index of the controller to check
     * @return True if the controller is pressed to the right
     */
    public boolean isControllerRightD(int index) {
        if (index == -1) {
            for (int int0 = 0; int0 < this.controllers.length; int0++) {
                if (this.isControllerRightD(int0)) {
                    return true;
                }
            }

            return false;
        } else {
            Controller controller = this.getController(index);
            return controller == null ? false : controller.getPovX() > 0.5F;
        }
    }

    /**
     * Check if the controller has the up direction pressed
     * 
     * @param index The index of the controller to check
     * @return True if the controller is pressed to the up
     */
    public boolean isControllerUpD(int index) {
        if (index == -1) {
            for (int int0 = 0; int0 < this.controllers.length; int0++) {
                if (this.isControllerUpD(int0)) {
                    return true;
                }
            }

            return false;
        } else {
            Controller controller = this.getController(index);
            return controller == null ? false : controller.getPovY() < -0.5F;
        }
    }

    /**
     * Check if the controller has the down direction pressed
     * 
     * @param index The index of the controller to check
     * @return True if the controller is pressed to the down
     */
    public boolean isControllerDownD(int index) {
        if (index == -1) {
            for (int int0 = 0; int0 < this.controllers.length; int0++) {
                if (this.isControllerDownD(int0)) {
                    return true;
                }
            }

            return false;
        } else {
            Controller controller = this.getController(index);
            return controller == null ? false : controller.getPovY() > 0.5F;
        }
    }

    private Controller checkControllerButton(int int0, int int1) {
        Controller controller = this.getController(int0);
        if (controller == null) {
            return null;
        } else {
            return int1 >= 0 && int1 < controller.getButtonCount() ? controller : null;
        }
    }

    /**
     * Check if controller button is pressed
     * 
     * @param button The index of the button to check
     * @param index The index of the controller to check
     * @return True if the button is pressed
     */
    public boolean isButtonPressedD(int button, int index) {
        if (index == -1) {
            for (int int0 = 0; int0 < this.controllers.length; int0++) {
                if (this.isButtonPressedD(button, int0)) {
                    return true;
                }
            }

            return false;
        } else {
            Controller controller = this.checkControllerButton(index, button);
            return controller == null ? false : this.controllerPressed[index][button];
        }
    }

    /**
     * Check if a controller button was pressed the previous frame.
     * 
     * @param index The controller index.
     * @param button The button index.
     * @return true if the controller button was in the pressed state the previous frame.
     */
    public boolean wasButtonPressed(int index, int button) {
        Controller controller = this.checkControllerButton(index, button);
        return controller == null ? false : this.controllerWasPressed[index][button];
    }

    public boolean isButtonStartPress(int index, int button) {
        return !this.wasButtonPressed(index, button) && this.isButtonPressedD(button, index);
    }

    public boolean isButtonReleasePress(int index, int button) {
        return this.wasButtonPressed(index, button) && !this.isButtonPressedD(button, index);
    }

    /**
     * Initialise the controllers system
     */
    public void initControllers() {
        this.updateGameThread();
        this.updateGameThread();
    }

    private void onControllerConnected(Controller controller) {
        JoypadManager.instance.onControllerConnected(controller);
        if (LuaManager.env != null) {
            LuaEventManager.triggerEvent("OnGamepadConnect", controller.getID());
        }
    }

    private void onControllerDisconnected(Controller controller) {
        JoypadManager.instance.onControllerDisconnected(controller);
        if (LuaManager.env != null) {
            LuaEventManager.triggerEvent("OnGamepadDisconnect", controller.getID());
        }
    }

    /**
     * Poll the state of the input
     */
    public void poll() {
        if (!Core.getInstance().isDoingTextEntry()) {
            while (GameKeyboard.getEventQueuePolling().next()) {
            }
        }

        while (Mouse.next()) {
        }

        this.m_controllerStateCache.poll();
    }

    public Controller getController(int index) {
        return index >= 0 && index < this.controllers.length ? this.controllers[index] : null;
    }

    public int getButtonCount(int index) {
        Controller controller = this.getController(index);
        return controller == null ? null : controller.getButtonCount();
    }

    public String getButtonName(int index, int button) {
        Controller controller = this.getController(index);
        return controller == null ? null : controller.getButtonName(button);
    }

    public void updateGameThread() {
        if (!this.m_controllerStateCache.getState().isCreated()) {
            this.m_controllerStateCache.swap();
        } else {
            ControllerState controllerState = this.m_controllerStateCache.getState();
            if (this.checkConnectDisconnect(controllerState)) {
                for (int int0 = 0; int0 < this.newlyDisconnected.size(); int0++) {
                    Controller controller0 = this.newlyDisconnected.get(int0);
                    this.onControllerDisconnected(controller0);
                }

                for (int int1 = 0; int1 < this.newlyConnected.size(); int1++) {
                    Controller controller1 = this.newlyConnected.get(int1);
                    this.onControllerConnected(controller1);
                }
            }

            for (int int2 = 0; int2 < this.getControllerCount(); int2++) {
                Controller controller2 = this.getController(int2);
                if (controller2 != null) {
                    int int3 = controller2.getButtonCount();

                    for (int int4 = 0; int4 < int3; int4++) {
                        this.controllerWasPressed[int2][int4] = this.controllerPressed[int2][int4];
                        if (this.controllerPressed[int2][int4] && !controller2.isButtonPressed(int4)) {
                            this.controllerPressed[int2][int4] = false;
                        } else if (!this.controllerPressed[int2][int4] && controller2.isButtonPressed(int4)) {
                            this.controllerPressed[int2][int4] = true;
                            JoypadManager.instance.onPressed(int2, int4);
                        }
                    }

                    int3 = controller2.getAxisCount();

                    for (int int5 = 0; int5 < int3; int5++) {
                        float float0 = controller2.getAxisValue(int5);
                        if ((!controller2.isGamepad() || int5 != 4) && int5 != 5) {
                            if (float0 < -0.5F) {
                                JoypadManager.instance.onPressedAxisNeg(int2, int5);
                            }

                            if (float0 > 0.5F) {
                                JoypadManager.instance.onPressedAxis(int2, int5);
                            }
                        } else if (float0 > 0.0F) {
                            JoypadManager.instance.onPressedTrigger(int2, int5);
                        }
                    }

                    float float1 = controller2.getPovX();
                    float float2 = controller2.getPovY();
                    if (float1 != this.controllerPov[int2][0] || float2 != this.controllerPov[int2][1]) {
                        this.controllerPov[int2][0] = float1;
                        this.controllerPov[int2][1] = float2;
                        JoypadManager.instance.onPressedPov(int2);
                    }
                }
            }

            this.m_controllerStateCache.swap();
        }
    }

    private boolean checkConnectDisconnect(ControllerState controllerState) {
        boolean boolean0 = false;
        this.newlyConnected.clear();
        this.newlyDisconnected.clear();

        for (int int0 = 0; int0 < 16; int0++) {
            Controller controller = controllerState.getController(int0);
            if (controller != this.controllers[int0]) {
                boolean0 = true;
                if (controller != null && controller.isGamepad()) {
                    this.newlyConnected.add(controller);
                } else {
                    if (this.controllers[int0] != null) {
                        this.newlyDisconnected.add(this.controllers[int0]);
                    }

                    controller = null;
                }

                this.controllers[int0] = controller;
            }
        }

        return boolean0;
    }

    public void quit() {
        this.m_controllerStateCache.quit();
    }
}
