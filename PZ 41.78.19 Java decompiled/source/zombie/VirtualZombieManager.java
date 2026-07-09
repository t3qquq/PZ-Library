// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.ZombiesZoneDefinition;
import zombie.characters.action.ActionGroup;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaChunk;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoFireManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.popman.NetworkZombieSimulator;
import zombie.popman.ZombiePopulationManager;
import zombie.vehicles.PolygonalMap2;

public final class VirtualZombieManager {
    private final ArrayDeque<IsoZombie> ReusableZombies = new ArrayDeque<>();
    private final HashSet<IsoZombie> ReusableZombieSet = new HashSet<>();
    private final ArrayList<IsoZombie> ReusedThisFrame = new ArrayList<>();
    private final ArrayList<IsoZombie> RecentlyRemoved = new ArrayList<>();
    public static VirtualZombieManager instance = new VirtualZombieManager();
    public int MaxRealZombies = 1;
    private final ArrayList<IsoZombie> m_tempZombies = new ArrayList<>();
    public final ArrayList<IsoGridSquare> choices = new ArrayList<>();
    private final ArrayList<IsoGridSquare> bestchoices = new ArrayList<>();
    HandWeapon w = null;
    private int BLOCKED_N = 1;
    private int BLOCKED_S = 2;
    private int BLOCKED_W = 4;
    private int BLOCKED_E = 8;
    private int NO_SQUARE_N = 16;
    private int NO_SQUARE_S = 32;
    private int NO_SQUARE_W = 64;
    private int NO_SQUARE_E = 128;

    public boolean removeZombieFromWorld(IsoZombie z) {
        boolean boolean0 = z.getCurrentSquare() != null;
        z.getEmitter().unregister();
        z.removeFromWorld();
        z.removeFromSquare();
        return boolean0;
    }

    private void reuseZombie(IsoZombie zombie0) {
        if (zombie0 != null) {
            assert !IsoWorld.instance.CurrentCell.getObjectList().contains(zombie0);

            assert !IsoWorld.instance.CurrentCell.getZombieList().contains(zombie0);

            assert zombie0.getCurrentSquare() == null || !zombie0.getCurrentSquare().getMovingObjects().contains(zombie0);

            if (!this.isReused(zombie0)) {
                NetworkZombieSimulator.getInstance().remove(zombie0);
                zombie0.resetForReuse();
                this.addToReusable(zombie0);
            }
        }
    }

    public void addToReusable(IsoZombie z) {
        if (z != null && !this.ReusableZombieSet.contains(z)) {
            this.ReusableZombies.addLast(z);
            this.ReusableZombieSet.add(z);
        }
    }

    public boolean isReused(IsoZombie z) {
        return this.ReusableZombieSet.contains(z);
    }

    public void init() {
        if (!GameClient.bClient) {
            Object object = null;
            if (!IsoWorld.getZombiesDisabled()) {
                for (int int0 = 0; int0 < this.MaxRealZombies; int0++) {
                    object = new IsoZombie(IsoWorld.instance.CurrentCell);
                    ((IsoZombie)object).getEmitter().unregister();
                    this.addToReusable((IsoZombie)object);
                }
            }
        }
    }

    public void Reset() {
        for (IsoZombie zombie0 : this.ReusedThisFrame) {
            if (zombie0.vocalEvent != 0L) {
                zombie0.getEmitter().stopSoundLocal(zombie0.vocalEvent);
                zombie0.vocalEvent = 0L;
            }
        }

        this.bestchoices.clear();
        this.choices.clear();
        this.RecentlyRemoved.clear();
        this.ReusableZombies.clear();
        this.ReusableZombieSet.clear();
        this.ReusedThisFrame.clear();
    }

