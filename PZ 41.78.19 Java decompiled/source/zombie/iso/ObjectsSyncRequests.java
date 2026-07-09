// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.SystemDisabler;
import zombie.Lua.LuaEventManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.UdpEngine;
import zombie.debug.DebugLog;
import zombie.inventory.ItemContainer;
import zombie.iso.objects.IsoLightSwitch;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.network.ServerMap;
import zombie.network.WorldItemTypes;

public final class ObjectsSyncRequests {
    public static final short ClientSendChunkHashes = 1;
    public static final short ServerSendGridSquareHashes = 2;
    public static final short ClientSendGridSquareRequest = 3;
    public static final short ServerSendGridSquareObjectsHashes = 4;
    public static final short ClientSendObjectRequests = 5;
    public static final short ServerSendObject = 6;
    public ArrayList<ObjectsSyncRequests.SyncIsoChunk> requestsSyncIsoChunk;
    public ArrayList<ObjectsSyncRequests.SyncIsoGridSquare> requestsSyncIsoGridSquare;
    public ArrayList<ObjectsSyncRequests.SyncIsoObject> requestsSyncIsoObject;
    public long timeout = 1000L;

    public ObjectsSyncRequests(boolean boolean0) {
        if (boolean0) {
            this.requestsSyncIsoChunk = new ArrayList<>();
            this.requestsSyncIsoGridSquare = new ArrayList<>();
            this.requestsSyncIsoObject = new ArrayList<>();
        } else {
            this.requestsSyncIsoGridSquare = new ArrayList<>();
        }
    }

    static int getObjectInsertIndex(long[] longs1, long[] longs0, long long0) {
        if (long0 == longs0[0]) {
            return 0;
        } else {
            for (int int0 = 0; int0 < longs1.length; int0++) {
                if (longs1[int0] == long0) {
                    return -1;
                }
            }

            int int1 = 0;

            for (int int2 = 0; int2 < longs0.length; int2++) {
                if (int1 < longs1.length && longs0[int2] == longs1[int1]) {
                    int1++;
                }

                if (longs0[int2] == long0) {
                    return int1;
                }
            }

            return -1;
        }
    }

    public void putRequestSyncIsoChunk(IsoChunk chunk) {
        if (!GameClient.bClient || SystemDisabler.doWorldSyncEnable) {
            ObjectsSyncRequests.SyncIsoChunk syncIsoChunk = new ObjectsSyncRequests.SyncIsoChunk();
            syncIsoChunk.x = chunk.wx;
            syncIsoChunk.y = chunk.wy;
            syncIsoChunk.hashCodeObjects = chunk.getHashCodeObjects();
            syncIsoChunk.reqTime = 0L;
            syncIsoChunk.reqCount = 0;
            synchronized (this.requestsSyncIsoChunk) {
                this.requestsSyncIsoChunk.add(syncIsoChunk);
            }
        }
    }

    public void putRequestSyncItemContainer(ItemContainer container) {
        if (container != null && container.parent != null && container.parent.square != null) {
            this.putRequestSyncIsoGridSquare(container.parent.square);
        }
    }

    public void putRequestSyncIsoGridSquare(IsoGridSquare square) {
        if (square != null) {
            ObjectsSyncRequests.SyncIsoGridSquare syncIsoGridSquare = new ObjectsSyncRequests.SyncIsoGridSquare();
            syncIsoGridSquare.x = square.x;
            syncIsoGridSquare.y = square.y;
            syncIsoGridSquare.z = square.z;
            syncIsoGridSquare.reqTime = 0L;
            syncIsoGridSquare.reqCount = 0;
            synchronized (this.requestsSyncIsoGridSquare) {
                if (!this.requestsSyncIsoGridSquare.contains(square)) {
                    this.requestsSyncIsoGridSquare.add(syncIsoGridSquare);
                } else {
                    DebugLog.log("Warning: [putRequestSyncIsoGridSquare] Tryed to add dublicate object.");
                }
            }
        }
    }

