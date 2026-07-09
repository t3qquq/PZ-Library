// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.util.Stack;
import org.lwjgl.opengl.GL11;
import zombie.core.SpriteRenderer;
import zombie.core.math.Vector4;
import zombie.core.opengl.GLState;
import zombie.core.opengl.Shader;
import zombie.core.opengl.ShaderProgram;
import zombie.core.textures.Texture;
import zombie.iso.IsoCamera;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import zombie.util.Lambda;
import zombie.util.lambda.Invokers;

public final class IndieGL {
    public static int nCount = 0;
    private static final GLState.CIntValue tempInt = new GLState.CIntValue();
    private static final GLState.C2IntsValue temp2Ints = new GLState.C2IntsValue();
    private static final GLState.C3IntsValue temp3Ints = new GLState.C3IntsValue();
    private static final GLState.C4IntsValue temp4Ints = new GLState.C4IntsValue();
    private static final GLState.C4BooleansValue temp4Booleans = new GLState.C4BooleansValue();
    private static final GLState.CIntFloatValue tempIntFloat = new GLState.CIntFloatValue();
    private static final Stack<ShaderStackEntry> m_shaderStack = new Stack<>();

    public static void glBlendFunc(int int0, int int1) {
        if (SpriteRenderer.instance != null && SpriteRenderer.GL_BLENDFUNC_ENABLED) {
            GLState.BlendFuncSeparate.set(temp4Ints.set(int0, int1, int0, int1));
        }
    }

    public static void glBlendFuncSeparate(int int0, int int1, int int2, int int3) {
        if (SpriteRenderer.instance != null && SpriteRenderer.GL_BLENDFUNC_ENABLED) {
            GLState.BlendFuncSeparate.set(temp4Ints.set(int0, int1, int2, int3));
        }
    }

    public static void StartShader(Shader shader) {
        int int0 = IsoCamera.frameState.playerIndex;
        StartShader(shader, int0);
    }

    public static void StartShader(Shader shader, int int0) {
        if (shader != null) {
            StartShader(shader.getID(), int0);
        } else {
            EndShader();
        }
    }

    public static void StartShader(int int1) {
        int int0 = IsoCamera.frameState.playerIndex;
        StartShader(int1, int0);
    }

    public static void StartShader(int int0, int int1) {
        SpriteRenderer.instance.StartShader(int0, int1);
    }

    public static void EndShader() {
        SpriteRenderer.instance.EndShader();
    }

    public static void pushShader(Shader shader) {
        int int0 = IsoCamera.frameState.playerIndex;
        m_shaderStack.push(ShaderStackEntry.alloc(shader, int0));
        StartShader(shader, int0);
    }

    public static void popShader(Shader shader) {
        if (m_shaderStack.isEmpty()) {
            throw new RuntimeException("Push/PopShader mismatch. Cannot pop. Stack is empty.");
        } else if (m_shaderStack.peek().getShader() != shader) {
            throw new RuntimeException("Push/PopShader mismatch. The popped shader != the pushed shader.");
        } else {
            ShaderStackEntry shaderStackEntry0 = m_shaderStack.pop();
            shaderStackEntry0.release();
            if (m_shaderStack.isEmpty()) {
                EndShader();
            } else {
                ShaderStackEntry shaderStackEntry1 = m_shaderStack.peek();
                StartShader(shaderStackEntry1.getShader(), shaderStackEntry1.getPlayerIndex());
            }
        }
    }

    public static void bindShader(Shader shader, Runnable runnable) {
        pushShader(shader);

        try {
            runnable.run();
        } finally {
            popShader(shader);
        }
    }

    public static <T1> void bindShader(Shader shader, T1 object, Invokers.Params1.ICallback<T1> iCallback) {
        Lambda.capture(
            shader, object, iCallback, (genericStack, shaderx, objectx, iCallbackx) -> bindShader(shaderx, genericStack.invoker(objectx, iCallbackx))
        );
    }

    public static <T1, T2> void bindShader(Shader shader, T1 object0, T2 object1, Invokers.Params2.ICallback<T1, T2> iCallback) {
        Lambda.capture(
            shader,
            object0,
            object1,
            iCallback,
            (genericStack, shaderx, object0x, object1x, iCallbackx) -> bindShader(shaderx, genericStack.invoker(object0x, object1x, iCallbackx))
        );
    }

