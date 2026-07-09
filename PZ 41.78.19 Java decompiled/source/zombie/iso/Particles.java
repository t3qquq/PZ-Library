// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjglx.BufferUtils;
import zombie.GameTime;
import zombie.core.SpriteRenderer;
import zombie.core.VBO.GLBufferObject15;
import zombie.core.VBO.GLBufferObjectARB;
import zombie.core.VBO.IGLBufferObject;
import zombie.core.opengl.RenderThread;
import zombie.debug.DebugLog;

public abstract class Particles {
    private float ParticlesTime;
    public static int ParticleSystemsCount = 0;
    public static int ParticleSystemsLast = 0;
    public static final ArrayList<Particles> ParticleSystems = new ArrayList<>();
    private int id;
    int particle_vertex_buffer;
    public static IGLBufferObject funcs = null;
    private Matrix4f projectionMatrix;
    private Matrix4f mvpMatrix;
    private FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);

    public static synchronized int addParticle(Particles particles) {
        if (ParticleSystems.size() == ParticleSystemsCount) {
            ParticleSystems.add(particles);
            ParticleSystemsCount++;
            return ParticleSystems.size() - 1;
        } else {
            int int0 = ParticleSystemsLast;
            if (int0 < ParticleSystems.size()) {
                if (ParticleSystems.get(int0) == null) {
                    ParticleSystemsLast = int0;
                    ParticleSystems.set(int0, particles);
                    ParticleSystemsCount++;
                }

                return int0;
            } else {
                byte byte0 = 0;
                if (byte0 < ParticleSystemsLast) {
                    if (ParticleSystems.get(byte0) == null) {
                        ParticleSystemsLast = byte0;
                        ParticleSystems.set(byte0, particles);
                        ParticleSystemsCount++;
                    }

                    return byte0;
                } else {
                    DebugLog.log("ERROR: addParticle has unknown error");
                    return -1;
                }
            }
        }
    }

    public static synchronized void deleteParticle(int int0) {
        ParticleSystems.set(int0, null);
        ParticleSystemsCount--;
    }

    public static void init() {
        if (funcs == null) {
            if (!GL.getCapabilities().OpenGL33) {
                System.out.println("OpenGL 3.3 don't supported");
            }

            if (GL.getCapabilities().OpenGL15) {
                System.out.println("OpenGL 1.5 buffer objects supported");
                funcs = new GLBufferObject15();
            } else {
                if (!GL.getCapabilities().GL_ARB_vertex_buffer_object) {
                    throw new RuntimeException("Neither OpenGL 1.5 nor GL_ARB_vertex_buffer_object supported");
                }

                System.out.println("GL_ARB_vertex_buffer_object supported");
                funcs = new GLBufferObjectARB();
            }
        }
    }

    public void initBuffers() {
        ByteBuffer byteBuffer = MemoryUtil.memAlloc(48);
        byteBuffer.clear();
        byteBuffer.putFloat(-1.0F);
        byteBuffer.putFloat(-1.0F);
        byteBuffer.putFloat(0.0F);
        byteBuffer.putFloat(1.0F);
        byteBuffer.putFloat(-1.0F);
        byteBuffer.putFloat(0.0F);
        byteBuffer.putFloat(-1.0F);
        byteBuffer.putFloat(1.0F);
        byteBuffer.putFloat(0.0F);
        byteBuffer.putFloat(1.0F);
        byteBuffer.putFloat(1.0F);
        byteBuffer.putFloat(0.0F);
        byteBuffer.flip();
        this.particle_vertex_buffer = funcs.glGenBuffers();
        funcs.glBindBuffer(34962, this.particle_vertex_buffer);
        funcs.glBufferData(34962, byteBuffer, 35044);
        MemoryUtil.memFree(byteBuffer);
        this.createParticleBuffers();
    }

    public void destroy() {
        deleteParticle(this.id);
        funcs.glDeleteBuffers(this.particle_vertex_buffer);
        this.destroyParticleBuffers();
    }

    public abstract void reloadShader();

    public Particles() {
        RenderThread.invokeOnRenderContext(() -> {
            init();
            this.initBuffers();
            this.projectionMatrix = new Matrix4f();
        });
        this.reloadShader();
        this.id = addParticle(this);
    }

    private static Matrix4f orthogonal(float float1, float float0, float float3, float float2, float float5, float float4) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        matrix4f.m00 = 2.0F / (float0 - float1);
        matrix4f.m11 = 2.0F / (float2 - float3);
        matrix4f.m22 = -2.0F / (float4 - float5);
        matrix4f.m32 = (-float4 - float5) / (float4 - float5);
        matrix4f.m30 = (-float0 - float1) / (float0 - float1);
        matrix4f.m31 = (-float2 - float3) / (float2 - float3);
        return matrix4f;
    }

    public void render() {
        int int0 = IsoCamera.frameState.playerIndex;
        this.ParticlesTime = this.ParticlesTime + 0.0166F * GameTime.getInstance().getMultiplier();
        this.updateMVPMatrix();
        SpriteRenderer.instance.drawParticles(int0, 0, 0);
    }

    private void updateMVPMatrix() {
        this.projectionMatrix = orthogonal(
            IsoCamera.frameState.OffX,
            IsoCamera.frameState.OffX + IsoCamera.frameState.OffscreenWidth,
            IsoCamera.frameState.OffY + IsoCamera.frameState.OffscreenHeight,
            IsoCamera.frameState.OffY,
            -1.0F,
            1.0F
        );
        this.mvpMatrix = this.projectionMatrix;
    }

    public FloatBuffer getMVPMatrix() {
        this.floatBuffer.clear();
        this.floatBuffer.put(this.mvpMatrix.m00);
        this.floatBuffer.put(this.mvpMatrix.m10);
        this.floatBuffer.put(this.mvpMatrix.m20);
        this.floatBuffer.put(this.mvpMatrix.m30);
        this.floatBuffer.put(this.mvpMatrix.m01);
        this.floatBuffer.put(this.mvpMatrix.m11);
        this.floatBuffer.put(this.mvpMatrix.m21);
        this.floatBuffer.put(this.mvpMatrix.m31);
        this.floatBuffer.put(this.mvpMatrix.m02);
        this.floatBuffer.put(this.mvpMatrix.m12);
        this.floatBuffer.put(this.mvpMatrix.m22);
        this.floatBuffer.put(this.mvpMatrix.m32);
        this.floatBuffer.put(this.mvpMatrix.m03);
        this.floatBuffer.put(this.mvpMatrix.m13);
        this.floatBuffer.put(this.mvpMatrix.m23);
        this.floatBuffer.put(this.mvpMatrix.m33);
        this.floatBuffer.flip();
        return this.floatBuffer;
    }

    public void getGeometry(int var1) {
        this.updateParticleParams();
        GL20.glEnableVertexAttribArray(0);
        funcs.glBindBuffer(34962, this.particle_vertex_buffer);
        GL20.glVertexAttribPointer(0, 3, 5126, false, 0, 0L);
        GL33.glVertexAttribDivisor(0, 0);
        GL31.glDrawArraysInstanced(5, 0, 4, this.getParticleCount());
    }

    public void getGeometryFire(int var1) {
        this.updateParticleParams();
        GL20.glEnableVertexAttribArray(0);
        funcs.glBindBuffer(34962, this.particle_vertex_buffer);
        GL20.glVertexAttribPointer(0, 3, 5126, false, 0, 0L);
        GL33.glVertexAttribDivisor(0, 0);
        GL31.glDrawArraysInstanced(5, 0, 4, this.getParticleCount());
    }

    public float getShaderTime() {
        return this.ParticlesTime;
    }

    abstract void createParticleBuffers();

    abstract void destroyParticleBuffers();

    abstract void updateParticleParams();

    abstract int getParticleCount();
}
