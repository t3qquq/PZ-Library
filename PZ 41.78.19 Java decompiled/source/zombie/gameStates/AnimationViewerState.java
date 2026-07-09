// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.gameStates;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import se.krka.kahlua.vm.KahluaTable;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.core.Core;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.debug.DebugOptions;
import zombie.input.GameKeyboard;
import zombie.ui.UIElement;
import zombie.ui.UIManager;
import zombie.vehicles.EditVehicleState;

public final class AnimationViewerState extends GameState {
    public static AnimationViewerState instance;
    private EditVehicleState.LuaEnvironment m_luaEnv;
    private boolean bExit = false;
    private final ArrayList<UIElement> m_gameUI = new ArrayList<>();
    private final ArrayList<UIElement> m_selfUI = new ArrayList<>();
    private boolean m_bSuspendUI;
    private KahluaTable m_table = null;
    private final ArrayList<String> m_clipNames = new ArrayList<>();
    private static final int VERSION = 1;
    private final ArrayList<ConfigOption> options = new ArrayList<>();
    private AnimationViewerState.BooleanDebugOption DrawGrid = new AnimationViewerState.BooleanDebugOption("DrawGrid", false);
    private AnimationViewerState.BooleanDebugOption Isometric = new AnimationViewerState.BooleanDebugOption("Isometric", false);
    private AnimationViewerState.BooleanDebugOption UseDeferredMovement = new AnimationViewerState.BooleanDebugOption("UseDeferredMovement", false);

    @Override
    public void enter() {
        instance = this;
        this.load();
        if (this.m_luaEnv == null) {
            this.m_luaEnv = new EditVehicleState.LuaEnvironment(LuaManager.platform, LuaManager.converterManager, LuaManager.env);
        }

        this.saveGameUI();
        if (this.m_selfUI.size() == 0) {
            this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_luaEnv.env.rawget("AnimationViewerState_InitUI"));
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
        this.save();
        this.restoreGameUI();
    }

    @Override
    public void render() {
        byte byte0 = 0;
        Core.getInstance().StartFrame(byte0, true);
        this.renderScene();
        Core.getInstance().EndFrame(byte0);
        Core.getInstance().RenderOffScreenBuffer();
        UIManager.useUIFBO = Core.getInstance().supportsFBO() && Core.OptionUIFBO;
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

    public static AnimationViewerState checkInstance() {
        if (instance != null) {
            if (instance.m_table != null && instance.m_table.getMetatable() != null) {
                if (instance.m_table.getMetatable().rawget("_LUA_RELOADED_CHECK") == null) {
                    instance = null;
                }
            } else {
                instance = null;
            }
        }

        return instance == null ? new AnimationViewerState() : instance;
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
        switch (func) {
            case "exit":
                this.bExit = true;
                return null;
            case "getClipNames":
                if (this.m_clipNames.isEmpty()) {
                    for (AnimationClip animationClip : ModelManager.instance.getAllAnimationClips()) {
                        this.m_clipNames.add(animationClip.Name);
                    }

                    this.m_clipNames.sort(Comparator.naturalOrder());
                }

                return this.m_clipNames;
            default:
                throw new IllegalArgumentException("unhandled \"" + func + "\"");
        }
    }

    public Object fromLua1(String func, Object arg0) {
        byte byte0 = -1;
        func.hashCode();
        switch (byte0) {
            default:
                throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\"", func, arg0));
        }
    }

    public ConfigOption getOptionByName(String name) {
        for (int int0 = 0; int0 < this.options.size(); int0++) {
            ConfigOption configOption = this.options.get(int0);
            if (configOption.getName().equals(name)) {
                return configOption;
            }
        }

        return null;
    }

    public int getOptionCount() {
        return this.options.size();
    }

    public ConfigOption getOptionByIndex(int index) {
        return this.options.get(index);
    }

    public void setBoolean(String name, boolean value) {
        ConfigOption configOption = this.getOptionByName(name);
        if (configOption instanceof BooleanConfigOption) {
            ((BooleanConfigOption)configOption).setValue(value);
        }
    }

    public boolean getBoolean(String name) {
        ConfigOption configOption = this.getOptionByName(name);
        return configOption instanceof BooleanConfigOption ? ((BooleanConfigOption)configOption).getValue() : false;
    }

    public void save() {
        String string = ZomboidFileSystem.instance.getCacheDir() + File.separator + "animationViewerState-options.ini";
        ConfigFile configFile = new ConfigFile();
        configFile.write(string, 1, this.options);
    }

    public void load() {
        String string = ZomboidFileSystem.instance.getCacheDir() + File.separator + "animationViewerState-options.ini";
        ConfigFile configFile = new ConfigFile();
        if (configFile.read(string)) {
            for (int int0 = 0; int0 < configFile.getOptions().size(); int0++) {
                ConfigOption configOption0 = configFile.getOptions().get(int0);
                ConfigOption configOption1 = this.getOptionByName(configOption0.getName());
                if (configOption1 != null) {
                    configOption1.parse(configOption0.getValueAsString());
                }
            }
        }
    }

    public class BooleanDebugOption extends BooleanConfigOption {
        public BooleanDebugOption(String string, boolean boolean0) {
            super(string, boolean0);
            AnimationViewerState.this.options.add(this);
        }
    }
}
