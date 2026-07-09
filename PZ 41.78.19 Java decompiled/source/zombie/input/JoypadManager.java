// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.input;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import org.lwjglx.input.Controller;
import zombie.GameWindow;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.iso.Vector2;

public final class JoypadManager {
    public static final JoypadManager instance = new JoypadManager();
    public final JoypadManager.Joypad[] Joypads = new JoypadManager.Joypad[4];
    public final JoypadManager.Joypad[] JoypadsController = new JoypadManager.Joypad[16];
    public final ArrayList<JoypadManager.Joypad> JoypadList = new ArrayList<>();
    public final HashSet<String> ActiveControllerGUIDs = new HashSet<>();
    private static final int VERSION_1 = 1;
    private static final int VERSION_2 = 2;
    private static final int VERSION_LATEST = 2;

    public JoypadManager.Joypad addJoypad(int int0, String string0, String string1) {
        JoypadManager.Joypad joypad = new JoypadManager.Joypad();
        joypad.ID = int0;
        joypad.guid = string0;
        joypad.name = string1;
        this.JoypadsController[int0] = joypad;
        this.doControllerFile(joypad);
        if (!joypad.isDisabled() && this.ActiveControllerGUIDs.contains(string0)) {
            this.JoypadList.add(joypad);
        }

        return joypad;
    }

    private JoypadManager.Joypad checkJoypad(int int0) {
        if (this.JoypadsController[int0] == null) {
            Controller controller = GameWindow.GameInput.getController(int0);
            this.addJoypad(int0, controller.getGUID(), controller.getGamepadName());
        }

        return this.JoypadsController[int0];
    }

    private void doControllerFile(JoypadManager.Joypad joypad) {
        File file = new File(ZomboidFileSystem.instance.getCacheDirSub("joypads"));
        if (!file.exists()) {
            file.mkdir();
        }

        file = new File(ZomboidFileSystem.instance.getCacheDirSub("joypads" + File.separator + joypad.guid + ".config"));

        try (
            FileReader fileReader = new FileReader(file.getAbsolutePath());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
        ) {
            System.out.println("reloading " + file.getAbsolutePath());
            int int0 = -1;

            try {
                String string = "";

                while (string != null) {
                    string = bufferedReader.readLine();
                    if (string != null && string.trim().length() != 0 && !string.trim().startsWith("//")) {
                        String[] strings = string.split("=");
                        if (strings.length == 2) {
                            strings[0] = strings[0].trim();
                            strings[1] = strings[1].trim();
                            if (strings[0].equals("Version")) {
                                int0 = Integer.parseInt(strings[1]);
                                if (int0 < 1 || int0 > 2) {
                                    DebugLog.General.warn("Unknown version %d in %s", int0, file.getAbsolutePath());
                                    break;
                                }

                                if (int0 == 1) {
                                    DebugLog.General.warn("Obsolete version %d in %s.  Using default values.", int0, file.getAbsolutePath());
                                    break;
                                }
                            }

                            if (int0 == -1) {
                                DebugLog.General.warn("Ignoring %s=%s because Version is missing", strings[0], strings[1]);
                            } else if (strings[0].equals("MovementAxisX")) {
                                joypad.MovementAxisX = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("MovementAxisXFlipped")) {
                                joypad.MovementAxisXFlipped = strings[1].equals("true");
                            } else if (strings[0].equals("MovementAxisY")) {
                                joypad.MovementAxisY = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("MovementAxisYFlipped")) {
                                joypad.MovementAxisYFlipped = strings[1].equals("true");
                            } else if (strings[0].equals("MovementAxisDeadZone")) {
                                joypad.MovementAxisDeadZone = Float.parseFloat(strings[1]);
                            } else if (strings[0].equals("AimingAxisX")) {
                                joypad.AimingAxisX = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("AimingAxisXFlipped")) {
                                joypad.AimingAxisXFlipped = strings[1].equals("true");
                            } else if (strings[0].equals("AimingAxisY")) {
                                joypad.AimingAxisY = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("AimingAxisYFlipped")) {
                                joypad.AimingAxisYFlipped = strings[1].equals("true");
                            } else if (strings[0].equals("AimingAxisDeadZone")) {
                                joypad.AimingAxisDeadZone = Float.parseFloat(strings[1]);
                            } else if (strings[0].equals("AButton")) {
                                joypad.AButton = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("BButton")) {
                                joypad.BButton = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("XButton")) {
                                joypad.XButton = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("YButton")) {
                                joypad.YButton = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("LBumper")) {
                                joypad.BumperLeft = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("RBumper")) {
                                joypad.BumperRight = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("L3")) {
                                joypad.LeftStickButton = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("R3")) {
                                joypad.RightStickButton = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("Back")) {
                                joypad.Back = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("Start")) {
                                joypad.Start = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("DPadUp")) {
                                joypad.DPadUp = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("DPadDown")) {
                                joypad.DPadDown = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("DPadLeft")) {
                                joypad.DPadLeft = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("DPadRight")) {
                                joypad.DPadRight = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("TriggersFlipped")) {
                                joypad.TriggersFlipped = strings[1].equals("true");
                            } else if (strings[0].equals("TriggerLeft")) {
                                joypad.TriggerLeft = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("TriggerRight")) {
                                joypad.TriggerRight = Integer.parseInt(strings[1]);
                            } else if (strings[0].equals("Disabled")) {
                                joypad.Disabled = strings[1].equals("true");
                            } else if (strings[0].equals("Sensitivity")) {
                                joypad.setDeadZone(Float.parseFloat(strings[1]));
                            }
                        }
                    }
                }
            } catch (Exception exception0) {
                ExceptionLogger.logException(exception0);
            }
        } catch (FileNotFoundException fileNotFoundException) {
            if (!this.ActiveControllerGUIDs.contains(joypad.guid)) {
                this.ActiveControllerGUIDs.add(joypad.guid);

                try {
                    Core.getInstance().saveOptions();
                } catch (Exception exception1) {
                    ExceptionLogger.logException(exception1);
                }
            }
        } catch (IOException iOException) {
            ExceptionLogger.logException(iOException);
        }

        this.saveFile(joypad);
    }

