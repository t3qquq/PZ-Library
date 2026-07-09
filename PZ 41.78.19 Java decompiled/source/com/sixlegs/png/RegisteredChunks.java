// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.sixlegs.png;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

class RegisteredChunks {
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");
    private static final String ISO_8859_1 = "ISO-8859-1";
    private static final String US_ASCII = "US-ASCII";
    private static final String UTF_8 = "UTF-8";

    public static boolean read(int int0, DataInput dataInput, int int1, PngImage pngImage) throws IOException {
        Map map = pngImage.getProperties();
        switch (int0) {
            case 1229278788:
                checkLength(1229278788, int1, 0);
                break;
            case 1229472850:
                read_IHDR(dataInput, int1, map);
                break;
            case 1347179589:
                read_PLTE(dataInput, int1, map, pngImage);
                break;
            case 1649100612:
                read_bKGD(dataInput, int1, map, pngImage);
                break;
            case 1665684045:
                read_cHRM(dataInput, int1, map);
                break;
            case 1732332865:
                read_gAMA(dataInput, int1, map);
                break;
            case 1732855399:
                read_gIFg(dataInput, int1, map);
                break;
            case 1749635924:
                read_hIST(dataInput, int1, map, pngImage);
                break;
            case 1766015824:
                read_iCCP(dataInput, int1, map);
                break;
            case 1767135348:
            case 1950701684:
            case 2052348020:
                readText(int0, dataInput, int1, map, pngImage);
                break;
            case 1866876531:
                read_oFFs(dataInput, int1, map);
                break;
            case 1883789683:
                read_pHYs(dataInput, int1, map);
                break;
            case 1933723988:
                read_sBIT(dataInput, int1, map, pngImage);
                break;
            case 1933787468:
                read_sCAL(dataInput, int1, map);
                break;
            case 1934642260:
                read_sPLT(dataInput, int1, map, pngImage);
                break;
            case 1934772034:
                read_sRGB(dataInput, int1, map);
                break;
            case 1934902610:
                read_sTER(dataInput, int1, map);
                break;
            case 1950960965:
                read_tIME(dataInput, int1, map);
                break;
            case 1951551059:
                read_tRNS(dataInput, int1, map, pngImage);
                break;
            default:
                return false;
        }

        return true;
    }

    private static void read_IHDR(DataInput dataInput, int int0, Map map) throws IOException {
        checkLength(1229472850, int0, 13);
        int int1 = dataInput.readInt();
        int int2 = dataInput.readInt();
        if (int1 > 0 && int2 > 0) {
            byte byte0 = dataInput.readByte();
            switch (byte0) {
                case 1:
                case 2:
                case 4:
                case 8:
                case 16:
                    Object object = null;
                    int int3 = dataInput.readUnsignedByte();
                    switch (int3) {
                        case 0:
                        case 2:
                            break;
                        case 1:
                        case 5:
                        default:
                            throw new PngException("Bad color type: " + int3, true);
                        case 3:
                            if (byte0 == 16) {
                                throw new PngException("Bad bit depth for color type " + int3 + ": " + byte0, true);
                            }
                            break;
                        case 4:
                        case 6:
                            if (byte0 <= 4) {
                                throw new PngException("Bad bit depth for color type " + int3 + ": " + byte0, true);
                            }
                    }

                    int int4 = dataInput.readUnsignedByte();
                    if (int4 != 0) {
                        throw new PngException("Unrecognized compression method: " + int4, true);
                    } else {
                        int int5 = dataInput.readUnsignedByte();
                        if (int5 != 0) {
                            throw new PngException("Unrecognized filter method: " + int5, true);
                        } else {
                            int int6 = dataInput.readUnsignedByte();
                            switch (int6) {
                                case 0:
                                case 1:
                                    map.put("width", Integers.valueOf(int1));
                                    map.put("height", Integers.valueOf(int2));
                                    map.put("bit_depth", Integers.valueOf(byte0));
                                    map.put("interlace", Integers.valueOf(int6));
                                    map.put("compression", Integers.valueOf(int4));
                                    map.put("filter", Integers.valueOf(int5));
                                    map.put("color_type", Integers.valueOf(int3));
                                    return;
                                default:
                                    throw new PngException("Unrecognized interlace method: " + int6, true);
                            }
                        }
                    }
                default:
                    throw new PngException("Bad bit depth: " + byte0, true);
            }
        } else {
            throw new PngException("Bad image size: " + int1 + "x" + int2, true);
        }
    }

