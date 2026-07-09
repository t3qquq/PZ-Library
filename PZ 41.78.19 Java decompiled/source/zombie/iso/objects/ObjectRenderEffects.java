// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.util.ArrayDeque;
import java.util.ArrayList;
import zombie.GameTime;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.iso.IsoObject;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameServer;

/**
 * TurboTuTone.
 */
public class ObjectRenderEffects {
    public static final boolean ENABLED = true;
    private static ArrayDeque<ObjectRenderEffects> pool = new ArrayDeque<>();
    public double x1;
    public double y1;
    public double x2;
    public double y2;
    public double x3;
    public double y3;
    public double x4;
    public double y4;
    private double tx1;
    private double ty1;
    private double tx2;
    private double ty2;
    private double tx3;
    private double ty3;
    private double tx4;
    private double ty4;
    private double lx1;
    private double ly1;
    private double lx2;
    private double ly2;
    private double lx3;
    private double ly3;
    private double lx4;
    private double ly4;
    private double maxX;
    private double maxY;
    private float curTime = 0.0F;
    private float maxTime = 0.0F;
    private float totalTime = 0.0F;
    private float totalMaxTime = 0.0F;
    private RenderEffectType type;
    private IsoObject parent;
    private boolean finish = false;
    private boolean isTree = false;
    private boolean isBig = false;
    private boolean gust = false;
    private int windType = 1;
    private static float T_MOD = 1.0F;
    private static int windCount = 0;
    private static int windCountTree = 0;
    private static final int EFFECTS_COUNT = 15;
    private static final int TYPE_COUNT = 3;
    private static final ObjectRenderEffects[][] WIND_EFFECTS = new ObjectRenderEffects[3][15];
    private static final ObjectRenderEffects[][] WIND_EFFECTS_TREES = new ObjectRenderEffects[3][15];
    private static final ArrayList<ObjectRenderEffects> DYNAMIC_EFFECTS = new ArrayList<>();
    private static ObjectRenderEffects RANDOM_RUSTLE;
    private static float randomRustleTime = 0.0F;
    private static float randomRustleTotalTime = 0.0F;
    private static int randomRustleTarget = 0;
    private static int randomRustleType = 0;

    public static ObjectRenderEffects alloc() {
        return !pool.isEmpty() ? pool.pop() : new ObjectRenderEffects();
    }

    public static void release(ObjectRenderEffects o) {
        assert !pool.contains(o);

        pool.push(o.reset());
    }

    private ObjectRenderEffects() {
    }

    private ObjectRenderEffects reset() {
        this.parent = null;
        this.finish = false;
        this.isBig = false;
        this.isTree = false;
        this.curTime = 0.0F;
        this.maxTime = 0.0F;
        this.totalTime = 0.0F;
        this.totalMaxTime = 0.0F;
        this.x1 = 0.0;
        this.y1 = 0.0;
        this.x2 = 0.0;
        this.y2 = 0.0;
        this.x3 = 0.0;
        this.y3 = 0.0;
        this.x4 = 0.0;
        this.y4 = 0.0;
        this.tx1 = 0.0;
        this.ty1 = 0.0;
        this.tx2 = 0.0;
        this.ty2 = 0.0;
        this.tx3 = 0.0;
        this.ty3 = 0.0;
        this.tx4 = 0.0;
        this.ty4 = 0.0;
        this.swapTargetToLast();
        return this;
    }

    public static ObjectRenderEffects getNew(IsoObject _parent, RenderEffectType t, boolean reuseEqualType) {
        return getNew(_parent, t, reuseEqualType, false);
    }