    private void saveFile(JoypadManager.Joypad joypad) {
        File file = new File(ZomboidFileSystem.instance.getCacheDir() + File.separator + "joypads");
        if (!file.exists()) {
            file.mkdir();
        }

        file = new File(ZomboidFileSystem.instance.getCacheDirSub("joypads" + File.separator + joypad.guid + ".config"));

        try (
            FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        ) {
            String string = System.getProperty("line.separator");
            bufferedWriter.write("Version=2" + string);
            bufferedWriter.write("Name=" + joypad.name + string);
            bufferedWriter.write("MovementAxisX=" + joypad.MovementAxisX + string);
            bufferedWriter.write("MovementAxisXFlipped=" + joypad.MovementAxisXFlipped + string);
            bufferedWriter.write("MovementAxisY=" + joypad.MovementAxisY + string);
            bufferedWriter.write("MovementAxisYFlipped=" + joypad.MovementAxisYFlipped + string);
            bufferedWriter.write("// Set the dead zone to the smallest number between 0.0 and 1.0." + string);
            bufferedWriter.write("// This is to fix \"loose sticks\"." + string);
            bufferedWriter.write("MovementAxisDeadZone=" + joypad.MovementAxisDeadZone + string);
            bufferedWriter.write("AimingAxisX=" + joypad.AimingAxisX + string);
            bufferedWriter.write("AimingAxisXFlipped=" + joypad.AimingAxisXFlipped + string);
            bufferedWriter.write("AimingAxisY=" + joypad.AimingAxisY + string);
            bufferedWriter.write("AimingAxisYFlipped=" + joypad.AimingAxisYFlipped + string);
            bufferedWriter.write("AimingAxisDeadZone=" + joypad.AimingAxisDeadZone + string);
            bufferedWriter.write("AButton=" + joypad.AButton + string);
            bufferedWriter.write("BButton=" + joypad.BButton + string);
            bufferedWriter.write("XButton=" + joypad.XButton + string);
            bufferedWriter.write("YButton=" + joypad.YButton + string);
            bufferedWriter.write("LBumper=" + joypad.BumperLeft + string);
            bufferedWriter.write("RBumper=" + joypad.BumperRight + string);
            bufferedWriter.write("L3=" + joypad.LeftStickButton + string);
            bufferedWriter.write("R3=" + joypad.RightStickButton + string);
            bufferedWriter.write("Back=" + joypad.Back + string);
            bufferedWriter.write("Start=" + joypad.Start + string);
            bufferedWriter.write("// Normally the D-pad is treated as a single axis (the POV Hat), and these should be -1." + string);
            bufferedWriter.write("// If your D-pad is actually 4 separate buttons, set the button numbers here." + string);
            bufferedWriter.write("DPadUp=" + joypad.DPadUp + string);
            bufferedWriter.write("DPadDown=" + joypad.DPadDown + string);
            bufferedWriter.write("DPadLeft=" + joypad.DPadLeft + string);
            bufferedWriter.write("DPadRight=" + joypad.DPadRight + string);
            bufferedWriter.write("TriggersFlipped=" + joypad.TriggersFlipped + string);
            bufferedWriter.write("// If your triggers are buttons, set the button numbers here." + string);
            bufferedWriter.write("// If these are set to something other than -1, then Triggers= is ignored." + string);
            bufferedWriter.write("TriggerLeft=" + joypad.TriggerLeft + string);
            bufferedWriter.write("TriggerRight=" + joypad.TriggerRight + string);
            bufferedWriter.write("Disabled=" + joypad.Disabled + string);
            bufferedWriter.write("Sensitivity=" + joypad.getDeadZone(0) + string);
        } catch (IOException iOException) {
            ExceptionLogger.logException(iOException);
        }
    }

