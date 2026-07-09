// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import zombie.GameWindow;
import zombie.debug.DebugLog;

public class Pcx {
    public static HashMap<String, Pcx> Cache = new HashMap<>();
    public byte[] imageData;
    public int imageWidth;
    public int imageHeight;
    public int[] palette;
    public int[] pic;

    public Pcx(String var1) {
    }

    public Pcx(URL var1) {
    }

    public Pcx(String var1, int[] var2) {
    }

    public Pcx(String var1, String var2) {
    }

    public Image getImage() {
        int[] ints = new int[this.imageWidth * this.imageHeight];
        int int0 = 0;
        int int1 = 0;

        for (int int2 = 0; int2 < this.imageWidth; int2++) {
            for (int int3 = 0; int3 < this.imageHeight; int3++) {
                ints[int0++] = 0xFF000000 | (this.imageData[int1++] & 255) << 16 | (this.imageData[int1++] & 255) << 8 | this.imageData[int1++] & 255;
            }
        }

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        return toolkit.createImage(new MemoryImageSource(this.imageWidth, this.imageHeight, ints, 0, this.imageWidth));
    }

    int loadPCX(URL url) {
        try {
            InputStream inputStream = url.openStream();
            int int0 = inputStream.available();
            byte[] bytes0 = new byte[int0 + 1];
            bytes0[int0] = 0;

            for (int int1 = 0; int1 < int0; int1++) {
                bytes0[int1] = (byte)inputStream.read();
            }

            inputStream.close();
            if (int0 == -1) {
                return -1;
            } else {
                Pcx.pcx_t pcx_t = new Pcx.pcx_t(bytes0);
                byte[] bytes1 = pcx_t.data;
                if (pcx_t.manufacturer == '\n'
                    && pcx_t.version == 5
                    && pcx_t.encoding == 1
                    && pcx_t.bits_per_pixel == '\b'
                    && pcx_t.xmax < 640
                    && pcx_t.ymax < 480) {
                    this.palette = new int[768];

                    for (int int2 = 0; int2 < 768; int2++) {
                        if (int0 - 128 - 768 + int2 < pcx_t.data.length) {
                            this.palette[int2] = pcx_t.data[int0 - 128 - 768 + int2] & 255;
                        }
                    }

                    this.imageWidth = pcx_t.xmax + 1;
                    this.imageHeight = pcx_t.ymax + 1;
                    int[] ints0 = new int[(pcx_t.ymax + 1) * (pcx_t.xmax + 1)];
                    this.pic = ints0;
                    int[] ints1 = ints0;
                    int int3 = 0;
                    int int4 = 0;

                    for (int int5 = 0; int5 <= pcx_t.ymax; int3 += pcx_t.xmax + 1) {
                        int int6 = 0;

                        while (int6 <= pcx_t.xmax) {
                            byte byte0 = bytes1[int4++];
                            int int7;
                            if ((byte0 & 192) == 192) {
                                int7 = byte0 & 63;
                                byte0 = bytes1[int4++];
                            } else {
                                int7 = 1;
                            }

                            while (int7-- > 0) {
                                ints1[int3 + int6++] = byte0 & 255;
                            }
                        }

                        int5++;
                    }

                    if (this.pic != null && this.palette != null) {
                        this.imageData = new byte[(this.imageWidth + 1) * (this.imageHeight + 1) * 3];

                        for (int int8 = 0; int8 < this.imageWidth * this.imageHeight; int8++) {
                            this.imageData[int8 * 3] = (byte)this.palette[this.pic[int8] * 3];
                            this.imageData[int8 * 3 + 1] = (byte)this.palette[this.pic[int8] * 3 + 1];
                            this.imageData[int8 * 3 + 2] = (byte)this.palette[this.pic[int8] * 3 + 2];
                        }

                        return 1;
                    } else {
                        return -1;
                    }
                } else {
                    DebugLog.log("Bad pcx file " + url);
                    return -1;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return 1;
        }
    }

    int loadPCXminusPal(String string) {
        try {
            if (Cache.containsKey(string)) {
                Pcx pcx0 = Cache.get(string);
                this.imageWidth = pcx0.imageWidth;
                this.imageHeight = pcx0.imageHeight;
                this.imageData = new byte[(pcx0.imageWidth + 1) * (pcx0.imageHeight + 1) * 3];

                for (int int0 = 0; int0 < pcx0.imageWidth * pcx0.imageHeight; int0++) {
                    this.imageData[int0 * 3] = (byte)this.palette[pcx0.pic[int0] * 3];
                    this.imageData[int0 * 3 + 1] = (byte)this.palette[pcx0.pic[int0] * 3 + 1];
                    this.imageData[int0 * 3 + 2] = (byte)this.palette[pcx0.pic[int0] * 3 + 2];
                }

                return 1;
            } else {
                InputStream inputStream = GameWindow.class.getClassLoader().getResourceAsStream(string);
                if (inputStream == null) {
                    return 0;
                } else {
                    int int1 = inputStream.available();
                    byte[] bytes0 = new byte[int1 + 1];
                    bytes0[int1] = 0;

                    for (int int2 = 0; int2 < int1; int2++) {
                        bytes0[int2] = (byte)inputStream.read();
                    }

                    inputStream.close();
                    if (int1 == -1) {
                        return -1;
                    } else {
                        Pcx.pcx_t pcx_t = new Pcx.pcx_t(bytes0);
                        byte[] bytes1 = pcx_t.data;
                        if (pcx_t.manufacturer == '\n'
                            && pcx_t.version == 5
                            && pcx_t.encoding == 1
                            && pcx_t.bits_per_pixel == '\b'
                            && pcx_t.xmax < 640
                            && pcx_t.ymax < 480) {
                            this.imageWidth = pcx_t.xmax + 1;
                            this.imageHeight = pcx_t.ymax + 1;
                            int[] ints0 = new int[(pcx_t.ymax + 1) * (pcx_t.xmax + 1)];
                            this.pic = ints0;
                            int[] ints1 = ints0;
                            int int3 = 0;
                            int int4 = 0;

                            for (int int5 = 0; int5 <= pcx_t.ymax; int3 += pcx_t.xmax + 1) {
                                int int6 = 0;

                                while (int6 <= pcx_t.xmax) {
                                    byte byte0 = bytes1[int4++];
                                    int int7;
                                    if ((byte0 & 192) == 192) {
                                        int7 = byte0 & 63;
                                        byte0 = bytes1[int4++];
                                    } else {
                                        int7 = 1;
                                    }

                                    while (int7-- > 0) {
                                        ints1[int3 + int6++] = byte0 & 255;
                                    }
                                }

                                int5++;
                            }

                            if (this.pic != null && this.palette != null) {
                                this.imageData = new byte[(this.imageWidth + 1) * (this.imageHeight + 1) * 3];

                                for (int int8 = 0; int8 < this.imageWidth * this.imageHeight; int8++) {
                                    this.imageData[int8 * 3] = (byte)this.palette[this.pic[int8] * 3];
                                    this.imageData[int8 * 3 + 1] = (byte)this.palette[this.pic[int8] * 3 + 1];
                                    this.imageData[int8 * 3 + 2] = (byte)this.palette[this.pic[int8] * 3 + 2];
                                }

                                Cache.put(string, this);
                                return 1;
                            } else {
                                return -1;
                            }
                        } else {
                            DebugLog.log("Bad pcx file " + string);
                            return -1;
                        }
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return 1;
        }
    }

    int loadPCXpal(String string) {
        try {
            InputStream inputStream = GameWindow.class.getClassLoader().getResourceAsStream(string);
            if (inputStream == null) {
                return 1;
            } else {
                int int0 = inputStream.available();
                byte[] bytes0 = new byte[int0 + 1];
                bytes0[int0] = 0;

                for (int int1 = 0; int1 < int0; int1++) {
                    bytes0[int1] = (byte)inputStream.read();
                }

                inputStream.close();
                if (int0 == -1) {
                    return -1;
                } else {
                    Pcx.pcx_t pcx_t = new Pcx.pcx_t(bytes0);
                    byte[] bytes1 = pcx_t.data;
                    if (pcx_t.manufacturer == '\n'
                        && pcx_t.version == 5
                        && pcx_t.encoding == 1
                        && pcx_t.bits_per_pixel == '\b'
                        && pcx_t.xmax < 640
                        && pcx_t.ymax < 480) {
                        this.palette = new int[768];

                        for (int int2 = 0; int2 < 768; int2++) {
                            if (int0 - 128 - 768 + int2 < pcx_t.data.length) {
                                this.palette[int2] = pcx_t.data[int0 - 128 - 768 + int2] & 255;
                            }
                        }

                        this.imageWidth = pcx_t.xmax + 1;
                        this.imageHeight = pcx_t.ymax + 1;
                        int[] ints = new int[(pcx_t.ymax + 1) * (pcx_t.xmax + 1)];
                        this.pic = ints;
                        boolean boolean0 = false;
                        boolean boolean1 = false;
                        return this.pic != null && this.palette != null ? 1 : -1;
                    } else {
                        DebugLog.log("Bad pcx file " + string);
                        return -1;
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return 1;
        }
    }

    private void loadPCXpal(int[] ints) {
        this.palette = ints;
    }

    class pcx_t {
        char bits_per_pixel;
        short bytes_per_line;
        char color_planes;
        byte[] data;
        char encoding;
        byte[] filler = new byte[58];
        short hres;
        short vres;
        char manufacturer;
        int[] palette = new int[48];
        short palette_type;
        char reserved;
        char version;
        short xmin;
        short ymin;
        short xmax;
        short ymax;

        pcx_t(byte[] bytes) {
            this.manufacturer = (char)bytes[0];
            this.version = (char)bytes[1];
            this.encoding = (char)bytes[2];
            this.bits_per_pixel = (char)bytes[3];
            this.xmin = (short)(bytes[4] + (bytes[5] << 8) & 0xFF);
            this.ymin = (short)(bytes[6] + (bytes[7] << 8) & 0xFF);
            this.xmax = (short)(bytes[8] + (bytes[9] << 8) & 0xFF);
            this.ymax = (short)(bytes[10] + (bytes[11] << 8) & 0xFF);
            this.hres = (short)(bytes[12] + (bytes[13] << 8) & 0xFF);
            this.vres = (short)(bytes[14] + (bytes[15] << 8) & 0xFF);

            for (int int0 = 0; int0 < 48; int0++) {
                this.palette[int0] = bytes[16 + int0] & 255;
            }

            this.reserved = (char)bytes[64];
            this.color_planes = (char)bytes[65];
            this.bytes_per_line = (short)(bytes[66] + (bytes[67] << 8) & 0xFF);
            this.palette_type = (short)(bytes[68] + (bytes[69] << 8) & 0xFF);

            for (int int1 = 0; int1 < 58; int1++) {
                this.filler[int1] = bytes[70 + int1];
            }

            this.data = new byte[bytes.length - 128];

            for (int int2 = 0; int2 < bytes.length - 128; int2++) {
                this.data[int2] = bytes[128 + int2];
            }
        }
    }
}
