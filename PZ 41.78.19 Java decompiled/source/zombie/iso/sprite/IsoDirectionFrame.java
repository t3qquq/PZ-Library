// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.sprite;

import java.util.function.Consumer;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.iso.IsoDirections;
import zombie.iso.objects.ObjectRenderEffects;

public final class IsoDirectionFrame {
    public final Texture[] directions = new Texture[8];
    boolean bDoFlip = true;

    public IsoDirectionFrame(Texture tex) {
        this.SetAllDirections(tex);
    }

    public IsoDirectionFrame() {
    }

    public IsoDirectionFrame(Texture nw, Texture n, Texture ne, Texture e, Texture se) {
        this.directions[0] = n;
        this.directions[1] = nw;
        this.directions[2] = n;
        this.directions[3] = ne;
        this.directions[4] = e;
        this.directions[5] = se;
        this.directions[6] = e;
        this.directions[7] = ne;
    }

    public IsoDirectionFrame(Texture n, Texture nw, Texture w, Texture sw, Texture s, Texture se, Texture e, Texture ne) {
        if (s == null) {
            boolean boolean0 = false;
        }

        this.directions[0] = n;
        this.directions[1] = ne;
        this.directions[2] = e;
        this.directions[3] = se;
        this.directions[4] = s;
        this.directions[5] = sw;
        this.directions[6] = w;
        this.directions[7] = nw;
        this.bDoFlip = false;
    }

    public IsoDirectionFrame(Texture n, Texture s, Texture e, Texture w) {
        this.directions[0] = n;
        this.directions[1] = n;
        this.directions[2] = w;
        this.directions[3] = w;
        this.directions[4] = s;
        this.directions[5] = s;
        this.directions[6] = e;
        this.directions[7] = e;
        this.bDoFlip = false;
    }

    public Texture getTexture(IsoDirections dir) {
        return this.directions[dir.index()];
    }

    public void SetAllDirections(Texture tex) {
        this.directions[0] = tex;
        this.directions[1] = tex;
        this.directions[2] = tex;
        this.directions[3] = tex;
        this.directions[4] = tex;
        this.directions[5] = tex;
        this.directions[6] = tex;
        this.directions[7] = tex;
    }

    public void SetDirection(Texture tex, IsoDirections dir) {
        this.directions[dir.index()] = tex;
    }

    public void render(float sx, float sy, IsoDirections dir, ColorInfo info, boolean Flip, Consumer<TextureDraw> texdModifier) {
        Texture texture = this.directions[dir.index()];
        if (texture != null) {
            if (Flip) {
                texture.flip = !texture.flip;
            }

            if (texture != null) {
                if (!this.bDoFlip) {
                    texture.flip = false;
                }

                texture.render(sx, sy, texture.getWidth(), texture.getHeight(), info.r, info.g, info.b, info.a, texdModifier);
                texture.flip = false;
            }
        }
    }

    void render(
        float float0,
        float float1,
        float float2,
        float float3,
        IsoDirections directionsx,
        ColorInfo colorInfo,
        boolean boolean0,
        Consumer<TextureDraw> consumer
    ) {
        Texture texture = this.directions[directionsx.index()];
        if (texture != null) {
            if (boolean0) {
                texture.flip = !texture.flip;
            }

            if (!this.bDoFlip) {
                texture.flip = false;
            }

            texture.render(float0, float1, float2, float3, colorInfo.r, colorInfo.g, colorInfo.b, colorInfo.a, consumer);
            texture.flip = false;
        }
    }

    void render(
        ObjectRenderEffects objectRenderEffects,
        float float0,
        float float1,
        float float2,
        float float3,
        IsoDirections directionsx,
        ColorInfo colorInfo,
        boolean boolean0,
        Consumer<TextureDraw> consumer
    ) {
        Texture texture = this.directions[directionsx.index()];
        if (texture != null) {
            if (boolean0) {
                texture.flip = !texture.flip;
            }

            if (!this.bDoFlip) {
                texture.flip = false;
            }

            texture.render(objectRenderEffects, float0, float1, float2, float3, colorInfo.r, colorInfo.g, colorInfo.b, colorInfo.a, consumer);
            texture.flip = false;
        }
    }

    public void renderexplicit(int sx, int sy, IsoDirections dir, float scale) {
        this.renderexplicit(sx, sy, dir, scale, null);
    }

    public void renderexplicit(int sx, int sy, IsoDirections dir, float scale, ColorInfo color) {
        Texture texture = this.directions[dir.index()];
        if (texture != null) {
            float float0 = 1.0F;
            float float1 = 1.0F;
            float float2 = 1.0F;
            float float3 = 1.0F;
            if (color != null) {
                float0 *= color.a;
                float1 *= color.r;
                float2 *= color.g;
                float3 *= color.b;
            }

            texture.renderstrip(sx, sy, (int)(texture.getWidth() * scale), (int)(texture.getHeight() * scale), float1, float2, float3, float0, null);
        }
    }
}