    private static void read_PLTE(DataInput dataInput, int int0, Map map, PngImage pngImage) throws IOException {
        if (int0 == 0) {
            throw new PngException("PLTE chunk cannot be empty", true);
        } else if (int0 % 3 != 0) {
            throw new PngException("PLTE chunk length indivisible by 3: " + int0, true);
        } else {
            int int1 = int0 / 3;
            if (int1 > 256) {
                throw new PngException("Too many palette entries: " + int1, true);
            } else {
                switch (pngImage.getColorType()) {
                    case 0:
                    case 4:
                        throw new PngException("PLTE chunk found in grayscale image", false);
                    case 3:
                        if (int1 > 2 << pngImage.getBitDepth() - 1) {
                            throw new PngException("Too many palette entries: " + int1, true);
                        }
                    case 1:
                    case 2:
                    default:
                        byte[] bytes = new byte[int0];
                        dataInput.readFully(bytes);
                        map.put("palette", bytes);
                }
            }
        }
    }

    private static void read_tRNS(DataInput dataInput, int int0, Map map, PngImage pngImage) throws IOException {
        switch (pngImage.getColorType()) {
            case 0:
                checkLength(1951551059, int0, 2);
                map.put("transparency", new int[]{dataInput.readUnsignedShort()});
                break;
            case 1:
            default:
                throw new PngException("tRNS prohibited for color type " + pngImage.getColorType(), true);
            case 2:
                checkLength(1951551059, int0, 6);
                map.put("transparency", new int[]{dataInput.readUnsignedShort(), dataInput.readUnsignedShort(), dataInput.readUnsignedShort()});
                break;
            case 3:
                int int1 = ((byte[])pngImage.getProperty("palette", byte[].class, true)).length / 3;
                if (int0 > int1) {
                    throw new PngException("Too many transparency palette entries (" + int0 + " > " + int1 + ")", true);
                }

                byte[] bytes = new byte[int0];
                dataInput.readFully(bytes);
                map.put("palette_alpha", bytes);
        }
    }

    private static void read_bKGD(DataInput dataInput, int int0, Map map, PngImage pngImage) throws IOException {
        map.put("background_rgb", switch (pngImage.getColorType()) {
            case 0, 4 -> {
                checkLength(1649100612, int0, 2);
                yield new int[]{dataInput.readUnsignedShort()};
            }
            default -> {
                checkLength(1649100612, int0, 6);
                yield new int[]{dataInput.readUnsignedShort(), dataInput.readUnsignedShort(), dataInput.readUnsignedShort()};
            }
            case 3 -> {
                checkLength(1649100612, int0, 1);
                yield new int[]{dataInput.readUnsignedByte()};
            }
        });
    }

    private static void read_cHRM(DataInput dataInput, int int0, Map map) throws IOException {
        checkLength(1665684045, int0, 32);
        float[] floats = new float[8];

        for (int int1 = 0; int1 < 8; int1++) {
            floats[int1] = dataInput.readInt() / 100000.0F;
        }

        if (!map.containsKey("chromaticity")) {
            map.put("chromaticity", floats);
        }
    }

