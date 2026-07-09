// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoWorld;
import zombie.iso.LightingJNI;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.radio.ZomboidRadio;

public class IsoTelevision extends IsoWaveSignal {
    protected ArrayList<IsoSprite> screenSprites = new ArrayList<>();
    protected boolean defaultToNoise = false;
    private IsoSprite cacheObjectSprite;
    protected IsoDirections facing = IsoDirections.Max;
    private boolean hasSetupScreens = false;
    private boolean tickIsLightUpdate = false;
    private IsoTelevision.Screens currentScreen = IsoTelevision.Screens.OFFSCREEN;
    private int spriteIndex = 0;

    @Override
    public String getObjectName() {
        return "Television";
    }

    public IsoTelevision(IsoCell cell) {
        super(cell);
    }

    public IsoTelevision(IsoCell cell, IsoGridSquare sq, IsoSprite spr) {
        super(cell, sq, spr);
    }

    @Override
    protected void init(boolean boolean0) {
        super.init(boolean0);
    }

    private void setupDefaultScreens() {
        this.hasSetupScreens = true;
        this.cacheObjectSprite = this.sprite;
        if (this.screenSprites.size() == 0) {
            for (byte byte0 = 16; byte0 <= 64; byte0 += 16) {
                IsoSprite sprite = IsoSprite.getSprite(IsoSpriteManager.instance, this.sprite.getName(), byte0);
                if (sprite != null) {
                    this.addTvScreenSprite(sprite);
                }
            }
        }

        this.facing = IsoDirections.Max;
        if (this.sprite != null && this.sprite.getProperties().Is("Facing")) {
            String string = this.sprite.getProperties().Val("Facing");
            switch (string) {
                case "N":
                    this.facing = IsoDirections.N;
                    break;
                case "S":
                    this.facing = IsoDirections.S;
                    break;
                case "W":
                    this.facing = IsoDirections.W;
                    break;
                case "E":
                    this.facing = IsoDirections.E;
            }
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.cacheObjectSprite != null && this.cacheObjectSprite != this.sprite) {
            this.hasSetupScreens = false;
            this.screenSprites.clear();
            this.currentScreen = IsoTelevision.Screens.OFFSCREEN;
            this.nextLightUpdate = 0.0F;
        }

        if (!this.hasSetupScreens) {
            this.setupDefaultScreens();
        }

        this.updateTvScreen();
    }

    @Override
    protected void updateLightSource() {
        this.tickIsLightUpdate = false;
        if (this.lightSource == null) {
            this.lightSource = new IsoLightSource(this.square.getX(), this.square.getY(), this.square.getZ(), 0.0F, 0.0F, 1.0F, this.lightSourceRadius);
            this.lightWasRemoved = true;
        }

        if (this.lightWasRemoved) {
            IsoWorld.instance.CurrentCell.addLamppost(this.lightSource);
            IsoGridSquare.RecalcLightTime = -1;
            GameTime.instance.lightSourceUpdate = 100.0F;
            this.lightWasRemoved = false;
        }

        this.lightUpdateCnt = this.lightUpdateCnt + GameTime.getInstance().getMultiplier();
        if (this.lightUpdateCnt >= this.nextLightUpdate) {
            float float0 = 300.0F;
            float float1 = 0.0F;
            if (!this.hasChatToDisplay()) {
                float1 = 0.6F;
                float0 = Rand.Next(200, 400);
            } else {
                float0 = Rand.Next(15, 300);
            }

            float float2 = Rand.Next(float1, 1.0F);
            this.tickIsLightUpdate = true;
            float float3 = 0.58F + 0.25F * float2;
            float float4 = Rand.Next(0.65F, 0.85F);
            int int0 = 1 + (int)((this.lightSourceRadius - 1) * float2);
            IsoGridSquare.RecalcLightTime = -1;
            GameTime.instance.lightSourceUpdate = 100.0F;
            this.lightSource.setRadius(int0);
            this.lightSource.setR(float3);
            this.lightSource.setG(float4);
            this.lightSource.setB(float4);
            if (LightingJNI.init && this.lightSource.ID != 0) {
                LightingJNI.setLightColor(this.lightSource.ID, this.lightSource.getR(), this.lightSource.getG(), this.lightSource.getB());
            }

            this.lightUpdateCnt = 0.0F;
            this.nextLightUpdate = float0;
        }
    }

