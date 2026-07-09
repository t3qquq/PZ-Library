// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap.styles;

import java.util.ArrayList;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.popman.ObjectPool;
import zombie.worldMap.WorldMapFeature;
import zombie.worldMap.WorldMapRenderer;

public abstract class WorldMapStyleLayer {
    public String m_id;
    public float m_minZoom = 0.0F;
    public WorldMapStyleLayer.IWorldMapStyleFilter m_filter;
    public String m_filterKey;
    public String m_filterValue;

    public WorldMapStyleLayer(String id) {
        this.m_id = id;
    }

    public abstract String getTypeString();

    static <S extends WorldMapStyleLayer.Stop> int findStop(float float0, ArrayList<S> arrayList) {
        if (arrayList.isEmpty()) {
            return -2;
        } else if (float0 <= ((WorldMapStyleLayer.Stop)arrayList.get(0)).m_zoom) {
            return -1;
        } else {
            for (int int0 = 0; int0 < arrayList.size() - 1; int0++) {
                if (float0 <= ((WorldMapStyleLayer.Stop)arrayList.get(int0 + 1)).m_zoom) {
                    return int0;
                }
            }

            return arrayList.size() - 1;
        }
    }

    protected WorldMapStyleLayer.RGBAf evalColor(WorldMapStyleLayer.RenderArgs renderArgs, ArrayList<WorldMapStyleLayer.ColorStop> arrayList) {
        if (arrayList.isEmpty()) {
            return WorldMapStyleLayer.RGBAf.s_pool.alloc().init(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            float float0 = renderArgs.drawer.m_zoomF;
            int int0 = findStop(float0, arrayList);
            int int1 = int0 == -1 ? 0 : int0;
            int int2 = PZMath.min(int0 + 1, arrayList.size() - 1);
            WorldMapStyleLayer.ColorStop colorStop0 = (WorldMapStyleLayer.ColorStop)arrayList.get(int1);
            WorldMapStyleLayer.ColorStop colorStop1 = (WorldMapStyleLayer.ColorStop)arrayList.get(int2);
            float float1 = int1 == int2
                ? 1.0F
                : (PZMath.clamp(float0, colorStop0.m_zoom, colorStop1.m_zoom) - colorStop0.m_zoom) / (colorStop1.m_zoom - colorStop0.m_zoom);
            float float2 = PZMath.lerp(colorStop0.r, colorStop1.r, float1) / 255.0F;
            float float3 = PZMath.lerp(colorStop0.g, colorStop1.g, float1) / 255.0F;
            float float4 = PZMath.lerp(colorStop0.b, colorStop1.b, float1) / 255.0F;
            float float5 = PZMath.lerp(colorStop0.a, colorStop1.a, float1) / 255.0F;
            return WorldMapStyleLayer.RGBAf.s_pool.alloc().init(float2, float3, float4, float5);
        }
    }

    protected float evalFloat(WorldMapStyleLayer.RenderArgs renderArgs, ArrayList<WorldMapStyleLayer.FloatStop> arrayList) {
        if (arrayList.isEmpty()) {
            return 1.0F;
        } else {
            float float0 = renderArgs.drawer.m_zoomF;
            int int0 = findStop(float0, arrayList);
            int int1 = int0 == -1 ? 0 : int0;
            int int2 = PZMath.min(int0 + 1, arrayList.size() - 1);
            WorldMapStyleLayer.FloatStop floatStop0 = (WorldMapStyleLayer.FloatStop)arrayList.get(int1);
            WorldMapStyleLayer.FloatStop floatStop1 = (WorldMapStyleLayer.FloatStop)arrayList.get(int2);
            float float1 = int1 == int2
                ? 1.0F
                : (PZMath.clamp(float0, floatStop0.m_zoom, floatStop1.m_zoom) - floatStop0.m_zoom) / (floatStop1.m_zoom - floatStop0.m_zoom);
            return PZMath.lerp(floatStop0.f, floatStop1.f, float1);
        }
    }

    protected Texture evalTexture(WorldMapStyleLayer.RenderArgs renderArgs, ArrayList<WorldMapStyleLayer.TextureStop> arrayList) {
        if (arrayList.isEmpty()) {
            return null;
        } else {
            float float0 = renderArgs.drawer.m_zoomF;
            int int0 = findStop(float0, arrayList);
            int int1 = int0 == -1 ? 0 : int0;
            int int2 = PZMath.min(int0 + 1, arrayList.size() - 1);
            WorldMapStyleLayer.TextureStop textureStop0 = (WorldMapStyleLayer.TextureStop)arrayList.get(int1);
            WorldMapStyleLayer.TextureStop textureStop1 = (WorldMapStyleLayer.TextureStop)arrayList.get(int2);
            if (textureStop0 == textureStop1) {
                return float0 < textureStop0.m_zoom ? null : textureStop0.texture;
            } else if (!(float0 < textureStop0.m_zoom) && !(float0 > textureStop1.m_zoom)) {
                float float1 = int1 == int2
                    ? 1.0F
                    : (PZMath.clamp(float0, textureStop0.m_zoom, textureStop1.m_zoom) - textureStop0.m_zoom) / (textureStop1.m_zoom - textureStop0.m_zoom);
                return float1 < 0.5F ? textureStop0.texture : textureStop1.texture;
            } else {
                return null;
            }
        }
    }

    public boolean filter(WorldMapFeature feature, WorldMapStyleLayer.FilterArgs args) {
        return this.m_filter == null ? false : this.m_filter.filter(feature, args);
    }

    public abstract void render(WorldMapFeature feature, WorldMapStyleLayer.RenderArgs args);

    public void renderCell(WorldMapStyleLayer.RenderArgs args) {
    }

    public static class ColorStop extends WorldMapStyleLayer.Stop {
        public int r;
        public int g;
        public int b;
        public int a;

        public ColorStop(float float0, int int0, int int1, int int2, int int3) {
            super(float0);
            this.r = int0;
            this.g = int1;
            this.b = int2;
            this.a = int3;
        }
    }

    public static final class FilterArgs {
        public WorldMapRenderer renderer;
    }

    public static class FloatStop extends WorldMapStyleLayer.Stop {
        public float f;

        public FloatStop(float float0, float float1) {
            super(float0);
            this.f = float1;
        }
    }

    public interface IWorldMapStyleFilter {
        boolean filter(WorldMapFeature feature, WorldMapStyleLayer.FilterArgs args);
    }

    public static final class RGBAf {
        public float r;
        public float g;
        public float b;
        public float a;
        public static final ObjectPool<WorldMapStyleLayer.RGBAf> s_pool = new ObjectPool<>(WorldMapStyleLayer.RGBAf::new);

        public RGBAf() {
            this.r = this.g = this.b = this.a = 1.0F;
        }

        public WorldMapStyleLayer.RGBAf init(float _r, float _g, float _b, float _a) {
            this.r = _r;
            this.g = _g;
            this.b = _b;
            this.a = _a;
            return this;
        }
    }

    public static final class RenderArgs {
        public WorldMapRenderer renderer;
        public WorldMapRenderer.Drawer drawer;
        public int cellX;
        public int cellY;
    }

    public static class Stop {
        public float m_zoom;

        Stop(float float0) {
            this.m_zoom = float0;
        }
    }

    public static class TextureStop extends WorldMapStyleLayer.Stop {
        public String texturePath;
        public Texture texture;

        public TextureStop(float float0, String string) {
            super(float0);
            this.texturePath = string;
            this.texture = Texture.getTexture(string);
        }
    }
}
