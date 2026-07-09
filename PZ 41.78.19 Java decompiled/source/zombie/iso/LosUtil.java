// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import zombie.characters.IsoGameCharacter;

public final class LosUtil {
    public static int XSIZE = 200;
    public static int YSIZE = 200;
    public static int ZSIZE = 16;
    public static byte[][][][] cachedresults = new byte[XSIZE][YSIZE][ZSIZE][4];
    public static boolean[] cachecleared = new boolean[4];

    public static void init(int width, int height) {
        XSIZE = Math.min(width, 200);
        YSIZE = Math.min(height, 200);
        cachedresults = new byte[XSIZE][YSIZE][ZSIZE][4];
    }

    public static LosUtil.TestResults lineClear(IsoCell cell, int x0, int y0, int z0, int x1, int y1, int z1, boolean bIgnoreDoors) {
        return lineClear(cell, x0, y0, z0, x1, y1, z1, bIgnoreDoors, 10000);
    }

    public static LosUtil.TestResults lineClear(IsoCell cell, int x0, int y0, int z0, int x1, int y1, int z1, boolean bIgnoreDoors, int RangeTillWindows) {
        if (z1 == z0 - 1) {
            IsoGridSquare square0 = cell.getGridSquare(x1, y1, z1);
            if (square0 != null && square0.HasElevatedFloor()) {
                z1 = z0;
            }
        }

        LosUtil.TestResults testResults0 = LosUtil.TestResults.Clear;
        int int0 = y1 - y0;
        int int1 = x1 - x0;
        int int2 = z1 - z0;
        float float0 = 0.5F;
        float float1 = 0.5F;
        IsoGridSquare square1 = cell.getGridSquare(x0, y0, z0);
        int int3 = 0;
        boolean boolean0 = false;
        if (Math.abs(int1) > Math.abs(int0) && Math.abs(int1) > Math.abs(int2)) {
            float float2 = (float)int0 / int1;
            float float3 = (float)int2 / int1;
            float0 += y0;
            float1 += z0;
            int1 = int1 < 0 ? -1 : 1;
            float2 *= int1;

            for (float float4 = float3 * int1; x0 != x1; boolean0 = false) {
                x0 += int1;
                float0 += float2;
                float1 += float4;
                IsoGridSquare square2 = cell.getGridSquare(x0, (int)float0, (int)float1);
                if (square2 != null && square1 != null) {
                    LosUtil.TestResults testResults1 = square2.testVisionAdjacent(
                        square1.getX() - square2.getX(), square1.getY() - square2.getY(), square1.getZ() - square2.getZ(), true, bIgnoreDoors
                    );
                    if (testResults1 == LosUtil.TestResults.ClearThroughWindow) {
                        boolean0 = true;
                    }

                    if (testResults1 != LosUtil.TestResults.Blocked
                        && testResults0 != LosUtil.TestResults.Clear
                        && (testResults1 != LosUtil.TestResults.ClearThroughWindow || testResults0 != LosUtil.TestResults.ClearThroughOpenDoor)) {
                        if (testResults1 == LosUtil.TestResults.ClearThroughClosedDoor && testResults0 == LosUtil.TestResults.ClearThroughOpenDoor) {
                            testResults0 = testResults1;
                        }
                    } else {
                        testResults0 = testResults1;
                    }

                    if (testResults0 == LosUtil.TestResults.Blocked) {
                        return LosUtil.TestResults.Blocked;
                    }

                    if (boolean0) {
                        if (int3 > RangeTillWindows) {
                            return LosUtil.TestResults.Blocked;
                        }

                        int3 = 0;
                    }
                }

                square1 = square2;
                int int4 = (int)float0;
                int int5 = (int)float1;
                int3++;
            }
        } else if (Math.abs(int0) >= Math.abs(int1) && Math.abs(int0) > Math.abs(int2)) {
            float float5 = (float)int1 / int0;
            float float6 = (float)int2 / int0;
            float0 += x0;
            float1 += z0;
            int0 = int0 < 0 ? -1 : 1;
            float5 *= int0;

            for (float float7 = float6 * int0; y0 != y1; boolean0 = false) {
                y0 += int0;
                float0 += float5;
                float1 += float7;
                IsoGridSquare square3 = cell.getGridSquare((int)float0, y0, (int)float1);
                if (square3 != null && square1 != null) {
                    LosUtil.TestResults testResults2 = square3.testVisionAdjacent(
                        square1.getX() - square3.getX(), square1.getY() - square3.getY(), square1.getZ() - square3.getZ(), true, bIgnoreDoors
                    );
                    if (testResults2 == LosUtil.TestResults.ClearThroughWindow) {
                        boolean0 = true;
                    }

                    if (testResults2 != LosUtil.TestResults.Blocked
                        && testResults0 != LosUtil.TestResults.Clear
                        && (testResults2 != LosUtil.TestResults.ClearThroughWindow || testResults0 != LosUtil.TestResults.ClearThroughOpenDoor)) {
                        if (testResults2 == LosUtil.TestResults.ClearThroughClosedDoor && testResults0 == LosUtil.TestResults.ClearThroughOpenDoor) {
                            testResults0 = testResults2;
                        }
                    } else {
                        testResults0 = testResults2;
                    }

                    if (testResults0 == LosUtil.TestResults.Blocked) {
                        return LosUtil.TestResults.Blocked;
                    }

                    if (boolean0) {
                        if (int3 > RangeTillWindows) {
                            return LosUtil.TestResults.Blocked;
                        }

                        int3 = 0;
                    }
                }

                square1 = square3;
                int int6 = (int)float0;
                int int7 = (int)float1;
                int3++;
            }
        } else {
            float float8 = (float)int1 / int2;
            float float9 = (float)int0 / int2;
            float0 += x0;
            float1 += y0;
            int2 = int2 < 0 ? -1 : 1;
            float8 *= int2;

            for (float float10 = float9 * int2; z0 != z1; boolean0 = false) {
                z0 += int2;
                float0 += float8;
                float1 += float10;
                IsoGridSquare square4 = cell.getGridSquare((int)float0, (int)float1, z0);
                if (square4 != null && square1 != null) {
                    LosUtil.TestResults testResults3 = square4.testVisionAdjacent(
                        square1.getX() - square4.getX(), square1.getY() - square4.getY(), square1.getZ() - square4.getZ(), true, bIgnoreDoors
                    );
                    if (testResults3 == LosUtil.TestResults.ClearThroughWindow) {
                        boolean0 = true;
                    }

                    if (testResults3 != LosUtil.TestResults.Blocked
                        && testResults0 != LosUtil.TestResults.Clear
                        && (testResults3 != LosUtil.TestResults.ClearThroughWindow || testResults0 != LosUtil.TestResults.ClearThroughOpenDoor)) {
                        if (testResults3 == LosUtil.TestResults.ClearThroughClosedDoor && testResults0 == LosUtil.TestResults.ClearThroughOpenDoor) {
                            testResults0 = testResults3;
                        }
                    } else {
                        testResults0 = testResults3;
                    }

                    if (testResults0 == LosUtil.TestResults.Blocked) {
                        return LosUtil.TestResults.Blocked;
                    }

                    if (boolean0) {
                        if (int3 > RangeTillWindows) {
                            return LosUtil.TestResults.Blocked;
                        }

                        int3 = 0;
                    }
                }

                square1 = square4;
                int int8 = (int)float0;
                int int9 = (int)float1;
                int3++;
            }
        }

        return testResults0;
    }