    public static <T1, T2, T3> void bindShader(Shader shader, T1 object0, T2 object1, T3 object2, Invokers.Params3.ICallback<T1, T2, T3> iCallback) {
        Lambda.capture(
            shader,
            object0,
            object1,
            object2,
            iCallback,
            (genericStack, shaderx, object0x, object1x, object2x, iCallbackx) -> bindShader(
                shaderx, genericStack.invoker(object0x, object1x, object2x, iCallbackx)
            )
        );
    }

    public static <T1, T2, T3, T4> void bindShader(
        Shader shader, T1 object0, T2 object1, T3 object2, T4 object3, Invokers.Params4.ICallback<T1, T2, T3, T4> iCallback
    ) {
        Lambda.capture(
            shader,
            object0,
            object1,
            object2,
            object3,
            iCallback,
            (genericStack, shaderx, object0x, object1x, object2x, object3x, iCallbackx) -> bindShader(
                shaderx, genericStack.invoker(object0x, object1x, object2x, object3x, iCallbackx)
            )
        );
    }

    private static ShaderProgram.Uniform getShaderUniform(Shader shader, String string, int int0) {
        if (shader == null) {
            return null;
        } else {
            ShaderProgram shaderProgram = shader.getProgram();
            return shaderProgram == null ? null : shaderProgram.getUniform(string, int0, false);
        }
    }

    public static void shaderSetSamplerUnit(Shader shader, String string, int int0) {
        ShaderProgram.Uniform uniform = getShaderUniform(shader, string, 35678);
        if (uniform != null) {
            uniform.sampler = int0;
            ShaderUpdate1i(shader.getID(), uniform.loc, int0);
        }
    }

    public static void shaderSetValue(Shader shader, String string, float float0) {
        ShaderProgram.Uniform uniform = getShaderUniform(shader, string, 5126);
        if (uniform != null) {
            ShaderUpdate1f(shader.getID(), uniform.loc, float0);
        }
    }

    public static void shaderSetValue(Shader shader, String string, int int0) {
        ShaderProgram.Uniform uniform = getShaderUniform(shader, string, 5124);
        if (uniform != null) {
            ShaderUpdate1i(shader.getID(), uniform.loc, int0);
        }
    }

    public static void shaderSetValue(Shader shader, String string, Vector2 vector) {
        shaderSetVector2(shader, string, vector.x, vector.y);
    }

    public static void shaderSetValue(Shader shader, String string, Vector3 vector) {
        shaderSetVector3(shader, string, vector.x, vector.y, vector.z);
    }

    public static void shaderSetValue(Shader shader, String string, Vector4 vector4) {
        shaderSetVector4(shader, string, vector4.x, vector4.y, vector4.z, vector4.w);
    }

    public static void shaderSetVector2(Shader shader, String string, float float0, float float1) {
        ShaderProgram.Uniform uniform = getShaderUniform(shader, string, 35664);
        if (uniform != null) {
            ShaderUpdate2f(shader.getID(), uniform.loc, float0, float1);
        }
    }

    public static void shaderSetVector3(Shader shader, String string, float float0, float float1, float float2) {
        ShaderProgram.Uniform uniform = getShaderUniform(shader, string, 35665);
        if (uniform != null) {
            ShaderUpdate3f(shader.getID(), uniform.loc, float0, float1, float2);
        }
    }

    public static void shaderSetVector4(Shader shader, String string, float float0, float float1, float float2, float float3) {
        ShaderProgram.Uniform uniform = getShaderUniform(shader, string, 35666);
        if (uniform != null) {
            ShaderUpdate4f(shader.getID(), uniform.loc, float0, float1, float2, float3);
        }
    }

    public static void ShaderUpdate1i(int int0, int int1, int int2) {
        SpriteRenderer.instance.ShaderUpdate1i(int0, int1, int2);
    }

    public static void ShaderUpdate1f(int int0, int int1, float float0) {
        SpriteRenderer.instance.ShaderUpdate1f(int0, int1, float0);
    }

    public static void ShaderUpdate2f(int int0, int int1, float float0, float float1) {
        SpriteRenderer.instance.ShaderUpdate2f(int0, int1, float0, float1);
    }

