// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory.types;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import zombie.GameWindow;
import zombie.ZomboidFileSystem;
import zombie.core.logger.ExceptionLogger;
import zombie.inventory.InventoryItem;
import zombie.iso.SliceY;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.worldMap.symbols.WorldMapSymbols;

public class MapItem extends InventoryItem {
    public static MapItem WORLD_MAP_INSTANCE;
    private static final byte[] FILE_MAGIC = new byte[]{87, 77, 83, 89};
    private String m_mapID;
    private final WorldMapSymbols m_symbols = new WorldMapSymbols();

    public static MapItem getSingleton() {
        if (WORLD_MAP_INSTANCE == null) {
            Item item = ScriptManager.instance.FindItem("Base.Map");
            if (item == null) {
                return null;
            }

            WORLD_MAP_INSTANCE = new MapItem("Base", "World Map", "WorldMap", item);
        }

        return WORLD_MAP_INSTANCE;
    }

    public static void SaveWorldMap() {
        if (WORLD_MAP_INSTANCE != null) {
            try {
                ByteBuffer byteBuffer = SliceY.SliceBuffer;
                byteBuffer.clear();
                byteBuffer.put(FILE_MAGIC);
                byteBuffer.putInt(195);
                WORLD_MAP_INSTANCE.getSymbols().save(byteBuffer);
                File file = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave("map_symbols.bin"));

                try (
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                ) {
                    bufferedOutputStream.write(byteBuffer.array(), 0, byteBuffer.position());
                }
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
            }
        }
    }

    public static void LoadWorldMap() {
        if (getSingleton() != null) {
            File file = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave("map_symbols.bin"));

            try (
                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            ) {
                ByteBuffer byteBuffer = SliceY.SliceBuffer;
                byteBuffer.clear();
                int int0 = bufferedInputStream.read(byteBuffer.array());
                byteBuffer.limit(int0);
                byte[] bytes = new byte[4];
                byteBuffer.get(bytes);
                if (!Arrays.equals(bytes, FILE_MAGIC)) {
                    throw new IOException(file.getAbsolutePath() + " does not appear to be map_symbols.bin");
                }

                int int1 = byteBuffer.getInt();
                getSingleton().getSymbols().load(byteBuffer, int1);
            } catch (FileNotFoundException fileNotFoundException) {
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
            }
        }
    }

    public static void Reset() {
        if (WORLD_MAP_INSTANCE != null) {
            WORLD_MAP_INSTANCE.getSymbols().clear();
            WORLD_MAP_INSTANCE = null;
        }
    }

    public MapItem(String module, String name, String type, String tex) {
        super(module, name, type, tex);
    }

    public MapItem(String module, String name, String type, Item item) {
        super(module, name, type, item);
    }

    @Override
    public int getSaveType() {
        return Item.Type.Map.ordinal();
    }

    @Override
    public boolean IsMap() {
        return true;
    }

    public void setMapID(String mapID) {
        this.m_mapID = mapID;
    }

    public String getMapID() {
        return this.m_mapID;
    }

    public WorldMapSymbols getSymbols() {
        return this.m_symbols;
    }

    @Override
    public void save(ByteBuffer output, boolean net) throws IOException {
        super.save(output, net);
        GameWindow.WriteString(output, this.m_mapID);
        this.m_symbols.save(output);
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion) throws IOException {
        super.load(input, WorldVersion);
        this.m_mapID = GameWindow.ReadString(input);
        this.m_symbols.load(input, WorldVersion);
    }
}
