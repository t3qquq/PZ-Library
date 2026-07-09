// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.SoundManager;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.ai.states.ThumpState;
import zombie.audio.parameters.ParameterMeleeHitSurface;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.math.PZMath;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.util.Type;

public class IsoBarricade extends IsoObject implements Thumpable {
    public static final int MAX_PLANKS = 4;
    public static final int PLANK_HEALTH = 1000;
    private int[] plankHealth = new int[4];
    public static final int METAL_HEALTH = 5000;
    public static final int METAL_HEALTH_DAMAGED = 2500;
    private int metalHealth;
    public static final int METAL_BAR_HEALTH = 3000;
    private int metalBarHealth;

    public IsoBarricade(IsoCell cell) {
        super(cell);
    }

    public IsoBarricade(IsoCell cell, IsoGridSquare gridSquare, IsoDirections dir) {
        this.square = gridSquare;
        this.dir = dir;
    }

    @Override
    public String getObjectName() {
        return "Barricade";
    }

    public void addPlank(IsoGameCharacter chr, InventoryItem plank) {
        if (this.canAddPlank()) {
            int int0 = 1000;
            if (plank != null) {
                int0 = (int)((float)plank.getCondition() / plank.getConditionMax() * 1000.0F);
            }

            if (chr != null) {
                int0 = (int)(int0 * chr.getBarricadeStrengthMod());
            }

            for (int int1 = 0; int1 < 4; int1++) {
                if (this.plankHealth[int1] <= 0) {
                    this.plankHealth[int1] = int0;
                    break;
                }
            }

            this.chooseSprite();
            if (!GameServer.bServer) {
                for (int int2 = 0; int2 < IsoPlayer.numPlayers; int2++) {
                    LosUtil.cachecleared[int2] = true;
                }

                IsoGridSquare.setRecalcLightTime(-1);
                GameTime.instance.lightSourceUpdate = 100.0F;
            }

            if (this.square != null) {
                this.square.RecalcProperties();
            }
        }
    }

