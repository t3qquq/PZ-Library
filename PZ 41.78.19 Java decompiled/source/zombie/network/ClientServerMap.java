// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.nio.ByteBuffer;
import java.util.Arrays;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.textures.Texture;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;

public final class ClientServerMap {
    private static final int ChunksPerServerCell = 5;
    private static final int SquaresPerServerCell = 50;
    int playerIndex;
    int centerX;
    int centerY;
    int chunkGridWidth;
    int width;
    boolean[] loaded;
    private static boolean[] isLoaded;
    private static Texture trafficCone;

    public ClientServerMap(int _playerIndex, int squareX, int squareY, int _chunkGridWidth) {
        this.playerIndex = _playerIndex;
        this.centerX = squareX;
        this.centerY = squareY;
        this.chunkGridWidth = _chunkGridWidth;
        this.width = (_chunkGridWidth - 1) * 10 / 50;
        if ((_chunkGridWidth - 1) * 10 % 50 != 0) {
            this.width++;
        }

        this.width++;
        this.loaded = new boolean[this.width * this.width];
    }

    public int getMinX() {
        return (this.centerX / 10 - this.chunkGridWidth / 2) / 5;
    }

    public int getMinY() {
        return (this.centerY / 10 - this.chunkGridWidth / 2) / 5;
    }

    public int getMaxX() {
        return this.getMinX() + this.width - 1;
    }

    public int getMaxY() {
        return this.getMinY() + this.width - 1;
    }

    public boolean isValidCell(int x, int y) {
        return x >= 0 && y >= 0 && x < this.width && y < this.width;
    }

    public boolean setLoaded() {
        if (!GameServer.bServer) {
            return false;
        } else {
            int int0 = ServerMap.instance.getMinX();
            int int1 = ServerMap.instance.getMinY();
            int int2 = this.getMinX();
            int int3 = this.getMinY();
            boolean boolean0 = false;

            for (int int4 = 0; int4 < this.width; int4++) {
                for (int int5 = 0; int5 < this.width; int5++) {
                    ServerMap.ServerCell serverCell = ServerMap.instance.getCell(int2 + int5 - int0, int3 + int4 - int1);
                    boolean boolean1 = serverCell == null ? false : serverCell.bLoaded;
                    boolean0 |= this.loaded[int5 + int4 * this.width] != boolean1;
                    this.loaded[int5 + int4 * this.width] = boolean1;
                }
            }

            return boolean0;
        }
    }

    public boolean setPlayerPosition(int squareX, int squareY) {
        if (!GameServer.bServer) {
            return false;
        } else {
            int int0 = this.getMinX();
            int int1 = this.getMinY();
            this.centerX = squareX;
            this.centerY = squareY;
            return this.setLoaded() || int0 != this.getMinX() || int1 != this.getMinY();
        }
    }

