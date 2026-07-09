// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import zombie.debug.LineDrawer;
import zombie.iso.SpriteDetails.IsoFlagType;

public class NearestWalls {
    private static final int CPW = 10;
    private static final int CPWx4 = 40;
    private static final int LEVELS = 8;
    private static int CHANGE_COUNT = 0;
    private static int renderX;
    private static int renderY;
    private static int renderZ;

    public static void chunkLoaded(IsoChunk chunk) {
        CHANGE_COUNT++;
        if (CHANGE_COUNT < 0) {
            CHANGE_COUNT = 0;
        }

        chunk.nearestWalls.changeCount = -1;
    }

    private static void calcDistanceOnThisChunkOnly(IsoChunk chunk) {
        byte[] bytes = chunk.nearestWalls.distanceSelf;

        for (int int0 = 0; int0 < 8; int0++) {
            for (int int1 = 0; int1 < 10; int1++) {
                byte byte0 = -1;

                for (int int2 = 0; int2 < 10; int2++) {
                    chunk.nearestWalls.closest[int2 + int1 * 10 + int0 * 10 * 10] = -1;
                    int int3 = int2 * 4 + int1 * 40 + int0 * 10 * 40;
                    bytes[int3 + 0] = byte0 == -1 ? -1 : (byte)(int2 - byte0);
                    bytes[int3 + 1] = -1;
                    IsoGridSquare square0 = chunk.getGridSquare(int2, int1, int0);
                    if (square0 != null
                        && (
                            square0.Is(IsoFlagType.WallW)
                                || square0.Is(IsoFlagType.DoorWallW)
                                || square0.Is(IsoFlagType.WallNW)
                                || square0.Is(IsoFlagType.WindowW)
                        )) {
                        byte0 = (byte)int2;
                        bytes[int3 + 0] = 0;

                        for (int int4 = int2 - 1; int4 >= 0; int4--) {
                            int3 = int4 * 4 + int1 * 40 + int0 * 10 * 40;
                            if (bytes[int3 + 1] != -1) {
                                break;
                            }

                            bytes[int3 + 1] = (byte)(byte0 - int4);
                        }
                    }
                }
            }

            for (int int5 = 0; int5 < 10; int5++) {
                byte byte1 = -1;

                for (int int6 = 0; int6 < 10; int6++) {
                    int int7 = int5 * 4 + int6 * 40 + int0 * 10 * 40;
                    bytes[int7 + 2] = byte1 == -1 ? -1 : (byte)(int6 - byte1);
                    bytes[int7 + 3] = -1;
                    IsoGridSquare square1 = chunk.getGridSquare(int5, int6, int0);
                    if (square1 != null
                        && (
                            square1.Is(IsoFlagType.WallN)
                                || square1.Is(IsoFlagType.DoorWallN)
                                || square1.Is(IsoFlagType.WallNW)
                                || square1.Is(IsoFlagType.WindowN)
                        )) {
                        byte1 = (byte)int6;
                        bytes[int7 + 2] = 0;

                        for (int int8 = int6 - 1; int8 >= 0; int8--) {
                            int7 = int5 * 4 + int8 * 40 + int0 * 10 * 40;
                            if (bytes[int7 + 3] != -1) {
                                break;
                            }

                            bytes[int7 + 3] = (byte)(byte1 - int8);
                        }
                    }
                }
            }
        }
    }

    private static int getIndex(IsoChunk chunk, int int2, int int1, int int0) {
        return (int2 - chunk.wx * 10) * 4 + (int1 - chunk.wy * 10) * 40 + int0 * 10 * 40;
    }

    private static int getNearestWallOnSameChunk(IsoChunk chunk, int int1, int int2, int int3, int int4) {
        NearestWalls.ChunkData chunkData = chunk.nearestWalls;
        if (chunkData.changeCount != CHANGE_COUNT) {
            calcDistanceOnThisChunkOnly(chunk);
            chunkData.changeCount = CHANGE_COUNT;
        }

        int int0 = getIndex(chunk, int1, int2, int3);
        return chunkData.distanceSelf[int0 + int4];
    }

    private static boolean hasWall(IsoChunk chunk, int int0, int int1, int int2, int int3) {
        return getNearestWallOnSameChunk(chunk, int0, int1, int2, int3) == 0;
    }

