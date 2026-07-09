// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.network.packets.INetworkPacket;

public abstract class Hit implements INetworkPacket {
    private static final float MAX_DAMAGE = 100.0F;
    protected boolean ignore;
    protected float damage;
    protected float hitForce;
    protected float hitDirectionX;
    protected float hitDirectionY;

    public void set(boolean boolean0, float float0, float float1, float float2, float float3) {
        this.ignore = boolean0;
        this.damage = Math.min(float0, 100.0F);
        this.hitForce = float1;
        this.hitDirectionX = float2;
        this.hitDirectionY = float3;
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection var2) {
        this.ignore = byteBuffer.get() != 0;
        this.damage = byteBuffer.getFloat();
        this.hitForce = byteBuffer.getFloat();
        this.hitDirectionX = byteBuffer.getFloat();
        this.hitDirectionY = byteBuffer.getFloat();
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putBoolean(this.ignore);
        byteBufferWriter.putFloat(this.damage);
        byteBufferWriter.putFloat(this.hitForce);
        byteBufferWriter.putFloat(this.hitDirectionX);
        byteBufferWriter.putFloat(this.hitDirectionY);
    }

    @Override
    public String getDescription() {
        return "\n\tHit [ ignore="
            + this.ignore
            + " | damage="
            + this.damage
            + " | force="
            + this.hitForce
            + " | dir=( "
            + this.hitDirectionX
            + " ; "
            + this.hitDirectionY
            + " ) ]";
    }

    void process(IsoGameCharacter character1, IsoGameCharacter character0) {
        character0.getHitDir().set(this.hitDirectionX, this.hitDirectionY);
        character0.setHitForce(this.hitForce);
        if (GameServer.bServer && character0 instanceof IsoZombie && character1 instanceof IsoPlayer) {
            ((IsoZombie)character0).addAggro(character1, this.damage);
            DebugLog.Damage
                .noise(
                    "AddAggro zombie=%d player=%d ( \"%s\" ) damage=%f",
                    character0.getOnlineID(),
                    character1.getOnlineID(),
                    ((IsoPlayer)character1).getUsername(),
                    this.damage
                );
        }

        character0.setAttackedBy(character1);
    }

    public float getDamage() {
        return this.damage;
    }
}
