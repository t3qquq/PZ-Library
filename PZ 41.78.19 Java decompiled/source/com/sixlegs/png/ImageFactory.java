// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.sixlegs.png;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

class ImageFactory {
    private static short[] GAMMA_TABLE_45455 = PngImage.createGammaTable(0.45455F, 2.2F, false);
    private static short[] GAMMA_TABLE_100000 = PngImage.createGammaTable(1.0F, 2.2F, false);

    public static BufferedImage createImage(PngImage pngImage, InputStream inputStream) throws IOException {
        return createImage(pngImage, inputStream, new Dimension(pngImage.getWidth(), pngImage.getHeight()));
    }

    public static BufferedImage createImage(PngImage pngImage, InputStream inputStream, Dimension dimension) throws IOException {
        PngConfig pngConfig = pngImage.getConfig();
        int int0 = dimension.width;
        int int1 = dimension.height;
        int int2 = pngImage.getBitDepth();
        int int3 = pngImage.getSamples();
        boolean boolean0 = pngImage.isInterlaced();
        boolean boolean1 = isIndexed(pngImage);
        boolean boolean2 = boolean1 && pngConfig.getConvertIndexed();
        short[] shorts = pngConfig.getGammaCorrect() ? getGammaTable(pngImage) : null;
        ColorModel colorModel = createColorModel(pngImage, shorts, boolean2);
        int int4 = int0;
        int int5 = int1;
        Rectangle rectangle = pngConfig.getSourceRegion();
        if (rectangle != null) {
            if (!new Rectangle(int0, int1).contains(rectangle)) {
                throw new IllegalStateException("Source region " + rectangle + " falls outside of " + int0 + "x" + int1 + " image");
            }

            int4 = rectangle.width;
            int5 = rectangle.height;
        }

        int int6 = pngConfig.getSourceXSubsampling();
        int int7 = pngConfig.getSourceYSubsampling();
        Object object0;
        if (int6 == 1 && int7 == 1) {
            object0 = new RasterDestination(colorModel.createCompatibleWritableRaster(int4, int5), int0);
        } else {
            int int8 = pngConfig.getSubsamplingXOffset();
            int int9 = pngConfig.getSubsamplingYOffset();
            int int10 = calcSubsamplingSize(int4, int6, int8, 'X');
            int int11 = calcSubsamplingSize(int5, int7, int9, 'Y');
            WritableRaster writableRaster = colorModel.createCompatibleWritableRaster(int10, int11);
            object0 = new SubsamplingDestination(writableRaster, int0, int6, int7, int8, int9);
        }

        if (rectangle != null) {
            object0 = new SourceRegionDestination((Destination)object0, rectangle);
        }

        BufferedImage bufferedImage = new BufferedImage(colorModel, ((Destination)object0).getRaster(), false, null);
        Object object1 = null;
        if (!boolean1) {
            int[] ints = (int[])pngImage.getProperty("transparency", int[].class, false);
            int int12 = int2 == 16 && pngConfig.getReduce16() ? 8 : 0;
            if (int12 != 0 || ints != null || shorts != null) {
                if (shorts == null) {
                    shorts = getIdentityTable(int2 - int12);
                }

                if (ints != null) {
                    object1 = new TransGammaPixelProcessor((Destination)object0, shorts, ints, int12);
                } else {
                    object1 = new GammaPixelProcessor((Destination)object0, shorts, int12);
                }
            }
        }

        if (boolean2) {
            IndexColorModel indexColorModel = (IndexColorModel)createColorModel(pngImage, shorts, false);
            object0 = new ConvertIndexedDestination((Destination)object0, int0, indexColorModel, (ComponentColorModel)colorModel);
        }

        if (object1 == null) {
            object1 = new BasicPixelProcessor((Destination)object0, int3);
        }

        if (pngConfig.getProgressive() && boolean0 && !boolean2) {
            object1 = new ProgressivePixelProcessor((Destination)object0, (PixelProcessor)object1, int0, int1);
        }

        object1 = new ProgressUpdater(pngImage, bufferedImage, (PixelProcessor)object1);
        InflaterInputStream inflaterInputStream = new InflaterInputStream(inputStream, new Inflater(), 4096);
        Defilterer defilterer = new Defilterer(inflaterInputStream, int2, int3, int0, (PixelProcessor)object1);
        if (boolean0) {
            if (defilterer.defilter(0, 0, 8, 8, (int0 + 7) / 8, (int1 + 7) / 8)
                && pngImage.handlePass(bufferedImage, 0)
                && defilterer.defilter(4, 0, 8, 8, (int0 + 3) / 8, (int1 + 7) / 8)
                && pngImage.handlePass(bufferedImage, 1)
                && defilterer.defilter(0, 4, 4, 8, (int0 + 3) / 4, (int1 + 3) / 8)
                && pngImage.handlePass(bufferedImage, 2)
                && defilterer.defilter(2, 0, 4, 4, (int0 + 1) / 4, (int1 + 3) / 4)
                && pngImage.handlePass(bufferedImage, 3)
                && defilterer.defilter(0, 2, 2, 4, (int0 + 1) / 2, (int1 + 1) / 4)
                && pngImage.handlePass(bufferedImage, 4)
                && defilterer.defilter(1, 0, 2, 2, int0 / 2, (int1 + 1) / 2)
                && pngImage.handlePass(bufferedImage, 5)
                && defilterer.defilter(0, 1, 1, 2, int0, int1 / 2)
                && pngImage.handlePass(bufferedImage, 6)) {
                boolean boolean3 = true;
            } else {
                boolean boolean4 = false;
            }
        } else if (defilterer.defilter(0, 0, 1, 1, int0, int1) && pngImage.handlePass(bufferedImage, 0)) {
            boolean boolean5 = true;
        } else {
            boolean boolean6 = false;
        }

        ((Destination)object0).done();
        return bufferedImage;
    }