    public void reloadControllerFiles() {
        for (int int0 = 0; int0 < GameWindow.GameInput.getControllerCount(); int0++) {
            Controller controller = GameWindow.GameInput.getController(int0);
            if (controller != null) {
                if (this.JoypadsController[int0] == null) {
                    this.addJoypad(int0, controller.getGUID(), controller.getGamepadName());
                } else {
                    this.doControllerFile(this.JoypadsController[int0]);
                }
            }
        }
    }

    public void assignJoypad(int int0, int int1) {
        this.checkJoypad(int0);
        this.Joypads[int1] = this.JoypadsController[int0];
        this.Joypads[int1].player = int1;
    }

    public JoypadManager.Joypad getFromPlayer(int int0) {
        return this.Joypads[int0];
    }

    public JoypadManager.Joypad getFromControllerID(int int0) {
        return this.JoypadsController[int0];
    }

    public void onPressed(int int0, int int1) {
        this.checkJoypad(int0);
        this.JoypadsController[int0].onPressed(int1);
    }

    public boolean isDownPressed(int int0) {
        this.checkJoypad(int0);
        return this.JoypadsController[int0].isDownPressed();
    }

    public boolean isUpPressed(int int0) {
        this.checkJoypad(int0);
        return this.JoypadsController[int0].isUpPressed();
    }

    public boolean isRightPressed(int int0) {
        this.checkJoypad(int0);
        return this.JoypadsController[int0].isRightPressed();
    }

    public boolean isLeftPressed(int int0) {
        this.checkJoypad(int0);
        return this.JoypadsController[int0].isLeftPressed();
    }

    public boolean isLBPressed(int int0) {
        if (int0 < 0) {
            for (int int1 = 0; int1 < this.JoypadList.size(); int1++) {
                if (this.JoypadList.get(int1).isLBPressed()) {
                    return true;
                }
            }

            return false;
        } else {
            this.checkJoypad(int0);
            return this.JoypadsController[int0].isLBPressed();
        }
    }

    public boolean isRBPressed(int int0) {
        if (int0 < 0) {
            for (int int1 = 0; int1 < this.JoypadList.size(); int1++) {
                if (this.JoypadList.get(int1).isRBPressed()) {
                    return true;
                }
            }

            return false;
        } else {
            this.checkJoypad(int0);
            return this.JoypadsController[int0].isRBPressed();
        }
    }

