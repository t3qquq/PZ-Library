// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;

public class PlayerVariables implements INetworkPacket {
    byte count = 0;
    PlayerVariables.NetworkPlayerVariable[] variables = new PlayerVariables.NetworkPlayerVariable[2];

    public PlayerVariables() {
        for (byte byte0 = 0; byte0 < this.variables.length; byte0++) {
            this.variables[byte0] = new PlayerVariables.NetworkPlayerVariable();
        }
    }

    public void set(IsoPlayer player) {
        String string = player.getActionStateName();
        if (string.equals("idle")) {
            this.variables[0].set(player, PlayerVariables.NetworkPlayerVariableIDs.IdleSpeed);
            this.count = 1;
        } else if (string.equals("maskingleft")
            || string.equals("maskingright")
            || string.equals("movement")
            || string.equals("run")
            || string.equals("sprint")) {
            this.variables[0].set(player, PlayerVariables.NetworkPlayerVariableIDs.WalkInjury);
            this.variables[1].set(player, PlayerVariables.NetworkPlayerVariableIDs.WalkSpeed);
            this.count = 2;
        }
    }

    public void apply(IsoPlayer player) {
        for (byte byte0 = 0; byte0 < this.count; byte0++) {
            player.setVariable(this.variables[byte0].id.name(), this.variables[byte0].value);
        }
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection var2) {
        this.count = byteBuffer.get();

        for (byte byte0 = 0; byte0 < this.count; byte0++) {
            this.variables[byte0].id = PlayerVariables.NetworkPlayerVariableIDs.values()[byteBuffer.get()];
            this.variables[byte0].value = byteBuffer.getFloat();
        }
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putByte(this.count);

        for (byte byte0 = 0; byte0 < this.count; byte0++) {
            byteBufferWriter.putByte((byte)this.variables[byte0].id.ordinal());
            byteBufferWriter.putFloat(this.variables[byte0].value);
        }
    }

    @Override
    public int getPacketSizeBytes() {
        return 1 + this.count * 5;
    }

    @Override
    public String getDescription() {
        String string = "PlayerVariables: ";
        string = string + "count=" + this.count + " | ";

        for (byte byte0 = 0; byte0 < this.count; byte0++) {
            string = string + "id=" + this.variables[byte0].id.name() + ", ";
            string = string + "value=" + this.variables[byte0].value + " | ";
        }

        return string;
    }

    public void copy(PlayerVariables playerVariables0) {
        this.count = playerVariables0.count;

        for (byte byte0 = 0; byte0 < this.count; byte0++) {
            this.variables[byte0].id = playerVariables0.variables[byte0].id;
            this.variables[byte0].value = playerVariables0.variables[byte0].value;
        }
    }

    private class NetworkPlayerVariable {
        PlayerVariables.NetworkPlayerVariableIDs id;
        float value;

        public void set(IsoPlayer player, PlayerVariables.NetworkPlayerVariableIDs networkPlayerVariableIDs) {
            this.id = networkPlayerVariableIDs;
            this.value = player.getVariableFloat(networkPlayerVariableIDs.name(), 0.0F);
        }
    }

    private static enum NetworkPlayerVariableIDs {
        IdleSpeed,
        WalkInjury,
        WalkSpeed,
        DeltaX,
        DeltaY,
        AttackVariationX,
        AttackVariationY,
        targetDist,
        autoShootVarX,
        autoShootVarY,
        recoilVarX,
        recoilVarY,
        ShoveAimX,
        ShoveAimY;
    }
}
