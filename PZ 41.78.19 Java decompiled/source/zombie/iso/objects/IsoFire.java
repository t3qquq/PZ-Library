// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoHeatSource;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.areas.SafeHouse;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.ui.TutorialManager;

public class IsoFire extends IsoObject {
    public int Age = 0;
    public int Energy = 0;
    public int Life;
    public int LifeStage;
    public int LifeStageDuration;
    public int LifeStageTimer;
    public int MaxLife = 3000;
    public int MinLife = 800;
    public int SpreadDelay;
    public int SpreadTimer;
    public int numFlameParticles;
    public boolean perm = false;
    public boolean bSmoke = false;
    public IsoLightSource LightSource = null;
    public int LightRadius = 1;
    public float LightOscillator = 0.0F;
    private IsoHeatSource heatSource;
    private float accum = 0.0F;

    public IsoFire(IsoCell cell) {
        super(cell);
    }

    public IsoFire(IsoCell cell, IsoGridSquare gridSquare) {
        super(cell);
        this.square = gridSquare;
        this.perm = true;
    }

    @Override
    public String getObjectName() {
        return "Fire";
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        ArrayList arrayList = this.AttachedAnimSprite;
        this.AttachedAnimSprite = null;
        super.save(output, IS_DEBUG_SAVE);
        this.AttachedAnimSprite = arrayList;
        this.sprite = null;
        output.putInt(this.Life);
        output.putInt(this.SpreadDelay);
        output.putInt(this.LifeStage - 1);
        output.putInt(this.LifeStageTimer);
        output.putInt(this.LifeStageDuration);
        output.putInt(this.Energy);
        output.putInt(this.numFlameParticles);
        output.putInt(this.SpreadTimer);
        output.putInt(this.Age);
        output.put((byte)(this.perm ? 1 : 0));
        output.put((byte)this.LightRadius);
        output.put((byte)(this.bSmoke ? 1 : 0));
    }

