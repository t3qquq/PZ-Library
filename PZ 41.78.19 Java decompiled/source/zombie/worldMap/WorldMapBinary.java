// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import gnu.trove.map.hash.TIntObjectHashMap;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameWindow;
import zombie.util.SharedStrings;

public final class WorldMapBinary {
    private static final int VERSION1 = 1;
    private static final int VERSION_LATEST = 1;
    private final SharedStrings m_sharedStrings = new SharedStrings();
    private final TIntObjectHashMap<String> m_stringTable = new TIntObjectHashMap<>();
    private final WorldMapProperties m_properties = new WorldMapProperties();
    private final ArrayList<WorldMapProperties> m_sharedProperties = new ArrayList<>();

    public boolean read(String string, WorldMapData worldMapData) throws Exception {
        try (
            FileInputStream fileInputStream = new FileInputStream(string);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ) {
            int int0 = bufferedInputStream.read();
            int int1 = bufferedInputStream.read();
            int int2 = bufferedInputStream.read();
            int int3 = bufferedInputStream.read();
            if (int0 == 73 && int1 == 71 && int2 == 77 && int3 == 66) {
                int int4 = this.readInt(bufferedInputStream);
                if (int4 >= 1 && int4 <= 1) {
                    int int5 = this.readInt(bufferedInputStream);
                    int int6 = this.readInt(bufferedInputStream);
                    this.readStringTable(bufferedInputStream);

                    for (int int7 = 0; int7 < int6; int7++) {
                        for (int int8 = 0; int8 < int5; int8++) {
                            WorldMapCell worldMapCell = this.parseCell(bufferedInputStream);
                            if (worldMapCell != null) {
                                worldMapData.m_cells.add(worldMapCell);
                            }
                        }
                    }

                    return true;
                } else {
                    throw new IOException("unrecognized version " + int4);
                }
            } else {
                throw new IOException("invalid format (magic doesn't match)");
            }
        }
    }

    private int readByte(InputStream inputStream) throws IOException {
        return inputStream.read();
    }

    private int readInt(InputStream inputStream) throws IOException {
        int int0 = inputStream.read();
        int int1 = inputStream.read();
        int int2 = inputStream.read();
        int int3 = inputStream.read();
        if ((int0 | int1 | int2 | int3) < 0) {
            throw new EOFException();
        } else {
            return (int0 << 0) + (int1 << 8) + (int2 << 16) + (int3 << 24);
        }
    }

    private int readShort(InputStream inputStream) throws IOException {
        int int0 = inputStream.read();
        int int1 = inputStream.read();
        if ((int0 | int1) < 0) {
            throw new EOFException();
        } else {
            return (short)((int0 << 0) + (int1 << 8));
        }
    }

    private void readStringTable(InputStream inputStream) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byte[] bytes = new byte[1024];
        int int0 = this.readInt(inputStream);

        for (int int1 = 0; int1 < int0; int1++) {
            byteBuffer.clear();
            int int2 = this.readShort(inputStream);
            byteBuffer.putShort((short)int2);
            inputStream.read(bytes, 0, int2);
            byteBuffer.put(bytes, 0, int2);
            byteBuffer.flip();
            this.m_stringTable.put(int1, GameWindow.ReadStringUTF(byteBuffer));
        }
    }

    private String readStringIndexed(InputStream inputStream) throws IOException {
        int int0 = this.readShort(inputStream);
        if (!this.m_stringTable.containsKey(int0)) {
            throw new IOException("invalid string-table index " + int0);
        } else {
            return this.m_stringTable.get(int0);
        }
    }

    private WorldMapCell parseCell(InputStream inputStream) throws IOException {
        int int0 = this.readInt(inputStream);
        if (int0 == -1) {
            return null;
        } else {
            int int1 = this.readInt(inputStream);
            WorldMapCell worldMapCell = new WorldMapCell();
            worldMapCell.m_x = int0;
            worldMapCell.m_y = int1;
            int int2 = this.readInt(inputStream);

            for (int int3 = 0; int3 < int2; int3++) {
                WorldMapFeature worldMapFeature = this.parseFeature(worldMapCell, inputStream);
                worldMapCell.m_features.add(worldMapFeature);
            }

            return worldMapCell;
        }
    }

    private WorldMapFeature parseFeature(WorldMapCell worldMapCell, InputStream inputStream) throws IOException {
        WorldMapFeature worldMapFeature = new WorldMapFeature(worldMapCell);
        WorldMapGeometry worldMapGeometry = this.parseGeometry(inputStream);
        worldMapFeature.m_geometries.add(worldMapGeometry);
        this.parseFeatureProperties(inputStream, worldMapFeature);
        return worldMapFeature;
    }

    private void parseFeatureProperties(InputStream inputStream, WorldMapFeature worldMapFeature) throws IOException {
        this.m_properties.clear();
        int int0 = this.readByte(inputStream);

        for (int int1 = 0; int1 < int0; int1++) {
            String string0 = this.m_sharedStrings.get(this.readStringIndexed(inputStream));
            String string1 = this.m_sharedStrings.get(this.readStringIndexed(inputStream));
            this.m_properties.put(string0, string1);
        }

        worldMapFeature.m_properties = this.getOrCreateProperties(this.m_properties);
    }

    private WorldMapProperties getOrCreateProperties(WorldMapProperties worldMapProperties0) {
        for (int int0 = 0; int0 < this.m_sharedProperties.size(); int0++) {
            if (this.m_sharedProperties.get(int0).equals(worldMapProperties0)) {
                return this.m_sharedProperties.get(int0);
            }
        }

        WorldMapProperties worldMapProperties1 = new WorldMapProperties();
        worldMapProperties1.putAll(worldMapProperties0);
        this.m_sharedProperties.add(worldMapProperties1);
        return worldMapProperties1;
    }

    private WorldMapGeometry parseGeometry(InputStream inputStream) throws IOException {
        WorldMapGeometry worldMapGeometry = new WorldMapGeometry();
        worldMapGeometry.m_type = WorldMapGeometry.Type.valueOf(this.readStringIndexed(inputStream));
        int int0 = this.readByte(inputStream);

        for (int int1 = 0; int1 < int0; int1++) {
            WorldMapPoints worldMapPoints = new WorldMapPoints();
            this.parseGeometryCoordinates(inputStream, worldMapPoints);
            worldMapGeometry.m_points.add(worldMapPoints);
        }

        worldMapGeometry.calculateBounds();
        return worldMapGeometry;
    }

    private void parseGeometryCoordinates(InputStream inputStream, WorldMapPoints worldMapPoints) throws IOException {
        int int0 = this.readShort(inputStream);

        for (int int1 = 0; int1 < int0; int1++) {
            worldMapPoints.add(this.readShort(inputStream));
            worldMapPoints.add(this.readShort(inputStream));
        }
    }
}