    public boolean isL3Pressed(int int0) {
        if (int0 < 0) {
            for (int int1 = 0; int1 < this.JoypadList.size(); int1++) {
                if (this.JoypadList.get(int1).isL3Pressed()) {
                    return true;
                }
            }

            return false;
        } else {
            this.checkJoypad(int0);
            return this.JoypadsController[int0].isL3Pressed();
        }
    }

    public boolean isR3Pressed(int int0) {
        if (int0 < 0) {
            for (int int1 = 0; int1 < this.JoypadList.size(); int1++) {
                if (this.JoypadList.get(int1).isR3Pressed()) {
                    return true;
                }
            }

            return false;
        } else {
            this.checkJoypad(int0);
            return this.JoypadsController[int0].isR3Pressed();
        }
    }

    public boolean isRTPressed(int int0) {
        if (int0 < 0) {
            for (int int1 = 0; int1 < this.JoypadList.size(); int1++) {
                if (this.JoypadList.get(int1).isRTPressed()) {
                    return true;
                }
            }

            return false;
        } else {
            this.checkJoypad(int0);
            return this.JoypadsController[int0].isRTPressed();
        }
    }

    public boolean isLTPressed(int int0) {
        if (int0 < 0) {
            for (int int1 = 0; int1 < this.JoypadList.size(); int1++) {
                if (this.JoypadList.get(int1).isLTPressed()) {
                    return true;
                }
            }

            return false;
        } else {
            this.checkJoypad(int0);
            return this.JoypadsController[int0].isLTPressed();
        }
    }

    public boolean isAPressed(int int0) {
        if (int0 < 0) {
            for (int int1 = 0; int1 < this.JoypadList.size(); int1++) {
                if (this.JoypadList.get(int1).isAPressed()) {
                    return true;
                }
            }

            return false;
        } else {
            this.checkJoypad(int0);
            return this.JoypadsController[int0].isAPressed();
        }
    }

    public boolean isBPressed(int int0) {
        if (int0 < 0) {
            for (int int1 = 0; int1 < this.JoypadList.size(); int1++) {
                if (this.JoypadList.get(int1).isBPressed()) {
                    return true;
                }
            }

            return false;
        } else {
            this.checkJoypad(int0);
            return this.JoypadsController[int0].isBPressed();
        }
    }

    public boolean isXPressed(int int0) {
        if (int0 < 0) {
            for (int int1 = 0; int1 < this.JoypadList.size(); int1++) {
                if (this.JoypadList.get(int1).isXPressed()) {
                    return true;
                }
            }

            return false;
        } else {
            this.checkJoypad(int0);
            return this.JoypadsController[int0].isXPressed();
        }
    }

    public boolean isYPressed(int int0) {
        if (int0 < 0) {
            for (int int1 = 0; int1 < this.JoypadList.size(); int1++) {
                if (this.JoypadList.get(int1).isYPressed()) {
                    return true;
                }
            }

            return false;
        } else {
            this.checkJoypad(int0);
            return this.JoypadsController[int0].isYPressed();
        }
    }

    public boolean isButtonStartPress(int int0, int int1) {
        JoypadManager.Joypad joypad = this.checkJoypad(int0);
        return joypad.isButtonStartPress(int1);
    }

    public boolean isButtonReleasePress(int int0, int int1) {
        JoypadManager.Joypad joypad = this.checkJoypad(int0);
        return joypad.isButtonReleasePress(int1);
    }

    public boolean isAButtonStartPress(int int0) {
        JoypadManager.Joypad joypad = this.checkJoypad(int0);
        return this.isButtonStartPress(int0, joypad.getAButton());
    }

    public boolean isBButtonStartPress(int int0) {
        JoypadManager.Joypad joypad = this.checkJoypad(int0);
        return joypad.isButtonStartPress(joypad.getBButton());
    }

    public boolean isXButtonStartPress(int int0) {
        JoypadManager.Joypad joypad = this.checkJoypad(int0);
        return joypad.isButtonStartPress(joypad.getXButton());
    }

    public boolean isYButtonStartPress(int int0) {
        JoypadManager.Joypad joypad = this.checkJoypad(int0);
        return joypad.isButtonStartPress(joypad.getYButton());
    }

