// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.popman;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import zombie.GameTime;
import zombie.WorldSoundManager;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.RakVoice;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugOptions;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;

public final class MPDebugInfo {
    public static final MPDebugInfo instance = new MPDebugInfo();
    private static final ConcurrentHashMap<Long, MPDebugInfo.MPSoundDebugInfo> debugSounds = new ConcurrentHashMap<>();
    private final ArrayList<MPDebugInfo.MPCell> loadedCells = new ArrayList<>();
    private final ObjectPool<MPDebugInfo.MPCell> cellPool = new ObjectPool<>(MPDebugInfo.MPCell::new);
    private final LoadedAreas loadedAreas = new LoadedAreas(false);
    private ArrayList<MPDebugInfo.MPRepopEvent> repopEvents = new ArrayList<>();
    private final ObjectPool<MPDebugInfo.MPRepopEvent> repopEventPool = new ObjectPool<>(MPDebugInfo.MPRepopEvent::new);
    private short repopEpoch = 0;
    private long requestTime = 0L;
    private boolean requestFlag = false;
    private boolean requestPacketReceived = false;
    private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
    private float RESPAWN_EVERY_HOURS = 1.0F;
    private float REPOP_DISPLAY_HOURS = 0.5F;

    private static native boolean n_hasData(boolean var0);

    private static native void n_requestData();

    private static native int n_getLoadedCellsCount();

    private static native int n_getLoadedCellsData(int var0, ByteBuffer var1);

    private static native int n_getLoadedAreasCount();

    private static native int n_getLoadedAreasData(int var0, ByteBuffer var1);

    private static native int n_getRepopEventCount();

    private static native int n_getRepopEventData(int var0, ByteBuffer var1);

    private void requestServerInfo() {
        if (GameClient.bClient) {
            long long0 = System.currentTimeMillis();
            if (this.requestTime + 1000L <= long0) {
                this.requestTime = long0;
                ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
                PacketTypes.PacketType.KeepAlive.doPacket(byteBufferWriter);
                byteBufferWriter.bb.put((byte)1);
                byteBufferWriter.bb.putShort(this.repopEpoch);
                PacketTypes.PacketType.KeepAlive.send(GameClient.connection);
            }
        }
    }

    public void clientPacket(ByteBuffer byteBufferx) {
        if (GameClient.bClient) {
            byte byte0 = byteBufferx.get();
            if (byte0 == 1) {
                this.cellPool.release(this.loadedCells);
                this.loadedCells.clear();
                this.RESPAWN_EVERY_HOURS = byteBufferx.getFloat();
                short short0 = byteBufferx.getShort();

                for (int int0 = 0; int0 < short0; int0++) {
                    MPDebugInfo.MPCell mPCell = this.cellPool.alloc();
                    mPCell.cx = byteBufferx.getShort();
                    mPCell.cy = byteBufferx.getShort();
                    mPCell.currentPopulation = byteBufferx.getShort();
                    mPCell.desiredPopulation = byteBufferx.getShort();
                    mPCell.lastRepopTime = byteBufferx.getFloat();
                    this.loadedCells.add(mPCell);
                }

                this.loadedAreas.clear();
                short short1 = byteBufferx.getShort();

                for (int int1 = 0; int1 < short1; int1++) {
                    short short2 = byteBufferx.getShort();
                    short short3 = byteBufferx.getShort();
                    short short4 = byteBufferx.getShort();
                    short short5 = byteBufferx.getShort();
                    this.loadedAreas.add(short2, short3, short4, short5);
                }
            }

            if (byte0 == 2) {
                this.repopEventPool.release(this.repopEvents);
                this.repopEvents.clear();
                this.repopEpoch = byteBufferx.getShort();
                short short6 = byteBufferx.getShort();

                for (int int2 = 0; int2 < short6; int2++) {
                    MPDebugInfo.MPRepopEvent mPRepopEvent = this.repopEventPool.alloc();
                    mPRepopEvent.wx = byteBufferx.getShort();
                    mPRepopEvent.wy = byteBufferx.getShort();
                    mPRepopEvent.worldAge = byteBufferx.getFloat();
                    this.repopEvents.add(mPRepopEvent);
                }
            }
        }
    }

