// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.opengl;

import java.io.PrintStream;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjglx.opengl.OpenGLException;
import org.lwjglx.opengl.Util;
import zombie.core.skinnedmodel.model.Model;

public class PZGLUtil {
    static int test = 0;

    public static void checkGLErrorThrow(String string, Object... objects) throws OpenGLException {
        int int0 = GL11.glGetError();
        if (int0 != 0) {
            test++;
            throw new OpenGLException(createErrorMessage(int0, string, objects));
        }
    }

    private static String createErrorMessage(int int0, String string1, Object... objects) {
        String string0 = System.lineSeparator();
        return "  GL Error code ("
            + int0
            + ") encountered."
            + string0
            + "  Error translation: "
            + createErrorMessage(int0)
            + string0
            + "  While performing: "
            + String.format(string1, objects);
    }

    private static String createErrorMessage(int int0) {
        String string = Util.translateGLErrorString(int0);
        return string + " (" + int0 + ")";
    }

    public static boolean checkGLError(boolean boolean0) {
        try {
            Util.checkGLError();
            return true;
        } catch (OpenGLException openGLException) {
            RenderThread.logGLException(openGLException, boolean0);
            return false;
        }
    }

    public static void printGLState(PrintStream printStream) {
        int int0 = GL11.glGetInteger(2979);
        printStream.println("DEBUG: GL_MODELVIEW_STACK_DEPTH= " + int0);
        int0 = GL11.glGetInteger(2980);
        printStream.println("DEBUG: GL_PROJECTION_STACK_DEPTH= " + int0);
        int0 = GL11.glGetInteger(2981);
        printStream.println("DEBUG: GL_TEXTURE_STACK_DEPTH= " + int0);
        int0 = GL11.glGetInteger(2992);
        printStream.println("DEBUG: GL_ATTRIB_STACK_DEPTH= " + int0);
        int0 = GL11.glGetInteger(2993);
        printStream.println("DEBUG: GL_CLIENT_ATTRIB_STACK_DEPTH= " + int0);
        int0 = GL11.glGetInteger(3381);
        printStream.println("DEBUG: GL_MAX_ATTRIB_STACK_DEPTH= " + int0);
        int0 = GL11.glGetInteger(3382);
        printStream.println("DEBUG: GL_MAX_MODELVIEW_STACK_DEPTH= " + int0);
        int0 = GL11.glGetInteger(3383);
        printStream.println("DEBUG: GL_MAX_NAME_STACK_DEPTH= " + int0);
        int0 = GL11.glGetInteger(3384);
        printStream.println("DEBUG: GL_MAX_PROJECTION_STACK_DEPTH= " + int0);
        int0 = GL11.glGetInteger(3385);
        printStream.println("DEBUG: GL_MAX_TEXTURE_STACK_DEPTH= " + int0);
        int0 = GL11.glGetInteger(3387);
        printStream.println("DEBUG: GL_MAX_CLIENT_ATTRIB_STACK_DEPTH= " + int0);
        int0 = GL11.glGetInteger(3440);
        printStream.println("DEBUG: GL_NAME_STACK_DEPTH= " + int0);
    }

    public static void loadMatrix(Matrix4f matrix4f) {
        matrix4f.get(Model.m_staticReusableFloatBuffer);
        Model.m_staticReusableFloatBuffer.position(16);
        Model.m_staticReusableFloatBuffer.flip();
        GL11.glLoadMatrixf(Model.m_staticReusableFloatBuffer);
    }

    public static void multMatrix(Matrix4f matrix4f) {
        matrix4f.get(Model.m_staticReusableFloatBuffer);
        Model.m_staticReusableFloatBuffer.position(16);
        Model.m_staticReusableFloatBuffer.flip();
        GL11.glMultMatrixf(Model.m_staticReusableFloatBuffer);
    }

    public static void loadMatrix(int int0, Matrix4f matrix4f) {
        GL11.glMatrixMode(int0);
        loadMatrix(matrix4f);
    }

    public static void multMatrix(int int0, Matrix4f matrix4f) {
        GL11.glMatrixMode(int0);
        multMatrix(matrix4f);
    }

    public static void pushAndLoadMatrix(int int0, Matrix4f matrix4f) {
        GL11.glMatrixMode(int0);
        GL11.glPushMatrix();
        loadMatrix(matrix4f);
    }

    public static void pushAndMultMatrix(int int0, Matrix4f matrix4f) {
        GL11.glMatrixMode(int0);
        GL11.glPushMatrix();
        multMatrix(matrix4f);
    }

    public static void popMatrix(int int0) {
        GL11.glMatrixMode(int0);
        GL11.glPopMatrix();
    }
}