    public boolean isAButtonReleasePress(int int0) {
        JoypadManager.Joypad joypad = this.checkJoypad(int0);
        return joypad.isButtonReleasePress(joypad.getAButton());
    }

    public boolean isBButtonReleasePress(int int0) {
        JoypadManager.Joypad joypad = this.checkJoypad(int0);
        return joypad.isButtonReleasePress(joypad.getBButton());
    }

    public boolean isXButtonReleasePress(int int0) {
        JoypadManager.Joypad joypad = this.checkJoypad(int0);
        return joypad.isButtonReleasePress(joypad.getXButton());
    }

    public boolean isYButtonReleasePress(int int0) {
        JoypadManager.Joypad joypad = this.checkJoypad(int0);
        return joypad.isButtonReleasePress(joypad.getYButton());
    }

    public float getMovementAxisX(int int0) {
        this.checkJoypad(int0);
        return this.JoypadsController[int0].getMovementAxisX();
    }

    public float getMovementAxisY(int int0) {
        this.checkJoypad(int0);
        return this.JoypadsController[int0].getMovementAxisY();
    }

    public float getAimingAxisX(int int0) {
        this.checkJoypad(int0);
        return this.JoypadsController[int0].getAimingAxisX();
    }

    public float getAimingAxisY(int int0) {
        this.checkJoypad(int0);
        return this.JoypadsController[int0].getAimingAxisY();
    }

    public void onPressedAxis(int int0, int int1) {
        this.checkJoypad(int0);
        this.JoypadsController[int0].onPressedAxis(int1);
    }

    public void onPressedAxisNeg(int int0, int int1) {
        this.checkJoypad(int0);
        this.JoypadsController[int0].onPressedAxisNeg(int1);
    }

    public void onPressedTrigger(int int0, int int1) {
        this.checkJoypad(int0);
        this.JoypadsController[int0].onPressedTrigger(int1);
    }

    public void onPressedPov(int int0) {
        this.checkJoypad(int0);
        this.JoypadsController[int0].onPressedPov();
    }

    public float getDeadZone(int int0, int int1) {
        this.checkJoypad(int0);
        return this.JoypadsController[int0].getDeadZone(int1);
    }

    public void setDeadZone(int int0, int int1, float float0) {
        this.checkJoypad(int0);
        this.JoypadsController[int0].setDeadZone(int1, float0);
    }

    public void saveControllerSettings(int int0) {
        this.checkJoypad(int0);
        this.saveFile(this.JoypadsController[int0]);
    }

    public long getLastActivity(int int0) {
        return this.JoypadsController[int0] == null ? 0L : this.JoypadsController[int0].lastActivity;
    }

    public void setControllerActive(String string, boolean boolean0) {
        if (boolean0) {
            this.ActiveControllerGUIDs.add(string);
        } else {
            this.ActiveControllerGUIDs.remove(string);
        }

        this.syncActiveControllers();
    }

    public void syncActiveControllers() {
        this.JoypadList.clear();

        for (int int0 = 0; int0 < this.JoypadsController.length; int0++) {
            JoypadManager.Joypad joypad = this.JoypadsController[int0];
            if (joypad != null && !joypad.isDisabled() && this.ActiveControllerGUIDs.contains(joypad.guid)) {
                this.JoypadList.add(joypad);
            }
        }
    }

    public boolean isJoypadConnected(int int0) {
        if (int0 >= 0 && int0 < 16) {
            assert Thread.currentThread() == GameWindow.GameThread;

            return GameWindow.GameInput.getController(int0) != null;
        } else {
            return false;
        }
    }

    public void onControllerConnected(Controller controller) {
        JoypadManager.Joypad joypad = this.JoypadsController[controller.getID()];
        if (joypad != null) {
            LuaEventManager.triggerEvent("OnJoypadBeforeReactivate", BoxedStaticValues.toDouble(joypad.getID()));
            joypad.bConnected = true;
            LuaEventManager.triggerEvent("OnJoypadReactivate", BoxedStaticValues.toDouble(joypad.getID()));
        }
    }