    private void setScreen(IsoTelevision.Screens screens) {
        if (screens == IsoTelevision.Screens.OFFSCREEN) {
            this.currentScreen = IsoTelevision.Screens.OFFSCREEN;
            if (this.overlaySprite != null) {
                this.overlaySprite = null;
            }
        } else {
            if (this.currentScreen != screens || screens == IsoTelevision.Screens.ALTERNATESCREEN) {
                this.currentScreen = screens;
                IsoSprite sprite = null;
                switch (screens) {
                    case TESTSCREEN:
                        if (this.screenSprites.size() > 0) {
                            sprite = this.screenSprites.get(0);
                        }
                        break;
                    case DEFAULTSCREEN:
                        if (this.screenSprites.size() > 1) {
                            sprite = this.screenSprites.get(1);
                        }
                        break;
                    case ALTERNATESCREEN:
                        if (this.screenSprites.size() >= 2) {
                            if (this.screenSprites.size() == 2) {
                                sprite = this.screenSprites.get(1);
                            } else if (this.screenSprites.size() > 2) {
                                this.spriteIndex++;
                                if (this.spriteIndex < 1) {
                                    this.spriteIndex = 1;
                                }

                                if (this.spriteIndex > this.screenSprites.size() - 1) {
                                    this.spriteIndex = 1;
                                }

                                sprite = this.screenSprites.get(this.spriteIndex);
                            }
                        }
                }

                this.overlaySprite = sprite;
            }
        }
    }

    protected void updateTvScreen() {
        if (this.deviceData != null && this.deviceData.getIsTurnedOn() && this.screenSprites.size() > 0) {
            if (!this.deviceData.isReceivingSignal() && !this.deviceData.isPlayingMedia()) {
                if (ZomboidRadio.POST_RADIO_SILENCE) {
                    this.setScreen(IsoTelevision.Screens.TESTSCREEN);
                } else {
                    this.setScreen(IsoTelevision.Screens.DEFAULTSCREEN);
                }
            } else if (this.tickIsLightUpdate || this.currentScreen != IsoTelevision.Screens.ALTERNATESCREEN) {
                this.setScreen(IsoTelevision.Screens.ALTERNATESCREEN);
            }
        } else if (this.currentScreen != IsoTelevision.Screens.OFFSCREEN) {
            this.setScreen(IsoTelevision.Screens.OFFSCREEN);
        }
    }

    public void addTvScreenSprite(IsoSprite sprite) {
        this.screenSprites.add(sprite);
    }

    public void clearTvScreenSprites() {
        this.screenSprites.clear();
    }

    public void removeTvScreenSprite(IsoSprite sprite) {
        this.screenSprites.remove(sprite);
    }

    @Override
    public void renderlast() {
        super.renderlast();
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(input, WorldVersion, IS_DEBUG_SAVE);
        this.overlaySprite = null;
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        super.save(output, IS_DEBUG_SAVE);
    }

    public boolean isFacing(IsoPlayer player) {
        if (player != null && player.isLocalPlayer()) {
            if (this.getObjectIndex() == -1) {
                return false;
            } else if (!this.square.isCanSee(player.PlayerIndex)) {
                return false;
            } else if (this.facing == IsoDirections.Max) {
                return false;
            } else {
                switch (this.facing) {
                    case N:
                        if (player.y >= this.square.y) {
                            return false;
                        }

                        return player.dir == IsoDirections.SW || player.dir == IsoDirections.S || player.dir == IsoDirections.SE;
                    case S:
                        if (player.y < this.square.y + 1) {
                            return false;
                        }

                        return player.dir == IsoDirections.NW || player.dir == IsoDirections.N || player.dir == IsoDirections.NE;
                    case W:
                        if (player.x >= this.square.x) {
                            return false;
                        }

                        return player.dir == IsoDirections.SE || player.dir == IsoDirections.E || player.dir == IsoDirections.NE;
                    case E:
                        if (player.x < this.square.x + 1) {
                            return false;
                        }

                        return player.dir == IsoDirections.SW || player.dir == IsoDirections.W || player.dir == IsoDirections.NW;
                    default:
                        return false;
                }
            }
        } else {
            return false;
        }
    }

    private static enum Screens {
        OFFSCREEN,
        TESTSCREEN,
        DEFAULTSCREEN,
        ALTERNATESCREEN;
    }
}
