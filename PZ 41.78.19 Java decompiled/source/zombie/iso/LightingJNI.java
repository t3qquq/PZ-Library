// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.util.ArrayList;
import java.util.Stack;
import zombie.GameTime;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.Moodles.MoodleType;
import zombie.core.PerformanceSettings;
import zombie.core.opengl.RenderSettings;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.textures.ColorInfo;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.weather.ClimateManager;
import zombie.meta.Meta;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleLight;
import zombie.vehicles.VehiclePart;

public final class LightingJNI {
    public static final int ROOM_SPAWN_DIST = 50;
    public static boolean init = false;
    public static final int[][] ForcedVis = new int[][]{
        {-1, 0, -1, -1, 0, -1, 1, -1, 1, 0, -2, -2, -1, -2, 0, -2, 1, -2, 2, -2},
        {-1, 1, -1, 0, -1, -1, 0, -1, 1, -1, -2, 0, -2, -1, -2, -2, -1, -2, 0, -2},
        {0, 1, -1, 1, -1, 0, -1, -1, 0, -1, -2, 2, -2, 1, -2, 0, -2, -1, -2, -2},
        {1, 1, 0, 1, -1, 1, -1, 0, -1, -1, 0, 2, -1, 2, -2, 2, -2, 1, -2, 0},
        {1, 0, 1, 1, 0, 1, -1, 1, -1, 0, 2, 2, 1, 2, 0, 2, -1, 2, -2, 2},
        {-1, 1, 0, 1, 1, 1, 1, 0, 1, -1, 2, 0, 2, 1, 2, 2, 1, 2, 0, 2},
        {0, 1, 1, 1, 1, 0, 1, -1, 0, -1, 2, -2, 2, -1, 2, 0, 2, 1, 2, 2},
        {-1, -1, 0, -1, 1, -1, 1, 0, 1, 1, 0, -2, 1, -2, 2, -2, 2, -1, 2, 0}
    };
    private static final ArrayList<IsoGameCharacter.TorchInfo> torches = new ArrayList<>();
    private static final ArrayList<IsoGameCharacter.TorchInfo> activeTorches = new ArrayList<>();
    private static final ArrayList<IsoLightSource> JNILights = new ArrayList<>();
    private static final int[] updateCounter = new int[4];
    private static boolean bWasElecShut = false;
    private static boolean bWasNight = false;
    private static final Vector2 tempVector2 = new Vector2();
    private static final int MAX_PLAYERS = 256;
    private static final int MAX_LIGHTS_PER_PLAYER = 4;
    private static final int MAX_LIGHTS_PER_VEHICLE = 10;
    private static final ArrayList<InventoryItem> tempItems = new ArrayList<>();