    @Override
    public void load(ByteBuffer b, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(b, WorldVersion, IS_DEBUG_SAVE);
        this.sprite = null;
        this.Life = b.getInt();
        this.SpreadDelay = b.getInt();
        this.LifeStage = b.getInt();
        this.LifeStageTimer = b.getInt();
        this.LifeStageDuration = b.getInt();
        this.Energy = b.getInt();
        this.numFlameParticles = b.getInt();
        this.SpreadTimer = b.getInt();
        this.Age = b.getInt();
        this.perm = b.get() == 1;
        this.LightRadius = b.get() & 255;
        if (WorldVersion >= 89) {
            this.bSmoke = b.get() == 1;
        }

        if (this.perm) {
            this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -16, -78, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
        } else {
            if (this.numFlameParticles == 0) {
                this.numFlameParticles = 1;
            }

            switch (this.LifeStage) {
                case -1:
                    this.LifeStage = 0;

                    for (int int0 = 0; int0 < this.numFlameParticles; int0++) {
                        this.AttachAnim(
                            "Fire",
                            "01",
                            4,
                            IsoFireManager.FireAnimDelay,
                            -16 + -16 + Rand.Next(32),
                            -85 + -16 + Rand.Next(32),
                            true,
                            0,
                            false,
                            0.7F,
                            IsoFireManager.FireTintMod
                        );
                    }
                    break;
                case 0:
                    this.LifeStage = 1;
                    this.LifeStageTimer = this.LifeStageDuration;
                    this.AttachAnim("Fire", "02", 4, IsoFireManager.FireAnimDelay, -16, -72, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
                    break;
                case 1:
                    this.LifeStage = 2;
                    this.LifeStageTimer = this.LifeStageDuration;
                    this.AttachAnim("Smoke", "01", 4, IsoFireManager.SmokeAnimDelay, -9, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
                    this.AttachAnim("Fire", "03", 4, IsoFireManager.FireAnimDelay, -9, -52, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
                    break;
                case 2:
                    this.LifeStage = 3;
                    this.LifeStageTimer = this.LifeStageDuration / 3;
                    this.RemoveAttachedAnims();
                    this.AttachAnim("Smoke", "02", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
                    this.AttachAnim("Fire", "02", 4, IsoFireManager.FireAnimDelay, -16, -72, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
                    break;
                case 3:
                    this.LifeStage = 4;
                    this.LifeStageTimer = this.LifeStageDuration / 3;
                    this.RemoveAttachedAnims();
                    if (this.bSmoke) {
                        this.AttachAnim("Smoke", "03", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
                    } else {
                        this.AttachAnim("Smoke", "03", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
                        this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -16, -85, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
                    }
                    break;
                case 4:
                    this.LifeStage = 5;
                    this.LifeStageTimer = this.LifeStageDuration / 3;
                    this.RemoveAttachedAnims();
                    this.AttachAnim("Smoke", "01", 4, IsoFireManager.SmokeAnimDelay, -9, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
            }

            if (this.square != null) {
                if (this.LifeStage < 4) {
                    this.square.getProperties().Set(IsoFlagType.burning);
                } else {
                    this.square.getProperties().Set(IsoFlagType.smoke);
                }
            }
        }
    }

    public IsoFire(IsoCell cell, IsoGridSquare gridSquare, boolean CanBurnAnywhere, int StartingEnergy, int SetLife, boolean isSmoke) {
        this.square = gridSquare;
        this.DirtySlice();
        this.square.getProperties().Set(IsoFlagType.smoke);
        this.AttachAnim("Smoke", "03", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
        this.Life = this.MinLife + Rand.Next(this.MaxLife - this.MinLife);
        if (SetLife > 0) {
            this.Life = SetLife;
        }

        this.LifeStage = 4;
        this.LifeStageTimer = this.LifeStageDuration = this.Life / 4;
        this.Energy = StartingEnergy;
        this.bSmoke = isSmoke;
    }

    public IsoFire(IsoCell cell, IsoGridSquare gridSquare, boolean CanBurnAnywhere, int StartingEnergy, int SetLife) {
        this.square = gridSquare;
        this.DirtySlice();
        this.numFlameParticles = 2 + Rand.Next(2);

        for (int int0 = 0; int0 < this.numFlameParticles; int0++) {
            this.AttachAnim(
                "Fire",
                "01",
                4,
                IsoFireManager.FireAnimDelay,
                -16 + -16 + Rand.Next(32),
                -85 + -16 + Rand.Next(32),
                true,
                0,
                false,
                0.7F,
                IsoFireManager.FireTintMod
            );
        }

        this.Life = this.MinLife + Rand.Next(this.MaxLife - this.MinLife);
        if (SetLife > 0) {
            this.Life = SetLife;
        }

        if (this.square.getProperties() != null && !this.square.getProperties().Is(IsoFlagType.vegitation) && this.square.getFloor() != null) {
            this.Life = this.Life - this.square.getFloor().getSprite().firerequirement * 100;
            if (this.Life < 600) {
                this.Life = Rand.Next(300, 600);
            }
        }

        this.SpreadDelay = this.SpreadTimer = Rand.Next(this.Life - this.Life / 2);
        this.LifeStage = 0;
        this.LifeStageTimer = this.LifeStageDuration = this.Life / 4;
        if (TutorialManager.instance.Active) {
            this.LifeStageDuration *= 2;
            this.Life *= 2;
        }

        if (TutorialManager.instance.Active) {
            this.SpreadDelay = this.SpreadTimer /= 4;
        }

        gridSquare.getProperties().Set(IsoFlagType.burning);
        this.Energy = StartingEnergy;
        if (this.square.getProperties().Is(IsoFlagType.vegitation)) {
            this.Energy += 50;
        }

        LuaEventManager.triggerEvent("OnNewFire", this);
    }

    public IsoFire(IsoCell cell, IsoGridSquare gridSquare, boolean CanBurnAnywhere, int StartingEnergy) {
        this(cell, gridSquare, CanBurnAnywhere, StartingEnergy, 0);
    }

    public static boolean CanAddSmoke(IsoGridSquare gridSquare, boolean CanBurnAnywhere) {
        return CanAddFire(gridSquare, CanBurnAnywhere, true);
    }

    public static boolean CanAddFire(IsoGridSquare gridSquare, boolean CanBurnAnywhere) {
        return CanAddFire(gridSquare, CanBurnAnywhere, false);
    }

    public static boolean CanAddFire(IsoGridSquare gridSquare, boolean CanBurnAnywhere, boolean smoke) {
        if (!smoke && (GameServer.bServer || GameClient.bClient) && ServerOptions.instance.NoFire.getValue()) {
            return false;
        } else if (gridSquare == null || gridSquare.getObjects().isEmpty()) {
            return false;
        } else if (gridSquare.Is(IsoFlagType.water)) {
            return false;
        } else if (!CanBurnAnywhere && gridSquare.getProperties().Is(IsoFlagType.burntOut)) {
            return false;
        } else if (gridSquare.getProperties().Is(IsoFlagType.burning) || gridSquare.getProperties().Is(IsoFlagType.smoke)) {
            return false;
        } else {
            return !CanBurnAnywhere && !Fire_IsSquareFlamable(gridSquare)
                ? false
                : smoke
                    || !GameServer.bServer && !GameClient.bClient
                    || SafeHouse.getSafeHouse(gridSquare) == null
                    || ServerOptions.instance.SafehouseAllowFire.getValue();
        }
    }

    public static boolean Fire_IsSquareFlamable(IsoGridSquare gridSquare) {
        return !gridSquare.getProperties().Is(IsoFlagType.unflamable);
    }

    @Override
    public boolean HasTooltip() {
        return false;
    }

    public void Spread() {
        if (!GameClient.bClient) {
            if (SandboxOptions.instance.FireSpread.getValue()) {
                if (this.getCell() != null) {
                    if (this.square != null) {
                        if (this.LifeStage < 4) {
                            IsoGridSquare square = null;
                            int int0 = Rand.Next(3) + 1;
                            if (Rand.Next(50) == 0) {
                                int0 += 15;
                            }

                            if (TutorialManager.instance.Active) {
                                int0 += 15;
                            }

                            for (int int1 = 0; int1 < int0; int1++) {
                                int int2 = Rand.Next(13);
                                switch (int2) {
                                    case 0:
                                        square = this.getCell().getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ());
                                        break;
                                    case 1:
                                        square = this.getCell().getGridSquare(this.square.getX() + 1, this.square.getY() - 1, this.square.getZ());
                                        break;
                                    case 2:
                                        square = this.getCell().getGridSquare(this.square.getX() + 1, this.square.getY(), this.square.getZ());
                                        break;
                                    case 3:
                                        square = this.getCell().getGridSquare(this.square.getX() + 1, this.square.getY() + 1, this.square.getZ());
                                        break;
                                    case 4:
                                        square = this.getCell().getGridSquare(this.square.getX(), this.square.getY() + 1, this.square.getZ());
                                        break;
                                    case 5:
                                        square = this.getCell().getGridSquare(this.square.getX() - 1, this.square.getY() + 1, this.square.getZ());
                                        break;
                                    case 6:
                                        square = this.getCell().getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
                                        break;
                                    case 7:
                                        square = this.getCell().getGridSquare(this.square.getX() - 1, this.square.getY() - 1, this.square.getZ());
                                        break;
                                    case 8:
                                        square = this.getCell().getGridSquare(this.square.getX() - 1, this.square.getY() - 1, this.square.getZ() - 1);
                                        break;
                                    case 9:
                                        square = this.getCell().getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ() - 1);
                                        break;
                                    case 10:
                                        square = this.getCell().getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ() - 1);
                                        break;
                                    case 11:
                                        square = this.getCell().getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ() - 1);
                                        break;
                                    case 12:
                                        square = this.getCell().getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ() + 1);
                                }

                                if (CanAddFire(square, false)) {
                                    int int3 = this.getSquaresEnergyRequirement(square);
                                    if (this.Energy >= int3) {
                                        this.Energy -= int3;
                                        if (GameServer.bServer) {
                                            this.sendObjectChange("Energy");
                                        }

                                        if (RainManager.isRaining()) {
                                            return;
                                        }

                                        int int4 = square.getProperties().Is(IsoFlagType.exterior) ? this.Energy : int3 * 2;
                                        IsoFireManager.StartFire(this.getCell(), square, false, int4);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean TestCollide(IsoMovingObject obj, IsoGridSquare PassedObjectSquare) {
        return this.square == PassedObjectSquare;
    }

    @Override
    public IsoObject.VisionResult TestVision(IsoGridSquare from, IsoGridSquare to) {
        return IsoObject.VisionResult.NoEffect;
    }

    @Override
    public void update() {
        if (this.getObjectIndex() != -1) {
            if (!GameServer.bServer) {
                IsoFireManager.updateSound(this);
            }

            if (this.LifeStage < 4) {
                this.square.getProperties().Set(IsoFlagType.burning);
            } else {
                this.square.getProperties().Set(IsoFlagType.smoke);
            }

            if (!this.bSmoke && this.LifeStage < 5) {
                this.square.BurnTick();
            }

            int int0 = this.AttachedAnimSprite.size();

            for (int int1 = 0; int1 < int0; int1++) {
                IsoSpriteInstance spriteInstance = this.AttachedAnimSprite.get(int1);
                IsoSprite sprite = spriteInstance.parentSprite;
                spriteInstance.update();
                float float0 = GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F;
                spriteInstance.Frame = spriteInstance.Frame + spriteInstance.AnimFrameIncrease * float0;
                if ((int)spriteInstance.Frame >= sprite.CurrentAnim.Frames.size() && sprite.Loop && spriteInstance.Looped) {
                    spriteInstance.Frame = 0.0F;
                }
            }

            if (!this.bSmoke && !GameServer.bServer && this.LightSource == null) {
                this.LightSource = new IsoLightSource(
                    this.square.getX(), this.square.getY(), this.square.getZ(), 0.61F, 0.165F, 0.0F, this.perm ? this.LightRadius : 5
                );
                IsoWorld.instance.CurrentCell.addLamppost(this.LightSource);
            }

            if (this.perm) {
                if (this.heatSource == null) {
                    this.heatSource = new IsoHeatSource(this.square.x, this.square.y, this.square.z, this.LightRadius, 35);
                    IsoWorld.instance.CurrentCell.addHeatSource(this.heatSource);
                } else {
                    this.heatSource.setRadius(this.LightRadius);
                }
            } else {
                this.accum = this.accum + GameTime.getInstance().getMultiplier() / 1.6F;

                while (this.accum > 1.0F) {
                    this.accum--;
                    this.Age++;
                    if (this.LifeStageTimer > 0) {
                        this.LifeStageTimer--;
                        if (this.LifeStageTimer <= 0) {
                            switch (this.LifeStage) {
                                case 0:
                                    this.LifeStage = 1;
                                    this.LifeStageTimer = this.LifeStageDuration;
                                    this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -16, -72, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
                                    this.square.Burn();
                                    if (this.LightSource != null) {
                                        this.setLightRadius(5);
                                    }
                                    break;
                                case 1:
                                    this.LifeStage = 2;
                                    this.LifeStageTimer = this.LifeStageDuration;
                                    this.RemoveAttachedAnims();
                                    this.AttachAnim("Smoke", "02", 4, IsoFireManager.SmokeAnimDelay, -9, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
                                    this.AttachAnim("Fire", "02", 4, IsoFireManager.FireAnimDelay, -9, -52, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
                                    this.square.Burn();
                                    if (this.LightSource != null) {
                                        this.setLightRadius(8);
                                    }
                                    break;
                                case 2:
                                    this.LifeStage = 3;
                                    this.LifeStageTimer = this.LifeStageDuration / 3;
                                    this.RemoveAttachedAnims();
                                    this.AttachAnim("Smoke", "03", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
                                    this.AttachAnim("Fire", "03", 4, IsoFireManager.FireAnimDelay, -16, -72, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
                                    if (this.LightSource != null) {
                                        this.setLightRadius(12);
                                    }
                                    break;
                                case 3:
                                    this.LifeStage = 4;
                                    this.LifeStageTimer = this.LifeStageDuration / 3;
                                    this.RemoveAttachedAnims();
                                    this.AttachAnim("Smoke", "02", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
                                    this.AttachAnim("Fire", "02", 4, IsoFireManager.FireAnimDelay, -16, -85, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
                                    if (this.LightSource != null) {
                                        this.setLightRadius(8);
                                    }
                                    break;
                                case 4:
                                    this.LifeStage = 5;
                                    this.LifeStageTimer = this.LifeStageDuration / 3;
                                    this.RemoveAttachedAnims();
                                    this.AttachAnim("Smoke", "01", 4, IsoFireManager.SmokeAnimDelay, -9, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
                                    if (this.LightSource != null) {
                                        this.setLightRadius(1);
                                    }
                            }
                        }
                    }

                    if (this.Life > 0) {
                        this.Life--;
                        if (this.LifeStage > 0 && this.SpreadTimer > 0) {
                            this.SpreadTimer--;
                            if (this.SpreadTimer <= 0) {
                                if (this.LifeStage != 5) {
                                    this.Spread();
                                }

                                this.SpreadTimer = this.SpreadDelay;
                            }
                        }

                        if (this.Energy > 0) {
                            continue;
                        }

                        this.extinctFire();
                        break;
                    }

                    this.extinctFire();
                    break;
                }
            }
        }
    }

    @Override
    public void render(float x, float y, float z, ColorInfo col, boolean bDoChild, boolean bWallLightingPass, Shader shader) {
        x += 0.5F;
        y += 0.5F;
        this.sx = 0.0F;
        this.offsetX = 0.0F;
        this.offsetY = 0.0F;
        float float0 = Core.TileScale;

        for (int int0 = 0; int0 < this.AttachedAnimSprite.size(); int0++) {
            IsoSprite sprite = this.AttachedAnimSprite.get(int0).parentSprite;
            if (sprite != null && sprite.CurrentAnim != null && sprite.def != null) {
                Texture texture = sprite.CurrentAnim.Frames.get((int)sprite.def.Frame).directions[this.dir.index()];
                if (texture != null) {
                    sprite.soffX = (short)(-(texture.getWidthOrig() / 2 * float0));
                    sprite.soffY = (short)(-(texture.getHeightOrig() * float0));
                    this.AttachedAnimSprite.get(int0).setScale(float0, float0);
                }
            }
        }

        super.render(x, y, z, col, bDoChild, bWallLightingPass, shader);
        if (Core.bDebug) {
        }
    }

    public void extinctFire() {
        this.square.getProperties().UnSet(IsoFlagType.burning);
        this.square.getProperties().UnSet(IsoFlagType.smoke);
        this.RemoveAttachedAnims();
        this.square.getObjects().remove(this);
        this.square.RemoveTileObject(this);
        this.setLife(0);
        this.removeFromWorld();
    }

    int getSquaresEnergyRequirement(IsoGridSquare square) {
        int int0 = 30;
        if (square.getProperties().Is(IsoFlagType.vegitation)) {
            int0 = -15;
        }

        if (!square.getProperties().Is(IsoFlagType.exterior)) {
            int0 = 40;
        }

        if (square.getFloor() != null && square.getFloor().getSprite() != null) {
            int0 = square.getFloor().getSprite().firerequirement;
        }

        return TutorialManager.instance.Active ? int0 / 4 : int0;
    }

    /**
     * The more this number is low, the faster it's gonna spread
     */
    public void setSpreadDelay(int _SpreadDelay) {
        this.SpreadDelay = _SpreadDelay;
    }

    /**
     * The more this number is low, the faster it's gonna spread
     */
    public int getSpreadDelay() {
        return this.SpreadDelay;
    }

    /**
     * Up this number to make the fire life longer
     */
    public void setLife(int _Life) {
        this.Life = _Life;
    }

    public int getLife() {
        return this.Life;
    }

    public int getEnergy() {
        return this.Energy;
    }

    public boolean isPermanent() {
        return this.perm;
    }

    public void setLifeStage(int lifeStage) {
        if (this.perm) {
            this.RemoveAttachedAnims();
            switch (lifeStage) {
                case 0:
                    this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -16, -72, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
                    break;
                case 1:
                    this.AttachAnim("Smoke", "02", 4, IsoFireManager.SmokeAnimDelay, -9, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
                    this.AttachAnim("Fire", "02", 4, IsoFireManager.FireAnimDelay, -9, -52, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
                    break;
                case 2:
                    this.AttachAnim("Smoke", "03", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
                    this.AttachAnim("Fire", "03", 4, IsoFireManager.FireAnimDelay, -16, -72, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
                    break;
                case 3:
                    this.AttachAnim("Smoke", "02", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
                    this.AttachAnim("Fire", "02", 4, IsoFireManager.FireAnimDelay, -16, -85, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
                    break;
                case 4:
                    this.AttachAnim("Smoke", "01", 4, IsoFireManager.SmokeAnimDelay, -9, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
            }
        }
    }

    public void setLightRadius(int radius) {
        this.LightRadius = radius;
        if (this.LightSource != null && radius != this.LightSource.getRadius()) {
            this.getCell().removeLamppost(this.LightSource);
            this.LightSource = new IsoLightSource(this.square.getX(), this.square.getY(), this.square.getZ(), 0.61F, 0.165F, 0.0F, this.LightRadius);
            this.getCell().getLamppostPositions().add(this.LightSource);
            IsoGridSquare.RecalcLightTime = -1;
            GameTime.instance.lightSourceUpdate = 100.0F;
        }
    }

    public int getLightRadius() {
        return this.LightRadius;
    }

    @Override
    public void addToWorld() {
        if (this.perm) {
            this.getCell().addToStaticUpdaterObjectList(this);
        } else {
            IsoFireManager.Add(this);
        }
    }

    @Override
    public void removeFromWorld() {
        if (!this.perm) {
            IsoFireManager.Remove(this);
        }

        IsoFireManager.stopSound(this);
        if (this.LightSource != null) {
            this.getCell().removeLamppost(this.LightSource);
            this.LightSource = null;
        }

        if (this.heatSource != null) {
            this.getCell().removeHeatSource(this.heatSource);
            this.heatSource = null;
        }

        super.removeFromWorld();
    }

    @Override
    public void saveChange(String change, KahluaTable tbl, ByteBuffer bb) {
        super.saveChange(change, tbl, bb);
        if ("Energy".equals(change)) {
            bb.putInt(this.Energy);
        } else if ("lightRadius".equals(change)) {
            bb.putInt(this.getLightRadius());
        }
    }

    @Override
    public void loadChange(String change, ByteBuffer bb) {
        super.loadChange(change, bb);
        if ("Energy".equals(change)) {
            this.Energy = bb.getInt();
        }

        if ("lightRadius".equals(change)) {
            int int0 = bb.getInt();
            this.setLightRadius(int0);
        }
    }

    public boolean isCampfire() {
        if (this.getSquare() == null) {
            return false;
        } else {
            IsoObject[] objects = this.getSquare().getObjects().getElements();
            int int0 = 1;

            for (int int1 = this.getSquare().getObjects().size(); int0 < int1; int0++) {
                IsoObject object = objects[int0];
                if (!(object instanceof IsoWorldInventoryObject) && "Campfire".equalsIgnoreCase(object.getName())) {
                    return true;
                }
            }

            return false;
        }
    }
}
