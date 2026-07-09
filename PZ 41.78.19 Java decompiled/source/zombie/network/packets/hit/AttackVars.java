// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.characters.IsoLivingCharacter;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.network.packets.INetworkPacket;

public class AttackVars implements INetworkPacket {
    private boolean isBareHeadsWeapon;
    public MovingObject targetOnGround = new MovingObject();
    public boolean bAimAtFloor;
    public boolean bCloseKill;
    public boolean bDoShove;
    public float useChargeDelta;
    public int recoilDelay;
    public final ArrayList<HitInfo> targetsStanding = new ArrayList<>();
    public final ArrayList<HitInfo> targetsProne = new ArrayList<>();

    public void setWeapon(HandWeapon weapon) {
        this.isBareHeadsWeapon = "BareHands".equals(weapon.getType());
    }

    public HandWeapon getWeapon(IsoLivingCharacter livingCharacter) {
        return !this.isBareHeadsWeapon && livingCharacter.getUseHandWeapon() != null ? livingCharacter.getUseHandWeapon() : livingCharacter.bareHands;
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        byte byte0 = byteBuffer.get();
        this.isBareHeadsWeapon = (byte0 & 1) != 0;
        this.bAimAtFloor = (byte0 & 2) != 0;
        this.bCloseKill = (byte0 & 4) != 0;
        this.bDoShove = (byte0 & 8) != 0;
        this.targetOnGround.parse(byteBuffer, udpConnection);
        this.useChargeDelta = byteBuffer.getFloat();
        this.recoilDelay = byteBuffer.getInt();
        byte byte1 = byteBuffer.get();
        this.targetsStanding.clear();

        for (int int0 = 0; int0 < byte1; int0++) {
            HitInfo hitInfo0 = new HitInfo();
            hitInfo0.parse(byteBuffer, udpConnection);
            this.targetsStanding.add(hitInfo0);
        }

        byte1 = byteBuffer.get();
        this.targetsProne.clear();

        for (int int1 = 0; int1 < byte1; int1++) {
            HitInfo hitInfo1 = new HitInfo();
            hitInfo1.parse(byteBuffer, udpConnection);
            this.targetsProne.add(hitInfo1);
        }
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        byte byte0 = 0;
        byte0 = (byte)(byte0 | (byte)(this.isBareHeadsWeapon ? 1 : 0));
        byte0 = (byte)(byte0 | (byte)(this.bAimAtFloor ? 2 : 0));
        byte0 = (byte)(byte0 | (byte)(this.bCloseKill ? 4 : 0));
        byte0 = (byte)(byte0 | (byte)(this.bDoShove ? 8 : 0));
        byteBufferWriter.putByte(byte0);
        this.targetOnGround.write(byteBufferWriter);
        byteBufferWriter.putFloat(this.useChargeDelta);
        byteBufferWriter.putInt(this.recoilDelay);
        byte byte1 = (byte)Math.min(100, this.targetsStanding.size());
        byteBufferWriter.putByte(byte1);

        for (int int0 = 0; int0 < byte1; int0++) {
            HitInfo hitInfo0 = this.targetsStanding.get(int0);
            hitInfo0.write(byteBufferWriter);
        }

        byte1 = (byte)Math.min(100, this.targetsProne.size());
        byteBufferWriter.putByte(byte1);

        for (int int1 = 0; int1 < byte1; int1++) {
            HitInfo hitInfo1 = this.targetsProne.get(int1);
            hitInfo1.write(byteBufferWriter);
        }
    }

    @Override
    public int getPacketSizeBytes() {
        int int0 = 11 + this.targetOnGround.getPacketSizeBytes();
        byte byte0 = (byte)Math.min(100, this.targetsStanding.size());

        for (int int1 = 0; int1 < byte0; int1++) {
            HitInfo hitInfo0 = this.targetsStanding.get(int1);
            int0 += hitInfo0.getPacketSizeBytes();
        }

        byte0 = (byte)Math.min(100, this.targetsProne.size());

        for (int int2 = 0; int2 < byte0; int2++) {
            HitInfo hitInfo1 = this.targetsProne.get(int2);
            int0 += hitInfo1.getPacketSizeBytes();
        }

        return int0;
    }

    @Override
    public String getDescription() {
        String string0 = "";
        byte byte0 = (byte)Math.min(100, this.targetsStanding.size());

        for (int int0 = 0; int0 < byte0; int0++) {
            HitInfo hitInfo0 = this.targetsStanding.get(int0);
            string0 = string0 + hitInfo0.getDescription();
        }

        String string1 = "";
        byte0 = (byte)Math.min(100, this.targetsProne.size());

        for (int int1 = 0; int1 < byte0; int1++) {
            HitInfo hitInfo1 = this.targetsProne.get(int1);
            string1 = string1 + hitInfo1.getDescription();
        }

        return "\n\tHitInfo [ isBareHeadsWeapon="
            + this.isBareHeadsWeapon
            + " bAimAtFloor="
            + this.bAimAtFloor
            + " bCloseKill="
            + this.bCloseKill
            + " bDoShove="
            + this.bDoShove
            + " useChargeDelta="
            + this.useChargeDelta
            + " recoilDelay="
            + this.recoilDelay
            + "\n\t  targetOnGround:"
            + this.targetOnGround.getDescription()
            + "\n\t  targetsStanding=["
            + string0
            + "](size="
            + this.targetsStanding.size()
            + ")\n\t  targetsProne=["
            + string1
            + "](size="
            + this.targetsProne.size()
            + ")]";
    }

    public void copy(AttackVars attackVars0) {
        this.isBareHeadsWeapon = attackVars0.isBareHeadsWeapon;
        this.targetOnGround = attackVars0.targetOnGround;
        this.bAimAtFloor = attackVars0.bAimAtFloor;
        this.bCloseKill = attackVars0.bCloseKill;
        this.bDoShove = attackVars0.bDoShove;
        this.useChargeDelta = attackVars0.useChargeDelta;
        this.recoilDelay = attackVars0.recoilDelay;
        this.targetsStanding.clear();

        for (HitInfo hitInfo0 : attackVars0.targetsStanding) {
            this.targetsStanding.add(hitInfo0);
        }

        this.targetsProne.clear();

        for (HitInfo hitInfo1 : attackVars0.targetsProne) {
            this.targetsProne.add(hitInfo1);
        }
    }

    public void clear() {
        this.targetOnGround.setMovingObject(null);
        this.targetsStanding.clear();
        this.targetsProne.clear();
    }
}