    public static boolean lineClearCollide(int x1, int y1, int z1, int x0, int y0, int z0, boolean bIgnoreDoors) {
        IsoCell cell = IsoWorld.instance.CurrentCell;
        int int0 = y1 - y0;
        int int1 = x1 - x0;
        int int2 = z1 - z0;
        float float0 = 0.5F;
        float float1 = 0.5F;
        IsoGridSquare square0 = cell.getGridSquare(x0, y0, z0);
        if (Math.abs(int1) > Math.abs(int0) && Math.abs(int1) > Math.abs(int2)) {
            float float2 = (float)int0 / int1;
            float float3 = (float)int2 / int1;
            float0 += y0;
            float1 += z0;
            int1 = int1 < 0 ? -1 : 1;
            float2 *= int1;
            float3 *= int1;

            while (x0 != x1) {
                x0 += int1;
                float0 += float2;
                float1 += float3;
                IsoGridSquare square1 = cell.getGridSquare(x0, (int)float0, (int)float1);
                if (square1 != null && square0 != null) {
                    boolean boolean0 = square1.CalculateCollide(square0, false, false, true, true);
                    if (!bIgnoreDoors && square1.isDoorBlockedTo(square0)) {
                        boolean0 = true;
                    }

                    if (boolean0) {
                        return true;
                    }
                }

                square0 = square1;
                int int3 = (int)float0;
                int int4 = (int)float1;
            }
        } else if (Math.abs(int0) >= Math.abs(int1) && Math.abs(int0) > Math.abs(int2)) {
            float float4 = (float)int1 / int0;
            float float5 = (float)int2 / int0;
            float0 += x0;
            float1 += z0;
            int0 = int0 < 0 ? -1 : 1;
            float4 *= int0;
            float5 *= int0;

            while (y0 != y1) {
                y0 += int0;
                float0 += float4;
                float1 += float5;
                IsoGridSquare square2 = cell.getGridSquare((int)float0, y0, (int)float1);
                if (square2 != null && square0 != null) {
                    boolean boolean1 = square2.CalculateCollide(square0, false, false, true, true);
                    if (!bIgnoreDoors && square2.isDoorBlockedTo(square0)) {
                        boolean1 = true;
                    }

                    if (boolean1) {
                        return true;
                    }
                }

                square0 = square2;
                int int5 = (int)float0;
                int int6 = (int)float1;
            }
        } else {
            float float6 = (float)int1 / int2;
            float float7 = (float)int0 / int2;
            float0 += x0;
            float1 += y0;
            int2 = int2 < 0 ? -1 : 1;
            float6 *= int2;
            float7 *= int2;

            while (z0 != z1) {
                z0 += int2;
                float0 += float6;
                float1 += float7;
                IsoGridSquare square3 = cell.getGridSquare((int)float0, (int)float1, z0);
                if (square3 != null && square0 != null) {
                    boolean boolean2 = square3.CalculateCollide(square0, false, false, true, true);
                    if (boolean2) {
                        return true;
                    }
                }

                square0 = square3;
                int int7 = (int)float0;
                int int8 = (int)float1;
            }
        }

        return false;
    }

