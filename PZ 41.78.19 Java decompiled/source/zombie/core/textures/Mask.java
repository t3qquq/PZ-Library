// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import zombie.core.opengl.RenderThread;
import zombie.core.utils.BooleanGrid;
import zombie.core.utils.WrappedBuffer;
import zombie.interfaces.ITexture;

public final class Mask implements Serializable, Cloneable {
    private static final long serialVersionUID = -5679205580926696806L;
    private boolean full;
    private int height;
    BooleanGrid mask;
    private int width;

    protected Mask() {
    }

    /**
     * Creates a new instance of Mask.
     * 
     * @param _width width of mask
     * @param _height height of mask
     */
    public Mask(int _width, int _height) {
        this.width = _width;
        this.height = _height;
        this.mask = new BooleanGrid(_width, _height);
        this.full();
    }

    /**
     * Creates a new instance of Mask from a texture
     * 
     * @param from
     * @param texture the source texture
     * @param x
     * @param y
     * @param _width
     * @param _height
     */
    public Mask(Texture from, Texture texture, int x, int y, int _width, int _height) {
        if (from.getMask() != null) {
            _width = texture.getWidth();
            _height = texture.getHeight();
            texture.setMask(this);
            this.mask = new BooleanGrid(_width, _height);

            for (int int0 = x; int0 < x + _width; int0++) {
                for (int int1 = y; int1 < y + _height; int1++) {
                    this.mask.setValue(int0 - x, int1 - y, from.getMask().mask.getValue(int0, int1));
                }
            }
        }
    }

    public Mask(Mask other, int x, int y, int _width, int _height) {
        this.width = _width;
        this.height = _height;
        this.mask = new BooleanGrid(_width, _height);

        for (int int0 = 0; int0 < _width; int0++) {
            for (int int1 = 0; int1 < _height; int1++) {
                this.mask.setValue(int0, int1, other.mask.getValue(x + int0, y + int1));
            }
        }
    }

    public Mask(boolean[] booleans, int int4, int var3, int int6, int int5, int int0, int int1) {
        this.width = int0;
        this.height = int1;
        this.mask = new BooleanGrid(int0, int1);

        for (int int2 = 0; int2 < int0; int2++) {
            for (int int3 = 0; int3 < int1; int3++) {
                this.mask.setValue(int2, int3, booleans[int6 + int2 + (int5 + int3) * int4]);
            }
        }
    }

    public Mask(BooleanGrid mask1, int maskW, int maskH, int x, int y, int _width, int _height) {
        this.width = _width;
        this.height = _height;
        this.mask = new BooleanGrid(_width, _height);

        for (int int0 = 0; int0 < _width; int0++) {
            for (int int1 = 0; int1 < _height; int1++) {
                this.mask.setValue(int0, int1, mask1.getValue(x + int0, y + int1));
            }
        }
    }

    protected Mask(Texture texture, WrappedBuffer wrappedBuffer) {
        this.width = texture.getWidth();
        this.height = texture.getHeight();
        int int0 = texture.getWidthHW();
        int int1 = texture.getHeightHW();
        int int2 = (int)(texture.getXStart() * int0);
        int int3 = (int)(texture.getXEnd() * int0);
        int int4 = (int)(texture.getYStart() * int1);
        int int5 = (int)(texture.getYEnd() * int1);
        this.mask = new BooleanGrid(this.width, this.height);
        texture.setMask(this);
        ByteBuffer byteBuffer = wrappedBuffer.getBuffer();
        byteBuffer.rewind();

        for (int int6 = 0; int6 < texture.getHeightHW(); int6++) {
            for (int int7 = 0; int7 < texture.getWidthHW(); int7++) {
                byteBuffer.get();
                byteBuffer.get();
                byteBuffer.get();
                byte byte0 = byteBuffer.get();
                if (int7 >= int2 && int7 < int3 && int6 >= int4 && int6 < int5) {
                    if (byte0 == 0) {
                        this.mask.setValue(int7 - int2, int6 - int4, false);
                        this.full = false;
                    } else {
                        if (byte0 < 127) {
                            this.mask.setValue(int7 - int2, int6 - int4, true);
                        }

                        this.mask.setValue(int7 - int2, int6 - int4, true);
                    }
                }

                if (int6 >= int5) {
                    break;
                }
            }
        }

        wrappedBuffer.dispose();
    }

