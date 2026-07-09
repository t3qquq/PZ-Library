// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.debug.DebugLog;
import zombie.debug.LogSeverity;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;

public abstract class DeadCharacterPacket implements INetworkPacket {
    public short id;
    protected float x;
    protected float y;
    protected float z;
    protected float angle;
    protected IsoDirections direction;
    protected byte characterFlags;
    protected IsoGameCharacter killer;
    protected IsoGameCharacter character;

    public void set(IsoGameCharacter characterx) {
        this.character = characterx;
        this.id = characterx.getOnlineID();
        this.killer = characterx.getAttackedBy();
        this.x = characterx.getX();
        this.y = characterx.getY();
        this.z = characterx.getZ();
        this.angle = characterx.getAnimAngleRadians();
        this.direction = characterx.getDir();
        this.characterFlags = (byte)(characterx.isFallOnFront() ? 1 : 0);
    }

    public void process() {
        if (this.character != null) {
            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)this.y, (double)this.z);
            if (this.character.getCurrentSquare() != square) {
                DebugLog.Multiplayer
                    .warn(
                        String.format(
                            "Corpse %s(%d) teleport: position (%f ; %f) => (%f ; %f)",
                            this.character.getClass().getSimpleName(),
                            this.id,
                            this.character.x,
                            this.character.y,
                            this.x,
                            this.y
                        )
                    );
                this.character.setX(this.x);
                this.character.setY(this.y);
                this.character.setZ(this.z);
            }

            if (this.character.getAnimAngleRadians() - this.angle > 1.0E-4F) {
                DebugLog.Multiplayer
                    .warn(
                        String.format(
                            "Corpse %s(%d) teleport: direction (%f) => (%f)",
                            this.character.getClass().getSimpleName(),
                            this.id,
                            this.character.getAnimAngleRadians(),
                            this.angle
                        )
                    );
                if (this.character.hasAnimationPlayer()
                    && this.character.getAnimationPlayer().isReady()
                    && !this.character.getAnimationPlayer().isBoneTransformsNeedFirstFrame()) {
                    this.character.getAnimationPlayer().setAngle(this.angle);
                } else {
                    this.character.getForwardDirection().setDirection(this.angle);
                }
            }

            boolean boolean0 = (this.characterFlags & 1) != 0;
            if (boolean0 != this.character.isFallOnFront()) {
                DebugLog.Multiplayer
                    .warn(
                        String.format(
                            "Corpse %s(%d) teleport: pose (%s) => (%s)",
                            this.character.getClass().getSimpleName(),
                            this.id,
                            this.character.isFallOnFront() ? "front" : "back",
                            boolean0 ? "front" : "back"
                        )
                    );
                this.character.setFallOnFront(boolean0);
            }