    public static int lineClearCollideCount(IsoGameCharacter chr, IsoCell cell, int x1, int y1, int z1, int x0, int y0, int z0) {
        int int0 = 0;
        int int1 = y1 - y0;
        int int2 = x1 - x0;
        int int3 = z1 - z0;
        float float0 = 0.5F;
        float float1 = 0.5F;
        IsoGridSquare square0 = cell.getGridSquare(x0, y0, z0);
        if (Math.abs(int2) > Math.abs(int1) && Math.abs(int2) > Math.abs(int3)) {
            float float2 = (float)int1 / int2;
            float float3 = (float)int3 / int2;
            float0 += y0;
            float1 += z0;
            int2 = int2 < 0 ? -1 : 1;
            float2 *= int2;
            float3 *= int2;

            while (x0 != x1) {
                x0 += int2;
                float0 += float2;
                float1 += float3;
                IsoGridSquare square1 = cell.getGridSquare(x0, (int)float0, (int)float1);
                if (square1 != null && square0 != null) {
                    boolean boolean0 = square0.testCollideAdjacent(
                        chr, square1.getX() - square0.getX(), square1.getY() - square0.getY(), square1.getZ() - square0.getZ()
                    );
                    if (boolean0) {
                        return int0;
                    }
                }

                int0++;
                square0 = square1;
                int int4 = (int)float0;
                int int5 = (int)float1;
            }
        } else if (Math.abs(int1) >= Math.abs(int2) && Math.abs(int1) > Math.abs(int3)) {
            float float4 = (float)int2 / int1;
            float float5 = (float)int3 / int1;
            float0 += x0;
            float1 += z0;
            int1 = int1 < 0 ? -1 : 1;
            float4 *= int1;
            float5 *= int1;

            while (y0 != y1) {
                y0 += int1;
                float0 += float4;
                float1 += float5;
                IsoGridSquare square2 = cell.getGridSquare((int)float0, y0, (int)float1);
                if (square2 != null && square0 != null) {
                    boolean boolean1 = square0.testCollideAdjacent(
                        chr, square2.getX() - square0.getX(), square2.getY() - square0.getY(), square2.getZ() - square0.getZ()
                    );
                    if (boolean1) {
                        return int0;
                    }
                }

                int0++;
                square0 = square2;
                int int6 = (int)float0;
                int int7 = (int)float1;
            }
        } else {
            float float6 = (float)int2 / int3;
            float float7 = (float)int1 / int3;
            float0 += x0;
            float1 += y0;
            int3 = int3 < 0 ? -1 : 1;
            float6 *= int3;
            float7 *= int3;

            while (z0 != z1) {
                z0 += int3;
                float0 += float6;
                float1 += float7;
                IsoGridSquare square3 = cell.getGridSquare((int)float0, (int)float1, z0);
                if (square3 != null && square0 != null) {
                    boolean boolean2 = square0.testCollideAdjacent(
                        chr, square3.getX() - square0.getX(), square3.getY() - square0.getY(), square3.getZ() - square0.getZ()
                    );
                    if (boolean2) {
                        return int0;
                    }
                }

                int0++;
                square0 = square3;
                int int8 = (int)float0;
                int int9 = (int)float1;
            }
        }

        return int0;
    }

