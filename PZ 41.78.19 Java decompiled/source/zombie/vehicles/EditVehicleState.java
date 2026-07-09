// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Locale;
import org.joml.Vector2f;
import org.joml.Vector3f;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.LuaCaller;
import se.krka.kahlua.j2se.J2SEPlatform;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.skinnedmodel.ModelManager;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.gameStates.GameState;
import zombie.gameStates.GameStateMachine;
import zombie.input.GameKeyboard;
import zombie.scripting.ScriptManager;
import zombie.scripting.ScriptParser;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.VehicleScript;
import zombie.ui.UIElement;
import zombie.ui.UIManager;
import zombie.util.list.PZArrayUtil;

public final class EditVehicleState extends GameState {
    public static EditVehicleState instance;
    private EditVehicleState.LuaEnvironment m_luaEnv;
    private boolean bExit = false;
    private String m_initialScript = null;
    private final ArrayList<UIElement> m_gameUI = new ArrayList<>();
    private final ArrayList<UIElement> m_selfUI = new ArrayList<>();
    private boolean m_bSuspendUI;
    private KahluaTable m_table = null;

    public EditVehicleState() {
        instance = this;
    }

    @Override
    public void enter() {
        instance = this;
        if (this.m_luaEnv == null) {
            this.m_luaEnv = new EditVehicleState.LuaEnvironment(LuaManager.platform, LuaManager.converterManager, LuaManager.env);
        }

        this.saveGameUI();
        if (this.m_selfUI.size() == 0) {
            this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_luaEnv.env.rawget("EditVehicleState_InitUI"));
            if (this.m_table != null && this.m_table.getMetatable() != null) {
                this.m_table.getMetatable().rawset("_LUA_RELOADED_CHECK", Boolean.FALSE);
            }
        } else {
            UIManager.UI.addAll(this.m_selfUI);
            this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_table.rawget("showUI"), this.m_table);
        }

        this.bExit = false;
    }

    @Override
    public void yield() {
        this.restoreGameUI();
    }

    @Override
    public void reenter() {
        this.saveGameUI();
    }

    @Override
    public void exit() {
        this.restoreGameUI();
    }

    @Override
    public void render() {
        byte byte0 = 0;
        Core.getInstance().StartFrame(byte0, true);
        this.renderScene();
        Core.getInstance().EndFrame(byte0);
        Core.getInstance().RenderOffScreenBuffer();
        if (Core.getInstance().StartFrameUI()) {
            this.renderUI();
        }

        Core.getInstance().EndFrameUI();
    }

    @Override
    public GameStateMachine.StateAction update() {
        if (!this.bExit && !GameKeyboard.isKeyPressed(65)) {
            this.updateScene();
            return GameStateMachine.StateAction.Remain;
        } else {
            return GameStateMachine.StateAction.Continue;
        }
    }

    public static EditVehicleState checkInstance() {
        if (instance != null) {
            if (instance.m_table != null && instance.m_table.getMetatable() != null) {
                if (instance.m_table.getMetatable().rawget("_LUA_RELOADED_CHECK") == null) {
                    instance = null;
                }
            } else {
                instance = null;
            }
        }

        return instance == null ? new EditVehicleState() : instance;
    }

    private void saveGameUI() {
        this.m_gameUI.clear();
        this.m_gameUI.addAll(UIManager.UI);
        UIManager.UI.clear();
        this.m_bSuspendUI = UIManager.bSuspend;
        UIManager.bSuspend = false;
        UIManager.setShowPausedMessage(false);
        UIManager.defaultthread = this.m_luaEnv.thread;
    }

    private void restoreGameUI() {
        this.m_selfUI.clear();
        this.m_selfUI.addAll(UIManager.UI);
        UIManager.UI.clear();
        UIManager.UI.addAll(this.m_gameUI);
        UIManager.bSuspend = this.m_bSuspendUI;
        UIManager.setShowPausedMessage(true);
        UIManager.defaultthread = LuaManager.thread;
    }

    private void updateScene() {
        ModelManager.instance.update();
        if (GameKeyboard.isKeyPressed(17)) {
            DebugOptions.instance.ModelRenderWireframe.setValue(!DebugOptions.instance.ModelRenderWireframe.getValue());
        }
    }

    private void renderScene() {
    }

    private void renderUI() {
        UIManager.render();
    }

    public void setTable(KahluaTable table) {
        this.m_table = table;
    }

    public void setScript(String scriptName) {
        if (this.m_table == null) {
            this.m_initialScript = scriptName;
        } else {
            this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_table.rawget("setScript"), this.m_table, scriptName);
        }
    }

    public Object fromLua0(String func) {
        switch (func) {
            case "exit":
                this.bExit = true;
                return null;
            case "getInitialScript":
                return this.m_initialScript;
            default:
                throw new IllegalArgumentException("unhandled \"" + func + "\"");
        }
    }

    public Object fromLua1(String func, Object arg0) {
        byte byte0 = -1;
        switch (func.hashCode()) {
            case 1396535690:
                if (func.equals("writeScript")) {
                    byte0 = 0;
                }
            default:
                switch (byte0) {
                    case 0:
                        VehicleScript vehicleScript = ScriptManager.instance.getVehicle((String)arg0);
                        if (vehicleScript == null) {
                            throw new NullPointerException("vehicle script \"" + arg0 + "\" not found");
                        }

                        ArrayList arrayList = this.readScript(vehicleScript.getFileName());
                        if (arrayList != null) {
                            this.updateScript(vehicleScript.getFileName(), arrayList, vehicleScript);
                        }

                        return null;
                    default:
                        throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\"", func, arg0));
                }
        }
    }

    private ArrayList<String> readScript(String string0) {
        StringBuilder stringBuilder = new StringBuilder();
        string0 = ZomboidFileSystem.instance.getString(string0);
        File file = new File(string0);

        try (
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
        ) {
            String string1 = System.lineSeparator();

            String string2;
            while ((string2 = bufferedReader.readLine()) != null) {
                stringBuilder.append(string2);
                stringBuilder.append(string1);
            }
        } catch (Throwable throwable) {
            ExceptionLogger.logException(throwable);
            return null;
        }

        String string3 = ScriptParser.stripComments(stringBuilder.toString());
        return ScriptParser.parseTokens(string3);
    }

    private void updateScript(String string0, ArrayList<String> arrayList0, VehicleScript vehicleScript) {
        string0 = ZomboidFileSystem.instance.getString(string0);

        for (int int0 = arrayList0.size() - 1; int0 >= 0; int0--) {
            String string1 = ((String)arrayList0.get(int0)).trim();
            int int1 = string1.indexOf("{");
            int int2 = string1.lastIndexOf("}");
            String string2 = string1.substring(0, int1);
            if (string2.startsWith("module")) {
                string2 = string1.substring(0, int1).trim();
                String[] strings = string2.split("\\s+");
                String string3 = strings.length > 1 ? strings[1].trim() : "";
                if (string3.equals(vehicleScript.getModule().getName())) {
                    String string4 = string1.substring(int1 + 1, int2).trim();
                    ArrayList arrayList1 = ScriptParser.parseTokens(string4);

                    for (int int3 = arrayList1.size() - 1; int3 >= 0; int3--) {
                        String string5 = ((String)arrayList1.get(int3)).trim();
                        if (string5.startsWith("vehicle")) {
                            int1 = string5.indexOf("{");
                            string2 = string5.substring(0, int1).trim();
                            strings = string2.split("\\s+");
                            String string6 = strings.length > 1 ? strings[1].trim() : "";
                            if (string6.equals(vehicleScript.getName())) {
                                string5 = this.vehicleScriptToText(vehicleScript, string5).trim();
                                arrayList1.set(int3, string5);
                                String string7 = System.lineSeparator();
                                String string8 = String.join(string7 + "\t", arrayList1);
                                string8 = "module " + string3 + string7 + "{" + string7 + "\t" + string8 + string7 + "}" + string7;
                                arrayList0.set(int0, string8);
                                this.writeScript(string0, arrayList0);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private String vehicleScriptToText(VehicleScript vehicleScript, String string0) {
        float float0 = vehicleScript.getModelScale();
        ScriptParser.Block block0 = ScriptParser.parse(string0);
        block0 = block0.children.get(0);
        VehicleScript.Model model = vehicleScript.getModel();
        ScriptParser.Block block1 = block0.getBlock("model", null);
        if (model != null && block1 != null) {
            float float1 = vehicleScript.getModelScale();
            block1.setValue("scale", String.format(Locale.US, "%.4f", float1));
            Vector3f vector3f0 = vehicleScript.getModel().getOffset();
            block1.setValue("offset", String.format(Locale.US, "%.4f %.4f %.4f", vector3f0.x / float0, vector3f0.y / float0, vector3f0.z / float0));
        }

        ArrayList arrayList = new ArrayList();

        for (int int0 = 0; int0 < block0.children.size(); int0++) {
            ScriptParser.Block block2 = block0.children.get(int0);
            if ("physics".equals(block2.type)) {
                if (arrayList.size() == vehicleScript.getPhysicsShapeCount()) {
                    block0.elements.remove(block2);
                    block0.children.remove(int0);
                    int0--;
                } else {
                    arrayList.add(block2);
                }
            }
        }

        for (int int1 = 0; int1 < vehicleScript.getPhysicsShapeCount(); int1++) {
            VehicleScript.PhysicsShape physicsShape = vehicleScript.getPhysicsShape(int1);
            boolean boolean0 = int1 < arrayList.size();
            ScriptParser.Block block3 = boolean0 ? (ScriptParser.Block)arrayList.get(int1) : new ScriptParser.Block();
            block3.type = "physics";
            block3.id = physicsShape.getTypeString();
            if (boolean0) {
                block3.elements.clear();
                block3.children.clear();
                block3.values.clear();
            }

            block3.setValue(
                "offset",
                String.format(
                    Locale.US,
                    "%.4f %.4f %.4f",
                    physicsShape.getOffset().x() / float0,
                    physicsShape.getOffset().y() / float0,
                    physicsShape.getOffset().z() / float0
                )
            );
            if (physicsShape.type == 1) {
                block3.setValue(
                    "extents",
                    String.format(
                        Locale.US,
                        "%.4f %.4f %.4f",
                        physicsShape.getExtents().x() / float0,
                        physicsShape.getExtents().y() / float0,
                        physicsShape.getExtents().z() / float0
                    )
                );
                block3.setValue(
                    "rotate",
                    String.format(Locale.US, "%.4f %.4f %.4f", physicsShape.getRotate().x(), physicsShape.getRotate().y(), physicsShape.getRotate().z())
                );
            }

            if (physicsShape.type == 2) {
                block3.setValue("radius", String.format(Locale.US, "%.4f", physicsShape.getRadius() / float0));
            }

            if (!boolean0) {
                block0.elements.add(block3);
                block0.children.add(block3);
            }
        }

        for (int int2 = block0.children.size() - 1; int2 >= 0; int2--) {
            ScriptParser.Block block4 = block0.children.get(int2);
            if ("attachment".equals(block4.type)) {
                block0.elements.remove(block4);
                block0.children.remove(int2);
            }
        }

        for (int int3 = 0; int3 < vehicleScript.getAttachmentCount(); int3++) {
            ModelAttachment modelAttachment = vehicleScript.getAttachment(int3);
            ScriptParser.Block block5 = block0.getBlock("attachment", modelAttachment.getId());
            if (block5 == null) {
                block5 = new ScriptParser.Block();
                block5.type = "attachment";
                block5.id = modelAttachment.getId();
                block0.elements.add(block5);
                block0.children.add(block5);
            }

            block5.setValue(
                "offset",
                String.format(
                    Locale.US,
                    "%.4f %.4f %.4f",
                    modelAttachment.getOffset().x() / float0,
                    modelAttachment.getOffset().y() / float0,
                    modelAttachment.getOffset().z() / float0
                )
            );
            block5.setValue(
                "rotate",
                String.format(Locale.US, "%.4f %.4f %.4f", modelAttachment.getRotate().x(), modelAttachment.getRotate().y(), modelAttachment.getRotate().z())
            );
            if (modelAttachment.getBone() != null) {
                block5.setValue("bone", modelAttachment.getBone());
            }

            if (modelAttachment.getCanAttach() != null) {
                block5.setValue("canAttach", PZArrayUtil.arrayToString(modelAttachment.getCanAttach(), "", "", ","));
            }

            if (modelAttachment.getZOffset() != 0.0F) {
                block5.setValue("zoffset", String.format(Locale.US, "%.4f", modelAttachment.getZOffset()));
            }

            if (!modelAttachment.isUpdateConstraint()) {
                block5.setValue("updateconstraint", "false");
            }
        }

        Vector3f vector3f1 = vehicleScript.getExtents();
        block0.setValue("extents", String.format(Locale.US, "%.4f %.4f %.4f", vector3f1.x / float0, vector3f1.y / float0, vector3f1.z / float0));
        Vector3f vector3f2 = vehicleScript.getPhysicsChassisShape();
        block0.setValue("physicsChassisShape", String.format(Locale.US, "%.4f %.4f %.4f", vector3f2.x / float0, vector3f2.y / float0, vector3f2.z / float0));
        Vector3f vector3f3 = vehicleScript.getCenterOfMassOffset();
        block0.setValue("centerOfMassOffset", String.format(Locale.US, "%.4f %.4f %.4f", vector3f3.x / float0, vector3f3.y / float0, vector3f3.z / float0));
        Vector2f vector2f0 = vehicleScript.getShadowExtents();
        boolean boolean1 = block0.getValue("shadowExtents") != null;
        block0.setValue("shadowExtents", String.format(Locale.US, "%.4f %.4f", vector2f0.x / float0, vector2f0.y / float0));
        if (!boolean1) {
            block0.moveValueAfter("shadowExtents", "centerOfMassOffset");
        }

        Vector2f vector2f1 = vehicleScript.getShadowOffset();
        boolean1 = block0.getValue("shadowOffset") != null;
        block0.setValue("shadowOffset", String.format(Locale.US, "%.4f %.4f", vector2f1.x / float0, vector2f1.y / float0));
        if (!boolean1) {
            block0.moveValueAfter("shadowOffset", "shadowExtents");
        }

        for (int int4 = 0; int4 < vehicleScript.getAreaCount(); int4++) {
            VehicleScript.Area area = vehicleScript.getArea(int4);
            ScriptParser.Block block6 = block0.getBlock("area", area.getId());
            if (block6 != null) {
                block6.setValue(
                    "xywh",
                    String.format(Locale.US, "%.4f %.4f %.4f %.4f", area.getX() / float0, area.getY() / float0, area.getW() / float0, area.getH() / float0)
                );
            }
        }

        for (int int5 = 0; int5 < vehicleScript.getPassengerCount(); int5++) {
            VehicleScript.Passenger passenger = vehicleScript.getPassenger(int5);
            ScriptParser.Block block7 = block0.getBlock("passenger", passenger.getId());
            if (block7 != null) {
                for (VehicleScript.Position position : passenger.positions) {
                    ScriptParser.Block block8 = block7.getBlock("position", position.id);
                    if (block8 != null) {
                        block8.setValue(
                            "offset",
                            String.format(Locale.US, "%.4f %.4f %.4f", position.offset.x / float0, position.offset.y / float0, position.offset.z / float0)
                        );
                        block8.setValue(
                            "rotate",
                            String.format(Locale.US, "%.4f %.4f %.4f", position.rotate.x / float0, position.rotate.y / float0, position.rotate.z / float0)
                        );
                    }
                }
            }
        }

        for (int int6 = 0; int6 < vehicleScript.getWheelCount(); int6++) {
            VehicleScript.Wheel wheel = vehicleScript.getWheel(int6);
            ScriptParser.Block block9 = block0.getBlock("wheel", wheel.getId());
            if (block9 != null) {
                block9.setValue("offset", String.format(Locale.US, "%.4f %.4f %.4f", wheel.offset.x / float0, wheel.offset.y / float0, wheel.offset.z / float0));
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        String string1 = System.lineSeparator();
        block0.prettyPrint(1, stringBuilder, string1);
        return stringBuilder.toString();
    }

    private void writeScript(String string1, ArrayList<String> arrayList) {
        String string0 = ZomboidFileSystem.instance.getString(string1);
        File file = new File(string0);

        try (
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        ) {
            DebugLog.General.printf("writing %s\n", string1);

            for (String string2 : arrayList) {
                bufferedWriter.write(string2);
            }

            this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_table.rawget("wroteScript"), this.m_table, string0);
        } catch (Throwable throwable) {
            ExceptionLogger.logException(throwable);
        }
    }

    public static final class LuaEnvironment {
        public J2SEPlatform platform;
        public KahluaTable env;
        public KahluaThread thread;
        public LuaCaller caller;

        public LuaEnvironment(J2SEPlatform _platform, KahluaConverterManager converterManager, KahluaTable _env) {
            this.platform = _platform;
            this.env = _env;
            this.thread = LuaManager.thread;
            this.caller = LuaManager.caller;
        }
    }
}
