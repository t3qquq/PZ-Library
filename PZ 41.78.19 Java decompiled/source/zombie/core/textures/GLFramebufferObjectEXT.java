// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

import org.lwjgl.opengl.EXTFramebufferObject;

public final class GLFramebufferObjectEXT implements IGLFramebufferObject {
    @Override
    public int GL_FRAMEBUFFER() {
        return 36160;
    }

    @Override
    public int GL_RENDERBUFFER() {
        return 36161;
    }

    @Override
    public int GL_COLOR_ATTACHMENT0() {
        return 36064;
    }

    @Override
    public int GL_DEPTH_ATTACHMENT() {
        return 36096;
    }

    @Override
    public int GL_STENCIL_ATTACHMENT() {
        return 36128;
    }

    @Override
    public int GL_DEPTH_STENCIL() {
        return 34041;
    }

    @Override
    public int GL_DEPTH24_STENCIL8() {
        return 35056;
    }

    @Override
    public int GL_FRAMEBUFFER_COMPLETE() {
        return 36053;
    }

    @Override
    public int GL_FRAMEBUFFER_UNDEFINED() {
        return 0;
    }

    @Override
    public int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT() {
        return 36054;
    }

    @Override
    public int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT() {
        return 36055;
    }

    @Override
    public int GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS() {
        return 36057;
    }

    @Override
    public int GL_FRAMEBUFFER_INCOMPLETE_FORMATS() {
        return 36058;
    }

    @Override
    public int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER() {
        return 36059;
    }

    @Override
    public int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER() {
        return 36060;
    }

    @Override
    public int GL_FRAMEBUFFER_UNSUPPORTED() {
        return 36061;
    }

    @Override
    public int GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE() {
        return 0;
    }

    @Override
    public int glGenFramebuffers() {
        return EXTFramebufferObject.glGenFramebuffersEXT();
    }

    @Override
    public void glBindFramebuffer(int int0, int int1) {
        EXTFramebufferObject.glBindFramebufferEXT(int0, int1);
    }

    @Override
    public void glFramebufferTexture2D(int int0, int int1, int int2, int int3, int int4) {
        EXTFramebufferObject.glFramebufferTexture2DEXT(int0, int1, int2, int3, int4);
    }

    @Override
    public int glGenRenderbuffers() {
        return EXTFramebufferObject.glGenRenderbuffersEXT();
    }

    @Override
    public void glBindRenderbuffer(int int0, int int1) {
        EXTFramebufferObject.glBindRenderbufferEXT(int0, int1);
    }

    @Override
    public void glRenderbufferStorage(int int0, int int1, int int2, int int3) {
        EXTFramebufferObject.glRenderbufferStorageEXT(int0, int1, int2, int3);
    }

    @Override
    public void glFramebufferRenderbuffer(int int0, int int1, int int2, int int3) {
        EXTFramebufferObject.glFramebufferRenderbufferEXT(int0, int1, int2, int3);
    }

    @Override
    public int glCheckFramebufferStatus(int int0) {
        return EXTFramebufferObject.glCheckFramebufferStatusEXT(int0);
    }

    @Override
    public void glDeleteFramebuffers(int int0) {
        EXTFramebufferObject.glDeleteFramebuffersEXT(int0);
    }

    @Override
    public void glDeleteRenderbuffers(int int0) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(int0);
    }
}