    public static LosUtil.TestResults lineClearCached(IsoCell cell, int x1, int y1, int z1, int x0, int y0, int z0, boolean bIgnoreDoors, int playerIndex) {
        if (z1 == z0 - 1) {
            IsoGridSquare square0 = cell.getGridSquare(x1, y1, z1);
            if (square0 != null && square0.HasElevatedFloor()) {
                z1 = z0;
            }
        }

        int int0 = x0;
        int int1 = y0;
        int int2 = z0;
        int int3 = y1 - y0;
        int int4 = x1 - x0;
        int int5 = z1 - z0;
        int int6 = int4 + XSIZE / 2;
        int int7 = int3 + YSIZE / 2;
        int int8 = int5 + ZSIZE / 2;
        if (int6 >= 0 && int7 >= 0 && int8 >= 0 && int6 < XSIZE && int7 < YSIZE && int8 < ZSIZE) {
            LosUtil.TestResults testResults = LosUtil.TestResults.Clear;
            byte byte0 = 1;
            if (cachedresults[int6][int7][int8][playerIndex] != 0) {
                if (cachedresults[int6][int7][int8][playerIndex] == 1) {
                    testResults = LosUtil.TestResults.Clear;
                }

                if (cachedresults[int6][int7][int8][playerIndex] == 2) {
                    testResults = LosUtil.TestResults.ClearThroughOpenDoor;
                }

                if (cachedresults[int6][int7][int8][playerIndex] == 3) {
                    testResults = LosUtil.TestResults.ClearThroughWindow;
                }

                if (cachedresults[int6][int7][int8][playerIndex] == 4) {
                    testResults = LosUtil.TestResults.Blocked;
                }

                if (cachedresults[int6][int7][int8][playerIndex] == 5) {
                    testResults = LosUtil.TestResults.ClearThroughClosedDoor;
                }

                return testResults;
            } else {
                float float0 = 0.5F;
                float float1 = 0.5F;
                IsoGridSquare square1 = cell.getGridSquare(x0, y0, z0);
                if (Math.abs(int4) > Math.abs(int3) && Math.abs(int4) > Math.abs(int5)) {
                    float float2 = (float)int3 / int4;
                    float float3 = (float)int5 / int4;
                    float0 += y0;
                    float1 += z0;
                    int4 = int4 < 0 ? -1 : 1;
                    float2 *= int4;
                    float3 *= int4;

                    while (x0 != x1) {
                        x0 += int4;
                        float0 += float2;
                        float1 += float3;
                        IsoGridSquare square2 = cell.getGridSquare(x0, (int)float0, (int)float1);
                        if (square2 != null && square1 != null) {
                            if (byte0 != 4
                                && square2.testVisionAdjacent(
                                        square1.getX() - square2.getX(), square1.getY() - square2.getY(), square1.getZ() - square2.getZ(), true, bIgnoreDoors
                                    )
                                    == LosUtil.TestResults.Blocked) {
                                byte0 = 4;
                            }

                            int int9 = x0 - int0;
                            int int10 = (int)float0 - int1;
                            int int11 = (int)float1 - int2;
                            int9 += XSIZE / 2;
                            int10 += YSIZE / 2;
                            int11 += ZSIZE / 2;
                            if (cachedresults[int9][int10][int11][playerIndex] == 0) {
                                cachedresults[int9][int10][int11][playerIndex] = (byte)byte0;
                            }
                        } else {
                            int int12 = x0 - int0;
                            int int13 = (int)float0 - int1;
                            int int14 = (int)float1 - int2;
                            int12 += XSIZE / 2;
                            int13 += YSIZE / 2;
                            int14 += ZSIZE / 2;
                            if (cachedresults[int12][int13][int14][playerIndex] == 0) {
                                cachedresults[int12][int13][int14][playerIndex] = (byte)byte0;
                            }
                        }

                        square1 = square2;
                        int int15 = (int)float0;
                        int int16 = (int)float1;
                    }
                } else if (Math.abs(int3) >= Math.abs(int4) && Math.abs(int3) > Math.abs(int5)) {
                    float float4 = (float)int4 / int3;
                    float float5 = (float)int5 / int3;
                    float0 += x0;
                    float1 += z0;
                    int3 = int3 < 0 ? -1 : 1;
                    float4 *= int3;
                    float5 *= int3;

                    while (y0 != y1) {
                        y0 += int3;
                        float0 += float4;
                        float1 += float5;
                        IsoGridSquare square3 = cell.getGridSquare((int)float0, y0, (int)float1);
                        if (square3 != null && square1 != null) {
                            if (byte0 != 4
                                && square3.testVisionAdjacent(
                                        square1.getX() - square3.getX(), square1.getY() - square3.getY(), square1.getZ() - square3.getZ(), true, bIgnoreDoors
                                    )
                                    == LosUtil.TestResults.Blocked) {
                                byte0 = 4;
                            }

                            int int17 = (int)float0 - int0;
                            int int18 = y0 - int1;
                            int int19 = (int)float1 - int2;
                            int17 += XSIZE / 2;
                            int18 += YSIZE / 2;
                            int19 += ZSIZE / 2;
                            if (cachedresults[int17][int18][int19][playerIndex] == 0) {
                                cachedresults[int17][int18][int19][playerIndex] = (byte)byte0;
                            }
                        } else {
                            int int20 = (int)float0 - int0;
                            int int21 = y0 - int1;
                            int int22 = (int)float1 - int2;
                            int20 += XSIZE / 2;
                            int21 += YSIZE / 2;
                            int22 += ZSIZE / 2;
                            if (cachedresults[int20][int21][int22][playerIndex] == 0) {
                                cachedresults[int20][int21][int22][playerIndex] = (byte)byte0;
                            }
                        }

                        square1 = square3;
                        int int23 = (int)float0;
                        int int24 = (int)float1;
                    }
                } else {
                    float float6 = (float)int4 / int5;
                    float float7 = (float)int3 / int5;
                    float0 += x0;
                    float1 += y0;
                    int5 = int5 < 0 ? -1 : 1;
                    float6 *= int5;
                    float7 *= int5;

                    while (z0 != z1) {
                        z0 += int5;
                        float0 += float6;
                        float1 += float7;
                        IsoGridSquare square4 = cell.getGridSquare((int)float0, (int)float1, z0);
                        if (square4 != null && square1 != null) {
                            if (byte0 != 4
                                && square4.testVisionAdjacent(
                                        square1.getX() - square4.getX(), square1.getY() - square4.getY(), square1.getZ() - square4.getZ(), true, bIgnoreDoors
                                    )
                                    == LosUtil.TestResults.Blocked) {
                                byte0 = 4;
                            }

                            int int25 = (int)float0 - int0;
                            int int26 = (int)float1 - int1;
                            int int27 = z0 - int2;
                            int25 += XSIZE / 2;
                            int26 += YSIZE / 2;
                            int27 += ZSIZE / 2;
                            if (cachedresults[int25][int26][int27][playerIndex] == 0) {
                                cachedresults[int25][int26][int27][playerIndex] = (byte)byte0;
                            }
                        } else {
                            int int28 = (int)float0 - int0;
                            int int29 = (int)float1 - int1;
                            int int30 = z0 - int2;
                            int28 += XSIZE / 2;
                            int29 += YSIZE / 2;
                            int30 += ZSIZE / 2;
                            if (cachedresults[int28][int29][int30][playerIndex] == 0) {
                                cachedresults[int28][int29][int30][playerIndex] = (byte)byte0;
                            }
                        }

                        square1 = square4;
                        int int31 = (int)float0;
                        int int32 = (int)float1;
                    }
                }

                if (byte0 == 1) {
                    cachedresults[int6][int7][int8][playerIndex] = (byte)byte0;
                    return LosUtil.TestResults.Clear;
                } else if (byte0 == 2) {
                    cachedresults[int6][int7][int8][playerIndex] = (byte)byte0;
                    return LosUtil.TestResults.ClearThroughOpenDoor;
                } else if (byte0 == 3) {
                    cachedresults[int6][int7][int8][playerIndex] = (byte)byte0;
                    return LosUtil.TestResults.ClearThroughWindow;
                } else if (byte0 == 4) {
                    cachedresults[int6][int7][int8][playerIndex] = (byte)byte0;
                    return LosUtil.TestResults.Blocked;
                } else if (byte0 == 5) {
                    cachedresults[int6][int7][int8][playerIndex] = (byte)byte0;
                    return LosUtil.TestResults.ClearThroughClosedDoor;
                } else {
                    return LosUtil.TestResults.Blocked;
                }
            }
        } else {
            return LosUtil.TestResults.Blocked;
        }
    }

    static {
        for (int int0 = 0; int0 < 4; int0++) {
            cachecleared[int0] = true;
        }
    }

    public static enum TestResults {
        Clear,
        ClearThroughOpenDoor,
        ClearThroughWindow,
        Blocked,
        ClearThroughClosedDoor;
    }
}
