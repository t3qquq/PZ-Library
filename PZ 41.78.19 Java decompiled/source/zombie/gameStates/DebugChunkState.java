// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.gameStates;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import org.joml.Vector2f;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.AmbientStreamManager;
import zombie.FliesSound;
import zombie.VirtualZombieManager;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.ai.astar.Mover;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatElement;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.properties.PropertyContainer;
import zombie.core.utils.BooleanGrid;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.erosion.ErosionData;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoRoomLight;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.NearestWalls;
import zombie.iso.ParticlesFire;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.randomizedWorld.randomizedVehicleStory.RandomizedVehicleStoryBase;
import zombie.randomizedWorld.randomizedVehicleStory.VehicleStorySpawner;
import zombie.ui.TextDrawObject;
import zombie.ui.TextManager;
import zombie.ui.UIElement;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.util.Type;
import zombie.vehicles.ClipperOffset;
import zombie.vehicles.EditVehicleState;
import zombie.vehicles.PolygonalMap2;

public final class DebugChunkState extends GameState {
    public static DebugChunkState instance;
    private EditVehicleState.LuaEnvironment m_luaEnv;
    private boolean bExit = false;
    private final ArrayList<UIElement> m_gameUI = new ArrayList<>();
    private final ArrayList<UIElement> m_selfUI = new ArrayList<>();
    private boolean m_bSuspendUI;
    private KahluaTable m_table = null;
    private int m_playerIndex = 0;
    private int m_z = 0;
    private int gridX = -1;
    private int gridY = -1;
    private UIFont FONT = UIFont.DebugConsole;
    private String m_vehicleStory = "Basic Car Crash";
    static boolean keyQpressed = false;
    private static ClipperOffset m_clipperOffset = null;
    private static ByteBuffer m_clipperBuffer;
    private static final int VERSION = 1;
    private final ArrayList<ConfigOption> options = new ArrayList<>();
    private DebugChunkState.BooleanDebugOption BuildingRect = new DebugChunkState.BooleanDebugOption("BuildingRect", true);
    private DebugChunkState.BooleanDebugOption ChunkGrid = new DebugChunkState.BooleanDebugOption("ChunkGrid", true);
    private DebugChunkState.BooleanDebugOption ClosestRoomSquare = new DebugChunkState.BooleanDebugOption("ClosestRoomSquare", true);
    private DebugChunkState.BooleanDebugOption EmptySquares = new DebugChunkState.BooleanDebugOption("EmptySquares", true);
    private DebugChunkState.BooleanDebugOption FlyBuzzEmitters = new DebugChunkState.BooleanDebugOption("FlyBuzzEmitters", true);
    private DebugChunkState.BooleanDebugOption LightSquares = new DebugChunkState.BooleanDebugOption("LightSquares", true);
    private DebugChunkState.BooleanDebugOption LineClearCollide = new DebugChunkState.BooleanDebugOption("LineClearCollide", true);
    private DebugChunkState.BooleanDebugOption NearestWallsOpt = new DebugChunkState.BooleanDebugOption("NearestWalls", true);
    private DebugChunkState.BooleanDebugOption ObjectPicker = new DebugChunkState.BooleanDebugOption("ObjectPicker", true);
    private DebugChunkState.BooleanDebugOption RoomLightRects = new DebugChunkState.BooleanDebugOption("RoomLightRects", true);
    private DebugChunkState.BooleanDebugOption VehicleStory = new DebugChunkState.BooleanDebugOption("VehicleStory", true);
    private DebugChunkState.BooleanDebugOption RandomSquareInZone = new DebugChunkState.BooleanDebugOption("RandomSquareInZone", true);
    private DebugChunkState.BooleanDebugOption ZoneRect = new DebugChunkState.BooleanDebugOption("ZoneRect", true);

    public DebugChunkState() {
        instance = this;
    }

    @Override
    public void enter() {
        instance = this;
        this.load();
        if (this.m_luaEnv == null) {
            this.m_luaEnv = new EditVehicleState.LuaEnvironment(LuaManager.platform, LuaManager.converterManager, LuaManager.env);
        }

        this.saveGameUI();
        if (this.m_selfUI.size() == 0) {
            IsoPlayer player = IsoPlayer.players[this.m_playerIndex];
            this.m_z = player == null ? 0 : (int)player.z;
            this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_luaEnv.env.rawget("DebugChunkState_InitUI"), this);
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

        for (int int0 = 0; int0 < IsoCamera.cameras.length; int0++) {
            IsoCamera.cameras[int0].DeferedX = IsoCamera.cameras[int0].DeferedY = 0.0F;
        }
    }

    @Override
    public void render() {
        IsoPlayer.setInstance(IsoPlayer.players[this.m_playerIndex]);
        IsoCamera.CamCharacter = IsoPlayer.players[this.m_playerIndex];
        boolean boolean0 = true;

        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            if (int0 != this.m_playerIndex && IsoPlayer.players[int0] != null) {
                Core.getInstance().StartFrame(int0, boolean0);
                Core.getInstance().EndFrame(int0);
                boolean0 = false;
            }
        }

        Core.getInstance().StartFrame(this.m_playerIndex, boolean0);
        this.renderScene();
        Core.getInstance().EndFrame(this.m_playerIndex);
        Core.getInstance().RenderOffScreenBuffer();