    public void update() {
        long long0 = System.currentTimeMillis();

        for (int int0 = this.RecentlyRemoved.size() - 1; int0 >= 0; int0--) {
            IsoZombie zombie0 = this.RecentlyRemoved.get(int0);
            zombie0.updateEmitter();
            if (long0 - zombie0.removedFromWorldMS > 5000L) {
                if (zombie0.vocalEvent != 0L) {
                    zombie0.getEmitter().stopSoundLocal(zombie0.vocalEvent);
                    zombie0.vocalEvent = 0L;
                }

                zombie0.getEmitter().stopAll();
                this.RecentlyRemoved.remove(int0);
                this.ReusedThisFrame.add(zombie0);
            }
        }

        if (!GameClient.bClient && !GameServer.bServer) {
            for (int int1 = 0; int1 < IsoWorld.instance.CurrentCell.getZombieList().size(); int1++) {
                IsoZombie zombie1 = IsoWorld.instance.CurrentCell.getZombieList().get(int1);
                if (!zombie1.KeepItReal && zombie1.getCurrentSquare() == null) {
                    zombie1.removeFromWorld();
                    zombie1.removeFromSquare();

                    assert this.ReusedThisFrame.contains(zombie1);

                    assert !IsoWorld.instance.CurrentCell.getZombieList().contains(zombie1);

                    int1--;
                }
            }

            for (int int2 = 0; int2 < this.ReusedThisFrame.size(); int2++) {
                IsoZombie zombie2 = this.ReusedThisFrame.get(int2);
                this.reuseZombie(zombie2);
            }

            this.ReusedThisFrame.clear();
        } else {
            for (int int3 = 0; int3 < this.ReusedThisFrame.size(); int3++) {
                IsoZombie zombie3 = this.ReusedThisFrame.get(int3);
                this.reuseZombie(zombie3);
            }

            this.ReusedThisFrame.clear();
        }
    }

    public IsoZombie createRealZombieAlways(int ZombieDir, boolean bDead) {
        return this.createRealZombieAlways(ZombieDir, bDead, 0);
    }

    public IsoZombie createRealZombieAlways(int descriptorID, int ZombieDir, boolean bDead) {
        int int0 = PersistentOutfits.instance.getOutfit(descriptorID);
        return this.createRealZombieAlways(ZombieDir, bDead, int0);
    }