    public void serverPacket(ByteBuffer byteBufferx, UdpConnection udpConnection) {
        if (GameServer.bServer) {
            if (udpConnection.accessLevel == 32) {
                byte byte0 = byteBufferx.get();
                if (byte0 == 1) {
                    this.requestTime = System.currentTimeMillis();
                    this.requestPacketReceived = true;
                    short short0 = byteBufferx.getShort();
                    ByteBufferWriter byteBufferWriter0 = udpConnection.startPacket();
                    PacketTypes.PacketType.KeepAlive.doPacket(byteBufferWriter0);
                    byteBufferWriter0.bb.put((byte)1);
                    byteBufferWriter0.bb.putFloat(this.RESPAWN_EVERY_HOURS);
                    byteBufferWriter0.bb.putShort((short)this.loadedCells.size());

                    for (int int0 = 0; int0 < this.loadedCells.size(); int0++) {
                        MPDebugInfo.MPCell mPCell = this.loadedCells.get(int0);
                        byteBufferWriter0.bb.putShort(mPCell.cx);
                        byteBufferWriter0.bb.putShort(mPCell.cy);
                        byteBufferWriter0.bb.putShort(mPCell.currentPopulation);
                        byteBufferWriter0.bb.putShort(mPCell.desiredPopulation);
                        byteBufferWriter0.bb.putFloat(mPCell.lastRepopTime);
                    }

                    byteBufferWriter0.bb.putShort((short)this.loadedAreas.count);

                    for (int int1 = 0; int1 < this.loadedAreas.count; int1++) {
                        int int2 = int1 * 4;
                        byteBufferWriter0.bb.putShort((short)this.loadedAreas.areas[int2++]);
                        byteBufferWriter0.bb.putShort((short)this.loadedAreas.areas[int2++]);
                        byteBufferWriter0.bb.putShort((short)this.loadedAreas.areas[int2++]);
                        byteBufferWriter0.bb.putShort((short)this.loadedAreas.areas[int2++]);
                    }

                    if (short0 != this.repopEpoch) {
                        byte0 = 2;
                    }

                    PacketTypes.PacketType.KeepAlive.send(udpConnection);
                }

                if (byte0 != 2) {
                    if (byte0 == 3) {
                        short short1 = byteBufferx.getShort();
                        short short2 = byteBufferx.getShort();
                        ZombiePopulationManager.instance.dbgSpawnTimeToZero(short1, short2);
                    } else if (byte0 == 4) {
                        short short3 = byteBufferx.getShort();
                        short short4 = byteBufferx.getShort();
                        ZombiePopulationManager.instance.dbgClearZombies(short3, short4);
                    } else if (byte0 == 5) {
                        short short5 = byteBufferx.getShort();
                        short short6 = byteBufferx.getShort();
                        ZombiePopulationManager.instance.dbgSpawnNow(short5, short6);
                    }
                } else {
                    ByteBufferWriter byteBufferWriter1 = udpConnection.startPacket();
                    PacketTypes.PacketType.KeepAlive.doPacket(byteBufferWriter1);
                    byteBufferWriter1.bb.put((byte)2);
                    byteBufferWriter1.bb.putShort(this.repopEpoch);
                    byteBufferWriter1.bb.putShort((short)this.repopEvents.size());

                    for (int int3 = 0; int3 < this.repopEvents.size(); int3++) {
                        MPDebugInfo.MPRepopEvent mPRepopEvent = this.repopEvents.get(int3);
                        byteBufferWriter1.bb.putShort((short)mPRepopEvent.wx);
                        byteBufferWriter1.bb.putShort((short)mPRepopEvent.wy);
                        byteBufferWriter1.bb.putFloat(mPRepopEvent.worldAge);
                    }

                    PacketTypes.PacketType.KeepAlive.send(udpConnection);
                }
            }
        }
    }

