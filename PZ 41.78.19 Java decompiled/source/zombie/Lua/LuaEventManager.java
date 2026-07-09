// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.Lua;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;
import se.krka.kahlua.vm.Prototype;
import zombie.debug.DebugLog;

public final class LuaEventManager implements JavaFunction {
    public static final ArrayList<LuaClosure> OnTickCallbacks = new ArrayList<>();
    static Object[][] a1 = new Object[1][1];
    static Object[][] a2 = new Object[1][2];
    static Object[][] a3 = new Object[1][3];
    static Object[][] a4 = new Object[1][4];
    static Object[][] a5 = new Object[1][5];
    static Object[][] a6 = new Object[1][6];
    static Object[][] a7 = new Object[1][7];
    static Object[][] a8 = new Object[1][8];
    static int a1index = 0;
    static int a2index = 0;
    static int a3index = 0;
    static int a4index = 0;
    static int a5index = 0;
    static int a6index = 0;
    static int a7index = 0;
    static int a8index = 0;
    private static final ArrayList<Event> EventList = new ArrayList<>();
    private static final HashMap<String, Event> EventMap = new HashMap<>();

    private static Event checkEvent(String string) {
        Event event = EventMap.get(string);
        if (event == null) {
            DebugLog.log("LuaEventManager: adding unknown event \"" + string + "\"");
            event = AddEvent(string);
        }

        return event.callbacks.isEmpty() ? null : event;
    }

    public static void triggerEvent(String event) {
        synchronized (EventMap) {
            Event _event = checkEvent(event);
            if (_event != null) {
                _event.trigger(LuaManager.env, LuaManager.caller, null);
            }
        }
    }

    public static void triggerEvent(String event, Object param1) {
        synchronized (EventMap) {
            Event _event = checkEvent(event);
            if (_event != null) {
                if (a1index == a1.length) {
                    a1 = Arrays.copyOf(a1, a1.length * 2);

                    for (int int0 = a1index; int0 < a1.length; int0++) {
                        a1[int0] = new Object[1];
                    }
                }

                Object[] objects = a1[a1index];
                objects[0] = param1;
                a1index++;

                try {
                    _event.trigger(LuaManager.env, LuaManager.caller, objects);
                } finally {
                    a1index--;
                    objects[0] = null;
                }
            }
        }
    }

    public static void triggerEventGarbage(String event, Object param1) {
        triggerEvent(event, param1);
    }

    public static void triggerEventUnique(String event, Object param1) {
        triggerEvent(event, param1);
    }

    public static void triggerEvent(String event, Object param1, Object param2) {
        synchronized (EventMap) {
            Event _event = checkEvent(event);
            if (_event != null) {
                if (a2index == a2.length) {
                    a2 = Arrays.copyOf(a2, a2.length * 2);

                    for (int int0 = a2index; int0 < a2.length; int0++) {
                        a2[int0] = new Object[2];
                    }
                }

                Object[] objects = a2[a2index];
                objects[0] = param1;
                objects[1] = param2;
                a2index++;

                try {
                    _event.trigger(LuaManager.env, LuaManager.caller, objects);
                } finally {
                    a2index--;
                    objects[0] = null;
                    objects[1] = null;
                }
            }
        }
    }

    public static void triggerEventGarbage(String event, Object param1, Object param2) {
        triggerEvent(event, param1, param2);
    }

    public static void triggerEvent(String event, Object param1, Object param2, Object param3) {
        synchronized (EventMap) {
            Event _event = checkEvent(event);
            if (_event != null) {
                if (a3index == a3.length) {
                    a3 = Arrays.copyOf(a3, a3.length * 2);

                    for (int int0 = a3index; int0 < a3.length; int0++) {
                        a3[int0] = new Object[3];
                    }
                }

                Object[] objects = a3[a3index];
                objects[0] = param1;
                objects[1] = param2;
                objects[2] = param3;
                a3index++;

                try {
                    _event.trigger(LuaManager.env, LuaManager.caller, objects);
                } finally {
                    a3index--;
                    objects[0] = null;
                    objects[1] = null;
                    objects[2] = null;
                }
            }
        }
    }

    public static void triggerEventGarbage(String event, Object param1, Object param2, Object param3) {
        triggerEvent(event, param1, param2, param3);
    }