    private static int getNearestWallWest(IsoChunk chunk0, int int1, int int2, int int3) {
        byte byte0 = 0;
        byte byte1 = -1;
        byte byte2 = 0;
        int int0 = getNearestWallOnSameChunk(chunk0, int1, int2, int3, byte0);
        if (int0 != -1) {
            return int1 - int0;
        } else {
            for (int int4 = 1; int4 <= 3; int4++) {
                IsoChunk chunk1 = IsoWorld.instance.CurrentCell.getChunk(chunk0.wx + int4 * byte1, chunk0.wy + int4 * byte2);
                if (chunk1 == null) {
                    break;
                }

                int int5 = (chunk1.wx + 1) * 10 - 1;
                int0 = getNearestWallOnSameChunk(chunk1, int5, int2, int3, byte0);
                if (int0 != -1) {
                    return int5 - int0;
                }
            }

            return -1;
        }
    }

    private static int getNearestWallEast(IsoChunk chunk0, int int1, int int2, int int3) {
        byte byte0 = 1;
        byte byte1 = 1;
        byte byte2 = 0;
        int int0 = getNearestWallOnSameChunk(chunk0, int1, int2, int3, byte0);
        if (int0 != -1) {
            return int1 + int0;
        } else {
            for (int int4 = 1; int4 <= 3; int4++) {
                IsoChunk chunk1 = IsoWorld.instance.CurrentCell.getChunk(chunk0.wx + int4 * byte1, chunk0.wy + int4 * byte2);
                if (chunk1 == null) {
                    break;
                }

                int int5 = chunk1.wx * 10;
                int0 = hasWall(chunk1, int5, int2, int3, 0) ? 0 : getNearestWallOnSameChunk(chunk1, int5, int2, int3, byte0);
                if (int0 != -1) {
                    return int5 + int0;
                }
            }

            return -1;
        }
    }

    private static int getNearestWallNorth(IsoChunk chunk0, int int1, int int2, int int3) {
        byte byte0 = 2;
        byte byte1 = 0;
        byte byte2 = -1;
        int int0 = getNearestWallOnSameChunk(chunk0, int1, int2, int3, byte0);
        if (int0 != -1) {
            return int2 - int0;
        } else {
            for (int int4 = 1; int4 <= 3; int4++) {
                IsoChunk chunk1 = IsoWorld.instance.CurrentCell.getChunk(chunk0.wx + int4 * byte1, chunk0.wy + int4 * byte2);
                if (chunk1 == null) {
                    break;
                }

                int int5 = (chunk1.wy + 1) * 10 - 1;
                int0 = getNearestWallOnSameChunk(chunk1, int1, int5, int3, byte0);
                if (int0 != -1) {
                    return int5 - int0;
                }
            }

            return -1;
        }
    }

    private static int getNearestWallSouth(IsoChunk chunk0, int int1, int int2, int int3) {
        byte byte0 = 3;
        byte byte1 = 0;
        byte byte2 = 1;
        int int0 = getNearestWallOnSameChunk(chunk0, int1, int2, int3, byte0);
        if (int0 != -1) {
            return int2 + int0;
        } else {
            for (int int4 = 1; int4 <= 3; int4++) {
                IsoChunk chunk1 = IsoWorld.instance.CurrentCell.getChunk(chunk0.wx + int4 * byte1, chunk0.wy + int4 * byte2);
                if (chunk1 == null) {
                    break;
                }

                int int5 = chunk1.wy * 10;
                int0 = hasWall(chunk1, int1, int5, int3, 2) ? 0 : getNearestWallOnSameChunk(chunk1, int1, int5, int3, byte0);
                if (int0 != -1) {
                    return int5 + int0;
                }
            }

            return -1;
        }
    }

