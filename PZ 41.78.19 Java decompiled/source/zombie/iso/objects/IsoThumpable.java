// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.SystemDisabler;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.states.ThumpState;
import zombie.characters.BaseCharacterSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.core.Translator;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.properties.PropertyContainer;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.HandWeapon;
import zombie.iso.BrokenFences;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.util.Type;
import zombie.util.io.BitHeader;
import zombie.util.io.BitHeaderRead;
import zombie.util.io.BitHeaderWrite;
import zombie.world.WorldDictionary;

public class IsoThumpable extends IsoObject implements BarricadeAble, Thumpable {
    private KahluaTable table;
    private KahluaTable modData;
    public Boolean isDoor = false;
    public Boolean isDoorFrame = false;
    public String breakSound = "BreakObject";
    private boolean isCorner = false;
    private boolean isFloor = false;
    private boolean blockAllTheSquare = false;
    public boolean Locked = false;
    public int MaxHealth = 500;
    public int Health = 500;
    public int PushedMaxStrength = 0;
    public int PushedStrength = 0;
    IsoSprite closedSprite;
    public boolean north = false;
    private int thumpDmg = 8;
    private float crossSpeed = 1.0F;
    public boolean open = false;
    IsoSprite openSprite;
    private boolean destroyed = false;
    private boolean canBarricade = false;
    public boolean canPassThrough = false;
    private boolean isStairs = false;
    private boolean isContainer = false;
    private boolean dismantable = false;
    private boolean canBePlastered = false;
    private boolean paintable = false;
    private boolean isThumpable = true;
    private boolean isHoppable = false;
    private int lightSourceRadius = -1;
    private int lightSourceLife = -1;
    private int lightSourceXOffset = 0;
    private int lightSourceYOffset = 0;
    private boolean lightSourceOn = false;
    private IsoLightSource lightSource = null;
    private String lightSourceFuel = null;
    private float lifeLeft = -1.0F;
    private float lifeDelta = 0.0F;
    private boolean haveFuel = false;
    private float updateAccumulator = 0.0F;
    private float lastUpdateHours = -1.0F;
    public int keyId = -1;
    private boolean lockedByKey = false;
    public boolean lockedByPadlock = false;
    private boolean canBeLockByPadlock = false;
    public int lockedByCode = 0;
    public int OldNumPlanks = 0;
    public String thumpSound = "ZombieThumpGeneric";
    public static final Vector2 tempo = new Vector2();

    @Override
    public KahluaTable getModData() {
        if (this.modData == null) {
            this.modData = LuaManager.platform.newTable();
        }

        return this.modData;
    }

    public void setModData(KahluaTable _modData) {
        this.modData = _modData;
    }

    @Override
    public boolean hasModData() {
        return this.modData != null && !this.modData.isEmpty();
    }

    /**
     * Can you pass through the item, if false we gonna test the collide default to false (so it collide)
     */
    public boolean isCanPassThrough() {
        return this.canPassThrough;
    }

    public void setCanPassThrough(boolean pCanPassThrough) {
        this.canPassThrough = pCanPassThrough;
    }

    public boolean isBlockAllTheSquare() {
        return this.blockAllTheSquare;
    }

    public void setBlockAllTheSquare(boolean _blockAllTheSquare) {
        this.blockAllTheSquare = _blockAllTheSquare;
    }

    public void setIsDismantable(boolean _dismantable) {
        this.dismantable = _dismantable;
    }

    public boolean isDismantable() {
        return this.dismantable;
    }

    public float getCrossSpeed() {
        return this.crossSpeed;
    }

    public void setCrossSpeed(float pCrossSpeed) {
        this.crossSpeed = pCrossSpeed;
    }

    public void setIsFloor(boolean pIsFloor) {
        this.isFloor = pIsFloor;
    }

    public boolean isCorner() {
        return this.isCorner;
    }

    @Override
    public boolean isFloor() {
        return this.isFloor;
    }

    public void setIsContainer(boolean pIsContainer) {
        this.isContainer = pIsContainer;
        if (pIsContainer) {
            this.container = new ItemContainer("crate", this.square, this);
            if (this.sprite.getProperties().Is("ContainerCapacity")) {
                this.container.Capacity = Integer.parseInt(this.sprite.getProperties().Val("ContainerCapacity"));
            }

            this.container.setExplored(true);
        }
    }

    public void setIsStairs(boolean pStairs) {
        this.isStairs = pStairs;
    }

    public boolean isStairs() {
        return this.isStairs;
    }

    public boolean isWindow() {
        return this.sprite != null && (this.sprite.getProperties().Is(IsoFlagType.WindowN) || this.sprite.getProperties().Is(IsoFlagType.WindowW));
    }

    @Override
    public String getObjectName() {
        return "Thumpable";
    }

    public IsoThumpable(IsoCell cell) {
        super(cell);
    }

    public void setCorner(boolean pCorner) {
        this.isCorner = pCorner;
    }

    /**
     * Can you barricade/unbarricade the item default true
     */
    public void setCanBarricade(boolean pCanBarricade) {
        this.canBarricade = pCanBarricade;
    }

    /**
     * Can you barricade/unbarricade the item
     */
    public boolean getCanBarricade() {
        return this.canBarricade;
    }

    public void setHealth(int health) {
        this.Health = health;
    }

    public int getHealth() {
        return this.Health;
    }

    public void setMaxHealth(int maxHealth) {
        this.MaxHealth = maxHealth;
    }

    public int getMaxHealth() {
        return this.MaxHealth;
    }

    /**
     * Numbers of zeds need to hurt the object default 8
     */
    public void setThumpDmg(Integer pThumpDmg) {
        this.thumpDmg = pThumpDmg;
    }

    public int getThumpDmg() {
        return this.thumpDmg;
    }

    /**
     * The sound that be played if this object is broken default "BreakDoor"
     */
    public void setBreakSound(String pBreakSound) {
        this.breakSound = pBreakSound;
    }

    public String getBreakSound() {
        return this.breakSound;
    }

    public boolean isDoor() {
        return this.isDoor;
    }

    @Override
    public boolean getNorth() {
        return this.north;
    }

    @Override
    public Vector2 getFacingPosition(Vector2 pos) {
        if (this.square == null) {
            return pos.set(0.0F, 0.0F);
        } else if (this.isDoor
            || this.isDoorFrame
            || this.isWindow()
            || this.isHoppable
            || this.getProperties() != null && (this.getProperties().Is(IsoFlagType.collideN) || this.getProperties().Is(IsoFlagType.collideW))) {
            return this.north ? pos.set(this.getX() + 0.5F, this.getY()) : pos.set(this.getX(), this.getY() + 0.5F);
        } else {
            return pos.set(this.getX() + 0.5F, this.getY() + 0.5F);
        }
    }

    public boolean isDoorFrame() {
        return this.isDoorFrame;
    }

    public void setIsDoor(boolean pIsDoor) {
        this.isDoor = pIsDoor;
    }

    public void setIsDoorFrame(boolean pIsDoorFrame) {
        this.isDoorFrame = pIsDoorFrame;
    }

    @Override
    public void setSprite(String sprite) {
        this.closedSprite = IsoSpriteManager.instance.getSprite(sprite);
        this.sprite = this.closedSprite;
    }

    @Override
    public void setSpriteFromName(String name) {
        this.sprite = IsoSpriteManager.instance.getSprite(name);
    }

    public void setClosedSprite(IsoSprite sprite) {
        this.closedSprite = sprite;
        this.sprite = this.closedSprite;
    }

    public void setOpenSprite(IsoSprite sprite) {
        this.openSprite = sprite;
    }

    /**
     * Create an object than can be interacted by you, survivor or zombie (destroy, barricade, etc.) This one have a closed/openSprite so it can be a  door for example
     */
    public IsoThumpable(IsoCell cell, IsoGridSquare gridSquare, String _closedSprite, String _openSprite, boolean _north, KahluaTable _table) {
        this.OutlineOnMouseover = true;
        this.PushedMaxStrength = this.PushedStrength = 2500;
        this.openSprite = IsoSpriteManager.instance.getSprite(_openSprite);
        this.closedSprite = IsoSpriteManager.instance.getSprite(_closedSprite);
        this.table = _table;
        this.sprite = this.closedSprite;
        this.square = gridSquare;
        this.north = _north;
    }

    /**
     * Create an object than can be interacted by you, survivor or zombie (destroy, barricade, etc.) This one can be a wall, a fence, etc.
     */
    public IsoThumpable(IsoCell cell, IsoGridSquare gridSquare, String sprite, boolean _north, KahluaTable _table) {
        this.OutlineOnMouseover = true;
        this.PushedMaxStrength = this.PushedStrength = 2500;
        this.closedSprite = IsoSpriteManager.instance.getSprite(sprite);
        this.table = _table;
        this.sprite = this.closedSprite;
        this.square = gridSquare;
        this.north = _north;
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(input, WorldVersion, IS_DEBUG_SAVE);
        BitHeaderRead bitHeaderRead = BitHeader.allocRead(BitHeader.HeaderSize.Long, input);
        this.OutlineOnMouseover = true;
        this.PushedMaxStrength = this.PushedStrength = 2500;
        if (!bitHeaderRead.equals(0)) {
            this.open = bitHeaderRead.hasFlags(1);
            this.Locked = bitHeaderRead.hasFlags(2);
            this.north = bitHeaderRead.hasFlags(4);
            if (bitHeaderRead.hasFlags(8)) {
                this.MaxHealth = input.getInt();
            }

            if (bitHeaderRead.hasFlags(16)) {
                this.Health = input.getInt();
            } else {
                this.Health = this.MaxHealth;
            }

            if (bitHeaderRead.hasFlags(32)) {
                this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, input.getInt());
            }

            if (bitHeaderRead.hasFlags(64)) {
                this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, input.getInt());
            }

