// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.properties.PropertyContainer;
import zombie.debug.DebugLog;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoBarbecue;
import zombie.iso.objects.IsoClothingDryer;
import zombie.iso.objects.IsoClothingWasher;
import zombie.iso.objects.IsoCombinationWasherDryer;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoFireplace;
import zombie.iso.objects.IsoJukebox;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoMannequin;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoStove;
import zombie.iso.objects.IsoTelevision;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWheelieBin;
import zombie.iso.objects.IsoWindow;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;

public final class CellLoader {
    public static final ArrayDeque<IsoObject> isoObjectCache = new ArrayDeque<>();
    public static final ArrayDeque<IsoTree> isoTreeCache = new ArrayDeque<>();
    static int wanderX = 0;
    static int wanderY = 0;
    static IsoRoom wanderRoom = null;
    static final HashSet<String> missingTiles = new HashSet<>();
    public static final HashMap<IsoSprite, IsoSprite> smashedWindowSpriteMap = new HashMap<>();

    public static void DoTileObjectCreation(
        IsoSprite sprite, IsoObjectType objectType, IsoGridSquare square, IsoCell cell, int int3, int int4, int int5, String string1
    ) throws NumberFormatException {
        IsoObject object0 = null;
        if (square != null) {
            boolean boolean0 = false;
            if (smashedWindowSpriteMap.containsKey(sprite)) {
                sprite = smashedWindowSpriteMap.get(sprite);
                objectType = sprite.getType();
                boolean0 = true;
            }

            PropertyContainer propertyContainer = sprite.getProperties();
            if (sprite.solidfloor && propertyContainer.Is(IsoFlagType.diamondFloor) && !propertyContainer.Is(IsoFlagType.transparentFloor)) {
                IsoObject object1 = square.getFloor();
                if (object1 != null && object1.getProperties().Is(IsoFlagType.diamondFloor)) {
                    object1.clearAttachedAnimSprite();
                    object1.setSprite(sprite);
                    return;
                }
            }

            if (objectType == IsoObjectType.doorW || objectType == IsoObjectType.doorN) {
                IsoDoor door = new IsoDoor(cell, square, sprite, objectType == IsoObjectType.doorN);
                object0 = door;
                AddSpecialObject(square, door);
                if (sprite.getProperties().Is("DoubleDoor")) {
                    door.Locked = false;
                    door.lockedByKey = false;
                }

                if (sprite.getProperties().Is("GarageDoor")) {
                    door.Locked = !door.IsOpen();
                    door.lockedByKey = false;
                }

                GameClient.instance.objectSyncReq.putRequest(square, door);
            } else if (objectType == IsoObjectType.lightswitch) {
                object0 = new IsoLightSwitch(cell, square, sprite, square.getRoomID());
                AddObject(square, object0);
                GameClient.instance.objectSyncReq.putRequest(square, object0);
                if (object0.sprite.getProperties().Is("lightR")) {
                    float float0 = Float.parseFloat(object0.sprite.getProperties().Val("lightR")) / 255.0F;
                    float float1 = Float.parseFloat(object0.sprite.getProperties().Val("lightG")) / 255.0F;
                    float float2 = Float.parseFloat(object0.sprite.getProperties().Val("lightB")) / 255.0F;
                    int int0 = 10;
                    if (object0.sprite.getProperties().Is("LightRadius") && Integer.parseInt(object0.sprite.getProperties().Val("LightRadius")) > 0) {
                        int0 = Integer.parseInt(object0.sprite.getProperties().Val("LightRadius"));
                    }

                    IsoLightSource lightSource = new IsoLightSource(
                        object0.square.getX(), object0.square.getY(), object0.square.getZ(), float0, float1, float2, int0
                    );
                    lightSource.bActive = true;
                    lightSource.bHydroPowered = true;
                    lightSource.switches.add((IsoLightSwitch)object0);
                    ((IsoLightSwitch)object0).lights.add(lightSource);
                } else {
                    ((IsoLightSwitch)object0).lightRoom = true;
                }
            } else if (objectType != IsoObjectType.curtainN
                && objectType != IsoObjectType.curtainS
                && objectType != IsoObjectType.curtainE
                && objectType != IsoObjectType.curtainW) {
                if (sprite.getProperties().Is(IsoFlagType.windowW) || sprite.getProperties().Is(IsoFlagType.windowN)) {
                    object0 = new IsoWindow(cell, square, sprite, sprite.getProperties().Is(IsoFlagType.windowN));
                    if (boolean0) {
                        ((IsoWindow)object0).setSmashed(true);
                    }

                    AddSpecialObject(square, object0);
                    GameClient.instance.objectSyncReq.putRequest(square, object0);
                } else if (sprite.getProperties().Is(IsoFlagType.container) && sprite.getProperties().Val("container").equals("barbecue")) {
                    object0 = new IsoBarbecue(cell, square, sprite);
                    AddObject(square, object0);
                } else if (sprite.getProperties().Is(IsoFlagType.container) && sprite.getProperties().Val("container").equals("fireplace")) {
                    object0 = new IsoFireplace(cell, square, sprite);
                    AddObject(square, object0);
                } else if ("IsoCombinationWasherDryer".equals(sprite.getProperties().Val("IsoType"))) {
                    object0 = new IsoCombinationWasherDryer(cell, square, sprite);
                    AddObject(square, object0);
                } else if (sprite.getProperties().Is(IsoFlagType.container) && sprite.getProperties().Val("container").equals("clothingdryer")) {
                    object0 = new IsoClothingDryer(cell, square, sprite);
                    AddObject(square, object0);
                } else if (sprite.getProperties().Is(IsoFlagType.container) && sprite.getProperties().Val("container").equals("clothingwasher")) {
                    object0 = new IsoClothingWasher(cell, square, sprite);
                    AddObject(square, object0);
                } else if (sprite.getProperties().Is(IsoFlagType.container) && sprite.getProperties().Val("container").equals("woodstove")) {
                    object0 = new IsoFireplace(cell, square, sprite);
                    AddObject(square, object0);
                } else if (!sprite.getProperties().Is(IsoFlagType.container)
                    || !sprite.getProperties().Val("container").equals("stove") && !sprite.getProperties().Val("container").equals("microwave")) {
                    if (objectType == IsoObjectType.jukebox) {
                        object0 = new IsoJukebox(cell, square, sprite);
                        object0.OutlineOnMouseover = true;
                        AddObject(square, object0);
                    } else if (objectType == IsoObjectType.radio) {
                        object0 = new IsoRadio(cell, square, sprite);
                        AddObject(square, object0);
                    } else if (sprite.getProperties().Is("signal")) {
                        String string0 = sprite.getProperties().Val("signal");
                        if ("radio".equals(string0)) {
                            object0 = new IsoRadio(cell, square, sprite);
                        } else if ("tv".equals(string0)) {
                            object0 = new IsoTelevision(cell, square, sprite);
                        }

                        AddObject(square, object0);
                    } else {
                        if (sprite.getProperties().Is(IsoFlagType.WallOverlay)) {
                            IsoObject object2 = null;
                            if (sprite.getProperties().Is(IsoFlagType.attachedSE)) {
                                object2 = square.getWallSE();
                            } else if (sprite.getProperties().Is(IsoFlagType.attachedW)) {
                                object2 = square.getWall(false);
                            } else if (sprite.getProperties().Is(IsoFlagType.attachedN)) {
                                object2 = square.getWall(true);
                            } else {
                                for (int int1 = square.getObjects().size() - 1; int1 >= 0; int1--) {
                                    IsoObject object3 = square.getObjects().get(int1);
                                    if (object3.sprite.getProperties().Is(IsoFlagType.cutW) || object3.sprite.getProperties().Is(IsoFlagType.cutN)) {
                                        object2 = object3;
                                        break;
                                    }
                                }
                            }

                            if (object2 != null) {
                                if (object2.AttachedAnimSprite == null) {
                                    object2.AttachedAnimSprite = new ArrayList<>(4);
                                }

                                object2.AttachedAnimSprite.add(IsoSpriteInstance.get(sprite));
                            } else {
                                object0 = IsoObject.getNew();
                                object0.sx = 0.0F;
                                object0.sprite = sprite;
                                object0.square = square;
                                AddObject(square, object0);
                            }

                            return;
                        }

                        if (sprite.getProperties().Is(IsoFlagType.FloorOverlay)) {
                            IsoObject object4 = square.getFloor();
                            if (object4 != null) {
                                if (object4.AttachedAnimSprite == null) {
                                    object4.AttachedAnimSprite = new ArrayList<>(4);
                                }

                                object4.AttachedAnimSprite.add(IsoSpriteInstance.get(sprite));
                            }
                        } else if (IsoMannequin.isMannequinSprite(sprite)) {
                            object0 = new IsoMannequin(cell, square, sprite);
                            AddObject(square, object0);
                        } else if (objectType == IsoObjectType.tree) {
                            if (sprite.getName() != null && sprite.getName().startsWith("vegetation_trees")) {
                                IsoObject object5 = square.getFloor();
                                if (object5 == null
                                    || object5.getSprite() == null
                                    || object5.getSprite().getName() == null
                                    || !object5.getSprite().getName().startsWith("blends_natural")) {
                                    DebugLog.log(
                                        "ERROR: removed tree at " + square.x + "," + square.y + "," + square.z + " because floor is not blends_natural"
                                    );
                                    return;
                                }
                            }

                            object0 = IsoTree.getNew();
                            object0.sprite = sprite;
                            object0.square = square;
                            object0.sx = 0.0F;
                            ((IsoTree)object0).initTree();

                            for (int int2 = 0; int2 < square.getObjects().size(); int2++) {
                                IsoObject object6 = square.getObjects().get(int2);
                                if (object6 instanceof IsoTree) {
                                    square.getObjects().remove(int2);
                                    object6.reset();
                                    isoTreeCache.push((IsoTree)object6);
                                    break;
                                }
                            }

                            AddObject(square, object0);
                        } else {
                            if ((sprite.CurrentAnim.Frames.isEmpty() || sprite.CurrentAnim.Frames.get(0).getTexture(IsoDirections.N) == null)
                                && !GameServer.bServer) {
                                if (!missingTiles.contains(string1)) {
                                    if (Core.bDebug) {
                                        DebugLog.General.error("CellLoader> missing tile " + string1);
                                    }

                                    missingTiles.add(string1);
                                }

                                sprite.LoadFramesNoDirPageSimple(Core.bDebug ? "media/ui/missing-tile-debug.png" : "media/ui/missing-tile.png");
                                if (sprite.CurrentAnim.Frames.isEmpty() || sprite.CurrentAnim.Frames.get(0).getTexture(IsoDirections.N) == null) {
                                    return;
                                }
                            }

                            String string2 = GameServer.bServer ? null : sprite.CurrentAnim.Frames.get(0).getTexture(IsoDirections.N).getName();
                            boolean boolean1 = true;
                            if (!GameServer.bServer
                                && string2.contains("TileObjectsExt")
                                && (string2.contains("_5") || string2.contains("_6") || string2.contains("_7") || string2.contains("_8"))) {
                                object0 = new IsoWheelieBin(cell, int3, int4, int5);
                                if (string2.contains("_5")) {
                                    object0.dir = IsoDirections.S;
                                }

                                if (string2.contains("_6")) {
                                    object0.dir = IsoDirections.W;
                                }

                                if (string2.contains("_7")) {
                                    object0.dir = IsoDirections.N;
                                }

                                if (string2.contains("_8")) {
                                    object0.dir = IsoDirections.E;
                                }

                                boolean1 = false;
                            }

                            if (boolean1) {
                                object0 = IsoObject.getNew();
                                object0.sx = 0.0F;
                                object0.sprite = sprite;
                                object0.square = square;
                                AddObject(square, object0);
                                if (object0.sprite.getProperties().Is("lightR")) {
                                    float float3 = Float.parseFloat(object0.sprite.getProperties().Val("lightR"));
                                    float float4 = Float.parseFloat(object0.sprite.getProperties().Val("lightG"));
                                    float float5 = Float.parseFloat(object0.sprite.getProperties().Val("lightB"));
                                    cell.getLamppostPositions()
                                        .add(new IsoLightSource(object0.square.getX(), object0.square.getY(), object0.square.getZ(), float3, float4, float5, 8));
                                }
                            }
                        }
                    }
                } else {
                    object0 = new IsoStove(cell, square, sprite);
                    AddObject(square, object0);
                    GameClient.instance.objectSyncReq.putRequest(square, object0);
                }
            } else {
                boolean boolean2 = Integer.parseInt(string1.substring(string1.lastIndexOf("_") + 1)) % 8 <= 3;
                object0 = new IsoCurtain(cell, square, sprite, objectType == IsoObjectType.curtainN || objectType == IsoObjectType.curtainS, boolean2);
                AddSpecialObject(square, object0);
                GameClient.instance.objectSyncReq.putRequest(square, object0);
            }

            if (object0 != null) {
                object0.tile = string1;
                object0.createContainersFromSpriteProperties();
                if (object0.sprite.getProperties().Is(IsoFlagType.vegitation)) {
                    object0.tintr = 0.7F + Rand.Next(30) / 100.0F;
                    object0.tintg = 0.7F + Rand.Next(30) / 100.0F;
                    object0.tintb = 0.7F + Rand.Next(30) / 100.0F;
                }
            }
        }
    }