    public static void ShaderUpdate3f(int int0, int int1, float float0, float float1, float float2) {
        SpriteRenderer.instance.ShaderUpdate3f(int0, int1, float0, float1, float2);
    }

    public static void ShaderUpdate4f(int int0, int int1, float float0, float float1, float float2, float float3) {
        SpriteRenderer.instance.ShaderUpdate4f(int0, int1, float0, float1, float2, float3);
    }

    public static void glBlendFuncA(int int0, int int1) {
        GL11.glBlendFunc(int0, int1);
    }

    public static void glEnable(int int0) {
        SpriteRenderer.instance.glEnable(int0);
    }

    public static void glDoStartFrame(int int0, int int1, float float0, int int2) {
        glDoStartFrame(int0, int1, float0, int2, false);
    }

    public static void glDoStartFrame(int int0, int int1, float float0, int int2, boolean boolean0) {
        SpriteRenderer.instance.glDoStartFrame(int0, int1, float0, int2, boolean0);
    }

    public static void glDoEndFrame() {
        SpriteRenderer.instance.glDoEndFrame();
    }

    public static void glColorMask(boolean boolean0, boolean boolean1, boolean boolean2, boolean boolean3) {
        GLState.ColorMask.set(temp4Booleans.set(boolean0, boolean1, boolean2, boolean3));
    }

    public static void glColorMaskA(boolean boolean0, boolean var1, boolean var2, boolean boolean1) {
        GL11.glColorMask(boolean0, boolean0, boolean1, boolean1);
    }

    public static void glEnableA(int int0) {
        GL11.glEnable(int0);
    }

    public static void glAlphaFunc(int int0, float float0) {
        GLState.AlphaFunc.set(tempIntFloat.set(int0, float0));
    }

    public static void glAlphaFuncA(int int0, float float0) {
        GL11.glAlphaFunc(int0, float0);
    }

    public static void glStencilFunc(int int0, int int1, int int2) {
        GLState.StencilFunc.set(temp3Ints.set(int0, int1, int2));
    }

    public static void glStencilFuncA(int int0, int int1, int int2) {
        GL11.glStencilFunc(int0, int1, int2);
    }

    public static void glStencilOp(int int0, int int1, int int2) {
        GLState.StencilOp.set(temp3Ints.set(int0, int1, int2));
    }

    public static void glStencilOpA(int int0, int int1, int int2) {
        GL11.glStencilOp(int0, int1, int2);
    }

    public static void glTexParameteri(int int0, int int1, int int2) {
        SpriteRenderer.instance.glTexParameteri(int0, int1, int2);
    }

    public static void glTexParameteriActual(int int0, int int1, int int2) {
        GL11.glTexParameteri(int0, int1, int2);
    }

    public static void glStencilMask(int int0) {
        GLState.StencilMask.set(tempInt.set(int0));
    }

    public static void glStencilMaskA(int int0) {
        GL11.glStencilMask(int0);
    }

    public static void glDisable(int int0) {
        SpriteRenderer.instance.glDisable(int0);
    }

    public static void glClear(int int0) {
        SpriteRenderer.instance.glClear(int0);
    }

    public static void glClearA(int int0) {
        GL11.glClear(int0);
    }

    public static void glDisableA(int int0) {
        GL11.glDisable(int0);
    }

    public static void glLoadIdentity() {
        SpriteRenderer.instance.glLoadIdentity();
    }

    public static void glBind(Texture texture) {
        SpriteRenderer.instance.glBind(texture.getID());
    }

    public static void enableAlphaTest() {
        GLState.AlphaTest.set(GLState.CBooleanValue.TRUE);
    }

    public static void disableAlphaTest() {
        GLState.AlphaTest.set(GLState.CBooleanValue.FALSE);
    }

    public static void enableStencilTest() {
        GLState.StencilTest.set(GLState.CBooleanValue.TRUE);
    }

    public static void disableStencilTest() {
        GLState.StencilTest.set(GLState.CBooleanValue.FALSE);
    }

    public static boolean isMaxZoomLevel() {
        return SpriteRenderer.instance.isMaxZoomLevel();
    }

    public static boolean isMinZoomLevel() {
        return SpriteRenderer.instance.isMinZoomLevel();
    }
}