            if (bitHeaderRead.hasFlags(128)) {
                this.thumpDmg = input.getInt();
            }

            this.isDoor = bitHeaderRead.hasFlags(512);
            this.isDoorFrame = bitHeaderRead.hasFlags(1024);
            this.isCorner = bitHeaderRead.hasFlags(2048);
            this.isStairs = bitHeaderRead.hasFlags(4096);
            this.isContainer = bitHeaderRead.hasFlags(8192);
            this.isFloor = bitHeaderRead.hasFlags(16384);
            this.canBarricade = bitHeaderRead.hasFlags(32768);
            this.canPassThrough = bitHeaderRead.hasFlags(65536);
            this.dismantable = bitHeaderRead.hasFlags(131072);
            this.canBePlastered = bitHeaderRead.hasFlags(262144);
            this.paintable = bitHeaderRead.hasFlags(524288);
            if (bitHeaderRead.hasFlags(1048576)) {
                this.crossSpeed = input.getFloat();
            }

            if (bitHeaderRead.hasFlags(2097152)) {
                if (this.table == null) {
                    this.table = LuaManager.platform.newTable();
                }

                this.table.load(input, WorldVersion);
            }

            if (bitHeaderRead.hasFlags(4194304)) {
                if (this.modData == null) {
                    this.modData = LuaManager.platform.newTable();
                }

                this.modData.load(input, WorldVersion);
            }

            this.blockAllTheSquare = bitHeaderRead.hasFlags(8388608);
            this.isThumpable = bitHeaderRead.hasFlags(16777216);
            this.isHoppable = bitHeaderRead.hasFlags(33554432);
            if (bitHeaderRead.hasFlags(67108864)) {
                this.setLightSourceLife(input.getInt());
            }

            if (bitHeaderRead.hasFlags(134217728)) {
                this.setLightSourceRadius(input.getInt());
            }

            if (bitHeaderRead.hasFlags(268435456)) {
                this.setLightSourceXOffset(input.getInt());
            }

            if (bitHeaderRead.hasFlags(536870912)) {
                this.setLightSourceYOffset(input.getInt());
            }

            if (bitHeaderRead.hasFlags(1073741824)) {
                this.setLightSourceFuel(WorldDictionary.getItemTypeFromID(input.getShort()));
            }

            if (bitHeaderRead.hasFlags(2147483648L)) {
                this.setLifeDelta(input.getFloat());
            }

            if (bitHeaderRead.hasFlags(4294967296L)) {
                this.setLifeLeft(input.getFloat());
            }

            if (bitHeaderRead.hasFlags(8589934592L)) {
                this.keyId = input.getInt();
            }

            this.lockedByKey = bitHeaderRead.hasFlags(17179869184L);
            this.lockedByPadlock = bitHeaderRead.hasFlags(34359738368L);
            this.canBeLockByPadlock = bitHeaderRead.hasFlags(68719476736L);
            if (bitHeaderRead.hasFlags(137438953472L)) {
                this.lockedByCode = input.getInt();
            }

            if (bitHeaderRead.hasFlags(274877906944L)) {
                this.thumpSound = GameWindow.ReadString(input);
                if ("thumpa2".equals(this.thumpSound)) {
                    this.thumpSound = "ZombieThumpGeneric";
                }

                if ("metalthump".equals(this.thumpSound)) {
                    this.thumpSound = "ZombieThumpMetal";
                }
            }

            if (bitHeaderRead.hasFlags(549755813888L)) {
                this.lastUpdateHours = input.getFloat();
            }

