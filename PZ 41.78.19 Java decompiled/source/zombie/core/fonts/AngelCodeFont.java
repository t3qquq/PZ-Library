// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.fonts;

import gnu.trove.list.array.TShortArrayList;
import gnu.trove.map.hash.TShortObjectHashMap;
import gnu.trove.procedure.TShortObjectProcedure;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import org.lwjgl.opengl.GL11;
import zombie.ZomboidFileSystem;
import zombie.asset.Asset;
import zombie.asset.AssetStateObserver;
import zombie.core.Color;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.util.StringUtils;

/**
 * A font implementation that will parse BMFont format font files. The font files can be output  by Hiero, which is included with Slick, and also the AngelCode font tool available at:
 */
public final class AngelCodeFont implements Font, AssetStateObserver {
    private static final int DISPLAY_LIST_CACHE_SIZE = 200;
    private static final int MAX_CHAR = 255;
    private int baseDisplayListID = -1;
    /**
     * The characters building up the font
     */
    public AngelCodeFont.CharDef[] chars;
    private boolean displayListCaching = false;
    private AngelCodeFont.DisplayList eldestDisplayList;
    private int eldestDisplayListID;
    private final LinkedHashMap displayLists = new LinkedHashMap(200, 1.0F, true) {
        @Override
        protected boolean removeEldestEntry(Entry entry) {
            AngelCodeFont.this.eldestDisplayList = (AngelCodeFont.DisplayList)entry.getValue();
            AngelCodeFont.this.eldestDisplayListID = AngelCodeFont.this.eldestDisplayList.id;
            return false;
        }
    };
    private Texture fontImage;
    private int lineHeight;
    private HashMap<Short, Texture> pages = new HashMap<>();
    private File fntFile;
    public static int xoff = 0;
    public static int yoff = 0;
    public static Color curCol = null;
    public static float curR = 0.0F;
    public static float curG = 0.0F;
    public static float curB = 0.0F;
    public static float curA = 0.0F;
    private static float s_scale = 0.0F;
    private static char[] data = new char[256];

    /**
     * Create a new font based on a font definition from AngelCode's tool and  the font image generated from the tool.
     * 
     * @param _fntFile The location of the font defnition file
     * @param image The image to use for the font
     */
    public AngelCodeFont(String _fntFile, Texture image) throws FileNotFoundException {
        this.fontImage = image;
        String string = _fntFile;
        FileInputStream fileInputStream = new FileInputStream(new File(_fntFile));
        if (_fntFile.startsWith("/")) {
            string = _fntFile.substring(1);
        }

        int int0;
        while ((int0 = string.indexOf("\\")) != -1) {
            string = string.substring(0, int0) + "/" + string.substring(int0 + 1);
        }

        this.parseFnt(fileInputStream);
    }

    /**
     * Create a new font based on a font definition from AngelCode's tool and  the font image generated from the tool.
     * 
     * @param _fntFile The location of the font defnition file
     * @param imgFile The location of the font image
     */
    public AngelCodeFont(String _fntFile, String imgFile) throws FileNotFoundException {
        if (!StringUtils.isNullOrWhitespace(imgFile)) {
            int int0 = 0;
            int0 |= TextureID.bUseCompression ? 4 : 0;
            this.fontImage = Texture.getSharedTexture(imgFile, int0);
            if (this.fontImage != null && !this.fontImage.isReady()) {
                this.fontImage.getObserverCb().add(this);
            }
        }

        String string = _fntFile;
        Object object = null;
        if (_fntFile.startsWith("/")) {
            string = _fntFile.substring(1);
        }

        int int1;
        while ((int1 = string.indexOf("\\")) != -1) {
            string = string.substring(0, int1) + "/" + string.substring(int1 + 1);
        }

        this.fntFile = new File(ZomboidFileSystem.instance.getString(string));
        object = new FileInputStream(ZomboidFileSystem.instance.getString(string));
        this.parseFnt((InputStream)object);
    }

    /**
     * Description copied from interface: Font
     * 
     * @param x The x location at which to draw the string
     * @param y The y location at which to draw the string
     * @param text The text to be displayed
     */
    @Override
    public void drawString(float x, float y, String text) {
        this.drawString(x, y, text, Color.white);
    }

