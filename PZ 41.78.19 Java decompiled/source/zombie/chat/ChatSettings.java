// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.chat;

import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.core.Color;
import zombie.core.network.ByteBufferWriter;
import zombie.ui.UIFont;

public class ChatSettings {
    private boolean unique;
    private Color fontColor;
    private UIFont font;
    private ChatSettings.FontSize fontSize;
    private boolean bold;
    private boolean allowImages;
    private boolean allowChatIcons;
    private boolean allowColors;
    private boolean allowFonts;
    private boolean allowBBcode;
    private boolean equalizeLineHeights;
    private boolean showAuthor;
    private boolean showTimestamp;
    private boolean showChatTitle;
    private boolean useOnlyActiveTab;
    private float range;
    private float zombieAttractionRange;
    public static final float infinityRange = -1.0F;

    public ChatSettings() {
        this.unique = true;
        this.fontColor = Color.white;
        this.font = UIFont.Dialogue;
        this.bold = true;
        this.showAuthor = true;
        this.showTimestamp = true;
        this.showChatTitle = true;
        this.range = -1.0F;
        this.zombieAttractionRange = -1.0F;
        this.useOnlyActiveTab = false;
        this.fontSize = ChatSettings.FontSize.Medium;
    }

    public ChatSettings(ByteBuffer bb) {
        this.unique = bb.get() == 1;
        this.fontColor = new Color(bb.getFloat(), bb.getFloat(), bb.getFloat(), bb.getFloat());
        this.font = UIFont.FromString(GameWindow.ReadString(bb));
        this.bold = bb.get() == 1;
        this.allowImages = bb.get() == 1;
        this.allowChatIcons = bb.get() == 1;
        this.allowColors = bb.get() == 1;
        this.allowFonts = bb.get() == 1;
        this.allowBBcode = bb.get() == 1;
        this.equalizeLineHeights = bb.get() == 1;
        this.showAuthor = bb.get() == 1;
        this.showTimestamp = bb.get() == 1;
        this.showChatTitle = bb.get() == 1;
        this.range = bb.getFloat();
        if (bb.get() == 1) {
            this.zombieAttractionRange = bb.getFloat();
        } else {
            this.zombieAttractionRange = this.range;
        }

        this.fontSize = ChatSettings.FontSize.Medium;
    }

    public boolean isUnique() {
        return this.unique;
    }

    public void setUnique(boolean _unique) {
        this.unique = _unique;
    }

    public Color getFontColor() {
        return this.fontColor;
    }

    public void setFontColor(Color _fontColor) {
        this.fontColor = _fontColor;
    }

    public void setFontColor(float r, float g, float b, float a) {
        this.fontColor = new Color(r, g, b, a);
    }

    public UIFont getFont() {
        return this.font;
    }

    public void setFont(UIFont _font) {
        this.font = _font;
    }

    public String getFontSize() {
        return this.fontSize.toString().toLowerCase();
    }

    public void setFontSize(String _fontSize) {
        switch (_fontSize) {
            case "small":
            case "Small":
                this.fontSize = ChatSettings.FontSize.Small;
                break;
            case "medium":
            case "Medium":
                this.fontSize = ChatSettings.FontSize.Medium;
                break;
            case "large":
            case "Large":
                this.fontSize = ChatSettings.FontSize.Large;
                break;
            default:
                this.fontSize = ChatSettings.FontSize.NotDefine;
        }
    }

    public boolean isBold() {
        return this.bold;
    }

    public void setBold(boolean _bold) {
        this.bold = _bold;
    }

    public boolean isShowAuthor() {
        return this.showAuthor;
    }

    public void setShowAuthor(boolean _showAuthor) {
        this.showAuthor = _showAuthor;
    }

    public boolean isShowTimestamp() {
        return this.showTimestamp;
    }

    public void setShowTimestamp(boolean _showTimestamp) {
        this.showTimestamp = _showTimestamp;
    }

    public boolean isShowChatTitle() {
        return this.showChatTitle;
    }

    public void setShowChatTitle(boolean _showChatTitle) {
        this.showChatTitle = _showChatTitle;
    }

    public boolean isAllowImages() {
        return this.allowImages;
    }

    public void setAllowImages(boolean _allowImages) {
        this.allowImages = _allowImages;
    }

    public boolean isAllowChatIcons() {
        return this.allowChatIcons;
    }

    public void setAllowChatIcons(boolean _allowChatIcons) {
        this.allowChatIcons = _allowChatIcons;
    }

    public boolean isAllowColors() {
        return this.allowColors;
    }

    public void setAllowColors(boolean _allowColors) {
        this.allowColors = _allowColors;
    }

    public boolean isAllowFonts() {
        return this.allowFonts;
    }

    public void setAllowFonts(boolean _allowFonts) {
        this.allowFonts = _allowFonts;
    }

    public boolean isAllowBBcode() {
        return this.allowBBcode;
    }

    public void setAllowBBcode(boolean _allowBBcode) {
        this.allowBBcode = _allowBBcode;
    }

    public boolean isEqualizeLineHeights() {
        return this.equalizeLineHeights;
    }

    public void setEqualizeLineHeights(boolean _equalizeLineHeights) {
        this.equalizeLineHeights = _equalizeLineHeights;
    }

    public float getRange() {
        return this.range;
    }

    public void setRange(float _range) {
        this.range = _range;
    }

    public float getZombieAttractionRange() {
        return this.zombieAttractionRange == -1.0F ? this.range : this.zombieAttractionRange;
    }

    public void setZombieAttractionRange(float _zombieAttractionRange) {
        this.zombieAttractionRange = _zombieAttractionRange;
    }

    public boolean isUseOnlyActiveTab() {
        return this.useOnlyActiveTab;
    }

    public void setUseOnlyActiveTab(boolean _useOnlyActiveTab) {
        this.useOnlyActiveTab = _useOnlyActiveTab;
    }

    public void pack(ByteBufferWriter bb) {
        bb.putBoolean(this.unique);
        bb.putFloat(this.fontColor.r);
        bb.putFloat(this.fontColor.g);
        bb.putFloat(this.fontColor.b);
        bb.putFloat(this.fontColor.a);
        bb.putUTF(this.font.toString());
        bb.putBoolean(this.bold);
        bb.putBoolean(this.allowImages);
        bb.putBoolean(this.allowChatIcons);
        bb.putBoolean(this.allowColors);
        bb.putBoolean(this.allowFonts);
        bb.putBoolean(this.allowBBcode);
        bb.putBoolean(this.equalizeLineHeights);
        bb.putBoolean(this.showAuthor);
        bb.putBoolean(this.showTimestamp);
        bb.putBoolean(this.showChatTitle);
        bb.putFloat(this.range);
        bb.putBoolean(this.range != this.zombieAttractionRange);
        if (this.range != this.zombieAttractionRange) {
            bb.putFloat(this.zombieAttractionRange);
        }
    }

    public static enum FontSize {
        NotDefine,
        Small,
        Medium,
        Large;
    }
}
