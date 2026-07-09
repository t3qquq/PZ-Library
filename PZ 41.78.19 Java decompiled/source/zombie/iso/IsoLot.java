// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import gnu.trove.list.array.TIntArrayList;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import zombie.ChunkMapFilenames;
import zombie.core.logger.ExceptionLogger;
import zombie.popman.ObjectPool;
import zombie.util.BufferedRandomAccessFile;

public class IsoLot {
    public static final HashMap<String, LotHeader> InfoHeaders = new HashMap<>();
    public static final ArrayList<String> InfoHeaderNames = new ArrayList<>();
    public static final HashMap<String, String> InfoFileNames = new HashMap<>();
    public static final ObjectPool<IsoLot> pool = new ObjectPool<>(IsoLot::new);
    private String m_lastUsedPath = "";
    public int wx = 0;
    public int wy = 0;
    final int[] m_offsetInData = new int[800];
    final TIntArrayList m_data = new TIntArrayList();
    private RandomAccessFile m_in = null;
    LotHeader info;

    public static void Dispose() {
        for (LotHeader lotHeader : InfoHeaders.values()) {
            lotHeader.Dispose();
        }

        InfoHeaders.clear();
        InfoHeaderNames.clear();
        InfoFileNames.clear();
        pool.forEach(lot -> {
            RandomAccessFile randomAccessFile = lot.m_in;
            if (randomAccessFile != null) {
                lot.m_in = null;

                try {
                    randomAccessFile.close();
                } catch (IOException iOException) {
                    ExceptionLogger.logException(iOException);
                }
            }
        });
    }

    public static String readString(BufferedRandomAccessFile __in__) throws EOFException, IOException {
        return __in__.getNextLine();
    }

    public static int readInt(RandomAccessFile __in__) throws EOFException, IOException {
        int int0 = __in__.read();
        int int1 = __in__.read();
        int int2 = __in__.read();
        int int3 = __in__.read();
        if ((int0 | int1 | int2 | int3) < 0) {
            throw new EOFException();
        } else {
            return (int0 << 0) + (int1 << 8) + (int2 << 16) + (int3 << 24);
        }
    }

    public static int readShort(RandomAccessFile __in__) throws EOFException, IOException {
        int int0 = __in__.read();
        int int1 = __in__.read();
        if ((int0 | int1) < 0) {
            throw new EOFException();
        } else {
            return (int0 << 0) + (int1 << 8);
        }
    }

    public static synchronized void put(IsoLot lot) {
        lot.info = null;
        lot.m_data.resetQuick();
        pool.release(lot);
    }

    public static synchronized IsoLot get(Integer cX, Integer cY, Integer wX, Integer wY, IsoChunk ch) {
        IsoLot lot = pool.alloc();
        lot.load(cX, cY, wX, wY, ch);
        return lot;
    }

    public void load(Integer cX, Integer cY, Integer wX, Integer wY, IsoChunk ch) {
        String string = ChunkMapFilenames.instance.getHeader(cX, cY);
        this.info = InfoHeaders.get(string);
        this.wx = wX;
        this.wy = wY;
        ch.lotheader = this.info;

        try {
            string = "world_" + cX + "_" + cY + ".lotpack";
            File file = new File(InfoFileNames.get(string));
            if (this.m_in == null || !this.m_lastUsedPath.equals(file.getAbsolutePath())) {
                if (this.m_in != null) {
                    this.m_in.close();
                }

                this.m_in = new BufferedRandomAccessFile(file.getAbsolutePath(), "r", 4096);
                this.m_lastUsedPath = file.getAbsolutePath();
            }

            int int0 = 0;
            int int1 = this.wx - cX * 30;
            int int2 = this.wy - cY * 30;
            int int3 = int1 * 30 + int2;
            this.m_in.seek(4 + int3 * 8);
            int int4 = readInt(this.m_in);
            this.m_in.seek(int4);
            this.m_data.resetQuick();
            int int5 = Math.min(this.info.levels, 8);

            for (int int6 = 0; int6 < int5; int6++) {
                for (int int7 = 0; int7 < 10; int7++) {
                    for (int int8 = 0; int8 < 10; int8++) {
                        int int9 = int7 + int8 * 10 + int6 * 100;
                        this.m_offsetInData[int9] = -1;
                        if (int0 > 0) {
                            int0--;
                        } else {
                            int int10 = readInt(this.m_in);
                            if (int10 == -1) {
                                int0 = readInt(this.m_in);
                                if (int0 > 0) {
                                    int0--;
                                    continue;
                                }
                            }

                            if (int10 > 1) {
                                this.m_offsetInData[int9] = this.m_data.size();
                                this.m_data.add(int10 - 1);
                                int int11 = readInt(this.m_in);

                                for (int int12 = 1; int12 < int10; int12++) {
                                    int int13 = readInt(this.m_in);
                                    this.m_data.add(int13);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception exception) {
            Arrays.fill(this.m_offsetInData, -1);
            this.m_data.resetQuick();
            ExceptionLogger.logException(exception);
        }
    }
}
