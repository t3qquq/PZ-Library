// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.popman;

import zombie.characters.IsoPlayer;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoWorld;
import zombie.iso.Vector3;
import zombie.network.GameServer;
import zombie.network.ServerMap;

final class LoadedAreas {
    public static final int MAX_AREAS = 64;
    public int[] areas = new int[256];
    public int count;
    public boolean changed;
    public int[] prevAreas = new int[256];
    public int prevCount;
    private boolean serverCells;

    public LoadedAreas(boolean boolean0) {
        this.serverCells = boolean0;
    }

    public boolean set() {
        this.setPrev();
        this.clear();
        if (GameServer.bServer) {
            if (this.serverCells) {
                for (int int0 = 0; int0 < ServerMap.instance.LoadedCells.size(); int0++) {
                    ServerMap.ServerCell serverCell = ServerMap.instance.LoadedCells.get(int0);
                    this.add(serverCell.WX * 5, serverCell.WY * 5, 5, 5);
                }
            } else {
                for (int int1 = 0; int1 < GameServer.Players.size(); int1++) {
                    IsoPlayer player = GameServer.Players.get(int1);
                    int int2 = (int)player.x / 10;
                    int int3 = (int)player.y / 10;
                    this.add(
                        int2 - player.OnlineChunkGridWidth / 2,
                        int3 - player.OnlineChunkGridWidth / 2,
                        player.OnlineChunkGridWidth,
                        player.OnlineChunkGridWidth
                    );
                }

                for (int int4 = 0; int4 < GameServer.udpEngine.connections.size(); int4++) {
                    UdpConnection udpConnection = GameServer.udpEngine.connections.get(int4);

                    for (int int5 = 0; int5 < 4; int5++) {
                        Vector3 vector = udpConnection.connectArea[int5];
                        if (vector != null) {
                            int int6 = (int)vector.z;
                            this.add((int)vector.x - int6 / 2, (int)vector.y - int6 / 2, int6, int6);
                        }
                    }
                }
            }
        } else {
            for (int int7 = 0; int7 < IsoPlayer.numPlayers; int7++) {
                IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[int7];
                if (!chunkMap.ignore) {
                    this.add(chunkMap.getWorldXMin(), chunkMap.getWorldYMin(), IsoChunkMap.ChunkGridWidth, IsoChunkMap.ChunkGridWidth);
                }
            }
        }

        return this.changed = this.compareWithPrev();
    }

    public void add(int int1, int int2, int int3, int int4) {
        if (this.count < 64) {
            int int0 = this.count * 4;
            this.areas[int0++] = int1;
            this.areas[int0++] = int2;
            this.areas[int0++] = int3;
            this.areas[int0++] = int4;
            this.count++;
        }
    }

    public void clear() {
        this.count = 0;
        this.changed = false;
    }

    public void copy(LoadedAreas loadedAreas0) {
        this.count = loadedAreas0.count;

        for (int int0 = 0; int0 < this.count; int0++) {
            int int1 = int0 * 4;
            this.areas[int1] = loadedAreas0.areas[int1++];
            this.areas[int1] = loadedAreas0.areas[int1++];
            this.areas[int1] = loadedAreas0.areas[int1++];
            this.areas[int1] = loadedAreas0.areas[int1++];
        }
    }

    private void setPrev() {
        this.prevCount = this.count;

        for (int int0 = 0; int0 < this.count; int0++) {
            int int1 = int0 * 4;
            this.prevAreas[int1] = this.areas[int1++];
            this.prevAreas[int1] = this.areas[int1++];
            this.prevAreas[int1] = this.areas[int1++];
            this.prevAreas[int1] = this.areas[int1++];
        }
    }

    private boolean compareWithPrev() {
        if (this.prevCount != this.count) {
            return true;
        } else {
            for (int int0 = 0; int0 < this.count; int0++) {
                int int1 = int0 * 4;
                if (this.prevAreas[int1] != this.areas[int1++]) {
                    return true;
                }

                if (this.prevAreas[int1] != this.areas[int1++]) {
                    return true;
                }

                if (this.prevAreas[int1] != this.areas[int1++]) {
                    return true;
                }

                if (this.prevAreas[int1] != this.areas[int1++]) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean isOnEdge(int int1, int int0) {
        if (int1 % 10 != 0 && (int1 + 1) % 10 != 0 && int0 % 10 != 0 && (int0 + 1) % 10 != 0) {
            return false;
        } else {
            int int2 = 0;

            while (int2 < this.count) {
                int int3 = int2 * 4;
                int int4 = this.areas[int3++] * 10;
                int int5 = this.areas[int3++] * 10;
                int int6 = int4 + this.areas[int3++] * 10;
                int int7 = int5 + this.areas[int3++] * 10;
                boolean boolean0 = int1 >= int4 && int1 < int6;
                boolean boolean1 = int0 >= int5 && int0 < int7;
                if (!boolean0 || int0 != int5 && int0 != int7 - 1) {
                    if (!boolean1 || int1 != int4 && int1 != int6 - 1) {
                        int2++;
                        continue;
                    }

                    return true;
                }

                return true;
            }

            return false;
        }
    }
}