    /**
     * Description copied from interface: Font
     * 
     * @param x The x location at which to draw the string
     * @param y The y location at which to draw the string
     * @param text The text to be displayed
     * @param col The colour to draw with
     */
    @Override
    public void drawString(float x, float y, String text, Color col) {
        this.drawString(x, y, text, col, 0, text.length() - 1);
    }

    public void drawString(float x, float y, String text, float r, float g, float b, float a) {
        this.drawString(x, y, text, r, g, b, a, 0, text.length() - 1);
    }

    public void drawString(float x, float y, float scale, String text, float r, float g, float b, float a) {
        this.drawString(x, y, scale, text, r, g, b, a, 0, text.length() - 1);
    }

    /**
     * Description copied from interface: Font
     * 
     * @param x The x location at which to draw the string
     * @param y The y location at which to draw the string
     * @param text The text to be displayed
     * @param col The colour to draw with
     * @param startIndex The index of the first character to draw
     * @param endIndex The index of the last character from the string to draw
     */
    @Override
    public void drawString(float x, float y, String text, Color col, int startIndex, int endIndex) {
        xoff = (int)x;
        yoff = (int)y;
        curR = col.r;
        curG = col.g;
        curB = col.b;
        curA = col.a;
        s_scale = 0.0F;
        Texture.lr = col.r;
        Texture.lg = col.g;
        Texture.lb = col.b;
        Texture.la = col.a;
        if (this.displayListCaching && startIndex == 0 && endIndex == text.length() - 1) {
            AngelCodeFont.DisplayList displayList = (AngelCodeFont.DisplayList)this.displayLists.get(text);
            if (displayList != null) {
                GL11.glCallList(displayList.id);
            } else {
                displayList = new AngelCodeFont.DisplayList();
                displayList.text = text;
                int int0 = this.displayLists.size();
                if (int0 < 200) {
                    displayList.id = this.baseDisplayListID + int0;
                } else {
                    displayList.id = this.eldestDisplayListID;
                    this.displayLists.remove(this.eldestDisplayList.text);
                }

                this.displayLists.put(text, displayList);
                GL11.glNewList(displayList.id, 4865);
                this.render(text, startIndex, endIndex);
                GL11.glEndList();
            }
        } else {
            this.render(text, startIndex, endIndex);
        }
    }

    public void drawString(float x, float y, String text, float r, float g, float b, float a, int startIndex, int endIndex) {
        this.drawString(x, y, 0.0F, text, r, g, b, a, startIndex, endIndex);
    }

    public void drawString(float x, float y, float scale, String text, float r, float g, float b, float a, int startIndex, int endIndex) {
        xoff = (int)x;
        yoff = (int)y;
        curR = r;
        curG = g;
        curB = b;
        curA = a;
        s_scale = scale;
        Texture.lr = r;
        Texture.lg = g;
        Texture.lb = b;
        Texture.la = a;
        if (this.displayListCaching && startIndex == 0 && endIndex == text.length() - 1) {
            AngelCodeFont.DisplayList displayList = (AngelCodeFont.DisplayList)this.displayLists.get(text);
            if (displayList != null) {
                GL11.glCallList(displayList.id);
            } else {
                displayList = new AngelCodeFont.DisplayList();
                displayList.text = text;
                int int0 = this.displayLists.size();
                if (int0 < 200) {
                    displayList.id = this.baseDisplayListID + int0;
                } else {
                    displayList.id = this.eldestDisplayListID;
                    this.displayLists.remove(this.eldestDisplayList.text);
                }

                this.displayLists.put(text, displayList);
                GL11.glNewList(displayList.id, 4865);
                this.render(text, startIndex, endIndex);
                GL11.glEndList();
            }
        } else {
            this.render(text, startIndex, endIndex);
        }
    }

