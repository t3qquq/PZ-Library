// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.SystemDisabler;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.opengl.Shader;
import zombie.core.properties.PropertyContainer;
import zombie.core.raknet.UdpConnection;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.util.Type;
import zombie.util.list.PZArrayList;

public class IsoCurtain extends IsoObject {
    public boolean Barricaded = false;
    public Integer BarricideMaxStrength = 0;
    public Integer BarricideStrength = 0;
    public Integer Health = 1000;
    public boolean Locked = false;
    public Integer MaxHealth = 1000;
    public Integer PushedMaxStrength = 0;
    public Integer PushedStrength = 0;
    IsoSprite closedSprite;
    public boolean north = false;
    public boolean open = false;
    IsoSprite openSprite;
    private boolean destroyed = false;

    public void removeSheet(IsoGameCharacter chr) {
        this.square.transmitRemoveItemFromSquare(this);
        if (GameServer.bServer) {
            chr.sendObjectChange("addItemOfType", new Object[]{"type", "Base.Sheet"});
        } else {
            chr.getInventory().AddItem("Base.Sheet");
        }

        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            LosUtil.cachecleared[int0] = true;
        }

        GameTime.instance.lightSourceUpdate = 100.0F;
        IsoGridSquare.setRecalcLightTime(-1);
    }

    public IsoCurtain(IsoCell cell, IsoGridSquare gridSquare, IsoSprite gid, boolean _north, boolean spriteclosed) {
        this.OutlineOnMouseover = true;
        this.PushedMaxStrength = this.PushedStrength = 2500;
        if (spriteclosed) {
            this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, gid, 4);
            this.closedSprite = gid;
        } else {
            this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, gid, -4);
            this.openSprite = gid;
        }

        this.open = true;
        this.sprite = this.openSprite;
        this.square = gridSquare;
        this.north = _north;
        this.DirtySlice();
    }

    public IsoCurtain(IsoCell cell, IsoGridSquare gridSquare, String gid, boolean _north) {
        this.OutlineOnMouseover = true;
        this.PushedMaxStrength = this.PushedStrength = 2500;
        this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, gid, -4);
        this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, gid, 0);
        this.open = true;
        this.sprite = this.openSprite;
        this.square = gridSquare;
        this.north = _north;
        this.DirtySlice();
    }

    public IsoCurtain(IsoCell cell) {
        super(cell);
    }

    @Override
    public String getObjectName() {
        return "Curtain";
    }

    @Override
    public Vector2 getFacingPosition(Vector2 pos) {
        if (this.square == null) {
            return pos.set(0.0F, 0.0F);
        } else if (this.getType() == IsoObjectType.curtainS) {
            return pos.set(this.getX() + 0.5F, this.getY() + 1.0F);
        } else if (this.getType() == IsoObjectType.curtainE) {
            return pos.set(this.getX() + 1.0F, this.getY() + 0.5F);
        } else {
            return this.north ? pos.set(this.getX() + 0.5F, this.getY()) : pos.set(this.getX(), this.getY() + 0.5F);
        }
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(input, WorldVersion, IS_DEBUG_SAVE);
        this.open = input.get() == 1;
        this.north = input.get() == 1;
        this.Health = input.getInt();
        this.BarricideStrength = input.getInt();
        if (this.open) {
            this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, input.getInt());
            this.openSprite = this.sprite;
        } else {
            this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, input.getInt());
            this.closedSprite = this.sprite;
        }

        if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
            GameClient.instance.objectSyncReq.putRequestLoad(this.square);
        }
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        super.save(output, IS_DEBUG_SAVE);
        output.put((byte)(this.open ? 1 : 0));
        output.put((byte)(this.north ? 1 : 0));
        output.putInt(this.Health);
        output.putInt(this.BarricideStrength);
        if (this.open) {
            output.putInt(this.closedSprite.ID);
        } else {
            output.putInt(this.openSprite.ID);
        }
    }

    public boolean getNorth() {
        return this.north;
    }

    public boolean IsOpen() {
        return this.open;
    }

    @Override
    public boolean onMouseLeftClick(int x, int y) {
        return false;
    }

    public boolean canInteractWith(IsoGameCharacter chr) {
        if (chr != null && chr.getCurrentSquare() != null) {
            IsoGridSquare square = chr.getCurrentSquare();
            return (this.isAdjacentToSquare(square) || square == this.getOppositeSquare()) && !this.getSquare().isBlockedTo(square);
        } else {
            return false;
        }
    }

    public IsoGridSquare getOppositeSquare() {
        if (this.getType() == IsoObjectType.curtainN) {
            return this.getCell().getGridSquare((double)this.getX(), (double)(this.getY() - 1.0F), (double)this.getZ());
        } else if (this.getType() == IsoObjectType.curtainS) {
            return this.getCell().getGridSquare((double)this.getX(), (double)(this.getY() + 1.0F), (double)this.getZ());
        } else if (this.getType() == IsoObjectType.curtainW) {
            return this.getCell().getGridSquare((double)(this.getX() - 1.0F), (double)this.getY(), (double)this.getZ());
        } else {
            return this.getType() == IsoObjectType.curtainE
                ? this.getCell().getGridSquare((double)(this.getX() + 1.0F), (double)this.getY(), (double)this.getZ())
                : null;
        }
    }

    public boolean isAdjacentToSquare(IsoGridSquare square1, IsoGridSquare square2) {
        if (square1 != null && square2 != null) {
            return this.getType() != IsoObjectType.curtainN && this.getType() != IsoObjectType.curtainS
                ? square1.x == square2.x && Math.abs(square1.y - square2.y) <= 1
                : square1.y == square2.y && Math.abs(square1.x - square2.x) <= 1;
        } else {
            return false;
        }
    }

    public boolean isAdjacentToSquare(IsoGridSquare square2) {
        return this.isAdjacentToSquare(this.getSquare(), square2);
    }

    @Override
    public IsoObject.VisionResult TestVision(IsoGridSquare from, IsoGridSquare to) {
        if (to.getZ() != from.getZ()) {
            return IsoObject.VisionResult.NoEffect;
        } else {
            if (from == this.square && (this.getType() == IsoObjectType.curtainW || this.getType() == IsoObjectType.curtainN)
                || from != this.square && (this.getType() == IsoObjectType.curtainE || this.getType() == IsoObjectType.curtainS)) {
                if (this.north && to.getY() < from.getY() && !this.open) {
                    return IsoObject.VisionResult.Blocked;
                }

                if (!this.north && to.getX() < from.getX() && !this.open) {
                    return IsoObject.VisionResult.Blocked;
                }
            } else {
                if (this.north && to.getY() > from.getY() && !this.open) {
                    return IsoObject.VisionResult.Blocked;
                }

                if (!this.north && to.getX() > from.getX() && !this.open) {
                    return IsoObject.VisionResult.Blocked;
                }
            }

            return IsoObject.VisionResult.NoEffect;
        }
    }

    public void ToggleDoor(IsoGameCharacter chr) {
        if (!this.Barricaded) {
            this.DirtySlice();
            if (!this.Locked || chr == null || chr.getCurrentSquare().getRoom() != null || this.open) {
                this.open = !this.open;
                this.sprite = this.closedSprite;
                if (this.open) {
                    this.sprite = this.openSprite;
                    if (chr != null) {
                        chr.playSound(this.getSoundPrefix() + "Open");
                    }
                } else if (chr != null) {
                    chr.playSound(this.getSoundPrefix() + "Close");
                }

                this.syncIsoObject(false, (byte)(this.open ? 1 : 0), null);
            }
        }
    }

    public void ToggleDoorSilent() {
        if (!this.Barricaded) {
            this.DirtySlice();

            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                LosUtil.cachecleared[int0] = true;
            }

            GameTime.instance.lightSourceUpdate = 100.0F;
            IsoGridSquare.setRecalcLightTime(-1);
            this.open = !this.open;
            this.sprite = this.closedSprite;
            if (this.open) {
                this.sprite = this.openSprite;
            }

            this.syncIsoObject(false, (byte)(this.open ? 1 : 0), null);
        }
    }

    @Override
    public void render(float x, float y, float z, ColorInfo col, boolean bDoAttached, boolean bWallLightingPass, Shader shader) {
        int int0 = IsoCamera.frameState.playerIndex;
        IsoObject object = this.getObjectAttachedTo();
        if (object != null && this.getSquare().getTargetDarkMulti(int0) <= object.getSquare().getTargetDarkMulti(int0)) {
            col = object.getSquare().lighting[int0].lightInfo();
            this.setTargetAlpha(int0, object.getTargetAlpha(int0));
        }

        super.render(x, y, z, col, bDoAttached, bWallLightingPass, shader);
    }

    @Override
    public void syncIsoObjectSend(ByteBufferWriter b) {
        b.putInt(this.square.getX());
        b.putInt(this.square.getY());
        b.putInt(this.square.getZ());
        byte byte0 = (byte)this.square.getObjects().indexOf(this);
        b.putByte(byte0);
        b.putByte((byte)1);
        b.putByte((byte)(this.open ? 1 : 0));
    }

    @Override
    public void syncIsoObject(boolean bRemote, byte val, UdpConnection source, ByteBuffer bb) {
        this.syncIsoObject(bRemote, val, source);
    }

    public void syncIsoObject(boolean bRemote, byte val, UdpConnection source) {
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
        } else {
            if (GameClient.bClient && !bRemote) {
                ByteBufferWriter byteBufferWriter0 = GameClient.connection.startPacket();
                PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter0);
                this.syncIsoObjectSend(byteBufferWriter0);
                PacketTypes.PacketType.SyncIsoObject.send(GameClient.connection);
            } else if (bRemote) {
                if (val == 1) {
                    this.open = true;
                    this.sprite = this.openSprite;
                } else {
                    this.open = false;
                    this.sprite = this.closedSprite;
                }

                if (GameServer.bServer) {
                    for (UdpConnection udpConnection : GameServer.udpEngine.connections) {
                        if (source != null && udpConnection.getConnectedGUID() != source.getConnectedGUID()) {
                            ByteBufferWriter byteBufferWriter1 = udpConnection.startPacket();
                            PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter1);
                            this.syncIsoObjectSend(byteBufferWriter1);
                            PacketTypes.PacketType.SyncIsoObject.send(udpConnection);
                        }
                    }
                }
            }

            this.square.RecalcProperties();
            this.square.RecalcAllWithNeighbours(true);

            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                LosUtil.cachecleared[int0] = true;
            }

            IsoGridSquare.setRecalcLightTime(-1);
            GameTime.instance.lightSourceUpdate = 100.0F;
            LuaEventManager.triggerEvent("OnContainerUpdate");
            if (this.square != null) {
                this.square.RecalcProperties();
            }
        }
    }

    public IsoObject getObjectAttachedTo() {
        int int0 = this.getObjectIndex();
        if (int0 == -1) {
            return null;
        } else {
            PZArrayList pZArrayList = this.getSquare().getObjects();
            if (this.getType() != IsoObjectType.curtainW && this.getType() != IsoObjectType.curtainN) {
                if (this.getType() == IsoObjectType.curtainE || this.getType() == IsoObjectType.curtainS) {
                    IsoGridSquare square = this.getOppositeSquare();
                    if (square != null) {
                        boolean boolean0 = this.getType() == IsoObjectType.curtainS;
                        pZArrayList = square.getObjects();

                        for (int int1 = pZArrayList.size() - 1; int1 >= 0; int1--) {
                            BarricadeAble barricadeAble0 = Type.tryCastTo((IsoObject)pZArrayList.get(int1), BarricadeAble.class);
                            if (barricadeAble0 != null && boolean0 == barricadeAble0.getNorth()) {
                                return (IsoObject)pZArrayList.get(int1);
                            }
                        }
                    }
                }
            } else {
                boolean boolean1 = this.getType() == IsoObjectType.curtainN;

                for (int int2 = int0 - 1; int2 >= 0; int2--) {
                    BarricadeAble barricadeAble1 = Type.tryCastTo((IsoObject)pZArrayList.get(int2), BarricadeAble.class);
                    if (barricadeAble1 != null && boolean1 == barricadeAble1.getNorth()) {
                        return (IsoObject)pZArrayList.get(int2);
                    }
                }
            }

            return null;
        }
    }

    public String getSoundPrefix() {
        if (this.closedSprite == null) {
            return "CurtainShort";
        } else {
            PropertyContainer propertyContainer = this.closedSprite.getProperties();
            return propertyContainer.Is("CurtainSound") ? "Curtain" + propertyContainer.Val("CurtainSound") : "CurtainShort";
        }
    }

    public static boolean isSheet(IsoObject curtain) {
        if (curtain instanceof IsoDoor) {
            curtain = ((IsoDoor)curtain).HasCurtains();
        }

        if (curtain instanceof IsoThumpable) {
            curtain = ((IsoThumpable)curtain).HasCurtains();
        }

        if (curtain instanceof IsoWindow) {
            curtain = ((IsoWindow)curtain).HasCurtains();
        }

        if (curtain != null && ((IsoObject)curtain).getSprite() != null) {
            IsoSprite sprite = ((IsoObject)curtain).getSprite();
            return sprite.getProperties().Is("CurtainSound") ? "Sheet".equals(sprite.getProperties().Val("CurtainSound")) : false;
        } else {
            return false;
        }
    }
}