    public void request() {
        if (GameServer.bServer) {
            this.requestTime = System.currentTimeMillis();
        }
    }

    private void addRepopEvent(int int0, int int1, float float1) {
        float float0 = (float)GameTime.getInstance().getWorldAgeHours();

        while (!this.repopEvents.isEmpty() && this.repopEvents.get(0).worldAge + this.REPOP_DISPLAY_HOURS < float0) {
            this.repopEventPool.release(this.repopEvents.remove(0));
        }

        this.repopEvents.add(this.repopEventPool.alloc().init(int0, int1, float1));
        this.repopEpoch++;
    }

    public void serverUpdate() {
        if (GameServer.bServer) {
            long long0 = System.currentTimeMillis();
            if (this.requestTime + 10000L < long0) {
                this.requestFlag = false;
                this.requestPacketReceived = false;
            } else {
                if (this.requestFlag) {
                    if (n_hasData(false)) {
                        this.requestFlag = false;
                        this.cellPool.release(this.loadedCells);
                        this.loadedCells.clear();
                        this.loadedAreas.clear();
                        int int0 = n_getLoadedCellsCount();
                        int int1 = 0;

                        while (int1 < int0) {
                            this.byteBuffer.clear();
                            int int2 = n_getLoadedCellsData(int1, this.byteBuffer);
                            int1 += int2;

                            for (int int3 = 0; int3 < int2; int3++) {
                                MPDebugInfo.MPCell mPCell = this.cellPool.alloc();
                                mPCell.cx = this.byteBuffer.getShort();
                                mPCell.cy = this.byteBuffer.getShort();
                                mPCell.currentPopulation = this.byteBuffer.getShort();
                                mPCell.desiredPopulation = this.byteBuffer.getShort();
                                mPCell.lastRepopTime = this.byteBuffer.getFloat();
                                this.loadedCells.add(mPCell);
                            }
                        }

                        int0 = n_getLoadedAreasCount();
                        int1 = 0;

                        while (int1 < int0) {
                            this.byteBuffer.clear();
                            int int4 = n_getLoadedAreasData(int1, this.byteBuffer);
                            int1 += int4;

                            for (int int5 = 0; int5 < int4; int5++) {
                                boolean boolean0 = this.byteBuffer.get() == 0;
                                short short0 = this.byteBuffer.getShort();
                                short short1 = this.byteBuffer.getShort();
                                short short2 = this.byteBuffer.getShort();
                                short short3 = this.byteBuffer.getShort();
                                this.loadedAreas.add(short0, short1, short2, short3);
                            }
                        }
                    }
                } else if (this.requestPacketReceived) {
                    n_requestData();
                    this.requestFlag = true;
                    this.requestPacketReceived = false;
                }

                if (n_hasData(true)) {
                    int int6 = n_getRepopEventCount();
                    int int7 = 0;

                    while (int7 < int6) {
                        this.byteBuffer.clear();
                        int int8 = n_getRepopEventData(int7, this.byteBuffer);
                        int7 += int8;

                        for (int int9 = 0; int9 < int8; int9++) {
                            short short4 = this.byteBuffer.getShort();
                            short short5 = this.byteBuffer.getShort();
                            float float0 = this.byteBuffer.getFloat();
                            this.addRepopEvent(short4, short5, float0);
                        }
                    }
                }
            }
        }
    }

    boolean isRespawnEnabled() {
        return IsoWorld.getZombiesDisabled() ? false : !(this.RESPAWN_EVERY_HOURS <= 0.0F);
    }