    /**
     * Description copied from interface: Font
     * 
     * @param text The string to obtain the rendered with of
     * @return The width of the given string
     */
    @Override
    public int getHeight(String text) {
        AngelCodeFont.DisplayList displayList = null;
        if (this.displayListCaching) {
            displayList = (AngelCodeFont.DisplayList)this.displayLists.get(text);
            if (displayList != null && displayList.height != null) {
                return displayList.height.intValue();
            }
        }

        int int0 = 1;
        int int1 = 0;

        for (int int2 = 0; int2 < text.length(); int2++) {
            char char0 = text.charAt(int2);
            if (char0 == '\n') {
                int0++;
                int1 = 0;
            } else if (char0 != ' ' && char0 < this.chars.length) {
                AngelCodeFont.CharDef charDef = this.chars[char0];
                if (charDef != null) {
                    int1 = Math.max(charDef.height + charDef.yoffset, int1);
                }
            }
        }

        int1 = int0 * this.getLineHeight();
        if (displayList != null) {
            displayList.height = new Short((short)int1);
        }

        return int1;
    }

    /**
     * Description copied from interface: Font
     * @return The maxium height of any line drawn by this font
     */
    @Override
    public int getLineHeight() {
        return this.lineHeight;
    }

    /**
     * Description copied from interface: Font
     * 
     * @param text The string to obtain the rendered with of
     * @return The width of the given string
     */
    @Override
    public int getWidth(String text) {
        return this.getWidth(text, 0, text.length() - 1, false);
    }

    @Override
    public int getWidth(String text, boolean xAdvance) {
        return this.getWidth(text, 0, text.length() - 1, xAdvance);
    }

    @Override
    public int getWidth(String text, int start, int __end__) {
        return this.getWidth(text, start, __end__, false);
    }

    @Override
    public int getWidth(String text, int start, int __end__, boolean xadvance) {
        AngelCodeFont.DisplayList displayList = null;
        if (this.displayListCaching && start == 0 && __end__ == text.length() - 1) {
            displayList = (AngelCodeFont.DisplayList)this.displayLists.get(text);
            if (displayList != null && displayList.width != null) {
                return displayList.width.intValue();
            }
        }

        int int0 = __end__ - start + 1;
        int int1 = 0;
        int int2 = 0;
        AngelCodeFont.CharDef charDef0 = null;

        for (int int3 = 0; int3 < int0; int3++) {
            char char0 = text.charAt(start + int3);
            if (char0 == '\n') {
                int2 = 0;
            } else if (char0 < this.chars.length) {
                AngelCodeFont.CharDef charDef1 = this.chars[char0];
                if (charDef1 != null) {
                    if (charDef0 != null) {
                        int2 += charDef0.getKerning(char0);
                    }

                    charDef0 = charDef1;
                    if (!xadvance && int3 >= int0 - 1) {
                        int2 += charDef1.width;
                    } else {
                        int2 += charDef1.xadvance;
                    }

                    int1 = Math.max(int1, int2);
                }
            }
        }

        if (displayList != null) {
            displayList.width = new Short((short)int1);
        }

        return int1;
    }

    /**
     * Returns the distance from the y drawing location to the top most pixel of the specified text.
     * 
     * @param text The text that is to be tested
     * @return The yoffset from the y draw location at which text will start
     */
    public int getYOffset(String text) {
        AngelCodeFont.DisplayList displayList = null;
        if (this.displayListCaching) {
            displayList = (AngelCodeFont.DisplayList)this.displayLists.get(text);
            if (displayList != null && displayList.yOffset != null) {
                return displayList.yOffset.intValue();
            }
        }

        int int0 = text.indexOf(10);
        if (int0 == -1) {
            int0 = text.length();
        }

        int int1 = 10000;

        for (int int2 = 0; int2 < int0; int2++) {
            char char0 = text.charAt(int2);
            AngelCodeFont.CharDef charDef = this.chars[char0];
            if (charDef != null) {
                int1 = Math.min(charDef.yoffset, int1);
            }
        }

        if (displayList != null) {
            displayList.yOffset = new Short((short)int1);
        }

        return int1;
    }