    public static boolean LoadCellBinaryChunk(IsoCell cell, int int1, int int3, IsoChunk chunk) {
        int int0 = int1;
        int int2 = int3;
        String string = "world_" + int1 / 30 + "_" + int3 / 30 + ".lotpack";
        if (!IsoLot.InfoFileNames.containsKey(string)) {
            DebugLog.log("LoadCellBinaryChunk: NO SUCH LOT " + string);
            return false;
        } else {
            File file = new File(IsoLot.InfoFileNames.get(string));
            if (file.exists()) {
                IsoLot lot = null;

                try {
                    lot = IsoLot.get(int0 / 30, int2 / 30, int1, int3, chunk);
                    cell.PlaceLot(lot, 0, 0, 0, chunk, int1, int3);
                } finally {
                    if (lot != null) {
                        IsoLot.put(lot);
                    }
                }

                return true;
            } else {
                DebugLog.log("LoadCellBinaryChunk: NO SUCH LOT " + string);
                return false;
            }
        }
    }

    public static IsoCell LoadCellBinaryChunk(IsoSpriteManager var0, int int1, int int2) throws IOException {
        wanderX = 0;
        wanderY = 0;
        wanderRoom = null;
        wanderX = 0;
        wanderY = 0;
        IsoCell cell = new IsoCell(300, 300);
        int int0 = IsoPlayer.numPlayers;
        byte byte0 = 1;
        if (!GameServer.bServer) {
            if (GameClient.bClient) {
                WorldStreamer.instance.requestLargeAreaZip(int1, int2, IsoChunkMap.ChunkGridWidth / 2 + 2);
                IsoChunk.bDoServerRequests = false;
            }

            for (int int3 = 0; int3 < byte0; int3++) {
                cell.ChunkMap[int3].setInitialPos(int1, int2);
                IsoPlayer.assumedPlayer = int3;
                IsoChunkMap chunkMap = cell.ChunkMap[int3];
                int int4 = int1 - IsoChunkMap.ChunkGridWidth / 2;
                chunkMap = cell.ChunkMap[int3];
                int int5 = int2 - IsoChunkMap.ChunkGridWidth / 2;
                chunkMap = cell.ChunkMap[int3];
                int int6 = int1 + IsoChunkMap.ChunkGridWidth / 2 + 1;
                chunkMap = cell.ChunkMap[int3];
                int int7 = int2 + IsoChunkMap.ChunkGridWidth / 2 + 1;

                for (int int8 = int4; int8 < int6; int8++) {
                    for (int int9 = int5; int9 < int7; int9++) {
                        if (IsoWorld.instance.getMetaGrid().isValidChunk(int8, int9)) {
                            cell.ChunkMap[int3].LoadChunk(int8, int9, int8 - int4, int9 - int5);
                        }
                    }
                }
            }
        }

        IsoPlayer.assumedPlayer = 0;
        LuaEventManager.triggerEvent("OnPostMapLoad", cell, int1, int2);
        ConnectMultitileObjects(cell);
        return cell;
    }

