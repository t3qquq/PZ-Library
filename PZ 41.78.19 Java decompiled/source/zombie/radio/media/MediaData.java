// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.media;

import java.util.ArrayList;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Translator;
import zombie.debug.DebugLog;

/**
 * TurboTuTone.
 */
public final class MediaData {
    private final String id;
    private final String itemDisplayName;
    private String title;
    private String subtitle;
    private String author;
    private String extra;
    private short index;
    private String category;
    private final int spawning;
    private final ArrayList<MediaData.MediaLineData> lines = new ArrayList<>();

    public MediaData(String _id, String _itemDisplayName, int _spawning) {
        this.itemDisplayName = _itemDisplayName;
        this.id = _id;
        this.spawning = _spawning;
        if (Core.bDebug) {
            if (_itemDisplayName == null) {
                throw new RuntimeException("ItemDisplayName may not be null.");
            }

            if (_id == null) {
                throw new RuntimeException("Id may not be null.");
            }
        }
    }

    public void addLine(String text, float r, float g, float b, String codes) {
        MediaData.MediaLineData mediaLineData = new MediaData.MediaLineData(text, r, g, b, codes);
        this.lines.add(mediaLineData);
    }

    public int getLineCount() {
        return this.lines.size();
    }

    public String getTranslatedItemDisplayName() {
        return Translator.getText(this.itemDisplayName);
    }

    public boolean hasTitle() {
        return this.title != null;
    }

    public void setTitle(String _title) {
        this.title = _title;
    }

    public String getTitleEN() {
        return this.title != null ? Translator.getTextMediaEN(this.title) : null;
    }

    public String getTranslatedTitle() {
        return this.title != null ? Translator.getText(this.title) : null;
    }

    public boolean hasSubTitle() {
        return this.subtitle != null;
    }

    public void setSubtitle(String _subtitle) {
        this.subtitle = _subtitle;
    }

    public String getSubtitleEN() {
        return this.subtitle != null ? Translator.getTextMediaEN(this.subtitle) : null;
    }

    public String getTranslatedSubTitle() {
        return this.subtitle != null ? Translator.getText(this.subtitle) : null;
    }

    public boolean hasAuthor() {
        return this.author != null;
    }

    public void setAuthor(String _author) {
        this.author = _author;
    }

    public String getAuthorEN() {
        return this.author != null ? Translator.getTextMediaEN(this.author) : null;
    }

    public String getTranslatedAuthor() {
        return this.author != null ? Translator.getText(this.author) : null;
    }

    public boolean hasExtra() {
        return this.extra != null;
    }

    public void setExtra(String _extra) {
        this.extra = _extra;
    }

    public String getExtraEN() {
        return this.extra != null ? Translator.getTextMediaEN(this.extra) : null;
    }

    public String getTranslatedExtra() {
        return this.extra != null ? Translator.getText(this.extra) : null;
    }

    public String getId() {
        return this.id;
    }

    public short getIndex() {
        return this.index;
    }

    protected void setIndex(short short0) {
        this.index = short0;
    }

    public String getCategory() {
        return this.category;
    }

    protected void setCategory(String string) {
        this.category = string;
    }

    public int getSpawning() {
        return this.spawning;
    }

    public byte getMediaType() {
        if (this.category == null) {
            DebugLog.log("Warning MediaData has no category set, mediadata = " + (this.itemDisplayName != null ? this.itemDisplayName : "unknown"));
        }

        return RecordedMedia.getMediaTypeForCategory(this.category);
    }

    public MediaData.MediaLineData getLine(int _index) {
        return _index >= 0 && _index < this.lines.size() ? this.lines.get(_index) : null;
    }

    public static final class MediaLineData {
        private final String text;
        private final Color color;
        private final String codes;

        public MediaLineData(String _text, float r, float g, float b, String _codes) {
            this.text = _text;
            this.codes = _codes;
            if (r == 0.0F && g == 0.0F && b == 0.0F) {
                r = 1.0F;
                g = 1.0F;
                b = 1.0F;
            }

            this.color = new Color(r, g, b);
        }

        public String getTranslatedText() {
            return Translator.getText(this.text);
        }

        public Color getColor() {
            return this.color;
        }

        public float getR() {
            return this.color.r;
        }

        public float getG() {
            return this.color.g;
        }

        public float getB() {
            return this.color.b;
        }

        public String getCodes() {
            return this.codes;
        }

        public String getTextGuid() {
            return this.text;
        }
    }
}