    public void sendRequests(UdpConnection udpConnection) {
        if (SystemDisabler.doWorldSyncEnable) {
            if (this.requestsSyncIsoChunk != null && this.requestsSyncIsoChunk.size() != 0) {
                ByteBufferWriter byteBufferWriter0 = udpConnection.startPacket();
                PacketTypes.PacketType.SyncObjects.doPacket(byteBufferWriter0);
                byteBufferWriter0.putShort((short)1);
                ByteBuffer byteBuffer0 = byteBufferWriter0.bb;
                int int0 = byteBuffer0.position();
                byteBufferWriter0.putShort((short)0);
                int int1 = 0;
                synchronized (this.requestsSyncIsoChunk) {
                    for (int int2 = this.requestsSyncIsoChunk.size() - 1; int2 >= 0; int2--) {
                        ObjectsSyncRequests.SyncIsoChunk syncIsoChunk = this.requestsSyncIsoChunk.get(int2);
                        if (syncIsoChunk.reqCount > 3) {
                            this.requestsSyncIsoChunk.remove(int2);
                        } else {
                            if (syncIsoChunk.reqTime == 0L) {
                                syncIsoChunk.reqTime = System.currentTimeMillis();
                                int1++;
                                byteBuffer0.putInt(syncIsoChunk.x);
                                byteBuffer0.putInt(syncIsoChunk.y);
                                byteBuffer0.putLong(syncIsoChunk.hashCodeObjects);
                                syncIsoChunk.reqCount++;
                            }

                            if (System.currentTimeMillis() - syncIsoChunk.reqTime >= this.timeout) {
                                syncIsoChunk.reqTime = System.currentTimeMillis();
                                int1++;
                                byteBuffer0.putInt(syncIsoChunk.x);
                                byteBuffer0.putInt(syncIsoChunk.y);
                                byteBuffer0.putLong(syncIsoChunk.hashCodeObjects);
                                syncIsoChunk.reqCount++;
                            }

                            if (int1 >= 5) {
                                break;
                            }
                        }
                    }
                }

                if (int1 == 0) {
                    GameClient.connection.cancelPacket();
                    return;
                }

                int int3 = byteBuffer0.position();
                byteBuffer0.position(int0);
                byteBuffer0.putShort((short)int1);
                byteBuffer0.position(int3);
                PacketTypes.PacketType.SyncObjects.send(GameClient.connection);
            }

            if (this.requestsSyncIsoGridSquare != null && this.requestsSyncIsoGridSquare.size() != 0) {
                ByteBufferWriter byteBufferWriter1 = udpConnection.startPacket();
                PacketTypes.PacketType.SyncObjects.doPacket(byteBufferWriter1);
                byteBufferWriter1.putShort((short)3);
                ByteBuffer byteBuffer1 = byteBufferWriter1.bb;
                int int4 = byteBuffer1.position();
                byteBufferWriter1.putShort((short)0);
                int int5 = 0;
                synchronized (this.requestsSyncIsoGridSquare) {
                    for (int int6 = 0; int6 < this.requestsSyncIsoGridSquare.size(); int6++) {
                        ObjectsSyncRequests.SyncIsoGridSquare syncIsoGridSquare = this.requestsSyncIsoGridSquare.get(int6);
                        if (syncIsoGridSquare.reqCount > 3) {
                            this.requestsSyncIsoGridSquare.remove(int6);
                            int6--;
                        } else {
                            if (syncIsoGridSquare.reqTime == 0L) {
                                syncIsoGridSquare.reqTime = System.currentTimeMillis();
                                int5++;
                                byteBuffer1.putInt(syncIsoGridSquare.x);
                                byteBuffer1.putInt(syncIsoGridSquare.y);
                                byteBuffer1.put((byte)syncIsoGridSquare.z);
                                syncIsoGridSquare.reqCount++;
                            }

                            if (System.currentTimeMillis() - syncIsoGridSquare.reqTime >= this.timeout) {
                                syncIsoGridSquare.reqTime = System.currentTimeMillis();
                                int5++;
                                byteBuffer1.putInt(syncIsoGridSquare.x);
                                byteBuffer1.putInt(syncIsoGridSquare.y);
                                byteBuffer1.put((byte)syncIsoGridSquare.z);
                                syncIsoGridSquare.reqCount++;
                            }

                            if (int5 >= 100) {
                                break;
                            }
                        }
                    }
                }

                if (int5 == 0) {
                    GameClient.connection.cancelPacket();
                    return;
                }

                int int7 = byteBuffer1.position();
                byteBuffer1.position(int4);
                byteBuffer1.putShort((short)int5);
                byteBuffer1.position(int7);
                PacketTypes.PacketType.SyncObjects.send(GameClient.connection);
            }

            if (this.requestsSyncIsoObject != null && this.requestsSyncIsoObject.size() != 0) {
                ByteBufferWriter byteBufferWriter2 = udpConnection.startPacket();
                PacketTypes.PacketType.SyncObjects.doPacket(byteBufferWriter2);
                byteBufferWriter2.putShort((short)5);
                ByteBuffer byteBuffer2 = byteBufferWriter2.bb;
                int int8 = byteBuffer2.position();
                byteBufferWriter2.putShort((short)0);
                int int9 = 0;
                synchronized (this.requestsSyncIsoObject) {
                    for (int int10 = 0; int10 < this.requestsSyncIsoObject.size(); int10++) {
                        ObjectsSyncRequests.SyncIsoObject syncIsoObject = this.requestsSyncIsoObject.get(int10);
                        if (syncIsoObject.reqCount > 3) {
                            this.requestsSyncIsoObject.remove(int10);
                            int10--;
                        } else {
                            if (syncIsoObject.reqTime == 0L) {
                                syncIsoObject.reqTime = System.currentTimeMillis();
                                int9++;
                                byteBuffer2.putInt(syncIsoObject.x);
                                byteBuffer2.putInt(syncIsoObject.y);
                                byteBuffer2.put((byte)syncIsoObject.z);
                                byteBuffer2.putLong(syncIsoObject.hash);
                                syncIsoObject.reqCount++;
                            }

                            if (System.currentTimeMillis() - syncIsoObject.reqTime >= this.timeout) {
                                syncIsoObject.reqTime = System.currentTimeMillis();
                                int9++;
                                byteBuffer2.putInt(syncIsoObject.x);
                                byteBuffer2.putInt(syncIsoObject.y);
                                byteBuffer2.put((byte)syncIsoObject.z);
                                byteBuffer2.putLong(syncIsoObject.hash);
                                syncIsoObject.reqCount++;
                            }

                            if (int9 >= 100) {
                                break;
                            }
                        }
                    }
                }

                if (int9 == 0) {
                    GameClient.connection.cancelPacket();
                    return;
                }

                int int11 = byteBuffer2.position();
                byteBuffer2.position(int8);
                byteBuffer2.putShort((short)int9);
                byteBuffer2.position(int11);
                PacketTypes.PacketType.SyncObjects.send(GameClient.connection);
            }
        }
    }