    public static void init() {
        if (!init) {
            String string = "";
            if ("1".equals(System.getProperty("zomboid.debuglibs.lighting"))) {
                DebugLog.log("***** Loading debug version of Lighting");
                string = "d";
            }

            try {
                if (System.getProperty("os.name").contains("OS X")) {
                    System.loadLibrary("Lighting");
                } else if (System.getProperty("os.name").startsWith("Win")) {
                    if (System.getProperty("sun.arch.data.model").equals("64")) {
                        System.loadLibrary("Lighting64" + string);
                    } else {
                        System.loadLibrary("Lighting32" + string);
                    }
                } else if (System.getProperty("sun.arch.data.model").equals("64")) {
                    System.loadLibrary("Lighting64");
                } else {
                    System.loadLibrary("Lighting32");
                }

                for (int int0 = 0; int0 < 4; int0++) {
                    updateCounter[int0] = -1;
                }

                configure(0.005F);
                init = true;
            } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
                unsatisfiedLinkError.printStackTrace();

                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException interruptedException) {
                }

                System.exit(1);
            }
        }
    }

    private static int getTorchIndexById(int int1) {
        for (int int0 = 0; int0 < torches.size(); int0++) {
            IsoGameCharacter.TorchInfo torchInfo = torches.get(int0);
            if (torchInfo.id == int1) {
                return int0;
            }
        }

        return -1;
    }

    private static void checkTorch(IsoPlayer player, InventoryItem item, int int1) {
        int int0 = getTorchIndexById(int1);
        IsoGameCharacter.TorchInfo torchInfo;
        if (int0 == -1) {
            torchInfo = IsoGameCharacter.TorchInfo.alloc();
            torches.add(torchInfo);
        } else {
            torchInfo = torches.get(int0);
        }

        torchInfo.set(player, item);
        if (torchInfo.id == 0) {
            torchInfo.id = int1;
        }

        updateTorch(
            torchInfo.id,
            torchInfo.x,
            torchInfo.y,
            torchInfo.z,
            torchInfo.angleX,
            torchInfo.angleY,
            torchInfo.dist,
            torchInfo.strength,
            torchInfo.bCone,
            torchInfo.dot,
            torchInfo.focusing
        );
        activeTorches.add(torchInfo);
    }

    private static int checkPlayerTorches(IsoPlayer player, int int2) {
        ArrayList arrayList = tempItems;
        arrayList.clear();
        player.getActiveLightItems(arrayList);
        int int0 = Math.min(arrayList.size(), 4);

        for (int int1 = 0; int1 < int0; int1++) {
            checkTorch(player, (InventoryItem)arrayList.get(int1), int2 * 4 + int1 + 1);
        }

        return int0;
    }

    private static void clearPlayerTorches(int int3, int int1) {
        for (int int0 = int1; int0 < 4; int0++) {
            int int2 = int3 * 4 + int0 + 1;
            int int4 = getTorchIndexById(int2);
            if (int4 != -1) {
                IsoGameCharacter.TorchInfo torchInfo = torches.get(int4);
                removeTorch(torchInfo.id);
                torchInfo.id = 0;
                IsoGameCharacter.TorchInfo.release(torchInfo);
                torches.remove(int4);
                break;
            }
        }
    }

    private static void checkTorch(VehiclePart part, int int1) {
        VehicleLight vehicleLight = part.getLight();
        if (vehicleLight != null && vehicleLight.getActive()) {
            IsoGameCharacter.TorchInfo torchInfo0 = null;

            for (int int0 = 0; int0 < torches.size(); int0++) {
                torchInfo0 = torches.get(int0);
                if (torchInfo0.id == int1) {
                    break;
                }

                torchInfo0 = null;
            }

            if (torchInfo0 == null) {
                torchInfo0 = IsoGameCharacter.TorchInfo.alloc();
                torches.add(torchInfo0);
            }

            torchInfo0.set(part);
            if (torchInfo0.id == 0) {
                torchInfo0.id = int1;
            }

            updateTorch(
                torchInfo0.id,
                torchInfo0.x,
                torchInfo0.y,
                torchInfo0.z,
                torchInfo0.angleX,
                torchInfo0.angleY,
                torchInfo0.dist,
                torchInfo0.strength,
                torchInfo0.bCone,
                torchInfo0.dot,
                torchInfo0.focusing
            );
            activeTorches.add(torchInfo0);
        } else {
            for (int int2 = 0; int2 < torches.size(); int2++) {
                IsoGameCharacter.TorchInfo torchInfo1 = torches.get(int2);
                if (torchInfo1.id == int1) {
                    removeTorch(torchInfo1.id);
                    torchInfo1.id = 0;
                    IsoGameCharacter.TorchInfo.release(torchInfo1);
                    torches.remove(int2--);
                }
            }
        }
    }

    private static void checkLights() {
        if (IsoWorld.instance.CurrentCell != null) {
            if (GameClient.bClient) {
                IsoGenerator.updateSurroundingNow();
            }

            boolean boolean0 = IsoWorld.instance.isHydroPowerOn();
            Stack stack = IsoWorld.instance.CurrentCell.getLamppostPositions();

            for (int int0 = 0; int0 < stack.size(); int0++) {
                IsoLightSource lightSource0 = (IsoLightSource)stack.get(int0);
                IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunkForGridSquare(lightSource0.x, lightSource0.y, lightSource0.z);
                if (chunk != null && lightSource0.chunk != null && lightSource0.chunk != chunk) {
                    lightSource0.life = 0;
                }

                if (lightSource0.life != 0 && lightSource0.isInBounds()) {
                    if (lightSource0.bHydroPowered) {
                        if (lightSource0.switches.isEmpty()) {
                            assert false;

                            boolean boolean1 = boolean0;
                            if (!boolean0) {
                                IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(lightSource0.x, lightSource0.y, lightSource0.z);
                                boolean1 = square != null && square.haveElectricity();
                            }

                            if (lightSource0.bActive != boolean1) {
                                lightSource0.bActive = boolean1;
                                GameTime.instance.lightSourceUpdate = 100.0F;
                            }
                        } else {
                            IsoLightSwitch lightSwitch0 = lightSource0.switches.get(0);
                            boolean boolean2 = lightSwitch0.canSwitchLight();
                            if (lightSwitch0.bStreetLight && GameTime.getInstance().getNight() < 0.5F) {
                                boolean2 = false;
                            }

                            if (lightSource0.bActive && !boolean2) {
                                lightSource0.bActive = false;
                                GameTime.instance.lightSourceUpdate = 100.0F;
                            } else if (!lightSource0.bActive && boolean2 && lightSwitch0.isActivated()) {
                                lightSource0.bActive = true;
                                GameTime.instance.lightSourceUpdate = 100.0F;
                            }
                        }
                    }

                    if (lightSource0.ID == 0) {
                        lightSource0.ID = IsoLightSource.NextID++;
                        if (lightSource0.life != -1) {
                            addTempLight(
                                lightSource0.ID,
                                lightSource0.x,
                                lightSource0.y,
                                lightSource0.z,
                                lightSource0.radius,
                                lightSource0.r,
                                lightSource0.g,
                                lightSource0.b,
                                (int)(lightSource0.life * PerformanceSettings.getLockFPS() / 30.0F)
                            );
                            stack.remove(int0--);
                        } else {
                            lightSource0.rJNI = lightSource0.r;
                            lightSource0.gJNI = lightSource0.g;
                            lightSource0.bJNI = lightSource0.b;
                            lightSource0.bActiveJNI = lightSource0.bActive;
                            JNILights.add(lightSource0);
                            addLight(
                                lightSource0.ID,
                                lightSource0.x,
                                lightSource0.y,
                                lightSource0.z,
                                lightSource0.radius,
                                lightSource0.r,
                                lightSource0.g,
                                lightSource0.b,
                                lightSource0.localToBuilding == null ? -1 : lightSource0.localToBuilding.ID,
                                lightSource0.bActive
                            );
                        }
                    } else {
                        if (lightSource0.r != lightSource0.rJNI || lightSource0.g != lightSource0.gJNI || lightSource0.b != lightSource0.bJNI) {
                            lightSource0.rJNI = lightSource0.r;
                            lightSource0.gJNI = lightSource0.g;
                            lightSource0.bJNI = lightSource0.b;
                            setLightColor(lightSource0.ID, lightSource0.r, lightSource0.g, lightSource0.b);
                        }

                        if (lightSource0.bActiveJNI != lightSource0.bActive) {
                            lightSource0.bActiveJNI = lightSource0.bActive;
                            setLightActive(lightSource0.ID, lightSource0.bActive);
                        }
                    }
                } else {
                    stack.remove(int0);
                    if (lightSource0.ID != 0) {
                        int int1 = lightSource0.ID;
                        lightSource0.ID = 0;
                        JNILights.remove(lightSource0);
                        removeLight(int1);
                        GameTime.instance.lightSourceUpdate = 100.0F;
                    }

                    int0--;
                }
            }

            for (int int2 = 0; int2 < JNILights.size(); int2++) {
                IsoLightSource lightSource1 = JNILights.get(int2);
                if (!stack.contains(lightSource1)) {
                    int int3 = lightSource1.ID;
                    lightSource1.ID = 0;
                    JNILights.remove(int2--);
                    removeLight(int3);
                }
            }

            ArrayList arrayList0 = IsoWorld.instance.CurrentCell.roomLights;

            for (int int4 = 0; int4 < arrayList0.size(); int4++) {
                IsoRoomLight roomLight = (IsoRoomLight)arrayList0.get(int4);
                if (!roomLight.isInBounds()) {
                    arrayList0.remove(int4--);
                    if (roomLight.ID != 0) {
                        int int5 = roomLight.ID;
                        roomLight.ID = 0;
                        removeRoomLight(int5);
                        GameTime.instance.lightSourceUpdate = 100.0F;
                    }
                } else {
                    roomLight.bActive = roomLight.room.def.bLightsActive;
                    if (!boolean0) {
                        boolean boolean3 = false;

                        for (int int6 = 0; !boolean3 && int6 < roomLight.room.lightSwitches.size(); int6++) {
                            IsoLightSwitch lightSwitch1 = roomLight.room.lightSwitches.get(int6);
                            if (lightSwitch1.square != null && lightSwitch1.square.haveElectricity()) {
                                boolean3 = true;
                            }
                        }

                        if (!boolean3 && roomLight.bActive) {
                            roomLight.bActive = false;
                            if (roomLight.bActiveJNI) {
                                IsoGridSquare.RecalcLightTime = -1;
                                GameTime.instance.lightSourceUpdate = 100.0F;
                            }
                        } else if (boolean3 && roomLight.bActive && !roomLight.bActiveJNI) {
                            IsoGridSquare.RecalcLightTime = -1;
                            GameTime.instance.lightSourceUpdate = 100.0F;
                        }
                    }

                    if (roomLight.ID == 0) {
                        roomLight.ID = 100000 + IsoRoomLight.NextID++;
                        addRoomLight(
                            roomLight.ID,
                            roomLight.room.building.ID,
                            roomLight.room.def.ID,
                            roomLight.x,
                            roomLight.y,
                            roomLight.z,
                            roomLight.width,
                            roomLight.height,
                            roomLight.bActive
                        );
                        roomLight.bActiveJNI = roomLight.bActive;
                        GameTime.instance.lightSourceUpdate = 100.0F;
                    } else if (roomLight.bActiveJNI != roomLight.bActive) {
                        setRoomLightActive(roomLight.ID, roomLight.bActive);
                        roomLight.bActiveJNI = roomLight.bActive;
                        GameTime.instance.lightSourceUpdate = 100.0F;
                    }
                }
            }

            activeTorches.clear();
            if (GameClient.bClient) {
                ArrayList arrayList1 = GameClient.instance.getPlayers();

                for (int int7 = 0; int7 < arrayList1.size(); int7++) {
                    IsoPlayer player0 = (IsoPlayer)arrayList1.get(int7);
                    checkPlayerTorches(player0, player0.OnlineID + 1);
                }
            } else {
                for (int int8 = 0; int8 < IsoPlayer.numPlayers; int8++) {
                    IsoPlayer player1 = IsoPlayer.players[int8];
                    if (player1 != null && !player1.isDead() && player1.getVehicle() == null) {
                        int int9 = checkPlayerTorches(player1, int8);
                        clearPlayerTorches(int8, int9);
                    } else {
                        clearPlayerTorches(int8, 0);
                    }
                }
            }

            for (int int10 = 0; int10 < IsoWorld.instance.CurrentCell.getVehicles().size(); int10++) {
                BaseVehicle vehicle = IsoWorld.instance.CurrentCell.getVehicles().get(int10);
                if (vehicle.VehicleID != -1) {
                    for (int int11 = 0; int11 < vehicle.getLightCount(); int11++) {
                        VehiclePart part = vehicle.getLightByIndex(int11);
                        checkTorch(part, 1024 + vehicle.VehicleID * 10 + int11);
                    }
                }
            }

            for (int int12 = 0; int12 < torches.size(); int12++) {
                IsoGameCharacter.TorchInfo torchInfo = torches.get(int12);
                if (!activeTorches.contains(torchInfo)) {
                    removeTorch(torchInfo.id);
                    torchInfo.id = 0;
                    IsoGameCharacter.TorchInfo.release(torchInfo);
                    torches.remove(int12--);
                }
            }
        }
    }

    public static float calculateVisionCone(IsoGameCharacter character) {
        float float0;
        if (character.getVehicle() == null) {
            float0 = -0.2F;
            float0 -= character.getStats().fatigue - 0.6F;
            if (float0 > -0.2F) {
                float0 = -0.2F;
            }

            if (character.getStats().fatigue >= 1.0F) {
                float0 -= 0.2F;
            }

            if (character.getMoodles().getMoodleLevel(MoodleType.Panic) == 4) {
                float0 -= 0.2F;
            }

            if (character.isInARoom()) {
                float0 -= 0.2F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
            } else {
                float0 -= 0.7F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
            }

            if (float0 < -0.9F) {
                float0 = -0.9F;
            }

            if (character.Traits.EagleEyed.isSet()) {
                float0 += 0.2F * ClimateManager.getInstance().getDayLightStrength();
            }

            if (character.Traits.NightVision.isSet()) {
                float0 += 0.2F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
            }

            if (float0 > 0.0F) {
                float0 = 0.0F;
            }
        } else {
            if (character.getVehicle().getHeadlightsOn() && character.getVehicle().getHeadlightCanEmmitLight()) {
                float0 = 0.8F - 3.0F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
                if (float0 < -0.8F) {
                    float0 = -0.8F;
                }
            } else {
                float0 = 0.8F - 3.0F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
                if (float0 < -0.95F) {
                    float0 = -0.95F;
                }
            }

            if (character.Traits.NightVision.isSet()) {
                float0 += 0.2F * (1.0F - ClimateManager.getInstance().getDayLightStrength());
            }

            if (float0 > 1.0F) {
                float0 = 1.0F;
            }
        }

        return float0;
    }

    public static void updatePlayer(int int0) {
        IsoPlayer player = IsoPlayer.players[int0];
        if (player != null) {
            float float0 = player.getStats().fatigue - 0.6F;
            if (float0 < 0.0F) {
                float0 = 0.0F;
            }

            float0 *= 2.5F;
            if (player.Traits.HardOfHearing.isSet() && float0 < 0.7F) {
                float0 = 0.7F;
            }

            float float1 = 2.0F;
            if (player.Traits.KeenHearing.isSet()) {
                float1 += 3.0F;
            }

            float float2 = calculateVisionCone(player);
            Vector2 vector = player.getLookVector(tempVector2);
            BaseVehicle vehicle = player.getVehicle();
            if (vehicle != null
                && !player.isAiming()
                && !player.isLookingWhileInVehicle()
                && vehicle.isDriver(player)
                && vehicle.getCurrentSpeedKmHour() < -1.0F) {
                vector.rotate((float) Math.PI);
            }

            playerSet(
                player.x,
                player.y,
                player.z,
                vector.x,
                vector.y,
                false,
                player.ReanimatedCorpse != null,
                player.isGhostMode(),
                player.Traits.ShortSighted.isSet(),
                float0,
                float1,
                float2
            );
        }
    }

    public static void updateChunk(IsoChunk chunk) {
        chunkBeginUpdate(chunk.wx, chunk.wy);

        for (int int0 = 0; int0 < IsoCell.MaxHeight; int0++) {
            for (int int1 = 0; int1 < 10; int1++) {
                for (int int2 = 0; int2 < 10; int2++) {
                    IsoGridSquare square = chunk.getGridSquare(int2, int1, int0);
                    if (square == null) {
                        squareSetNull(int2, int1, int0);
                    } else {
                        squareBeginUpdate(int2, int1, int0);
                        int int3 = square.visionMatrix;
                        boolean boolean0 = square.Has(IsoObjectType.stairsTN)
                            || square.Has(IsoObjectType.stairsMN)
                            || square.Has(IsoObjectType.stairsTW)
                            || square.Has(IsoObjectType.stairsMW);
                        squareSet(
                            square.w != null,
                            square.n != null,
                            square.e != null,
                            square.s != null,
                            boolean0,
                            int3,
                            square.getRoom() != null ? square.getBuilding().ID : -1,
                            square.getRoomID()
                        );

                        for (int int4 = 0; int4 < square.getSpecialObjects().size(); int4++) {
                            IsoObject object = square.getSpecialObjects().get(int4);
                            if (object instanceof IsoCurtain curtain) {
                                byte byte0 = 0;
                                if (curtain.getType() == IsoObjectType.curtainW) {
                                    byte0 |= 4;
                                } else if (curtain.getType() == IsoObjectType.curtainN) {
                                    byte0 |= 8;
                                } else if (curtain.getType() == IsoObjectType.curtainE) {
                                    byte0 |= 16;
                                } else if (curtain.getType() == IsoObjectType.curtainS) {
                                    byte0 |= 32;
                                }

                                squareAddCurtain(byte0, curtain.open);
                            } else if (object instanceof IsoDoor door) {
                                boolean boolean1 = door.sprite != null && door.sprite.getProperties().Is("doorTrans");
                                if (door.open) {
                                    boolean1 = true;
                                } else {
                                    boolean1 = boolean1 && (door.HasCurtains() == null || door.isCurtainOpen());
                                }

                                IsoBarricade barricade0 = door.getBarricadeOnSameSquare();
                                IsoBarricade barricade1 = door.getBarricadeOnOppositeSquare();
                                if (barricade0 != null && barricade0.isBlockVision()) {
                                    boolean1 = false;
                                }

                                if (barricade1 != null && barricade1.isBlockVision()) {
                                    boolean1 = false;
                                }

                                if (door.IsOpen() && IsoDoor.getGarageDoorIndex(door) != -1) {
                                    boolean1 = true;
                                }

                                squareAddDoor(door.north, door.open, boolean1);
                            } else if (object instanceof IsoThumpable thumpable0) {
                                boolean boolean2 = thumpable0.getSprite().getProperties().Is("doorTrans");
                                if (thumpable0.isDoor && thumpable0.open) {
                                    boolean2 = true;
                                }

                                squareAddThumpable(thumpable0.north, thumpable0.open, thumpable0.isDoor, boolean2);
                                IsoThumpable thumpable1 = (IsoThumpable)object;
                                boolean boolean3 = false;
                                IsoBarricade barricade2 = thumpable1.getBarricadeOnSameSquare();
                                IsoBarricade barricade3 = thumpable1.getBarricadeOnOppositeSquare();
                                if (barricade2 != null) {
                                    boolean3 |= barricade2.isBlockVision();
                                }

                                if (barricade3 != null) {
                                    boolean3 |= barricade3.isBlockVision();
                                }

                                squareAddWindow(thumpable1.north, thumpable1.open, boolean3);
                            } else if (object instanceof IsoWindow window) {
                                boolean boolean4 = false;
                                IsoBarricade barricade4 = window.getBarricadeOnSameSquare();
                                IsoBarricade barricade5 = window.getBarricadeOnOppositeSquare();
                                if (barricade4 != null) {
                                    boolean4 |= barricade4.isBlockVision();
                                }

                                if (barricade5 != null) {
                                    boolean4 |= barricade5.isBlockVision();
                                }

                                squareAddWindow(window.north, window.open, boolean4);
                            }
                        }

                        squareEndUpdate();
                    }
                }
            }
        }

        chunkEndUpdate();
    }

    public static void update() {
        if (IsoWorld.instance != null && IsoWorld.instance.CurrentCell != null) {
            checkLights();
            GameTime gameTime = GameTime.getInstance();
            RenderSettings renderSettings = RenderSettings.getInstance();
            boolean boolean0 = IsoWorld.instance.isHydroPowerOn();
            boolean boolean1 = GameTime.getInstance().getNight() < 0.5F;
            if (boolean0 != bWasElecShut || boolean1 != bWasNight) {
                bWasElecShut = boolean0;
                bWasNight = boolean1;
                IsoGridSquare.RecalcLightTime = -1;
                gameTime.lightSourceUpdate = 100.0F;
            }

            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[int0];
                if (chunkMap != null && !chunkMap.ignore) {
                    RenderSettings.PlayerRenderSettings playerRenderSettings = renderSettings.getPlayerSettings(int0);
                    stateBeginUpdate(int0, chunkMap.getWorldXMin(), chunkMap.getWorldYMin(), IsoChunkMap.ChunkGridWidth, IsoChunkMap.ChunkGridWidth);
                    updatePlayer(int0);
                    stateEndFrame(
                        playerRenderSettings.getRmod(),
                        playerRenderSettings.getGmod(),
                        playerRenderSettings.getBmod(),
                        playerRenderSettings.getAmbient(),
                        playerRenderSettings.getNight(),
                        playerRenderSettings.getViewDistance(),
                        gameTime.getViewDistMax(),
                        LosUtil.cachecleared[int0],
                        gameTime.lightSourceUpdate
                    );
                    if (LosUtil.cachecleared[int0]) {
                        LosUtil.cachecleared[int0] = false;
                        IsoWorld.instance.CurrentCell.invalidatePeekedRoom(int0);
                    }

                    for (int int1 = 0; int1 < IsoChunkMap.ChunkGridWidth; int1++) {
                        for (int int2 = 0; int2 < IsoChunkMap.ChunkGridWidth; int2++) {
                            IsoChunk chunk = chunkMap.getChunk(int2, int1);
                            if (chunk != null && chunk.lightCheck[int0]) {
                                updateChunk(chunk);
                                chunk.lightCheck[int0] = false;
                            }

                            if (chunk != null) {
                                chunk.bLightingNeverDone[int0] = !chunkLightingDone(chunk.wx, chunk.wy);
                            }
                        }
                    }

                    stateEndUpdate();
                    updateCounter[int0] = stateUpdateCounter(int0);
                    if (gameTime.lightSourceUpdate > 0.0F && IsoPlayer.players[int0] != null) {
                        IsoPlayer.players[int0].dirtyRecalcGridStackTime = 20.0F;
                    }
                }
            }

            DeadBodyAtlas.instance.lightingUpdate(updateCounter[0], gameTime.lightSourceUpdate > 0.0F);
            gameTime.lightSourceUpdate = 0.0F;
        }
    }

    public static void getTorches(ArrayList<IsoGameCharacter.TorchInfo> arrayList) {
        arrayList.addAll(torches);
    }

    public static void stop() {
        torches.clear();
        JNILights.clear();
        destroy();

        for (int int0 = 0; int0 < updateCounter.length; int0++) {
            updateCounter[int0] = -1;
        }

        bWasElecShut = false;
        bWasNight = false;
        IsoLightSource.NextID = 1;
        IsoRoomLight.NextID = 1;
    }

    public static native void configure(float var0);

    public static native void scrollLeft(int var0);

    public static native void scrollRight(int var0);

    public static native void scrollUp(int var0);

    public static native void scrollDown(int var0);

    public static native void stateBeginUpdate(int var0, int var1, int var2, int var3, int var4);

    public static native void stateEndFrame(float var0, float var1, float var2, float var3, float var4, float var5, float var6, boolean var7, float var8);

    public static native void stateEndUpdate();

    public static native int stateUpdateCounter(int var0);

    public static native void teleport(int var0, int var1, int var2);

    public static native void DoLightingUpdateNew(long var0);

    public static native boolean WaitingForMain();

    public static native void playerSet(
        float var0,
        float var1,
        float var2,
        float var3,
        float var4,
        boolean var5,
        boolean var6,
        boolean var7,
        boolean var8,
        float var9,
        float var10,
        float var11
    );

    public static native boolean chunkLightingDone(int var0, int var1);

    public static native void chunkBeginUpdate(int var0, int var1);

    public static native void chunkEndUpdate();

    public static native void squareSetNull(int var0, int var1, int var2);

    public static native void squareBeginUpdate(int var0, int var1, int var2);

    public static native void squareSet(boolean var0, boolean var1, boolean var2, boolean var3, boolean var4, int var5, int var6, int var7);

    public static native void squareAddCurtain(int var0, boolean var1);

    public static native void squareAddDoor(boolean var0, boolean var1, boolean var2);

    public static native void squareAddThumpable(boolean var0, boolean var1, boolean var2, boolean var3);

    public static native void squareAddWindow(boolean var0, boolean var1, boolean var2);

    public static native void squareEndUpdate();

    public static native int getVertLight(int var0, int var1, int var2, int var3, int var4);

    public static native float getLightInfo(int var0, int var1, int var2, int var3, int var4);

    public static native float getDarkMulti(int var0, int var1, int var2, int var3);

    public static native float getTargetDarkMulti(int var0, int var1, int var2, int var3);

    public static native boolean getSeen(int var0, int var1, int var2, int var3);

    public static native boolean getCanSee(int var0, int var1, int var2, int var3);

    public static native boolean getCouldSee(int var0, int var1, int var2, int var3);

    public static native boolean getSquareLighting(int var0, int var1, int var2, int var3, int[] var4);

    public static native void addLight(int var0, int var1, int var2, int var3, int var4, float var5, float var6, float var7, int var8, boolean var9);

    public static native void addTempLight(int var0, int var1, int var2, int var3, int var4, float var5, float var6, float var7, int var8);

    public static native void removeLight(int var0);

    public static native void setLightActive(int var0, boolean var1);

    public static native void setLightColor(int var0, float var1, float var2, float var3);

    public static native void addRoomLight(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8);

    public static native void removeRoomLight(int var0);

    public static native void setRoomLightActive(int var0, boolean var1);

    public static native void updateTorch(
        int var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, boolean var8, float var9, int var10
    );

    public static native void removeTorch(int var0);

    public static native void destroy();

    public static final class JNILighting implements IsoGridSquare.ILighting {
        private static final int RESULT_LIGHTS_PER_SQUARE = 5;
        private static final int[] lightInts = new int[43];
        private static final byte VIS_SEEN = 1;
        private static final byte VIS_CAN_SEE = 2;
        private static final byte VIS_COULD_SEE = 4;
        private int playerIndex;
        private final IsoGridSquare square;
        private ColorInfo lightInfo = new ColorInfo();
        private byte vis;
        private float cacheDarkMulti;
        private float cacheTargetDarkMulti;
        private int[] cacheVertLight;
        private int updateTick = -1;
        private int lightsCount;
        private IsoGridSquare.ResultLight[] lights;

        public JNILighting(int int0, IsoGridSquare squarex) {
            this.playerIndex = int0;
            this.square = squarex;
            this.cacheDarkMulti = 0.0F;
            this.cacheTargetDarkMulti = 0.0F;
            this.cacheVertLight = new int[8];

            for (int int1 = 0; int1 < 8; int1++) {
                this.cacheVertLight[int1] = 0;
            }
        }

        @Override
        public int lightverts(int int0) {
            return this.cacheVertLight[int0];
        }

        @Override
        public float lampostTotalR() {
            return 0.0F;
        }

        @Override
        public float lampostTotalG() {
            return 0.0F;
        }

        @Override
        public float lampostTotalB() {
            return 0.0F;
        }

        @Override
        public boolean bSeen() {
            this.update();
            return (this.vis & 1) != 0;
        }

        @Override
        public boolean bCanSee() {
            this.update();
            return (this.vis & 2) != 0;
        }

        @Override
        public boolean bCouldSee() {
            this.update();
            return (this.vis & 4) != 0;
        }

        @Override
        public float darkMulti() {
            return this.cacheDarkMulti;
        }

        @Override
        public float targetDarkMulti() {
            return this.cacheTargetDarkMulti;
        }

        @Override
        public ColorInfo lightInfo() {
            this.update();
            return this.lightInfo;
        }

        @Override
        public void lightverts(int var1, int var2) {
            throw new IllegalStateException();
        }

        @Override
        public void lampostTotalR(float var1) {
            throw new IllegalStateException();
        }

        @Override
        public void lampostTotalG(float var1) {
            throw new IllegalStateException();
        }

        @Override
        public void lampostTotalB(float var1) {
            throw new IllegalStateException();
        }

        @Override
        public void bSeen(boolean var1) {
            throw new IllegalStateException();
        }

        @Override
        public void bCanSee(boolean var1) {
            throw new IllegalStateException();
        }

        @Override
        public void bCouldSee(boolean var1) {
            throw new IllegalStateException();
        }

        @Override
        public void darkMulti(float var1) {
            throw new IllegalStateException();
        }

        @Override
        public void targetDarkMulti(float var1) {
            throw new IllegalStateException();
        }

        @Override
        public int resultLightCount() {
            return this.lightsCount;
        }

        @Override
        public IsoGridSquare.ResultLight getResultLight(int int0) {
            return this.lights[int0];
        }

        @Override
        public void reset() {
            this.updateTick = -1;
        }

        private void update() {
            if (LightingJNI.updateCounter[this.playerIndex] != -1) {
                if (this.updateTick != LightingJNI.updateCounter[this.playerIndex]
                    && LightingJNI.getSquareLighting(this.playerIndex, this.square.x, this.square.y, this.square.z, lightInts)) {
                    IsoPlayer player = IsoPlayer.players[this.playerIndex];
                    boolean boolean0 = (this.vis & 1) != 0;
                    int int0 = 0;
                    this.vis = (byte)(lightInts[int0++] & 7);
                    this.lightInfo.r = (lightInts[int0] & 0xFF) / 255.0F;
                    this.lightInfo.g = (lightInts[int0] >> 8 & 0xFF) / 255.0F;
                    this.lightInfo.b = (lightInts[int0++] >> 16 & 0xFF) / 255.0F;
                    this.cacheDarkMulti = lightInts[int0++] / 100000.0F;
                    this.cacheTargetDarkMulti = lightInts[int0++] / 100000.0F;
                    float float0 = 1.0F;
                    float float1 = 1.0F;
                    if (player != null) {
                        int int1 = this.square.z - (int)player.z;
                        if (int1 == -1) {
                            float0 = 1.0F;
                            float1 = 0.85F;
                        } else if (int1 < -1) {
                            float0 = 0.85F;
                            float1 = 0.85F;
                        }

                        if ((this.vis & 2) == 0 && (this.vis & 4) != 0) {
                            int int2 = (int)player.x;
                            int int3 = (int)player.y;
                            int int4 = this.square.x - int2;
                            int int5 = this.square.y - int3;
                            if (player.dir != IsoDirections.Max && Math.abs(int4) <= 2 && Math.abs(int5) <= 2) {
                                int[] ints = LightingJNI.ForcedVis[player.dir.index()];

                                for (byte byte0 = 0; byte0 < ints.length; byte0 += 2) {
                                    if (int4 == ints[byte0] && int5 == ints[byte0 + 1]) {
                                        this.vis = (byte)(this.vis | 2);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    for (int int6 = 0; int6 < 4; int6++) {
                        int int7 = lightInts[int0++];
                        float float2 = (int7 & 0xFF) * float1;
                        float float3 = ((int7 & 0xFF00) >> 8) * float1;
                        float float4 = ((int7 & 0xFF0000) >> 16) * float1;
                        this.cacheVertLight[int6] = (int)float2 << 0 | (int)float3 << 8 | (int)float4 << 16 | 0xFF000000;
                    }

                    for (int int8 = 4; int8 < 8; int8++) {
                        int int9 = lightInts[int0++];
                        float float5 = (int9 & 0xFF) * float0;
                        float float6 = ((int9 & 0xFF00) >> 8) * float0;
                        float float7 = ((int9 & 0xFF0000) >> 16) * float0;
                        this.cacheVertLight[int8] = (int)float5 << 0 | (int)float6 << 8 | (int)float7 << 16 | 0xFF000000;
                    }

                    this.lightsCount = lightInts[int0++];

                    for (int int10 = 0; int10 < this.lightsCount; int10++) {
                        if (this.lights == null) {
                            this.lights = new IsoGridSquare.ResultLight[5];
                        }

                        if (this.lights[int10] == null) {
                            this.lights[int10] = new IsoGridSquare.ResultLight();
                        }

                        this.lights[int10].id = lightInts[int0++];
                        this.lights[int10].x = lightInts[int0++];
                        this.lights[int10].y = lightInts[int0++];
                        this.lights[int10].z = lightInts[int0++];
                        this.lights[int10].radius = lightInts[int0++];
                        int int11 = lightInts[int0++];
                        this.lights[int10].r = (int11 & 0xFF) / 255.0F;
                        this.lights[int10].g = (int11 >> 8 & 0xFF) / 255.0F;
                        this.lights[int10].b = (int11 >> 16 & 0xFF) / 255.0F;
                        this.lights[int10].flags = int11 >> 24 & 0xFF;
                    }

                    this.updateTick = LightingJNI.updateCounter[this.playerIndex];
                    if ((this.vis & 1) != 0) {
                        if (boolean0 && this.square.getRoom() != null && this.square.getRoom().def != null && !this.square.getRoom().def.bExplored) {
                            boolean boolean1 = true;
                        }

                        this.square.checkRoomSeen(this.playerIndex);
                        if (!boolean0) {
                            assert !GameServer.bServer;

                            if (!GameClient.bClient) {
                                Meta.instance.dealWithSquareSeen(this.square);
                            }
                        }
                    }
                }
            }
        }
    }
}
