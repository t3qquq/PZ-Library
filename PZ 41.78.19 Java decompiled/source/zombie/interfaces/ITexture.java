// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.interfaces;

import java.nio.ByteBuffer;
import zombie.core.textures.Mask;
import zombie.core.utils.WrappedBuffer;

public interface ITexture extends IDestroyable, IMaskerable {
    /**
     * bind the current texture in the VRAM
     */
    void bind();

    /**
     * bind the current texture object in the specified texture unit
     * 
     * @param unit the texture unit in witch the current TextureObject will be binded
     */
    void bind(int unit);

    /**
     * returns the texture's pixel in a ByteBuffer   EXAMPLE:
     */
    WrappedBuffer getData();

    /**
     * returns the height of image
     * @return the height of image
     */
    int getHeight();

    /**
     * return the height hardware of image
     */
    int getHeightHW();

    /**
     * returns the ID of image in the Vram
     * @return the ID of image in the Vram
     */
    int getID();

    /**
     * returns the width of image
     * @return the width of image
     */
    int getWidth();

    /**
     * return the width Hardware of image
     */
    int getWidthHW();

    /**
     * returns the end X-coordinate
     * @return the end X-coordinate
     */
    float getXEnd();

    /**
     * returns the start X-coordinate
     * @return the start X-coordinate
     */
    float getXStart();

    /**
     * returns the end Y-coordinate
     * @return the end Y-coordinate
     */
    float getYEnd();

    /**
     * returns the start Y-coordinate
     * @return the start Y-coordinate
     */
    float getYStart();

    /**
     * indicates if the texture is solid or not.
     * @return if the texture is solid or not.
     */
    boolean isSolid();

    /**
     * sets transparent each pixel that it's equal to the red, green blue value specified
     * 
     * @param red color used in the test
     * @param green color used in the test
     * @param blue color used in the test
     */
    void makeTransp(int red, int green, int blue);

    /**
     * sets the specified alpha for each pixel that it's equal to the red, green blue value specified
     * 
     * @param red color used in the test
     * @param green color used in the test
     * @param blue color used in the test
     * @param alpha the alpha color that will be setted to the pixel that pass the test
     */
    void setAlphaForeach(int red, int green, int blue, int alpha);

    /**
     * sets the texture's pixel from a ByteBuffer   EXAMPLE:
     * 
     * @param data texture's pixel data
     */
    void setData(ByteBuffer data);

    /**
     * Pixel collision mask of texture
     */
    void setMask(Mask mask);

    /**
     * sets the region of the image
     * 
     * @param x xstart position
     * @param y ystart position
     * @param width width of the region
     * @param height height of the region
     */
    void setRegion(int x, int y, int width, int height);
}