    public static void triggerEvent(String event, Object param1, Object param2, Object param3, Object param4) {
        synchronized (EventMap) {
            Event _event = checkEvent(event);
            if (_event != null) {
                if (a4index == a4.length) {
                    a4 = Arrays.copyOf(a4, a4.length * 2);

                    for (int int0 = a4index; int0 < a4.length; int0++) {
                        a4[int0] = new Object[4];
                    }
                }

                Object[] objects = a4[a4index];
                objects[0] = param1;
                objects[1] = param2;
                objects[2] = param3;
                objects[3] = param4;
                a4index++;

                try {
                    _event.trigger(LuaManager.env, LuaManager.caller, objects);
                } finally {
                    a4index--;
                    objects[0] = null;
                    objects[1] = null;
                    objects[2] = null;
                    objects[3] = null;
                }
            }
        }
    }

    public static void triggerEventGarbage(String event, Object param1, Object param2, Object param3, Object param4) {
        triggerEvent(event, param1, param2, param3, param4);
    }

    public static void triggerEvent(String event, Object param1, Object param2, Object param3, Object param4, Object param5) {
        synchronized (EventMap) {
            Event _event = checkEvent(event);
            if (_event != null) {
                if (a5index == a5.length) {
                    a5 = Arrays.copyOf(a5, a5.length * 2);

                    for (int int0 = a5index; int0 < a5.length; int0++) {
                        a5[int0] = new Object[5];
                    }
                }

                Object[] objects = a5[a5index];
                objects[0] = param1;
                objects[1] = param2;
                objects[2] = param3;
                objects[3] = param4;
                objects[4] = param5;
                a5index++;

                try {
                    _event.trigger(LuaManager.env, LuaManager.caller, objects);
                } finally {
                    a5index--;
                    objects[0] = null;
                    objects[1] = null;
                    objects[2] = null;
                    objects[3] = null;
                    objects[4] = null;
                }
            }
        }
    }

    public static void triggerEvent(String event, Object param1, Object param2, Object param3, Object param4, Object param5, Object param6) {
        synchronized (EventMap) {
            Event _event = checkEvent(event);
            if (_event != null) {
                if (a6index == a6.length) {
                    a6 = Arrays.copyOf(a6, a6.length * 2);

                    for (int int0 = a6index; int0 < a6.length; int0++) {
                        a6[int0] = new Object[6];
                    }
                }

                Object[] objects = a6[a6index];
                objects[0] = param1;
                objects[1] = param2;
                objects[2] = param3;
                objects[3] = param4;
                objects[4] = param5;
                objects[5] = param6;
                a6index++;

                try {
                    _event.trigger(LuaManager.env, LuaManager.caller, objects);
                } finally {
                    a6index--;
                    objects[0] = null;
                    objects[1] = null;
                    objects[2] = null;
                    objects[3] = null;
                    objects[4] = null;
                    objects[5] = null;
                }
            }
        }
    }

    public static void triggerEvent(String event, Object param1, Object param2, Object param3, Object param4, Object param5, Object param6, Object param7) {
        synchronized (EventMap) {
            Event _event = checkEvent(event);
            if (_event != null) {
                if (a7index == a7.length) {
                    a7 = Arrays.copyOf(a7, a7.length * 2);

                    for (int int0 = a7index; int0 < a7.length; int0++) {
                        a7[int0] = new Object[7];
                    }
                }

                Object[] objects = a7[a7index];
                objects[0] = param1;
                objects[1] = param2;
                objects[2] = param3;
                objects[3] = param4;
                objects[4] = param5;
                objects[5] = param6;
                objects[6] = param7;
                a7index++;

                try {
                    _event.trigger(LuaManager.env, LuaManager.caller, objects);
                } finally {
                    a7index--;
                    objects[0] = null;
                    objects[1] = null;
                    objects[2] = null;
                    objects[3] = null;
                    objects[4] = null;
                    objects[5] = null;
                    objects[6] = null;
                }
            }
        }
    }

