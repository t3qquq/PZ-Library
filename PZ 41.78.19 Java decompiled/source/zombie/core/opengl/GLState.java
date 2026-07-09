// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.opengl;

import zombie.core.SpriteRenderer;
import zombie.util.Type;

public final class GLState {
    public static final GLState.CAlphaFunc AlphaFunc = new GLState.CAlphaFunc();
    public static final GLState.CAlphaTest AlphaTest = new GLState.CAlphaTest();
    public static final GLState.CBlendFunc BlendFunc = new GLState.CBlendFunc();
    public static final GLState.CBlendFuncSeparate BlendFuncSeparate = new GLState.CBlendFuncSeparate();
    public static final GLState.CColorMask ColorMask = new GLState.CColorMask();
    public static final GLState.CStencilFunc StencilFunc = new GLState.CStencilFunc();
    public static final GLState.CStencilMask StencilMask = new GLState.CStencilMask();
    public static final GLState.CStencilOp StencilOp = new GLState.CStencilOp();
    public static final GLState.CStencilTest StencilTest = new GLState.CStencilTest();

    public static void startFrame() {
        AlphaFunc.setDirty();
        AlphaTest.setDirty();
        BlendFunc.setDirty();
        BlendFuncSeparate.setDirty();
        ColorMask.setDirty();
        StencilFunc.setDirty();
        StencilMask.setDirty();
        StencilOp.setDirty();
        StencilTest.setDirty();
    }

    public abstract static class Base2Ints extends IOpenGLState<GLState.C2IntsValue> {
        GLState.C2IntsValue defaultValue() {
            return new GLState.C2IntsValue();
        }
    }

    public abstract static class Base3Ints extends IOpenGLState<GLState.C3IntsValue> {
        GLState.C3IntsValue defaultValue() {
            return new GLState.C3IntsValue();
        }
    }

    public abstract static class Base4Booleans extends IOpenGLState<GLState.C4BooleansValue> {
        GLState.C4BooleansValue defaultValue() {
            return new GLState.C4BooleansValue();
        }
    }

    public abstract static class Base4Ints extends IOpenGLState<GLState.C4IntsValue> {
        GLState.C4IntsValue defaultValue() {
            return new GLState.C4IntsValue();
        }
    }

    public abstract static class BaseBoolean extends IOpenGLState<GLState.CBooleanValue> {
        GLState.CBooleanValue defaultValue() {
            return new GLState.CBooleanValue(true);
        }
    }

    public abstract static class BaseInt extends IOpenGLState<GLState.CIntValue> {
        GLState.CIntValue defaultValue() {
            return new GLState.CIntValue();
        }
    }

    public abstract static class BaseIntFloat extends IOpenGLState<GLState.CIntFloatValue> {
        GLState.CIntFloatValue defaultValue() {
            return new GLState.CIntFloatValue();
        }
    }

    public static final class C2IntsValue implements IOpenGLState.Value {
        int a;
        int b;

        public GLState.C2IntsValue set(int int0, int int1) {
            this.a = int0;
            this.b = int1;
            return this;
        }

        @Override
        public boolean equals(Object object) {
            GLState.C2IntsValue c2IntsValue0 = Type.tryCastTo(object, GLState.C2IntsValue.class);
            return c2IntsValue0 != null && c2IntsValue0.a == this.a && c2IntsValue0.b == this.b;
        }

        @Override
        public IOpenGLState.Value set(IOpenGLState.Value value) {
            GLState.C2IntsValue c2IntsValue0 = (GLState.C2IntsValue)value;
            this.a = c2IntsValue0.a;
            this.b = c2IntsValue0.b;
            return this;
        }
    }

    public static final class C3IntsValue implements IOpenGLState.Value {
        int a;
        int b;
        int c;

        public GLState.C3IntsValue set(int int0, int int1, int int2) {
            this.a = int0;
            this.b = int1;
            this.c = int2;
            return this;
        }

        @Override
        public boolean equals(Object object) {
            GLState.C3IntsValue c3IntsValue0 = Type.tryCastTo(object, GLState.C3IntsValue.class);
            return c3IntsValue0 != null && c3IntsValue0.a == this.a && c3IntsValue0.b == this.b && c3IntsValue0.c == this.c;
        }

        @Override
        public IOpenGLState.Value set(IOpenGLState.Value value) {
            GLState.C3IntsValue c3IntsValue0 = (GLState.C3IntsValue)value;
            this.a = c3IntsValue0.a;
            this.b = c3IntsValue0.b;
            this.c = c3IntsValue0.c;
            return this;
        }
    }

    public static final class C4BooleansValue implements IOpenGLState.Value {
        boolean a;
        boolean b;
        boolean c;
        boolean d;

        public GLState.C4BooleansValue set(boolean boolean0, boolean boolean1, boolean boolean2, boolean boolean3) {
            this.a = boolean0;
            this.b = boolean1;
            this.c = boolean2;
            this.d = boolean3;
            return this;
        }

        @Override
        public boolean equals(Object object) {
            GLState.C4BooleansValue c4BooleansValue0 = Type.tryCastTo(object, GLState.C4BooleansValue.class);
            return c4BooleansValue0 != null && c4BooleansValue0.a == this.a && c4BooleansValue0.b == this.b && c4BooleansValue0.c == this.c;
        }

        @Override
        public IOpenGLState.Value set(IOpenGLState.Value value) {
            GLState.C4BooleansValue c4BooleansValue0 = (GLState.C4BooleansValue)value;
            this.a = c4BooleansValue0.a;
            this.b = c4BooleansValue0.b;
            this.c = c4BooleansValue0.c;
            this.d = c4BooleansValue0.d;
            return this;
        }
    }