    public void receiveSyncIsoChunk(int int2, int int1) {
        synchronized (this.requestsSyncIsoChunk) {
            for (int int0 = 0; int0 < this.requestsSyncIsoChunk.size(); int0++) {
                ObjectsSyncRequests.SyncIsoChunk syncIsoChunk = this.requestsSyncIsoChunk.get(int0);
                if (syncIsoChunk.x == int2 && syncIsoChunk.y == int1) {
                    this.requestsSyncIsoChunk.remove(int0);
                    return;
                }
            }
        }
    }

    public void receiveSyncIsoGridSquare(int int3, int int2, int int1) {
        synchronized (this.requestsSyncIsoGridSquare) {
            for (int int0 = 0; int0 < this.requestsSyncIsoGridSquare.size(); int0++) {
                ObjectsSyncRequests.SyncIsoGridSquare syncIsoGridSquare = this.requestsSyncIsoGridSquare.get(int0);
                if (syncIsoGridSquare.x == int3 && syncIsoGridSquare.y == int2 && syncIsoGridSquare.z == int1) {
                    this.requestsSyncIsoGridSquare.remove(int0);
                    return;
                }
            }
        }
    }

    public void receiveSyncIsoObject(int int3, int int2, int int1, long long0) {
        synchronized (this.requestsSyncIsoObject) {
            for (int int0 = 0; int0 < this.requestsSyncIsoObject.size(); int0++) {
                ObjectsSyncRequests.SyncIsoObject syncIsoObject = this.requestsSyncIsoObject.get(int0);
                if (syncIsoObject.x == int3 && syncIsoObject.y == int2 && syncIsoObject.z == int1 && syncIsoObject.hash == long0) {
                    this.requestsSyncIsoObject.remove(int0);
                    return;
                }
            }
        }
    }