    public void onControllerDisconnected(Controller controller) {
        JoypadManager.Joypad joypad = this.JoypadsController[controller.getID()];
        if (joypad != null) {
            LuaEventManager.triggerEvent("OnJoypadBeforeDeactivate", BoxedStaticValues.toDouble(joypad.getID()));
            joypad.bConnected = false;
            LuaEventManager.triggerEvent("OnJoypadDeactivate", BoxedStaticValues.toDouble(joypad.getID()));
        }
    }

    public void revertToKeyboardAndMouse() {
        for (int int0 = 0; int0 < this.JoypadList.size(); int0++) {
            JoypadManager.Joypad joypad = this.JoypadList.get(int0);
            if (joypad.player == 0) {
                if (GameWindow.ActivatedJoyPad == joypad) {
                    GameWindow.ActivatedJoyPad = null;
                }

                IsoPlayer player = IsoPlayer.players[0];
                if (player != null) {
                    player.JoypadBind = -1;
                }

                this.JoypadsController[joypad.getID()] = null;
                this.Joypads[0] = null;
                this.JoypadList.remove(int0);
                break;
            }
        }
    }

    public void renderUI() {
        assert Thread.currentThread() == GameWindow.GameThread;

        if (DebugOptions.instance.JoypadRenderUI.getValue()) {
            if (!GameWindow.DrawReloadingLua) {
                LuaEventManager.triggerEvent("OnJoypadRenderUI");
            }
        }
    }

    public void Reset() {
        for (int int0 = 0; int0 < this.Joypads.length; int0++) {
            this.Joypads[int0] = null;
        }
    }

    public static final class Joypad {
        String guid;
        String name;
        int ID;
        int player = -1;
        int MovementAxisX = 0;
        boolean MovementAxisXFlipped = false;
        int MovementAxisY = 1;
        boolean MovementAxisYFlipped = false;
        float MovementAxisDeadZone = 0.0F;
        int AimingAxisX = 2;
        boolean AimingAxisXFlipped = false;
        int AimingAxisY = 3;
        boolean AimingAxisYFlipped = false;
        float AimingAxisDeadZone = 0.0F;
        int AButton = 0;
        int BButton = 1;
        int XButton = 2;
        int YButton = 3;
        int DPadUp = -1;
        int DPadDown = -1;
        int DPadLeft = -1;
        int DPadRight = -1;
        int BumperLeft = 4;
        int BumperRight = 5;
        int Back = 6;
        int Start = 7;
        int LeftStickButton = 9;
        int RightStickButton = 10;
        boolean TriggersFlipped = false;
        int TriggerLeft = 4;
        int TriggerRight = 5;
        boolean Disabled = false;
        boolean bConnected = true;
        long lastActivity;
        private static final Vector2 tempVec2 = new Vector2();

        public boolean isDownPressed() {
            return this.DPadDown != -1 ? GameWindow.GameInput.isButtonPressedD(this.DPadDown, this.ID) : GameWindow.GameInput.isControllerDownD(this.ID);
        }

        public boolean isUpPressed() {
            return this.DPadUp != -1 ? GameWindow.GameInput.isButtonPressedD(this.DPadUp, this.ID) : GameWindow.GameInput.isControllerUpD(this.ID);
        }

        public boolean isRightPressed() {
            return this.DPadRight != -1 ? GameWindow.GameInput.isButtonPressedD(this.DPadRight, this.ID) : GameWindow.GameInput.isControllerRightD(this.ID);
        }

        public boolean isLeftPressed() {
            return this.DPadLeft != -1 ? GameWindow.GameInput.isButtonPressedD(this.DPadLeft, this.ID) : GameWindow.GameInput.isControllerLeftD(this.ID);
        }

        public boolean isLBPressed() {
            return GameWindow.GameInput.isButtonPressedD(this.BumperLeft, this.ID);
        }

        public boolean isRBPressed() {
            return GameWindow.GameInput.isButtonPressedD(this.BumperRight, this.ID);
        }

        public boolean isL3Pressed() {
            return GameWindow.GameInput.isButtonPressedD(this.LeftStickButton, this.ID);
        }

        public boolean isR3Pressed() {
            return GameWindow.GameInput.isButtonPressedD(this.RightStickButton, this.ID);
        }

