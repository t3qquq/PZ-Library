// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.math.PZMath;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameServer;
import zombie.util.StringUtils;

public class IsoTrap extends IsoObject {
    private int timerBeforeExplosion = 0;
    private int FPS;
    private int sensorRange = 0;
    private int firePower = 0;
    private int fireRange = 0;
    private int explosionPower = 0;
    private int explosionRange = 0;
    private int smokeRange = 0;
    private int noiseRange = 0;
    private int noiseDuration = 0;
    private float noiseStartTime = 0.0F;
    private float lastWorldSoundTime = 0.0F;
    private float extraDamage = 0.0F;
    private int remoteControlID = -1;
    private String countDownSound = null;
    private String explosionSound = null;
    private int lastBeep = 0;
    private HandWeapon weapon;
    private boolean instantExplosion;

    public IsoTrap(IsoCell cell) {
        super(cell);
        this.FPS = GameServer.bServer ? 10 : PerformanceSettings.getLockFPS();
    }

    public IsoTrap(HandWeapon _weapon, IsoCell cell, IsoGridSquare sq) {
        this.square = sq;
        this.initSprite(_weapon);
        this.setSensorRange(_weapon.getSensorRange());
        this.setFireRange(_weapon.getFireRange());
        this.setFirePower(_weapon.getFirePower());
        this.setExplosionPower(_weapon.getExplosionPower());
        this.setExplosionRange(_weapon.getExplosionRange());
        this.setSmokeRange(_weapon.getSmokeRange());
        this.setNoiseRange(_weapon.getNoiseRange());
        this.setNoiseDuration(_weapon.getNoiseDuration());
        this.setExtraDamage(_weapon.getExtraDamage());
        this.setRemoteControlID(_weapon.getRemoteControlID());
        this.setCountDownSound(_weapon.getCountDownSound());
        this.setExplosionSound(_weapon.getExplosionSound());
        this.FPS = GameServer.bServer ? 10 : PerformanceSettings.getLockFPS();
        if (_weapon.getExplosionTimer() > 0) {
            this.timerBeforeExplosion = _weapon.getExplosionTimer() * this.FPS - 1;
        } else if (!_weapon.canBeRemote()) {
            this.timerBeforeExplosion = 1;
        }

        if (_weapon.canBePlaced()) {
            this.weapon = _weapon;
        }

        this.instantExplosion = _weapon.isInstantExplosion();
    }

    private void initSprite(HandWeapon weaponx) {
        if (weaponx != null) {
            String string;
            if (weaponx.getPlacedSprite() != null && !weaponx.getPlacedSprite().isEmpty()) {
                string = weaponx.getPlacedSprite();
            } else if (weaponx.getTex() != null && weaponx.getTex().getName() != null) {
                string = weaponx.getTex().getName();
            } else {
                string = "media/inventory/world/WItem_Sack.png";
            }

            this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
            Texture texture = this.sprite.LoadFrameExplicit(string);
            if (string.startsWith("Item_") && texture != null) {
                if (weaponx.getScriptItem() == null) {
                    this.sprite.def.scaleAspect(texture.getWidthOrig(), texture.getHeightOrig(), 16 * Core.TileScale, 16 * Core.TileScale);
                } else {
                    float float0 = Core.TileScale;
                    float float1 = weaponx.getScriptItem().ScaleWorldIcon * (float0 / 2.0F);
                    this.sprite.def.setScale(float1, float1);
                }
            }
        }
    }

    @Override
    public void update() {
        if (this.timerBeforeExplosion > 0) {
            if (this.timerBeforeExplosion / this.FPS + 1 != this.lastBeep) {
                this.lastBeep = this.timerBeforeExplosion / this.FPS + 1;
                if (!GameServer.bServer && this.getObjectIndex() != -1) {
                    this.getOrCreateEmitter();
                    if (!StringUtils.isNullOrWhitespace(this.getCountDownSound())) {
                        this.emitter.playSound(this.getCountDownSound());
                    } else if (this.lastBeep == 1) {
                        this.emitter.playSound("TrapTimerExpired");
                    } else {
                        this.emitter.playSound("TrapTimerLoop");
                    }
                }
            }

            this.timerBeforeExplosion--;
            if (this.timerBeforeExplosion == 0) {
                this.triggerExplosion(this.getSensorRange() > 0);
            }
        }

        this.updateSounds();
    }