            this.character.setCurrent(square);
            this.character.dir = this.direction;
            this.character.setAttackedBy(this.killer);
            this.character.becomeCorpse();
        }
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection var2) {
        this.id = byteBuffer.getShort();
        this.x = byteBuffer.getFloat();
        this.y = byteBuffer.getFloat();
        this.z = byteBuffer.getFloat();
        this.angle = byteBuffer.getFloat();
        this.direction = IsoDirections.fromIndex(byteBuffer.get());
        this.characterFlags = byteBuffer.get();
        byte byte0 = byteBuffer.get();
        short short0 = -1;
        if (GameServer.bServer) {
            switch (byte0) {
                case 0:
                    this.killer = null;
                    break;
                case 1:
                    short0 = byteBuffer.getShort();
                    this.killer = ServerMap.instance.ZombieMap.get(short0);
                    break;
                case 2:
                    short0 = byteBuffer.getShort();
                    this.killer = GameServer.IDToPlayerMap.get(short0);
                    break;
                default:
                    Exception exception0 = new Exception("killerIdType:" + byte0);
                    exception0.printStackTrace();
            }
        } else {
            switch (byte0) {
                case 0:
                    this.killer = null;
                    break;
                case 1:
                    short0 = byteBuffer.getShort();
                    this.killer = GameClient.IDToZombieMap.get(short0);
                    break;
                case 2:
                    short0 = byteBuffer.getShort();
                    this.killer = GameClient.IDToPlayerMap.get(short0);
                    break;
                default:
                    Exception exception1 = new Exception("killerIdType:" + byte0);
                    exception1.printStackTrace();
            }
        }
    }

    protected IsoDeadBody getDeadBody() {
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)this.y, (double)this.z);
        if (square != null) {
            for (IsoMovingObject movingObject : square.getStaticMovingObjects()) {
                if (movingObject instanceof IsoDeadBody && ((IsoDeadBody)movingObject).getOnlineID() == this.id) {
                    return (IsoDeadBody)movingObject;
                }
            }
        }

        return null;
    }

    protected void parseDeadBodyInventory(IsoDeadBody deadBody, ByteBuffer byteBuffer) {
        String string = deadBody.readInventory(byteBuffer);
        deadBody.getContainer().setType(string);
    }

    protected void parseDeadBodyHumanVisuals(IsoDeadBody deadBody, ByteBuffer byteBuffer) {
        HumanVisual humanVisual = deadBody.getHumanVisual();
        if (humanVisual != null) {
            try {
                humanVisual.load(byteBuffer, IsoWorld.getWorldVersion());
            } catch (Exception exception) {
                DebugLog.Multiplayer.printException(exception, "Parse dead body HumanVisuals failed", LogSeverity.Error);
            }
        }
    }

    protected void parseCharacterInventory(ByteBuffer byteBuffer) {
        if (this.character != null) {
            if (this.character.getContainer() != null) {
                this.character.getContainer().clear();
            }

            if (this.character.getInventory() != null) {
                this.character.getInventory().clear();
            }

            if (this.character.getWornItems() != null) {
                this.character.getWornItems().clear();
            }

            if (this.character.getAttachedItems() != null) {
                this.character.getAttachedItems().clear();
            }

            this.character.getInventory().setSourceGrid(this.character.getCurrentSquare());
            String string = this.character.readInventory(byteBuffer);
            this.character.getInventory().setType(string);
            this.character.resetModelNextFrame();
        }
    }

    protected void writeCharacterInventory(ByteBufferWriter byteBufferWriter) {
        if (this.character != null) {
            this.character.writeInventory(byteBufferWriter.bb);
        }
    }

    protected void writeCharacterHumanVisuals(ByteBufferWriter byteBufferWriter) {
        if (this.character != null) {
            int int0 = byteBufferWriter.bb.position();
            byteBufferWriter.putByte((byte)1);

            try {
                byteBufferWriter.putBoolean(this.character.isFemale());
                this.character.getVisual().save(byteBufferWriter.bb);
            } catch (Exception exception) {
                byteBufferWriter.bb.put(int0, (byte)0);
                DebugLog.Multiplayer.printException(exception, "Write character HumanVisuals failed", LogSeverity.Error);
            }
        }
    }

    protected void parseCharacterHumanVisuals(ByteBuffer byteBuffer) {
        byte byte0 = byteBuffer.get();
        if (this.character != null && byte0 != 0) {
            try {
                this.character.setFemale(byteBuffer.get() != 0);
                this.character.getVisual().load(byteBuffer, IsoWorld.getWorldVersion());
            } catch (Exception exception) {
                DebugLog.Multiplayer.printException(exception, "Parse character HumanVisuals failed", LogSeverity.Error);
            }
        }
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putShort(this.id);
        byteBufferWriter.putFloat(this.x);
        byteBufferWriter.putFloat(this.y);
        byteBufferWriter.putFloat(this.z);
        byteBufferWriter.putFloat(this.angle);
        byteBufferWriter.putByte((byte)this.direction.index());
        byteBufferWriter.putByte(this.characterFlags);
        if (this.killer == null) {
            byteBufferWriter.putByte((byte)0);
        } else {
            if (this.killer instanceof IsoZombie) {
                byteBufferWriter.putByte((byte)1);
            } else {
                byteBufferWriter.putByte((byte)2);
            }

            byteBufferWriter.putShort(this.killer.getOnlineID());
        }
    }

    @Override
    public String getDescription() {
        String string = this.getDeathDescription() + "\n\t";
        if (this.character != null) {
            string = string + " isDead=" + this.character.isDead();
            string = string + " isOnDeathDone=" + this.character.isOnDeathDone();
            string = string + " isOnKillDone=" + this.character.isOnKillDone();
            string = string + " | health=" + this.character.getHealth();
            if (this.character.getBodyDamage() != null) {
                string = string + " | bodyDamage=" + this.character.getBodyDamage().getOverallBodyHealth();
            }

            string = string
                + " | states=( "
                + this.character.getPreviousActionContextStateName()
                + " > "
                + this.character.getCurrentActionContextStateName()
                + " )";
        }

        return string;
    }

    public String getDeathDescription() {
        return this.getClass().getSimpleName()
            + " id("
            + this.id
            + ") | killer="
            + (this.killer == null ? "Null" : this.killer.getClass().getSimpleName() + "(" + this.killer.getOnlineID() + ")")
            + " | pos=(x="
            + this.x
            + ",y="
            + this.y
            + ",z="
            + this.z
            + ";a="
            + this.angle
            + ") | dir="
            + this.direction.name()
            + " | isFallOnFront="
            + ((this.characterFlags & 1) != 0);
    }

    @Override
    public boolean isConsistent() {
        return this.character != null;
    }
}
