// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import zombie.ZomboidFileSystem;
import zombie.core.math.PZMath;

public final class WorldMapImages {
    private static final HashMap<String, WorldMapImages> s_filenameToImages = new HashMap<>();
    private String m_directory;
    private ImagePyramid m_pyramid;

    public static WorldMapImages getOrCreate(String directory) {
        String string = ZomboidFileSystem.instance.getString(directory + "/pyramid.zip");
        if (!Files.exists(Paths.get(string))) {
            return null;
        } else {
            WorldMapImages worldMapImages = s_filenameToImages.get(string);
            if (worldMapImages == null) {
                worldMapImages = new WorldMapImages();
                worldMapImages.m_directory = directory;
                worldMapImages.m_pyramid = new ImagePyramid();
                worldMapImages.m_pyramid.setZipFile(string);
                s_filenameToImages.put(string, worldMapImages);
            }

            return worldMapImages;
        }
    }

    public ImagePyramid getPyramid() {
        return this.m_pyramid;
    }

    public int getMinX() {
        return this.m_pyramid.m_minX;
    }

    public int getMinY() {
        return this.m_pyramid.m_minY;
    }

    public int getMaxX() {
        return this.m_pyramid.m_maxX;
    }

    public int getMaxY() {
        return this.m_pyramid.m_maxY;
    }

    public int getZoom(float zoomF) {
        byte byte0 = 4;
        if (zoomF >= 16.0) {
            byte0 = 0;
        } else if (zoomF >= 15.0F) {
            byte0 = 1;
        } else if (zoomF >= 14.0F) {
            byte0 = 2;
        } else if (zoomF >= 13.0F) {
            byte0 = 3;
        }

        return PZMath.clamp(byte0, this.m_pyramid.m_minZ, this.m_pyramid.m_maxZ);
    }

    public float getResolution() {
        return this.m_pyramid.m_resolution;
    }

    private void destroy() {
        this.m_pyramid.destroy();
    }

    public static void Reset() {
        for (WorldMapImages worldMapImages : s_filenameToImages.values()) {
            worldMapImages.destroy();
        }

        s_filenameToImages.clear();
    }
}
