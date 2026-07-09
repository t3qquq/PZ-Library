// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;
import org.lwjglx.BufferUtils;
import zombie.core.Rand;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.textures.Texture;
import zombie.interfaces.ITexture;
import zombie.iso.weather.ClimateManager;

public final class ParticlesFire extends Particles {
    int MaxParticles = 1000000;
    int MaxVortices = 4;
    int particles_data_buffer;
    ByteBuffer particule_data;
    private Texture texFireSmoke;
    private Texture texFlameFire;
    public FireShader EffectFire;
    public SmokeShader EffectSmoke;
    public Shader EffectVape;
    float windX;
    float windY;
    private static ParticlesFire instance;
    private ParticlesArray<ParticlesFire.Particle> particles;
    private ArrayList<ParticlesFire.Zone> zones;
    private int intensityFire = 0;
    private int intensitySmoke = 0;
    private int intensitySteam = 0;
    private FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);

    public static synchronized ParticlesFire getInstance() {
        if (instance == null) {
            instance = new ParticlesFire();
        }

        return instance;
    }

    public ParticlesFire() {
        this.particles = new ParticlesArray<>();
        this.zones = new ArrayList<>();
        this.particule_data = BufferUtils.createByteBuffer(this.MaxParticles * 4 * 4);
        this.texFireSmoke = Texture.getSharedTexture("media/textures/FireSmokes.png");
        this.texFlameFire = Texture.getSharedTexture("media/textures/FireFlame.png");
        this.zones.clear();
        float float0 = (int)(IsoCamera.frameState.OffX + IsoCamera.frameState.OffscreenWidth / 2);
        float float1 = (int)(IsoCamera.frameState.OffY + IsoCamera.frameState.OffscreenHeight / 2);
        this.zones.add(new ParticlesFire.Zone(10, float0 - 30.0F, float1 - 10.0F, float0 + 30.0F, float1 + 10.0F));
        this.zones.add(new ParticlesFire.Zone(10, float0 - 200.0F, float1, 50.0F));
        this.zones.add(new ParticlesFire.Zone(40, float0 + 200.0F, float1, 100.0F));
        this.zones.add(new ParticlesFire.Zone(60, float0 - 150.0F, float1 - 300.0F, float0 + 250.0F, float1 - 300.0F, 10.0F));
        this.zones.add(new ParticlesFire.Zone(10, float0 - 350.0F, float1 - 200.0F, float0 - 350.0F, float1 - 300.0F, 10.0F));
    }

    private void ParticlesProcess() {
        for (int int0 = 0; int0 < this.zones.size(); int0++) {
            ParticlesFire.Zone zone = this.zones.get(int0);
            int int1 = (int)Math.ceil((zone.intensity - zone.currentParticles) * 0.1F);
            if (zone.type == ParticlesFire.ZoneType.Rectangle) {
                for (int int2 = 0; int2 < int1; int2++) {
                    ParticlesFire.Particle particle0 = new ParticlesFire.Particle();
                    particle0.x = Rand.Next(zone.x0, zone.x1);
                    particle0.y = Rand.Next(zone.y0, zone.y1);
                    particle0.vx = Rand.Next(-3.0F, 3.0F);
                    particle0.vy = Rand.Next(1.0F, 5.0F);
                    particle0.tShift = 0.0F;
                    particle0.id = Rand.Next(-1000000.0F, 1000000.0F);
                    particle0.zone = zone;
                    zone.currentParticles++;
                    this.particles.addParticle(particle0);
                }
            }

            if (zone.type == ParticlesFire.ZoneType.Circle) {
                for (int int3 = 0; int3 < int1; int3++) {
                    ParticlesFire.Particle particle1 = new ParticlesFire.Particle();
                    float float0 = Rand.Next(0.0F, (float) (Math.PI * 2));
                    float float1 = Rand.Next(0.0F, zone.r);
                    particle1.x = (float)(zone.x0 + float1 * Math.cos(float0));
                    particle1.y = (float)(zone.y0 + float1 * Math.sin(float0));
                    particle1.vx = Rand.Next(-3.0F, 3.0F);
                    particle1.vy = Rand.Next(1.0F, 5.0F);
                    particle1.tShift = 0.0F;
                    particle1.id = Rand.Next(-1000000.0F, 1000000.0F);
                    particle1.zone = zone;
                    zone.currentParticles++;
                    this.particles.addParticle(particle1);
                }
            }

            if (zone.type == ParticlesFire.ZoneType.Line) {
                for (int int4 = 0; int4 < int1; int4++) {
                    ParticlesFire.Particle particle2 = new ParticlesFire.Particle();
                    float float2 = Rand.Next(0.0F, (float) (Math.PI * 2));
                    float float3 = Rand.Next(0.0F, zone.r);
                    float float4 = Rand.Next(0.0F, 1.0F);
                    particle2.x = (float)(zone.x0 * float4 + zone.x1 * (1.0F - float4) + float3 * Math.cos(float2));
                    particle2.y = (float)(zone.y0 * float4 + zone.y1 * (1.0F - float4) + float3 * Math.sin(float2));
                    particle2.vx = Rand.Next(-3.0F, 3.0F);
                    particle2.vy = Rand.Next(1.0F, 5.0F);
                    particle2.tShift = 0.0F;
                    particle2.id = Rand.Next(-1000000.0F, 1000000.0F);
                    particle2.zone = zone;
                    zone.currentParticles++;
                    this.particles.addParticle(particle2);
                }
            }

            if (int1 < 0) {
                for (int int5 = 0; int5 < -int1; int5++) {
                    zone.currentParticles--;
                    this.particles.deleteParticle(Rand.Next(0, this.particles.getCount() + 1));
                }
            }
        }
    }

    public FloatBuffer getParametersFire() {
        this.floatBuffer.clear();
        this.floatBuffer.put(this.windX);
        this.floatBuffer.put(this.intensityFire);
        this.floatBuffer.put(0.0F);
        this.floatBuffer.put(this.windY);
        this.floatBuffer.put(0.0F);
        this.floatBuffer.put(0.0F);
        this.floatBuffer.put(0.0F);
        this.floatBuffer.put(0.0F);
        this.floatBuffer.put(0.0F);
        this.floatBuffer.put(0.0F);
        this.floatBuffer.put(0.0F);
        this.floatBuffer.put(0.0F);
        this.floatBuffer.flip();
        return this.floatBuffer;
    }

    public int getFireShaderID() {
        return this.EffectFire.getID();
    }

    public int getSmokeShaderID() {
        return this.EffectSmoke.getID();
    }

    public int getVapeShaderID() {
        return this.EffectVape.getID();
    }

    public ITexture getFireFlameTexture() {
        return this.texFlameFire;
    }

    public ITexture getFireSmokeTexture() {
        return this.texFireSmoke;
    }

    @Override
    public void reloadShader() {
        RenderThread.invokeOnRenderContext(() -> {
            this.EffectFire = new FireShader("fire");
            this.EffectSmoke = new SmokeShader("smoke");
            this.EffectVape = new Shader("vape");
        });
    }

    @Override
    void createParticleBuffers() {
        this.particles_data_buffer = funcs.glGenBuffers();
        funcs.glBindBuffer(34962, this.particles_data_buffer);
        funcs.glBufferData(34962, this.MaxParticles * 4 * 4, 35044);
    }

    @Override
    void destroyParticleBuffers() {
        funcs.glDeleteBuffers(this.particles_data_buffer);
    }

    @Override
    void updateParticleParams() {
        float float0 = ClimateManager.getInstance().getWindAngleIntensity();
        float float1 = ClimateManager.getInstance().getWindIntensity();
        this.windX = (float)Math.sin(float0 * 6.0F) * float1;
        this.windY = (float)Math.cos(float0 * 6.0F) * float1;
        this.ParticlesProcess();
        if (this.particles.getNeedToUpdate()) {
            this.particles.defragmentParticle();
            this.particule_data.clear();

            for (int int0 = 0; int0 < this.particles.size(); int0++) {
                ParticlesFire.Particle particle = this.particles.get(int0);
                if (particle != null) {
                    this.particule_data.putFloat(particle.x);
                    this.particule_data.putFloat(particle.y);
                    this.particule_data.putFloat(particle.id);
                    this.particule_data.putFloat((float)int0 / this.particles.size());
                }
            }

            this.particule_data.flip();
        }

        funcs.glBindBuffer(34962, this.particles_data_buffer);
        funcs.glBufferData(34962, this.particule_data, 35040);
        GL20.glEnableVertexAttribArray(1);
        funcs.glBindBuffer(34962, this.particles_data_buffer);
        GL20.glVertexAttribPointer(1, 4, 5126, false, 0, 0L);
        GL33.glVertexAttribDivisor(1, 1);
    }

    @Override
    int getParticleCount() {
        return this.particles.getCount();
    }

    public class Particle {
        float id;
        float x;
        float y;
        float tShift;
        float vx;
        float vy;
        ParticlesFire.Zone zone;
    }

    public class Vortice {
        float x;
        float y;
        float z;
        float size;
        float vx;
        float vy;
        float speed;
        int life;
        int lifeTime;
        ParticlesFire.Zone zone;
    }

    public class Zone {
        ParticlesFire.ZoneType type;
        int intensity;
        int currentParticles;
        float x0;
        float y0;
        float x1;
        float y1;
        float r;
        float fireIntensity;
        float smokeIntensity;
        float sparksIntensity;
        float vortices;
        float vorticeSpeed;
        float area;
        float temperature;
        float centerX;
        float centerY;
        float centerRp2;
        float currentVorticesCount;

        Zone(int int0, float float0, float float1, float float2) {
            this.type = ParticlesFire.ZoneType.Circle;
            this.intensity = int0;
            this.currentParticles = 0;
            this.x0 = float0;
            this.y0 = float1;
            this.r = float2;
            this.area = (float)(Math.PI * float2 * float2);
            this.vortices = this.intensity * 0.3F;
            this.vorticeSpeed = 0.5F;
            this.temperature = 2000.0F;
            this.centerX = float0;
            this.centerY = float1;
            this.centerRp2 = float2 * float2;
        }

        Zone(int int0, float float0, float float2, float float1, float float3) {
            this.type = ParticlesFire.ZoneType.Rectangle;
            this.intensity = int0;
            this.currentParticles = 0;
            if (float0 < float1) {
                this.x0 = float0;
                this.x1 = float1;
            } else {
                this.x1 = float0;
                this.x0 = float1;
            }

            if (float2 < float3) {
                this.y0 = float2;
                this.y1 = float3;
            } else {
                this.y1 = float2;
                this.y0 = float3;
            }

            this.area = (this.x1 - this.x0) * (this.y1 - this.y0);
            this.vortices = this.intensity * 0.3F;
            this.vorticeSpeed = 0.5F;
            this.temperature = 2000.0F;
            this.centerX = (this.x0 + this.x1) * 0.5F;
            this.centerY = (this.y0 + this.y1) * 0.5F;
            this.centerRp2 = (this.x1 - this.x0) * (this.x1 - this.x0);
        }

        Zone(int int0, float float0, float float2, float float1, float float3, float float4) {
            this.type = ParticlesFire.ZoneType.Line;
            this.intensity = int0;
            this.currentParticles = 0;
            if (float0 < float1) {
                this.x0 = float0;
                this.x1 = float1;
                this.y0 = float2;
                this.y1 = float3;
            } else {
                this.x1 = float0;
                this.x0 = float1;
                this.y1 = float2;
                this.y0 = float3;
            }

            this.r = float4;
            this.area = (float)(this.r * Math.sqrt(Math.pow(float0 - float1, 2.0) + Math.pow(float2 - float3, 2.0)));
            this.vortices = this.intensity * 0.3F;
            this.vorticeSpeed = 0.5F;
            this.temperature = 2000.0F;
            this.centerX = (this.x0 + this.x1) * 0.5F;
            this.centerY = (this.y0 + this.y1) * 0.5F;
            this.centerRp2 = (this.x1 - this.x0 + float4) * (this.x1 - this.x0 + float4) * 100.0F;
        }
    }

    static enum ZoneType {
        Rectangle,
        Circle,
        Line;
    }
}