    private static void RecurseMultitileObjects(IsoCell cell, IsoGridSquare square1, IsoGridSquare square0, ArrayList<IsoPushableObject> arrayList) {
        Iterator iterator = square0.getMovingObjects().iterator();
        IsoPushableObject pushableObject0 = null;
        boolean boolean0 = false;

        while (iterator != null && iterator.hasNext()) {
            IsoMovingObject movingObject = (IsoMovingObject)iterator.next();
            if (movingObject instanceof IsoPushableObject pushableObject1) {
                int int0 = square1.getX() - square0.getX();
                int int1 = square1.getY() - square0.getY();
                if (int1 != 0 && movingObject.sprite.getProperties().Is("connectY")) {
                    int int2 = Integer.parseInt(movingObject.sprite.getProperties().Val("connectY"));
                    if (int2 == int1) {
                        pushableObject1.connectList = arrayList;
                        arrayList.add(pushableObject1);
                        pushableObject0 = pushableObject1;
                        boolean0 = false;
                        break;
                    }
                }

                if (int0 != 0 && movingObject.sprite.getProperties().Is("connectX")) {
                    int int3 = Integer.parseInt(movingObject.sprite.getProperties().Val("connectX"));
                    if (int3 == int0) {
                        pushableObject1.connectList = arrayList;
                        arrayList.add(pushableObject1);
                        pushableObject0 = pushableObject1;
                        boolean0 = true;
                        break;
                    }
                }
            }
        }

        if (pushableObject0 != null) {
            if (pushableObject0.sprite.getProperties().Is("connectY") && boolean0) {
                int int4 = Integer.parseInt(pushableObject0.sprite.getProperties().Val("connectY"));
                IsoGridSquare square2 = cell.getGridSquare(
                    pushableObject0.getCurrentSquare().getX(), pushableObject0.getCurrentSquare().getY() + int4, pushableObject0.getCurrentSquare().getZ()
                );
                RecurseMultitileObjects(cell, pushableObject0.getCurrentSquare(), square2, pushableObject0.connectList);
            }

            if (pushableObject0.sprite.getProperties().Is("connectX") && !boolean0) {
                int int5 = Integer.parseInt(pushableObject0.sprite.getProperties().Val("connectX"));
                IsoGridSquare square3 = cell.getGridSquare(
                    pushableObject0.getCurrentSquare().getX() + int5, pushableObject0.getCurrentSquare().getY(), pushableObject0.getCurrentSquare().getZ()
                );
                RecurseMultitileObjects(cell, pushableObject0.getCurrentSquare(), square3, pushableObject0.connectList);
            }
        }
    }

