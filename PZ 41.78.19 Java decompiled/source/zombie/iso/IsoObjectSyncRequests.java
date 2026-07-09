// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.SystemDisabler;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameClient;
import zombie.network.PacketTypes;

public final class IsoObjectSyncRequests {
    public final ArrayList<IsoObjectSyncRequests.SyncData> requests = new ArrayList<>();
    public long timeout = 1000L;

    public void putRequest(IsoGridSquare square, IsoObject object) {
        if (GameClient.bClient) {
            this.putRequest(square.x, square.y, square.z, (byte)square.getObjects().indexOf(object));
        }
    }

    public void putRequestLoad(IsoGridSquare square) {
        if (GameClient.bClient) {
            this.putRequest(square.x, square.y, square.z, (byte)square.getObjects().size());
        }
    }

    public void putRequest(int int0, int int1, int int2, byte byte0) {
        if (SystemDisabler.doObjectStateSyncEnable) {
            IsoObjectSyncRequests.SyncData syncData = new IsoObjectSyncRequests.SyncData();
            syncData.x = int0;
            syncData.y = int1;
            syncData.z = int2;
            syncData.objIndex = byte0;
            syncData.reqTime = 0L;
            syncData.reqCount = 0;
            synchronized (this.requests) {
                this.requests.add(syncData);
            }
        }
    }

    public void sendRequests(UdpConnection udpConnection) {
        if (SystemDisabler.doObjectStateSyncEnable) {
            if (this.requests.size() != 0) {
                ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
                PacketTypes.PacketType.SyncIsoObjectReq.doPacket(byteBufferWriter);
                ByteBuffer byteBuffer = byteBufferWriter.bb;
                int int0 = byteBuffer.position();
                byteBufferWriter.putShort((short)0);
                int int1 = 0;
                synchronized (this.requests) {
                    for (int int2 = 0; int2 < this.requests.size(); int2++) {
                        IsoObjectSyncRequests.SyncData syncData = this.requests.get(int2);
                        if (syncData.reqCount > 4) {
                            this.requests.remove(int2);
                            int2--;
                        } else {
                            if (syncData.reqTime == 0L) {
                                syncData.reqTime = System.currentTimeMillis();
                                int1++;
                                byteBuffer.putInt(syncData.x);
                                byteBuffer.putInt(syncData.y);
                                byteBuffer.putInt(syncData.z);
                                byteBuffer.put(syncData.objIndex);
                                syncData.reqCount++;
                            }

                            if (System.currentTimeMillis() - syncData.reqTime >= this.timeout) {
                                syncData.reqTime = System.currentTimeMillis();
                                int1++;
                                byteBuffer.putInt(syncData.x);
                                byteBuffer.putInt(syncData.y);
                                byteBuffer.putInt(syncData.z);
                                byteBuffer.put(syncData.objIndex);
                                syncData.reqCount++;
                            }

                            if (int1 >= 50) {
                                break;
                            }
                        }
                    }
                }

                if (int1 == 0) {
                    GameClient.connection.cancelPacket();
                } else {
                    int int3 = byteBuffer.position();
                    byteBuffer.position(int0);
                    byteBuffer.putShort((short)int1);
                    byteBuffer.position(int3);
                    PacketTypes.PacketType.SyncIsoObjectReq.send(GameClient.connection);
                }
            }
        }
    }

    public void receiveIsoSync(int int3, int int2, int int1, byte byte0) {
        synchronized (this.requests) {
            for (int int0 = 0; int0 < this.requests.size(); int0++) {
                IsoObjectSyncRequests.SyncData syncData = this.requests.get(int0);
                if (syncData.x == int3 && syncData.y == int2 && syncData.z == int1 && syncData.objIndex == byte0) {
                    this.requests.remove(int0);
                }
            }
        }
    }

    private class SyncData {
        int x;
        int y;
        int z;
        byte objIndex;
        long reqTime;
        int reqCount;
    }
}
