// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.popman;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import zombie.VirtualZombieManager;
import zombie.characters.IsoZombie;
import zombie.characters.NetworkZombieVariables;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.network.MPStatistics;
import zombie.network.PacketTypes;
import zombie.network.ServerMap;
import zombie.network.packets.ZombiePacket;

public class NetworkZombiePacker {
    private static final NetworkZombiePacker instance = new NetworkZombiePacker();
    private final ArrayList<NetworkZombiePacker.DeletedZombie> zombiesDeleted = new ArrayList<>();
    private final ArrayList<NetworkZombiePacker.DeletedZombie> zombiesDeletedForSending = new ArrayList<>();
    private final HashSet<IsoZombie> zombiesReceived = new HashSet<>();
    private final ArrayList<IsoZombie> zombiesProcessing = new ArrayList<>();
    private final NetworkZombieList zombiesRequest = new NetworkZombieList();
    private final ZombiePacket packet = new ZombiePacket();
    private HashSet<UdpConnection> extraUpdate = new HashSet<>();
    private final ByteBuffer bb = ByteBuffer.allocate(1000000);
    UpdateLimit ZombieSimulationReliableLimit = new UpdateLimit(5000L);

    public static NetworkZombiePacker getInstance() {
        return instance;
    }

    public void setExtraUpdate() {
        for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
            UdpConnection udpConnection = GameServer.udpEngine.connections.get(int0);
            if (udpConnection.isFullyConnected()) {
                this.extraUpdate.add(udpConnection);
            }
        }
    }

    public void deleteZombie(IsoZombie zombie0) {
        synchronized (this.zombiesDeleted) {
            this.zombiesDeleted.add(new NetworkZombiePacker.DeletedZombie(zombie0.OnlineID, zombie0.x, zombie0.y));
        }
    }

    public void receivePacket(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        short short0 = byteBuffer.getShort();

        for (int int0 = 0; int0 < short0; int0++) {
            short short1 = byteBuffer.getShort();
            IsoZombie zombie0 = ServerMap.instance.ZombieMap.get(short1);
            if (zombie0 != null && (udpConnection.accessLevel == 32 || zombie0.authOwner == udpConnection)) {
                this.deleteZombie(zombie0);
                DebugLog.Multiplayer.noise("Zombie was deleted id=%d (%f, %f)", zombie0.OnlineID, zombie0.x, zombie0.y);
                VirtualZombieManager.instance.removeZombieFromWorld(zombie0);
                MPStatistics.serverZombieCulled();
            }
        }

        short short2 = byteBuffer.getShort();

        for (int int1 = 0; int1 < short2; int1++) {
            short short3 = byteBuffer.getShort();
            IsoZombie zombie1 = ServerMap.instance.ZombieMap.get((short)short3);
            if (zombie1 != null) {
                this.zombiesRequest.getNetworkZombie(udpConnection).zombies.add(zombie1);
            }
        }

        short short4 = byteBuffer.getShort();

        for (int int2 = 0; int2 < short4; int2++) {
            this.parseZombie(byteBuffer, udpConnection);
        }
    }

    public void parseZombie(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        this.packet.parse(byteBuffer, udpConnection);
        if (this.packet.id == -1) {
            DebugLog.General.error("NetworkZombiePacker.parseZombie id=" + this.packet.id);
        } else {
            try {
                IsoZombie zombie0 = ServerMap.instance.ZombieMap.get(this.packet.id);
                if (zombie0 == null) {
                    return;
                }

                if (zombie0.authOwner != udpConnection) {
                    NetworkZombieManager.getInstance().recheck(udpConnection);
                    this.extraUpdate.add(udpConnection);
                    return;
                }

                this.applyZombie(zombie0);
                zombie0.lastRemoteUpdate = 0;
                if (!IsoWorld.instance.CurrentCell.getZombieList().contains(zombie0)) {
                    IsoWorld.instance.CurrentCell.getZombieList().add(zombie0);
                }

                if (!IsoWorld.instance.CurrentCell.getObjectList().contains(zombie0)) {
                    IsoWorld.instance.CurrentCell.getObjectList().add(zombie0);
                }

                zombie0.zombiePacket.copy(this.packet);
                zombie0.zombiePacketUpdated = true;
                synchronized (this.zombiesReceived) {
                    this.zombiesReceived.add(zombie0);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void postupdate() {
        this.updateAuth();
        synchronized (this.zombiesReceived) {
            this.zombiesProcessing.clear();
            this.zombiesProcessing.addAll(this.zombiesReceived);
            this.zombiesReceived.clear();
        }

        synchronized (this.zombiesDeleted) {
            this.zombiesDeletedForSending.clear();
            this.zombiesDeletedForSending.addAll(this.zombiesDeleted);
            this.zombiesDeleted.clear();
        }

        for (UdpConnection udpConnection : GameServer.udpEngine.connections) {
            if (udpConnection != null && udpConnection.isFullyConnected()) {
                this.send(udpConnection);
            }
        }
    }

    private void updateAuth() {
        ArrayList arrayList = IsoWorld.instance.CurrentCell.getZombieList();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            IsoZombie zombie0 = (IsoZombie)arrayList.get(int0);
            NetworkZombieManager.getInstance().updateAuth(zombie0);
        }
    }

    public int getZombieData(UdpConnection udpConnection, ByteBuffer byteBuffer) {
        int int0 = byteBuffer.position();
        byteBuffer.putShort((short)300);
        int int1 = 0;

        try {
            NetworkZombieList.NetworkZombie networkZombie = this.zombiesRequest.getNetworkZombie(udpConnection);

            while (!networkZombie.zombies.isEmpty()) {
                IsoZombie zombie0 = networkZombie.zombies.poll();
                zombie0.zombiePacket.set(zombie0);
                if (zombie0.OnlineID != -1) {
                    zombie0.zombiePacket.write(byteBuffer);
                    zombie0.zombiePacketUpdated = false;
                    if (++int1 >= 300) {
                        break;
                    }
                }
            }

            for (int int2 = 0; int2 < this.zombiesProcessing.size(); int2++) {
                IsoZombie zombie1 = this.zombiesProcessing.get(int2);
                if (zombie1.authOwner != null
                    && zombie1.authOwner != udpConnection
                    && udpConnection.RelevantTo(zombie1.x, zombie1.y, (udpConnection.ReleventRange - 2) * 10)
                    && zombie1.OnlineID != -1) {
                    zombie1.zombiePacket.write(byteBuffer);
                    zombie1.zombiePacketUpdated = false;
                    int1++;
                }
            }

            int int3 = byteBuffer.position();
            byteBuffer.position(int0);
            byteBuffer.putShort((short)int1);
            byteBuffer.position(int3);
        } catch (BufferOverflowException bufferOverflowException) {
            bufferOverflowException.printStackTrace();
        }

        return int1;
    }

    public void send(UdpConnection udpConnection) {
        this.bb.clear();
        this.bb.put((byte)(udpConnection.isNeighborPlayer ? 1 : 0));
        int int0 = this.bb.position();
        short short0 = 0;
        this.bb.putShort((short)0);

        for (NetworkZombiePacker.DeletedZombie deletedZombie : this.zombiesDeletedForSending) {
            if (udpConnection.RelevantTo(deletedZombie.x, deletedZombie.y)) {
                short0++;
                this.bb.putShort(deletedZombie.OnlineID);
            }
        }

        int int1 = this.bb.position();
        this.bb.position(int0);
        this.bb.putShort(short0);
        this.bb.position(int1);
        NetworkZombieManager.getInstance().getZombieAuth(udpConnection, this.bb);
        int int2 = this.getZombieData(udpConnection, this.bb);
        if (int2 > 0 || udpConnection.timerSendZombie.check() || this.extraUpdate.contains(udpConnection)) {
            this.extraUpdate.remove(udpConnection);
            udpConnection.timerSendZombie.reset(3800L);
            ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
            PacketTypes.PacketType packetType;
            if (this.ZombieSimulationReliableLimit.Check()) {
                packetType = PacketTypes.PacketType.ZombieSimulationReliable;
            } else {
                packetType = PacketTypes.PacketType.ZombieSimulation;
            }

            packetType.doPacket(byteBufferWriter);
            byteBufferWriter.bb.put(this.bb.array(), 0, this.bb.position());
            packetType.send(udpConnection);
        }
    }

    private void applyZombie(IsoZombie zombie0) {
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)this.packet.x, (int)this.packet.y, this.packet.z);
        zombie0.lx = zombie0.nx = zombie0.x = this.packet.realX;
        zombie0.ly = zombie0.ny = zombie0.y = this.packet.realY;
        zombie0.lz = zombie0.z = this.packet.realZ;
        zombie0.setForwardDirection(zombie0.dir.ToVector());
        zombie0.setCurrent(square);
        zombie0.networkAI.targetX = this.packet.x;
        zombie0.networkAI.targetY = this.packet.y;
        zombie0.networkAI.targetZ = this.packet.z;
        zombie0.networkAI.predictionType = this.packet.moveType;
        NetworkZombieVariables.setInt(zombie0, (short)0, this.packet.realHealth);
        NetworkZombieVariables.setInt(zombie0, (short)2, this.packet.speedMod);
        NetworkZombieVariables.setInt(zombie0, (short)1, this.packet.target);
        NetworkZombieVariables.setInt(zombie0, (short)3, this.packet.timeSinceSeenFlesh);
        NetworkZombieVariables.setInt(zombie0, (short)4, this.packet.smParamTargetAngle);
        NetworkZombieVariables.setBooleanVariables(zombie0, this.packet.booleanVariables);
        zombie0.setWalkType(this.packet.walkType.toString());
        zombie0.realState = this.packet.realState;
    }

    class DeletedZombie {
        short OnlineID;
        float x;
        float y;

        public DeletedZombie(short short0, float float0, float float1) {
            this.OnlineID = short0;
            this.x = float0;
            this.y = float1;
        }
    }
}
