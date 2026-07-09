// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap.symbols;

import java.util.ArrayList;
import java.util.Objects;
import zombie.Lua.LuaManager;
import zombie.ui.UIFont;
import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.worldMap.UIWorldMap;

public class WorldMapSymbolsV1 {
    private static final Pool<WorldMapSymbolsV1.WorldMapTextSymbolV1> s_textPool = new Pool<>(WorldMapSymbolsV1.WorldMapTextSymbolV1::new);
    private static final Pool<WorldMapSymbolsV1.WorldMapTextureSymbolV1> s_texturePool = new Pool<>(WorldMapSymbolsV1.WorldMapTextureSymbolV1::new);
    private final UIWorldMap m_ui;
    private final WorldMapSymbols m_uiSymbols;
    private final ArrayList<WorldMapSymbolsV1.WorldMapBaseSymbolV1> m_symbols = new ArrayList<>();

    public WorldMapSymbolsV1(UIWorldMap ui, WorldMapSymbols symbols) {
        Objects.requireNonNull(ui);
        this.m_ui = ui;
        this.m_uiSymbols = symbols;
        this.reinit();
    }

    public WorldMapSymbolsV1.WorldMapTextSymbolV1 addTranslatedText(String text, UIFont font, float x, float y) {
        WorldMapTextSymbol worldMapTextSymbol = this.m_uiSymbols.addTranslatedText(text, font, x, y, 1.0F, 1.0F, 1.0F, 1.0F);
        WorldMapSymbolsV1.WorldMapTextSymbolV1 worldMapTextSymbolV1 = s_textPool.alloc().init(this, worldMapTextSymbol);
        this.m_symbols.add(worldMapTextSymbolV1);
        return worldMapTextSymbolV1;
    }

    public WorldMapSymbolsV1.WorldMapTextSymbolV1 addUntranslatedText(String text, UIFont font, float x, float y) {
        WorldMapTextSymbol worldMapTextSymbol = this.m_uiSymbols.addUntranslatedText(text, font, x, y, 1.0F, 1.0F, 1.0F, 1.0F);
        WorldMapSymbolsV1.WorldMapTextSymbolV1 worldMapTextSymbolV1 = s_textPool.alloc().init(this, worldMapTextSymbol);
        this.m_symbols.add(worldMapTextSymbolV1);
        return worldMapTextSymbolV1;
    }

    public WorldMapSymbolsV1.WorldMapTextureSymbolV1 addTexture(String symbolID, float x, float y) {
        WorldMapTextureSymbol worldMapTextureSymbol = this.m_uiSymbols.addTexture(symbolID, x, y, 1.0F, 1.0F, 1.0F, 1.0F);
        WorldMapSymbolsV1.WorldMapTextureSymbolV1 worldMapTextureSymbolV1 = s_texturePool.alloc().init(this, worldMapTextureSymbol);
        this.m_symbols.add(worldMapTextureSymbolV1);
        return worldMapTextureSymbolV1;
    }

    public int hitTest(float uiX, float uiY) {
        return this.m_uiSymbols.hitTest(this.m_ui, uiX, uiY);
    }

    public int getSymbolCount() {
        return this.m_symbols.size();
    }

    public WorldMapSymbolsV1.WorldMapBaseSymbolV1 getSymbolByIndex(int index) {
        return this.m_symbols.get(index);
    }

    public void removeSymbolByIndex(int index) {
        this.m_uiSymbols.removeSymbolByIndex(index);
        this.m_symbols.remove(index).release();
    }

    public void clear() {
        this.m_uiSymbols.clear();
        this.reinit();
    }

    void reinit() {
        for (int int0 = 0; int0 < this.m_symbols.size(); int0++) {
            this.m_symbols.get(int0).release();
        }

        this.m_symbols.clear();

        for (int int1 = 0; int1 < this.m_uiSymbols.getSymbolCount(); int1++) {
            WorldMapBaseSymbol worldMapBaseSymbol = this.m_uiSymbols.getSymbolByIndex(int1);
            WorldMapTextSymbol worldMapTextSymbol = Type.tryCastTo(worldMapBaseSymbol, WorldMapTextSymbol.class);
            if (worldMapTextSymbol != null) {
                WorldMapSymbolsV1.WorldMapTextSymbolV1 worldMapTextSymbolV1 = s_textPool.alloc().init(this, worldMapTextSymbol);
                this.m_symbols.add(worldMapTextSymbolV1);
            }

            WorldMapTextureSymbol worldMapTextureSymbol = Type.tryCastTo(worldMapBaseSymbol, WorldMapTextureSymbol.class);
            if (worldMapTextureSymbol != null) {
                WorldMapSymbolsV1.WorldMapTextureSymbolV1 worldMapTextureSymbolV1 = s_texturePool.alloc().init(this, worldMapTextureSymbol);
                this.m_symbols.add(worldMapTextureSymbolV1);
            }
        }
    }