        public boolean isRTPressed() {
            int int0 = this.TriggerRight;
            if (GameWindow.GameInput.getAxisCount(this.ID) <= int0) {
                return this.isRBPressed();
            } else {
                return this.TriggersFlipped
                    ? GameWindow.GameInput.getAxisValue(this.ID, int0) < -0.7F
                    : GameWindow.GameInput.getAxisValue(this.ID, int0) > 0.7F;
            }
        }

        public boolean isLTPressed() {
            int int0 = this.TriggerLeft;
            if (GameWindow.GameInput.getAxisCount(this.ID) <= int0) {
                return this.isLBPressed();
            } else {
                return this.TriggersFlipped
                    ? GameWindow.GameInput.getAxisValue(this.ID, int0) < -0.7F
                    : GameWindow.GameInput.getAxisValue(this.ID, int0) > 0.7F;
            }
        }

        public boolean isAPressed() {
            return GameWindow.GameInput.isButtonPressedD(this.AButton, this.ID);
        }

        public boolean isBPressed() {
            return GameWindow.GameInput.isButtonPressedD(this.BButton, this.ID);
        }

        public boolean isXPressed() {
            return GameWindow.GameInput.isButtonPressedD(this.XButton, this.ID);
        }

        public boolean isYPressed() {
            return GameWindow.GameInput.isButtonPressedD(this.YButton, this.ID);
        }

        public boolean isButtonPressed(int button) {
            return GameWindow.GameInput.isButtonPressedD(button, this.ID);
        }

        public boolean wasButtonPressed(int button) {
            return GameWindow.GameInput.wasButtonPressed(this.ID, button);
        }

        public boolean isButtonStartPress(int button) {
            return GameWindow.GameInput.isButtonStartPress(this.ID, button);
        }

        public boolean isButtonReleasePress(int button) {
            return GameWindow.GameInput.isButtonReleasePress(this.ID, button);
        }