    private static short[] getGammaTable(PngImage pngImage) {
        PngConfig pngConfig = pngImage.getConfig();
        if ((pngImage.getBitDepth() != 16 || pngConfig.getReduce16()) && pngConfig.getDisplayExponent() == 2.2F) {
            float float0 = pngImage.getGamma();
            if (float0 == 0.45455F) {
                return GAMMA_TABLE_45455;
            }

            if (float0 == 1.0F) {
                return GAMMA_TABLE_100000;
            }
        }

        return pngImage.getGammaTable();
    }

    private static int calcSubsamplingSize(int int2, int int1, int int3, char char0) {
        int int0 = (int2 - int3 + int1 - 1) / int1;
        if (int0 == 0) {
            throw new IllegalStateException("Source " + char0 + " subsampling " + int1 + ", offset " + int3 + " is invalid for image dimension " + int2);
        } else {
            return int0;
        }
    }

    private static boolean isIndexed(PngImage pngImage) {
        int int0 = pngImage.getColorType();
        return int0 == 3 || int0 == 0 && pngImage.getBitDepth() < 16;
    }

    private static ColorModel createColorModel(PngImage pngImage, short[] shorts, boolean boolean0) throws PngException {
        Map map = pngImage.getProperties();
        int int0 = pngImage.getColorType();
        int int1 = pngImage.getBitDepth();
        int int2 = int1 == 16 && pngImage.getConfig().getReduce16() ? 8 : int1;
        if (isIndexed(pngImage) && !boolean0) {
            byte[] bytes0;
            byte[] bytes1;
            byte[] bytes2;
            if (int0 == 3) {
                byte[] bytes3 = (byte[])pngImage.getProperty("palette", byte[].class, true);
                int int3 = bytes3.length / 3;
                bytes0 = new byte[int3];
                bytes1 = new byte[int3];
                bytes2 = new byte[int3];
                int int4 = 0;

                for (int int5 = 0; int4 < int3; int4++) {
                    bytes0[int4] = bytes3[int5++];
                    bytes1[int4] = bytes3[int5++];
                    bytes2[int4] = bytes3[int5++];
                }

                applyGamma(bytes0, shorts);
                applyGamma(bytes1, shorts);
                applyGamma(bytes2, shorts);
            } else {
                int int6 = 1 << int1;
                bytes0 = bytes1 = bytes2 = new byte[int6];

                for (int int7 = 0; int7 < int6; int7++) {
                    bytes0[int7] = (byte)(int7 * 255 / (int6 - 1));
                }

                applyGamma(bytes0, shorts);
            }

            if (map.containsKey("palette_alpha")) {
                byte[] bytes4 = (byte[])pngImage.getProperty("palette_alpha", byte[].class, true);
                byte[] bytes5 = new byte[bytes0.length];
                Arrays.fill(bytes5, bytes4.length, bytes0.length, (byte)-1);
                System.arraycopy(bytes4, 0, bytes5, 0, bytes4.length);
                return new IndexColorModel(int2, bytes0.length, bytes0, bytes1, bytes2, bytes5);
            } else {
                int int8 = -1;
                if (map.containsKey("transparency")) {
                    int8 = ((int[])pngImage.getProperty("transparency", int[].class, true))[0];
                }

                return new IndexColorModel(int2, bytes0.length, bytes0, bytes1, bytes2, int8);
            }
        } else {
            int int9 = int2 == 16 ? 1 : 0;
            int int10 = int0 != 0 && int0 != 4 ? 1000 : 1003;
            int int11 = pngImage.getTransparency();
            return new ComponentColorModel(ColorSpace.getInstance(int10), null, int11 != 1, false, int11, int9);
        }
    }

    private static void applyGamma(byte[] bytes, short[] shorts) {
        if (shorts != null) {
            for (int int0 = 0; int0 < bytes.length; int0++) {
                bytes[int0] = (byte)shorts[255 & bytes[int0]];
            }
        }
    }

    private static short[] getIdentityTable(int int1) {
        int int0 = 1 << int1;
        short[] shorts = new short[int0];

        for (int int2 = 0; int2 < int0; int2++) {
            shorts[int2] = (short)int2;
        }

        return shorts;
    }
}