    public static void render(int int0, int int1, int int2) {
        IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunkForGridSquare(int0, int1, int2);
        if (chunk != null) {
            if (renderX != int0 || renderY != int1 || renderZ != int2) {
                renderX = int0;
                renderY = int1;
                renderZ = int2;
                System.out.println("ClosestWallDistance=" + ClosestWallDistance(chunk, int0, int1, int2));
            }

            int int3 = getNearestWallWest(chunk, int0, int1, int2);
            if (int3 != -1) {
                DrawIsoLine(int3, int1 + 0.5F, int0 + 0.5F, int1 + 0.5F, int2, 1.0F, 1.0F, 1.0F, 1.0F, 1);
                DrawIsoLine(int3, int1, int3, int1 + 1, int2, 1.0F, 1.0F, 1.0F, 1.0F, 1);
            }

            int3 = getNearestWallEast(chunk, int0, int1, int2);
            if (int3 != -1) {
                DrawIsoLine(int3, int1 + 0.5F, int0 + 0.5F, int1 + 0.5F, int2, 1.0F, 1.0F, 1.0F, 1.0F, 1);
                DrawIsoLine(int3, int1, int3, int1 + 1, int2, 1.0F, 1.0F, 1.0F, 1.0F, 1);
            }

            int int4 = getNearestWallNorth(chunk, int0, int1, int2);
            if (int4 != -1) {
                DrawIsoLine(int0 + 0.5F, int4, int0 + 0.5F, int1 + 0.5F, int2, 1.0F, 1.0F, 1.0F, 1.0F, 1);
                DrawIsoLine(int0, int4, int0 + 1, int4, int2, 1.0F, 1.0F, 1.0F, 1.0F, 1);
            }

            int4 = getNearestWallSouth(chunk, int0, int1, int2);
            if (int4 != -1) {
                DrawIsoLine(int0 + 0.5F, int4, int0 + 0.5F, int1 + 0.5F, int2, 1.0F, 1.0F, 1.0F, 1.0F, 1);
                DrawIsoLine(int0, int4, int0 + 1, int4, int2, 1.0F, 1.0F, 1.0F, 1.0F, 1);
            }
        }
    }

    private static void DrawIsoLine(
        float float1, float float2, float float6, float float7, float float3, float float9, float float10, float float11, float float12, int int0
    ) {
        float float0 = IsoUtils.XToScreenExact(float1, float2, float3, 0);
        float float4 = IsoUtils.YToScreenExact(float1, float2, float3, 0);
        float float5 = IsoUtils.XToScreenExact(float6, float7, float3, 0);
        float float8 = IsoUtils.YToScreenExact(float6, float7, float3, 0);
        LineDrawer.drawLine(float0, float4, float5, float8, float9, float10, float11, float12, int0);
    }

    public static int ClosestWallDistance(IsoGridSquare square) {
        return square != null && square.chunk != null ? ClosestWallDistance(square.chunk, square.x, square.y, square.z) : 127;
    }

    public static int ClosestWallDistance(IsoChunk chunk, int int3, int int2, int int1) {
        if (chunk == null) {
            return 127;
        } else {
            NearestWalls.ChunkData chunkData = chunk.nearestWalls;
            byte[] bytes = chunkData.closest;
            if (chunkData.changeCount != CHANGE_COUNT) {
                calcDistanceOnThisChunkOnly(chunk);
                chunkData.changeCount = CHANGE_COUNT;
            }

            int int0 = int3 - chunk.wx * 10 + (int2 - chunk.wy * 10) * 10 + int1 * 10 * 10;
            byte byte0 = bytes[int0];
            if (byte0 != -1) {
                return byte0;
            } else {
                int int4 = getNearestWallWest(chunk, int3, int2, int1);
                int int5 = getNearestWallEast(chunk, int3, int2, int1);
                int int6 = getNearestWallNorth(chunk, int3, int2, int1);
                int int7 = getNearestWallSouth(chunk, int3, int2, int1);
                if (int4 == -1 && int5 == -1 && int6 == -1 && int7 == -1) {
                    return bytes[int0] = (byte)127;
                } else {
                    int int8 = -1;
                    if (int4 != -1 && int5 != -1) {
                        int8 = int5 - int4;
                    }

                    int int9 = -1;
                    if (int6 != -1 && int7 != -1) {
                        int9 = int7 - int6;
                    }

                    if (int8 != -1 && int9 != -1) {
                        return bytes[int0] = (byte)Math.min(int8, int9);
                    } else if (int8 != -1) {
                        return bytes[int0] = (byte)int8;
                    } else if (int9 != -1) {
                        return bytes[int0] = (byte)int9;
                    } else {
                        IsoGridSquare square = chunk.getGridSquare(int3 - chunk.wx * 10, int2 - chunk.wy * 10, int1);
                        if (square != null && square.isOutside()) {
                            int4 = int4 == -1 ? 127 : int3 - int4;
                            int5 = int5 == -1 ? 127 : int5 - int3 - 1;
                            int6 = int6 == -1 ? 127 : int2 - int6;
                            int7 = int7 == -1 ? 127 : int7 - int2 - 1;
                            return bytes[int0] = (byte)Math.min(int4, Math.min(int5, Math.min(int6, int7)));
                        } else {
                            return bytes[int0] = (byte)127;
                        }
                    }
                }
            }
        }
    }

    public static final class ChunkData {
        int changeCount = -1;
        final byte[] distanceSelf = new byte[3200];
        final byte[] closest = new byte[800];
    }
}