    public InventoryItem removePlank(IsoGameCharacter chr) {
        if (this.getNumPlanks() <= 0) {
            return null;
        } else {
            InventoryItem item = null;

            for (int int0 = 3; int0 >= 0; int0--) {
                if (this.plankHealth[int0] > 0) {
                    float float0 = Math.min(this.plankHealth[int0] / 1000.0F, 1.0F);
                    item = InventoryItemFactory.CreateItem("Base.Plank");
                    item.setCondition((int)Math.max(item.getConditionMax() * float0, 1.0F));
                    this.plankHealth[int0] = 0;
                    break;
                }
            }

            if (this.getNumPlanks() <= 0) {
                if (this.square != null) {
                    if (GameServer.bServer) {
                        this.square.transmitRemoveItemFromSquare(this);
                    } else {
                        this.square.RemoveTileObject(this);
                    }
                }
            } else {
                this.chooseSprite();
                if (!GameServer.bServer) {
                    for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
                        LosUtil.cachecleared[int1] = true;
                    }

                    IsoGridSquare.setRecalcLightTime(-1);
                    GameTime.instance.lightSourceUpdate = 100.0F;
                }

                if (this.square != null) {
                    this.square.RecalcProperties();
                }
            }

            return item;
        }
    }

    public int getNumPlanks() {
        int int0 = 0;

        for (int int1 = 0; int1 < 4; int1++) {
            if (this.plankHealth[int1] > 0) {
                int0++;
            }
        }

        return int0;
    }

    public boolean canAddPlank() {
        return !this.isMetal() && this.getNumPlanks() < 4 && !this.isMetalBar();
    }

    public void addMetalBar(IsoGameCharacter chr, InventoryItem metalBar) {
        if (this.getNumPlanks() <= 0) {
            if (this.metalHealth <= 0) {
                if (this.metalBarHealth <= 0) {
                    this.metalBarHealth = 3000;
                    if (metalBar != null) {
                        this.metalBarHealth = (int)((float)metalBar.getCondition() / metalBar.getConditionMax() * 5000.0F);
                    }

                    if (chr != null) {
                        this.metalBarHealth = (int)(this.metalBarHealth * chr.getMetalBarricadeStrengthMod());
                    }

                    this.chooseSprite();
                    if (!GameServer.bServer) {
                        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                            LosUtil.cachecleared[int0] = true;
                        }

                        IsoGridSquare.setRecalcLightTime(-1);
                        GameTime.instance.lightSourceUpdate = 100.0F;
                    }

                    if (this.square != null) {
                        this.square.RecalcProperties();
                    }
                }
            }
        }
    }

    public InventoryItem removeMetalBar(IsoGameCharacter chr) {
        if (this.metalBarHealth <= 0) {
            return null;
        } else {
            float float0 = Math.min(this.metalBarHealth / 3000.0F, 1.0F);
            this.metalBarHealth = 0;
            InventoryItem item = InventoryItemFactory.CreateItem("Base.MetalBar");
            item.setCondition((int)Math.max(item.getConditionMax() * float0, 1.0F));
            if (this.square != null) {
                if (GameServer.bServer) {
                    this.square.transmitRemoveItemFromSquare(this);
                } else {
                    this.square.RemoveTileObject(this);
                }
            }

            return item;
        }
    }

    public void addMetal(IsoGameCharacter chr, InventoryItem metal) {
        if (this.getNumPlanks() <= 0) {
            if (this.metalHealth <= 0) {
                this.metalHealth = 5000;
                if (metal != null) {
                    this.metalHealth = (int)((float)metal.getCondition() / metal.getConditionMax() * 5000.0F);
                }

                if (chr != null) {
                    this.metalHealth = (int)(this.metalHealth * chr.getMetalBarricadeStrengthMod());
                }

                this.chooseSprite();
                if (!GameServer.bServer) {
                    for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                        LosUtil.cachecleared[int0] = true;
                    }

                    IsoGridSquare.setRecalcLightTime(-1);
                    GameTime.instance.lightSourceUpdate = 100.0F;
                }

                if (this.square != null) {
                    this.square.RecalcProperties();
                }
            }
        }
    }

    public boolean isMetalBar() {
        return this.metalBarHealth > 0;
    }

    public InventoryItem removeMetal(IsoGameCharacter chr) {
        if (this.metalHealth <= 0) {
            return null;
        } else {
            float float0 = Math.min(this.metalHealth / 5000.0F, 1.0F);
            this.metalHealth = 0;
            InventoryItem item = InventoryItemFactory.CreateItem("Base.SheetMetal");
            item.setCondition((int)Math.max(item.getConditionMax() * float0, 1.0F));
            if (this.square != null) {
                if (GameServer.bServer) {
                    this.square.transmitRemoveItemFromSquare(this);
                } else {
                    this.square.RemoveTileObject(this);
                }
            }

            return item;
        }
    }

    public boolean isMetal() {
        return this.metalHealth > 0;
    }

    public boolean isBlockVision() {
        return this.isMetal() || this.getNumPlanks() > 2;
    }

    private void chooseSprite() {
        IsoSpriteManager spriteManager = IsoSpriteManager.instance;
        if (this.metalHealth > 0) {
            int int0 = this.metalHealth <= 2500 ? 2 : 0;
            String string0 = "constructedobjects_01";
            switch (this.dir) {
                case W:
                    this.sprite = spriteManager.getSprite(string0 + "_" + (24 + int0));
                    break;
                case N:
                    this.sprite = spriteManager.getSprite(string0 + "_" + (25 + int0));
                    break;
                case E:
                    this.sprite = spriteManager.getSprite(string0 + "_" + (28 + int0));
                    break;
                case S:
                    this.sprite = spriteManager.getSprite(string0 + "_" + (29 + int0));
                    break;
                default:
                    this.sprite.LoadFramesNoDirPageSimple("media/ui/missing-tile.png");
            }
        } else if (this.metalBarHealth > 0) {
            String string1 = "constructedobjects_01";
            switch (this.dir) {
                case W:
                    this.sprite = spriteManager.getSprite(string1 + "_55");
                    break;
                case N:
                    this.sprite = spriteManager.getSprite(string1 + "_53");
                    break;
                case E:
                    this.sprite = spriteManager.getSprite(string1 + "_52");
                    break;
                case S:
                    this.sprite = spriteManager.getSprite(string1 + "_54");
                    break;
                default:
                    this.sprite.LoadFramesNoDirPageSimple("media/ui/missing-tile.png");
            }
        } else {
            int int1 = this.getNumPlanks();
            if (int1 <= 0) {
                this.sprite = spriteManager.getSprite("media/ui/missing-tile.png");
            } else {
                String string2 = "carpentry_01";
                switch (this.dir) {
                    case W:
                        this.sprite = spriteManager.getSprite(string2 + "_" + (8 + (int1 - 1) * 2));
                        break;
                    case N:
                        this.sprite = spriteManager.getSprite(string2 + "_" + (9 + (int1 - 1) * 2));
                        break;
                    case E:
                        this.sprite = spriteManager.getSprite(string2 + "_" + (0 + (int1 - 1) * 2));
                        break;
                    case S:
                        this.sprite = spriteManager.getSprite(string2 + "_" + (1 + (int1 - 1) * 2));
                        break;
                    default:
                        this.sprite.LoadFramesNoDirPageSimple("media/ui/missing-tile.png");
                }
            }
        }
    }

    @Override
    public boolean isDestroyed() {
        return this.metalHealth <= 0 && this.getNumPlanks() <= 0 && this.metalBarHealth <= 0;
    }

    @Override
    public boolean TestCollide(IsoMovingObject obj, IsoGridSquare from, IsoGridSquare to) {
        return false;
    }

    @Override
    public IsoObject.VisionResult TestVision(IsoGridSquare from, IsoGridSquare to) {
        if (this.metalHealth <= 0 && this.getNumPlanks() <= 2) {
            return IsoObject.VisionResult.NoEffect;
        } else {
            if (from == this.square) {
                if (this.dir == IsoDirections.N && to.getY() < from.getY()) {
                    return IsoObject.VisionResult.Blocked;
                }

                if (this.dir == IsoDirections.S && to.getY() > from.getY()) {
                    return IsoObject.VisionResult.Blocked;
                }

                if (this.dir == IsoDirections.W && to.getX() < from.getX()) {
                    return IsoObject.VisionResult.Blocked;
                }

                if (this.dir == IsoDirections.E && to.getX() > from.getX()) {
                    return IsoObject.VisionResult.Blocked;
                }
            } else if (to == this.square && from != this.square) {
                return this.TestVision(to, from);
            }

            return IsoObject.VisionResult.NoEffect;
        }
    }

    @Override
    public void Thump(IsoMovingObject thumper) {
        if (!this.isDestroyed()) {
            if (thumper instanceof IsoZombie) {
                int int0 = this.getNumPlanks();
                boolean boolean0 = this.metalHealth > 2500;
                int int1 = ThumpState.getFastForwardDamageMultiplier();
                this.Damage(((IsoZombie)thumper).strength * int1);
                if (int0 != this.getNumPlanks()) {
                    ((IsoGameCharacter)thumper).getEmitter().playSound("BreakBarricadePlank");
                    if (GameServer.bServer) {
                        GameServer.PlayWorldSoundServer("BreakBarricadePlank", false, thumper.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
                    }
                }

                if (this.isDestroyed()) {
                    if (this.getSquare().getBuilding() != null) {
                        this.getSquare().getBuilding().forceAwake();
                    }

                    this.square.transmitRemoveItemFromSquare(this);
                    if (!GameServer.bServer) {
                        this.square.RemoveTileObject(this);
                    }
                } else if ((int0 != this.getNumPlanks() || boolean0 && this.metalHealth < 2500) && GameServer.bServer) {
                    this.sendObjectChange("state");
                }

                if (!this.isDestroyed()) {
                    this.setRenderEffect(RenderEffectType.Hit_Door, true);
                }

                WorldSoundManager.instance.addSound(thumper, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
            }
        }
    }

    @Override
    public Thumpable getThumpableFor(IsoGameCharacter chr) {
        return this.isDestroyed() ? null : this;
    }

    @Override
    public Vector2 getFacingPosition(Vector2 pos) {
        if (this.square == null) {
            return pos.set(0.0F, 0.0F);
        } else if (this.dir == IsoDirections.N) {
            return pos.set(this.getX() + 0.5F, this.getY());
        } else if (this.dir == IsoDirections.S) {
            return pos.set(this.getX() + 0.5F, this.getY() + 1.0F);
        } else if (this.dir == IsoDirections.W) {
            return pos.set(this.getX(), this.getY() + 0.5F);
        } else {
            return this.dir == IsoDirections.E ? pos.set(this.getX() + 1.0F, this.getY() + 0.5F) : pos.set(this.getX(), this.getY() + 0.5F);
        }
    }

    @Override
    public void WeaponHit(IsoGameCharacter owner, HandWeapon weapon) {
        if (!this.isDestroyed()) {
            IsoPlayer player = Type.tryCastTo(owner, IsoPlayer.class);
            if (GameClient.bClient) {
                if (player != null) {
                    GameClient.instance.sendWeaponHit(player, weapon, this);
                }
            } else {
                LuaEventManager.triggerEvent("OnWeaponHitThumpable", owner, weapon, this);
                String string0 = !this.isMetal() && !this.isMetalBar() ? "HitBarricadePlank" : "HitBarricadeMetal";
                if (player != null) {
                    player.setMeleeHitSurface(
                        !this.isMetal() && !this.isMetalBar() ? ParameterMeleeHitSurface.Material.Wood : ParameterMeleeHitSurface.Material.Metal
                    );
                }

                SoundManager.instance.PlayWorldSound(string0, false, this.getSquare(), 1.0F, 20.0F, 2.0F, false);
                if (GameServer.bServer) {
                    GameServer.PlayWorldSoundServer(string0, false, this.getSquare(), 1.0F, 20.0F, 2.0F, false);
                }

                if (weapon != null) {
                    this.Damage(weapon.getDoorDamage() * 5);
                } else {
                    this.Damage(100);
                }

                WorldSoundManager.instance.addSound(owner, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
                if (this.isDestroyed()) {
                    if (owner != null) {
                        String string1 = string0 == "HitBarricadeMetal" ? "BreakBarricadeMetal" : "BreakBarricadePlank";
                        owner.getEmitter().playSound(string1);
                        if (GameServer.bServer) {
                            GameServer.PlayWorldSoundServer(string1, false, owner.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
                        }
                    }

                    this.square.transmitRemoveItemFromSquare(this);
                    if (!GameServer.bServer) {
                        this.square.RemoveTileObject(this);
                    }
                }

                if (!this.isDestroyed()) {
                    this.setRenderEffect(RenderEffectType.Hit_Door, true);
                }
            }
        }
    }

    public void DamageBarricade(int amount) {
        this.Damage(amount);
    }

    public void Damage(int amount) {
        if (!"Tutorial".equals(Core.GameMode)) {
            if (this.metalHealth > 0) {
                this.metalHealth -= amount;
                if (this.metalHealth <= 0) {
                    this.metalHealth = 0;
                    this.chooseSprite();
                }
            } else if (this.metalBarHealth > 0) {
                this.metalBarHealth -= amount;
                if (this.metalBarHealth <= 0) {
                    this.metalBarHealth = 0;
                    this.chooseSprite();
                }
            } else {
                for (int int0 = 3; int0 >= 0; int0--) {
                    if (this.plankHealth[int0] > 0) {
                        this.plankHealth[int0] = this.plankHealth[int0] - amount;
                        if (this.plankHealth[int0] <= 0) {
                            this.plankHealth[int0] = 0;
                            this.chooseSprite();
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public float getThumpCondition() {
        if (this.metalHealth > 0) {
            return PZMath.clamp(this.metalHealth, 0, 5000) / 5000.0F;
        } else if (this.metalBarHealth > 0) {
            return PZMath.clamp(this.metalBarHealth, 0, 3000) / 3000.0F;
        } else {
            for (int int0 = 3; int0 >= 0; int0--) {
                if (this.plankHealth[int0] > 0) {
                    return PZMath.clamp(this.plankHealth[int0], 0, 1000) / 1000.0F;
                }
            }

            return 0.0F;
        }
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        byte byte0 = input.get();
        this.dir = IsoDirections.fromIndex(byte0);
        byte byte1 = input.get();

        for (int int0 = 0; int0 < byte1; int0++) {
            short short0 = input.getShort();
            if (int0 < 4) {
                this.plankHealth[int0] = short0;
            }
        }

        this.metalHealth = input.getShort();
        if (WorldVersion >= 90) {
            this.metalBarHealth = input.getShort();
        }

        this.chooseSprite();
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        output.put((byte)1);
        output.put(IsoObject.factoryGetClassID(this.getObjectName()));
        output.put((byte)this.dir.index());
        output.put((byte)4);

        for (int int0 = 0; int0 < 4; int0++) {
            output.putShort((short)this.plankHealth[int0]);
        }

        output.putShort((short)this.metalHealth);
        output.putShort((short)this.metalBarHealth);
    }

    @Override
    public void saveChange(String change, KahluaTable tbl, ByteBuffer bb) {
        if ("state".equals(change)) {
            for (int int0 = 0; int0 < 4; int0++) {
                bb.putShort((short)this.plankHealth[int0]);
            }

            bb.putShort((short)this.metalHealth);
            bb.putShort((short)this.metalBarHealth);
        }
    }

    @Override
    public void loadChange(String change, ByteBuffer bb) {
        if ("state".equals(change)) {
            for (int int0 = 0; int0 < 4; int0++) {
                this.plankHealth[int0] = bb.getShort();
            }

            this.metalHealth = bb.getShort();
            this.metalBarHealth = bb.getShort();
            this.chooseSprite();
            if (this.square != null) {
                this.square.RecalcProperties();
            }

            for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
                LosUtil.cachecleared[int1] = true;
            }

            IsoGridSquare.setRecalcLightTime(-1);
            GameTime.instance.lightSourceUpdate = 100.0F;
        }
    }

    public BarricadeAble getBarricadedObject() {
        int int0 = this.getSpecialObjectIndex();
        if (int0 == -1) {
            return null;
        } else {
            ArrayList arrayList = this.getSquare().getSpecialObjects();
            if (this.getDir() != IsoDirections.W && this.getDir() != IsoDirections.N) {
                if (this.getDir() == IsoDirections.E || this.getDir() == IsoDirections.S) {
                    boolean boolean0 = this.getDir() == IsoDirections.S;
                    int int1 = this.getSquare().getX() + (this.getDir() == IsoDirections.E ? 1 : 0);
                    int int2 = this.getSquare().getY() + (this.getDir() == IsoDirections.S ? 1 : 0);
                    IsoGridSquare square = this.getCell().getGridSquare((double)int1, (double)int2, (double)this.getZ());
                    if (square != null) {
                        arrayList = square.getSpecialObjects();

                        for (int int3 = arrayList.size() - 1; int3 >= 0; int3--) {
                            IsoObject object0 = (IsoObject)arrayList.get(int3);
                            if (object0 instanceof BarricadeAble && boolean0 == ((BarricadeAble)object0).getNorth()) {
                                return (BarricadeAble)object0;
                            }
                        }
                    }
                }
            } else {
                boolean boolean1 = this.getDir() == IsoDirections.N;

                for (int int4 = int0 - 1; int4 >= 0; int4--) {
                    IsoObject object1 = (IsoObject)arrayList.get(int4);
                    if (object1 instanceof BarricadeAble && boolean1 == ((BarricadeAble)object1).getNorth()) {
                        return (BarricadeAble)object1;
                    }
                }
            }

            return null;
        }
    }

    @Override
    public void render(float x, float y, float z, ColorInfo col, boolean bDoAttached, boolean bWallLightingPass, Shader shader) {
        int int0 = IsoCamera.frameState.playerIndex;
        BarricadeAble barricadeAble = this.getBarricadedObject();
        if (barricadeAble != null && this.square.lighting[int0].targetDarkMulti() <= barricadeAble.getSquare().lighting[int0].targetDarkMulti()) {
            col = barricadeAble.getSquare().lighting[int0].lightInfo();
            this.setTargetAlpha(int0, ((IsoObject)barricadeAble).getTargetAlpha(int0));
        }

        super.render(x, y, z, col, bDoAttached, bWallLightingPass, shader);
    }

    public static IsoBarricade GetBarricadeOnSquare(IsoGridSquare square, IsoDirections dir) {
        if (square == null) {
            return null;
        } else {
            for (int int0 = 0; int0 < square.getSpecialObjects().size(); int0++) {
                IsoObject object = square.getSpecialObjects().get(int0);
                if (object instanceof IsoBarricade barricade && barricade.getDir() == dir) {
                    return barricade;
                }
            }

            return null;
        }
    }

    public static IsoBarricade GetBarricadeForCharacter(BarricadeAble obj, IsoGameCharacter chr) {
        if (obj != null && obj.getSquare() != null) {
            if (chr != null) {
                if (obj.getNorth()) {
                    if (chr.getY() < obj.getSquare().getY()) {
                        return GetBarricadeOnSquare(obj.getOppositeSquare(), obj.getNorth() ? IsoDirections.S : IsoDirections.E);
                    }
                } else if (chr.getX() < obj.getSquare().getX()) {
                    return GetBarricadeOnSquare(obj.getOppositeSquare(), obj.getNorth() ? IsoDirections.S : IsoDirections.E);
                }
            }

            return GetBarricadeOnSquare(obj.getSquare(), obj.getNorth() ? IsoDirections.N : IsoDirections.W);
        } else {
            return null;
        }
    }

    public static IsoBarricade GetBarricadeOppositeCharacter(BarricadeAble obj, IsoGameCharacter chr) {
        if (obj != null && obj.getSquare() != null) {
            if (chr != null) {
                if (obj.getNorth()) {
                    if (chr.getY() < obj.getSquare().getY()) {
                        return GetBarricadeOnSquare(obj.getSquare(), obj.getNorth() ? IsoDirections.N : IsoDirections.W);
                    }
                } else if (chr.getX() < obj.getSquare().getX()) {
                    return GetBarricadeOnSquare(obj.getSquare(), obj.getNorth() ? IsoDirections.N : IsoDirections.W);
                }
            }

            return GetBarricadeOnSquare(obj.getOppositeSquare(), obj.getNorth() ? IsoDirections.S : IsoDirections.E);
        } else {
            return null;
        }
    }

    public static IsoBarricade AddBarricadeToObject(BarricadeAble to, boolean addOpposite) {
        IsoGridSquare square = addOpposite ? to.getOppositeSquare() : to.getSquare();
        Object object0 = null;
        if (to.getNorth()) {
            object0 = addOpposite ? IsoDirections.S : IsoDirections.N;
        } else {
            object0 = addOpposite ? IsoDirections.E : IsoDirections.W;
        }

        if (square != null && object0 != null) {
            IsoBarricade barricade = GetBarricadeOnSquare(square, (IsoDirections)object0);
            if (barricade != null) {
                return barricade;
            } else {
                barricade = new IsoBarricade(IsoWorld.instance.CurrentCell, square, (IsoDirections)object0);
                int int0 = -1;

                for (int int1 = 0; int1 < square.getObjects().size(); int1++) {
                    IsoObject object1 = square.getObjects().get(int1);
                    if (object1 instanceof IsoCurtain curtain) {
                        if (curtain.getType() == IsoObjectType.curtainW && object0 == IsoDirections.W) {
                            int0 = int1;
                        } else if (curtain.getType() == IsoObjectType.curtainN && object0 == IsoDirections.N) {
                            int0 = int1;
                        } else if (curtain.getType() == IsoObjectType.curtainE && object0 == IsoDirections.E) {
                            int0 = int1;
                        } else if (curtain.getType() == IsoObjectType.curtainS && object0 == IsoDirections.S) {
                            int0 = int1;
                        }

                        if (int0 != -1) {
                            break;
                        }
                    }
                }

                square.AddSpecialObject(barricade, int0);

                for (int int2 = 0; int2 < IsoPlayer.numPlayers; int2++) {
                    LosUtil.cachecleared[int2] = true;
                }

                IsoGridSquare.setRecalcLightTime(-1);
                GameTime.instance.lightSourceUpdate = 100.0F;
                return barricade;
            }
        } else {
            return null;
        }
    }

    public static IsoBarricade AddBarricadeToObject(BarricadeAble to, IsoGameCharacter chr) {
        if (to == null || to.getSquare() == null || chr == null) {
            return null;
        } else if (to.getNorth()) {
            boolean boolean0 = chr.getY() < to.getSquare().getY();
            return AddBarricadeToObject(to, boolean0);
        } else {
            boolean boolean1 = chr.getX() < to.getSquare().getX();
            return AddBarricadeToObject(to, boolean1);
        }
    }
}
