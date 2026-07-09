// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.Lua.LuaEventManager;
import zombie.ai.states.SwipeStatePlayer;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoLivingCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.network.packets.INetworkPacket;

public class WeaponHit extends Hit implements INetworkPacket {
    protected float range;
    protected boolean hitHead;

    public void set(boolean boolean0, float float0, float float4, float float1, float float2, float float3, boolean boolean1) {
        super.set(boolean0, float0, float1, float2, float3);
        this.range = float4;
        this.hitHead = boolean1;
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        super.parse(byteBuffer, udpConnection);
        this.range = byteBuffer.getFloat();
        this.hitHead = byteBuffer.get() != 0;
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        byteBufferWriter.putFloat(this.range);
        byteBufferWriter.putBoolean(this.hitHead);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\n\tWeapon [ range=" + this.range + " | hitHead=" + this.hitHead + " ]";
    }

    void process(IsoGameCharacter character1, IsoGameCharacter character0, HandWeapon weapon) {
        character0.Hit(weapon, character1, this.damage, this.ignore, this.range, true);
        super.process(character1, character0);
        LuaEventManager.triggerEvent("OnWeaponHitXp", character1, weapon, character0, this.damage);
        if (character1.isAimAtFloor() && !weapon.isRanged() && character1.isNPC()) {
            SwipeStatePlayer.splash(character0, weapon, character1);
        }

        if (this.hitHead) {
            SwipeStatePlayer.splash(character0, weapon, character1);
            SwipeStatePlayer.splash(character0, weapon, character1);
            character0.addBlood(BloodBodyPartType.Head, true, true, true);
            character0.addBlood(BloodBodyPartType.Torso_Upper, true, false, false);
            character0.addBlood(BloodBodyPartType.UpperArm_L, true, false, false);
            character0.addBlood(BloodBodyPartType.UpperArm_R, true, false, false);
        }

        if ((!((IsoLivingCharacter)character1).bDoShove || character1.isAimAtFloor())
            && character1.DistToSquared(character0) < 2.0F
            && Math.abs(character1.z - character0.z) < 0.5F) {
            character1.addBlood(null, false, false, false);
        }

        if (!character0.isDead() && !(character0 instanceof IsoPlayer) && (!((IsoLivingCharacter)character1).bDoShove || character1.isAimAtFloor())) {
            SwipeStatePlayer.splash(character0, weapon, character1);
        }
    }
}