    public IsoZombie createRealZombieAlways(int ZombieDir, boolean bDead, int outfitID) {
        IsoZombie zombie0 = null;
        if (!SystemDisabler.doZombieCreation) {
            return null;
        } else if (this.choices != null && !this.choices.isEmpty()) {
            IsoGridSquare square = this.choices.get(Rand.Next(this.choices.size()));
            if (square == null) {
                return null;
            } else {
                if (this.w == null) {
                    this.w = (HandWeapon)InventoryItemFactory.CreateItem("Base.Axe");
                }

                if ((GameServer.bServer || GameClient.bClient) && outfitID == 0) {
                    outfitID = ZombiesZoneDefinition.pickPersistentOutfit(square);
                }

                if (this.ReusableZombies.isEmpty()) {
                    zombie0 = new IsoZombie(IsoWorld.instance.CurrentCell);
                    zombie0.bDressInRandomOutfit = outfitID == 0;
                    zombie0.setPersistentOutfitID(outfitID);
                    IsoWorld.instance.CurrentCell.getObjectList().add(zombie0);
                } else {
                    zombie0 = this.ReusableZombies.removeFirst();
                    this.ReusableZombieSet.remove(zombie0);
                    zombie0.getHumanVisual().clear();
                    zombie0.clearAttachedItems();
                    zombie0.clearItemsToSpawnAtDeath();
                    zombie0.bDressInRandomOutfit = outfitID == 0;
                    zombie0.setPersistentOutfitID(outfitID);
                    zombie0.setSitAgainstWall(false);
                    zombie0.setOnDeathDone(false);
                    zombie0.setOnKillDone(false);
                    zombie0.setDoDeathSound(true);
                    zombie0.setHitTime(0);
                    zombie0.setFallOnFront(false);
                    zombie0.setFakeDead(false);
                    zombie0.setReanimatedPlayer(false);
                    zombie0.setStateMachineLocked(false);
                    Vector2 vector = zombie0.dir.ToVector();
                    vector.x = vector.x + (Rand.Next(200) / 100.0F - 0.5F);
                    vector.y = vector.y + (Rand.Next(200) / 100.0F - 0.5F);
                    vector.normalize();
                    zombie0.setForwardDirection(vector);
                    IsoWorld.instance.CurrentCell.getObjectList().add(zombie0);
                    zombie0.walkVariant = "ZombieWalk";
                    zombie0.DoZombieStats();
                    if (zombie0.isOnFire()) {
                        IsoFireManager.RemoveBurningCharacter(zombie0);
                        zombie0.setOnFire(false);
                    }

                    if (zombie0.AttachedAnimSprite != null) {
                        zombie0.AttachedAnimSprite.clear();
                    }

                    zombie0.thumpFlag = 0;
                    zombie0.thumpSent = false;
                    zombie0.soundSourceTarget = null;
                    zombie0.soundAttract = 0.0F;
                    zombie0.soundAttractTimeout = 0.0F;
                    zombie0.bodyToEat = null;
                    zombie0.eatBodyTarget = null;
                    zombie0.atlasTex = null;
                    zombie0.clearVariables();
                    zombie0.setStaggerBack(false);
                    zombie0.setKnockedDown(false);
                    zombie0.setKnifeDeath(false);
                    zombie0.setJawStabAttach(false);
                    zombie0.setCrawler(false);
                    zombie0.initializeStates();
                    zombie0.actionContext.setGroup(ActionGroup.getActionGroup("zombie"));
                    zombie0.advancedAnimator.OnAnimDataChanged(false);
                    zombie0.setDefaultState();
                    zombie0.getAnimationPlayer().resetBoneModelTransforms();
                }

                zombie0.dir = IsoDirections.fromIndex(ZombieDir);
                zombie0.setForwardDirection(zombie0.dir.ToVector());
                zombie0.getInventory().setExplored(false);
                if (bDead) {
                    zombie0.bDressInRandomOutfit = true;
                }

                zombie0.target = null;
                zombie0.TimeSinceSeenFlesh = 100000.0F;
                if (!zombie0.isFakeDead()) {
                    if (SandboxOptions.instance.Lore.Toughness.getValue() == 1) {
                        zombie0.setHealth(3.5F + Rand.Next(0.0F, 0.3F));
                    }

                    if (SandboxOptions.instance.Lore.Toughness.getValue() == 2) {
                        zombie0.setHealth(1.5F + Rand.Next(0.0F, 0.3F));
                    }

                    if (SandboxOptions.instance.Lore.Toughness.getValue() == 3) {
                        zombie0.setHealth(0.5F + Rand.Next(0.0F, 0.3F));
                    }

                    if (SandboxOptions.instance.Lore.Toughness.getValue() == 4) {
                        zombie0.setHealth(Rand.Next(0.5F, 3.5F) + Rand.Next(0.0F, 0.3F));
                    }
                } else {
                    zombie0.setHealth(0.5F + Rand.Next(0.0F, 0.3F));
                }

                float float0 = Rand.Next(0, 1000);
                float float1 = Rand.Next(0, 1000);
                float0 /= 1000.0F;
                float1 /= 1000.0F;
                float0 += square.getX();
                float1 += square.getY();
                zombie0.setCurrent(square);
                zombie0.setMovingSquareNow();
                zombie0.setX(float0);
                zombie0.setY(float1);
                zombie0.setZ(square.getZ());
                if ((GameClient.bClient || GameServer.bServer) && zombie0.networkAI != null) {
                    zombie0.networkAI.reset();
                }

                zombie0.upKillCount = true;
                if (bDead) {
                    zombie0.setDir(IsoDirections.fromIndex(Rand.Next(8)));
                    zombie0.setForwardDirection(zombie0.dir.ToVector());
                    zombie0.setFakeDead(false);
                    zombie0.setHealth(0.0F);
                    zombie0.upKillCount = false;
                    zombie0.DoZombieInventory();
                    new IsoDeadBody(zombie0, true);
                    return zombie0;
                } else {
                    synchronized (IsoWorld.instance.CurrentCell.getZombieList()) {
                        zombie0.getEmitter().register();
                        IsoWorld.instance.CurrentCell.getZombieList().add(zombie0);
                        if (GameClient.bClient) {
                            zombie0.bRemote = true;
                        }

                        if (GameServer.bServer) {
                            zombie0.OnlineID = ServerMap.instance.getUniqueZombieId();
                            if (zombie0.OnlineID == -1) {
                                IsoWorld.instance.CurrentCell.getZombieList().remove(zombie0);
                                IsoWorld.instance.CurrentCell.getObjectList().remove(zombie0);
                                this.ReusedThisFrame.add(zombie0);
                                return null;
                            }

                            ServerMap.instance.ZombieMap.put(zombie0.OnlineID, zombie0);
                        }

                        return zombie0;
                    }
                }
            }
        } else {
            return null;
        }
    }