    private AngelCodeFont.CharDef parseChar(String string) {
        AngelCodeFont.CharDef charDef = new AngelCodeFont.CharDef();
        StringTokenizer stringTokenizer = new StringTokenizer(string, " =");
        stringTokenizer.nextToken();
        stringTokenizer.nextToken();
        charDef.id = Integer.parseInt(stringTokenizer.nextToken());
        if (charDef.id < 0) {
            return null;
        } else {
            if (charDef.id > 255) {
            }

            stringTokenizer.nextToken();
            charDef.x = Short.parseShort(stringTokenizer.nextToken());
            stringTokenizer.nextToken();
            charDef.y = Short.parseShort(stringTokenizer.nextToken());
            stringTokenizer.nextToken();
            charDef.width = Short.parseShort(stringTokenizer.nextToken());
            stringTokenizer.nextToken();
            charDef.height = Short.parseShort(stringTokenizer.nextToken());
            stringTokenizer.nextToken();
            charDef.xoffset = Short.parseShort(stringTokenizer.nextToken());
            stringTokenizer.nextToken();
            charDef.yoffset = Short.parseShort(stringTokenizer.nextToken());
            stringTokenizer.nextToken();
            charDef.xadvance = Short.parseShort(stringTokenizer.nextToken());
            stringTokenizer.nextToken();
            charDef.page = Short.parseShort(stringTokenizer.nextToken());
            Texture texture = this.fontImage;
            if (this.pages.containsKey(charDef.page)) {
                texture = this.pages.get(charDef.page);
            }

            if (texture != null && texture.isReady()) {
                charDef.init();
            }

            if (charDef.id != 32) {
                this.lineHeight = Math.max(charDef.height + charDef.yoffset, this.lineHeight);
            }

            return charDef;
        }
    }

