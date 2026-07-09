// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.gameStates;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Locale;
import se.krka.kahlua.vm.KahluaTable;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.skinnedmodel.ModelManager;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.input.GameKeyboard;
import zombie.scripting.ScriptManager;
import zombie.scripting.ScriptParser;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.ModelScript;
import zombie.ui.UIElement;
import zombie.ui.UIManager;
import zombie.vehicles.EditVehicleState;

public final class AttachmentEditorState extends GameState {
    public static AttachmentEditorState instance;
    private EditVehicleState.LuaEnvironment m_luaEnv;
    private boolean bExit = false;
    private final ArrayList<UIElement> m_gameUI = new ArrayList<>();
    private final ArrayList<UIElement> m_selfUI = new ArrayList<>();
    private boolean m_bSuspendUI;
    private KahluaTable m_table = null;

    @Override
    public void enter() {
        instance = this;
        if (this.m_luaEnv == null) {
            this.m_luaEnv = new EditVehicleState.LuaEnvironment(LuaManager.platform, LuaManager.converterManager, LuaManager.env);
        }

        this.saveGameUI();
        if (this.m_selfUI.size() == 0) {
            this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_luaEnv.env.rawget("AttachmentEditorState_InitUI"));
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

    public static AttachmentEditorState checkInstance() {
        if (instance != null) {
            if (instance.m_table != null && instance.m_table.getMetatable() != null) {
                if (instance.m_table.getMetatable().rawget("_LUA_RELOADED_CHECK") == null) {
                    instance = null;
                }
            } else {
                instance = null;
            }
        }

        return instance == null ? new AttachmentEditorState() : instance;
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

    public Object fromLua0(String func) {
        byte byte0 = -1;
        switch (func.hashCode()) {
            case 3127582:
                if (func.equals("exit")) {
                    byte0 = 0;
                }
            default:
                switch (byte0) {
                    case 0:
                        this.bExit = true;
                        return null;
                    default:
                        throw new IllegalArgumentException("unhandled \"" + func + "\"");
                }
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
                        ModelScript modelScript = ScriptManager.instance.getModelScript((String)arg0);
                        if (modelScript == null) {
                            throw new NullPointerException("model script \"" + arg0 + "\" not found");
                        }

                        ArrayList arrayList = this.readScript(modelScript.getFileName());
                        if (arrayList != null) {
                            this.updateScript(modelScript.getFileName(), arrayList, modelScript);
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

    private void updateScript(String string0, ArrayList<String> arrayList0, ModelScript modelScript) {
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
                if (string3.equals(modelScript.getModule().getName())) {
                    String string4 = string1.substring(int1 + 1, int2).trim();
                    ArrayList arrayList1 = ScriptParser.parseTokens(string4);

                    for (int int3 = arrayList1.size() - 1; int3 >= 0; int3--) {
                        String string5 = ((String)arrayList1.get(int3)).trim();
                        if (string5.startsWith("model")) {
                            int1 = string5.indexOf("{");
                            string2 = string5.substring(0, int1).trim();
                            strings = string2.split("\\s+");
                            String string6 = strings.length > 1 ? strings[1].trim() : "";
                            if (string6.equals(modelScript.getName())) {
                                string5 = this.modelScriptToText(modelScript, string5).trim();
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

    private String modelScriptToText(ModelScript modelScript, String string0) {
        ScriptParser.Block block0 = ScriptParser.parse(string0);
        block0 = block0.children.get(0);

        for (int int0 = block0.children.size() - 1; int0 >= 0; int0--) {
            ScriptParser.Block block1 = block0.children.get(int0);
            if ("attachment".equals(block1.type)) {
                block0.elements.remove(block1);
                block0.children.remove(int0);
            }
        }

        for (int int1 = 0; int1 < modelScript.getAttachmentCount(); int1++) {
            ModelAttachment modelAttachment = modelScript.getAttachment(int1);
            ScriptParser.Block block2 = block0.getBlock("attachment", modelAttachment.getId());
            if (block2 == null) {
                block2 = new ScriptParser.Block();
                block2.type = "attachment";
                block2.id = modelAttachment.getId();
                block2.setValue(
                    "offset",
                    String.format(
                        Locale.US, "%.4f %.4f %.4f", modelAttachment.getOffset().x(), modelAttachment.getOffset().y(), modelAttachment.getOffset().z()
                    )
                );
                block2.setValue(
                    "rotate",
                    String.format(
                        Locale.US, "%.4f %.4f %.4f", modelAttachment.getRotate().x(), modelAttachment.getRotate().y(), modelAttachment.getRotate().z()
                    )
                );
                if (modelAttachment.getBone() != null) {
                    block2.setValue("bone", modelAttachment.getBone());
                }

                block0.elements.add(block2);
                block0.children.add(block2);
            } else {
                block2.setValue(
                    "offset",
                    String.format(
                        Locale.US, "%.4f %.4f %.4f", modelAttachment.getOffset().x(), modelAttachment.getOffset().y(), modelAttachment.getOffset().z()
                    )
                );
                block2.setValue(
                    "rotate",
                    String.format(
                        Locale.US, "%.4f %.4f %.4f", modelAttachment.getRotate().x(), modelAttachment.getRotate().y(), modelAttachment.getRotate().z()
                    )
                );
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
}
