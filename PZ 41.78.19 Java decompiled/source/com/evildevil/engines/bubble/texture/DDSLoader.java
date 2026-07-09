// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.evildevil.engines.bubble.texture;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import zombie.core.textures.Texture;
import zombie.core.textures.TexturePackPage;
import zombie.debug.DebugLog;

public final class DDSLoader implements DDSurface {
    private final String DDS_IDENTIFIER = "DDS ";
    private final int DDS_HEADER_SIZE = 128;
    private final int DDS_DESC2_RESERVED_1 = 44;
    private final int DDS_DESC2_RESERVED_2 = 4;
    private final int DDS_CAPS2_RESERVED = 8;
    private final int DEFAULT_DXT_BLOCKSIZE = 16;
    private final int DXT1_BLOCKSIZE = 8;
    private final DDSurfaceDesc2 ddsDesc2 = new DDSurfaceDesc2();
    private static ByteBuffer ddsHeader = null;
    private BufferedInputStream ddsFileChannel = null;
    static ByteBuffer imageData = null;
    static ByteBuffer imageData2 = null;
    public static int lastWid = 0;
    public static int lastHei = 0;

    public int loadDDSFile(String string) {
        File file = new File(string);

        try {
            new FileInputStream(file);
            if (this.ddsFileChannel == null) {
                throw new NullPointerException("ddsFileChannel couldn't be null!");
            }
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

        if (ddsHeader == null) {
            ddsHeader = ByteBuffer.allocate(128);
        }

        this.readFileHeader();
        return this.readFileData();
    }

    public int loadDDSFile(BufferedInputStream bufferedInputStream) {
        this.ddsFileChannel = bufferedInputStream;
        if (this.ddsFileChannel == null) {
            throw new NullPointerException("ddsFileChannel couldn't be null!");
        } else {
            if (ddsHeader == null) {
                ddsHeader = ByteBuffer.allocate(128);
            }

            ddsHeader.rewind();
            this.readFileHeader();
            return this.readFileData();
        }
    }

    private void readFileHeader() {
        try {
            try {
                this.ddsFileChannel.read(ddsHeader.array());
                this.ddsDesc2.setIdentifier(TexturePackPage.readInt(ddsHeader) & 4294967295L);
                this.ddsDesc2.setSize(TexturePackPage.readInt(ddsHeader) & 4294967295L);
                this.ddsDesc2.setFlags(TexturePackPage.readInt(ddsHeader) & 4294967295L);
                this.ddsDesc2.setHeight(TexturePackPage.readInt(ddsHeader));
                this.ddsDesc2.setWidth(TexturePackPage.readInt(ddsHeader));
                this.ddsDesc2.setPitchOrLinearSize(TexturePackPage.readInt(ddsHeader) & 4294967295L);
                this.ddsDesc2.setDepth(TexturePackPage.readInt(ddsHeader) & 4294967295L);
                this.ddsDesc2.setMipMapCount(TexturePackPage.readInt(ddsHeader) & 4294967295L);
                ddsHeader.position(ddsHeader.position() + 44);
                DDPixelFormat dDPixelFormat = this.ddsDesc2.getDDPixelformat();
                dDPixelFormat.setSize(TexturePackPage.readInt(ddsHeader) & 4294967295L);
                dDPixelFormat.setFlags(TexturePackPage.readInt(ddsHeader) & 4294967295L);
                dDPixelFormat.setFourCC(TexturePackPage.readInt(ddsHeader) & 4294967295L);
                dDPixelFormat.setRGBBitCount(TexturePackPage.readInt(ddsHeader) & 4294967295L);
                dDPixelFormat.setRBitMask(TexturePackPage.readInt(ddsHeader) & 4294967295L);
                dDPixelFormat.setGBitMask(TexturePackPage.readInt(ddsHeader) & 4294967295L);
                dDPixelFormat.setBBitMask(TexturePackPage.readInt(ddsHeader) & 4294967295L);
                dDPixelFormat.setRGBAlphaBitMask(TexturePackPage.readInt(ddsHeader) & 4294967295L);
                DDSCaps2 dDSCaps2 = this.ddsDesc2.getDDSCaps2();
                dDSCaps2.setCaps1(TexturePackPage.readInt(ddsHeader) & 4294967295L);
                dDSCaps2.setCaps2(TexturePackPage.readInt(ddsHeader) & 4294967295L);
                ddsHeader.position(ddsHeader.position() + 8);
                ddsHeader.position(ddsHeader.position() + 4);
            } catch (BufferUnderflowException bufferUnderflowException) {
                bufferUnderflowException.printStackTrace();
            } catch (TextureFormatException textureFormatException) {
                textureFormatException.printStackTrace();
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
        } finally {
            ;
        }
    }

    private int readFileData() {
        DDPixelFormat dDPixelFormat = this.ddsDesc2.getDDPixelformat();
        int int0 = 0;
        char char0 = 0;
        if (dDPixelFormat.isCompressed && dDPixelFormat.getFourCCString().equalsIgnoreCase("DXT1")) {
            int0 = this.calculateSize(8);
            char0 = '\u83f1';
        } else {
            int0 = this.calculateSize(16);
            if (dDPixelFormat.getFourCCString().equalsIgnoreCase("DXT3")) {
                char0 = '\u83f2';
            } else if (dDPixelFormat.getFourCCString().equals("DXT5")) {
                char0 = '\u83f3';
            }
        }

        if (imageData == null) {
            imageData = ByteBuffer.allocate(4194304);
        }

        imageData.rewind();

        try {
            this.ddsFileChannel.read(imageData.array(), 0, (int)this.ddsDesc2.pitchOrLinearSize);
            imageData.flip();
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }

        if (imageData2 == null) {
            imageData2 = ByteBuffer.allocateDirect(4194304);
        }

        imageData2.clear();
        imageData2.put(imageData.array(), 0, (int)this.ddsDesc2.pitchOrLinearSize);
        imageData2.flip();
        lastWid = (int)this.ddsDesc2.width;
        lastHei = (int)this.ddsDesc2.height;
        int int1 = GL11.glGenTextures();
        Texture.lastTextureID = int1;
        GL11.glBindTexture(3553, int1);
        GL11.glTexParameteri(3553, 10240, 9728);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10242, 10497);
        GL11.glTexParameteri(3553, 10243, 10497);
        GL13.glCompressedTexImage2D(3553, 0, char0, (int)this.ddsDesc2.width, (int)this.ddsDesc2.height, 0, imageData2);
        return int1;
    }

    private int calculateSize(int int0) {
        double double0 = Math.ceil(this.ddsDesc2.width / 4L) * Math.ceil(this.ddsDesc2.height / 4L) * int0;
        return (int)double0;
    }

    public void debugInfo() {
        DDPixelFormat dDPixelFormat = this.ddsDesc2.getDDPixelformat();
        DDSCaps2 dDSCaps2 = this.ddsDesc2.getDDSCaps2();
        DebugLog.log("\nDDSURFACEDESC2:");
        DebugLog.log("----------------------------------------");
        DebugLog.log("SIZE: " + this.ddsDesc2.size);
        DebugLog.log("FLAGS: " + this.ddsDesc2.flags);
        DebugLog.log("HEIGHT: " + this.ddsDesc2.height);
        DebugLog.log("WIDTH: " + this.ddsDesc2.width);
        DebugLog.log("PITCH_OR_LINEAR_SIZE: " + this.ddsDesc2.pitchOrLinearSize);
        DebugLog.log("DEPTH: " + this.ddsDesc2.depth);
        DebugLog.log("MIP_MAP_COUNT: " + this.ddsDesc2.mipMapCount);
        DebugLog.log("\nDDPIXELFORMAT of DDSURFACEDESC2:");
        DebugLog.log("----------------------------------------");
        DebugLog.log("SIZE :" + dDPixelFormat.size);
        DebugLog.log("FLAGS: " + dDPixelFormat.flags);
        DebugLog.log("FOUR_CC: " + dDPixelFormat.getFourCCString());
        DebugLog.log("RGB_BIT_COUNT: " + dDPixelFormat.rgbBitCount);
        DebugLog.log("R_BIT_MASK: " + dDPixelFormat.rBitMask);
        DebugLog.log("G_BIT_MASK: " + dDPixelFormat.gBitMask);
        DebugLog.log("B_BIT_MASK: " + dDPixelFormat.bBitMask);
        DebugLog.log("RGB_ALPHA_BIT_MASK: " + dDPixelFormat.rgbAlphaBitMask);
        DebugLog.log("\nDDSCAPS of DDSURFACEDESC2");
        DebugLog.log("----------------------------------------");
        DebugLog.log("CAPS1: " + dDSCaps2.caps1);
        DebugLog.log("CAPS2: " + dDSCaps2.caps2);
    }
}