            if (WorldVersion >= 183) {
                if (bitHeaderRead.hasFlags(1099511627776L)) {
                    this.haveFuel = true;
                }

                if (bitHeaderRead.hasFlags(2199023255552L)) {
                    this.lightSourceOn = true;
                }
            }
        }

        bitHeaderRead.release();
        if (this.getLightSourceFuel() != null) {
            boolean boolean0 = this.isLightSourceOn();
            this.createLightSource(
                this.getLightSourceRadius(),
                this.getLightSourceXOffset(),
                this.getLightSourceYOffset(),
                0,
                this.getLightSourceLife(),
                this.getLightSourceFuel(),
                null,
                null
            );
            if (this.lightSource != null) {
                this.getLightSource().setActive(boolean0);
            }

            this.setLightSourceOn(boolean0);
        }

        if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
            GameClient.instance.objectSyncReq.putRequestLoad(this.square);
        }
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        super.save(output, IS_DEBUG_SAVE);
        BitHeaderWrite bitHeaderWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Long, output);
        if (this.open) {
            bitHeaderWrite.addFlags(1);
        }

        if (this.Locked) {
            bitHeaderWrite.addFlags(2);
        }

        if (this.north) {
            bitHeaderWrite.addFlags(4);
        }

        if (this.MaxHealth != 500) {
            bitHeaderWrite.addFlags(8);
            output.putInt(this.MaxHealth);
        }

        if (this.Health != this.MaxHealth) {
            bitHeaderWrite.addFlags(16);
            output.putInt(this.Health);
        }

        if (this.closedSprite != null) {
            bitHeaderWrite.addFlags(32);
            output.putInt(this.closedSprite.ID);
        }

        if (this.openSprite != null) {
            bitHeaderWrite.addFlags(64);
            output.putInt(this.openSprite.ID);
        }

        if (this.thumpDmg != 8) {
            bitHeaderWrite.addFlags(128);
            output.putInt(this.thumpDmg);
        }

        if (this.isDoor) {
            bitHeaderWrite.addFlags(512);
        }

        if (this.isDoorFrame) {
            bitHeaderWrite.addFlags(1024);
        }

        if (this.isCorner) {
            bitHeaderWrite.addFlags(2048);
        }

        if (this.isStairs) {
            bitHeaderWrite.addFlags(4096);
        }

        if (this.isContainer) {
            bitHeaderWrite.addFlags(8192);
        }

        if (this.isFloor) {
            bitHeaderWrite.addFlags(16384);
        }

        if (this.canBarricade) {
            bitHeaderWrite.addFlags(32768);
        }

        if (this.canPassThrough) {
            bitHeaderWrite.addFlags(65536);
        }

        if (this.dismantable) {
            bitHeaderWrite.addFlags(131072);
        }

        if (this.canBePlastered) {
            bitHeaderWrite.addFlags(262144);
        }

        if (this.paintable) {
            bitHeaderWrite.addFlags(524288);
        }

        if (this.crossSpeed != 1.0F) {
            bitHeaderWrite.addFlags(1048576);
            output.putFloat(this.crossSpeed);
        }

        if (this.table != null && !this.table.isEmpty()) {
            bitHeaderWrite.addFlags(2097152);
            this.table.save(output);
        }

        if (this.modData != null && !this.modData.isEmpty()) {
            bitHeaderWrite.addFlags(4194304);
            this.modData.save(output);
        }

        if (this.blockAllTheSquare) {
            bitHeaderWrite.addFlags(8388608);
        }

        if (this.isThumpable) {
            bitHeaderWrite.addFlags(16777216);
        }

        if (this.isHoppable) {
            bitHeaderWrite.addFlags(33554432);
        }

        if (this.getLightSourceLife() != -1) {
            bitHeaderWrite.addFlags(67108864);
            output.putInt(this.getLightSourceLife());
        }

        if (this.getLightSourceRadius() != -1) {
            bitHeaderWrite.addFlags(134217728);
            output.putInt(this.getLightSourceRadius());
        }

        if (this.getLightSourceXOffset() != 0) {
            bitHeaderWrite.addFlags(268435456);
            output.putInt(this.getLightSourceXOffset());
        }

        if (this.getLightSourceYOffset() != 0) {
            bitHeaderWrite.addFlags(536870912);
            output.putInt(this.getLightSourceYOffset());
        }

        if (this.getLightSourceFuel() != null) {
            bitHeaderWrite.addFlags(1073741824);
            output.putShort(WorldDictionary.getItemRegistryID(this.getLightSourceFuel()));
        }

        if (this.getLifeDelta() != 0.0F) {
            bitHeaderWrite.addFlags(2147483648L);
            output.putFloat(this.getLifeDelta());
        }

        if (this.getLifeLeft() != -1.0F) {
            bitHeaderWrite.addFlags(4294967296L);
            output.putFloat(this.getLifeLeft());
        }

        if (this.keyId != -1) {
            bitHeaderWrite.addFlags(8589934592L);
            output.putInt(this.keyId);
        }

        if (this.isLockedByKey()) {
            bitHeaderWrite.addFlags(17179869184L);
        }

        if (this.isLockedByPadlock()) {
            bitHeaderWrite.addFlags(34359738368L);
        }

        if (this.canBeLockByPadlock()) {
            bitHeaderWrite.addFlags(68719476736L);
        }

        if (this.getLockedByCode() != 0) {
            bitHeaderWrite.addFlags(137438953472L);
            output.putInt(this.getLockedByCode());
        }

        if (!this.thumpSound.equals("ZombieThumbGeneric")) {
            bitHeaderWrite.addFlags(274877906944L);
            GameWindow.WriteString(output, this.thumpSound);
        }

        if (this.lastUpdateHours != -1.0F) {
            bitHeaderWrite.addFlags(549755813888L);
            output.putFloat(this.lastUpdateHours);
        }

        if (this.haveFuel) {
            bitHeaderWrite.addFlags(1099511627776L);
        }

        if (this.lightSourceOn) {
            bitHeaderWrite.addFlags(2199023255552L);
        }

        bitHeaderWrite.write();
        bitHeaderWrite.release();
    }

    @Override
    public boolean isDestroyed() {
        return this.destroyed;
    }

    public boolean IsOpen() {
        return this.open;
    }

    public boolean IsStrengthenedByPushedItems() {
        return false;
    }

    @Override
    public boolean onMouseLeftClick(int x, int y) {
        return false;
    }

    @Override
    public boolean TestPathfindCollide(IsoMovingObject obj, IsoGridSquare from, IsoGridSquare to) {
        boolean boolean0 = this.north;
        if (obj instanceof IsoSurvivor && ((IsoSurvivor)obj).getInventory().contains("Hammer")) {
            return false;
        } else if (this.open) {
            return false;
        } else {
            if (from == this.square) {
                if (boolean0 && to.getY() < from.getY()) {
                    return true;
                }

                if (!boolean0 && to.getX() < from.getX()) {
                    return true;
                }
            } else {
                if (boolean0 && to.getY() > from.getY()) {
                    return true;
                }

                if (!boolean0 && to.getX() > from.getX()) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public boolean TestCollide(IsoMovingObject obj, IsoGridSquare from, IsoGridSquare to) {
        if (obj instanceof IsoPlayer && ((IsoPlayer)obj).isNoClip()) {
            return false;
        } else {
            boolean boolean0 = this.north;
            if (this.open) {
                return false;
            } else if (this.blockAllTheSquare) {
                if (from != this.square) {
                    if (obj != null) {
                        obj.collideWith(this);
                    }

                    return true;
                } else {
                    return false;
                }
            } else {
                if (from == this.square) {
                    if (boolean0 && to.getY() < from.getY()) {
                        if (obj != null) {
                            obj.collideWith(this);
                        }

                        if (!this.canPassThrough && !this.isStairs && !this.isCorner) {
                            return true;
                        }
                    }

                    if (!boolean0 && to.getX() < from.getX()) {
                        if (obj != null) {
                            obj.collideWith(this);
                        }

                        if (!this.canPassThrough && !this.isStairs && !this.isCorner) {
                            return true;
                        }
                    }
                } else {
                    if (boolean0 && to.getY() > from.getY()) {
                        if (obj != null) {
                            obj.collideWith(this);
                        }

                        if (!this.canPassThrough && !this.isStairs && !this.isCorner) {
                            return true;
                        }
                    }

                    if (!boolean0 && to.getX() > from.getX()) {
                        if (obj != null) {
                            obj.collideWith(this);
                        }

                        if (!this.canPassThrough && !this.isStairs && !this.isCorner) {
                            return true;
                        }
                    }
                }

                if (this.isCorner) {
                    if (to.getY() < from.getY() && to.getX() < from.getX()) {
                        if (obj != null) {
                            obj.collideWith(this);
                        }

                        if (!this.canPassThrough) {
                            return true;
                        }
                    }

                    if (to.getY() > from.getY() && to.getX() > from.getX()) {
                        if (obj != null) {
                            obj.collideWith(this);
                        }

                        if (!this.canPassThrough) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }
    }

    @Override
    public IsoObject.VisionResult TestVision(IsoGridSquare from, IsoGridSquare to) {
        if (this.canPassThrough) {
            return IsoObject.VisionResult.NoEffect;
        } else {
            boolean boolean0 = this.north;
            if (this.open) {
                boolean0 = !boolean0;
            }

            if (to.getZ() != from.getZ()) {
                return IsoObject.VisionResult.NoEffect;
            } else {
                boolean boolean1 = this.sprite != null && this.sprite.getProperties().Is("doorTrans");
                if (from == this.square) {
                    if (boolean0 && to.getY() < from.getY()) {
                        if (boolean1) {
                            return IsoObject.VisionResult.Unblocked;
                        }

                        if (this.isWindow()) {
                            return IsoObject.VisionResult.Unblocked;
                        }

                        return IsoObject.VisionResult.Blocked;
                    }

                    if (!boolean0 && to.getX() < from.getX()) {
                        if (boolean1) {
                            return IsoObject.VisionResult.Unblocked;
                        }

                        if (this.isWindow()) {
                            return IsoObject.VisionResult.Unblocked;
                        }

                        return IsoObject.VisionResult.Blocked;
                    }
                } else {
                    if (boolean0 && to.getY() > from.getY()) {
                        if (boolean1) {
                            return IsoObject.VisionResult.Unblocked;
                        }

                        if (this.isWindow()) {
                            return IsoObject.VisionResult.Unblocked;
                        }

                        return IsoObject.VisionResult.Blocked;
                    }

                    if (!boolean0 && to.getX() > from.getX()) {
                        if (boolean1) {
                            return IsoObject.VisionResult.Unblocked;
                        }

                        if (this.isWindow()) {
                            return IsoObject.VisionResult.Unblocked;
                        }

                        return IsoObject.VisionResult.Blocked;
                    }
                }

                return IsoObject.VisionResult.NoEffect;
            }
        }
    }

    @Override
    public void Thump(IsoMovingObject thumper) {
        if (SandboxOptions.instance.Lore.ThumpOnConstruction.getValue()) {
            if (thumper instanceof IsoGameCharacter) {
                Thumpable thumpable = this.getThumpableFor((IsoGameCharacter)thumper);
                if (thumpable == null) {
                    return;
                }

                if (thumpable != this) {
                    thumpable.Thump(thumper);
                    return;
                }
            }

            boolean boolean0 = BrokenFences.getInstance().isBreakableObject(this);
            if (thumper instanceof IsoZombie) {
                if (((IsoZombie)thumper).cognition == 1 && this.isDoor() && !this.IsOpen() && !this.isLocked()) {
                    this.ToggleDoor((IsoGameCharacter)thumper);
                    return;
                }

                int int0 = thumper.getCurrentSquare().getMovingObjects().size();
                if (thumper.getCurrentSquare().getW() != null) {
                    int0 += thumper.getCurrentSquare().getW().getMovingObjects().size();
                }

                if (thumper.getCurrentSquare().getE() != null) {
                    int0 += thumper.getCurrentSquare().getE().getMovingObjects().size();
                }

                if (thumper.getCurrentSquare().getS() != null) {
                    int0 += thumper.getCurrentSquare().getS().getMovingObjects().size();
                }

                if (thumper.getCurrentSquare().getN() != null) {
                    int0 += thumper.getCurrentSquare().getN().getMovingObjects().size();
                }

                int int1 = this.thumpDmg;
                if (int0 >= int1) {
                    int int2 = 1 * ThumpState.getFastForwardDamageMultiplier();
                    this.Health -= int2;
                } else {
                    this.partialThumpDmg = this.partialThumpDmg + (float)int0 / int1 * ThumpState.getFastForwardDamageMultiplier();
                    if ((int)this.partialThumpDmg > 0) {
                        int int3 = (int)this.partialThumpDmg;
                        this.Health -= int3;
                        this.partialThumpDmg -= int3;
                    }
                }

                WorldSoundManager.instance.addSound(thumper, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
                if (this.isDoor()) {
                    this.setRenderEffect(RenderEffectType.Hit_Door, true);
                }
            }

            if (this.Health <= 0) {
                ((IsoGameCharacter)thumper).getEmitter().playSound(this.breakSound, this);
                if (GameServer.bServer) {
                    GameServer.PlayWorldSoundServer((IsoGameCharacter)thumper, this.breakSound, false, thumper.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
                }

                WorldSoundManager.instance.addSound(null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
                thumper.setThumpTarget(null);
                if (IsoDoor.destroyDoubleDoor(this)) {
                    return;
                }

                if (IsoDoor.destroyGarageDoor(this)) {
                    return;
                }

                if (boolean0) {
                    PropertyContainer propertyContainer = this.getProperties();
                    IsoDirections directions;
                    if (propertyContainer.Is(IsoFlagType.collideN) && propertyContainer.Is(IsoFlagType.collideW)) {
                        directions = thumper.getY() >= this.getY() ? IsoDirections.N : IsoDirections.S;
                    } else if (propertyContainer.Is(IsoFlagType.collideN)) {
                        directions = thumper.getY() >= this.getY() ? IsoDirections.N : IsoDirections.S;
                    } else {
                        directions = thumper.getX() >= this.getX() ? IsoDirections.W : IsoDirections.E;
                    }

                    BrokenFences.getInstance().destroyFence(this, directions);
                    return;
                }

                this.destroy();
            }
        }
    }

    @Override
    public Thumpable getThumpableFor(IsoGameCharacter chr) {
        if (this.isDoor() || this.isWindow()) {
            IsoBarricade barricade = this.getBarricadeForCharacter(chr);
            if (barricade != null) {
                return barricade;
            }

            barricade = this.getBarricadeOppositeCharacter(chr);
            if (barricade != null) {
                return barricade;
            }
        }

        boolean boolean0 = this.isThumpable;
        boolean boolean1 = chr instanceof IsoZombie && ((IsoZombie)chr).isCrawling();
        if (!boolean0 && boolean1 && BrokenFences.getInstance().isBreakableObject(this)) {
            boolean0 = true;
        }

        if (!boolean0 && boolean1 && this.isHoppable()) {
            boolean0 = true;
        }

        if (boolean0 && !this.isDestroyed()) {
            if ((!this.isDoor() || !this.IsOpen()) && !this.isWindow()) {
                return !boolean1 && this.isHoppable() ? null : this;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public float getThumpCondition() {
        return (float)PZMath.clamp(this.Health, 0, this.MaxHealth) / this.MaxHealth;
    }

    @Override
    public void WeaponHit(IsoGameCharacter owner, HandWeapon weapon) {
        IsoPlayer player = Type.tryCastTo(owner, IsoPlayer.class);
        if (GameClient.bClient) {
            if (player != null) {
                GameClient.instance.sendWeaponHit(player, weapon, this);
            }

            if (this.isDoor()) {
                this.setRenderEffect(RenderEffectType.Hit_Door, true);
            }
        } else {
            Thumpable thumpable = this.getThumpableFor(owner);
            if (thumpable != null) {
                if (thumpable instanceof IsoBarricade) {
                    ((IsoBarricade)thumpable).WeaponHit(owner, weapon);
                } else {
                    LuaEventManager.triggerEvent("OnWeaponHitThumpable", owner, weapon, this);
                    this.Damage(weapon.getDoorDamage());
                    if (weapon.getDoorHitSound() != null) {
                        if (player != null) {
                            player.setMeleeHitSurface(this.getSoundPrefix());
                        }

                        owner.getEmitter().playSound(weapon.getDoorHitSound(), this);
                        if (GameServer.bServer) {
                            GameServer.PlayWorldSoundServer(owner, weapon.getDoorHitSound(), false, this.getSquare(), 0.2F, 20.0F, 1.0F, false);
                        }
                    }

                    WorldSoundManager.instance.addSound(owner, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
                    if (this.isDoor()) {
                        this.setRenderEffect(RenderEffectType.Hit_Door, true);
                    }

                    if (!this.IsStrengthenedByPushedItems() && this.Health <= 0 || this.IsStrengthenedByPushedItems() && this.Health <= -this.PushedMaxStrength
                        )
                     {
                        owner.getEmitter().playSound(this.breakSound, this);
                        WorldSoundManager.instance.addSound(owner, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
                        if (GameClient.bClient) {
                            GameClient.instance
                                .sendClientCommandV(
                                    null,
                                    "object",
                                    "OnDestroyIsoThumpable",
                                    "x",
                                    (int)this.getX(),
                                    "y",
                                    (int)this.getY(),
                                    "z",
                                    (int)this.getZ(),
                                    "index",
                                    this.getObjectIndex()
                                );
                        }

                        LuaEventManager.triggerEvent("OnDestroyIsoThumpable", this, null);
                        if (IsoDoor.destroyDoubleDoor(this)) {
                            return;
                        }

                        if (IsoDoor.destroyGarageDoor(this)) {
                            return;
                        }

                        this.destroyed = true;
                        if (this.getObjectIndex() != -1) {
                            this.square.transmitRemoveItemFromSquare(this);
                        }
                    }
                }
            }
        }
    }

    public IsoGridSquare getOtherSideOfDoor(IsoGameCharacter chr) {
        if (this.north) {
            return chr.getCurrentSquare().getRoom() == this.square.getRoom()
                ? IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ())
                : IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ());
        } else {
            return chr.getCurrentSquare().getRoom() == this.square.getRoom()
                ? IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ())
                : IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ());
        }
    }

    public void ToggleDoorActual(IsoGameCharacter chr) {
        if (this.isBarricaded()) {
            if (chr != null) {
                this.playDoorSound(chr.getEmitter(), "Blocked");
                chr.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBarricaded"), 255, 255, 255, 256.0F);
                this.setRenderEffect(RenderEffectType.Hit_Door, true);
            }
        } else if (this.isLockedByKey()
            && chr instanceof IsoPlayer
            && chr.getCurrentSquare().Is(IsoFlagType.exterior)
            && chr.getInventory().haveThisKeyId(this.getKeyId()) == null) {
            this.playDoorSound(chr.getEmitter(), "Locked");
            this.setRenderEffect(RenderEffectType.Hit_Door, true);
        } else {
            if (this.isLockedByKey() && chr instanceof IsoPlayer && chr.getInventory().haveThisKeyId(this.getKeyId()) != null) {
                this.playDoorSound(chr.getEmitter(), "Unlock");
                this.setIsLocked(false);
                this.setLockedByKey(false);
            }

            this.DirtySlice();
            this.square.InvalidateSpecialObjectPaths();
            if (this.Locked && chr instanceof IsoPlayer && chr.getCurrentSquare().Is(IsoFlagType.exterior) && !this.open) {
                this.playDoorSound(chr.getEmitter(), "Locked");
                this.setRenderEffect(RenderEffectType.Hit_Door, true);
            } else {
                if (chr instanceof IsoPlayer) {
                }

                for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                    LosUtil.cachecleared[int0] = true;
                }

                IsoGridSquare.setRecalcLightTime(-1);
                GameTime.instance.lightSourceUpdate = 100.0F;
                if (this.getSprite().getProperties().Is("DoubleDoor")) {
                    if (IsoDoor.isDoubleDoorObstructed(this)) {
                        if (chr != null) {
                            this.playDoorSound(chr.getEmitter(), "Blocked");
                            chr.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBlocked"), 255, 255, 255, 256.0F);
                        }
                    } else {
                        boolean boolean0 = this.open;
                        IsoDoor.toggleDoubleDoor(this, true);
                        if (boolean0 != this.open) {
                            this.playDoorSound(chr.getEmitter(), this.open ? "Open" : "Close");
                        }
                    }
                } else if (this.isObstructed()) {
                    if (chr != null) {
                        this.playDoorSound(chr.getEmitter(), "Blocked");
                        chr.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBlocked"), 255, 255, 255, 256.0F);
                    }
                } else {
                    this.sprite = this.closedSprite;
                    this.open = !this.open;
                    this.setLockedByKey(false);
                    if (this.open) {
                        this.playDoorSound(chr.getEmitter(), "Open");
                        this.sprite = this.openSprite;
                    } else {
                        this.playDoorSound(chr.getEmitter(), "Close");
                    }

                    this.square.RecalcProperties();
                    this.syncIsoObject(false, (byte)(this.open ? 1 : 0), null, null);
                    LuaEventManager.triggerEvent("OnContainerUpdate");
                }
            }
        }
    }

    public void ToggleDoor(IsoGameCharacter chr) {
        this.ToggleDoorActual(chr);
    }

    public void ToggleDoorSilent() {
        if (!this.isBarricaded()) {
            this.square.InvalidateSpecialObjectPaths();

            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                LosUtil.cachecleared[int0] = true;
            }

            IsoGridSquare.setRecalcLightTime(-1);
            this.open = !this.open;
            this.sprite = this.closedSprite;
            if (this.open) {
                this.sprite = this.openSprite;
            }
        }
    }

    public boolean isObstructed() {
        return IsoDoor.isDoorObstructed(this);
    }

    @Override
    public boolean haveSheetRope() {
        return IsoWindow.isTopOfSheetRopeHere(this.square, this.north);
    }

    @Override
    public int countAddSheetRope() {
        return !this.isHoppable() && !this.isWindow() ? 0 : IsoWindow.countAddSheetRope(this.square, this.north);
    }

    @Override
    public boolean canAddSheetRope() {
        return !this.isHoppable() && !this.isWindow() ? false : IsoWindow.canAddSheetRope(this.square, this.north);
    }

    @Override
    public boolean addSheetRope(IsoPlayer player, String itemType) {
        return !this.canAddSheetRope() ? false : IsoWindow.addSheetRope(player, this.square, this.north, itemType);
    }

    @Override
    public boolean removeSheetRope(IsoPlayer player) {
        return this.haveSheetRope() ? IsoWindow.removeSheetRope(player, this.square, this.north) : false;
    }

    public void createLightSource(
        int radius, int offsetX, int offsetY, int offsetZ, int life, String _lightSourceFuel, InventoryItem baseItem, IsoGameCharacter chr
    ) {
        this.setLightSourceXOffset(offsetX);
        this.setLightSourceYOffset(offsetY);
        this.setLightSourceRadius(radius);
        this.setLightSourceFuel(_lightSourceFuel);
        if (baseItem != null) {
            if (!(baseItem instanceof DrainableComboItem)) {
                this.setLifeLeft(1.0F);
                this.setHaveFuel(true);
            } else {
                this.setLifeLeft(((DrainableComboItem)baseItem).getUsedDelta());
                this.setLifeDelta(((DrainableComboItem)baseItem).getUseDelta());
                this.setHaveFuel(!"Base.Torch".equals(baseItem.getFullType()) || ((DrainableComboItem)baseItem).getUsedDelta() > 0.0F);
            }

            chr.removeFromHands(baseItem);
            IsoWorldInventoryObject worldInventoryObject = baseItem.getWorldItem();
            if (worldInventoryObject != null) {
                if (worldInventoryObject.getSquare() != null) {
                    worldInventoryObject.getSquare().transmitRemoveItemFromSquare(worldInventoryObject);
                    LuaEventManager.triggerEvent("OnContainerUpdate");
                }
            } else if (baseItem.getContainer() != null) {
                baseItem.getContainer().Remove(baseItem);
            }
        }

        this.setLightSourceOn(this.haveFuel);
        if (this.lightSource != null) {
            this.lightSource.setActive(this.isLightSourceOn());
        }
    }

    public InventoryItem insertNewFuel(InventoryItem item, IsoGameCharacter chr) {
        if (item != null) {
            InventoryItem _item = this.removeCurrentFuel(chr);
            if (chr != null) {
                chr.removeFromHands(item);
                chr.getInventory().Remove(item);
            }

            if (item instanceof DrainableComboItem) {
                this.setLifeLeft(((DrainableComboItem)item).getUsedDelta());
                this.setLifeDelta(((DrainableComboItem)item).getUseDelta());
            } else {
                this.setLifeLeft(1.0F);
            }

            this.setHaveFuel(true);
            this.toggleLightSource(true);
            return _item;
        } else {
            return null;
        }
    }

    public InventoryItem removeCurrentFuel(IsoGameCharacter chr) {
        if (this.haveFuel()) {
            InventoryItem item = InventoryItemFactory.CreateItem(this.getLightSourceFuel());
            if (item instanceof DrainableComboItem) {
                ((DrainableComboItem)item).setUsedDelta(this.getLifeLeft());
            }

            if (chr != null) {
                chr.getInventory().AddItem(item);
            }

            this.setLifeLeft(0.0F);
            this.setLifeDelta(-1.0F);
            this.toggleLightSource(false);
            this.setHaveFuel(false);
            return item;
        } else {
            return null;
        }
    }

    private int calcLightSourceX() {
        int int0 = (int)this.getX();
        int int1 = (int)this.getY();
        if (this.lightSourceXOffset != 0) {
            for (int int2 = 1; int2 <= Math.abs(this.lightSourceXOffset); int2++) {
                int int3 = this.lightSourceXOffset > 0 ? 1 : -1;
                LosUtil.TestResults testResults = LosUtil.lineClear(
                    this.getCell(), (int)this.getX(), (int)this.getY(), (int)this.getZ(), int0 + int3, int1, (int)this.getZ(), false
                );
                if (testResults == LosUtil.TestResults.Blocked || testResults == LosUtil.TestResults.ClearThroughWindow) {
                    break;
                }

                int0 += int3;
            }
        }

        return int0;
    }

    private int calcLightSourceY() {
        int int0 = (int)this.getX();
        int int1 = (int)this.getY();
        if (this.lightSourceYOffset != 0) {
            for (int int2 = 1; int2 <= Math.abs(this.lightSourceYOffset); int2++) {
                int int3 = this.lightSourceYOffset > 0 ? 1 : -1;
                LosUtil.TestResults testResults = LosUtil.lineClear(
                    this.getCell(), (int)this.getX(), (int)this.getY(), (int)this.getZ(), int0, int1 + int3, (int)this.getZ(), false
                );
                if (testResults == LosUtil.TestResults.Blocked || testResults == LosUtil.TestResults.ClearThroughWindow) {
                    break;
                }

                int1 += int3;
            }
        }

        return int1;
    }

    @Override
    public void update() {
        if (this.getObjectIndex() != -1) {
            if (!GameServer.bServer) {
                if (this.lightSource != null && !this.lightSource.isInBounds()) {
                    this.lightSource = null;
                }

                if (this.lightSourceFuel != null && !this.lightSourceFuel.isEmpty() && this.lightSource == null && this.square != null) {
                    byte byte0 = 0;
                    int int0 = this.calcLightSourceX();
                    int int1 = this.calcLightSourceY();
                    if (IsoWorld.instance.CurrentCell.isInChunkMap(int0, int1)) {
                        int int2 = this.getLightSourceLife();
                        this.setLightSource(
                            new IsoLightSource(int0, int1, (int)this.getZ() + byte0, 1.0F, 1.0F, 1.0F, this.lightSourceRadius, int2 > 0 ? int2 : -1)
                        );
                        this.lightSource.setActive(this.isLightSourceOn());
                        IsoWorld.instance.getCell().getLamppostPositions().add(this.getLightSource());
                    }
                }

                if (this.lightSource != null && this.lightSource.isActive()) {
                    byte byte1 = 0;
                    int int3 = this.calcLightSourceX();
                    int int4 = this.calcLightSourceY();
                    if (int3 != this.lightSource.x || int4 != this.lightSource.y) {
                        this.getCell().removeLamppost(this.lightSource);
                        int int5 = this.getLightSourceLife();
                        this.setLightSource(
                            new IsoLightSource(int3, int4, (int)this.getZ() + byte1, 1.0F, 1.0F, 1.0F, this.lightSourceRadius, int5 > 0 ? int5 : -1)
                        );
                        this.lightSource.setActive(this.isLightSourceOn());
                        IsoWorld.instance.getCell().getLamppostPositions().add(this.getLightSource());
                    }
                }
            }

            if (this.getLifeLeft() > -1.0F) {
                float float0 = (float)GameTime.getInstance().getWorldAgeHours();
                if (this.lastUpdateHours == -1.0F) {
                    this.lastUpdateHours = float0;
                } else if (this.lastUpdateHours > float0) {
                    this.lastUpdateHours = float0;
                }

                float float1 = float0 - this.lastUpdateHours;
                this.lastUpdateHours = float0;
                if (this.isLightSourceOn()) {
                    this.updateAccumulator += float1;
                    int int6 = (int)Math.floor(this.updateAccumulator / 0.004166667F);
                    if (int6 > 0) {
                        this.updateAccumulator -= 0.004166667F * int6;
                        this.setLifeLeft(this.getLifeLeft() - this.getLifeDelta() * int6);
                        if (this.getLifeLeft() <= 0.0F) {
                            this.setLifeLeft(0.0F);
                            this.toggleLightSource(false);
                        }
                    }
                } else {
                    this.updateAccumulator = 0.0F;
                }
            }
        }
    }

    void Damage(int int0) {
        if (this.isThumpable()) {
            this.DirtySlice();
            this.Health -= int0;
        }
    }

    public void destroy() {
        if (!this.destroyed) {
            if (this.getObjectIndex() != -1) {
                if (GameClient.bClient) {
                    GameClient.instance
                        .sendClientCommandV(
                            null,
                            "object",
                            "OnDestroyIsoThumpable",
                            "x",
                            this.square.getX(),
                            "y",
                            this.square.getY(),
                            "z",
                            this.square.getZ(),
                            "index",
                            this.getObjectIndex()
                        );
                }

                LuaEventManager.triggerEvent("OnDestroyIsoThumpable", this, null);
                this.Health = 0;
                this.destroyed = true;
                if (this.getObjectIndex() != -1) {
                    this.square.transmitRemoveItemFromSquare(this);
                }
            }
        }
    }

    @Override
    public IsoBarricade getBarricadeOnSameSquare() {
        return IsoBarricade.GetBarricadeOnSquare(this.square, this.north ? IsoDirections.N : IsoDirections.W);
    }

    @Override
    public IsoBarricade getBarricadeOnOppositeSquare() {
        return IsoBarricade.GetBarricadeOnSquare(this.getOppositeSquare(), this.north ? IsoDirections.S : IsoDirections.E);
    }

    @Override
    public boolean isBarricaded() {
        IsoBarricade barricade = this.getBarricadeOnSameSquare();
        if (barricade == null) {
            barricade = this.getBarricadeOnOppositeSquare();
        }

        return barricade != null;
    }

    @Override
    public boolean isBarricadeAllowed() {
        return this.canBarricade;
    }

    @Override
    public IsoBarricade getBarricadeForCharacter(IsoGameCharacter chr) {
        return IsoBarricade.GetBarricadeForCharacter(this, chr);
    }

    @Override
    public IsoBarricade getBarricadeOppositeCharacter(IsoGameCharacter chr) {
        return IsoBarricade.GetBarricadeOppositeCharacter(this, chr);
    }

    public void setIsDoor(Boolean pIsDoor) {
        this.isDoor = pIsDoor;
    }

    /**
     * @return the table
     */
    @Override
    public KahluaTable getTable() {
        return this.table;
    }

    /**
     * 
     * @param _table the table to set
     */
    @Override
    public void setTable(KahluaTable _table) {
        this.table = _table;
    }

    public boolean canBePlastered() {
        return this.canBePlastered;
    }

    public void setCanBePlastered(boolean _canBePlastered) {
        this.canBePlastered = _canBePlastered;
    }

    public boolean isPaintable() {
        return this.paintable;
    }

    public void setPaintable(boolean _paintable) {
        this.paintable = _paintable;
    }

    public boolean isLocked() {
        return this.Locked;
    }

    public void setIsLocked(boolean lock) {
        this.Locked = lock;
    }

    public boolean isThumpable() {
        return this.isBarricaded() ? true : this.isThumpable;
    }

    public void setIsThumpable(boolean thumpable) {
        this.isThumpable = thumpable;
    }

    public void setIsHoppable(boolean _isHoppable) {
        this.setHoppable(_isHoppable);
    }

    public IsoSprite getOpenSprite() {
        return this.openSprite;
    }

    @Override
    public boolean isHoppable() {
        if (this.isDoor() && !this.IsOpen() && this.closedSprite != null) {
            PropertyContainer propertyContainer = this.closedSprite.getProperties();
            return propertyContainer.Is(IsoFlagType.HoppableN) || propertyContainer.Is(IsoFlagType.HoppableW);
        } else {
            return this.sprite == null || !this.sprite.getProperties().Is(IsoFlagType.HoppableN) && !this.sprite.getProperties().Is(IsoFlagType.HoppableW)
                ? this.isHoppable
                : true;
        }
    }

    public void setHoppable(boolean _isHoppable) {
        this.isHoppable = _isHoppable;
    }

    public int getLightSourceRadius() {
        return this.lightSourceRadius;
    }

    public void setLightSourceRadius(int _lightSourceRadius) {
        this.lightSourceRadius = _lightSourceRadius;
    }

    public int getLightSourceXOffset() {
        return this.lightSourceXOffset;
    }

    public void setLightSourceXOffset(int _lightSourceXOffset) {
        this.lightSourceXOffset = _lightSourceXOffset;
    }

    public int getLightSourceYOffset() {
        return this.lightSourceYOffset;
    }

    public void setLightSourceYOffset(int _lightSourceYOffset) {
        this.lightSourceYOffset = _lightSourceYOffset;
    }

    public int getLightSourceLife() {
        return this.lightSourceLife;
    }

    public void setLightSourceLife(int _lightSourceLife) {
        this.lightSourceLife = _lightSourceLife;
    }

    public boolean isLightSourceOn() {
        return this.lightSourceOn;
    }

    public void setLightSourceOn(boolean _lightSourceOn) {
        this.lightSourceOn = _lightSourceOn;
    }

    public IsoLightSource getLightSource() {
        return this.lightSource;
    }

    public void setLightSource(IsoLightSource _lightSource) {
        this.lightSource = _lightSource;
    }

    public void toggleLightSource(boolean toggle) {
        this.setLightSourceOn(toggle);
        if (this.lightSource != null) {
            this.getLightSource().setActive(toggle);
            IsoGridSquare.setRecalcLightTime(-1);
            GameTime.instance.lightSourceUpdate = 100.0F;
        }
    }

    public String getLightSourceFuel() {
        return this.lightSourceFuel;
    }

    public void setLightSourceFuel(String _lightSourceFuel) {
        if (_lightSourceFuel != null && _lightSourceFuel.isEmpty()) {
            _lightSourceFuel = null;
        }

        this.lightSourceFuel = _lightSourceFuel;
    }

    public float getLifeLeft() {
        return this.lifeLeft;
    }

    public void setLifeLeft(float _lifeLeft) {
        this.lifeLeft = _lifeLeft;
    }

    public float getLifeDelta() {
        return this.lifeDelta;
    }

    public void setLifeDelta(float _lifeDelta) {
        this.lifeDelta = _lifeDelta;
    }

    public boolean haveFuel() {
        return this.haveFuel;
    }

    public void setHaveFuel(boolean _haveFuel) {
        this.haveFuel = _haveFuel;
    }

    @Override
    public void syncIsoObjectSend(ByteBufferWriter b) {
        b.putInt(this.square.getX());
        b.putInt(this.square.getY());
        b.putInt(this.square.getZ());
        byte byte0 = (byte)this.square.getObjects().indexOf(this);
        b.putByte(byte0);
        b.putByte((byte)1);
        b.putByte((byte)0);
        b.putBoolean(this.open);
        b.putBoolean(this.Locked);
        b.putBoolean(this.lockedByKey);
    }

    @Override
    public void syncIsoObject(boolean bRemote, byte val, UdpConnection source, ByteBuffer bb) {
        if (this.square == null) {
            System.out.println("ERROR: " + this.getClass().getSimpleName() + " square is null");
        } else if (this.getObjectIndex() == -1) {
            System.out
                .println(
                    "ERROR: "
                        + this.getClass().getSimpleName()
                        + " not found on square "
                        + this.square.getX()
                        + ","
                        + this.square.getY()
                        + ","
                        + this.square.getZ()
                );
        } else if (this.isDoor()) {
            boolean boolean0 = bb != null && bb.get() == 1;
            boolean boolean1 = bb != null && bb.get() == 1;
            boolean boolean2 = bb != null && bb.get() == 1;
            short short0 = -1;
            if ((GameServer.bServer || GameClient.bClient) && bb != null) {
                short0 = bb.getShort();
            }

            if (GameClient.bClient && !bRemote) {
                short0 = IsoPlayer.getInstance().getOnlineID();
                ByteBufferWriter byteBufferWriter0 = GameClient.connection.startPacket();
                PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter0);
                this.syncIsoObjectSend(byteBufferWriter0);
                byteBufferWriter0.putShort(short0);
                PacketTypes.PacketType.SyncIsoObject.send(GameClient.connection);
            } else if (GameServer.bServer && !bRemote) {
                for (UdpConnection udpConnection0 : GameServer.udpEngine.connections) {
                    ByteBufferWriter byteBufferWriter1 = udpConnection0.startPacket();
                    PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter1);
                    this.syncIsoObjectSend(byteBufferWriter1);
                    byteBufferWriter1.putShort(short0);
                    PacketTypes.PacketType.SyncIsoObject.send(udpConnection0);
                }
            } else if (bRemote) {
                if (GameClient.bClient && short0 != -1) {
                    IsoPlayer player = GameClient.IDToPlayerMap.get(short0);
                    if (player != null) {
                        player.networkAI.setNoCollision(1000L);
                    }
                }

                if (IsoDoor.getDoubleDoorIndex(this) != -1) {
                    if (boolean0 != this.open) {
                        IsoDoor.toggleDoubleDoor(this, false);
                    }
                } else if (boolean0) {
                    this.open = true;
                    this.sprite = this.openSprite;
                } else {
                    this.open = false;
                    this.sprite = this.closedSprite;
                }

                this.Locked = boolean1;
                this.lockedByKey = boolean2;
                if (GameServer.bServer) {
                    for (UdpConnection udpConnection1 : GameServer.udpEngine.connections) {
                        if (source != null && udpConnection1.getConnectedGUID() != source.getConnectedGUID()) {
                            ByteBufferWriter byteBufferWriter2 = udpConnection1.startPacket();
                            PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter2);
                            this.syncIsoObjectSend(byteBufferWriter2);
                            byteBufferWriter2.putShort(short0);
                            PacketTypes.PacketType.SyncIsoObject.send(udpConnection1);
                        }
                    }
                }

                this.square.InvalidateSpecialObjectPaths();
                this.square.RecalcProperties();
                this.square.RecalcAllWithNeighbours(true);

                for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                    LosUtil.cachecleared[int0] = true;
                }

                IsoGridSquare.setRecalcLightTime(-1);
                GameTime.instance.lightSourceUpdate = 100.0F;
                LuaEventManager.triggerEvent("OnContainerUpdate");
            }
        }
    }

    @Override
    public void addToWorld() {
        super.addToWorld();
        this.getCell().addToProcessIsoObject(this);
    }

    @Override
    public void removeFromWorld() {
        if (this.lightSource != null) {
            IsoWorld.instance.CurrentCell.removeLamppost(this.lightSource);
        }

        super.removeFromWorld();
    }

    @Override
    public void saveChange(String change, KahluaTable tbl, ByteBuffer bb) {
        super.saveChange(change, tbl, bb);
        if ("lightSource".equals(change)) {
            bb.put((byte)(this.lightSourceOn ? 1 : 0));
            bb.put((byte)(this.haveFuel ? 1 : 0));
            bb.putFloat(this.lifeLeft);
            bb.putFloat(this.lifeDelta);
        } else if ("paintable".equals(change)) {
            bb.put((byte)(this.isPaintable() ? 1 : 0));
        }
    }

    @Override
    public void loadChange(String change, ByteBuffer bb) {
        super.loadChange(change, bb);
        if ("lightSource".equals(change)) {
            boolean boolean0 = bb.get() == 1;
            this.haveFuel = bb.get() == 1;
            this.lifeLeft = bb.getFloat();
            this.lifeDelta = bb.getFloat();
            if (boolean0 != this.lightSourceOn) {
                this.toggleLightSource(boolean0);
            }
        } else if ("paintable".equals(change)) {
            this.setPaintable(bb.get() == 1);
        }
    }

    public IsoCurtain HasCurtains() {
        IsoGridSquare square = this.getOppositeSquare();
        if (square != null) {
            IsoCurtain curtain = square.getCurtain(this.getNorth() ? IsoObjectType.curtainS : IsoObjectType.curtainE);
            if (curtain != null) {
                return curtain;
            }
        }

        return this.getSquare().getCurtain(this.getNorth() ? IsoObjectType.curtainN : IsoObjectType.curtainW);
    }

    public IsoGridSquare getInsideSquare() {
        return this.north
            ? this.square.getCell().getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ())
            : this.square.getCell().getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
    }

    @Override
    public IsoGridSquare getOppositeSquare() {
        return this.getInsideSquare();
    }

    public boolean isAdjacentToSquare(IsoGridSquare square2) {
        IsoGridSquare square0 = this.getSquare();
        if (square0 != null && square2 != null) {
            boolean boolean0 = !this.IsOpen();
            IsoGridSquare square1 = square0.getAdjacentSquare(IsoDirections.NW);
            IsoGridSquare _square2 = square0.getAdjacentSquare(IsoDirections.N);
            IsoGridSquare square3 = square0.getAdjacentSquare(IsoDirections.NE);
            IsoGridSquare square4 = square0.getAdjacentSquare(IsoDirections.W);
            IsoGridSquare square5 = square0.getAdjacentSquare(IsoDirections.E);
            IsoGridSquare square6 = square0.getAdjacentSquare(IsoDirections.SW);
            IsoGridSquare square7 = square0.getAdjacentSquare(IsoDirections.S);
            IsoGridSquare square8 = square0.getAdjacentSquare(IsoDirections.SE);
            switch (this.getSpriteEdge(false)) {
                case N:
                    if (square2 == square1) {
                        if (!square1.isWallTo(_square2)
                            && !square1.isWindowTo(_square2)
                            && !square1.hasDoorOnEdge(IsoDirections.E, false)
                            && !_square2.hasDoorOnEdge(IsoDirections.W, false)) {
                            if (_square2.hasDoorOnEdge(IsoDirections.S, false)) {
                                return false;
                            }

                            if (this.IsOpen() && square0.hasClosedDoorOnEdge(IsoDirections.N)) {
                                return false;
                            }

                            return true;
                        }

                        return false;
                    }

                    if (square2 == _square2) {
                        if (_square2.hasDoorOnEdge(IsoDirections.S, false)) {
                            return false;
                        }

                        if (this.IsOpen() && square0.hasClosedDoorOnEdge(IsoDirections.N)) {
                            return false;
                        }

                        return true;
                    }

                    if (square2 == square3) {
                        if (!square3.isWallTo(_square2)
                            && !square3.isWindowTo(_square2)
                            && !square3.hasDoorOnEdge(IsoDirections.W, false)
                            && !_square2.hasDoorOnEdge(IsoDirections.E, false)) {
                            if (_square2.hasDoorOnEdge(IsoDirections.S, false)) {
                                return false;
                            }

                            if (this.IsOpen() && square0.hasClosedDoorOnEdge(IsoDirections.N)) {
                                return false;
                            }

                            return true;
                        }

                        return false;
                    }

                    if (square2 == square4) {
                        if (!square4.isWallTo(square0)
                            && !square4.isWindowTo(square0)
                            && !square4.hasDoorOnEdge(IsoDirections.E, false)
                            && !square0.hasDoorOnEdge(IsoDirections.W, false)) {
                            if (boolean0 && square0.hasOpenDoorOnEdge(IsoDirections.N)) {
                                return false;
                            }

                            return true;
                        }

                        return false;
                    }

                    if (square2 == square0) {
                        if (boolean0 && square0.hasOpenDoorOnEdge(IsoDirections.N)) {
                            return false;
                        }

                        return true;
                    }

                    if (square2 == square5) {
                        if (!square5.isWallTo(square0)
                            && !square5.isWindowTo(square0)
                            && !square5.hasDoorOnEdge(IsoDirections.W, false)
                            && !square0.hasDoorOnEdge(IsoDirections.E, false)) {
                            if (boolean0 && square0.hasOpenDoorOnEdge(IsoDirections.N)) {
                                return false;
                            }

                            return true;
                        }

                        return false;
                    }
                    break;
                case S:
                    if (square2 == square4) {
                        if (!square4.isWallTo(square0)
                            && !square4.isWindowTo(square0)
                            && !square4.hasDoorOnEdge(IsoDirections.E, false)
                            && !square0.hasDoorOnEdge(IsoDirections.W, false)) {
                            if (boolean0 && square0.hasOpenDoorOnEdge(IsoDirections.S)) {
                                return false;
                            }

                            return true;
                        }

                        return false;
                    }

                    if (square2 == square0) {
                        if (boolean0 && square0.hasOpenDoorOnEdge(IsoDirections.S)) {
                            return false;
                        }

                        return true;
                    }

                    if (square2 == square5) {
                        if (!square5.isWallTo(square0)
                            && !square5.isWindowTo(square0)
                            && !square5.hasDoorOnEdge(IsoDirections.W, false)
                            && !square0.hasDoorOnEdge(IsoDirections.E, false)) {
                            if (boolean0 && square0.hasOpenDoorOnEdge(IsoDirections.S)) {
                                return false;
                            }

                            return true;
                        }

                        return false;
                    }

                    if (square2 == square6) {
                        if (!square6.isWallTo(square7)
                            && !square6.isWindowTo(square7)
                            && !square6.hasDoorOnEdge(IsoDirections.E, false)
                            && !square7.hasDoorOnEdge(IsoDirections.W, false)) {
                            if (square7.hasDoorOnEdge(IsoDirections.N, false)) {
                                return false;
                            }

                            return true;
                        }

                        return false;
                    }

                    if (square2 == square7) {
                        if (square7.hasDoorOnEdge(IsoDirections.N, false)) {
                            return false;
                        }

                        return true;
                    }

                    if (square2 == square8) {
                        if (!square8.isWallTo(square7)
                            && !square8.isWindowTo(square7)
                            && !square8.hasDoorOnEdge(IsoDirections.W, false)
                            && !square7.hasDoorOnEdge(IsoDirections.E, false)) {
                            if (square7.hasDoorOnEdge(IsoDirections.N, false)) {
                                return false;
                            }

                            return true;
                        }

                        return false;
                    }
                    break;
                case W:
                    if (square2 == square1) {
                        if (!square1.isWallTo(square4)
                            && !square1.isWindowTo(square4)
                            && !square1.hasDoorOnEdge(IsoDirections.S, false)
                            && !square4.hasDoorOnEdge(IsoDirections.N, false)) {
                            if (boolean0 && square4.hasDoorOnEdge(IsoDirections.E, false)) {
                                return false;
                            }

                            if (this.IsOpen() && square0.hasClosedDoorOnEdge(IsoDirections.W)) {
                                return false;
                            }

                            return true;
                        }

                        return false;
                    }

                    if (square2 == square4) {
                        if (boolean0 && square4.hasDoorOnEdge(IsoDirections.E, false)) {
                            return false;
                        }

                        if (this.IsOpen() && square0.hasClosedDoorOnEdge(IsoDirections.W)) {
                            return false;
                        }

                        return true;
                    }

                    if (square2 == square6) {
                        if (!square6.isWallTo(square4)
                            && !square6.isWindowTo(square4)
                            && !square6.hasDoorOnEdge(IsoDirections.N, false)
                            && !square4.hasDoorOnEdge(IsoDirections.S, false)) {
                            if (boolean0 && square4.hasDoorOnEdge(IsoDirections.E, false)) {
                                return false;
                            }

                            if (this.IsOpen() && square0.hasClosedDoorOnEdge(IsoDirections.W)) {
                                return false;
                            }

                            return true;
                        }

                        return false;
                    }

                    if (square2 == _square2) {
                        if (!_square2.isWallTo(square0)
                            && !_square2.isWindowTo(square0)
                            && !_square2.hasDoorOnEdge(IsoDirections.S, false)
                            && !square0.hasDoorOnEdge(IsoDirections.N, false)) {
                            if (boolean0 && square0.hasOpenDoorOnEdge(IsoDirections.W)) {
                                return false;
                            }

                            return true;
                        }

                        return false;
                    }

                    if (square2 == square0) {
                        if (boolean0 && square0.hasOpenDoorOnEdge(IsoDirections.W)) {
                            return false;
                        }

                        return true;
                    }

                    if (square2 == square7) {
                        if (!square7.isWallTo(square0)
                            && !square7.isWindowTo(square0)
                            && !square7.hasDoorOnEdge(IsoDirections.N, false)
                            && !square0.hasDoorOnEdge(IsoDirections.S, false)) {
                            if (boolean0 && square0.hasOpenDoorOnEdge(IsoDirections.W)) {
                                return false;
                            }

                            return true;
                        }

                        return false;
                    }
                    break;
                case E:
                    if (square2 == _square2) {
                        if (!_square2.isWallTo(square0)
                            && !_square2.isWindowTo(square0)
                            && !_square2.hasDoorOnEdge(IsoDirections.S, false)
                            && !square0.hasDoorOnEdge(IsoDirections.N, false)) {
                            if (boolean0 && square0.hasOpenDoorOnEdge(IsoDirections.E)) {
                                return false;
                            }

                            return true;
                        }

                        return false;
                    }

                    if (square2 == square0) {
                        if (boolean0 && square0.hasOpenDoorOnEdge(IsoDirections.E)) {
                            return false;
                        }

                        return true;
                    }

                    if (square2 == square7) {
                        if (!square7.isWallTo(square0)
                            && !square7.isWindowTo(square0)
                            && !square7.hasDoorOnEdge(IsoDirections.N, false)
                            && !square0.hasDoorOnEdge(IsoDirections.S, false)) {
                            if (boolean0 && square0.hasOpenDoorOnEdge(IsoDirections.E)) {
                                return false;
                            }

                            return true;
                        }

                        return false;
                    }

                    if (square2 == square3) {
                        if (!square3.isWallTo(square5)
                            && !square3.isWindowTo(square5)
                            && !square3.hasDoorOnEdge(IsoDirections.S, false)
                            && !square4.hasDoorOnEdge(IsoDirections.N, false)) {
                            if (square5.hasDoorOnEdge(IsoDirections.W, false)) {
                                return false;
                            }

                            return true;
                        }

                        return false;
                    }

                    if (square2 == square5) {
                        if (square5.hasDoorOnEdge(IsoDirections.W, false)) {
                            return false;
                        }

                        return true;
                    }

                    if (square2 == square8) {
                        if (!square8.isWallTo(square5)
                            && !square8.isWindowTo(square5)
                            && !square8.hasDoorOnEdge(IsoDirections.N, false)
                            && !square5.hasDoorOnEdge(IsoDirections.S, false)) {
                            if (square5.hasDoorOnEdge(IsoDirections.E, false)) {
                                return false;
                            }

                            return true;
                        }

                        return false;
                    }
                    break;
                default:
                    return false;
            }

            return false;
        } else {
            return false;
        }
    }

    public IsoGridSquare getAddSheetSquare(IsoGameCharacter chr) {
        if (chr != null && chr.getCurrentSquare() != null) {
            IsoGridSquare square0 = chr.getCurrentSquare();
            IsoGridSquare square1 = this.getSquare();
            if (this.north) {
                return square0.getY() < square1.getY() ? this.getCell().getGridSquare(square1.x, square1.y - 1, square1.z) : square1;
            } else {
                return square0.getX() < square1.getX() ? this.getCell().getGridSquare(square1.x - 1, square1.y, square1.z) : square1;
            }
        } else {
            return null;
        }
    }

    public void addSheet(IsoGameCharacter chr) {
        IsoGridSquare square = this.getIndoorSquare();
        IsoObjectType objectType;
        if (this.north) {
            objectType = IsoObjectType.curtainN;
            if (square != this.square) {
                objectType = IsoObjectType.curtainS;
            }
        } else {
            objectType = IsoObjectType.curtainW;
            if (square != this.square) {
                objectType = IsoObjectType.curtainE;
            }
        }

        if (chr != null) {
            if (this.north) {
                if (chr.getY() < this.getY()) {
                    square = this.getCell().getGridSquare((double)this.getX(), (double)(this.getY() - 1.0F), (double)this.getZ());
                    objectType = IsoObjectType.curtainS;
                } else {
                    square = this.getSquare();
                    objectType = IsoObjectType.curtainN;
                }
            } else if (chr.getX() < this.getX()) {
                square = this.getCell().getGridSquare((double)(this.getX() - 1.0F), (double)this.getY(), (double)this.getZ());
                objectType = IsoObjectType.curtainE;
            } else {
                square = this.getSquare();
                objectType = IsoObjectType.curtainW;
            }
        }

        if (square != null) {
            if (square.getCurtain(objectType) == null) {
                if (square != null) {
                    int int0 = 16;
                    if (objectType == IsoObjectType.curtainE) {
                        int0++;
                    }

                    if (objectType == IsoObjectType.curtainS) {
                        int0 += 3;
                    }

                    if (objectType == IsoObjectType.curtainN) {
                        int0 += 2;
                    }

                    int0 += 4;
                    IsoCurtain curtain = new IsoCurtain(this.getCell(), square, "fixtures_windows_curtains_01_" + int0, this.north);
                    square.AddSpecialTileObject(curtain);
                    if (GameServer.bServer) {
                        curtain.transmitCompleteItemToClients();
                        chr.sendObjectChange("removeOneOf", new Object[]{"type", "Sheet"});
                    } else {
                        chr.getInventory().RemoveOneOf("Sheet");
                    }
                }
            }
        }
    }

    public IsoGridSquare getIndoorSquare() {
        if (this.square.getRoom() != null) {
            return this.square;
        } else {
            IsoGridSquare square;
            if (this.north) {
                square = IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ());
            } else {
                square = IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
            }

            if (square == null || square.getFloor() == null) {
                return this.square;
            } else if (square.getRoom() != null) {
                return square;
            } else if (this.square.getFloor() == null) {
                return square;
            } else {
                String string = square.getFloor().getSprite().getName();
                return string != null && string.startsWith("carpentry_02_") ? square : this.square;
            }
        }
    }

    @Override
    public int getKeyId() {
        return this.keyId;
    }

    public void setKeyId(int _keyId, boolean doNetwork) {
        if (doNetwork && this.keyId != _keyId && GameClient.bClient) {
            this.keyId = _keyId;
            this.syncIsoThumpable();
        } else {
            this.keyId = _keyId;
        }
    }

    @Override
    public void setKeyId(int _keyId) {
        this.setKeyId(_keyId, true);
    }

    public boolean isLockedByKey() {
        return this.lockedByKey;
    }

    public void setLockedByKey(boolean _lockedByKey) {
        boolean boolean0 = _lockedByKey != this.lockedByKey;
        this.lockedByKey = _lockedByKey;
        this.setIsLocked(_lockedByKey);
        if (!GameServer.bServer && boolean0) {
            if (_lockedByKey) {
                this.syncIsoObject(false, (byte)3, null, null);
            } else {
                this.syncIsoObject(false, (byte)4, null, null);
            }
        }
    }

    public boolean isLockedByPadlock() {
        return this.lockedByPadlock;
    }

    public void syncIsoThumpable() {
        ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
        PacketTypes.PacketType.SyncThumpable.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(this.square.getX());
        byteBufferWriter.putInt(this.square.getY());
        byteBufferWriter.putInt(this.square.getZ());
        byte byte0 = (byte)this.square.getObjects().indexOf(this);
        if (byte0 == -1) {
            System.out.println("ERROR: Thumpable door not found on square " + this.square.getX() + ", " + this.square.getY() + ", " + this.square.getZ());
            GameClient.connection.cancelPacket();
        } else {
            byteBufferWriter.putByte(byte0);
            byteBufferWriter.putInt(this.getLockedByCode());
            byteBufferWriter.putByte((byte)(this.lockedByPadlock ? 1 : 0));
            byteBufferWriter.putInt(this.getKeyId());
            PacketTypes.PacketType.SyncThumpable.send(GameClient.connection);
        }
    }

    public void setLockedByPadlock(boolean _lockedByPadlock) {
        if (this.lockedByPadlock != _lockedByPadlock && GameClient.bClient) {
            this.lockedByPadlock = _lockedByPadlock;
            this.syncIsoThumpable();
        } else {
            this.lockedByPadlock = _lockedByPadlock;
        }
    }

    public boolean canBeLockByPadlock() {
        return this.canBeLockByPadlock;
    }

    public void setCanBeLockByPadlock(boolean _canBeLockByPadlock) {
        this.canBeLockByPadlock = _canBeLockByPadlock;
    }

    public int getLockedByCode() {
        return this.lockedByCode;
    }

    public void setLockedByCode(int _lockedByCode) {
        if (this.lockedByCode != _lockedByCode && GameClient.bClient) {
            this.lockedByCode = _lockedByCode;
            this.syncIsoThumpable();
        } else {
            this.lockedByCode = _lockedByCode;
        }
    }

    public boolean isLockedToCharacter(IsoGameCharacter chr) {
        if (GameClient.bClient && chr instanceof IsoPlayer && !((IsoPlayer)chr).accessLevel.equals("")) {
            return false;
        } else {
            return this.getLockedByCode() > 0
                ? true
                : this.isLockedByPadlock() && (chr.getInventory() == null || chr.getInventory().haveThisKeyId(this.getKeyId()) == null);
        }
    }

    public boolean canClimbOver(IsoGameCharacter chr) {
        if (this.square == null) {
            return false;
        } else {
            return !this.isHoppable() ? false : chr == null || IsoWindow.canClimbThroughHelper(chr, this.getSquare(), this.getOppositeSquare(), this.north);
        }
    }

    public boolean canClimbThrough(IsoGameCharacter chr) {
        if (this.square == null) {
            return false;
        } else if (!this.isWindow()) {
            return false;
        } else {
            return this.isBarricaded() ? false : chr == null || IsoWindow.canClimbThroughHelper(chr, this.getSquare(), this.getOppositeSquare(), this.north);
        }
    }

    public String getThumpSound() {
        return this.thumpSound;
    }

    public void setThumpSound(String _thumpSound) {
        this.thumpSound = _thumpSound;
    }

    @Override
    public IsoObject getRenderEffectMaster() {
        int int0 = IsoDoor.getDoubleDoorIndex(this);
        if (int0 != -1) {
            IsoObject object0 = null;
            if (int0 == 2) {
                object0 = IsoDoor.getDoubleDoorObject(this, 1);
            } else if (int0 == 3) {
                object0 = IsoDoor.getDoubleDoorObject(this, 4);
            }

            if (object0 != null) {
                return object0;
            }
        } else {
            IsoObject object1 = IsoDoor.getGarageDoorFirst(this);
            if (object1 != null) {
                return object1;
            }
        }

        return this;
    }

    public IsoDirections getSpriteEdge(boolean ignoreOpen) {
        if (!this.isDoor() && !this.isWindow()) {
            return null;
        } else if (this.open && !ignoreOpen) {
            PropertyContainer propertyContainer = this.getProperties();
            if (propertyContainer != null && propertyContainer.Is(IsoFlagType.attachedE)) {
                return IsoDirections.E;
            } else if (propertyContainer != null && propertyContainer.Is(IsoFlagType.attachedS)) {
                return IsoDirections.S;
            } else {
                return this.north ? IsoDirections.W : IsoDirections.N;
            }
        } else {
            return this.north ? IsoDirections.N : IsoDirections.W;
        }
    }

    private String getSoundPrefix() {
        if (this.closedSprite == null) {
            return "WoodDoor";
        } else {
            PropertyContainer propertyContainer = this.closedSprite.getProperties();
            return propertyContainer.Is("DoorSound") ? propertyContainer.Val("DoorSound") : "WoodDoor";
        }
    }

    private void playDoorSound(BaseCharacterSoundEmitter baseCharacterSoundEmitter, String string) {
        baseCharacterSoundEmitter.playSound(this.getSoundPrefix() + string, this);
    }
}
