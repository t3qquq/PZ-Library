// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.fonts;

import zombie.core.Color;

public interface Font {
    /**
     * Draw a string to the screen
     * 
     * @param x The x location at which to draw the string
     * @param y The y location at which to draw the string
     * @param text The text to be displayed
     */
    void drawString(float x, float y, String text);

    /**
     * Draw a string to the screen
     * 
     * @param x The x location at which to draw the string
     * @param y The y location at which to draw the string
     * @param text The text to be displayed
     * @param col The colour to draw with
     */
    void drawString(float x, float y, String text, Color col);

    /**
     * Draw part of a string to the screen. Note that this will  still position the text as though it's part of the bigger string.
     * 
     * @param x The x location at which to draw the string
     * @param y The y location at which to draw the string
     * @param text The text to be displayed
     * @param col The colour to draw with
     * @param startIndex The index of the first character to draw
     * @param endIndex The index of the last character from the string to draw
     */
    void drawString(float x, float y, String text, Color col, int startIndex, int endIndex);

    /**
     * get the height of the given string
     * 
     * @param str The string to obtain the rendered with of
     * @return The width of the given string
     */
    int getHeight(String str);

    /**
     * get the width of the given string
     * 
     * @param str The string to obtain the rendered with of
     * @return The width of the given string
     */
    int getWidth(String str);

    int getWidth(String str, boolean xAdvance);

    int getWidth(String str, int startIndex, int endIndex);

    int getWidth(String str, int startIndex, int endIndex, boolean xAdvance);

    /**
     * get the maximum height of any line drawn by this font
     * @return The maxium height of any line drawn by this font
     */
    int getLineHeight();
}