    private static void read_gAMA(DataInput dataInput, int int0, Map map) throws IOException {
        checkLength(1732332865, int0, 4);
        int int1 = dataInput.readInt();
        if (int1 == 0) {
            throw new PngException("Meaningless zero gAMA chunk value", false);
        } else {
            if (!map.containsKey("rendering_intent")) {
                map.put("gamma", new Float(int1 / 100000.0F));
            }
        }
    }

    private static void read_hIST(DataInput dataInput, int int1, Map map, PngImage pngImage) throws IOException {
        int int0 = ((byte[])pngImage.getProperty("palette", byte[].class, true)).length / 3;
        checkLength(1749635924, int1, int0 * 2);
        int[] ints = new int[int0];

        for (int int2 = 0; int2 < int0; int2++) {
            ints[int2] = dataInput.readUnsignedShort();
        }

        map.put("histogram", ints);
    }

    private static void read_iCCP(DataInput dataInput, int int0, Map map) throws IOException {
        String string = readKeyword(dataInput, int0);
        byte[] bytes = readCompressed(dataInput, int0 - string.length() - 1, true);
        map.put("icc_profile_name", string);
        map.put("icc_profile", bytes);
    }

    private static void read_pHYs(DataInput dataInput, int int0, Map map) throws IOException {
        checkLength(1883789683, int0, 9);
        int int1 = dataInput.readInt();
        int int2 = dataInput.readInt();
        int int3 = dataInput.readUnsignedByte();
        if (int3 != 0 && int3 != 1) {
            throw new PngException("Illegal pHYs chunk unit specifier: " + int3, false);
        } else {
            map.put("pixels_per_unit_x", Integers.valueOf(int1));
            map.put("pixels_per_unit_y", Integers.valueOf(int2));
            map.put("unit", Integers.valueOf(int3));
        }
    }

    private static void read_sBIT(DataInput dataInput, int int1, Map map, PngImage pngImage) throws IOException {
        boolean boolean0 = pngImage.getColorType() == 3;
        int int0 = boolean0 ? 3 : pngImage.getSamples();
        checkLength(1933723988, int1, int0);
        int int2 = boolean0 ? 8 : pngImage.getBitDepth();
        byte[] bytes = new byte[int0];

        for (int int3 = 0; int3 < int0; int3++) {
            byte byte0 = dataInput.readByte();
            if (byte0 <= 0 || byte0 > int2) {
                throw new PngException("Illegal sBIT sample depth", false);
            }

            bytes[int3] = byte0;
        }

        map.put("significant_bits", bytes);
    }

    private static void read_sRGB(DataInput dataInput, int int0, Map map) throws IOException {
        checkLength(1934772034, int0, 1);
        byte byte0 = dataInput.readByte();
        map.put("rendering_intent", Integers.valueOf(byte0));
        map.put("gamma", new Float(0.45455));
        map.put("chromaticity", new float[]{0.3127F, 0.329F, 0.64F, 0.33F, 0.3F, 0.6F, 0.15F, 0.06F});
    }

    private static void read_tIME(DataInput dataInput, int int0, Map map) throws IOException {
        checkLength(1950960965, int0, 7);
        Calendar calendar = Calendar.getInstance(TIME_ZONE);
        calendar.set(
            dataInput.readUnsignedShort(),
            check(dataInput.readUnsignedByte(), 1, 12, "month") - 1,
            check(dataInput.readUnsignedByte(), 1, 31, "day"),
            check(dataInput.readUnsignedByte(), 0, 23, "hour"),
            check(dataInput.readUnsignedByte(), 0, 59, "minute"),
            check(dataInput.readUnsignedByte(), 0, 60, "second")
        );
        map.put("time", calendar.getTime());
    }

    private static int check(int int0, int int2, int int1, String string) throws PngException {
        if (int0 >= int2 && int0 <= int1) {
            return int0;
        } else {
            throw new PngException("tIME " + string + " value " + int0 + " is out of bounds (" + int2 + "-" + int1 + ")", false);
        }
    }

