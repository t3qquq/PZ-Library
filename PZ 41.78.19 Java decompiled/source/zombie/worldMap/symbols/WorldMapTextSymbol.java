// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap.symbols;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.core.Translator;
import zombie.network.GameServer;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.worldMap.UIWorldMap;

public final class WorldMapTextSymbol extends WorldMapBaseSymbol {
    String m_text;
    boolean m_translated = false;
    UIFont m_font = UIFont.Handwritten;

    public WorldMapTextSymbol(WorldMapSymbols owner) {
        super(owner);
    }

    public void setTranslatedText(String text) {
        this.m_text = text;
        this.m_translated = true;
        if (!GameServer.bServer) {
            this.m_width = TextManager.instance.MeasureStringX(this.m_font, this.getTranslatedText());
            this.m_height = TextManager.instance.getFontHeight(this.m_font);
        }
    }

    public void setUntranslatedText(String text) {
        this.m_text = text;
        this.m_translated = false;
        if (!GameServer.bServer) {
            this.m_width = TextManager.instance.MeasureStringX(this.m_font, this.getTranslatedText());
            this.m_height = TextManager.instance.getFontHeight(this.m_font);
        }
    }

    public String getTranslatedText() {
        return this.m_translated ? this.m_text : Translator.getText(this.m_text);
    }

    public String getUntranslatedText() {
        return this.m_translated ? null : this.m_text;
    }

    @Override
    public WorldMapSymbols.WorldMapSymbolType getType() {
        return WorldMapSymbols.WorldMapSymbolType.Text;
    }

    @Override
    public boolean isVisible() {
        return this.m_owner.getMiniMapSymbols() ? false : super.isVisible();
    }

    @Override
    public void save(ByteBuffer output) throws IOException {
        super.save(output);
        GameWindow.WriteString(output, this.m_text);
        output.put((byte)(this.m_translated ? 1 : 0));
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, int SymbolsVersion) throws IOException {
        super.load(input, WorldVersion, SymbolsVersion);
        this.m_text = GameWindow.ReadString(input);
        this.m_translated = input.get() == 1;
    }

    @Override
    public void render(UIWorldMap ui, float rox, float roy) {
        if (this.m_width == 0.0F || this.m_height == 0.0F) {
            this.m_width = TextManager.instance.MeasureStringX(this.m_font, this.getTranslatedText());
            this.m_height = TextManager.instance.getFontHeight(this.m_font);
        }

        if (this.m_collided) {
            this.renderCollided(ui, rox, roy);
        } else {
            float float0 = rox + this.m_layoutX;
            float float1 = roy + this.m_layoutY;
            if (this.m_scale > 0.0F) {
                ui.DrawText(this.m_font, this.getTranslatedText(), float0, float1, this.getDisplayScale(ui), this.m_r, this.m_g, this.m_b, this.m_a);
            } else {
                ui.DrawText(this.m_font, this.getTranslatedText(), float0, float1, this.m_r, this.m_g, this.m_b, this.m_a);
            }
        }
    }

    @Override
    public void release() {
        this.m_text = null;
    }
}