    private void updateSounds() {
        if (this.noiseStartTime > 0.0F) {
            float float0 = (float)GameTime.getInstance().getWorldAgeHours();
            this.noiseStartTime = PZMath.min(this.noiseStartTime, float0);
            this.lastWorldSoundTime = PZMath.min(this.lastWorldSoundTime, float0);
            float float1 = 60.0F / SandboxOptions.getInstance().getDayLengthMinutes();
            float float2 = 60.0F;
            if (float0 - this.noiseStartTime > this.getNoiseDuration() / float2 * float1) {
                this.noiseStartTime = 0.0F;
                if (this.emitter != null) {
                    this.emitter.stopAll();
                }
            } else {
                if (!GameServer.bServer && (this.emitter == null || !this.emitter.isPlaying(this.getExplosionSound()))) {
                    BaseSoundEmitter baseSoundEmitter = this.getOrCreateEmitter();
                    if (baseSoundEmitter != null) {
                        baseSoundEmitter.playSound(this.getExplosionSound());
                    }
                }

                if (float0 - this.lastWorldSoundTime > 1.0F / float2 * float1 && this.getObjectIndex() != -1) {
                    this.lastWorldSoundTime = float0;
                    WorldSoundManager.instance
                        .addSoundRepeating(null, this.getSquare().getX(), this.getSquare().getY(), this.getSquare().getZ(), this.getNoiseRange(), 1, true);
                }
            }
        }

        if (this.emitter != null) {
            this.emitter.tick();
        }
    }

    @Override
    public void render(float x, float y, float z, ColorInfo col, boolean bDoChild, boolean bWallLightingPass, Shader shader) {
        if (this.sprite.CurrentAnim != null && !this.sprite.CurrentAnim.Frames.isEmpty()) {
            Texture texture = this.sprite.CurrentAnim.Frames.get(0).getTexture(this.dir);
            if (texture != null) {
                if (texture.getName().startsWith("Item_")) {
                    float float0 = texture.getWidthOrig() * this.sprite.def.getScaleX() / 2.0F;
                    float float1 = texture.getHeightOrig() * this.sprite.def.getScaleY() * 3.0F / 4.0F;
                    this.setAlphaAndTarget(1.0F);
                    this.offsetX = 0.0F;
                    this.offsetY = 0.0F;
                    this.sx = 0.0F;
                    this.sprite.render(this, x + 0.5F, y + 0.5F, z, this.dir, this.offsetX + float0, this.offsetY + float1, col, true);
                } else {
                    this.offsetX = 32 * Core.TileScale;
                    this.offsetY = 96 * Core.TileScale;
                    this.sx = 0.0F;
                    super.render(x, y, z, col, bDoChild, bWallLightingPass, shader);
                }
            }
        }
    }

    public void triggerExplosion(boolean sensor) {
        LuaEventManager.triggerEvent("OnThrowableExplode", this, this.square);
        if (sensor) {
            if (this.getSensorRange() > 0) {
                this.square.setTrapPositionX(this.square.getX());
                this.square.setTrapPositionY(this.square.getY());
                this.square.setTrapPositionZ(this.square.getZ());
                this.square.drawCircleExplosion(this.getSensorRange(), this, IsoTrap.ExplosionMode.Sensor);
            }
        } else {
            if (this.getExplosionSound() != null) {
                this.playExplosionSound();
            }

            if (this.getNoiseRange() > 0) {
                WorldSoundManager.instance.addSound(null, (int)this.getX(), (int)this.getY(), (int)this.getZ(), this.getNoiseRange(), 1);
            } else if (this.getExplosionSound() != null) {
                WorldSoundManager.instance.addSound(null, (int)this.getX(), (int)this.getY(), (int)this.getZ(), 50, 1);
            }

            if (this.getExplosionRange() > 0) {
                this.square.drawCircleExplosion(this.getExplosionRange(), this, IsoTrap.ExplosionMode.Explosion);
            }

            if (this.getFireRange() > 0) {
                this.square.drawCircleExplosion(this.getFireRange(), this, IsoTrap.ExplosionMode.Fire);
            }

            if (this.getSmokeRange() > 0) {
                this.square.drawCircleExplosion(this.getSmokeRange(), this, IsoTrap.ExplosionMode.Smoke);
            }

            if (this.weapon == null || !this.weapon.canBeReused()) {
                if (GameServer.bServer) {
                    GameServer.RemoveItemFromMap(this);
                } else {
                    this.removeFromWorld();
                    this.removeFromSquare();
                }
            }
        }
    }