        public float getMovementAxisX() {
            if (GameWindow.GameInput.getAxisCount(this.ID) <= this.MovementAxisX) {
                return 0.0F;
            } else {
                this.MovementAxisDeadZone = GameWindow.GameInput.getController(this.ID).getDeadZone(this.MovementAxisX);
                float float0 = this.MovementAxisDeadZone;
                if (float0 > 0.0F && float0 < 1.0F) {
                    float float1 = GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisX);
                    float float2 = GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisY);
                    Vector2 vector = tempVec2.set(float1, float2);
                    if (vector.getLength() < float0) {
                        vector.set(0.0F, 0.0F);
                    } else {
                        vector.setLength((vector.getLength() - float0) / (1.0F - float0));
                    }

                    return this.MovementAxisXFlipped ? -vector.getX() : vector.getX();
                } else {
                    return this.MovementAxisXFlipped
                        ? -GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisX)
                        : GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisX);
                }
            }
        }

        public float getMovementAxisY() {
            if (GameWindow.GameInput.getAxisCount(this.ID) <= this.MovementAxisY) {
                return 0.0F;
            } else {
                this.MovementAxisDeadZone = GameWindow.GameInput.getController(this.ID).getDeadZone(this.MovementAxisY);
                float float0 = this.MovementAxisDeadZone;
                if (float0 > 0.0F && float0 < 1.0F) {
                    float float1 = GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisX);
                    float float2 = GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisY);
                    Vector2 vector = tempVec2.set(float1, float2);
                    if (vector.getLength() < float0) {
                        vector.set(0.0F, 0.0F);
                    } else {
                        vector.setLength((vector.getLength() - float0) / (1.0F - float0));
                    }

                    return this.MovementAxisYFlipped ? -vector.getY() : vector.getY();
                } else {
                    return this.MovementAxisYFlipped
                        ? -GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisY)
                        : GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisY);
                }
            }
        }

        public float getAimingAxisX() {
            if (GameWindow.GameInput.getAxisCount(this.ID) <= this.AimingAxisX) {
                return 0.0F;
            } else {
                this.AimingAxisDeadZone = GameWindow.GameInput.getController(this.ID).getDeadZone(this.AimingAxisX);
                float float0 = this.AimingAxisDeadZone;
                if (float0 > 0.0F && float0 < 1.0F) {
                    float float1 = GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisX);
                    float float2 = GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisY);
                    Vector2 vector = tempVec2.set(float1, float2);
                    if (vector.getLength() < float0) {
                        vector.set(0.0F, 0.0F);
                    } else {
                        vector.setLength((vector.getLength() - float0) / (1.0F - float0));
                    }

                    return this.AimingAxisXFlipped ? -vector.getX() : vector.getX();
                } else {
                    return this.AimingAxisXFlipped
                        ? -GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisX)
                        : GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisX);
                }
            }
        }

        public float getAimingAxisY() {
            if (GameWindow.GameInput.getAxisCount(this.ID) <= this.AimingAxisY) {
                return 0.0F;
            } else {
                this.AimingAxisDeadZone = GameWindow.GameInput.getController(this.ID).getDeadZone(this.AimingAxisY);
                float float0 = this.AimingAxisDeadZone;
                if (float0 > 0.0F && float0 < 1.0F) {
                    float float1 = GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisX);
                    float float2 = GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisY);
                    Vector2 vector = tempVec2.set(float1, float2);
                    if (vector.getLength() < float0) {
                        vector.set(0.0F, 0.0F);
                    } else {
                        vector.setLength((vector.getLength() - float0) / (1.0F - float0));
                    }

                    return this.AimingAxisYFlipped ? -vector.getY() : vector.getY();
                } else {
                    return this.AimingAxisYFlipped
                        ? -GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisY)
                        : GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisY);
                }
            }
        }

        public void onPressed(int i) {
            this.lastActivity = System.currentTimeMillis();
        }

        public void onPressedAxis(int i) {
            this.lastActivity = System.currentTimeMillis();
        }

        public void onPressedAxisNeg(int i) {
            this.lastActivity = System.currentTimeMillis();
        }

        public void onPressedTrigger(int i) {
            this.lastActivity = System.currentTimeMillis();
        }

        public void onPressedPov() {
            this.lastActivity = System.currentTimeMillis();
        }

        public float getDeadZone(int axis) {
            if (axis >= 0 && axis < GameWindow.GameInput.getAxisCount(this.ID)) {
                float float0 = GameWindow.GameInput.getController(this.ID).getDeadZone(axis);
                float float1 = 0.0F;
                if ((axis == this.MovementAxisX || axis == this.MovementAxisY) && this.MovementAxisDeadZone > 0.0F && this.MovementAxisDeadZone < 1.0F) {
                    float1 = this.MovementAxisDeadZone;
                }

                if ((axis == this.AimingAxisX || axis == this.AimingAxisY) && this.AimingAxisDeadZone > 0.0F && this.AimingAxisDeadZone < 1.0F) {
                    float1 = this.AimingAxisDeadZone;
                }

                return Math.max(float0, float1);
            } else {
                return 0.0F;
            }
        }

        public void setDeadZone(int axis, float value) {
            if (axis >= 0 && axis < GameWindow.GameInput.getAxisCount(this.ID)) {
                GameWindow.GameInput.getController(this.ID).setDeadZone(axis, value);
            }
        }

        public void setDeadZone(float value) {
            for (int int0 = 0; int0 < GameWindow.GameInput.getAxisCount(this.ID); int0++) {
                GameWindow.GameInput.getController(this.ID).setDeadZone(int0, value);
            }
        }

        public int getID() {
            return this.ID;
        }

        public boolean isDisabled() {
            return this.Disabled;
        }

        public int getAButton() {
            return this.AButton;
        }

        public int getBButton() {
            return this.BButton;
        }

        public int getXButton() {
            return this.XButton;
        }

        public int getYButton() {
            return this.YButton;
        }

        public int getLBumper() {
            return this.BumperLeft;
        }

        public int getRBumper() {
            return this.BumperRight;
        }

        public int getL3() {
            return this.LeftStickButton;
        }

        public int getR3() {
            return this.RightStickButton;
        }

        public int getBackButton() {
            return this.Back;
        }

        public int getStartButton() {
            return this.Start;
        }
    }
}