    public static void setExposed(LuaManager.Exposer exposer) {
        exposer.setExposed(WorldMapSymbolsV1.class);
        exposer.setExposed(WorldMapSymbolsV1.WorldMapTextSymbolV1.class);
        exposer.setExposed(WorldMapSymbolsV1.WorldMapTextureSymbolV1.class);
    }

    protected static class WorldMapBaseSymbolV1 extends PooledObject {
        WorldMapSymbolsV1 m_owner;
        WorldMapBaseSymbol m_symbol;

        WorldMapSymbolsV1.WorldMapBaseSymbolV1 init(WorldMapSymbolsV1 worldMapSymbolsV1, WorldMapBaseSymbol worldMapBaseSymbol) {
            this.m_owner = worldMapSymbolsV1;
            this.m_symbol = worldMapBaseSymbol;
            return this;
        }

        public float getWorldX() {
            return this.m_symbol.m_x;
        }

        public float getWorldY() {
            return this.m_symbol.m_y;
        }

        public float getDisplayX() {
            this.m_owner.m_uiSymbols.checkLayout(this.m_owner.m_ui);
            return this.m_symbol.m_layoutX + this.m_owner.m_ui.getAPIv1().worldOriginX();
        }

        public float getDisplayY() {
            this.m_owner.m_uiSymbols.checkLayout(this.m_owner.m_ui);
            return this.m_symbol.m_layoutY + this.m_owner.m_ui.getAPIv1().worldOriginY();
        }

        public float getDisplayWidth() {
            this.m_owner.m_uiSymbols.checkLayout(this.m_owner.m_ui);
            return this.m_symbol.widthScaled(this.m_owner.m_ui);
        }

        public float getDisplayHeight() {
            this.m_owner.m_uiSymbols.checkLayout(this.m_owner.m_ui);
            return this.m_symbol.heightScaled(this.m_owner.m_ui);
        }

        public void setAnchor(float arg0, float arg1) {
            this.m_symbol.setAnchor(arg0, arg1);
        }

        public void setPosition(float arg0, float arg1) {
            this.m_symbol.setPosition(arg0, arg1);
            this.m_owner.m_uiSymbols.invalidateLayout();
        }

        public void setCollide(boolean arg0) {
            this.m_symbol.setCollide(arg0);
        }

        public void setVisible(boolean arg0) {
            this.m_symbol.setVisible(arg0);
        }

        public boolean isVisible() {
            return this.m_symbol.isVisible();
        }

        public void setRGBA(float arg0, float arg1, float arg2, float arg3) {
            this.m_symbol.setRGBA(arg0, arg1, arg2, arg3);
        }

        public float getRed() {
            return this.m_symbol.m_r;
        }

        public float getGreen() {
            return this.m_symbol.m_g;
        }

        public float getBlue() {
            return this.m_symbol.m_b;
        }

        public float getAlpha() {
            return this.m_symbol.m_a;
        }

        public void setScale(float arg0) {
            this.m_symbol.setScale(arg0);
        }

        public boolean isText() {
            return false;
        }

        public boolean isTexture() {
            return false;
        }
    }

    public static class WorldMapTextSymbolV1 extends WorldMapSymbolsV1.WorldMapBaseSymbolV1 {
        WorldMapTextSymbol m_textSymbol;

        WorldMapSymbolsV1.WorldMapTextSymbolV1 init(WorldMapSymbolsV1 worldMapSymbolsV1, WorldMapTextSymbol worldMapTextSymbol) {
            super.init(worldMapSymbolsV1, worldMapTextSymbol);
            this.m_textSymbol = worldMapTextSymbol;
            return this;
        }

        public void setTranslatedText(String text) {
            if (!StringUtils.isNullOrWhitespace(text)) {
                this.m_textSymbol.setTranslatedText(text);
                this.m_owner.m_uiSymbols.invalidateLayout();
            }
        }

        public void setUntranslatedText(String text) {
            if (!StringUtils.isNullOrWhitespace(text)) {
                this.m_textSymbol.setUntranslatedText(text);
                this.m_owner.m_uiSymbols.invalidateLayout();
            }
        }

        public String getTranslatedText() {
            return this.m_textSymbol.getTranslatedText();
        }

        public String getUntranslatedText() {
            return this.m_textSymbol.getUntranslatedText();
        }

        @Override
        public boolean isText() {
            return true;
        }
    }

    public static class WorldMapTextureSymbolV1 extends WorldMapSymbolsV1.WorldMapBaseSymbolV1 {
        WorldMapTextureSymbol m_textureSymbol;

        WorldMapSymbolsV1.WorldMapTextureSymbolV1 init(WorldMapSymbolsV1 worldMapSymbolsV1, WorldMapTextureSymbol worldMapTextureSymbol) {
            super.init(worldMapSymbolsV1, worldMapTextureSymbol);
            this.m_textureSymbol = worldMapTextureSymbol;
            return this;
        }

        public String getSymbolID() {
            return this.m_textureSymbol.getSymbolID();
        }

        @Override
        public boolean isTexture() {
            return true;
        }
    }
}