    public Mask(ITexture iTexture, boolean[] booleans) {
        this.width = iTexture.getWidth();
        this.height = iTexture.getHeight();
        int int0 = iTexture.getWidthHW();
        int int1 = (int)(iTexture.getXStart() * int0);
        int int2 = (int)(iTexture.getXEnd() * int0);
        int int3 = (int)(iTexture.getYStart() * (int0 = iTexture.getHeightHW()));
        int int4 = (int)(iTexture.getYEnd() * int0);
        iTexture.setMask(this);
        this.mask = new BooleanGrid(this.width, this.height);

        for (int int5 = 0; int5 < iTexture.getHeight(); int5++) {
            for (int int6 = 0; int6 < iTexture.getWidth(); int6++) {
                this.mask.setValue(int6, int5, booleans[int5 * iTexture.getWidth() + int6]);
            }
        }
    }

    public Mask(ITexture texture, BooleanGrid _mask) {
        this.width = texture.getWidth();
        this.height = texture.getHeight();
        texture.setMask(this);
        this.mask = new BooleanGrid(this.width, this.height);

        for (int int0 = 0; int0 < texture.getHeight(); int0++) {
            for (int int1 = 0; int1 < texture.getWidth(); int1++) {
                this.mask.setValue(int1, int0, _mask.getValue(int1, int0));
            }
        }
    }

    public Mask(ITexture texture) {
        this.width = texture.getWidth();
        this.height = texture.getHeight();
        int int0 = texture.getWidthHW();
        int int1 = (int)(texture.getXStart() * int0);
        int int2 = (int)(texture.getXEnd() * int0);
        int int3 = (int)(texture.getYStart() * (int0 = texture.getHeightHW()));
        int int4 = (int)(texture.getYEnd() * int0);
        texture.setMask(this);
        this.mask = new BooleanGrid(this.width, this.height);
        RenderThread.invokeOnRenderContext(() -> {
            WrappedBuffer wrappedBuffer = texture.getData();
            ByteBuffer byteBuffer = wrappedBuffer.getBuffer();
            byteBuffer.rewind();

            for (int int0x = 0; int0x < texture.getHeightHW(); int0x++) {
                for (int int1x = 0; int1x < texture.getWidthHW(); int1x++) {
                    byteBuffer.get();
                    byteBuffer.get();
                    byteBuffer.get();
                    byte byte0 = byteBuffer.get();
                    if (int1x >= int1 && int1x < int2 && int0x >= int3 && int0x < int4) {
                        if (byte0 == 0) {
                            this.mask.setValue(int1x - int1, int0x - int3, false);
                            this.full = false;
                        } else {
                            if (byte0 < 127) {
                                this.mask.setValue(int1x - int1, int0x - int3, true);
                            } else {
                                boolean boolean0 = false;
                            }

                            this.mask.setValue(int1x - int1, int0x - int3, true);
                        }
                    }

                    if (int0x >= int4) {
                        break;
                    }
                }
            }

            wrappedBuffer.dispose();
        });
    }

    public Mask(Mask obj) {
        this.width = obj.width;
        this.height = obj.height;
        this.full = obj.full;

        try {
            this.mask = obj.mask.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            cloneNotSupportedException.printStackTrace(System.err);
        }
    }

    @Override
    public Object clone() {
        return new Mask(this);
    }

    /**
     * creates a full-rectangular mask
     */
    public void full() {
        this.mask.fill();
        this.full = true;
    }

    /**
     * changes the x,y value of the mask
     * 
     * @param x coordinate
     * @param y coordinate
     * @param val new value
     */
    public void set(int x, int y, boolean val) {
        this.mask.setValue(x, y, val);
        if (!val && this.full) {
            this.full = false;
        }
    }

    public boolean get(int x, int y) {
        return this.full ? true : this.mask.getValue(x, y);
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.width = objectInputStream.readInt();
        this.height = objectInputStream.readInt();
        this.full = objectInputStream.readBoolean();
        if (objectInputStream.readBoolean()) {
            this.mask = (BooleanGrid)objectInputStream.readObject();
        }
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(this.width);
        objectOutputStream.writeInt(this.height);
        objectOutputStream.writeBoolean(this.full);
        if (this.mask != null) {
            objectOutputStream.writeBoolean(true);
            objectOutputStream.writeObject(this.mask);
        } else {
            objectOutputStream.writeBoolean(false);
        }
    }

    public void save(String name) {
    }
}
