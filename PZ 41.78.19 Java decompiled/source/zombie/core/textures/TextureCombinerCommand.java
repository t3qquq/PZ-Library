// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

import java.util.ArrayList;
import zombie.core.opengl.SmartShader;
import zombie.popman.ObjectPool;
import zombie.util.list.PZArrayUtil;

public final class TextureCombinerCommand {
    public static final int DEFAULT_SRC_A = 1;
    public static final int DEFAULT_DST_A = 771;
    public int x = -1;
    public int y = -1;
    public int w = -1;
    public int h = -1;
    public Texture mask;
    public Texture tex;
    public int blendSrc;
    public int blendDest;
    public int blendSrcA;
    public int blendDestA;
    public SmartShader shader;
    public ArrayList<TextureCombinerShaderParam> shaderParams = null;
    public static final ObjectPool<TextureCombinerCommand> pool = new ObjectPool<>(TextureCombinerCommand::new);

    @Override
    public String toString() {
        String string = System.lineSeparator();
        return "{"
            + string
            + "\tpos: "
            + this.x
            + ","
            + this.y
            + string
            + "\tsize: "
            + this.w
            + ","
            + this.h
            + string
            + "\tmask:"
            + this.mask
            + string
            + "\ttex:"
            + this.tex
            + string
            + "\tblendSrc:"
            + this.blendSrc
            + string
            + "\tblendDest:"
            + this.blendDest
            + string
            + "\tblendSrcA:"
            + this.blendSrcA
            + string
            + "\tblendDestA:"
            + this.blendDestA
            + string
            + "\tshader:"
            + this.shader
            + string
            + "\tshaderParams:"
            + PZArrayUtil.arrayToString(this.shaderParams)
            + string
            + "}";
    }

    public TextureCombinerCommand init(Texture texture) {
        this.tex = this.requireNonNull(texture);
        this.blendSrc = 770;
        this.blendDest = 771;
        this.blendSrcA = 1;
        this.blendDestA = 771;
        return this;
    }

    public TextureCombinerCommand initSeparate(Texture texture, int int0, int int1, int int2, int int3) {
        this.tex = this.requireNonNull(texture);
        this.blendSrc = int0;
        this.blendDest = int1;
        this.blendSrcA = int2;
        this.blendDestA = int3;
        return this;
    }

    public TextureCombinerCommand init(Texture texture, int int0, int int1) {
        return this.initSeparate(texture, int0, int1, 1, 771);
    }

    public TextureCombinerCommand init(Texture texture, SmartShader smartShader) {
        this.tex = this.requireNonNull(texture);
        this.shader = smartShader;
        this.blendSrc = 770;
        this.blendDest = 771;
        this.blendSrcA = 1;
        this.blendDestA = 771;
        return this;
    }

    public TextureCombinerCommand init(Texture texture0, SmartShader smartShader, Texture texture1, int int0, int int1) {
        this.tex = this.requireNonNull(texture0);
        this.shader = smartShader;
        this.blendSrc = int0;
        this.blendDest = int1;
        this.blendSrcA = 1;
        this.blendDestA = 771;
        this.mask = this.requireNonNull(texture1);
        return this;
    }

    public TextureCombinerCommand init(Texture texture, int int0, int int1, int int2, int int3) {
        this.tex = this.requireNonNull(texture);
        this.x = int0;
        this.y = int1;
        this.w = int2;
        this.h = int3;
        this.blendSrc = 770;
        this.blendDest = 771;
        this.blendSrcA = 1;
        this.blendDestA = 771;
        return this;
    }

    public TextureCombinerCommand initSeparate(
        Texture texture0, SmartShader smartShader, ArrayList<TextureCombinerShaderParam> arrayList, Texture texture1, int int0, int int1, int int2, int int3
    ) {
        this.tex = this.requireNonNull(texture0);
        this.shader = smartShader;
        this.blendSrc = int0;
        this.blendDest = int1;
        this.blendSrcA = int2;
        this.blendDestA = int3;
        this.mask = this.requireNonNull(texture1);
        if (this.shaderParams == null) {
            this.shaderParams = new ArrayList<>();
        }

        this.shaderParams.clear();
        this.shaderParams.addAll(arrayList);
        return this;
    }

    public TextureCombinerCommand init(
        Texture texture0, SmartShader smartShader, ArrayList<TextureCombinerShaderParam> arrayList, Texture texture1, int int0, int int1
    ) {
        return this.initSeparate(texture0, smartShader, arrayList, texture1, int0, int1, 1, 771);
    }

    public TextureCombinerCommand init(Texture texture, SmartShader smartShader, ArrayList<TextureCombinerShaderParam> arrayList) {
        this.tex = this.requireNonNull(texture);
        this.blendSrc = 770;
        this.blendDest = 771;
        this.blendSrcA = 1;
        this.blendDestA = 771;
        this.shader = smartShader;
        if (this.shaderParams == null) {
            this.shaderParams = new ArrayList<>();
        }

        this.shaderParams.clear();
        this.shaderParams.addAll(arrayList);
        return this;
    }

    private Texture requireNonNull(Texture texture) {
        return texture == null ? Texture.getErrorTexture() : texture;
    }

    public static TextureCombinerCommand get() {
        TextureCombinerCommand textureCombinerCommand = pool.alloc();
        textureCombinerCommand.x = -1;
        textureCombinerCommand.tex = null;
        textureCombinerCommand.mask = null;
        textureCombinerCommand.shader = null;
        if (textureCombinerCommand.shaderParams != null) {
            textureCombinerCommand.shaderParams.clear();
        }

        return textureCombinerCommand;
    }
}