    public static void triggerEvent(
        String event, Object param1, Object param2, Object param3, Object param4, Object param5, Object param6, Object param7, Object param8
    ) {
        synchronized (EventMap) {
            Event _event = checkEvent(event);
            if (_event != null) {
                if (a8index == a8.length) {
                    a8 = Arrays.copyOf(a8, a8.length * 2);

                    for (int int0 = a8index; int0 < a8.length; int0++) {
                        a8[int0] = new Object[8];
                    }
                }

                Object[] objects = a8[a8index];
                objects[0] = param1;
                objects[1] = param2;
                objects[2] = param3;
                objects[3] = param4;
                objects[4] = param5;
                objects[5] = param6;
                objects[6] = param7;
                objects[7] = param8;
                a8index++;

                try {
                    _event.trigger(LuaManager.env, LuaManager.caller, objects);
                } finally {
                    a8index--;
                    objects[0] = null;
                    objects[1] = null;
                    objects[2] = null;
                    objects[3] = null;
                    objects[4] = null;
                    objects[5] = null;
                    objects[6] = null;
                    objects[7] = null;
                }
            }
        }
    }

    public static Event AddEvent(String name) {
        Event event = EventMap.get(name);
        if (event != null) {
            return event;
        } else {
            event = new Event(name, EventList.size());
            EventList.add(event);
            EventMap.put(name, event);
            if (LuaManager.env.rawget("Events") instanceof KahluaTable table) {
                event.register(LuaManager.platform, table);
            } else {
                DebugLog.log("ERROR: 'Events' table not found or not a table");
            }

            return event;
        }
    }

