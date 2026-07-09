// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.packets.INetworkPacket;

public abstract class HitCharacterPacket implements INetworkPacket {
    private final HitCharacterPacket.HitType hitType;

    public HitCharacterPacket(HitCharacterPacket.HitType hitTypex) {
        this.hitType = hitTypex;
    }

    public static HitCharacterPacket process(ByteBuffer byteBuffer) {
        byte byte0 = byteBuffer.get();
        if (byte0 > HitCharacterPacket.HitType.Min.ordinal() && byte0 < HitCharacterPacket.HitType.Max.ordinal()) {
            return (HitCharacterPacket)(switch (HitCharacterPacket.HitType.values()[byte0]) {
                case PlayerHitSquare -> new PlayerHitSquarePacket();
                case PlayerHitVehicle -> new PlayerHitVehiclePacket();
                case PlayerHitZombie -> new PlayerHitZombiePacket();
                case PlayerHitPlayer -> new PlayerHitPlayerPacket();
                case ZombieHitPlayer -> new ZombieHitPlayerPacket();
                case VehicleHitZombie -> new VehicleHitZombiePacket();
                case VehicleHitPlayer -> new VehicleHitPlayerPacket();
                default -> null;
            });
        } else {
            return null;
        }
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putByte((byte)this.hitType.ordinal());
    }

    @Override
    public String getDescription() {
        return INetworkPacket.super.getDescription() + " (" + this.hitType.name() + ")";
    }

    public void tryProcess() {
        if (!GameClient.bClient
            || !HitCharacterPacket.HitType.VehicleHitZombie.equals(this.hitType) && !HitCharacterPacket.HitType.VehicleHitPlayer.equals(this.hitType)) {
            this.tryProcessInternal();
        } else {
            this.postpone();
        }
    }

    public void tryProcessInternal() {
        if (this.isConsistent()) {
            this.preProcess();
            this.process();
            this.postProcess();
            this.attack();
            this.react();
        } else {
            DebugLog.Multiplayer.warn("HitCharacter: check error");
        }
    }

    public abstract boolean isRelevant(UdpConnection var1);

    protected abstract void attack();

    protected abstract void react();

    protected void preProcess() {
    }

    protected void process() {
    }

    protected void postProcess() {
    }

    protected void postpone() {
    }

    public abstract boolean validate(UdpConnection var1);

    public static enum HitType {
        Min,
        PlayerHitSquare,
        PlayerHitVehicle,
        PlayerHitZombie,
        PlayerHitPlayer,
        ZombieHitPlayer,
        VehicleHitZombie,
        VehicleHitPlayer,
        Max;
    }
}