    private void parseFnt(InputStream inputStream) {
        if (this.displayListCaching) {
            this.baseDisplayListID = GL11.glGenLists(200);
            if (this.baseDisplayListID == 0) {
                this.displayListCaching = false;
            }
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String string0 = bufferedReader.readLine();
            String string1 = bufferedReader.readLine();
            TShortObjectHashMap tShortObjectHashMap = new TShortObjectHashMap(64);
            ArrayList arrayList = new ArrayList(255);
            int int0 = 0;
            boolean boolean0 = false;

            while (!boolean0) {
                String string2 = bufferedReader.readLine();
                if (string2 == null) {
                    boolean0 = true;
                } else {
                    if (string2.startsWith("page")) {
                        StringTokenizer stringTokenizer0 = new StringTokenizer(string2, " =");
                        stringTokenizer0.nextToken();
                        stringTokenizer0.nextToken();
                        short short0 = Short.parseShort(stringTokenizer0.nextToken());
                        stringTokenizer0.nextToken();
                        String string3 = stringTokenizer0.nextToken().replace("\"", "");
                        string3 = this.fntFile.getParent() + File.separatorChar + string3;
                        string3 = string3.replace("\\", "/");
                        int int1 = 0;
                        int1 |= TextureID.bUseCompression ? 4 : 0;
                        Texture texture = Texture.getSharedTexture(string3, int1);
                        if (texture == null) {
                            System.out.println("AngelCodeFont failed to load page " + short0 + " texture " + string3);
                        } else {
                            this.pages.put(short0, texture);
                            if (!texture.isReady()) {
                                texture.getObserverCb().add(this);
                            }
                        }
                    }

                    if (!string2.startsWith("chars c") && string2.startsWith("char")) {
                        AngelCodeFont.CharDef charDef0 = this.parseChar(string2);
                        if (charDef0 != null) {
                            int0 = Math.max(int0, charDef0.id);
                            arrayList.add(charDef0);
                        }
                    }

                    if (!string2.startsWith("kernings c") && string2.startsWith("kerning")) {
                        StringTokenizer stringTokenizer1 = new StringTokenizer(string2, " =");
                        stringTokenizer1.nextToken();
                        stringTokenizer1.nextToken();
                        short short1 = Short.parseShort(stringTokenizer1.nextToken());
                        stringTokenizer1.nextToken();
                        int int2 = Integer.parseInt(stringTokenizer1.nextToken());
                        stringTokenizer1.nextToken();
                        int int3 = Integer.parseInt(stringTokenizer1.nextToken());
                        TShortArrayList tShortArrayList = (TShortArrayList)tShortObjectHashMap.get(short1);
                        if (tShortArrayList == null) {
                            tShortArrayList = new TShortArrayList();
                            tShortObjectHashMap.put(short1, tShortArrayList);
                        }

                        tShortArrayList.add((short)int2);
                        tShortArrayList.add((short)int3);
                    }
                }
            }

            this.chars = new AngelCodeFont.CharDef[int0 + 1];

            for (AngelCodeFont.CharDef charDef1 : arrayList) {
                this.chars[charDef1.id] = charDef1;
            }

            tShortObjectHashMap.forEachEntry(new TShortObjectProcedure<TShortArrayList>() {
                public boolean execute(short short0, TShortArrayList tShortArrayList) {
                    AngelCodeFont.CharDef charDef = AngelCodeFont.this.chars[short0];
                    charDef.kerningSecond = new short[tShortArrayList.size() / 2];
                    charDef.kerningAmount = new short[tShortArrayList.size() / 2];
                    int int0x = 0;

                    for (byte byte0 = 0; byte0 < tShortArrayList.size(); byte0 += 2) {
                        charDef.kerningSecond[int0x] = tShortArrayList.get(byte0);
                        charDef.kerningAmount[int0x] = tShortArrayList.get(byte0 + 1);
                        int0x++;
                    }

                    short[] shorts0 = Arrays.copyOf(charDef.kerningSecond, charDef.kerningSecond.length);
                    short[] shorts1 = Arrays.copyOf(charDef.kerningAmount, charDef.kerningAmount.length);
                    Arrays.sort(shorts0);

                    for (int int1 = 0; int1 < shorts0.length; int1++) {
                        for (int int2 = 0; int2 < charDef.kerningSecond.length; int2++) {
                            if (charDef.kerningSecond[int2] == shorts0[int1]) {
                                charDef.kerningAmount[int1] = shorts1[int2];
                                break;
                            }
                        }
                    }

                    charDef.kerningSecond = shorts0;
                    return true;
                }
            });
            bufferedReader.close();
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    private void render(String string, int int2, int int0) {
        int0++;
        int int1 = int0 - int2;
        float float0 = 0.0F;
        float float1 = 0.0F;
        AngelCodeFont.CharDef charDef0 = null;
        if (data.length < int1) {
            data = new char[(int1 + 128 - 1) / 128 * 128];
        }

        string.getChars(int2, int0, data, 0);

        for (int int3 = 0; int3 < int1; int3++) {
            char char0 = data[int3];
            if (char0 == '\n') {
                float0 = 0.0F;
                float1 += this.getLineHeight();
            } else if (char0 < this.chars.length) {
                AngelCodeFont.CharDef charDef1 = this.chars[char0];
                if (charDef1 != null) {
                    if (charDef0 != null) {
                        if (s_scale > 0.0F) {
                            float0 += charDef0.getKerning(char0) * s_scale;
                        } else {
                            float0 += charDef0.getKerning(char0);
                        }
                    }

                    charDef0 = charDef1;
                    charDef1.draw(float0, float1);
                    if (s_scale > 0.0F) {
                        float0 += charDef1.xadvance * s_scale;
                    } else {
                        float0 += charDef1.xadvance;
                    }
                }
            }
        }
    }

    @Override
    public void onStateChanged(Asset.State oldState, Asset.State newState, Asset asset) {
        if (asset == this.fontImage || this.pages.containsValue(asset)) {
            if (newState == Asset.State.READY) {
                for (AngelCodeFont.CharDef charDef : this.chars) {
                    if (charDef != null && charDef.image == null) {
                        Texture texture = this.fontImage;
                        if (this.pages.containsKey(charDef.page)) {
                            texture = this.pages.get(charDef.page);
                        }

                        if (asset == texture) {
                            charDef.init();
                        }
                    }
                }
            }
        }
    }

    public boolean isEmpty() {
        if (this.fontImage != null && this.fontImage.isEmpty()) {
            return true;
        } else {
            for (Texture texture : this.pages.values()) {
                if (texture.isEmpty()) {
                    return true;
                }
            }

            return false;
        }
    }

    public void destroy() {
        for (AngelCodeFont.CharDef charDef : this.chars) {
            if (charDef != null) {
                charDef.destroy();
            }
        }

        Arrays.fill(this.chars, null);
        this.pages.clear();
    }

    /**
     * The definition of a single character as defined in the AngelCode file  format
     */
    public class CharDef {
        /**
         * The display list index for this character
         */
        public short dlIndex;
        /**
         * The height of the character image
         */
        public short height;
        /**
         * The id of the character
         */
        public int id;
        /**
         * The image containing the character
         */
        public Texture image;
        /**
         * The kerning info for this character
         */
        public short[] kerningSecond;
        public short[] kerningAmount;
        /**
         * The width of the character image
         */
        public short width;
        /**
         * The x location on the sprite sheet
         */
        public short x;
        /**
         * The amount to move the current position after drawing the character
         */
        public short xadvance;
        /**
         * The amount the x position should be offset when drawing the image
         */
        public short xoffset;
        /**
         * The y location on the sprite sheet
         */
        public short y;
        /**
         * The amount the y position should be offset when drawing the image
         */
        public short yoffset;
        /**
         * The page number for fonts with multiple textures
         */
        public short page;

        /**
         * Draw this character embedded in a image draw
         * 
         * @param _x The x position at which to draw the text
         * @param _y The y position at which to draw the text
         */
        public void draw(float _x, float _y) {
            Texture texture = this.image;
            if (AngelCodeFont.s_scale > 0.0F) {
                SpriteRenderer.instance
                    .m_states
                    .getPopulatingActiveState()
                    .render(
                        texture,
                        _x + this.xoffset * AngelCodeFont.s_scale + AngelCodeFont.xoff,
                        _y + this.yoffset * AngelCodeFont.s_scale + AngelCodeFont.yoff,
                        this.width * AngelCodeFont.s_scale,
                        this.height * AngelCodeFont.s_scale,
                        AngelCodeFont.curR,
                        AngelCodeFont.curG,
                        AngelCodeFont.curB,
                        AngelCodeFont.curA,
                        null
                    );
            } else {
                SpriteRenderer.instance
                    .renderi(
                        texture,
                        (int)(_x + this.xoffset + AngelCodeFont.xoff),
                        (int)(_y + this.yoffset + AngelCodeFont.yoff),
                        this.width,
                        this.height,
                        AngelCodeFont.curR,
                        AngelCodeFont.curG,
                        AngelCodeFont.curB,
                        AngelCodeFont.curA,
                        null
                    );
            }
        }

        /**
         * get the kerning offset between this character and the specified character.
         * 
         * @param otherCodePoint The other code point
         * @return the kerning offset
         */
        public int getKerning(int otherCodePoint) {
            if (this.kerningSecond == null) {
                return 0;
            } else {
                int int0 = 0;
                int int1 = this.kerningSecond.length - 1;

                while (int0 <= int1) {
                    int int2 = int0 + int1 >>> 1;
                    if (this.kerningSecond[int2] < otherCodePoint) {
                        int0 = int2 + 1;
                    } else {
                        if (this.kerningSecond[int2] <= otherCodePoint) {
                            return this.kerningAmount[int2];
                        }

                        int1 = int2 - 1;
                    }
                }

                return 0;
            }
        }

        /**
         * Initialise the image by cutting the right section from the map  produced by the AngelCode tool.
         */
        public void init() {
            Texture texture = AngelCodeFont.this.fontImage;
            if (AngelCodeFont.this.pages.containsKey(this.page)) {
                texture = AngelCodeFont.this.pages.get(this.page);
            }

            this.image = new AngelCodeFont.CharDefTexture(texture.getTextureId(), texture.getName() + "_" + this.x + "_" + this.y);
            this.image
                .setRegion(
                    this.x + (int)(texture.xStart * texture.getWidthHW()), this.y + (int)(texture.yStart * texture.getHeightHW()), this.width, this.height
                );
        }

        public void destroy() {
            if (this.image != null && this.image.getTextureId() != null) {
                ((AngelCodeFont.CharDefTexture)this.image).releaseCharDef();
                this.image = null;
            }
        }

        @Override
        public String toString() {
            return "[CharDef id=" + this.id + " x=" + this.x + " y=" + this.y + "]";
        }
    }

    public static final class CharDefTexture extends Texture {
        public CharDefTexture(TextureID textureID, String string) {
            super(textureID, string);
        }

        public void releaseCharDef() {
            this.removeDependency(this.dataid);
        }
    }

    private static class DisplayList {
        Short height;
        int id;
        String text;
        Short width;
        Short yOffset;
    }
}