    private static void AddEvents() {
        AddEvent("OnGameBoot");
        AddEvent("OnPreGameStart");
        AddEvent("OnTick");
        AddEvent("OnTickEvenPaused");
        AddEvent("OnRenderUpdate");
        AddEvent("OnFETick");
        AddEvent("OnGameStart");
        AddEvent("OnPreUIDraw");
        AddEvent("OnPostUIDraw");
        AddEvent("OnCharacterCollide");
        AddEvent("OnKeyStartPressed");
        AddEvent("OnKeyPressed");
        AddEvent("OnObjectCollide");
        AddEvent("OnNPCSurvivorUpdate");
        AddEvent("OnPlayerUpdate");
        AddEvent("OnZombieUpdate");
        AddEvent("OnTriggerNPCEvent");
        AddEvent("OnMultiTriggerNPCEvent");
        AddEvent("OnLoadMapZones");
        AddEvent("OnLoadedMapZones");
        AddEvent("OnAddBuilding");
        AddEvent("OnCreateLivingCharacter");
        AddEvent("OnChallengeQuery");
        AddEvent("OnFillInventoryObjectContextMenu");
        AddEvent("OnPreFillInventoryObjectContextMenu");
        AddEvent("OnFillWorldObjectContextMenu");
        AddEvent("OnPreFillWorldObjectContextMenu");
        AddEvent("OnRefreshInventoryWindowContainers");
        AddEvent("OnGamepadConnect");
        AddEvent("OnGamepadDisconnect");
        AddEvent("OnJoypadActivate");
        AddEvent("OnJoypadActivateUI");
        AddEvent("OnJoypadBeforeDeactivate");
        AddEvent("OnJoypadDeactivate");
        AddEvent("OnJoypadBeforeReactivate");
        AddEvent("OnJoypadReactivate");
        AddEvent("OnJoypadRenderUI");
        AddEvent("OnMakeItem");
        AddEvent("OnWeaponHitCharacter");
        AddEvent("OnWeaponSwing");
        AddEvent("OnWeaponHitTree");
        AddEvent("OnWeaponHitXp");
        AddEvent("OnWeaponSwingHitPoint");
        AddEvent("OnPlayerAttackFinished");
        AddEvent("OnLoginState");
        AddEvent("OnLoginStateSuccess");
        AddEvent("OnCharacterCreateStats");
        AddEvent("OnLoadSoundBanks");
        AddEvent("OnObjectLeftMouseButtonDown");
        AddEvent("OnObjectLeftMouseButtonUp");
        AddEvent("OnObjectRightMouseButtonDown");
        AddEvent("OnObjectRightMouseButtonUp");
        AddEvent("OnDoTileBuilding");
        AddEvent("OnDoTileBuilding2");
        AddEvent("OnDoTileBuilding3");
        AddEvent("OnConnectFailed");
        AddEvent("OnConnected");
        AddEvent("OnDisconnect");
        AddEvent("OnConnectionStateChanged");
        AddEvent("OnScoreboardUpdate");
        AddEvent("OnMouseMove");
        AddEvent("OnMouseDown");
        AddEvent("OnMouseUp");
        AddEvent("OnRightMouseDown");
        AddEvent("OnRightMouseUp");
        AddEvent("OnNewSurvivorGroup");
        AddEvent("OnPlayerSetSafehouse");
        AddEvent("OnLoad");
        AddEvent("AddXP");
        AddEvent("LevelPerk");
        AddEvent("OnSave");
        AddEvent("OnMainMenuEnter");
        AddEvent("OnGameStateEnter");
        AddEvent("OnPreMapLoad");
        AddEvent("OnPostFloorSquareDraw");
        AddEvent("OnPostFloorLayerDraw");
        AddEvent("OnPostTilesSquareDraw");
        AddEvent("OnPostTileDraw");
        AddEvent("OnPostWallSquareDraw");
        AddEvent("OnPostCharactersSquareDraw");
        AddEvent("OnCreateUI");
        AddEvent("OnMapLoadCreateIsoObject");
        AddEvent("OnCreateSurvivor");
        AddEvent("OnCreatePlayer");
        AddEvent("OnPlayerDeath");
        AddEvent("OnZombieDead");
        AddEvent("OnCharacterDeath");
        AddEvent("OnCharacterMeet");
        AddEvent("OnSpawnRegionsLoaded");
        AddEvent("OnPostMapLoad");
        AddEvent("OnAIStateExecute");
        AddEvent("OnAIStateEnter");
        AddEvent("OnAIStateExit");
        AddEvent("OnAIStateChange");
        AddEvent("OnPlayerMove");
        AddEvent("OnInitWorld");
        AddEvent("OnNewGame");
        AddEvent("OnIsoThumpableLoad");
        AddEvent("OnIsoThumpableSave");
        AddEvent("ReuseGridsquare");
        AddEvent("LoadGridsquare");
        AddEvent("EveryOneMinute");
        AddEvent("EveryTenMinutes");
        AddEvent("EveryDays");
        AddEvent("EveryHours");
        AddEvent("OnDusk");
        AddEvent("OnDawn");
        AddEvent("OnEquipPrimary");
        AddEvent("OnEquipSecondary");
        AddEvent("OnClothingUpdated");
        AddEvent("OnWeatherPeriodStart");
        AddEvent("OnWeatherPeriodStage");
        AddEvent("OnWeatherPeriodComplete");
        AddEvent("OnWeatherPeriodStop");
        AddEvent("OnRainStart");
        AddEvent("OnRainStop");
        AddEvent("OnAmbientSound");
        AddEvent("OnWorldSound");
        AddEvent("OnResetLua");
        AddEvent("OnModsModified");
        AddEvent("OnSeeNewRoom");
        AddEvent("OnNewFire");
        AddEvent("OnFillContainer");
        AddEvent("OnChangeWeather");
        AddEvent("OnRenderTick");
        AddEvent("OnDestroyIsoThumpable");
        AddEvent("OnPostSave");
        AddEvent("OnResolutionChange");
        AddEvent("OnWaterAmountChange");
        AddEvent("OnClientCommand");
        AddEvent("OnServerCommand");
        AddEvent("OnContainerUpdate");
        AddEvent("OnObjectAdded");
        AddEvent("OnObjectAboutToBeRemoved");
        AddEvent("onLoadModDataFromServer");
        AddEvent("OnGameTimeLoaded");
        AddEvent("OnCGlobalObjectSystemInit");
        AddEvent("OnSGlobalObjectSystemInit");
        AddEvent("OnWorldMessage");
        AddEvent("OnKeyKeepPressed");
        AddEvent("SendCustomModData");
        AddEvent("ServerPinged");
        AddEvent("OnServerStarted");
        AddEvent("OnLoadedTileDefinitions");
        AddEvent("OnPostRender");
        AddEvent("DoSpecialTooltip");
        AddEvent("OnCoopJoinFailed");
        AddEvent("OnServerWorkshopItems");
        AddEvent("OnVehicleDamageTexture");
        AddEvent("OnCustomUIKey");
        AddEvent("OnCustomUIKeyPressed");
        AddEvent("OnCustomUIKeyReleased");
        AddEvent("OnDeviceText");
        AddEvent("OnRadioInteraction");
        AddEvent("OnLoadRadioScripts");
        AddEvent("OnAcceptInvite");
        AddEvent("OnCoopServerMessage");
        AddEvent("OnReceiveUserlog");
        AddEvent("OnAdminMessage");
        AddEvent("OnGetDBSchema");
        AddEvent("OnGetTableResult");
        AddEvent("ReceiveFactionInvite");
        AddEvent("AcceptedFactionInvite");
        AddEvent("ReceiveSafehouseInvite");
        AddEvent("AcceptedSafehouseInvite");
        AddEvent("ViewTickets");
        AddEvent("SyncFaction");
        AddEvent("OnReceiveItemListNet");
        AddEvent("OnMiniScoreboardUpdate");
        AddEvent("OnSafehousesChanged");
        AddEvent("RequestTrade");
        AddEvent("AcceptedTrade");
        AddEvent("TradingUIAddItem");
        AddEvent("TradingUIRemoveItem");
        AddEvent("TradingUIUpdateState");
        AddEvent("OnGridBurnt");
        AddEvent("OnPreDistributionMerge");
        AddEvent("OnDistributionMerge");
        AddEvent("OnPostDistributionMerge");
        AddEvent("MngInvReceiveItems");
        AddEvent("OnTileRemoved");
        AddEvent("OnServerStartSaving");
        AddEvent("OnServerFinishSaving");
        AddEvent("OnMechanicActionDone");
        AddEvent("OnClimateTick");
        AddEvent("OnThunderEvent");
        AddEvent("OnEnterVehicle");
        AddEvent("OnSteamGameJoin");
        AddEvent("OnTabAdded");
        AddEvent("OnSetDefaultTab");
        AddEvent("OnTabRemoved");
        AddEvent("OnAddMessage");
        AddEvent("SwitchChatStream");
        AddEvent("OnChatWindowInit");
        AddEvent("OnInitSeasons");
        AddEvent("OnClimateTickDebug");
        AddEvent("OnInitModdedWeatherStage");
        AddEvent("OnUpdateModdedWeatherStage");
        AddEvent("OnClimateManagerInit");
        AddEvent("OnPressReloadButton");
        AddEvent("OnPressRackButton");
        AddEvent("OnPressWalkTo");
        AddEvent("OnHitZombie");
        AddEvent("OnBeingHitByZombie");
        AddEvent("OnServerStatisticReceived");
        AddEvent("OnDynamicMovableRecipe");
        AddEvent("OnInitGlobalModData");
        AddEvent("OnReceiveGlobalModData");
        AddEvent("OnInitRecordedMedia");
        AddEvent("onUpdateIcon");
        AddEvent("preAddForageDefs");
        AddEvent("preAddSkillDefs");
        AddEvent("preAddZoneDefs");
        AddEvent("preAddCatDefs");
        AddEvent("preAddItemDefs");
        AddEvent("onAddForageDefs");
        AddEvent("onFillSearchIconContextMenu");
        AddEvent("onItemFall");
        AddEvent("OnTemplateTextInit");
        AddEvent("OnPlayerGetDamage");
        AddEvent("OnWeaponHitThumpable");
        AddEvent("OnThrowableExplode");
    }