        for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
            TextDrawObject.NoRender(int1);
            ChatElement.NoRender(int1);
        }

        if (Core.getInstance().StartFrameUI()) {
            this.renderUI();
        }

        Core.getInstance().EndFrameUI();
    }

    @Override
    public GameStateMachine.StateAction update() {
        return !this.bExit && !GameKeyboard.isKeyPressed(60) ? this.updateScene() : GameStateMachine.StateAction.Continue;
    }

    public static DebugChunkState checkInstance() {
        instance = null;
        if (instance != null) {
            if (instance.m_table != null && instance.m_table.getMetatable() != null) {
                if (instance.m_table.getMetatable().rawget("_LUA_RELOADED_CHECK") == null) {
                    instance = null;
                }
            } else {
                instance = null;
            }
        }

        return instance == null ? new DebugChunkState() : instance;
    }

    public void renderScene() {
        IsoCamera.frameState.set(this.m_playerIndex);
        SpriteRenderer.instance.doCoreIntParam(0, IsoCamera.CamCharacter.x);
        SpriteRenderer.instance.doCoreIntParam(1, IsoCamera.CamCharacter.y);
        SpriteRenderer.instance.doCoreIntParam(2, IsoCamera.CamCharacter.z);
        IsoSprite.globalOffsetX = -1.0F;
        IsoWorld.instance.CurrentCell.render();
        if (this.ChunkGrid.getValue()) {
            this.drawGrid();
        }

        this.drawCursor();
        if (this.LightSquares.getValue()) {
            Stack stack = IsoWorld.instance.getCell().getLamppostPositions();

            for (int int0 = 0; int0 < stack.size(); int0++) {
                IsoLightSource lightSource = (IsoLightSource)stack.get(int0);
                if (lightSource.z == this.m_z) {
                    this.paintSquare(lightSource.x, lightSource.y, lightSource.z, 1.0F, 1.0F, 0.0F, 0.5F);
                }
            }
        }

        if (this.ZoneRect.getValue()) {
            this.drawZones();
        }

        if (this.BuildingRect.getValue()) {
            IsoGridSquare square0 = IsoWorld.instance.getCell().getGridSquare(this.gridX, this.gridY, this.m_z);
            if (square0 != null && square0.getBuilding() != null) {
                BuildingDef buildingDef0 = square0.getBuilding().getDef();
                this.DrawIsoLine(buildingDef0.getX(), buildingDef0.getY(), buildingDef0.getX2(), buildingDef0.getY(), 1.0F, 1.0F, 1.0F, 1.0F, 2);
                this.DrawIsoLine(buildingDef0.getX2(), buildingDef0.getY(), buildingDef0.getX2(), buildingDef0.getY2(), 1.0F, 1.0F, 1.0F, 1.0F, 2);
                this.DrawIsoLine(buildingDef0.getX2(), buildingDef0.getY2(), buildingDef0.getX(), buildingDef0.getY2(), 1.0F, 1.0F, 1.0F, 1.0F, 2);
                this.DrawIsoLine(buildingDef0.getX(), buildingDef0.getY2(), buildingDef0.getX(), buildingDef0.getY(), 1.0F, 1.0F, 1.0F, 1.0F, 2);
            }
        }

        if (this.RoomLightRects.getValue()) {
            ArrayList arrayList = IsoWorld.instance.CurrentCell.roomLights;

            for (int int1 = 0; int1 < arrayList.size(); int1++) {
                IsoRoomLight roomLight = (IsoRoomLight)arrayList.get(int1);
                if (roomLight.z == this.m_z) {
                    this.DrawIsoRect(roomLight.x, roomLight.y, roomLight.width, roomLight.height, 0.0F, 1.0F, 1.0F, 1.0F, 1);
                }
            }
        }

        if (this.FlyBuzzEmitters.getValue()) {
            FliesSound.instance.render();
        }

        if (this.ClosestRoomSquare.getValue()) {
            float float0 = IsoPlayer.players[this.m_playerIndex].getX();
            float float1 = IsoPlayer.players[this.m_playerIndex].getY();
            Vector2f vector2f = new Vector2f();
            BuildingDef buildingDef1 = ((AmbientStreamManager)AmbientStreamManager.getInstance()).getNearestBuilding(float0, float1, vector2f);
            if (buildingDef1 != null) {
                this.DrawIsoLine(float0, float1, vector2f.x, vector2f.y, 1.0F, 1.0F, 1.0F, 1.0F, 1);
            }
        }

        if (this.m_table != null && this.m_table.rawget("selectedSquare") != null) {
            IsoGridSquare square1 = Type.tryCastTo(this.m_table.rawget("selectedSquare"), IsoGridSquare.class);
            if (square1 != null) {
                this.DrawIsoRect(square1.x, square1.y, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 2);
            }
        }

        LineDrawer.render();
        LineDrawer.clear();
    }

    private void renderUI() {
        int int0 = this.m_playerIndex;
        Stack stack = IsoWorld.instance.getCell().getLamppostPositions();
        int int1 = 0;

        for (int int2 = 0; int2 < stack.size(); int2++) {
            IsoLightSource lightSource = (IsoLightSource)stack.get(int2);
            if (lightSource.bActive) {
                int1++;
            }
        }

        UIManager.render();
    }

    public void setTable(KahluaTable table) {
        this.m_table = table;
    }

    public GameStateMachine.StateAction updateScene() {
        IsoPlayer.setInstance(IsoPlayer.players[this.m_playerIndex]);
        IsoCamera.CamCharacter = IsoPlayer.players[this.m_playerIndex];
        UIManager.setPicked(IsoObjectPicker.Instance.ContextPick(Mouse.getXA(), Mouse.getYA()));
        IsoObject object = UIManager.getPicked() == null ? null : UIManager.getPicked().tile;
        UIManager.setLastPicked(object);
        if (GameKeyboard.isKeyDown(16)) {
            if (!keyQpressed) {
                IsoGridSquare square = IsoWorld.instance.getCell().getGridSquare(this.gridX, this.gridY, 0);
                if (square != null) {
                    GameClient.instance.worldObjectsSyncReq.putRequestSyncIsoChunk(square.chunk);
                    DebugLog.General.debugln("Requesting sync IsoChunk %s", square.chunk);
                }

                keyQpressed = true;
            }
        } else {
            keyQpressed = false;
        }

        if (GameKeyboard.isKeyDown(19)) {
            if (!keyQpressed) {
                DebugOptions.instance.Terrain.RenderTiles.NewRender.setValue(true);
                keyQpressed = true;
                DebugLog.General.debugln("IsoCell.newRender = %s", DebugOptions.instance.Terrain.RenderTiles.NewRender.getValue());
            }
        } else {
            keyQpressed = false;
        }

        if (GameKeyboard.isKeyDown(20)) {
            if (!keyQpressed) {
                DebugOptions.instance.Terrain.RenderTiles.NewRender.setValue(false);
                keyQpressed = true;
                DebugLog.General.debugln("IsoCell.newRender = %s", DebugOptions.instance.Terrain.RenderTiles.NewRender.getValue());
            }
        } else {
            keyQpressed = false;
        }

        if (GameKeyboard.isKeyDown(31)) {
            if (!keyQpressed) {
                ParticlesFire.getInstance().reloadShader();
                keyQpressed = true;
                DebugLog.General.debugln("ParticlesFire.reloadShader");
            }
        } else {
            keyQpressed = false;
        }

        IsoCamera.update();
        this.updateCursor();
        return GameStateMachine.StateAction.Remain;
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

    public Object fromLua0(String func) {
        switch (func) {
            case "exit":
                this.bExit = true;
                return null;
            case "getCameraDragX":
                return BoxedStaticValues.toDouble(-IsoCamera.cameras[this.m_playerIndex].DeferedX);
            case "getCameraDragY":
                return BoxedStaticValues.toDouble(-IsoCamera.cameras[this.m_playerIndex].DeferedY);
            case "getPlayerIndex":
                return BoxedStaticValues.toDouble(this.m_playerIndex);
            case "getVehicleStory":
                return this.m_vehicleStory;
            case "getZ":
                return BoxedStaticValues.toDouble(this.m_z);
            default:
                throw new IllegalArgumentException(String.format("unhandled \"%s\"", func));
        }
    }

    public Object fromLua1(String func, Object arg0) {
        switch (func) {
            case "getCameraDragX":
                return BoxedStaticValues.toDouble(-IsoCamera.cameras[this.m_playerIndex].DeferedX);
            case "getCameraDragY":
                return BoxedStaticValues.toDouble(-IsoCamera.cameras[this.m_playerIndex].DeferedY);
            case "setPlayerIndex":
                this.m_playerIndex = PZMath.clamp(((Double)arg0).intValue(), 0, 3);
                return null;
            case "setVehicleStory":
                this.m_vehicleStory = (String)arg0;
                return null;
            case "setZ":
                this.m_z = PZMath.clamp(((Double)arg0).intValue(), 0, 7);
                return null;
            default:
                throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\"", func, arg0));
        }
    }

    public Object fromLua2(String func, Object arg0, Object arg1) {
        byte byte0 = -1;
        switch (func.hashCode()) {
            case -1879300743:
                if (func.equals("dragCamera")) {
                    byte0 = 0;
                }
            default:
                switch (byte0) {
                    case 0:
                        float float0 = ((Double)arg0).floatValue();
                        float float1 = ((Double)arg1).floatValue();
                        IsoCamera.cameras[this.m_playerIndex].DeferedX = -float0;
                        IsoCamera.cameras[this.m_playerIndex].DeferedY = -float1;
                        return null;
                    default:
                        throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\" \\\"%s\\\"", func, arg0, arg1));
                }
        }
    }

    private void updateCursor() {
        int int0 = this.m_playerIndex;
        int int1 = Core.TileScale;
        float float0 = Mouse.getXA();
        float float1 = Mouse.getYA();
        float0 -= IsoCamera.getScreenLeft(int0);
        float1 -= IsoCamera.getScreenTop(int0);
        float0 *= Core.getInstance().getZoom(int0);
        float1 *= Core.getInstance().getZoom(int0);
        int int2 = this.m_z;
        this.gridX = (int)IsoUtils.XToIso(float0, float1, int2);
        this.gridY = (int)IsoUtils.YToIso(float0, float1, int2);
    }

    private void DrawIsoLine(float float2, float float3, float float6, float float7, float float9, float float10, float float11, float float12, int int0) {
        float float0 = this.m_z;
        float float1 = IsoUtils.XToScreenExact(float2, float3, float0, 0);
        float float4 = IsoUtils.YToScreenExact(float2, float3, float0, 0);
        float float5 = IsoUtils.XToScreenExact(float6, float7, float0, 0);
        float float8 = IsoUtils.YToScreenExact(float6, float7, float0, 0);
        LineDrawer.drawLine(float1, float4, float5, float8, float9, float10, float11, float12, int0);
    }

    private void DrawIsoRect(float float0, float float1, float float6, float float7, float float2, float float3, float float4, float float5, int int0) {
        this.DrawIsoLine(float0, float1, float0 + float6, float1, float2, float3, float4, float5, int0);
        this.DrawIsoLine(float0 + float6, float1, float0 + float6, float1 + float7, float2, float3, float4, float5, int0);
        this.DrawIsoLine(float0 + float6, float1 + float7, float0, float1 + float7, float2, float3, float4, float5, int0);
        this.DrawIsoLine(float0, float1 + float7, float0, float1, float2, float3, float4, float5, int0);
    }

    private void drawGrid() {
        int int0 = this.m_playerIndex;
        float float0 = IsoUtils.XToIso(-128.0F, -256.0F, 0.0F);
        float float1 = IsoUtils.YToIso(Core.getInstance().getOffscreenWidth(int0) + 128, -256.0F, 0.0F);
        float float2 = IsoUtils.XToIso(Core.getInstance().getOffscreenWidth(int0) + 128, Core.getInstance().getOffscreenHeight(int0) + 256, 6.0F);
        float float3 = IsoUtils.YToIso(-128.0F, Core.getInstance().getOffscreenHeight(int0) + 256, 6.0F);
        int int1 = (int)float1;
        int int2 = (int)float3;
        int int3 = (int)float0;
        int int4 = (int)float2;
        int3 -= 2;
        int1 -= 2;

        for (int int5 = int1; int5 <= int2; int5++) {
            if (int5 % 10 == 0) {
                this.DrawIsoLine(int3, int5, int4, int5, 1.0F, 1.0F, 1.0F, 0.5F, 1);
            }
        }

        for (int int6 = int3; int6 <= int4; int6++) {
            if (int6 % 10 == 0) {
                this.DrawIsoLine(int6, int1, int6, int2, 1.0F, 1.0F, 1.0F, 0.5F, 1);
            }
        }

        for (int int7 = int1; int7 <= int2; int7++) {
            if (int7 % 300 == 0) {
                this.DrawIsoLine(int3, int7, int4, int7, 0.0F, 1.0F, 0.0F, 0.5F, 1);
            }
        }

        for (int int8 = int3; int8 <= int4; int8++) {
            if (int8 % 300 == 0) {
                this.DrawIsoLine(int8, int1, int8, int2, 0.0F, 1.0F, 0.0F, 0.5F, 1);
            }
        }

        if (GameClient.bClient) {
            for (int int9 = int1; int9 <= int2; int9++) {
                if (int9 % 50 == 0) {
                    this.DrawIsoLine(int3, int9, int4, int9, 1.0F, 0.0F, 0.0F, 0.5F, 1);
                }
            }

            for (int int10 = int3; int10 <= int4; int10++) {
                if (int10 % 50 == 0) {
                    this.DrawIsoLine(int10, int1, int10, int2, 1.0F, 0.0F, 0.0F, 0.5F, 1);
                }
            }
        }
    }

    private void drawCursor() {
        int int0 = this.m_playerIndex;
        int int1 = Core.TileScale;
        float float0 = this.m_z;
        int int2 = (int)IsoUtils.XToScreenExact(this.gridX, this.gridY + 1, float0, 0);
        int int3 = (int)IsoUtils.YToScreenExact(this.gridX, this.gridY + 1, float0, 0);
        SpriteRenderer.instance
            .renderPoly(int2, int3, int2 + 32 * int1, int3 - 16 * int1, int2 + 64 * int1, int3, int2 + 32 * int1, int3 + 16 * int1, 0.0F, 0.0F, 1.0F, 0.5F);
        IsoChunkMap chunkMap = IsoWorld.instance.getCell().ChunkMap[int0];

        for (int int4 = chunkMap.getWorldYMinTiles(); int4 < chunkMap.getWorldYMaxTiles(); int4++) {
            for (int int5 = chunkMap.getWorldXMinTiles(); int5 < chunkMap.getWorldXMaxTiles(); int5++) {
                IsoGridSquare square0 = IsoWorld.instance.getCell().getGridSquare((double)int5, (double)int4, (double)float0);
                if (square0 != null) {
                    if (square0 != chunkMap.getGridSquare(int5, int4, (int)float0)) {
                        int2 = (int)IsoUtils.XToScreenExact(int5, int4 + 1, float0, 0);
                        int3 = (int)IsoUtils.YToScreenExact(int5, int4 + 1, float0, 0);
                        SpriteRenderer.instance.renderPoly(int2, int3, int2 + 32, int3 - 16, int2 + 64, int3, int2 + 32, int3 + 16, 1.0F, 0.0F, 0.0F, 0.8F);
                    }

                    if (square0 == null
                        || square0.getX() != int5
                        || square0.getY() != int4
                        || square0.getZ() != float0
                        || square0.e != null && square0.e.w != null && square0.e.w != square0
                        || square0.w != null && square0.w.e != null && square0.w.e != square0
                        || square0.n != null && square0.n.s != null && square0.n.s != square0
                        || square0.s != null && square0.s.n != null && square0.s.n != square0
                        || square0.nw != null && square0.nw.se != null && square0.nw.se != square0
                        || square0.se != null && square0.se.nw != null && square0.se.nw != square0) {
                        int2 = (int)IsoUtils.XToScreenExact(int5, int4 + 1, float0, 0);
                        int3 = (int)IsoUtils.YToScreenExact(int5, int4 + 1, float0, 0);
                        SpriteRenderer.instance.renderPoly(int2, int3, int2 + 32, int3 - 16, int2 + 64, int3, int2 + 32, int3 + 16, 1.0F, 0.0F, 0.0F, 0.5F);
                    }

                    if (square0 != null) {
                        IsoGridSquare square1 = square0.testPathFindAdjacent(null, -1, 0, 0) ? null : square0.nav[IsoDirections.W.index()];
                        IsoGridSquare square2 = square0.testPathFindAdjacent(null, 0, -1, 0) ? null : square0.nav[IsoDirections.N.index()];
                        IsoGridSquare square3 = square0.testPathFindAdjacent(null, 1, 0, 0) ? null : square0.nav[IsoDirections.E.index()];
                        IsoGridSquare square4 = square0.testPathFindAdjacent(null, 0, 1, 0) ? null : square0.nav[IsoDirections.S.index()];
                        IsoGridSquare square5 = square0.testPathFindAdjacent(null, -1, -1, 0) ? null : square0.nav[IsoDirections.NW.index()];
                        IsoGridSquare square6 = square0.testPathFindAdjacent(null, 1, -1, 0) ? null : square0.nav[IsoDirections.NE.index()];
                        IsoGridSquare square7 = square0.testPathFindAdjacent(null, -1, 1, 0) ? null : square0.nav[IsoDirections.SW.index()];
                        IsoGridSquare square8 = square0.testPathFindAdjacent(null, 1, 1, 0) ? null : square0.nav[IsoDirections.SE.index()];
                        if (square1 != square0.w
                            || square2 != square0.n
                            || square3 != square0.e
                            || square4 != square0.s
                            || square5 != square0.nw
                            || square6 != square0.ne
                            || square7 != square0.sw
                            || square8 != square0.se) {
                            this.paintSquare(int5, int4, (int)float0, 1.0F, 0.0F, 0.0F, 0.5F);
                        }
                    }

                    if (square0 != null
                        && (
                            square0.nav[IsoDirections.NW.index()] != null && square0.nav[IsoDirections.NW.index()].nav[IsoDirections.SE.index()] != square0
                                || square0.nav[IsoDirections.NE.index()] != null
                                    && square0.nav[IsoDirections.NE.index()].nav[IsoDirections.SW.index()] != square0
                                || square0.nav[IsoDirections.SW.index()] != null
                                    && square0.nav[IsoDirections.SW.index()].nav[IsoDirections.NE.index()] != square0
                                || square0.nav[IsoDirections.SE.index()] != null
                                    && square0.nav[IsoDirections.SE.index()].nav[IsoDirections.NW.index()] != square0
                                || square0.nav[IsoDirections.N.index()] != null && square0.nav[IsoDirections.N.index()].nav[IsoDirections.S.index()] != square0
                                || square0.nav[IsoDirections.S.index()] != null && square0.nav[IsoDirections.S.index()].nav[IsoDirections.N.index()] != square0
                                || square0.nav[IsoDirections.W.index()] != null && square0.nav[IsoDirections.W.index()].nav[IsoDirections.E.index()] != square0
                                || square0.nav[IsoDirections.E.index()] != null && square0.nav[IsoDirections.E.index()].nav[IsoDirections.W.index()] != square0
                        )) {
                        int2 = (int)IsoUtils.XToScreenExact(int5, int4 + 1, float0, 0);
                        int3 = (int)IsoUtils.YToScreenExact(int5, int4 + 1, float0, 0);
                        SpriteRenderer.instance.renderPoly(int2, int3, int2 + 32, int3 - 16, int2 + 64, int3, int2 + 32, int3 + 16, 1.0F, 0.0F, 0.0F, 0.5F);
                    }

                    if (this.EmptySquares.getValue() && square0.getObjects().isEmpty()) {
                        this.paintSquare(int5, int4, (int)float0, 1.0F, 1.0F, 0.0F, 0.5F);
                    }

                    if (square0.getRoom() != null && square0.isFree(false) && !VirtualZombieManager.instance.canSpawnAt(int5, int4, (int)float0)) {
                        this.paintSquare(int5, int4, (int)float0, 1.0F, 1.0F, 1.0F, 1.0F);
                    }

                    if (square0.roofHideBuilding != null) {
                        this.paintSquare(int5, int4, (int)float0, 0.0F, 0.0F, 1.0F, 0.25F);
                    }
                }
            }
        }

        if (IsoCamera.CamCharacter.getCurrentSquare() != null
            && Math.abs(this.gridX - (int)IsoCamera.CamCharacter.x) <= 1
            && Math.abs(this.gridY - (int)IsoCamera.CamCharacter.y) <= 1) {
            IsoGridSquare square9 = IsoWorld.instance.CurrentCell.getGridSquare(this.gridX, this.gridY, this.m_z);
            IsoObject object = IsoCamera.CamCharacter.getCurrentSquare().testCollideSpecialObjects(square9);
            if (object != null) {
                object.getSprite().RenderGhostTileRed((int)object.getX(), (int)object.getY(), (int)object.getZ());
            }
        }

        if (this.LineClearCollide.getValue()) {
            this.lineClearCached(
                IsoWorld.instance.CurrentCell,
                this.gridX,
                this.gridY,
                (int)float0,
                (int)IsoCamera.CamCharacter.getX(),
                (int)IsoCamera.CamCharacter.getY(),
                this.m_z,
                false
            );
        }

        if (this.NearestWallsOpt.getValue()) {
            NearestWalls.render(this.gridX, this.gridY, this.m_z);
        }

        if (this.VehicleStory.getValue()) {
            this.drawVehicleStory();
        }
    }

    private void drawZones() {
        ArrayList arrayList = IsoWorld.instance.MetaGrid.getZonesAt(this.gridX, this.gridY, this.m_z, new ArrayList<>());
        IsoMetaGrid.Zone zone0 = null;

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            IsoMetaGrid.Zone zone1 = (IsoMetaGrid.Zone)arrayList.get(int0);
            if (zone1.isPreferredZoneForSquare) {
                zone0 = zone1;
            }

            if (!zone1.isPolyline()) {
                if (!zone1.points.isEmpty()) {
                    for (byte byte0 = 0; byte0 < zone1.points.size(); byte0 += 2) {
                        int int1 = zone1.points.get(byte0);
                        int int2 = zone1.points.get(byte0 + 1);
                        int int3 = zone1.points.get((byte0 + 2) % zone1.points.size());
                        int int4 = zone1.points.get((byte0 + 3) % zone1.points.size());
                        this.DrawIsoLine(int1, int2, int3, int4, 1.0F, 1.0F, 0.0F, 1.0F, 1);
                    }
                } else {
                    this.DrawIsoLine(zone1.x, zone1.y, zone1.x + zone1.w, zone1.y, 1.0F, 1.0F, 0.0F, 1.0F, 1);
                    this.DrawIsoLine(zone1.x, zone1.y + zone1.h, zone1.x + zone1.w, zone1.y + zone1.h, 1.0F, 1.0F, 0.0F, 1.0F, 1);
                    this.DrawIsoLine(zone1.x, zone1.y, zone1.x, zone1.y + zone1.h, 1.0F, 1.0F, 0.0F, 1.0F, 1);
                    this.DrawIsoLine(zone1.x + zone1.w, zone1.y, zone1.x + zone1.w, zone1.y + zone1.h, 1.0F, 1.0F, 0.0F, 1.0F, 1);
                }
            }
        }

        arrayList = IsoWorld.instance.MetaGrid.getZonesIntersecting(this.gridX - 1, this.gridY - 1, this.m_z, 3, 3, new ArrayList<>());
        PolygonalMap2.LiangBarsky liangBarsky = new PolygonalMap2.LiangBarsky();
        double[] doubles = new double[2];
        IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunkForGridSquare(this.gridX, this.gridY, this.m_z);

        for (int int5 = 0; int5 < arrayList.size(); int5++) {
            IsoMetaGrid.Zone zone2 = (IsoMetaGrid.Zone)arrayList.get(int5);
            if (zone2 != null && zone2.isPolyline() && !zone2.points.isEmpty()) {
                for (byte byte1 = 0; byte1 < zone2.points.size() - 2; byte1 += 2) {
                    int int6 = zone2.points.get(byte1);
                    int int7 = zone2.points.get(byte1 + 1);
                    int int8 = zone2.points.get(byte1 + 2);
                    int int9 = zone2.points.get(byte1 + 3);
                    this.DrawIsoLine(int6, int7, int8, int9, 1.0F, 1.0F, 0.0F, 1.0F, 1);
                    float float0 = int8 - int6;
                    float float1 = int9 - int7;
                    if (chunk != null
                        && liangBarsky.lineRectIntersect(
                            int6, int7, float0, float1, chunk.wx * 10, chunk.wy * 10, chunk.wx * 10 + 10, chunk.wy * 10 + 10, doubles
                        )) {
                        this.DrawIsoLine(
                            int6 + (float)doubles[0] * float0,
                            int7 + (float)doubles[0] * float1,
                            int6 + (float)doubles[1] * float0,
                            int7 + (float)doubles[1] * float1,
                            0.0F,
                            1.0F,
                            0.0F,
                            1.0F,
                            1
                        );
                    }
                }

                if (zone2.polylineOutlinePoints != null) {
                    float[] floats0 = zone2.polylineOutlinePoints;

                    for (byte byte2 = 0; byte2 < floats0.length; byte2 += 2) {
                        float float2 = floats0[byte2];
                        float float3 = floats0[byte2 + 1];
                        float float4 = floats0[(byte2 + 2) % floats0.length];
                        float float5 = floats0[(byte2 + 3) % floats0.length];
                        this.DrawIsoLine(float2, float3, float4, float5, 1.0F, 1.0F, 0.0F, 1.0F, 1);
                    }
                }
            }
        }

        IsoMetaGrid.VehicleZone vehicleZone = IsoWorld.instance.MetaGrid.getVehicleZoneAt(this.gridX, this.gridY, this.m_z);
        if (vehicleZone != null) {
            float float6 = 0.5F;
            float float7 = 1.0F;
            float float8 = 0.5F;
            float float9 = 1.0F;
            if (vehicleZone.isPolygon()) {
                for (byte byte3 = 0; byte3 < vehicleZone.points.size(); byte3 += 2) {
                    int int10 = vehicleZone.points.get(byte3);
                    int int11 = vehicleZone.points.get(byte3 + 1);
                    int int12 = vehicleZone.points.get((byte3 + 2) % vehicleZone.points.size());
                    int int13 = vehicleZone.points.get((byte3 + 3) % vehicleZone.points.size());
                    this.DrawIsoLine(int10, int11, int12, int13, 1.0F, 1.0F, 0.0F, 1.0F, 1);
                }
            } else if (vehicleZone.isPolyline()) {
                for (byte byte4 = 0; byte4 < vehicleZone.points.size() - 2; byte4 += 2) {
                    int int14 = vehicleZone.points.get(byte4);
                    int int15 = vehicleZone.points.get(byte4 + 1);
                    int int16 = vehicleZone.points.get(byte4 + 2);
                    int int17 = vehicleZone.points.get(byte4 + 3);
                    this.DrawIsoLine(int14, int15, int16, int17, 1.0F, 1.0F, 0.0F, 1.0F, 1);
                }

                if (vehicleZone.polylineOutlinePoints != null) {
                    float[] floats1 = vehicleZone.polylineOutlinePoints;

                    for (byte byte5 = 0; byte5 < floats1.length; byte5 += 2) {
                        float float10 = floats1[byte5];
                        float float11 = floats1[byte5 + 1];
                        float float12 = floats1[(byte5 + 2) % floats1.length];
                        float float13 = floats1[(byte5 + 3) % floats1.length];
                        this.DrawIsoLine(float10, float11, float12, float13, 1.0F, 1.0F, 0.0F, 1.0F, 1);
                    }
                }
            } else {
                this.DrawIsoLine(vehicleZone.x, vehicleZone.y, vehicleZone.x + vehicleZone.w, vehicleZone.y, float6, float7, float8, float9, 1);
                this.DrawIsoLine(
                    vehicleZone.x,
                    vehicleZone.y + vehicleZone.h,
                    vehicleZone.x + vehicleZone.w,
                    vehicleZone.y + vehicleZone.h,
                    float6,
                    float7,
                    float8,
                    float9,
                    1
                );
                this.DrawIsoLine(vehicleZone.x, vehicleZone.y, vehicleZone.x, vehicleZone.y + vehicleZone.h, float6, float7, float8, float9, 1);
                this.DrawIsoLine(
                    vehicleZone.x + vehicleZone.w,
                    vehicleZone.y,
                    vehicleZone.x + vehicleZone.w,
                    vehicleZone.y + vehicleZone.h,
                    float6,
                    float7,
                    float8,
                    float9,
                    1
                );
            }
        }

        if (this.RandomSquareInZone.getValue() && zone0 != null) {
            IsoGridSquare square = zone0.getRandomSquareInZone();
            if (square != null) {
                this.paintSquare(square.x, square.y, square.z, 0.0F, 1.0F, 0.0F, 0.5F);
            }
        }
    }

    private void drawVehicleStory() {
        ArrayList arrayList = IsoWorld.instance.MetaGrid.getZonesIntersecting(this.gridX - 1, this.gridY - 1, this.m_z, 3, 3, new ArrayList<>());
        if (!arrayList.isEmpty()) {
            IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunkForGridSquare(this.gridX, this.gridY, this.m_z);
            if (chunk != null) {
                for (int int0 = 0; int0 < arrayList.size(); int0++) {
                    IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)arrayList.get(int0);
                    if ("Nav".equals(zone.type)) {
                        VehicleStorySpawner vehicleStorySpawner = VehicleStorySpawner.getInstance();
                        RandomizedVehicleStoryBase randomizedVehicleStoryBase = IsoWorld.instance.getRandomizedVehicleStoryByName(this.m_vehicleStory);
                        if (randomizedVehicleStoryBase != null
                            && randomizedVehicleStoryBase.isValid(zone, chunk, true)
                            && randomizedVehicleStoryBase.initVehicleStorySpawner(zone, chunk, true)) {
                            int int1 = randomizedVehicleStoryBase.getMinZoneWidth();
                            int int2 = randomizedVehicleStoryBase.getMinZoneHeight();
                            float[] floats = new float[3];
                            if (randomizedVehicleStoryBase.getSpawnPoint(zone, chunk, floats)) {
                                float float0 = floats[0];
                                float float1 = floats[1];
                                float float2 = floats[2] + (float) (Math.PI / 2);
                                vehicleStorySpawner.spawn(float0, float1, 0.0F, float2, (var0, var1x) -> {});
                                vehicleStorySpawner.render(float0, float1, 0.0F, int1, int2, floats[2]);
                            }
                        }
                    }
                }
            }
        }
    }

    private void DrawBehindStuff() {
        this.IsBehindStuff(IsoCamera.CamCharacter.getCurrentSquare());
    }

    private boolean IsBehindStuff(IsoGridSquare square) {
        for (int int0 = 1; int0 < 8 && square.getZ() + int0 < 8; int0++) {
            for (int int1 = -5; int1 <= 6; int1++) {
                for (int int2 = -5; int2 <= 6; int2++) {
                    if (int2 >= int1 - 5 && int2 <= int1 + 5) {
                        this.paintSquare(square.getX() + int2 + int0 * 3, square.getY() + int1 + int0 * 3, square.getZ() + int0, 1.0F, 1.0F, 0.0F, 0.25F);
                    }
                }
            }
        }

        return true;
    }

    private boolean IsBehindStuffRecY(int int0, int int1, int int2) {
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (int2 >= 15) {
            return false;
        } else {
            this.paintSquare(int0, int1, int2, 1.0F, 1.0F, 0.0F, 0.25F);
            return this.IsBehindStuffRecY(int0, int1 + 1, int2 + 1);
        }
    }

    private boolean IsBehindStuffRecXY(int int0, int int1, int int2, int int3) {
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (int2 >= 15) {
            return false;
        } else {
            this.paintSquare(int0, int1, int2, 1.0F, 1.0F, 0.0F, 0.25F);
            return this.IsBehindStuffRecXY(int0 + int3, int1 + int3, int2 + 1, int3);
        }
    }

    private boolean IsBehindStuffRecX(int int0, int int1, int int2) {
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (int2 >= 15) {
            return false;
        } else {
            this.paintSquare(int0, int1, int2, 1.0F, 1.0F, 0.0F, 0.25F);
            return this.IsBehindStuffRecX(int0 + 1, int1, int2 + 1);
        }
    }

    private void paintSquare(int int4, int int3, int int2, float float0, float float1, float float2, float float3) {
        int int0 = Core.TileScale;
        int int1 = (int)IsoUtils.XToScreenExact(int4, int3 + 1, int2, 0);
        int int5 = (int)IsoUtils.YToScreenExact(int4, int3 + 1, int2, 0);
        SpriteRenderer.instance
            .renderPoly(
                int1, int5, int1 + 32 * int0, int5 - 16 * int0, int1 + 64 * int0, int5, int1 + 32 * int0, int5 + 16 * int0, float0, float1, float2, float3
            );
    }

    void drawModData() {
        int int0 = this.m_z;
        IsoGridSquare square0 = IsoWorld.instance.getCell().getGridSquare(this.gridX, this.gridY, int0);
        int int1 = Core.getInstance().getScreenWidth() - 250;
        int int2 = 10;
        int int3 = TextManager.instance.getFontFromEnum(this.FONT).getLineHeight();
        if (square0 != null && square0.getModData() != null) {
            KahluaTable table = square0.getModData();
            int int4;
            this.DrawString(int1, int4 = int2 + int3, "MOD DATA x,y,z=" + square0.getX() + "," + square0.getY() + "," + square0.getZ());
            KahluaTableIterator kahluaTableIterator0 = table.iterator();

            while (kahluaTableIterator0.advance()) {
                this.DrawString(int1, int4 += int3, kahluaTableIterator0.getKey().toString() + " = " + kahluaTableIterator0.getValue().toString());
                if (kahluaTableIterator0.getValue() instanceof KahluaTable) {
                    KahluaTableIterator kahluaTableIterator1 = ((KahluaTable)kahluaTableIterator0.getValue()).iterator();

                    while (kahluaTableIterator1.advance()) {
                        this.DrawString(int1 + 8, int4 += int3, kahluaTableIterator1.getKey().toString() + " = " + kahluaTableIterator1.getValue().toString());
                    }
                }
            }

            int2 = int4 + int3;
        }

        if (square0 != null) {
            PropertyContainer propertyContainer = square0.getProperties();
            ArrayList arrayList = propertyContainer.getPropertyNames();
            if (!arrayList.isEmpty()) {
                this.DrawString(int1, int2 += int3, "PROPERTIES x,y,z=" + square0.getX() + "," + square0.getY() + "," + square0.getZ());
                Collections.sort(arrayList);

                for (String string : arrayList) {
                    this.DrawString(int1, int2 += int3, string + " = \"" + propertyContainer.Val(string) + "\"");
                }
            }

            for (IsoFlagType flagType : IsoFlagType.values()) {
                if (propertyContainer.Is(flagType)) {
                    this.DrawString(int1, int2 += int3, flagType.toString());
                }
            }
        }

        if (square0 != null) {
            ErosionData.Square square1 = square0.getErosionData();
            if (square1 != null) {
                int2 += int3;
                int int5;
                this.DrawString(int1, int5 = int2 + int3, "EROSION x,y,z=" + square0.getX() + "," + square0.getY() + "," + square0.getZ());
                this.DrawString(int1, int2 = int5 + int3, "init=" + square1.init);
                int int6;
                this.DrawString(int1, int6 = int2 + int3, "doNothing=" + square1.doNothing);
                this.DrawString(int1, int2 = int6 + int3, "chunk.init=" + square0.chunk.getErosionData().init);
            }
        }
    }

    void drawPlayerInfo() {
        int int0 = Core.getInstance().getScreenWidth() - 250;
        int int1 = Core.getInstance().getScreenHeight() / 2;
        int int2 = TextManager.instance.getFontFromEnum(this.FONT).getLineHeight();
        IsoGameCharacter character = IsoCamera.CamCharacter;
        int int3;
        this.DrawString(int0, int3 = int1 + int2, "bored = " + character.getBodyDamage().getBoredomLevel());
        this.DrawString(int0, int1 = int3 + int2, "endurance = " + character.getStats().endurance);
        int int4;
        this.DrawString(int0, int4 = int1 + int2, "fatigue = " + character.getStats().fatigue);
        this.DrawString(int0, int1 = int4 + int2, "hunger = " + character.getStats().hunger);
        int int5;
        this.DrawString(int0, int5 = int1 + int2, "pain = " + character.getStats().Pain);
        this.DrawString(int0, int1 = int5 + int2, "panic = " + character.getStats().Panic);
        int int6;
        this.DrawString(int0, int6 = int1 + int2, "stress = " + character.getStats().getStress());
        this.DrawString(int0, int1 = int6 + int2, "clothingTemp = " + ((IsoPlayer)character).getPlayerClothingTemperature());
        int int7;
        this.DrawString(int0, int7 = int1 + int2, "temperature = " + character.getTemperature());
        this.DrawString(int0, int1 = int7 + int2, "thirst = " + character.getStats().thirst);
        int int8;
        this.DrawString(int0, int8 = int1 + int2, "foodPoison = " + character.getBodyDamage().getFoodSicknessLevel());
        this.DrawString(int0, int1 = int8 + int2, "poison = " + character.getBodyDamage().getPoisonLevel());
        int int9;
        this.DrawString(int0, int9 = int1 + int2, "unhappy = " + character.getBodyDamage().getUnhappynessLevel());
        this.DrawString(int0, int1 = int9 + int2, "infected = " + character.getBodyDamage().isInfected());
        int int10;
        this.DrawString(int0, int10 = int1 + int2, "InfectionLevel = " + character.getBodyDamage().getInfectionLevel());
        this.DrawString(int0, int1 = int10 + int2, "FakeInfectionLevel = " + character.getBodyDamage().getFakeInfectionLevel());
        int1 += int2;
        int int11;
        this.DrawString(int0, int11 = int1 + int2, "WORLD");
        this.DrawString(int0, int1 = int11 + int2, "globalTemperature = " + IsoWorld.instance.getGlobalTemperature());
    }

    public LosUtil.TestResults lineClearCached(IsoCell cell, int x1, int y1, int z1, int x0, int y0, int z0, boolean bIgnoreDoors) {
        int int0 = y1 - y0;
        int int1 = x1 - x0;
        int int2 = z1 - z0;
        int int3 = int1 + 100;
        int int4 = int0 + 100;
        int int5 = int2 + 16;
        if (int3 >= 0 && int4 >= 0 && int5 >= 0 && int3 < 200 && int4 < 200) {
            LosUtil.TestResults testResults = LosUtil.TestResults.Clear;
            byte byte0 = 1;
            float float0 = 0.5F;
            float float1 = 0.5F;
            IsoGridSquare square0 = cell.getGridSquare(x0, y0, z0);
            if (Math.abs(int1) > Math.abs(int0) && Math.abs(int1) > Math.abs(int2)) {
                float float2 = (float)int0 / int1;
                float float3 = (float)int2 / int1;
                float0 += y0;
                float1 += z0;
                int1 = int1 < 0 ? -1 : 1;
                float2 *= int1;
                float3 *= int1;

                while (x0 != x1) {
                    x0 += int1;
                    float0 += float2;
                    float1 += float3;
                    IsoGridSquare square1 = cell.getGridSquare(x0, (int)float0, (int)float1);
                    this.paintSquare(x0, (int)float0, (int)float1, 1.0F, 1.0F, 1.0F, 0.5F);
                    if (square1 != null
                        && square0 != null
                        && square1.testVisionAdjacent(
                                square0.getX() - square1.getX(), square0.getY() - square1.getY(), square0.getZ() - square1.getZ(), true, bIgnoreDoors
                            )
                            == LosUtil.TestResults.Blocked) {
                        this.paintSquare(x0, (int)float0, (int)float1, 1.0F, 0.0F, 0.0F, 0.5F);
                        this.paintSquare(square0.getX(), square0.getY(), square0.getZ(), 1.0F, 0.0F, 0.0F, 0.5F);
                        byte0 = 4;
                    }

                    square0 = square1;
                    int int6 = (int)float0;
                    int int7 = (int)float1;
                }
            } else if (Math.abs(int0) >= Math.abs(int1) && Math.abs(int0) > Math.abs(int2)) {
                float float4 = (float)int1 / int0;
                float float5 = (float)int2 / int0;
                float0 += x0;
                float1 += z0;
                int0 = int0 < 0 ? -1 : 1;
                float4 *= int0;
                float5 *= int0;

                while (y0 != y1) {
                    y0 += int0;
                    float0 += float4;
                    float1 += float5;
                    IsoGridSquare square2 = cell.getGridSquare((int)float0, y0, (int)float1);
                    this.paintSquare((int)float0, y0, (int)float1, 1.0F, 1.0F, 1.0F, 0.5F);
                    if (square2 != null
                        && square0 != null
                        && square2.testVisionAdjacent(
                                square0.getX() - square2.getX(), square0.getY() - square2.getY(), square0.getZ() - square2.getZ(), true, bIgnoreDoors
                            )
                            == LosUtil.TestResults.Blocked) {
                        this.paintSquare((int)float0, y0, (int)float1, 1.0F, 0.0F, 0.0F, 0.5F);
                        this.paintSquare(square0.getX(), square0.getY(), square0.getZ(), 1.0F, 0.0F, 0.0F, 0.5F);
                        byte0 = 4;
                    }

                    square0 = square2;
                    int int8 = (int)float0;
                    int int9 = (int)float1;
                }
            } else {
                float float6 = (float)int1 / int2;
                float float7 = (float)int0 / int2;
                float0 += x0;
                float1 += y0;
                int2 = int2 < 0 ? -1 : 1;
                float6 *= int2;
                float7 *= int2;

                while (z0 != z1) {
                    z0 += int2;
                    float0 += float6;
                    float1 += float7;
                    IsoGridSquare square3 = cell.getGridSquare((int)float0, (int)float1, z0);
                    this.paintSquare((int)float0, (int)float1, z0, 1.0F, 1.0F, 1.0F, 0.5F);
                    if (square3 != null
                        && square0 != null
                        && square3.testVisionAdjacent(
                                square0.getX() - square3.getX(), square0.getY() - square3.getY(), square0.getZ() - square3.getZ(), true, bIgnoreDoors
                            )
                            == LosUtil.TestResults.Blocked) {
                        byte0 = 4;
                    }

                    square0 = square3;
                    int int10 = (int)float0;
                    int int11 = (int)float1;
                }
            }

            if (byte0 == 1) {
                return LosUtil.TestResults.Clear;
            } else if (byte0 == 2) {
                return LosUtil.TestResults.ClearThroughOpenDoor;
            } else if (byte0 == 3) {
                return LosUtil.TestResults.ClearThroughWindow;
            } else {
                return byte0 == 4 ? LosUtil.TestResults.Blocked : LosUtil.TestResults.Blocked;
            }
        } else {
            return LosUtil.TestResults.Blocked;
        }
    }

    private void DrawString(int int3, int int2, String string) {
        int int0 = TextManager.instance.MeasureStringX(this.FONT, string);
        int int1 = TextManager.instance.getFontFromEnum(this.FONT).getLineHeight();
        SpriteRenderer.instance.renderi(null, int3 - 1, int2, int0 + 2, int1, 0.0F, 0.0F, 0.0F, 0.8F, null);
        TextManager.instance.DrawString(this.FONT, int3, int2, string, 1.0, 1.0, 1.0, 1.0);
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
        String string = ZomboidFileSystem.instance.getCacheDir() + File.separator + "debugChunkState-options.ini";
        ConfigFile configFile = new ConfigFile();
        configFile.write(string, 1, this.options);
    }

    public void load() {
        String string = ZomboidFileSystem.instance.getCacheDir() + File.separator + "debugChunkState-options.ini";
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
            DebugChunkState.this.options.add(this);
        }
    }

    private class FloodFill {
        private IsoGridSquare start = null;
        private final int FLOOD_SIZE = 11;
        private BooleanGrid visited = new BooleanGrid(11, 11);
        private Stack<IsoGridSquare> stack = new Stack<>();
        private IsoBuilding building = null;
        private Mover mover = null;

        void calculate(Mover moverx, IsoGridSquare square) {
            this.start = square;
            this.mover = moverx;
            if (this.start.getRoom() != null) {
                this.building = this.start.getRoom().getBuilding();
            }

            boolean boolean0 = false;
            boolean boolean1 = false;
            if (this.push(this.start.getX(), this.start.getY())) {
                while ((square = this.pop()) != null) {
                    int int0 = square.getX();
                    int int1 = square.getY();

                    while (this.shouldVisit(int0, int1, int0, int1 - 1)) {
                        int1--;
                    }

                    boolean1 = false;
                    boolean0 = false;

                    do {
                        this.visited.setValue(this.gridX(int0), this.gridY(int1), true);
                        if (!boolean0 && this.shouldVisit(int0, int1, int0 - 1, int1)) {
                            if (!this.push(int0 - 1, int1)) {
                                return;
                            }

                            boolean0 = true;
                        } else if (boolean0 && !this.shouldVisit(int0, int1, int0 - 1, int1)) {
                            boolean0 = false;
                        } else if (boolean0 && !this.shouldVisit(int0 - 1, int1, int0 - 1, int1 - 1) && !this.push(int0 - 1, int1)) {
                            return;
                        }

                        if (!boolean1 && this.shouldVisit(int0, int1, int0 + 1, int1)) {
                            if (!this.push(int0 + 1, int1)) {
                                return;
                            }

                            boolean1 = true;
                        } else if (boolean1 && !this.shouldVisit(int0, int1, int0 + 1, int1)) {
                            boolean1 = false;
                        } else if (boolean1 && !this.shouldVisit(int0 + 1, int1, int0 + 1, int1 - 1) && !this.push(int0 + 1, int1)) {
                            return;
                        }

                        int1++;
                    } while (!this.shouldVisit(int0, int1 - 1, int0, int1));
                }
            }
        }

        boolean shouldVisit(int int2, int int3, int int0, int int1) {
            if (this.gridX(int0) < 11 && this.gridX(int0) >= 0) {
                if (this.gridY(int1) < 11 && this.gridY(int1) >= 0) {
                    if (this.visited.getValue(this.gridX(int0), this.gridY(int1))) {
                        return false;
                    } else {
                        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, this.start.getZ());
                        if (square == null) {
                            return false;
                        } else if (square.Has(IsoObjectType.stairsBN) || square.Has(IsoObjectType.stairsMN) || square.Has(IsoObjectType.stairsTN)) {
                            return false;
                        } else if (square.Has(IsoObjectType.stairsBW) || square.Has(IsoObjectType.stairsMW) || square.Has(IsoObjectType.stairsTW)) {
                            return false;
                        } else if (square.getRoom() != null && this.building == null) {
                            return false;
                        } else {
                            return square.getRoom() == null && this.building != null
                                ? false
                                : !IsoWorld.instance.CurrentCell.blocked(this.mover, int0, int1, this.start.getZ(), int2, int3, this.start.getZ());
                        }
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        boolean push(int int0, int int1) {
            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, this.start.getZ());
            this.stack.push(square);
            return true;
        }

        IsoGridSquare pop() {
            return this.stack.isEmpty() ? null : this.stack.pop();
        }

        int gridX(int int0) {
            return int0 - (this.start.getX() - 5);
        }

        int gridY(int int0) {
            return int0 - (this.start.getY() - 5);
        }

        int gridX(IsoGridSquare square) {
            return square.getX() - (this.start.getX() - 5);
        }

        int gridY(IsoGridSquare square) {
            return square.getY() - (this.start.getY() - 5);
        }

        void draw() {
            int int0 = this.start.getX() - 5;
            int int1 = this.start.getY() - 5;

            for (int int2 = 0; int2 < 11; int2++) {
                for (int int3 = 0; int3 < 11; int3++) {
                    if (this.visited.getValue(int3, int2)) {
                        int int4 = (int)IsoUtils.XToScreenExact(int0 + int3, int1 + int2 + 1, this.start.getZ(), 0);
                        int int5 = (int)IsoUtils.YToScreenExact(int0 + int3, int1 + int2 + 1, this.start.getZ(), 0);
                        SpriteRenderer.instance.renderPoly(int4, int5, int4 + 32, int5 - 16, int4 + 64, int5, int4 + 32, int5 + 16, 1.0F, 1.0F, 0.0F, 0.5F);
                    }
                }
            }
        }
    }
}