    public void render(ZombiePopulationRenderer zombiePopulationRenderer, float float4) {
        this.requestServerInfo();
        float float0 = (float)GameTime.getInstance().getWorldAgeHours();
        IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;
        zombiePopulationRenderer.outlineRect(
            metaGrid.minX * 300 * 1.0F,
            metaGrid.minY * 300 * 1.0F,
            (metaGrid.maxX - metaGrid.minX + 1) * 300 * 1.0F,
            (metaGrid.maxY - metaGrid.minY + 1) * 300 * 1.0F,
            1.0F,
            1.0F,
            1.0F,
            0.25F
        );

        for (int int0 = 0; int0 < this.loadedCells.size(); int0++) {
            MPDebugInfo.MPCell mPCell0 = this.loadedCells.get(int0);
            zombiePopulationRenderer.outlineRect(mPCell0.cx * 300, mPCell0.cy * 300, 300.0F, 300.0F, 1.0F, 1.0F, 1.0F, 0.25F);
            if (this.isRespawnEnabled()) {
                float float1 = Math.min(float0 - mPCell0.lastRepopTime, this.RESPAWN_EVERY_HOURS) / this.RESPAWN_EVERY_HOURS;
                if (mPCell0.lastRepopTime > float0) {
                    float1 = 0.0F;
                }

                zombiePopulationRenderer.outlineRect(mPCell0.cx * 300 + 1, mPCell0.cy * 300 + 1, 298.0F, 298.0F, 0.0F, 1.0F, 0.0F, float1 * float1);
            }
        }

        for (int int1 = 0; int1 < this.loadedAreas.count; int1++) {
            int int2 = int1 * 4;
            int int3 = this.loadedAreas.areas[int2++];
            int int4 = this.loadedAreas.areas[int2++];
            int int5 = this.loadedAreas.areas[int2++];
            int int6 = this.loadedAreas.areas[int2++];
            zombiePopulationRenderer.outlineRect(int3 * 10, int4 * 10, int5 * 10, int6 * 10, 0.7F, 0.7F, 0.7F, 1.0F);
        }

        for (int int7 = 0; int7 < this.repopEvents.size(); int7++) {
            MPDebugInfo.MPRepopEvent mPRepopEvent = this.repopEvents.get(int7);
            if (!(mPRepopEvent.worldAge + this.REPOP_DISPLAY_HOURS < float0)) {
                float float2 = 1.0F - (float0 - mPRepopEvent.worldAge) / this.REPOP_DISPLAY_HOURS;
                float2 = Math.max(float2, 0.1F);
                zombiePopulationRenderer.outlineRect(mPRepopEvent.wx * 10, mPRepopEvent.wy * 10, 50.0F, 50.0F, 0.0F, 0.0F, 1.0F, float2);
            }
        }

        if (GameClient.bClient && DebugOptions.instance.MultiplayerShowPosition.getValue()) {
            float float3 = (IsoChunkMap.ChunkGridWidth / 2 + 2) * 10;

            for (Entry entry0 : GameClient.positions.entrySet()) {
                IsoPlayer player0 = GameClient.IDToPlayerMap.get(entry0.getKey());
                Color color0 = Color.white;
                if (player0 != null) {
                    color0 = player0.getSpeakColour();
                }

                Vector2 vector = (Vector2)entry0.getValue();
                zombiePopulationRenderer.renderZombie(vector.x, vector.y, color0.r, color0.g, color0.b);
                zombiePopulationRenderer.renderCircle(vector.x, vector.y, float3, color0.r, color0.g, color0.b, color0.a);
                zombiePopulationRenderer.renderString(
                    vector.x, vector.y, player0 == null ? String.valueOf(entry0.getKey()) : player0.getUsername(), color0.r, color0.g, color0.b, color0.a
                );
            }

            if (IsoPlayer.getInstance() != null) {
                IsoPlayer player1 = IsoPlayer.getInstance();
                Color color1 = player1.getSpeakColour();
                zombiePopulationRenderer.renderZombie(player1.x, player1.y, color1.r, color1.g, color1.b);
                zombiePopulationRenderer.renderCircle(player1.x, player1.y, float3, color1.r, color1.g, color1.b, color1.a);
                zombiePopulationRenderer.renderString(player1.x, player1.y, player1.getUsername(), color1.r, color1.g, color1.b, color1.a);
                color1 = Colors.LightBlue;
                zombiePopulationRenderer.renderCircle(player1.x, player1.y, RakVoice.GetMinDistance(), color1.r, color1.g, color1.b, color1.a);
                zombiePopulationRenderer.renderCircle(player1.x, player1.y, RakVoice.GetMaxDistance(), color1.r, color1.g, color1.b, color1.a);
            }
        }

        if (float4 > 0.25F) {
            for (int int8 = 0; int8 < this.loadedCells.size(); int8++) {
                MPDebugInfo.MPCell mPCell1 = this.loadedCells.get(int8);
                zombiePopulationRenderer.renderCellInfo(
                    mPCell1.cx, mPCell1.cy, mPCell1.currentPopulation, mPCell1.desiredPopulation, mPCell1.lastRepopTime + this.RESPAWN_EVERY_HOURS - float0
                );
            }
        }

        try {
            debugSounds.entrySet().removeIf(entry -> System.currentTimeMillis() > entry.getKey() + 1000L);

            for (Entry entry1 : debugSounds.entrySet()) {
                Color color2 = Colors.LightBlue;
                if (((MPDebugInfo.MPSoundDebugInfo)entry1.getValue()).sourceIsZombie) {
                    color2 = Colors.GreenYellow;
                } else if (((MPDebugInfo.MPSoundDebugInfo)entry1.getValue()).bRepeating) {
                    color2 = Colors.Coral;
                }

                float float5 = 1.0F - Math.max(0.0F, Math.min(1.0F, (float)(System.currentTimeMillis() - (Long)entry1.getKey()) / 1000.0F));
                zombiePopulationRenderer.renderCircle(
                    ((MPDebugInfo.MPSoundDebugInfo)entry1.getValue()).x,
                    ((MPDebugInfo.MPSoundDebugInfo)entry1.getValue()).y,
                    ((MPDebugInfo.MPSoundDebugInfo)entry1.getValue()).radius,
                    color2.r,
                    color2.g,
                    color2.b,
                    float5
                );
            }
        } catch (Exception exception) {
        }
    }