    public static ObjectRenderEffects getNew(IsoObject _parent, RenderEffectType t, boolean reuseEqualType, boolean dontAdd) {
        if (GameServer.bServer) {
            return null;
        } else if (t == RenderEffectType.Hit_Door && !Core.getInstance().getOptionDoDoorSpriteEffects()) {
            return null;
        } else {
            ObjectRenderEffects objectRenderEffects = null;

            try {
                boolean boolean0 = false;
                if (reuseEqualType && _parent != null && _parent.getObjectRenderEffects() != null && _parent.getObjectRenderEffects().type == t) {
                    objectRenderEffects = _parent.getObjectRenderEffects();
                    boolean0 = true;
                } else {
                    objectRenderEffects = alloc();
                }

                objectRenderEffects.type = t;
                objectRenderEffects.parent = _parent;
                objectRenderEffects.finish = false;
                objectRenderEffects.isBig = false;
                objectRenderEffects.totalTime = 0.0F;
                switch (t) {
                    case Hit_Tree_Shudder:
                        objectRenderEffects.totalMaxTime = Rand.Next(45.0F, 60.0F) * T_MOD;
                        break;
                    case Vegetation_Rustle:
                        objectRenderEffects.totalMaxTime = Rand.Next(45.0F, 60.0F) * T_MOD;
                        if (_parent != null && _parent instanceof IsoTree) {
                            objectRenderEffects.isTree = true;
                            objectRenderEffects.isBig = ((IsoTree)_parent).size > 4;
                        }
                        break;
                    case Hit_Door:
                        objectRenderEffects.totalMaxTime = Rand.Next(15.0F, 30.0F) * T_MOD;
                }

                if (!boolean0 && _parent != null && _parent.getWindRenderEffects() != null && Core.getInstance().getOptionDoWindSpriteEffects()) {
                    objectRenderEffects.copyMainFromOther(_parent.getWindRenderEffects());
                }

                if (!boolean0 && !dontAdd) {
                    DYNAMIC_EFFECTS.add(objectRenderEffects);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return objectRenderEffects;
        }
    }

    public static ObjectRenderEffects getNextWindEffect(int _windType, boolean isTreeLike) {
        int int0 = _windType - 1;
        if (int0 < 0 || int0 >= 3) {
            return null;
        } else if (isTreeLike) {
            if (++windCountTree >= 15) {
                windCountTree = 0;
            }

            return WIND_EFFECTS_TREES[int0][windCountTree];
        } else {
            if (++windCount >= 15) {
                windCount = 0;
            }

            return WIND_EFFECTS[int0][windCount];
        }
    }

    public static void init() {
        if (!GameServer.bServer) {
            for (int int0 = 0; int0 < 3; int0++) {
                for (int int1 = 0; int1 < 15; int1++) {
                    ObjectRenderEffects objectRenderEffects0 = new ObjectRenderEffects();
                    objectRenderEffects0.windType = int0 + 1;
                    WIND_EFFECTS[int0][int1] = objectRenderEffects0;
                }

                for (int int2 = 0; int2 < 15; int2++) {
                    ObjectRenderEffects objectRenderEffects1 = new ObjectRenderEffects();
                    objectRenderEffects1.isTree = true;
                    objectRenderEffects1.windType = int0 + 1;
                    WIND_EFFECTS_TREES[int0][int2] = objectRenderEffects1;
                }
            }

            DYNAMIC_EFFECTS.clear();
            windCount = 0;
            windCountTree = 0;
            RANDOM_RUSTLE = null;
            randomRustleTime = 0.0F;
            randomRustleTotalTime = 0.0F;
            randomRustleTarget = 0;
        }
    }

    public boolean update() {
        this.curTime = this.curTime + 1.0F * GameTime.getInstance().getMultiplier();
        this.totalTime = this.totalTime + 1.0F * GameTime.getInstance().getMultiplier();
        if (this.curTime > this.maxTime) {
            if (this.finish) {
                return false;
            }

            this.curTime = 0.0F;
            this.swapTargetToLast();
            float float0 = ClimateManager.clamp01(this.totalTime / this.totalMaxTime);
            float float1 = 1.0F - float0;
            switch (this.type) {
                case Hit_Tree_Shudder:
                    if (this.totalTime > this.totalMaxTime) {
                        this.maxTime = 10.0F * T_MOD;
                        this.tx1 = 0.0;
                        this.tx2 = 0.0;
                        this.finish = true;
                    } else {
                        this.maxTime = (3.0F + 15.0F * float0) * T_MOD;
                        double double4 = this.isBig
                            ? Rand.Next(-0.01F + -0.08F * float1, 0.01F + 0.08F * float1)
                            : Rand.Next(-0.02F + -0.16F * float1, 0.02F + 0.16F * float1);
                        this.tx1 = double4;
                        this.tx2 = double4;
                    }
                    break;
                case Vegetation_Rustle:
                    if (this.totalTime > this.totalMaxTime) {
                        this.maxTime = 3.0F * T_MOD;
                        this.tx1 = 0.0;
                        this.tx2 = 0.0;
                        this.finish = true;
                    } else {
                        this.maxTime = (2.0F + 6.0F * float0) * T_MOD;
                        double double2 = this.isBig ? Rand.Next(-0.00625F, 0.00625F) : Rand.Next(-0.015F, 0.015F);
                        double double3 = this.isBig ? Rand.Next(-0.00625F, 0.00625F) : Rand.Next(-0.015F, 0.015F);
                        if (ClimateManager.getWindTickFinal() < 0.15) {
                            double2 *= 0.6;
                            double3 *= 0.6;
                        }

                        this.tx1 = double2;
                        this.ty1 = double3;
                        this.tx2 = double2;
                        this.ty2 = double3;
                    }
                    break;
                case Hit_Door:
                    if (this.totalTime > this.totalMaxTime) {
                        this.maxTime = 3.0F * T_MOD;
                        this.tx1 = 0.0;
                        this.tx2 = 0.0;
                        this.finish = true;
                    } else {
                        this.maxTime = (1.0F + 2.0F * float0) * T_MOD;
                        double double0 = Rand.Next(-0.005F, 0.005F);
                        double double1 = Rand.Next(-0.0075F, 0.0075F);
                        this.tx1 = double0;
                        this.ty1 = double1;
                        this.tx2 = double0;
                        this.ty2 = double1;
                        this.tx3 = double0;
                        this.ty3 = double1;
                        this.tx4 = double0;
                        this.ty4 = double1;
                    }
                    break;
                default:
                    this.finish = true;
            }
        }

        this.lerpAll(this.curTime / this.maxTime);
        if (this.parent != null && this.parent.getWindRenderEffects() != null && Core.getInstance().getOptionDoWindSpriteEffects()) {
            this.add(this.parent.getWindRenderEffects());
        }

        return true;
    }

    private void update(float float2, float float5) {
        this.curTime = this.curTime + 1.0F * GameTime.getInstance().getMultiplier();
        if (this.curTime >= this.maxTime) {
            this.swapTargetToLast();
            if (this.isTree) {
                float float0 = 0.0F;
                float float1 = 0.04F;
                if (this.windType == 1) {
                    float0 = 0.6F;
                    float2 = float2 <= 0.08F ? 0.0F : (float2 - 0.08F) / 0.92F;
                } else if (this.windType == 2) {
                    float0 = 0.3F;
                    float1 = 0.06F;
                    float2 = float2 <= 0.15F ? 0.0F : (float2 - 0.15F) / 0.85F;
                } else if (this.windType == 3) {
                    float0 = 0.15F;
                    float2 = float2 <= 0.3F ? 0.0F : (float2 - 0.3F) / 0.7F;
                }

                float float3 = ClimateManager.clamp01(1.0F - float2);
                this.curTime = 0.0F;
                this.maxTime = Rand.Next(20.0F + 100.0F * float3, 70.0F + 200.0F * float3) * T_MOD;
                if (float2 <= 0.01F || !Core.OptionDoWindSpriteEffects) {
                    this.tx1 = 0.0;
                    this.tx2 = 0.0;
                    this.ty1 = 0.0;
                    this.ty2 = 0.0;
                    return;
                }

                float float4 = 0.6F * float2 + 0.4F * (float2 * float2);
                double double0;
                if (this.gust) {
                    double0 = Rand.Next(-0.1F + 0.6F * float2, 1.0F) * float5;
                    if (Rand.Next(0.0F, 1.0F) > Rand.Next(0.0F, 0.75F * float2)) {
                        this.gust = false;
                    }
                } else {
                    double0 = Rand.Next(-0.1F, 0.2F) * float5;
                    this.gust = true;
                }

                double0 *= float0 * float4;
                this.tx1 = double0;
                this.tx2 = double0;
                double double1 = Rand.Next(-1.0F, 1.0F);
                double1 *= 0.01 + float1 * float4;
                this.ty1 = double1;
                double1 = Rand.Next(-1.0F, 1.0F);
                double1 *= 0.01 + float1 * float4;
                this.ty2 = double1;
            } else {
                float float6 = 0.0F;
                if (this.windType == 1) {
                    float6 = 0.575F;
                    float2 = float2 <= 0.02F ? 0.0F : (float2 - 0.02F) / 0.98F;
                } else if (this.windType == 2) {
                    float6 = 0.375F;
                    float2 = float2 <= 0.2F ? 0.0F : (float2 - 0.2F) / 0.8F;
                } else if (this.windType == 3) {
                    float6 = 0.175F;
                    float2 = float2 <= 0.6F ? 0.0F : (float2 - 0.6F) / 0.4F;
                }

                float float7 = ClimateManager.clamp01(1.0F - float2);
                this.curTime = 0.0F;
                this.maxTime = Rand.Next(20.0F + 50.0F * float7, 60.0F + 100.0F * float7) * T_MOD;
                if (float2 <= 0.05F || !Core.OptionDoWindSpriteEffects) {
                    this.tx1 = 0.0;
                    this.tx2 = 0.0;
                    this.ty1 = 0.0;
                    this.ty2 = 0.0;
                    return;
                }

                float float8 = 0.55F * float2 + 0.45F * (float2 * float2);
                double double2;
                if (this.gust) {
                    double2 = Rand.Next(-0.1F + 0.9F * float2, 1.0F) * float5;
                    if (Rand.Next(0.0F, 1.0F) > Rand.Next(0.0F, 0.95F * float2)) {
                        this.gust = false;
                    }
                } else {
                    double2 = Rand.Next(-0.1F, 0.2F) * float5;
                    this.gust = true;
                }

                double2 *= 0.025F + float6 * float8;
                this.tx1 = double2;
                this.tx2 = double2;
                if (float2 > 0.5F) {
                    double double3 = Rand.Next(-1.0F, 1.0F);
                    double3 *= 0.05F * float8;
                    this.ty1 = double3;
                    double3 = Rand.Next(-1.0F, 1.0F);
                    double3 *= 0.05F * float8;
                    this.ty2 = double3;
                } else {
                    this.ty1 = 0.0;
                    this.ty2 = 0.0;
                }
            }
        } else {
            this.lerpAll(this.curTime / this.maxTime);
        }
    }

    private void updateOLD(float float1, float float3) {
        this.curTime = this.curTime + 1.0F * GameTime.getInstance().getMultiplier();
        if (this.curTime >= this.maxTime) {
            this.curTime = 0.0F;
            float float0 = ClimateManager.clamp01(1.0F - float1);
            this.maxTime = Rand.Next(20.0F + 100.0F * float0, 70.0F + 200.0F * float0) * T_MOD;
            this.swapTargetToLast();
            float float2 = ClimateManager.clamp01(float1 * 1.25F);
            double double0 = Rand.Next(-0.65F, 0.65F);
            double0 += float1 * float3 * 0.7F;
            double0 *= 0.4F * float2;
            this.tx1 = double0;
            this.tx2 = double0;
            double double1 = Rand.Next(-1.0F, 1.0F);
            double1 *= 0.05F * float2;
            this.ty1 = double1;
            double1 = Rand.Next(-1.0F, 1.0F);
            double1 *= 0.05F * float2;
            this.ty2 = double1;
        } else {
            this.lerpAll(this.curTime / this.maxTime);
        }
    }

    private void lerpAll(float float0) {
        this.x1 = ClimateManager.clerp(float0, (float)this.lx1, (float)this.tx1);
        this.y1 = ClimateManager.clerp(float0, (float)this.ly1, (float)this.ty1);
        this.x2 = ClimateManager.clerp(float0, (float)this.lx2, (float)this.tx2);
        this.y2 = ClimateManager.clerp(float0, (float)this.ly2, (float)this.ty2);
        this.x3 = ClimateManager.clerp(float0, (float)this.lx3, (float)this.tx3);
        this.y3 = ClimateManager.clerp(float0, (float)this.ly3, (float)this.ty3);
        this.x4 = ClimateManager.clerp(float0, (float)this.lx4, (float)this.tx4);
        this.y4 = ClimateManager.clerp(float0, (float)this.ly4, (float)this.ty4);
    }

    private void swapTargetToLast() {
        this.lx1 = this.tx1;
        this.ly1 = this.ty1;
        this.lx2 = this.tx2;
        this.ly2 = this.ty2;
        this.lx3 = this.tx3;
        this.ly3 = this.ty3;
        this.lx4 = this.tx4;
        this.ly4 = this.ty4;
    }

    public void copyMainFromOther(ObjectRenderEffects other) {
        this.x1 = other.x1;
        this.y1 = other.y1;
        this.x2 = other.x2;
        this.y2 = other.y2;
        this.x3 = other.x3;
        this.y3 = other.y3;
        this.x4 = other.x4;
        this.y4 = other.y4;
    }

    public void add(ObjectRenderEffects other) {
        this.x1 = this.x1 + other.x1;
        this.y1 = this.y1 + other.y1;
        this.x2 = this.x2 + other.x2;
        this.y2 = this.y2 + other.y2;
        this.x3 = this.x3 + other.x3;
        this.y3 = this.y3 + other.y3;
        this.x4 = this.x4 + other.x4;
        this.y4 = this.y4 + other.y4;
    }

    public static void updateStatic() {
        if (!GameServer.bServer) {
            try {
                float float0 = (float)ClimateManager.getWindTickFinal();
                float float1 = ClimateManager.getInstance().getWindAngleIntensity();
                if (float1 < 0.0F) {
                    float1 = -1.0F;
                } else {
                    float1 = 1.0F;
                }

                for (int int0 = 0; int0 < 3; int0++) {
                    for (int int1 = 0; int1 < 15; int1++) {
                        ObjectRenderEffects objectRenderEffects0 = WIND_EFFECTS[int0][int1];
                        objectRenderEffects0.update(float0, float1);
                    }

                    for (int int2 = 0; int2 < 15; int2++) {
                        ObjectRenderEffects objectRenderEffects1 = WIND_EFFECTS_TREES[int0][int2];
                        objectRenderEffects1.update(float0, float1);
                    }
                }

                randomRustleTime = randomRustleTime + 1.0F * GameTime.getInstance().getMultiplier();
                if (randomRustleTime > randomRustleTotalTime && RANDOM_RUSTLE == null) {
                    float float2 = 1.0F - float0;
                    RANDOM_RUSTLE = getNew(null, RenderEffectType.Vegetation_Rustle, false, true);
                    RANDOM_RUSTLE.isBig = false;
                    if (float0 > 0.45F && Rand.Next(0.0F, 1.0F) < Rand.Next(0.0F, 0.8F * float0)) {
                        RANDOM_RUSTLE.isBig = true;
                    }

                    randomRustleType = Rand.Next(3);
                    randomRustleTarget = Rand.Next(15);
                    randomRustleTime = 0.0F;
                    randomRustleTotalTime = Rand.Next(400.0F + 400.0F * float2, 1200.0F + 3200.0F * float2);
                }

                if (RANDOM_RUSTLE != null) {
                    if (!RANDOM_RUSTLE.update()) {
                        release(RANDOM_RUSTLE);
                        RANDOM_RUSTLE = null;
                    } else {
                        ObjectRenderEffects objectRenderEffects2 = WIND_EFFECTS_TREES[randomRustleType][randomRustleTarget];
                        objectRenderEffects2.add(RANDOM_RUSTLE);
                    }
                }

                for (int int3 = DYNAMIC_EFFECTS.size() - 1; int3 >= 0; int3--) {
                    ObjectRenderEffects objectRenderEffects3 = DYNAMIC_EFFECTS.get(int3);
                    if (!objectRenderEffects3.update()) {
                        if (objectRenderEffects3.parent != null) {
                            objectRenderEffects3.parent.removeRenderEffect(objectRenderEffects3);
                        }

                        DYNAMIC_EFFECTS.remove(int3);
                        release(objectRenderEffects3);
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