    private IsoGridSquare pickEatingZombieSquare(float float2, float float3, float float1, float float0, int int0) {
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float0, (double)int0);
        if (square == null || !this.canSpawnAt(square.x, square.y, square.z) || square.HasStairs()) {
            return null;
        } else {
            return PolygonalMap2.instance.lineClearCollide(float2, float3, float1, float0, int0, null, false, true) ? null : square;
        }
    }

    public void createEatingZombies(IsoDeadBody target, int nb) {
        if (!IsoWorld.getZombiesDisabled()) {
            for (int int0 = 0; int0 < nb; int0++) {
                float float0 = target.x;
                float float1 = target.y;
                switch (int0) {
                    case 0:
                        float0 -= 0.5F;
                        break;
                    case 1:
                        float0 += 0.5F;
                        break;
                    case 2:
                        float1 -= 0.5F;
                        break;
                    case 3:
                        float1 += 0.5F;
                }

                IsoGridSquare square = this.pickEatingZombieSquare(target.x, target.y, float0, float1, (int)target.z);
                if (square != null) {
                    this.choices.clear();
                    this.choices.add(square);
                    IsoZombie zombie0 = this.createRealZombieAlways(1, false);
                    if (zombie0 != null) {
                        ZombieSpawnRecorder.instance.record(zombie0, "createEatingZombies");
                        zombie0.bDressInRandomOutfit = true;
                        zombie0.setX(float0);
                        zombie0.setY(float1);
                        zombie0.setZ(target.z);
                        zombie0.faceLocationF(target.x, target.y);
                        zombie0.setEatBodyTarget(target, true);
                    }
                }
            }
        }
    }

    private IsoZombie createRealZombie(int int0, boolean boolean0) {
        return GameClient.bClient ? null : this.createRealZombieAlways(int0, boolean0);
    }

    public void AddBloodToMap(int nSize, IsoChunk chk) {
        for (int int0 = 0; int0 < nSize; int0++) {
            Object object = null;
            int int1 = 0;

            do {
                int int2 = Rand.Next(10);
                int int3 = Rand.Next(10);
                object = chk.getGridSquare(int2, int3, 0);
                int1++;
            } while (int1 < 100 && (object == null || !((IsoGridSquare)object).isFree(false)));

            if (object != null) {
                byte byte0 = 5;
                if (Rand.Next(10) == 0) {
                    byte0 = 10;
                }

                if (Rand.Next(40) == 0) {
                    byte0 = 20;
                }

                for (int int4 = 0; int4 < byte0; int4++) {
                    float float0 = Rand.Next(3000) / 1000.0F;
                    float float1 = Rand.Next(3000) / 1000.0F;
                    chk.addBloodSplat(
                        ((IsoGridSquare)object).getX() + --float0, ((IsoGridSquare)object).getY() + --float1, ((IsoGridSquare)object).getZ(), Rand.Next(12) + 8
                    );
                }
            }
        }
    }

    public ArrayList<IsoZombie> addZombiesToMap(int nSize, RoomDef room) {
        return this.addZombiesToMap(nSize, room, true);
    }

    public ArrayList<IsoZombie> addZombiesToMap(int nSize, RoomDef room, boolean bAllowDead) {
        ArrayList arrayList = new ArrayList();
        if ("Tutorial".equals(Core.GameMode)) {
            return arrayList;
        } else if (IsoWorld.getZombiesDisabled()) {
            return arrayList;
        } else {
            this.choices.clear();
            this.bestchoices.clear();
            Object object = null;

            for (int int0 = 0; int0 < room.rects.size(); int0++) {
                int int1 = room.level;
                RoomDef.RoomRect roomRect = room.rects.get(int0);

                for (int int2 = roomRect.x; int2 < roomRect.getX2(); int2++) {
                    for (int int3 = roomRect.y; int3 < roomRect.getY2(); int3++) {
                        object = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, int1);
                        if (object != null && this.canSpawnAt(int2, int3, int1)) {
                            this.choices.add((IsoGridSquare)object);
                            boolean boolean0 = false;

                            for (int int4 = 0; int4 < IsoPlayer.numPlayers; int4++) {
                                if (IsoPlayer.players[int4] != null && ((IsoGridSquare)object).isSeen(int4)) {
                                    boolean0 = true;
                                }
                            }

                            if (!boolean0) {
                                this.bestchoices.add((IsoGridSquare)object);
                            }
                        }
                    }
                }
            }

            nSize = Math.min(nSize, this.choices.size());
            if (!this.bestchoices.isEmpty()) {
                this.choices.addAll(this.bestchoices);
                this.choices.addAll(this.bestchoices);
            }

            for (int int5 = 0; int5 < nSize; int5++) {
                if (!this.choices.isEmpty()) {
                    room.building.bAlarmed = false;
                    int int6 = Rand.Next(8);
                    byte byte0 = 4;
                    IsoZombie zombie0 = this.createRealZombie(int6, bAllowDead ? Rand.Next((int)byte0) == 0 : false);
                    if (zombie0 != null && zombie0.getSquare() != null) {
                        if (!GameServer.bServer) {
                            zombie0.bDressInRandomOutfit = true;
                        }

                        zombie0.setX((int)zombie0.getX() + Rand.Next(2, 8) / 10.0F);
                        zombie0.setY((int)zombie0.getY() + Rand.Next(2, 8) / 10.0F);
                        this.choices.remove(zombie0.getSquare());
                        this.choices.remove(zombie0.getSquare());
                        this.choices.remove(zombie0.getSquare());
                        arrayList.add(zombie0);
                    }
                } else {
                    System.out.println("No choices for zombie.");
                }
            }

            this.bestchoices.clear();
            this.choices.clear();
            return arrayList;
        }
    }

    public void tryAddIndoorZombies(RoomDef room, boolean bAllowDead) {
    }

    private void addIndoorZombies(int int4, RoomDef roomDef, boolean boolean0) {
        this.choices.clear();
        this.bestchoices.clear();
        Object object = null;

        for (int int0 = 0; int0 < roomDef.rects.size(); int0++) {
            int int1 = roomDef.level;
            RoomDef.RoomRect roomRect = roomDef.rects.get(int0);

            for (int int2 = roomRect.x; int2 < roomRect.getX2(); int2++) {
                for (int int3 = roomRect.y; int3 < roomRect.getY2(); int3++) {
                    object = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, int1);
                    if (object != null && this.canSpawnAt(int2, int3, int1)) {
                        this.choices.add((IsoGridSquare)object);
                    }
                }
            }
        }

        int4 = Math.min(int4, this.choices.size());
        if (!this.bestchoices.isEmpty()) {
            this.choices.addAll(this.bestchoices);
            this.choices.addAll(this.bestchoices);
        }

        for (int int5 = 0; int5 < int4; int5++) {
            if (!this.choices.isEmpty()) {
                roomDef.building.bAlarmed = false;
                int int6 = Rand.Next(8);
                byte byte0 = 4;
                IsoZombie zombie0 = this.createRealZombie(int6, boolean0 ? Rand.Next((int)byte0) == 0 : false);
                if (zombie0 != null && zombie0.getSquare() != null) {
                    ZombieSpawnRecorder.instance.record(zombie0, "addIndoorZombies");
                    zombie0.bIndoorZombie = true;
                    zombie0.setX((int)zombie0.getX() + Rand.Next(2, 8) / 10.0F);
                    zombie0.setY((int)zombie0.getY() + Rand.Next(2, 8) / 10.0F);
                    this.choices.remove(zombie0.getSquare());
                    this.choices.remove(zombie0.getSquare());
                    this.choices.remove(zombie0.getSquare());
                }
            } else {
                System.out.println("No choices for zombie.");
            }
        }

        this.bestchoices.clear();
        this.choices.clear();
    }

    public void addIndoorZombiesToChunk(IsoChunk chunk, IsoRoom room, int zombieCountForRoom, ArrayList<IsoZombie> zombies) {
        if (zombieCountForRoom > 0) {
            float float0 = room.getRoomDef().getAreaOverlapping(chunk);
            int int0 = (int)Math.ceil(zombieCountForRoom * float0);
            if (int0 > 0) {
                this.choices.clear();
                int int1 = room.def.level;

                for (int int2 = 0; int2 < room.rects.size(); int2++) {
                    RoomDef.RoomRect roomRect = room.rects.get(int2);
                    int int3 = Math.max(chunk.wx * 10, roomRect.x);
                    int int4 = Math.max(chunk.wy * 10, roomRect.y);
                    int int5 = Math.min((chunk.wx + 1) * 10, roomRect.x + roomRect.w);
                    int int6 = Math.min((chunk.wy + 1) * 10, roomRect.y + roomRect.h);

                    for (int int7 = int3; int7 < int5; int7++) {
                        for (int int8 = int4; int8 < int6; int8++) {
                            IsoGridSquare square = chunk.getGridSquare(int7 - chunk.wx * 10, int8 - chunk.wy * 10, int1);
                            if (square != null && this.canSpawnAt(int7, int8, int1)) {
                                this.choices.add(square);
                            }
                        }
                    }
                }

                if (!this.choices.isEmpty()) {
                    room.def.building.bAlarmed = false;
                    int0 = Math.min(int0, this.choices.size());

                    for (int int9 = 0; int9 < int0; int9++) {
                        IsoZombie zombie0 = this.createRealZombie(Rand.Next(8), false);
                        if (zombie0 != null && zombie0.getSquare() != null) {
                            if (!GameServer.bServer) {
                                zombie0.bDressInRandomOutfit = true;
                            }

                            zombie0.setX((int)zombie0.getX() + Rand.Next(2, 8) / 10.0F);
                            zombie0.setY((int)zombie0.getY() + Rand.Next(2, 8) / 10.0F);
                            this.choices.remove(zombie0.getSquare());
                            zombies.add(zombie0);
                        }
                    }

                    this.choices.clear();
                }
            }
        }
    }

    public void addIndoorZombiesToChunk(IsoChunk chunk, IsoRoom room) {
        if (room.def.spawnCount == -1) {
            room.def.spawnCount = this.getZombieCountForRoom(room);
        }

        this.m_tempZombies.clear();
        this.addIndoorZombiesToChunk(chunk, room, room.def.spawnCount, this.m_tempZombies);
        ZombieSpawnRecorder.instance.record(this.m_tempZombies, "addIndoorZombiesToChunk");
    }

    public void addDeadZombiesToMap(int nSize, RoomDef room) {
        boolean boolean0 = false;
        this.choices.clear();
        this.bestchoices.clear();
        Object object = null;

        for (int int0 = 0; int0 < room.rects.size(); int0++) {
            int int1 = room.level;
            RoomDef.RoomRect roomRect = room.rects.get(int0);

            for (int int2 = roomRect.x; int2 < roomRect.getX2(); int2++) {
                for (int int3 = roomRect.y; int3 < roomRect.getY2(); int3++) {
                    object = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, int1);
                    if (object != null && ((IsoGridSquare)object).isFree(false)) {
                        this.choices.add((IsoGridSquare)object);
                        if (!GameServer.bServer) {
                            boolean boolean1 = false;

                            for (int int4 = 0; int4 < IsoPlayer.numPlayers; int4++) {
                                if (IsoPlayer.players[int4] != null && ((IsoGridSquare)object).isSeen(int4)) {
                                    boolean1 = true;
                                }
                            }

                            if (!boolean1) {
                                this.bestchoices.add((IsoGridSquare)object);
                            }
                        }
                    }
                }
            }
        }

        nSize = Math.min(nSize, this.choices.size());
        if (!this.bestchoices.isEmpty()) {
            this.choices.addAll(this.bestchoices);
            this.choices.addAll(this.bestchoices);
        }

        for (int int5 = 0; int5 < nSize; int5++) {
            if (!this.choices.isEmpty()) {
                int int6 = Rand.Next(8);
                this.createRealZombie(int6, true);
            }
        }

        this.bestchoices.clear();
        this.choices.clear();
    }

    public void RemoveZombie(IsoZombie obj) {
        if (obj.isReanimatedPlayer()) {
            if (obj.vocalEvent != 0L) {
                obj.getEmitter().stopSoundLocal(obj.vocalEvent);
                obj.vocalEvent = 0L;
            }

            ReanimatedPlayers.instance.removeReanimatedPlayerFromWorld(obj);
        } else {
            if (obj.isDead()) {
                if (!this.RecentlyRemoved.contains(obj)) {
                    obj.removedFromWorldMS = System.currentTimeMillis();
                    this.RecentlyRemoved.add(obj);
                }
            } else if (!this.ReusedThisFrame.contains(obj)) {
                this.ReusedThisFrame.add(obj);
            }
        }
    }

    public void createHordeFromTo(float spawnX, float spawnY, float targetX, float targetY, int count) {
        ZombiePopulationManager.instance.createHordeFromTo((int)spawnX, (int)spawnY, (int)targetX, (int)targetY, count);
    }

    public IsoZombie createRealZombie(float x, float y, float z) {
        this.choices.clear();
        this.choices.add(IsoWorld.instance.CurrentCell.getGridSquare((double)x, (double)y, (double)z));
        if (!this.choices.isEmpty()) {
            int int0 = Rand.Next(8);
            return this.createRealZombie(int0, true);
        } else {
            return null;
        }
    }

    public IsoZombie createRealZombieNow(float x, float y, float z) {
        this.choices.clear();
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)x, (double)y, (double)z);
        if (square == null) {
            return null;
        } else {
            this.choices.add(square);
            if (!this.choices.isEmpty()) {
                int int0 = Rand.Next(8);
                return this.createRealZombie(int0, false);
            } else {
                return null;
            }
        }
    }

    private int getZombieCountForRoom(IsoRoom room) {
        if (IsoWorld.getZombiesDisabled()) {
            return 0;
        } else if (GameClient.bClient) {
            return 0;
        } else if (Core.bLastStand) {
            return 0;
        } else {
            int int0 = 7;
            if (SandboxOptions.instance.Zombies.getValue() == 1) {
                int0 = 3;
            } else if (SandboxOptions.instance.Zombies.getValue() == 2) {
                int0 = 4;
            } else if (SandboxOptions.instance.Zombies.getValue() == 3) {
                int0 = 6;
            } else if (SandboxOptions.instance.Zombies.getValue() == 5) {
                int0 = 15;
            }

            float float0 = 0.0F;
            IsoMetaChunk metaChunk = IsoWorld.instance.getMetaChunk(room.def.x / 10, room.def.y / 10);
            if (metaChunk != null) {
                float0 = metaChunk.getLootZombieIntensity();
                if (float0 > 4.0F) {
                    int0 = (int)(int0 - (float0 / 2.0F - 2.0F));
                }
            }

            if (room.def.getArea() > 100) {
                int0 -= 2;
            }

            int0 = Math.max(2, int0);
            if (room.getBuilding() != null) {
                int int1 = room.def.getArea();
                if (room.getBuilding().getRoomsNumber() > 100 && int1 >= 20) {
                    int int2 = room.getBuilding().getRoomsNumber() - 95;
                    if (int2 > 20) {
                        int2 = 20;
                    }

                    if (SandboxOptions.instance.Zombies.getValue() == 1) {
                        int2 += 10;
                    } else if (SandboxOptions.instance.Zombies.getValue() == 2) {
                        int2 += 7;
                    } else if (SandboxOptions.instance.Zombies.getValue() == 3) {
                        int2 += 5;
                    } else if (SandboxOptions.instance.Zombies.getValue() == 4) {
                        int2 -= 10;
                    }

                    if (int1 < 30) {
                        int2 -= 6;
                    }

                    if (int1 < 50) {
                        int2 -= 10;
                    }

                    if (int1 < 70) {
                        int2 -= 13;
                    }

                    return Rand.Next(int2, int2 + 10);
                }
            }

            if (Rand.Next(int0) == 0) {
                int int3 = 1;
                int3 = (int)(int3 + (float0 / 2.0F - 2.0F));
                if (room.def.getArea() < 30) {
                    int3 -= 4;
                }

                if (room.def.getArea() > 85) {
                    int3 += 2;
                }

                if (room.getBuilding().getRoomsNumber() < 7) {
                    int3 -= 2;
                }

                if (SandboxOptions.instance.Zombies.getValue() == 1) {
                    int3 += 3;
                } else if (SandboxOptions.instance.Zombies.getValue() == 2) {
                    int3 += 2;
                } else if (SandboxOptions.instance.Zombies.getValue() == 3) {
                    int3++;
                } else if (SandboxOptions.instance.Zombies.getValue() == 5) {
                    int3 -= 2;
                }

                int3 = Math.max(0, int3);
                int3 = Math.min(7, int3);
                return Rand.Next(int3, int3 + 2);
            } else {
                return 0;
            }
        }
    }

    public void roomSpotted(IsoRoom room) {
        if (!GameClient.bClient) {
            room.def.forEachChunk((roomDef, chunk) -> chunk.addSpawnedRoom(roomDef.ID));
            if (room.def.spawnCount == -1) {
                room.def.spawnCount = this.getZombieCountForRoom(room);
            }

            if (room.def.spawnCount > 0) {
                if (room.getBuilding().getDef().isFullyStreamedIn()) {
                    ArrayList arrayList = this.addZombiesToMap(room.def.spawnCount, room.def, false);
                    ZombieSpawnRecorder.instance.record(arrayList, "roomSpotted");
                } else {
                    this.m_tempZombies.clear();
                    room.def.forEachChunk((var2x, chunk) -> this.addIndoorZombiesToChunk(chunk, room, room.def.spawnCount, this.m_tempZombies));
                    ZombieSpawnRecorder.instance.record(this.m_tempZombies, "roomSpotted");
                }
            }
        }
    }

    private int getBlockedBits(IsoGridSquare square) {
        int int0 = 0;
        if (square == null) {
            return int0;
        } else {
            if (square.nav[IsoDirections.N.index()] == null) {
                int0 |= this.NO_SQUARE_N;
            } else if (IsoGridSquare.getMatrixBit(square.pathMatrix, 1, 0, 1)) {
                int0 |= this.BLOCKED_N;
            }

            if (square.nav[IsoDirections.S.index()] == null) {
                int0 |= this.NO_SQUARE_S;
            } else if (IsoGridSquare.getMatrixBit(square.pathMatrix, 1, 2, 1)) {
                int0 |= this.BLOCKED_S;
            }

            if (square.nav[IsoDirections.W.index()] == null) {
                int0 |= this.NO_SQUARE_W;
            } else if (IsoGridSquare.getMatrixBit(square.pathMatrix, 0, 1, 1)) {
                int0 |= this.BLOCKED_W;
            }

            if (square.nav[IsoDirections.E.index()] == null) {
                int0 |= this.NO_SQUARE_E;
            } else if (IsoGridSquare.getMatrixBit(square.pathMatrix, 2, 1, 1)) {
                int0 |= this.BLOCKED_E;
            }

            return int0;
        }
    }

    private boolean isBlockedInAllDirections(int int0, int int1, int int2) {
        IsoGridSquare square = GameServer.bServer
            ? ServerMap.instance.getGridSquare(int0, int1, int2)
            : IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (square == null) {
            return false;
        } else {
            boolean boolean0 = IsoGridSquare.getMatrixBit(square.pathMatrix, 1, 0, 1) && square.nav[IsoDirections.N.index()] != null;
            boolean boolean1 = IsoGridSquare.getMatrixBit(square.pathMatrix, 1, 2, 1) && square.nav[IsoDirections.S.index()] != null;
            boolean boolean2 = IsoGridSquare.getMatrixBit(square.pathMatrix, 0, 1, 1) && square.nav[IsoDirections.W.index()] != null;
            boolean boolean3 = IsoGridSquare.getMatrixBit(square.pathMatrix, 2, 1, 1) && square.nav[IsoDirections.E.index()] != null;
            return boolean0 && boolean1 && boolean2 && boolean3;
        }
    }

    private boolean canPathOnlyN(IsoGridSquare square) {
        while (true) {
            int int0 = this.getBlockedBits(square);
            if ((int0 & (this.BLOCKED_W | this.BLOCKED_E)) != (this.BLOCKED_W | this.BLOCKED_E)) {
                return false;
            }

            if ((int0 & this.NO_SQUARE_N) != 0) {
                return false;
            }

            if ((int0 & this.BLOCKED_N) != 0) {
                return true;
            }

            square = square.nav[IsoDirections.N.index()];
        }
    }

    private boolean canPathOnlyS(IsoGridSquare square) {
        while (true) {
            int int0 = this.getBlockedBits(square);
            if ((int0 & (this.BLOCKED_W | this.BLOCKED_E)) != (this.BLOCKED_W | this.BLOCKED_E)) {
                return false;
            }

            if ((int0 & this.NO_SQUARE_S) != 0) {
                return false;
            }

            if ((int0 & this.BLOCKED_S) != 0) {
                return true;
            }

            square = square.nav[IsoDirections.S.index()];
        }
    }

    private boolean canPathOnlyW(IsoGridSquare square) {
        while (true) {
            int int0 = this.getBlockedBits(square);
            if ((int0 & (this.BLOCKED_N | this.BLOCKED_S)) != (this.BLOCKED_N | this.BLOCKED_S)) {
                return false;
            }

            if ((int0 & this.NO_SQUARE_W) != 0) {
                return false;
            }

            if ((int0 & this.BLOCKED_W) != 0) {
                return true;
            }

            square = square.nav[IsoDirections.W.index()];
        }
    }

    private boolean canPathOnlyE(IsoGridSquare square) {
        while (true) {
            int int0 = this.getBlockedBits(square);
            if ((int0 & (this.BLOCKED_N | this.BLOCKED_S)) != (this.BLOCKED_N | this.BLOCKED_S)) {
                return false;
            }

            if ((int0 & this.NO_SQUARE_E) != 0) {
                return false;
            }

            if ((int0 & this.BLOCKED_E) != 0) {
                return true;
            }

            square = square.nav[IsoDirections.E.index()];
        }
    }

    public boolean canSpawnAt(int x, int y, int z) {
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(x, y, z);
        if (square != null && square.isFree(false)) {
            int int0 = this.getBlockedBits(square);
            if (int0 == (this.BLOCKED_N | this.BLOCKED_S | this.BLOCKED_W | this.BLOCKED_E)) {
                return false;
            } else {
                return (int0 & (this.BLOCKED_N | this.BLOCKED_S)) == (this.BLOCKED_N | this.BLOCKED_S)
                        && this.canPathOnlyW(square)
                        && this.canPathOnlyE(square)
                    ? false
                    : (int0 & (this.BLOCKED_W | this.BLOCKED_E)) != (this.BLOCKED_W | this.BLOCKED_E)
                        || !this.canPathOnlyN(square)
                        || !this.canPathOnlyS(square);
            }
        } else {
            return false;
        }
    }

    public int reusableZombiesSize() {
        return this.ReusableZombies.size();
    }
}