    public static void AddDebugSound(WorldSoundManager.WorldSound worldSound) {
        try {
            debugSounds.put(System.currentTimeMillis(), new MPDebugInfo.MPSoundDebugInfo(worldSound));
        } catch (Exception exception) {
        }
    }

    private static final class MPCell {
        public short cx;
        public short cy;
        public short currentPopulation;
        public short desiredPopulation;
        public float lastRepopTime;

        MPDebugInfo.MPCell init(int int0, int int1, int int2, int int3, float float0) {
            this.cx = (short)int0;
            this.cy = (short)int1;
            this.currentPopulation = (short)int2;
            this.desiredPopulation = (short)int3;
            this.lastRepopTime = float0;
            return this;
        }
    }

    private static final class MPRepopEvent {
        public int wx;
        public int wy;
        public float worldAge;

        public MPDebugInfo.MPRepopEvent init(int int0, int int1, float float0) {
            this.wx = int0;
            this.wy = int1;
            this.worldAge = float0;
            return this;
        }
    }

    private static class MPSoundDebugInfo {
        int x;
        int y;
        int radius;
        boolean bRepeating;
        boolean sourceIsZombie;

        MPSoundDebugInfo(WorldSoundManager.WorldSound worldSound) {
            this.x = worldSound.x;
            this.y = worldSound.y;
            this.radius = worldSound.radius;
            this.bRepeating = worldSound.bRepeating;
            this.sourceIsZombie = worldSound.sourceIsZombie;
        }
    }
}