    private static void ConnectMultitileObjects(IsoCell cell) {
        Iterator iterator = cell.getObjectList().iterator();

        while (iterator != null && iterator.hasNext()) {
            IsoMovingObject movingObject = (IsoMovingObject)iterator.next();
            if (movingObject instanceof IsoPushableObject pushableObject
                && (movingObject.sprite.getProperties().Is("connectY") || movingObject.sprite.getProperties().Is("connectX"))
                && pushableObject.connectList == null) {
                pushableObject.connectList = new ArrayList<>();
                pushableObject.connectList.add(pushableObject);
                if (movingObject.sprite.getProperties().Is("connectY")) {
                    int int0 = Integer.parseInt(movingObject.sprite.getProperties().Val("connectY"));
                    IsoGridSquare square0 = cell.getGridSquare(
                        movingObject.getCurrentSquare().getX(), movingObject.getCurrentSquare().getY() + int0, movingObject.getCurrentSquare().getZ()
                    );
                    if (square0 == null) {
                        boolean boolean0 = false;
                    }

                    RecurseMultitileObjects(cell, pushableObject.getCurrentSquare(), square0, pushableObject.connectList);
                }

                if (movingObject.sprite.getProperties().Is("connectX")) {
                    int int1 = Integer.parseInt(movingObject.sprite.getProperties().Val("connectX"));
                    IsoGridSquare square1 = cell.getGridSquare(
                        movingObject.getCurrentSquare().getX() + int1, movingObject.getCurrentSquare().getY(), movingObject.getCurrentSquare().getZ()
                    );
                    RecurseMultitileObjects(cell, pushableObject.getCurrentSquare(), square1, pushableObject.connectList);
                }
            }
        }
    }

    private static void AddObject(IsoGridSquare square, IsoObject object) {
        int int0 = square.placeWallAndDoorCheck(object, square.getObjects().size());
        if (int0 != square.getObjects().size() && int0 >= 0 && int0 <= square.getObjects().size()) {
            square.getObjects().add(int0, object);
        } else {
            square.getObjects().add(object);
        }
    }

    private static void AddSpecialObject(IsoGridSquare square, IsoObject object) {
        int int0 = square.placeWallAndDoorCheck(object, square.getObjects().size());
        if (int0 != square.getObjects().size() && int0 >= 0 && int0 <= square.getObjects().size()) {
            square.getObjects().add(int0, object);
        } else {
            square.getObjects().add(object);
            square.getSpecialObjects().add(object);
        }
    }
}
