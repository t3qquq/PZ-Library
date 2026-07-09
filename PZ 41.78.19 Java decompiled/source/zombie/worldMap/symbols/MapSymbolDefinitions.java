// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap.symbols;

import java.util.ArrayList;
import java.util.HashMap;
import zombie.core.textures.Texture;

public final class MapSymbolDefinitions {
    private static MapSymbolDefinitions instance;
    private final ArrayList<MapSymbolDefinitions.MapSymbolDefinition> m_symbolList = new ArrayList<>();
    private final HashMap<String, MapSymbolDefinitions.MapSymbolDefinition> m_symbolByID = new HashMap<>();

    public static MapSymbolDefinitions getInstance() {
        if (instance == null) {
            instance = new MapSymbolDefinitions();
        }

        return instance;
    }

    public void addTexture(String string0, String string1, int int0, int int1) {
        MapSymbolDefinitions.MapSymbolDefinition mapSymbolDefinition = new MapSymbolDefinitions.MapSymbolDefinition();
        mapSymbolDefinition.id = string0;
        mapSymbolDefinition.texturePath = string1;
        mapSymbolDefinition.width = int0;
        mapSymbolDefinition.height = int1;
        this.m_symbolList.add(mapSymbolDefinition);
        this.m_symbolByID.put(string0, mapSymbolDefinition);
    }

    public void addTexture(String string1, String string0) {
        Texture texture = Texture.getSharedTexture(string0);
        if (texture == null) {
            this.addTexture(string1, string0, 18, 18);
        } else {
            this.addTexture(string1, string0, texture.getWidth(), texture.getHeight());
        }
    }

    public int getSymbolCount() {
        return this.m_symbolList.size();
    }

    public MapSymbolDefinitions.MapSymbolDefinition getSymbolByIndex(int int0) {
        return this.m_symbolList.get(int0);
    }

    public MapSymbolDefinitions.MapSymbolDefinition getSymbolById(String string) {
        return this.m_symbolByID.get(string);
    }

    public static void Reset() {
        if (instance != null) {
            getInstance().m_symbolList.clear();
            getInstance().m_symbolByID.clear();
        }
    }

    public static final class MapSymbolDefinition {
        private String id;
        private String texturePath;
        private int width;
        private int height;

        public String getId() {
            return this.id;
        }

        public String getTexturePath() {
            return this.texturePath;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }
    }
}
