// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

import org.lwjgl.opengl.ARBFramebufferObject;

public final class GLFramebufferObjectARB implements IGLFramebufferObject {
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
        return 33305;
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
        return 0;
    }

    @Override
    public int GL_FRAMEBUFFER_INCOMPLETE_FORMATS() {
        return 0;
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
        return 36182;
    }

    @Override
    public int glGenFramebuffers() {
        return ARBFramebufferObject.glGenFramebuffers();
    }

    @Override
    public void glBindFramebuffer(int int0, int int1) {
        ARBFramebufferObject.glBindFramebuffer(int0, int1);
    }

    @Override
    public void glFramebufferTexture2D(int int0, int int1, int int2, int int3, int int4) {
        ARBFramebufferObject.glFramebufferTexture2D(int0, int1, int2, int3, int4);
    }

    @Override
    public int glGenRenderbuffers() {
        return ARBFramebufferObject.glGenRenderbuffers();
    }

    @Override
    public void glBindRenderbuffer(int int0, int int1) {
        ARBFramebufferObject.glBindRenderbuffer(int0, int1);
    }

    @Override
    public void glRenderbufferStorage(int int0, int int1, int int2, int int3) {
        ARBFramebufferObject.glRenderbufferStorage(int0, int1, int2, int3);
    }

    @Override
    public void glFramebufferRenderbuffer(int int0, int int1, int int2, int int3) {
        ARBFramebufferObject.glFramebufferRenderbuffer(int0, int1, int2, int3);
    }

    @Override
    public int glCheckFramebufferStatus(int int0) {
        return ARBFramebufferObject.glCheckFramebufferStatus(int0);
    }

    @Override
    public void glDeleteFramebuffers(int int0) {
        ARBFramebufferObject.glDeleteFramebuffers(int0);
    }

    @Override
    public void glDeleteRenderbuffers(int int0) {
        ARBFramebufferObject.glDeleteRenderbuffers(int0);
    }
}