    private static void read_sPLT(DataInput dataInput, int int0, Map map, PngImage pngImage) throws IOException {
        String string = readKeyword(dataInput, int0);
        byte byte0 = dataInput.readByte();
        if (byte0 != 8 && byte0 != 16) {
            throw new PngException("Sample depth must be 8 or 16", false);
        } else {
            int0 -= string.length() + 2;
            if (int0 % (byte0 == 8 ? 6 : 10) != 0) {
                throw new PngException("Incorrect sPLT data length for given sample depth", false);
            } else {
                byte[] bytes = new byte[int0];
                dataInput.readFully(bytes);
                Object object = (List)pngImage.getProperty("suggested_palettes", List.class, false);
                if (object == null) {
                    map.put("suggested_palettes", object = new ArrayList());
                }

                Iterator iterator = object.iterator();

                while (iterator.hasNext()) {
                    if (string.equals(((SuggestedPalette)iterator.next()).getName())) {
                        throw new PngException("Duplicate suggested palette name " + string, false);
                    }
                }

                object.add(new SuggestedPaletteImpl(string, byte0, bytes));
            }
        }
    }

    private static void readText(int int1, DataInput dataInput, int int0, Map map, PngImage pngImage) throws IOException {
        byte[] bytes = new byte[int0];
        dataInput.readFully(bytes);
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bytes));
        String string0 = readKeyword(dataInputStream, int0);
        String string1 = "ISO-8859-1";
        boolean boolean0 = false;
        boolean boolean1 = true;
        String string2 = null;
        String string3 = null;
        switch (int1) {
            case 1767135348:
                string1 = "UTF-8";
                byte byte0 = dataInputStream.readByte();
                byte byte1 = dataInputStream.readByte();
                if (byte0 == 1) {
                    boolean0 = true;
                    boolean1 = false;
                    if (byte1 != 0) {
                        throw new PngException("Unrecognized " + PngConstants.getChunkName(int1) + " compression method: " + byte1, false);
                    }
                } else if (byte0 != 0) {
                    throw new PngException("Illegal " + PngConstants.getChunkName(int1) + " compression flag: " + byte0, false);
                }

                string2 = readString(dataInputStream, dataInputStream.available(), "US-ASCII");
                string3 = readString(dataInputStream, dataInputStream.available(), "UTF-8");
            case 1950701684:
            default:
                break;
            case 2052348020:
                boolean0 = true;
        }

        String string4;
        if (boolean0) {
            string4 = new String(readCompressed(dataInputStream, dataInputStream.available(), boolean1), string1);
        } else {
            string4 = new String(bytes, bytes.length - dataInputStream.available(), dataInputStream.available(), string1);
        }

        if (string4.indexOf(0) >= 0) {
            throw new PngException("Text value contains null", false);
        } else {
            Object object = (List)pngImage.getProperty("text_chunks", List.class, false);
            if (object == null) {
                map.put("text_chunks", object = new ArrayList());
            }

            object.add(new TextChunkImpl(string0, string4, string2, string3, int1));
        }
    }

    private static void read_gIFg(DataInput dataInput, int int0, Map map) throws IOException {
        checkLength(1732855399, int0, 4);
        int int1 = dataInput.readUnsignedByte();
        int int2 = dataInput.readUnsignedByte();
        int int3 = dataInput.readUnsignedShort();
        map.put("gif_disposal_method", Integers.valueOf(int1));
        map.put("gif_user_input_flag", Integers.valueOf(int2));
        map.put("gif_delay_time", Integers.valueOf(int3));
    }

    private static void read_oFFs(DataInput dataInput, int int0, Map map) throws IOException {
        checkLength(1866876531, int0, 9);
        int int1 = dataInput.readInt();
        int int2 = dataInput.readInt();
        byte byte0 = dataInput.readByte();
        if (byte0 != 0 && byte0 != 1) {
            throw new PngException("Illegal oFFs chunk unit specifier: " + byte0, false);
        } else {
            map.put("position_x", Integers.valueOf(int1));
            map.put("position_y", Integers.valueOf(int2));
            map.put("position_unit", Integers.valueOf(byte0));
        }
    }

    private static void read_sCAL(DataInput dataInput, int int0, Map map) throws IOException {
        byte[] bytes = new byte[int0];
        dataInput.readFully(bytes);
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bytes));
        byte byte0 = dataInputStream.readByte();
        if (byte0 != 1 && byte0 != 2) {
            throw new PngException("Illegal sCAL chunk unit specifier: " + byte0, false);
        } else {
            double double0 = readFloatingPoint(dataInputStream, dataInputStream.available());
            double double1 = readFloatingPoint(dataInputStream, dataInputStream.available());
            if (!(double0 <= 0.0) && !(double1 <= 0.0)) {
                map.put("scale_unit", Integers.valueOf(byte0));
                map.put("pixel_width", new Double(double0));
                map.put("pixel_height", new Double(double1));
            } else {
                throw new PngException("sCAL measurements must be >= 0", false);
            }
        }
    }

    private static void read_sTER(DataInput dataInput, int int0, Map map) throws IOException {
        checkLength(1934902610, int0, 1);
        byte byte0 = dataInput.readByte();
        switch (byte0) {
            case 0:
            case 1:
                map.put("stereo_mode", Integers.valueOf(byte0));
                return;
            default:
                throw new PngException("Unknown sTER mode: " + byte0, false);
        }
    }

    public static void checkLength(int int2, int int0, int int1) throws PngException {
        if (int0 != int1) {
            throw new PngException("Bad " + PngConstants.getChunkName(int2) + " chunk length: " + int0 + " (expected " + int1 + ")", true);
        }
    }

    private static byte[] readCompressed(DataInput dataInput, int int0, boolean boolean0) throws IOException {
        if (boolean0) {
            byte byte0 = dataInput.readByte();
            if (byte0 != 0) {
                throw new PngException("Unrecognized compression method: " + byte0, false);
            }

            int0--;
        }

        byte[] bytes0 = new byte[int0];
        dataInput.readFully(bytes0);
        byte[] bytes1 = new byte[4096];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Inflater inflater = new Inflater();
        inflater.reset();
        inflater.setInput(bytes0, 0, int0);

        try {
            while (!inflater.needsInput()) {
                byteArrayOutputStream.write(bytes1, 0, inflater.inflate(bytes1));
            }
        } catch (DataFormatException dataFormatException) {
            throw new PngException("Error reading compressed data", dataFormatException, false);
        }

        return byteArrayOutputStream.toByteArray();
    }

    private static String readString(DataInput dataInput, int int0, String string) throws IOException {
        return new String(readToNull(dataInput, int0), string);
    }

    private static String readKeyword(DataInput dataInput, int int0) throws IOException {
        String string = readString(dataInput, int0, "ISO-8859-1");
        if (string.length() != 0 && string.length() <= 79) {
            return string;
        } else {
            throw new PngException("Invalid keyword length: " + string.length(), false);
        }
    }

    private static byte[] readToNull(DataInput dataInput, int int1) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        for (int int0 = 0; int0 < int1; int0++) {
            int int2 = dataInput.readUnsignedByte();
            if (int2 == 0) {
                return byteArrayOutputStream.toByteArray();
            }

            byteArrayOutputStream.write(int2);
        }

        return byteArrayOutputStream.toByteArray();
    }

    private static double readFloatingPoint(DataInput dataInput, int int0) throws IOException {
        String string = readString(dataInput, int0, "US-ASCII");
        int int1 = Math.max(string.indexOf(101), string.indexOf(69));
        double double0 = Double.valueOf(string.substring(0, int1 < 0 ? string.length() : int1));
        if (int1 >= 0) {
            double0 *= Math.pow(10.0, Double.valueOf(string.substring(int1 + 1)));
        }

        return double0;
    }
}