    public void receiveGridSquareHashes(ByteBuffer byteBuffer) {
        short short0 = byteBuffer.getShort();

        for (int int0 = 0; int0 < short0; int0++) {
            short short1 = byteBuffer.getShort();
            short short2 = byteBuffer.getShort();
            long long0 = byteBuffer.getLong();
            short short3 = byteBuffer.getShort();

            for (int int1 = 0; int1 < short3; int1++) {
                int int2 = byteBuffer.get() + short1 * 10;
                int int3 = byteBuffer.get() + short2 * 10;
                byte byte0 = byteBuffer.get();
                int int4 = byteBuffer.getInt();
                IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, byte0);
                if (square != null) {
                    int int5 = square.getHashCodeObjectsInt();
                    if (int5 != int4) {
                        ObjectsSyncRequests.SyncIsoGridSquare syncIsoGridSquare = new ObjectsSyncRequests.SyncIsoGridSquare();
                        syncIsoGridSquare.x = int2;
                        syncIsoGridSquare.y = int3;
                        syncIsoGridSquare.z = byte0;
                        syncIsoGridSquare.reqTime = 0L;
                        syncIsoGridSquare.reqCount = 0;
                        synchronized (this.requestsSyncIsoGridSquare) {
                            this.requestsSyncIsoGridSquare.add(syncIsoGridSquare);
                        }
                    }
                }
            }

            this.receiveSyncIsoChunk(short1, short2);
        }
    }

    public void receiveGridSquareObjectHashes(ByteBuffer byteBuffer) {
        short short0 = byteBuffer.getShort();

        for (int int0 = 0; int0 < short0; int0++) {
            int int1 = byteBuffer.getInt();
            int int2 = byteBuffer.getInt();
            byte byte0 = byteBuffer.get();
            this.receiveSyncIsoGridSquare(int1, int2, byte0);
            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, byte0);
            if (square == null) {
                return;
            }

            byte byte1 = byteBuffer.get();
            int int3 = byteBuffer.getInt() - 3;
            long[] longs = new long[byte1];

            for (int int4 = 0; int4 < byte1; int4++) {
                longs[int4] = byteBuffer.getLong();
            }

            try {
                boolean[] booleans0 = new boolean[square.getObjects().size()];
                boolean[] booleans1 = new boolean[byte1];

                for (int int5 = 0; int5 < byte1; int5++) {
                    booleans1[int5] = true;
                }

                for (int int6 = 0; int6 < square.getObjects().size(); int6++) {
                    booleans0[int6] = false;
                    long long0 = square.getObjects().get(int6).customHashCode();
                    boolean boolean0 = false;

                    for (int int7 = 0; int7 < byte1; int7++) {
                        if (longs[int7] == long0) {
                            boolean0 = true;
                            booleans1[int7] = false;
                            break;
                        }
                    }

                    if (!boolean0) {
                        booleans0[int6] = true;
                    }
                }

                for (int int8 = square.getObjects().size() - 1; int8 >= 0; int8--) {
                    if (booleans0[int8]) {
                        square.getObjects().get(int8).removeFromWorld();
                        square.getObjects().get(int8).removeFromSquare();
                    }
                }

                for (int int9 = 0; int9 < byte1; int9++) {
                    if (booleans1[int9]) {
                        ObjectsSyncRequests.SyncIsoObject syncIsoObject = new ObjectsSyncRequests.SyncIsoObject();
                        syncIsoObject.x = int1;
                        syncIsoObject.y = int2;
                        syncIsoObject.z = byte0;
                        syncIsoObject.hash = longs[int9];
                        syncIsoObject.reqTime = 0L;
                        syncIsoObject.reqCount = 0;
                        synchronized (this.requestsSyncIsoObject) {
                            this.requestsSyncIsoObject.add(syncIsoObject);
                        }
                    }
                }
            } catch (Throwable throwable) {
                DebugLog.log("ERROR: receiveGridSquareObjects " + throwable.getMessage());
            }

            square.RecalcAllWithNeighbours(true);
            IsoWorld.instance.CurrentCell.checkHaveRoof(square.getX(), square.getY());
            byteBuffer.position(int3);
        }

        LuaEventManager.triggerEvent("OnContainerUpdate");
    }

    public void receiveObject(ByteBuffer byteBuffer) {
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        byte byte0 = byteBuffer.get();
        long long0 = byteBuffer.getLong();
        this.receiveSyncIsoObject(int0, int1, byte0, long0);
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, byte0);
        if (square != null) {
            byte byte1 = byteBuffer.get();
            long[] longs0 = new long[byte1];

            for (int int2 = 0; int2 < byte1; int2++) {
                longs0[int2] = byteBuffer.getLong();
            }

            long[] longs1 = new long[square.getObjects().size()];

            for (int int3 = 0; int3 < square.getObjects().size(); int3++) {
                longs1[int3] = square.getObjects().get(int3).customHashCode();
            }

            int int4 = square.getObjects().size();
            int int5 = getObjectInsertIndex(longs1, longs0, long0);
            if (int5 == -1) {
                DebugLog.log("ERROR: ObjectsSyncRequest.receiveObject OBJECT EXIST (" + int0 + ", " + int1 + ", " + byte0 + ") hash=" + long0);
            } else {
                IsoObject object = WorldItemTypes.createFromBuffer(byteBuffer);
                if (object != null) {
                    object.loadFromRemoteBuffer(byteBuffer, false);
                    square.getObjects().add(int5, object);
                    if (object instanceof IsoLightSwitch) {
                        ((IsoLightSwitch)object).addLightSourceFromSprite();
                    }

                    object.addToWorld();
                }

                square.RecalcAllWithNeighbours(true);
                IsoWorld.instance.CurrentCell.checkHaveRoof(square.getX(), square.getY());
                LuaEventManager.triggerEvent("OnContainerUpdate");
            }
        }
    }

    public void serverSendRequests(UdpEngine udpEngine) {
        for (int int0 = 0; int0 < udpEngine.connections.size(); int0++) {
            this.serverSendRequests(udpEngine.connections.get(int0));
        }

        synchronized (this.requestsSyncIsoGridSquare) {
            for (int int1 = 0; int1 < this.requestsSyncIsoGridSquare.size(); int1++) {
                this.requestsSyncIsoGridSquare.remove(0);
            }
        }
    }

    public void serverSendRequests(UdpConnection udpConnection) {
        if (this.requestsSyncIsoGridSquare.size() != 0) {
            ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
            PacketTypes.PacketType.SyncObjects.doPacket(byteBufferWriter);
            byteBufferWriter.putShort((short)4);
            int int0 = byteBufferWriter.bb.position();
            byteBufferWriter.putShort((short)0);
            int int1 = 0;

            for (int int2 = 0; int2 < this.requestsSyncIsoGridSquare.size(); int2++) {
                ObjectsSyncRequests.SyncIsoGridSquare syncIsoGridSquare = this.requestsSyncIsoGridSquare.get(int2);
                if (udpConnection.RelevantTo(syncIsoGridSquare.x, syncIsoGridSquare.y, 100.0F)) {
                    IsoGridSquare square = ServerMap.instance.getGridSquare(syncIsoGridSquare.x, syncIsoGridSquare.y, syncIsoGridSquare.z);
                    if (square != null) {
                        int1++;
                        byteBufferWriter.putInt(square.x);
                        byteBufferWriter.putInt(square.y);
                        byteBufferWriter.putByte((byte)square.z);
                        byteBufferWriter.putByte((byte)square.getObjects().size());
                        byteBufferWriter.putInt(0);
                        int int3 = byteBufferWriter.bb.position();

                        for (int int4 = 0; int4 < square.getObjects().size(); int4++) {
                            byteBufferWriter.putLong(square.getObjects().get(int4).customHashCode());
                        }

                        int int5 = byteBufferWriter.bb.position();
                        byteBufferWriter.bb.position(int3 - 4);
                        byteBufferWriter.putInt(int5);
                        byteBufferWriter.bb.position(int5);
                    }
                }
            }

            int int6 = byteBufferWriter.bb.position();
            byteBufferWriter.bb.position(int0);
            byteBufferWriter.putShort((short)int1);
            byteBufferWriter.bb.position(int6);
            PacketTypes.PacketType.SyncObjects.send(GameClient.connection);
        }
    }

    private class SyncIsoChunk {
        int x;
        int y;
        long hashCodeObjects;
        long reqTime;
        int reqCount;
    }

    private class SyncIsoGridSquare {
        int x;
        int y;
        int z;
        long reqTime;
        int reqCount;

        @Override
        public int hashCode() {
            return this.x + this.y + this.z;
        }
    }

    private class SyncIsoObject {
        int x;
        int y;
        int z;
        long hash;
        long reqTime;
        int reqCount;

        @Override
        public int hashCode() {
            return (int)(this.x + this.y + this.z + this.hash);
        }
    }
}
