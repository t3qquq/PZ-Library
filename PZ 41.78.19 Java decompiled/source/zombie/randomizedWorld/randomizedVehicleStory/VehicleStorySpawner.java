// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.ArrayList;
import java.util.HashMap;
import zombie.core.math.PZMath;
import zombie.debug.LineDrawer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.popman.ObjectPool;
import zombie.util.Type;

public class VehicleStorySpawner {
    private static final VehicleStorySpawner instance = new VehicleStorySpawner();
    private static final Vector2 s_vector2_1 = new Vector2();
    private static final Vector2 s_vector2_2 = new Vector2();
    private static final ObjectPool<VehicleStorySpawner.Element> s_elementPool = new ObjectPool<>(VehicleStorySpawner.Element::new);
    private static final int[] s_AABB = new int[4];
    public final ArrayList<VehicleStorySpawner.Element> m_elements = new ArrayList<>();
    public final HashMap<String, Object> m_storyParams = new HashMap<>();

    public static VehicleStorySpawner getInstance() {
        return instance;
    }

    public void clear() {
        s_elementPool.release(this.m_elements);
        this.m_elements.clear();
        this.m_storyParams.clear();
    }

    public VehicleStorySpawner.Element addElement(String id, float x, float y, float direction, float width, float height) {
        VehicleStorySpawner.Element element = s_elementPool.alloc().init(id, x, y, direction, width, height);
        this.m_elements.add(element);
        return element;
    }

    public void setParameter(String key, boolean value) {
        this.m_storyParams.put(key, value ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setParameter(String key, float value) {
        this.m_storyParams.put(key, value);
    }

    public void setParameter(String key, int value) {
        this.m_storyParams.put(key, value);
    }

    public void setParameter(String key, Object value) {
        this.m_storyParams.put(key, value);
    }

    public boolean getParameterBoolean(String key) {
        return this.getParameter(key, Boolean.class);
    }

    public float getParameterFloat(String key) {
        return this.getParameter(key, Float.class);
    }

    public int getParameterInteger(String key) {
        return this.getParameter(key, Integer.class);
    }

    public String getParameterString(String key) {
        return this.getParameter(key, String.class);
    }

    public <E> E getParameter(String string, Class<E> clazz) {
        return Type.tryCastTo(this.m_storyParams.get(string), clazz);
    }

    public void spawn(float worldX, float worldY, float worldZ, float angleRadians, VehicleStorySpawner.IElementSpawner spawner) {
        for (int int0 = 0; int0 < this.m_elements.size(); int0++) {
            VehicleStorySpawner.Element element = this.m_elements.get(int0);
            Vector2 vector = s_vector2_1.setLengthAndDirection(element.direction, 1.0F);
            vector.add(element.position);
            this.rotate(worldX, worldY, vector, angleRadians);
            this.rotate(worldX, worldY, element.position, angleRadians);
            element.direction = Vector2.getDirection(vector.x - element.position.x, vector.y - element.position.y);
            element.z = worldZ;
            element.square = IsoWorld.instance.CurrentCell.getGridSquare((double)element.position.x, (double)element.position.y, (double)worldZ);
            spawner.spawn(this, element);
        }
    }

    public Vector2 rotate(float centerX, float centerY, Vector2 v, float angleRadians) {
        float float0 = v.x;
        float float1 = v.y;
        v.x = centerX + (float)(float0 * Math.cos(angleRadians) - float1 * Math.sin(angleRadians));
        v.y = centerY + (float)(float0 * Math.sin(angleRadians) + float1 * Math.cos(angleRadians));
        return v;
    }

    public void getAABB(float float4, float float6, float float2, float float1, float float0, int[] ints) {
        Vector2 vector0 = s_vector2_1.setLengthAndDirection(float0, 1.0F);
        Vector2 vector1 = s_vector2_2.set(vector0);
        vector1.tangent();
        vector0.x *= float1 / 2.0F;
        vector0.y *= float1 / 2.0F;
        vector1.x *= float2 / 2.0F;
        vector1.y *= float2 / 2.0F;
        float float3 = float4 + vector0.x;
        float float5 = float6 + vector0.y;
        float float7 = float4 - vector0.x;
        float float8 = float6 - vector0.y;
        float float9 = float3 - vector1.x;
        float float10 = float5 - vector1.y;
        float float11 = float3 + vector1.x;
        float float12 = float5 + vector1.y;
        float float13 = float7 - vector1.x;
        float float14 = float8 - vector1.y;
        float float15 = float7 + vector1.x;
        float float16 = float8 + vector1.y;
        float float17 = PZMath.min(float9, PZMath.min(float11, PZMath.min(float13, float15)));
        float float18 = PZMath.max(float9, PZMath.max(float11, PZMath.max(float13, float15)));
        float float19 = PZMath.min(float10, PZMath.min(float12, PZMath.min(float14, float16)));
        float float20 = PZMath.max(float10, PZMath.max(float12, PZMath.max(float14, float16)));
        ints[0] = (int)PZMath.floor(float17);
        ints[1] = (int)PZMath.floor(float19);
        ints[2] = (int)PZMath.ceil(float18);
        ints[3] = (int)PZMath.ceil(float20);
    }

    public void render(float centerX, float centerY, float z, float width, float height, float angleRadians) {
        LineDrawer.DrawIsoRectRotated(centerX, centerY, z, width, height, angleRadians, 0.0F, 0.0F, 1.0F, 1.0F);
        float float0 = 1.0F;
        float float1 = 1.0F;
        float float2 = 1.0F;
        float float3 = 1.0F;
        float float4 = Float.MAX_VALUE;
        float float5 = Float.MAX_VALUE;
        float float6 = -Float.MAX_VALUE;
        float float7 = -Float.MAX_VALUE;

        for (VehicleStorySpawner.Element element : this.m_elements) {
            Vector2 vector = s_vector2_1.setLengthAndDirection(element.direction, 1.0F);
            LineDrawer.DrawIsoLine(
                element.position.x, element.position.y, z, element.position.x + vector.x, element.position.y + vector.y, z, float0, float1, float2, float3, 1
            );
            LineDrawer.DrawIsoRectRotated(
                element.position.x, element.position.y, z, element.width, element.height, element.direction, float0, float1, float2, float3
            );
            this.getAABB(element.position.x, element.position.y, element.width, element.height, element.direction, s_AABB);
            float4 = PZMath.min(float4, (float)s_AABB[0]);
            float5 = PZMath.min(float5, (float)s_AABB[1]);
            float6 = PZMath.max(float6, (float)s_AABB[2]);
            float7 = PZMath.max(float7, (float)s_AABB[3]);
        }
    }

    public static final class Element {
        String id;
        final Vector2 position = new Vector2();
        float direction;
        float width;
        float height;
        float z;
        IsoGridSquare square;

        VehicleStorySpawner.Element init(String string, float float0, float float1, float float2, float float3, float float4) {
            this.id = string;
            this.position.set(float0, float1);
            this.direction = float2;
            this.width = float3;
            this.height = float4;
            this.z = 0.0F;
            this.square = null;
            return this;
        }
    }

    public interface IElementSpawner {
        void spawn(VehicleStorySpawner spawner, VehicleStorySpawner.Element element);
    }
}