    public static final class C4IntsValue implements IOpenGLState.Value {
        int a;
        int b;
        int c;
        int d;

        public GLState.C4IntsValue set(int int0, int int1, int int2, int int3) {
            this.a = int0;
            this.b = int1;
            this.c = int2;
            this.d = int3;
            return this;
        }

        @Override
        public boolean equals(Object object) {
            GLState.C4IntsValue c4IntsValue0 = Type.tryCastTo(object, GLState.C4IntsValue.class);
            return c4IntsValue0 != null && c4IntsValue0.a == this.a && c4IntsValue0.b == this.b && c4IntsValue0.c == this.c && c4IntsValue0.d == this.d;
        }

        @Override
        public IOpenGLState.Value set(IOpenGLState.Value value) {
            GLState.C4IntsValue c4IntsValue0 = (GLState.C4IntsValue)value;
            this.a = c4IntsValue0.a;
            this.b = c4IntsValue0.b;
            this.c = c4IntsValue0.c;
            this.d = c4IntsValue0.d;
            return this;
        }
    }

    public static final class CAlphaFunc extends GLState.BaseIntFloat {
        void Set(GLState.CIntFloatValue cIntFloatValue) {
            SpriteRenderer.instance.glAlphaFunc(cIntFloatValue.a, cIntFloatValue.b);
        }
    }

    public static final class CAlphaTest extends GLState.BaseBoolean {
        void Set(GLState.CBooleanValue cBooleanValue) {
            if (cBooleanValue.value) {
                SpriteRenderer.instance.glEnable(3008);
            } else {
                SpriteRenderer.instance.glDisable(3008);
            }
        }
    }

    public static final class CBlendFunc extends GLState.Base2Ints {
        void Set(GLState.C2IntsValue c2IntsValue) {
            SpriteRenderer.instance.glBlendFunc(c2IntsValue.a, c2IntsValue.b);
        }
    }

    public static final class CBlendFuncSeparate extends GLState.Base4Ints {
        void Set(GLState.C4IntsValue c4IntsValue) {
            SpriteRenderer.instance.glBlendFuncSeparate(c4IntsValue.a, c4IntsValue.b, c4IntsValue.c, c4IntsValue.d);
        }
    }

    public static class CBooleanValue implements IOpenGLState.Value {
        public static final GLState.CBooleanValue TRUE = new GLState.CBooleanValue(true);
        public static final GLState.CBooleanValue FALSE = new GLState.CBooleanValue(false);
        boolean value;

        CBooleanValue(boolean boolean0) {
            this.value = boolean0;
        }

        @Override
        public boolean equals(Object object) {
            return object instanceof GLState.CBooleanValue && ((GLState.CBooleanValue)object).value == this.value;
        }

        @Override
        public IOpenGLState.Value set(IOpenGLState.Value valuex) {
            this.value = ((GLState.CBooleanValue)valuex).value;
            return this;
        }
    }

    public static final class CColorMask extends GLState.Base4Booleans {
        void Set(GLState.C4BooleansValue c4BooleansValue) {
            SpriteRenderer.instance.glColorMask(c4BooleansValue.a ? 1 : 0, c4BooleansValue.b ? 1 : 0, c4BooleansValue.c ? 1 : 0, c4BooleansValue.d ? 1 : 0);
        }
    }

    public static final class CIntFloatValue implements IOpenGLState.Value {
        int a;
        float b;

        public GLState.CIntFloatValue set(int int0, float float0) {
            this.a = int0;
            this.b = float0;
            return this;
        }

        @Override
        public boolean equals(Object object) {
            GLState.CIntFloatValue cIntFloatValue0 = Type.tryCastTo(object, GLState.CIntFloatValue.class);
            return cIntFloatValue0 != null && cIntFloatValue0.a == this.a && cIntFloatValue0.b == this.b;
        }

        @Override
        public IOpenGLState.Value set(IOpenGLState.Value value) {
            GLState.CIntFloatValue cIntFloatValue0 = (GLState.CIntFloatValue)value;
            this.a = cIntFloatValue0.a;
            this.b = cIntFloatValue0.b;
            return this;
        }
    }

    public static class CIntValue implements IOpenGLState.Value {
        int value;

        public GLState.CIntValue set(int int0) {
            this.value = int0;
            return this;
        }

        @Override
        public boolean equals(Object object) {
            return object instanceof GLState.CIntValue && ((GLState.CIntValue)object).value == this.value;
        }

        @Override
        public IOpenGLState.Value set(IOpenGLState.Value valuex) {
            this.value = ((GLState.CIntValue)valuex).value;
            return this;
        }
    }

    public static final class CStencilFunc extends GLState.Base3Ints {
        void Set(GLState.C3IntsValue c3IntsValue) {
            SpriteRenderer.instance.glStencilFunc(c3IntsValue.a, c3IntsValue.b, c3IntsValue.c);
        }
    }

    public static final class CStencilMask extends GLState.BaseInt {
        void Set(GLState.CIntValue cIntValue) {
            SpriteRenderer.instance.glStencilMask(cIntValue.value);
        }
    }

    public static final class CStencilOp extends GLState.Base3Ints {
        void Set(GLState.C3IntsValue c3IntsValue) {
            SpriteRenderer.instance.glStencilOp(c3IntsValue.a, c3IntsValue.b, c3IntsValue.c);
        }
    }

    public static final class CStencilTest extends GLState.BaseBoolean {
        void Set(GLState.CBooleanValue cBooleanValue) {
            if (cBooleanValue.value) {
                SpriteRenderer.instance.glEnable(2960);
            } else {
                SpriteRenderer.instance.glDisable(2960);
            }
        }
    }
}