    public static void clear() {
    }

    public static void register(Platform platform, KahluaTable environment) {
        KahluaTable table = platform.newTable();
        environment.rawset("Events", table);
        AddEvents();
    }

    public static void reroute(Prototype prototype, LuaClosure luaClosure) {
        for (int int0 = 0; int0 < EventList.size(); int0++) {
            Event event = EventList.get(int0);

            for (int int1 = 0; int1 < event.callbacks.size(); int1++) {
                LuaClosure _luaClosure = event.callbacks.get(int1);
                if (_luaClosure.prototype.filename.equals(prototype.filename) && _luaClosure.prototype.name.equals(prototype.name)) {
                    event.callbacks.set(int1, luaClosure);
                }
            }
        }
    }

    public static void Reset() {
        for (int int0 = 0; int0 < EventList.size(); int0++) {
            Event event = EventList.get(int0);
            event.callbacks.clear();
        }

        EventList.clear();
        EventMap.clear();
    }

    public static void ResetCallbacks() {
        for (int int0 = 0; int0 < EventList.size(); int0++) {
            Event event = EventList.get(int0);
            event.callbacks.clear();
        }
    }

    /**
     * Description copied from interface: se.krka.kahlua.vm.JavaFunction
     * @return N, number of return values. The top N objects on the stack are considered the return values.
     */
    @Override
    public int call(LuaCallFrame callFrame, int nArguments) {
        return 0;
    }

    private int OnTick(LuaCallFrame var1, int var2) {
        return 0;
    }
}