    public static boolean isChunkLoaded(int wx, int wy) {
        if (!GameClient.bClient) {
            return false;
        } else if (wx >= 0 && wy >= 0) {
            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                ClientServerMap clientServerMap = GameClient.loadedCells[int0];
                if (clientServerMap != null) {
                    int int1 = wx / 5 - clientServerMap.getMinX();
                    int int2 = wy / 5 - clientServerMap.getMinY();
                    if (clientServerMap.isValidCell(int1, int2) && clientServerMap.loaded[int1 + int2 * clientServerMap.width]) {
                        return true;
                    }
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static void characterIn(UdpConnection connection, int _playerIndex) {
        if (GameServer.bServer) {
            ClientServerMap clientServerMap = connection.loadedCells[_playerIndex];
            if (clientServerMap != null) {
                IsoPlayer player = connection.players[_playerIndex];
                if (player != null) {
                    if (clientServerMap.setPlayerPosition((int)player.x, (int)player.y)) {
                        clientServerMap.sendPacket(connection);
                    }
                }
            }
        }
    }

    public void sendPacket(UdpConnection connection) {
        if (GameServer.bServer) {
            ByteBufferWriter byteBufferWriter = connection.startPacket();
            PacketTypes.PacketType.ServerMap.doPacket(byteBufferWriter);
            byteBufferWriter.putByte((byte)this.playerIndex);
            byteBufferWriter.putInt(this.centerX);
            byteBufferWriter.putInt(this.centerY);

            for (int int0 = 0; int0 < this.width; int0++) {
                for (int int1 = 0; int1 < this.width; int1++) {
                    byteBufferWriter.putBoolean(this.loaded[int1 + int0 * this.width]);
                }
            }

            PacketTypes.PacketType.ServerMap.send(connection);
        }
    }

    public static void receivePacket(ByteBuffer bb) {
        if (GameClient.bClient) {
            byte byte0 = bb.get();
            int int0 = bb.getInt();
            int int1 = bb.getInt();
            ClientServerMap clientServerMap = GameClient.loadedCells[byte0];
            if (clientServerMap == null) {
                clientServerMap = GameClient.loadedCells[byte0] = new ClientServerMap(byte0, int0, int1, IsoChunkMap.ChunkGridWidth);
            }

            clientServerMap.centerX = int0;
            clientServerMap.centerY = int1;

            for (int int2 = 0; int2 < clientServerMap.width; int2++) {
                for (int int3 = 0; int3 < clientServerMap.width; int3++) {
                    clientServerMap.loaded[int3 + int2 * clientServerMap.width] = bb.get() == 1;
                }
            }
        }
    }

    public static void render(int _playerIndex) {
        if (GameClient.bClient) {
            IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.getChunkMap(_playerIndex);
            if (chunkMap != null && !chunkMap.ignore) {
                int int0 = Core.TileScale;
                byte byte0 = 10;
                float float0 = 0.1F;
                float float1 = 0.1F;
                float float2 = 0.1F;
                float float3 = 0.75F;
                float float4 = 0.0F;
                if (trafficCone == null) {
                    trafficCone = Texture.getSharedTexture("street_decoration_01_26");
                }

                Texture texture = trafficCone;
                if (isLoaded == null || isLoaded.length < IsoChunkMap.ChunkGridWidth * IsoChunkMap.ChunkGridWidth) {
                    isLoaded = new boolean[IsoChunkMap.ChunkGridWidth * IsoChunkMap.ChunkGridWidth];
                }

                for (int int1 = 0; int1 < IsoChunkMap.ChunkGridWidth; int1++) {
                    for (int int2 = 0; int2 < IsoChunkMap.ChunkGridWidth; int2++) {
                        IsoChunk chunk0 = chunkMap.getChunk(int2, int1);
                        if (chunk0 != null) {
                            isLoaded[int2 + int1 * IsoChunkMap.ChunkGridWidth] = isChunkLoaded(chunk0.wx, chunk0.wy);
                        }
                    }
                }

                for (int int3 = 0; int3 < IsoChunkMap.ChunkGridWidth; int3++) {
                    for (int int4 = 0; int4 < IsoChunkMap.ChunkGridWidth; int4++) {
                        IsoChunk chunk1 = chunkMap.getChunk(int4, int3);
                        if (chunk1 != null) {
                            boolean boolean0 = isLoaded[int4 + int3 * IsoChunkMap.ChunkGridWidth];
                            if (boolean0 && texture != null) {
                                IsoChunk chunk2 = chunkMap.getChunk(int4, int3 - 1);
                                if (chunk2 != null && !isLoaded[int4 + (int3 - 1) * IsoChunkMap.ChunkGridWidth]) {
                                    for (int int5 = 0; int5 < byte0; int5++) {
                                        float float5 = IsoUtils.XToScreenExact(chunk1.wx * byte0 + int5, chunk1.wy * byte0, float4, 0);
                                        float float6 = IsoUtils.YToScreenExact(chunk1.wx * byte0 + int5, chunk1.wy * byte0, float4, 0);
                                        SpriteRenderer.instance
                                            .render(
                                                texture,
                                                float5 - texture.getWidth() / 2,
                                                float6,
                                                texture.getWidth(),
                                                texture.getHeight(),
                                                1.0F,
                                                1.0F,
                                                1.0F,
                                                1.0F,
                                                null
                                            );
                                    }
                                }

                                IsoChunk chunk3 = chunkMap.getChunk(int4, int3 + 1);
                                if (chunk3 != null && !isLoaded[int4 + (int3 + 1) * IsoChunkMap.ChunkGridWidth]) {
                                    for (int int6 = 0; int6 < byte0; int6++) {
                                        float float7 = IsoUtils.XToScreenExact(chunk1.wx * byte0 + int6, chunk1.wy * byte0 + (byte0 - 1), float4, 0);
                                        float float8 = IsoUtils.YToScreenExact(chunk1.wx * byte0 + int6, chunk1.wy * byte0 + (byte0 - 1), float4, 0);
                                        SpriteRenderer.instance
                                            .render(
                                                texture,
                                                float7 - texture.getWidth() / 2,
                                                float8,
                                                texture.getWidth(),
                                                texture.getHeight(),
                                                1.0F,
                                                1.0F,
                                                1.0F,
                                                1.0F,
                                                null
                                            );
                                    }
                                }

                                IsoChunk chunk4 = chunkMap.getChunk(int4 - 1, int3);
                                if (chunk4 != null && !isLoaded[int4 - 1 + int3 * IsoChunkMap.ChunkGridWidth]) {
                                    for (int int7 = 0; int7 < byte0; int7++) {
                                        float float9 = IsoUtils.XToScreenExact(chunk1.wx * byte0, chunk1.wy * byte0 + int7, float4, 0);
                                        float float10 = IsoUtils.YToScreenExact(chunk1.wx * byte0, chunk1.wy * byte0 + int7, float4, 0);
                                        SpriteRenderer.instance
                                            .render(
                                                texture,
                                                float9 - texture.getWidth() / 2,
                                                float10,
                                                texture.getWidth(),
                                                texture.getHeight(),
                                                1.0F,
                                                1.0F,
                                                1.0F,
                                                1.0F,
                                                null
                                            );
                                    }
                                }

                                IsoChunk chunk5 = chunkMap.getChunk(int4 + 1, int3);
                                if (chunk5 != null && !isLoaded[int4 + 1 + int3 * IsoChunkMap.ChunkGridWidth]) {
                                    for (int int8 = 0; int8 < byte0; int8++) {
                                        float float11 = IsoUtils.XToScreenExact(chunk1.wx * byte0 + (byte0 - 1), chunk1.wy * byte0 + int8, float4, 0);
                                        float float12 = IsoUtils.YToScreenExact(chunk1.wx * byte0 + (byte0 - 1), chunk1.wy * byte0 + int8, float4, 0);
                                        SpriteRenderer.instance
                                            .render(
                                                texture,
                                                float11 - texture.getWidth() / 2,
                                                float12,
                                                texture.getWidth(),
                                                texture.getHeight(),
                                                1.0F,
                                                1.0F,
                                                1.0F,
                                                1.0F,
                                                null
                                            );
                                    }
                                }
                            }

                            if (!boolean0) {
                                float float13 = chunk1.wx * byte0;
                                float float14 = chunk1.wy * byte0;
                                float float15 = IsoUtils.XToScreenExact(float13, float14 + byte0, float4, 0);
                                float float16 = IsoUtils.YToScreenExact(float13, float14 + byte0, float4, 0);
                                SpriteRenderer.instance
                                    .renderPoly(
                                        (int)float15,
                                        (int)float16,
                                        (int)(float15 + byte0 * 64 / 2 * int0),
                                        (int)(float16 - byte0 * 32 / 2 * int0),
                                        (int)(float15 + byte0 * 64 * int0),
                                        (int)float16,
                                        (int)(float15 + byte0 * 64 / 2 * int0),
                                        (int)(float16 + byte0 * 32 / 2 * int0),
                                        float0,
                                        float1,
                                        float2,
                                        float3
                                    );
                            }
                        }
                    }
                }
            }
        }
    }

    public static void Reset() {
        Arrays.fill(GameClient.loadedCells, null);
        trafficCone = null;
    }
}
