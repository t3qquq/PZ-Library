// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.sixlegs.png;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class PngImage implements Transparency {
    private static final PngConfig DEFAULT_CONFIG = new PngConfig.Builder().build();
    private final PngConfig config;
    private final Map props = new HashMap();
    private boolean read = false;

    public PngImage() {
        this(DEFAULT_CONFIG);
    }

    public PngImage(PngConfig pngConfig) {
        this.config = pngConfig;
    }

    public PngConfig getConfig() {
        return this.config;
    }

    public BufferedImage read(File file) throws IOException {
        return this.read(new BufferedInputStream(new FileInputStream(file)), true);
    }

    public BufferedImage read(InputStream inputStream, boolean boolean0) throws IOException {
        if (inputStream == null) {
            throw new NullPointerException("InputStream is null");
        } else {
            this.read = true;
            this.props.clear();
            int int0 = this.config.getReadLimit();
            BufferedImage bufferedImage = null;
            StateMachine stateMachine = new StateMachine(this);

            try {
                PngInputStream pngInputStream = new PngInputStream(inputStream);
                HashSet hashSet = new HashSet();

                while (stateMachine.getState() != 6) {
                    int int1 = pngInputStream.startChunk();
                    stateMachine.nextState(int1);

                    try {
                        if (int1 == 1229209940) {
                            switch (int0) {
                                case 2:
                                    return null;
                                case 3:
                                    break;
                                default:
                                    ImageDataInputStream imageDataInputStream = new ImageDataInputStream(pngInputStream, stateMachine);
                                    bufferedImage = this.createImage(imageDataInputStream, new Dimension(this.getWidth(), this.getHeight()));

                                    while ((int1 = stateMachine.getType()) == 1229209940) {
                                        skipFully(imageDataInputStream, pngInputStream.getRemaining());
                                    }
                            }
                        }

                        if (!this.isMultipleOK(int1) && !hashSet.add(Integers.valueOf(int1))) {
                            throw new PngException("Multiple " + PngConstants.getChunkName(int1) + " chunks are not allowed", !PngConstants.isAncillary(int1));
                        }

                        try {
                            this.readChunk(int1, pngInputStream, pngInputStream.getOffset(), pngInputStream.getRemaining());
                        } catch (PngException pngException0) {
                            throw pngException0;
                        } catch (IOException iOException) {
                            throw new PngException("Malformed " + PngConstants.getChunkName(int1) + " chunk", iOException, !PngConstants.isAncillary(int1));
                        }

                        skipFully(pngInputStream, pngInputStream.getRemaining());
                        if (int1 == 1229472850 && int0 == 1) {
                            return null;
                        }
                    } catch (PngException pngException1) {
                        if (pngException1.isFatal()) {
                            throw pngException1;
                        }

                        skipFully(pngInputStream, pngInputStream.getRemaining());
                        this.handleWarning(pngException1);
                    }

                    pngInputStream.endChunk(int1);
                }

                return bufferedImage;
            } finally {
                if (boolean0) {
                    inputStream.close();
                }
            }
        }
    }

    protected BufferedImage createImage(InputStream inputStream, Dimension dimension) throws IOException {
        return ImageFactory.createImage(this, inputStream, dimension);
    }

    protected boolean handlePass(BufferedImage var1, int var2) {
        return true;
    }

    protected boolean handleProgress(BufferedImage var1, float var2) {
        return true;
    }

    protected void handleWarning(PngException pngException) throws PngException {
        if (this.config.getWarningsFatal()) {
            throw pngException;
        }
    }

    public int getWidth() {
        return this.getInt("width");
    }

    public int getHeight() {
        return this.getInt("height");
    }

    public int getBitDepth() {
        return this.getInt("bit_depth");
    }

    public boolean isInterlaced() {
        return this.getInt("interlace") != 0;
    }

    public int getColorType() {
        return this.getInt("color_type");
    }

    @Override
    public int getTransparency() {
        int int0 = this.getColorType();
        return int0 != 6 && int0 != 4 && !this.props.containsKey("transparency") && !this.props.containsKey("palette_alpha") ? 1 : 3;
    }

    public int getSamples() {
        switch (this.getColorType()) {
            case 2:
                return 3;
            case 3:
            case 5:
            default:
                return 1;
            case 4:
                return 2;
            case 6:
                return 4;
        }
    }

    public float getGamma() {
        this.assertRead();
        return this.props.containsKey("gamma") ? ((Number)this.getProperty("gamma", Number.class, true)).floatValue() : this.config.getDefaultGamma();
    }

    public short[] getGammaTable() {
        this.assertRead();
        return createGammaTable(this.getGamma(), this.config.getDisplayExponent(), this.getBitDepth() == 16 && !this.config.getReduce16());
    }

    static short[] createGammaTable(float float1, float float0, boolean boolean0) {
        int int0 = 1 << (boolean0 ? 16 : 8);
        short[] shorts = new short[int0];
        double double0 = 1.0 / ((double)float1 * float0);

        for (int int1 = 0; int1 < int0; int1++) {
            shorts[int1] = (short)(Math.pow((double)int1 / (int0 - 1), double0) * (int0 - 1));
        }

        return shorts;
    }

    public Color getBackground() {
        int[] ints = (int[])this.getProperty("background_rgb", int[].class, false);
        if (ints == null) {
            return null;
        } else {
            switch (this.getColorType()) {
                case 0:
                case 4:
                    int int1 = ints[0] * 255 / ((1 << this.getBitDepth()) - 1);
                    return new Color(int1, int1, int1);
                case 1:
                case 2:
                default:
                    if (this.getBitDepth() == 16) {
                        return new Color(ints[0] >> 8, ints[1] >> 8, ints[2] >> 8);
                    }

                    return new Color(ints[0], ints[1], ints[2]);
                case 3:
                    byte[] bytes = (byte[])this.getProperty("palette", byte[].class, true);
                    int int0 = ints[0] * 3;
                    return new Color(255 & bytes[int0 + 0], 255 & bytes[int0 + 1], 255 & bytes[int0 + 2]);
            }
        }
    }

    public Object getProperty(String string) {
        this.assertRead();
        return this.props.get(string);
    }

    Object getProperty(String string, Class clazz, boolean boolean0) {
        this.assertRead();
        Object object = this.props.get(string);
        if (object == null) {
            if (boolean0) {
                throw new IllegalStateException("Image is missing property \"" + string + "\"");
            }
        } else if (!clazz.isAssignableFrom(object.getClass())) {
            throw new IllegalStateException("Property \"" + string + "\" has type " + object.getClass().getName() + ", expected " + clazz.getName());
        }

        return object;
    }

    private int getInt(String string) {
        return ((Number)this.getProperty(string, Number.class, true)).intValue();
    }

    public Map getProperties() {
        return this.props;
    }

    public TextChunk getTextChunk(String string) {
        List list = (List)this.getProperty("text_chunks", List.class, false);
        if (string != null && list != null) {
            for (TextChunk textChunk : list) {
                if (textChunk.getKeyword().equals(string)) {
                    return textChunk;
                }
            }
        }

        return null;
    }

    protected void readChunk(int int0, DataInput dataInput, long var3, int int1) throws IOException {
        if (int0 != 1229209940) {
            if (this.config.getReadLimit() == 4 && PngConstants.isAncillary(int0)) {
                switch (int0) {
                    case 1732332865:
                    case 1951551059:
                        break;
                    default:
                        return;
                }
            }

            RegisteredChunks.read(int0, dataInput, int1, this);
        }
    }

    protected boolean isMultipleOK(int int0) {
        switch (int0) {
            case 1229209940:
            case 1767135348:
            case 1934642260:
            case 1950701684:
            case 2052348020:
                return true;
            default:
                return false;
        }
    }

    private void assertRead() {
        if (!this.read) {
            throw new IllegalStateException("Image has not been read");
        }
    }

    private static void skipFully(InputStream inputStream, long long0) throws IOException {
        while (long0 > 0L) {
            long long1 = inputStream.skip(long0);
            if (long1 == 0L) {
                if (inputStream.read() == -1) {
                    throw new EOFException();
                }

                long0--;
            } else {
                long0 -= long1;
            }
        }
    }
}