    private BaseSoundEmitter getOrCreateEmitter() {
        if (this.getObjectIndex() == -1) {
            return null;
        } else {
            if (this.emitter == null) {
                this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, this.getZ());
                IsoWorld.instance.takeOwnershipOfEmitter(this.emitter);
            }

            return this.emitter;
        }
    }

    public void playExplosionSound() {
        if (!StringUtils.isNullOrWhitespace(this.getExplosionSound())) {
            if (this.getObjectIndex() != -1) {
                if (this.getNoiseRange() > 0 && this.getNoiseDuration() > 0.0F) {
                    this.noiseStartTime = (float)GameTime.getInstance().getWorldAgeHours();
                }

                if (!GameServer.bServer) {
                    this.getOrCreateEmitter();
                    if (!this.emitter.isPlaying(this.getExplosionSound())) {
                        this.emitter.playSoundImpl(this.getExplosionSound(), (IsoObject)null);
                    }
                }
            }
        }
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(input, WorldVersion, IS_DEBUG_SAVE);
        this.sensorRange = input.getInt();
        this.firePower = input.getInt();
        this.fireRange = input.getInt();
        this.explosionPower = input.getInt();
        this.explosionRange = input.getInt();
        this.smokeRange = input.getInt();
        this.noiseRange = input.getInt();
        if (WorldVersion >= 180) {
            this.noiseDuration = input.getInt();
            this.noiseStartTime = input.getFloat();
        }

        this.extraDamage = input.getFloat();
        this.remoteControlID = input.getInt();
        if (WorldVersion >= 78) {
            this.timerBeforeExplosion = input.getInt() * this.FPS;
            this.countDownSound = GameWindow.ReadStringUTF(input);
            this.explosionSound = GameWindow.ReadStringUTF(input);
            if ("bigExplosion".equals(this.explosionSound)) {
                this.explosionSound = "BigExplosion";
            }

            if ("smallExplosion".equals(this.explosionSound)) {
                this.explosionSound = "SmallExplosion";
            }

            if ("feedback".equals(this.explosionSound)) {
                this.explosionSound = "NoiseTrapExplosion";
            }
        }

        if (WorldVersion >= 82) {
            boolean boolean0 = input.get() == 1;
            if (boolean0) {
                InventoryItem item = InventoryItem.loadItem(input, WorldVersion);
                if (item instanceof HandWeapon) {
                    this.weapon = (HandWeapon)item;
                    this.initSprite(this.weapon);
                }
            }
        }
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        super.save(output, IS_DEBUG_SAVE);
        output.putInt(this.sensorRange);
        output.putInt(this.firePower);
        output.putInt(this.fireRange);
        output.putInt(this.explosionPower);
        output.putInt(this.explosionRange);
        output.putInt(this.smokeRange);
        output.putInt(this.noiseRange);
        output.putInt(this.noiseDuration);
        output.putFloat(this.noiseStartTime);
        output.putFloat(this.extraDamage);
        output.putInt(this.remoteControlID);
        output.putInt(this.timerBeforeExplosion > 1 ? Math.max(this.timerBeforeExplosion / this.FPS, 1) : 0);
        GameWindow.WriteStringUTF(output, this.countDownSound);
        GameWindow.WriteStringUTF(output, this.explosionSound);
        if (this.weapon != null) {
            output.put((byte)1);
            this.weapon.saveWithSize(output, false);
        } else {
            output.put((byte)0);
        }
    }

    @Override
    public void addToWorld() {
        this.getCell().addToProcessIsoObject(this);
    }

    @Override
    public void removeFromWorld() {
        if (this.emitter != null) {
            if (this.noiseStartTime > 0.0F) {
                this.emitter.stopAll();
            }

            IsoWorld.instance.returnOwnershipOfEmitter(this.emitter);
            this.emitter = null;
        }

        super.removeFromWorld();
    }

    public int getTimerBeforeExplosion() {
        return this.timerBeforeExplosion;
    }

    public void setTimerBeforeExplosion(int _timerBeforeExplosion) {
        this.timerBeforeExplosion = _timerBeforeExplosion;
    }

    public int getSensorRange() {
        return this.sensorRange;
    }

    public void setSensorRange(int _sensorRange) {
        this.sensorRange = _sensorRange;
    }

    public int getFireRange() {
        return this.fireRange;
    }

    public void setFireRange(int _fireRange) {
        this.fireRange = _fireRange;
    }

    public int getFirePower() {
        return this.firePower;
    }

    public void setFirePower(int _firePower) {
        this.firePower = _firePower;
    }

    public int getExplosionPower() {
        return this.explosionPower;
    }

    public void setExplosionPower(int _explosionPower) {
        this.explosionPower = _explosionPower;
    }

    public int getNoiseDuration() {
        return this.noiseDuration;
    }

    public void setNoiseDuration(int _noiseDuration) {
        this.noiseDuration = _noiseDuration;
    }

    public int getNoiseRange() {
        return this.noiseRange;
    }

    public void setNoiseRange(int _noiseRange) {
        this.noiseRange = _noiseRange;
    }

    public int getExplosionRange() {
        return this.explosionRange;
    }

    public void setExplosionRange(int _explosionRange) {
        this.explosionRange = _explosionRange;
    }

    public int getSmokeRange() {
        return this.smokeRange;
    }

    public void setSmokeRange(int _smokeRange) {
        this.smokeRange = _smokeRange;
    }

    public float getExtraDamage() {
        return this.extraDamage;
    }

    public void setExtraDamage(float _extraDamage) {
        this.extraDamage = _extraDamage;
    }

    @Override
    public String getObjectName() {
        return "IsoTrap";
    }

    public int getRemoteControlID() {
        return this.remoteControlID;
    }

    public void setRemoteControlID(int _remoteControlID) {
        this.remoteControlID = _remoteControlID;
    }

    public String getCountDownSound() {
        return this.countDownSound;
    }

    public void setCountDownSound(String sound) {
        this.countDownSound = sound;
    }

    public String getExplosionSound() {
        return this.explosionSound;
    }

    public void setExplosionSound(String _explosionSound) {
        this.explosionSound = _explosionSound;
    }

    public InventoryItem getItem() {
        return this.weapon;
    }

    public static void triggerRemote(IsoPlayer player, int remoteID, int range) {
        int int0 = (int)player.getX();
        int int1 = (int)player.getY();
        int int2 = (int)player.getZ();
        int int3 = Math.max(int2 - range / 2, 0);
        int int4 = Math.min(int2 + range / 2, 8);
        IsoCell cell = IsoWorld.instance.CurrentCell;

        for (int int5 = int3; int5 < int4; int5++) {
            for (int int6 = int1 - range; int6 < int1 + range; int6++) {
                for (int int7 = int0 - range; int7 < int0 + range; int7++) {
                    IsoGridSquare square = cell.getGridSquare(int7, int6, int5);
                    if (square != null) {
                        for (int int8 = square.getObjects().size() - 1; int8 >= 0; int8--) {
                            IsoObject object = square.getObjects().get(int8);
                            if (object instanceof IsoTrap && ((IsoTrap)object).getRemoteControlID() == remoteID) {
                                ((IsoTrap)object).triggerExplosion(false);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isInstantExplosion() {
        return this.instantExplosion;
    }

    public static enum ExplosionMode {
        Explosion,
        Fire,
        Smoke,
        Sensor;
    }
}
