// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import zombie.characters.IsoPlayer;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigOption;
import zombie.config.DoubleConfigOption;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.VBO.GLVertexBufferObject;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.opengl.PZGLUtil;
import zombie.core.opengl.VBOLines;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.core.skinnedmodel.model.ModelSlotRenderData;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.core.textures.TextureID;
import zombie.iso.IsoCamera;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.popman.ObjectPool;
import zombie.ui.UIManager;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.UI3DScene;
import zombie.worldMap.styles.WorldMapStyle;
import zombie.worldMap.styles.WorldMapStyleLayer;
import zombie.worldMap.styles.WorldMapTextureStyleLayer;

public final class WorldMapRenderer {
    private WorldMap m_worldMap;
    private int m_x;
    private int m_y;
    private int m_width;
    private int m_height;
    private int m_zoom = 0;
    private float m_zoomF = 0.0F;
    private float m_displayZoomF = 0.0F;
    private float m_centerWorldX;
    private float m_centerWorldY;
    private float m_zoomUIX;
    private float m_zoomUIY;
    private float m_zoomWorldX;
    private float m_zoomWorldY;
    private final Matrix4f m_projection = new Matrix4f();
    private final Matrix4f m_modelView = new Matrix4f();
    private final Quaternionf m_modelViewChange = new Quaternionf();
    private long m_viewChangeTime;
    private static long VIEW_CHANGE_TIME = 350L;
    private boolean m_isometric;
    private boolean m_firstUpdate = false;
    private WorldMapVisited m_visited;
    private final WorldMapRenderer.Drawer[] m_drawer = new WorldMapRenderer.Drawer[3];
    private final WorldMapRenderer.CharacterModelCamera m_CharacterModelCamera = new WorldMapRenderer.CharacterModelCamera();
    private int m_dropShadowWidth = 12;
    public WorldMapStyle m_style = null;
    protected static final VBOLines m_vboLines = new VBOLines();
    protected static final VBOLinesUV m_vboLinesUV = new VBOLinesUV();
    private final int[] m_viewport = new int[]{0, 0, 0, 0};
    private static final ThreadLocal<ObjectPool<UI3DScene.Plane>> TL_Plane_pool = ThreadLocal.withInitial(UI3DScene.PlaneObjectPool::new);
    private static final ThreadLocal<ObjectPool<UI3DScene.Ray>> TL_Ray_pool = ThreadLocal.withInitial(UI3DScene.RayObjectPool::new);
    static final float SMALL_NUM = 1.0E-8F;
    private final ArrayList<ConfigOption> options = new ArrayList<>();
    private final WorldMapRenderer.WorldMapBooleanOption BlurUnvisited = new WorldMapRenderer.WorldMapBooleanOption("BlurUnvisited", true);
    private final WorldMapRenderer.WorldMapBooleanOption BuildingsWithoutFeatures = new WorldMapRenderer.WorldMapBooleanOption(
        "BuildingsWithoutFeatures", false
    );
    private final WorldMapRenderer.WorldMapBooleanOption DebugInfo = new WorldMapRenderer.WorldMapBooleanOption("DebugInfo", false);
    private final WorldMapRenderer.WorldMapBooleanOption CellGrid = new WorldMapRenderer.WorldMapBooleanOption("CellGrid", false);
    private final WorldMapRenderer.WorldMapBooleanOption TileGrid = new WorldMapRenderer.WorldMapBooleanOption("TileGrid", false);
    private final WorldMapRenderer.WorldMapBooleanOption UnvisitedGrid = new WorldMapRenderer.WorldMapBooleanOption("UnvisitedGrid", true);
    private final WorldMapRenderer.WorldMapBooleanOption Features = new WorldMapRenderer.WorldMapBooleanOption("Features", true);
    private final WorldMapRenderer.WorldMapBooleanOption ForestZones = new WorldMapRenderer.WorldMapBooleanOption("ForestZones", false);
    private final WorldMapRenderer.WorldMapBooleanOption HideUnvisited = new WorldMapRenderer.WorldMapBooleanOption("HideUnvisited", false);
    private final WorldMapRenderer.WorldMapBooleanOption HitTest = new WorldMapRenderer.WorldMapBooleanOption("HitTest", false);
    private final WorldMapRenderer.WorldMapBooleanOption ImagePyramid = new WorldMapRenderer.WorldMapBooleanOption("ImagePyramid", false);
    private final WorldMapRenderer.WorldMapBooleanOption Isometric = new WorldMapRenderer.WorldMapBooleanOption("Isometric", true);
    private final WorldMapRenderer.WorldMapBooleanOption LineString = new WorldMapRenderer.WorldMapBooleanOption("LineString", true);
    private final WorldMapRenderer.WorldMapBooleanOption Players = new WorldMapRenderer.WorldMapBooleanOption("Players", false);
    private final WorldMapRenderer.WorldMapBooleanOption RemotePlayers = new WorldMapRenderer.WorldMapBooleanOption("RemotePlayers", false);
    private final WorldMapRenderer.WorldMapBooleanOption PlayerNames = new WorldMapRenderer.WorldMapBooleanOption("PlayerNames", false);
    private final WorldMapRenderer.WorldMapBooleanOption Symbols = new WorldMapRenderer.WorldMapBooleanOption("Symbols", true);
    private final WorldMapRenderer.WorldMapBooleanOption Wireframe = new WorldMapRenderer.WorldMapBooleanOption("Wireframe", false);
    private final WorldMapRenderer.WorldMapBooleanOption WorldBounds = new WorldMapRenderer.WorldMapBooleanOption("WorldBounds", true);
    private final WorldMapRenderer.WorldMapBooleanOption MiniMapSymbols = new WorldMapRenderer.WorldMapBooleanOption("MiniMapSymbols", false);
    private final WorldMapRenderer.WorldMapBooleanOption VisibleCells = new WorldMapRenderer.WorldMapBooleanOption("VisibleCells", false);

    public WorldMapRenderer() {
        PZArrayUtil.arrayPopulate(this.m_drawer, WorldMapRenderer.Drawer::new);
    }

    public int getAbsoluteX() {
        return this.m_x;
    }

    public int getAbsoluteY() {
        return this.m_y;
    }

    public int getWidth() {
        return this.m_width;
    }

    public int getHeight() {
        return this.m_height;
    }

    private void calcMatrices(float var1, float var2, float var3, Matrix4f matrix4f0, Matrix4f matrix4f1) {
        int int0 = this.getWidth();
        int int1 = this.getHeight();
        matrix4f0.setOrtho(-int0 / 2.0F, int0 / 2.0F, int1 / 2.0F, -int1 / 2.0F, -2000.0F, 2000.0F);
        matrix4f1.identity();
        if (this.Isometric.getValue()) {
            matrix4f1.rotateXYZ((float) (Math.PI / 3), 0.0F, (float) (Math.PI / 4));
        }
    }

    public Vector3f uiToScene(float uiX, float uiY, Matrix4f projection, Matrix4f modelView, Vector3f out) {
        UI3DScene.Plane plane = allocPlane();
        plane.point.set(0.0F);
        plane.normal.set(0.0F, 0.0F, 1.0F);
        UI3DScene.Ray ray = this.getCameraRay(uiX, this.getHeight() - uiY, projection, modelView, allocRay());
        if (this.intersect_ray_plane(plane, ray, out) != 1) {
            out.set(0.0F);
        }

        releasePlane(plane);
        releaseRay(ray);
        return out;
    }

    public Vector3f sceneToUI(float sceneX, float sceneY, float sceneZ, Matrix4f projection, Matrix4f modelView, Vector3f out) {
        Matrix4f matrix4f = allocMatrix4f();
        matrix4f.set(projection);
        matrix4f.mul(modelView);
        this.m_viewport[0] = 0;
        this.m_viewport[1] = 0;
        this.m_viewport[2] = this.getWidth();
        this.m_viewport[3] = this.getHeight();
        matrix4f.project(sceneX, sceneY, sceneZ, this.m_viewport, out);
        releaseMatrix4f(matrix4f);
        return out;
    }

    public float uiToWorldX(float uiX, float uiY, float zoomF, float centerWorldX, float centerWorldY) {
        Matrix4f matrix4f0 = allocMatrix4f();
        Matrix4f matrix4f1 = allocMatrix4f();
        this.calcMatrices(centerWorldX, centerWorldY, zoomF, matrix4f0, matrix4f1);
        float float0 = this.uiToWorldX(uiX, uiY, zoomF, centerWorldX, centerWorldY, matrix4f0, matrix4f1);
        releaseMatrix4f(matrix4f0);
        releaseMatrix4f(matrix4f1);
        return float0;
    }

    public float uiToWorldY(float uiX, float uiY, float zoomF, float centerWorldX, float centerWorldY) {
        Matrix4f matrix4f0 = allocMatrix4f();
        Matrix4f matrix4f1 = allocMatrix4f();
        this.calcMatrices(centerWorldX, centerWorldY, zoomF, matrix4f0, matrix4f1);
        float float0 = this.uiToWorldY(uiX, uiY, zoomF, centerWorldX, centerWorldY, matrix4f0, matrix4f1);
        releaseMatrix4f(matrix4f0);
        releaseMatrix4f(matrix4f1);
        return float0;
    }

    public float uiToWorldX(float uiX, float uiY, float zoomF, float centerWorldX, float centerWorldY, Matrix4f projection, Matrix4f modelView) {
        Vector3f vector3f = this.uiToScene(uiX, uiY, projection, modelView, allocVector3f());
        float float0 = this.getWorldScale(zoomF);
        vector3f.mul(1.0F / float0);
        float float1 = vector3f.x() + centerWorldX;
        releaseVector3f(vector3f);
        return float1;
    }

    public float uiToWorldY(float uiX, float uiY, float zoomF, float centerWorldX, float centerWorldY, Matrix4f projection, Matrix4f modelView) {
        Vector3f vector3f = this.uiToScene(uiX, uiY, projection, modelView, allocVector3f());
        float float0 = this.getWorldScale(zoomF);
        vector3f.mul(1.0F / float0);
        float float1 = vector3f.y() + centerWorldY;
        releaseVector3f(vector3f);
        return float1;
    }

    public float worldToUIX(float worldX, float worldY, float zoomF, float centerWorldX, float centerWorldY, Matrix4f projection, Matrix4f modelView) {
        float float0 = this.getWorldScale(zoomF);
        Vector3f vector3f = this.sceneToUI((worldX - centerWorldX) * float0, (worldY - centerWorldY) * float0, 0.0F, projection, modelView, allocVector3f());
        float float1 = vector3f.x();
        releaseVector3f(vector3f);
        return float1;
    }

    public float worldToUIY(float worldX, float worldY, float zoomF, float centerWorldX, float centerWorldY, Matrix4f projection, Matrix4f modelView) {
        float float0 = this.getWorldScale(zoomF);
        Vector3f vector3f = this.sceneToUI((worldX - centerWorldX) * float0, (worldY - centerWorldY) * float0, 0.0F, projection, modelView, allocVector3f());
        float float1 = this.getHeight() - vector3f.y();
        releaseVector3f(vector3f);
        return float1;
    }

    public float worldOriginUIX(float zoomF, float centerWorldX) {
        return this.worldToUIX(0.0F, 0.0F, zoomF, centerWorldX, this.m_centerWorldY, this.m_projection, this.m_modelView);
    }

    public float worldOriginUIY(float zoomF, float centerWorldY) {
        return this.worldToUIY(0.0F, 0.0F, zoomF, this.m_centerWorldX, centerWorldY, this.m_projection, this.m_modelView);
    }

    public int getZoom() {
        return this.m_zoom;
    }

    public float getZoomF() {
        return this.m_zoomF;
    }

    public float getDisplayZoomF() {
        return this.m_displayZoomF;
    }

    public float zoomMult() {
        return this.zoomMult(this.m_zoomF);
    }

    public float zoomMult(float zoomF) {
        return (float)Math.pow(2.0, zoomF);
    }

    public float getWorldScale(float zoomF) {
        int int0 = this.getHeight();
        double double0 = MapProjection.metersPerPixelAtZoom(zoomF, int0);
        return (float)(1.0 / double0);
    }

    public void zoomAt(int mouseX, int mouseY, int delta) {
        float float0 = this.uiToWorldX(mouseX, mouseY, this.m_displayZoomF, this.m_centerWorldX, this.m_centerWorldY);
        float float1 = this.uiToWorldY(mouseX, mouseY, this.m_displayZoomF, this.m_centerWorldX, this.m_centerWorldY);
        this.m_zoomF = PZMath.clamp(this.m_zoomF + delta / 2.0F, this.getBaseZoom(), 24.0F);
        this.m_zoom = (int)this.m_zoomF;
        this.m_zoomWorldX = float0;
        this.m_zoomWorldY = float1;
        this.m_zoomUIX = mouseX;
        this.m_zoomUIY = mouseY;
    }

    public float getCenterWorldX() {
        return this.m_centerWorldX;
    }

    public float getCenterWorldY() {
        return this.m_centerWorldY;
    }

    public void centerOn(float worldX, float worldY) {
        this.m_centerWorldX = worldX;
        this.m_centerWorldY = worldY;
        if (this.m_displayZoomF != this.m_zoomF) {
            this.m_zoomWorldX = worldX;
            this.m_zoomWorldY = worldY;
            this.m_zoomUIX = this.m_width / 2.0F;
            this.m_zoomUIY = this.m_height / 2.0F;
        }
    }

    public void moveView(int dx, int dy) {
        this.centerOn(this.m_centerWorldX + dx, this.m_centerWorldY + dy);
    }

    public double log2(double x) {
        return Math.log(x) / Math.log(2.0);
    }

    public float getBaseZoom() {
        double double0 = MapProjection.zoomAtMetersPerPixel((double)this.m_worldMap.getHeightInSquares() / this.getHeight(), this.getHeight());
        if (this.m_worldMap.getWidthInSquares() * this.getWorldScale((float)double0) > this.getWidth()) {
            double0 = MapProjection.zoomAtMetersPerPixel((double)this.m_worldMap.getWidthInSquares() / this.getWidth(), this.getHeight());
        }

        double0 = (int)(double0 * 2.0) / 2.0;
        return (float)double0;
    }

    public void setZoom(float zoom) {
        this.m_zoomF = PZMath.clamp(zoom, this.getBaseZoom(), 24.0F);
        this.m_zoom = (int)this.m_zoomF;
        this.m_displayZoomF = this.m_zoomF;
    }

    public void resetView() {
        this.m_zoomF = this.getBaseZoom();
        this.m_zoom = (int)this.m_zoomF;
        this.m_centerWorldX = this.m_worldMap.getMinXInSquares() + this.m_worldMap.getWidthInSquares() / 2.0F;
        this.m_centerWorldY = this.m_worldMap.getMinYInSquares() + this.m_worldMap.getHeightInSquares() / 2.0F;
        this.m_zoomWorldX = this.m_centerWorldX;
        this.m_zoomWorldY = this.m_centerWorldY;
        this.m_zoomUIX = this.getWidth() / 2.0F;
        this.m_zoomUIY = this.getHeight() / 2.0F;
    }

    public Matrix4f getProjectionMatrix() {
        return this.m_projection;
    }

    public Matrix4f getModelViewMatrix() {
        return this.m_modelView;
    }

    public void setMap(WorldMap worldMap, int x, int y, int width, int height) {
        this.m_worldMap = worldMap;
        this.m_x = x;
        this.m_y = y;
        this.m_width = width;
        this.m_height = height;
    }

    public WorldMap getWorldMap() {
        return this.m_worldMap;
    }

    public void setVisited(WorldMapVisited visited) {
        this.m_visited = visited;
    }

    public void updateView() {
        if (this.m_displayZoomF != this.m_zoomF) {
            float float0 = (float)(UIManager.getMillisSinceLastRender() / 750.0);
            float float1 = Math.abs(this.m_zoomF - this.m_displayZoomF);
            float float2 = float1 > 0.25F ? float1 / 0.25F : 1.0F;
            if (this.m_displayZoomF < this.m_zoomF) {
                this.m_displayZoomF = PZMath.min(this.m_displayZoomF + float0 * float2, this.m_zoomF);
            } else if (this.m_displayZoomF > this.m_zoomF) {
                this.m_displayZoomF = PZMath.max(this.m_displayZoomF - float0 * float2, this.m_zoomF);
            }

            float float3 = this.uiToWorldX(this.m_zoomUIX, this.m_zoomUIY, this.m_displayZoomF, 0.0F, 0.0F);
            float float4 = this.uiToWorldY(this.m_zoomUIX, this.m_zoomUIY, this.m_displayZoomF, 0.0F, 0.0F);
            this.m_centerWorldX = this.m_zoomWorldX - float3;
            this.m_centerWorldY = this.m_zoomWorldY - float4;
        }

        if (!this.m_firstUpdate) {
            this.m_firstUpdate = true;
            this.m_isometric = this.Isometric.getValue();
        }

        if (this.m_isometric != this.Isometric.getValue()) {
            this.m_isometric = this.Isometric.getValue();
            long long0 = System.currentTimeMillis();
            if (this.m_viewChangeTime + VIEW_CHANGE_TIME < long0) {
                this.m_modelViewChange.setFromUnnormalized(this.m_modelView);
            }

            this.m_viewChangeTime = long0;
        }

        this.calcMatrices(this.m_centerWorldX, this.m_centerWorldY, this.m_displayZoomF, this.m_projection, this.m_modelView);
        long long1 = System.currentTimeMillis();
        if (this.m_viewChangeTime + VIEW_CHANGE_TIME > long1) {
            float float5 = (float)(this.m_viewChangeTime + VIEW_CHANGE_TIME - long1) / (float)VIEW_CHANGE_TIME;
            Quaternionf quaternionf = allocQuaternionf().setFromUnnormalized(this.m_modelView);
            this.m_modelView.set(this.m_modelViewChange.slerp(quaternionf, 1.0F - float5));
            releaseQuaternionf(quaternionf);
        }
    }

    public void render(UIWorldMap ui) {
        this.m_style = ui.getAPI().getStyle();
        int int0 = SpriteRenderer.instance.getMainStateIndex();
        this.m_drawer[int0].init(this, ui);
        SpriteRenderer.instance.drawGeneric(this.m_drawer[int0]);
    }

    public void setDropShadowWidth(int width) {
        this.m_dropShadowWidth = width;
    }

    private static Matrix4f allocMatrix4f() {
        return BaseVehicle.TL_matrix4f_pool.get().alloc();
    }

    private static void releaseMatrix4f(Matrix4f matrix4f) {
        BaseVehicle.TL_matrix4f_pool.get().release(matrix4f);
    }

    private static Quaternionf allocQuaternionf() {
        return BaseVehicle.TL_quaternionf_pool.get().alloc();
    }

    private static void releaseQuaternionf(Quaternionf quaternionf) {
        BaseVehicle.TL_quaternionf_pool.get().release(quaternionf);
    }

    private static UI3DScene.Ray allocRay() {
        return TL_Ray_pool.get().alloc();
    }

    private static void releaseRay(UI3DScene.Ray ray) {
        TL_Ray_pool.get().release(ray);
    }

    private static UI3DScene.Plane allocPlane() {
        return TL_Plane_pool.get().alloc();
    }

    private static void releasePlane(UI3DScene.Plane plane) {
        TL_Plane_pool.get().release(plane);
    }

    private static Vector2 allocVector2() {
        return BaseVehicle.TL_vector2_pool.get().alloc();
    }

    private static void releaseVector2(Vector2 vector) {
        BaseVehicle.TL_vector2_pool.get().release(vector);
    }

    private static Vector3f allocVector3f() {
        return BaseVehicle.TL_vector3f_pool.get().alloc();
    }

    private static void releaseVector3f(Vector3f vector3f) {
        BaseVehicle.TL_vector3f_pool.get().release(vector3f);
    }

    UI3DScene.Ray getCameraRay(float float0, float float1, UI3DScene.Ray ray) {
        return this.getCameraRay(float0, float1, this.m_projection, this.m_modelView, ray);
    }

    UI3DScene.Ray getCameraRay(float float0, float float1, Matrix4f matrix4f1, Matrix4f matrix4f2, UI3DScene.Ray ray) {
        Matrix4f matrix4f0 = allocMatrix4f();
        matrix4f0.set(matrix4f1);
        matrix4f0.mul(matrix4f2);
        matrix4f0.invert();
        this.m_viewport[0] = 0;
        this.m_viewport[1] = 0;
        this.m_viewport[2] = this.getWidth();
        this.m_viewport[3] = this.getHeight();
        Vector3f vector3f0 = matrix4f0.unprojectInv(float0, float1, 0.0F, this.m_viewport, allocVector3f());
        Vector3f vector3f1 = matrix4f0.unprojectInv(float0, float1, 1.0F, this.m_viewport, allocVector3f());
        ray.origin.set(vector3f0);
        ray.direction.set(vector3f1.sub(vector3f0).normalize());
        releaseVector3f(vector3f1);
        releaseVector3f(vector3f0);
        releaseMatrix4f(matrix4f0);
        return ray;
    }

    int intersect_ray_plane(UI3DScene.Plane plane, UI3DScene.Ray ray, Vector3f vector3f2) {
        Vector3f vector3f0 = allocVector3f().set(ray.direction).mul(10000.0F);
        Vector3f vector3f1 = allocVector3f().set(ray.origin).sub(plane.point);

        byte byte0;
        try {
            float float0 = plane.normal.dot(vector3f0);
            float float1 = -plane.normal.dot(vector3f1);
            if (!(Math.abs(float0) < 1.0E-8F)) {
                float float2 = float1 / float0;
                if (!(float2 < 0.0F) && !(float2 > 1.0F)) {
                    vector3f2.set(ray.origin).add(vector3f0.mul(float2));
                    return 1;
                }

                return 0;
            }

            if (float1 != 0.0F) {
                return 0;
            }

            byte0 = 2;
        } finally {
            releaseVector3f(vector3f0);
            releaseVector3f(vector3f1);
        }

        return byte0;
    }

    public ConfigOption getOptionByName(String name) {
        for (int int0 = 0; int0 < this.options.size(); int0++) {
            ConfigOption configOption = this.options.get(int0);
            if (configOption.getName().equals(name)) {
                return configOption;
            }
        }

        return null;
    }

    public int getOptionCount() {
        return this.options.size();
    }

    public ConfigOption getOptionByIndex(int index) {
        return this.options.get(index);
    }

    public void setBoolean(String name, boolean value) {
        ConfigOption configOption = this.getOptionByName(name);
        if (configOption instanceof BooleanConfigOption) {
            ((BooleanConfigOption)configOption).setValue(value);
        }
    }

    public boolean getBoolean(String name) {
        ConfigOption configOption = this.getOptionByName(name);
        return configOption instanceof BooleanConfigOption ? ((BooleanConfigOption)configOption).getValue() : false;
    }

    public void setDouble(String name, double value) {
        ConfigOption configOption = this.getOptionByName(name);
        if (configOption instanceof DoubleConfigOption) {
            ((DoubleConfigOption)configOption).setValue(value);
        }
    }

    public double getDouble(String name, double defaultValue) {
        ConfigOption configOption = this.getOptionByName(name);
        return configOption instanceof DoubleConfigOption ? ((DoubleConfigOption)configOption).getValue() : defaultValue;
    }

    private static final class CharacterModelCamera extends ModelCamera {
        float m_worldScale;
        float m_angle;
        float m_playerX;
        float m_playerY;
        boolean m_bVehicle;

        @Override
        public void Begin() {
            Matrix4f matrix4f = WorldMapRenderer.allocMatrix4f();
            matrix4f.identity();
            matrix4f.translate(this.m_playerX * this.m_worldScale, this.m_playerY * this.m_worldScale, 0.0F);
            matrix4f.rotateX((float) (Math.PI / 2));
            matrix4f.rotateY(this.m_angle + (float) (Math.PI * 3.0 / 2.0));
            if (this.m_bVehicle) {
                matrix4f.scale(this.m_worldScale);
            } else {
                matrix4f.scale(1.5F * this.m_worldScale);
            }

            PZGLUtil.pushAndMultMatrix(5888, matrix4f);
            WorldMapRenderer.releaseMatrix4f(matrix4f);
        }

        @Override
        public void End() {
            PZGLUtil.popMatrix(5888);
        }
    }

    public static final class Drawer extends TextureDraw.GenericDrawer {
        WorldMapRenderer m_renderer;
        final WorldMapStyle m_style = new WorldMapStyle();
        WorldMap m_worldMap;
        int m_x;
        int m_y;
        int m_width;
        int m_height;
        float m_centerWorldX;
        float m_centerWorldY;
        int m_zoom = 0;
        public float m_zoomF = 0.0F;
        float m_worldScale;
        float m_renderOriginX;
        float m_renderOriginY;
        float m_renderCellX;
        float m_renderCellY;
        private final Matrix4f m_projection = new Matrix4f();
        private final Matrix4f m_modelView = new Matrix4f();
        private final WorldMapRenderer.PlayerRenderData[] m_playerRenderData = new WorldMapRenderer.PlayerRenderData[4];
        final WorldMapStyleLayer.FilterArgs m_filterArgs = new WorldMapStyleLayer.FilterArgs();
        final WorldMapStyleLayer.RenderArgs m_renderArgs = new WorldMapStyleLayer.RenderArgs();
        final ArrayList<WorldMapRenderLayer> m_renderLayers = new ArrayList<>();
        final ArrayList<WorldMapFeature> m_features = new ArrayList<>();
        final ArrayList<IsoMetaGrid.Zone> m_zones = new ArrayList<>();
        final HashSet<IsoMetaGrid.Zone> m_zoneSet = new HashSet<>();
        WorldMapStyleLayer.RGBAf m_fill;
        int m_triangulationsThisFrame = 0;
        float[] m_floatArray;
        final Vector2f m_vector2f = new Vector2f();
        final TIntArrayList m_rasterizeXY = new TIntArrayList();
        final TIntSet m_rasterizeSet = new TIntHashSet();
        float m_rasterizeMinTileX;
        float m_rasterizeMinTileY;
        float m_rasterizeMaxTileX;
        float m_rasterizeMaxTileY;
        final Rasterize m_rasterize = new Rasterize();
        int[] m_rasterizeXY_ints;
        int m_rasterizeMult = 1;

        Drawer() {
            PZArrayUtil.arrayPopulate(this.m_playerRenderData, WorldMapRenderer.PlayerRenderData::new);
        }

        void init(WorldMapRenderer worldMapRenderer, UIWorldMap uIWorldMap) {
            this.m_renderer = worldMapRenderer;
            this.m_style.copyFrom(this.m_renderer.m_style);
            this.m_worldMap = worldMapRenderer.m_worldMap;
            this.m_x = worldMapRenderer.m_x;
            this.m_y = worldMapRenderer.m_y;
            this.m_width = worldMapRenderer.m_width;
            this.m_height = worldMapRenderer.m_height;
            this.m_centerWorldX = worldMapRenderer.m_centerWorldX;
            this.m_centerWorldY = worldMapRenderer.m_centerWorldY;
            this.m_zoomF = worldMapRenderer.m_displayZoomF;
            this.m_zoom = (int)this.m_zoomF;
            this.m_worldScale = this.getWorldScale();
            this.m_renderOriginX = (this.m_renderer.m_worldMap.getMinXInSquares() - this.m_centerWorldX) * this.m_worldScale;
            this.m_renderOriginY = (this.m_renderer.m_worldMap.getMinYInSquares() - this.m_centerWorldY) * this.m_worldScale;
            this.m_projection.set(worldMapRenderer.m_projection);
            this.m_modelView.set(worldMapRenderer.m_modelView);
            this.m_fill = uIWorldMap.m_color;
            this.m_triangulationsThisFrame = 0;
            if (this.m_renderer.m_visited != null) {
                this.m_renderer.m_visited.renderMain();
            }

            for (int int0 = 0; int0 < 4; int0++) {
                this.m_playerRenderData[int0].m_modelSlotRenderData = null;
            }

            if (this.m_renderer.Players.getValue() && this.m_zoomF >= 20.0F) {
                for (int int1 = 0; int1 < 4; int1++) {
                    IsoPlayer player = IsoPlayer.players[int1];
                    if (player != null && !player.isDead() && player.legsSprite.hasActiveModel()) {
                        float float0 = player.x;
                        float float1 = player.y;
                        if (player.getVehicle() != null) {
                            float0 = player.getVehicle().getX();
                            float1 = player.getVehicle().getY();
                        }

                        float float2 = this.m_renderer
                            .worldToUIX(float0, float1, this.m_zoomF, this.m_centerWorldX, this.m_centerWorldY, this.m_projection, this.m_modelView);
                        float float3 = this.m_renderer
                            .worldToUIY(float0, float1, this.m_zoomF, this.m_centerWorldX, this.m_centerWorldY, this.m_projection, this.m_modelView);
                        if (!(float2 < -100.0F) && !(float2 > this.m_width + 100) && !(float3 < -100.0F) && !(float3 > this.m_height + 100)) {
                            this.m_playerRenderData[int1].m_angle = player.getVehicle() == null
                                ? player.getAnimationPlayer().getAngle()
                                : (float) (Math.PI * 3.0 / 2.0);
                            this.m_playerRenderData[int1].m_x = float0 - this.m_centerWorldX;
                            this.m_playerRenderData[int1].m_y = float1 - this.m_centerWorldY;
                            player.legsSprite.modelSlot.model.updateLights();
                            int int2 = IsoCamera.frameState.playerIndex;
                            IsoCamera.frameState.playerIndex = int1;
                            player.checkUpdateModelTextures();
                            this.m_playerRenderData[int1].m_modelSlotRenderData = ModelSlotRenderData.alloc().init(player.legsSprite.modelSlot);
                            this.m_playerRenderData[int1].m_modelSlotRenderData.centerOfMassY = 0.0F;
                            IsoCamera.frameState.playerIndex = int2;
                            player.legsSprite.modelSlot.renderRefCount++;
                        }
                    }
                }
            }
        }

        public int getAbsoluteX() {
            return this.m_x;
        }

        public int getAbsoluteY() {
            return this.m_y;
        }

        public int getWidth() {
            return this.m_width;
        }

        public int getHeight() {
            return this.m_height;
        }

        public float getWorldScale() {
            return this.m_renderer.getWorldScale(this.m_zoomF);
        }

        public float uiToWorldX(float uiX, float uiY) {
            return this.m_renderer.uiToWorldX(uiX, uiY, this.m_zoomF, this.m_centerWorldX, this.m_centerWorldY, this.m_projection, this.m_modelView);
        }

        public float uiToWorldY(float uiX, float uiY) {
            return this.m_renderer.uiToWorldY(uiX, uiY, this.m_zoomF, this.m_centerWorldX, this.m_centerWorldY, this.m_projection, this.m_modelView);
        }

        public float worldOriginUIX(float centerWorldX) {
            return this.m_renderer.worldOriginUIX(this.m_zoomF, centerWorldX);
        }

        public float worldOriginUIY(float centerWorldY) {
            return this.m_renderer.worldOriginUIY(this.m_zoomF, centerWorldY);
        }

        private void renderCellFeatures() {
            for (byte byte0 = 0; byte0 < this.m_rasterizeXY.size() - 1; byte0 += 2) {
                int int0 = this.m_rasterizeXY_ints[byte0];
                int int1 = this.m_rasterizeXY_ints[byte0 + 1];
                if (this.m_renderer.m_visited == null || this.m_renderer.m_visited.isCellVisible(int0, int1)) {
                    this.m_features.clear();

                    for (int int2 = 0; int2 < this.m_worldMap.m_data.size(); int2++) {
                        WorldMapData worldMapData = this.m_worldMap.m_data.get(int2);
                        if (worldMapData.isReady()) {
                            WorldMapCell worldMapCell = worldMapData.getCell(int0, int1);
                            if (worldMapCell != null && !worldMapCell.m_features.isEmpty()) {
                                this.m_features.addAll(worldMapCell.m_features);
                                if (this.m_worldMap.isLastDataInDirectory(worldMapData)) {
                                    break;
                                }
                            }
                        }
                    }

                    if (this.m_features.isEmpty()) {
                        this.m_renderArgs.renderer = this.m_renderer;
                        this.m_renderArgs.drawer = this;
                        this.m_renderArgs.cellX = int0;
                        this.m_renderArgs.cellY = int1;
                        this.m_renderCellX = this.m_renderOriginX + (int0 * 300 - this.m_worldMap.getMinXInSquares()) * this.m_worldScale;
                        this.m_renderCellY = this.m_renderOriginY + (int1 * 300 - this.m_worldMap.getMinYInSquares()) * this.m_worldScale;

                        for (int int3 = 0; int3 < this.m_style.m_layers.size(); int3++) {
                            WorldMapStyleLayer worldMapStyleLayer = this.m_style.m_layers.get(int3);
                            if (worldMapStyleLayer instanceof WorldMapTextureStyleLayer) {
                                worldMapStyleLayer.renderCell(this.m_renderArgs);
                            }
                        }
                    } else {
                        this.renderCell(int0, int1, this.m_features);
                    }
                }
            }
        }

        private void renderCell(int int0, int int1, ArrayList<WorldMapFeature> arrayList) {
            this.m_renderCellX = this.m_renderOriginX + (int0 * 300 - this.m_worldMap.getMinXInSquares()) * this.m_worldScale;
            this.m_renderCellY = this.m_renderOriginY + (int1 * 300 - this.m_worldMap.getMinYInSquares()) * this.m_worldScale;
            WorldMapRenderLayer.s_pool.release(this.m_renderLayers);
            this.m_renderLayers.clear();
            this.m_filterArgs.renderer = this.m_renderer;
            this.filterFeatures(arrayList, this.m_filterArgs, this.m_renderLayers);
            this.m_renderArgs.renderer = this.m_renderer;
            this.m_renderArgs.drawer = this;
            this.m_renderArgs.cellX = int0;
            this.m_renderArgs.cellY = int1;

            for (int int2 = 0; int2 < this.m_renderLayers.size(); int2++) {
                WorldMapRenderLayer worldMapRenderLayer = this.m_renderLayers.get(int2);
                worldMapRenderLayer.m_styleLayer.renderCell(this.m_renderArgs);

                for (int int3 = 0; int3 < worldMapRenderLayer.m_features.size(); int3++) {
                    WorldMapFeature worldMapFeature = worldMapRenderLayer.m_features.get(int3);
                    worldMapRenderLayer.m_styleLayer.render(worldMapFeature, this.m_renderArgs);
                }
            }
        }

        void filterFeatures(ArrayList<WorldMapFeature> arrayList1, WorldMapStyleLayer.FilterArgs filterArgs, ArrayList<WorldMapRenderLayer> arrayList0) {
            for (int int0 = 0; int0 < this.m_style.m_layers.size(); int0++) {
                WorldMapStyleLayer worldMapStyleLayer = this.m_style.m_layers.get(int0);
                if (!(worldMapStyleLayer.m_minZoom > this.m_zoomF)) {
                    if (worldMapStyleLayer.m_id.equals("mylayer")) {
                        boolean boolean0 = true;
                    }

                    WorldMapRenderLayer worldMapRenderLayer = null;
                    if (worldMapStyleLayer instanceof WorldMapTextureStyleLayer) {
                        worldMapRenderLayer = WorldMapRenderLayer.s_pool.alloc();
                        worldMapRenderLayer.m_styleLayer = worldMapStyleLayer;
                        worldMapRenderLayer.m_features.clear();
                        arrayList0.add(worldMapRenderLayer);
                    } else {
                        for (int int1 = 0; int1 < arrayList1.size(); int1++) {
                            WorldMapFeature worldMapFeature = (WorldMapFeature)arrayList1.get(int1);
                            if (worldMapStyleLayer.filter(worldMapFeature, filterArgs)) {
                                if (worldMapRenderLayer == null) {
                                    worldMapRenderLayer = WorldMapRenderLayer.s_pool.alloc();
                                    worldMapRenderLayer.m_styleLayer = worldMapStyleLayer;
                                    worldMapRenderLayer.m_features.clear();
                                    arrayList0.add(worldMapRenderLayer);
                                }

                                worldMapRenderLayer.m_features.add(worldMapFeature);
                            }
                        }
                    }
                }
            }
        }

        void renderCellGrid(int int0, int int1, int int2, int int3) {
            float float0 = this.m_renderOriginX + (int0 * 300 - this.m_worldMap.getMinXInSquares()) * this.m_worldScale;
            float float1 = this.m_renderOriginY + (int1 * 300 - this.m_worldMap.getMinYInSquares()) * this.m_worldScale;
            float float2 = float0 + (int2 - int0 + 1) * 300 * this.m_worldScale;
            float float3 = float1 + (int3 - int1 + 1) * 300 * this.m_worldScale;
            WorldMapRenderer.m_vboLines.setMode(1);
            WorldMapRenderer.m_vboLines.setLineWidth(1.0F);

            for (int int4 = int0; int4 <= int2 + 1; int4++) {
                WorldMapRenderer.m_vboLines
                    .addLine(
                        this.m_renderOriginX + (int4 * 300 - this.m_worldMap.getMinXInSquares()) * this.m_worldScale,
                        float1,
                        0.0F,
                        this.m_renderOriginX + (int4 * 300 - this.m_worldMap.getMinXInSquares()) * this.m_worldScale,
                        float3,
                        0.0F,
                        0.25F,
                        0.25F,
                        0.25F,
                        1.0F
                    );
            }

            for (int int5 = int1; int5 <= int3 + 1; int5++) {
                WorldMapRenderer.m_vboLines
                    .addLine(
                        float0,
                        this.m_renderOriginY + (int5 * 300 - this.m_worldMap.getMinYInSquares()) * this.m_worldScale,
                        0.0F,
                        float2,
                        this.m_renderOriginY + (int5 * 300 - this.m_worldMap.getMinYInSquares()) * this.m_worldScale,
                        0.0F,
                        0.25F,
                        0.25F,
                        0.25F,
                        1.0F
                    );
            }

            WorldMapRenderer.m_vboLines.flush();
        }

        void renderPlayers() {
            boolean boolean0 = true;

            for (int int0 = 0; int0 < this.m_playerRenderData.length; int0++) {
                WorldMapRenderer.PlayerRenderData playerRenderData = this.m_playerRenderData[int0];
                if (playerRenderData.m_modelSlotRenderData != null) {
                    if (boolean0) {
                        GL11.glClear(256);
                        boolean0 = false;
                    }

                    this.m_renderer.m_CharacterModelCamera.m_worldScale = this.m_worldScale;
                    this.m_renderer.m_CharacterModelCamera.m_bUseWorldIso = true;
                    this.m_renderer.m_CharacterModelCamera.m_angle = playerRenderData.m_angle;
                    this.m_renderer.m_CharacterModelCamera.m_playerX = playerRenderData.m_x;
                    this.m_renderer.m_CharacterModelCamera.m_playerY = playerRenderData.m_y;
                    this.m_renderer.m_CharacterModelCamera.m_bVehicle = playerRenderData.m_modelSlotRenderData.bInVehicle;
                    ModelCamera.instance = this.m_renderer.m_CharacterModelCamera;
                    playerRenderData.m_modelSlotRenderData.render();
                }
            }

            if (UIManager.useUIFBO) {
                GL14.glBlendFuncSeparate(770, 771, 1, 771);
            }
        }

        public void drawLineStringXXX(WorldMapStyleLayer.RenderArgs args, WorldMapFeature feature, WorldMapStyleLayer.RGBAf color, float lineWidth) {
            float float0 = this.m_renderCellX;
            float float1 = this.m_renderCellY;
            float float2 = this.m_worldScale;
            float float3 = color.r;
            float float4 = color.g;
            float float5 = color.b;
            float float6 = color.a;

            for (int int0 = 0; int0 < feature.m_geometries.size(); int0++) {
                WorldMapGeometry worldMapGeometry = feature.m_geometries.get(int0);
                switch (worldMapGeometry.m_type) {
                    case LineString:
                        WorldMapRenderer.m_vboLines.setMode(1);
                        WorldMapRenderer.m_vboLines.setLineWidth(lineWidth);

                        for (int int1 = 0; int1 < worldMapGeometry.m_points.size(); int1++) {
                            WorldMapPoints worldMapPoints = worldMapGeometry.m_points.get(int1);

                            for (int int2 = 0; int2 < worldMapPoints.numPoints() - 1; int2++) {
                                float float7 = worldMapPoints.getX(int2);
                                float float8 = worldMapPoints.getY(int2);
                                float float9 = worldMapPoints.getX(int2 + 1);
                                float float10 = worldMapPoints.getY(int2 + 1);
                                WorldMapRenderer.m_vboLines
                                    .addLine(
                                        float0 + float7 * float2,
                                        float1 + float8 * float2,
                                        0.0F,
                                        float0 + float9 * float2,
                                        float1 + float10 * float2,
                                        0.0F,
                                        float3,
                                        float4,
                                        float5,
                                        float6
                                    );
                            }
                        }
                }
            }
        }

        public void drawLineStringYYY(WorldMapStyleLayer.RenderArgs args, WorldMapFeature feature, WorldMapStyleLayer.RGBAf color, float lineWidth) {
            float float0 = this.m_renderCellX;
            float float1 = this.m_renderCellY;
            float float2 = this.m_worldScale;
            float float3 = color.r;
            float float4 = color.g;
            float float5 = color.b;
            float float6 = color.a;

            for (int int0 = 0; int0 < feature.m_geometries.size(); int0++) {
                WorldMapGeometry worldMapGeometry = feature.m_geometries.get(int0);
                switch (worldMapGeometry.m_type) {
                    case LineString:
                        StrokeGeometry.Point[] points = new StrokeGeometry.Point[worldMapGeometry.m_points.size()];
                        WorldMapPoints worldMapPoints = worldMapGeometry.m_points.get(0);

                        for (int int1 = 0; int1 < worldMapPoints.numPoints(); int1++) {
                            float float7 = worldMapPoints.getX(int1);
                            float float8 = worldMapPoints.getY(int1);
                            points[int1] = StrokeGeometry.newPoint(float0 + float7 * float2, float1 + float8 * float2);
                        }

                        StrokeGeometry.Attrs attrs = new StrokeGeometry.Attrs();
                        attrs.join = "miter";
                        attrs.width = lineWidth;
                        ArrayList arrayList = StrokeGeometry.getStrokeGeometry(points, attrs);
                        if (arrayList != null) {
                            WorldMapRenderer.m_vboLines.setMode(4);

                            for (int int2 = 0; int2 < arrayList.size(); int2++) {
                                float float9 = (float)((StrokeGeometry.Point)arrayList.get(int2)).x;
                                float float10 = (float)((StrokeGeometry.Point)arrayList.get(int2)).y;
                                WorldMapRenderer.m_vboLines.addElement(float9, float10, 0.0F, float3, float4, float5, float6);
                            }

                            StrokeGeometry.release(arrayList);
                        }
                }
            }
        }

        public void drawLineString(WorldMapStyleLayer.RenderArgs args, WorldMapFeature feature, WorldMapStyleLayer.RGBAf color, float lineWidth) {
            if (this.m_renderer.LineString.getValue()) {
                float float0 = this.m_renderCellX;
                float float1 = this.m_renderCellY;
                float float2 = this.m_worldScale;
                float float3 = color.r;
                float float4 = color.g;
                float float5 = color.b;
                float float6 = color.a;
                WorldMapRenderer.m_vboLines.flush();
                WorldMapRenderer.m_vboLinesUV.flush();

                for (int int0 = 0; int0 < feature.m_geometries.size(); int0++) {
                    WorldMapGeometry worldMapGeometry = feature.m_geometries.get(int0);
                    switch (worldMapGeometry.m_type) {
                        case LineString:
                            WorldMapPoints worldMapPoints = worldMapGeometry.m_points.get(0);
                            if (this.m_floatArray == null || this.m_floatArray.length < worldMapPoints.numPoints() * 2) {
                                this.m_floatArray = new float[worldMapPoints.numPoints() * 2];
                            }

                            for (int int1 = 0; int1 < worldMapPoints.numPoints(); int1++) {
                                float float7 = worldMapPoints.getX(int1);
                                float float8 = worldMapPoints.getY(int1);
                                this.m_floatArray[int1 * 2] = float0 + float7 * float2;
                                this.m_floatArray[int1 * 2 + 1] = float1 + float8 * float2;
                            }

                            GL13.glActiveTexture(33984);
                            GL11.glDisable(3553);
                            GL11.glEnable(3042);
                    }
                }
            }
        }

        public void drawLineStringTexture(
            WorldMapStyleLayer.RenderArgs args, WorldMapFeature feature, WorldMapStyleLayer.RGBAf color, float lineWidth, Texture texture
        ) {
            float float0 = this.m_renderCellX;
            float float1 = this.m_renderCellY;
            float float2 = this.m_worldScale;
            if (texture != null && texture.isReady()) {
                if (texture.getID() == -1) {
                    texture.bind();
                }

                for (int int0 = 0; int0 < feature.m_geometries.size(); int0++) {
                    WorldMapGeometry worldMapGeometry = feature.m_geometries.get(int0);
                    if (worldMapGeometry.m_type == WorldMapGeometry.Type.LineString) {
                        WorldMapRenderer.m_vboLinesUV.setMode(7);
                        WorldMapRenderer.m_vboLinesUV.startRun(texture.getTextureId());
                        float float3 = lineWidth;
                        WorldMapPoints worldMapPoints = worldMapGeometry.m_points.get(0);

                        for (int int1 = 0; int1 < worldMapPoints.numPoints() - 1; int1++) {
                            float float4 = float0 + worldMapPoints.getX(int1) * float2;
                            float float5 = float1 + worldMapPoints.getY(int1) * float2;
                            float float6 = float0 + worldMapPoints.getX(int1 + 1) * float2;
                            float float7 = float1 + worldMapPoints.getY(int1 + 1) * float2;
                            float float8 = float7 - float5;
                            float float9 = -(float6 - float4);
                            Vector2f vector2f = this.m_vector2f.set(float8, float9);
                            vector2f.normalize();
                            float float10 = float4 + vector2f.x * float3 / 2.0F;
                            float float11 = float5 + vector2f.y * float3 / 2.0F;
                            float float12 = float6 + vector2f.x * float3 / 2.0F;
                            float float13 = float7 + vector2f.y * float3 / 2.0F;
                            float float14 = float6 - vector2f.x * float3 / 2.0F;
                            float float15 = float7 - vector2f.y * float3 / 2.0F;
                            float float16 = float4 - vector2f.x * float3 / 2.0F;
                            float float17 = float5 - vector2f.y * float3 / 2.0F;
                            float float18 = Vector2f.length(float6 - float4, float7 - float5);
                            float float19 = 0.0F;
                            float float20 = float18 / (float3 * ((float)texture.getHeight() / texture.getWidth()));
                            float float21 = 0.0F;
                            float float22 = 0.0F;
                            float float23 = 1.0F;
                            float float24 = 0.0F;
                            float float25 = 1.0F;
                            float float26 = float18 / (float3 * ((float)texture.getHeight() / texture.getWidth()));
                            WorldMapRenderer.m_vboLinesUV
                                .addQuad(
                                    float10,
                                    float11,
                                    float19,
                                    float20,
                                    float12,
                                    float13,
                                    float21,
                                    float22,
                                    float14,
                                    float15,
                                    float23,
                                    float24,
                                    float16,
                                    float17,
                                    float25,
                                    float26,
                                    0.0F,
                                    color.r,
                                    color.g,
                                    color.b,
                                    color.a
                                );
                        }
                    }
                }
            }
        }

        public void fillPolygon(WorldMapStyleLayer.RenderArgs args, WorldMapFeature feature, WorldMapStyleLayer.RGBAf color) {
            WorldMapRenderer.m_vboLinesUV.flush();
            float float0 = this.m_renderCellX;
            float float1 = this.m_renderCellY;
            float float2 = this.m_worldScale;
            float float3 = color.r;
            float float4 = color.g;
            float float5 = color.b;
            float float6 = color.a;

            for (int int0 = 0; int0 < feature.m_geometries.size(); int0++) {
                WorldMapGeometry worldMapGeometry = feature.m_geometries.get(int0);
                if (worldMapGeometry.m_type == WorldMapGeometry.Type.Polygon) {
                    boolean boolean0 = false;
                    if (worldMapGeometry.m_triangles == null) {
                        if (this.m_triangulationsThisFrame > 500) {
                            continue;
                        }

                        this.m_triangulationsThisFrame++;
                        double[] doubles = feature.m_properties.containsKey("highway") ? new double[]{1.0, 2.0, 4.0, 8.0, 12.0, 18.0} : null;
                        worldMapGeometry.triangulate(doubles);
                        if (worldMapGeometry.m_triangles == null) {
                            if (!Core.bDebug) {
                                continue;
                            }

                            WorldMapRenderer.m_vboLines.setMode(1);
                            float3 = 1.0F;
                            float5 = 0.0F;
                            float4 = 0.0F;
                            WorldMapRenderer.m_vboLines.setLineWidth(4.0F);

                            for (int int1 = 0; int1 < worldMapGeometry.m_points.size(); int1++) {
                                WorldMapPoints worldMapPoints = worldMapGeometry.m_points.get(int1);

                                for (int int2 = 0; int2 < worldMapPoints.numPoints(); int2++) {
                                    int int3 = worldMapPoints.getX(int2);
                                    int int4 = worldMapPoints.getY(int2);
                                    int int5 = worldMapPoints.getX((int2 + 1) % worldMapPoints.numPoints());
                                    int int6 = worldMapPoints.getY((int2 + 1) % worldMapPoints.numPoints());
                                    WorldMapRenderer.m_vboLines.reserve(2);
                                    WorldMapRenderer.m_vboLines
                                        .addElement(float0 + int3 * float2, float1 + int4 * float2, 0.0F, float3, float4, float5, float6);
                                    WorldMapRenderer.m_vboLines
                                        .addElement(float0 + int5 * float2, float1 + int6 * float2, 0.0F, float3, float4, float5, float6);
                                }
                            }

                            WorldMapRenderer.m_vboLines.setLineWidth(1.0F);
                            continue;
                        }

                        if (boolean0) {
                            this.uploadTrianglesToVBO(worldMapGeometry);
                        }
                    }

                    if (boolean0) {
                        GL11.glTranslatef(float0, float1, 0.0F);
                        GL11.glScalef(float2, float2, float2);
                        GL11.glColor4f(float3, float4, float5, float6);
                        if (worldMapGeometry.m_triangles.length / 2 > 2340) {
                            int int7 = PZMath.min(worldMapGeometry.m_triangles.length / 2, 2340);
                            WorldMapVBOs.getInstance().drawElements(4, worldMapGeometry.m_vboIndex1, worldMapGeometry.m_vboIndex2, int7);
                            WorldMapVBOs.getInstance()
                                .drawElements(4, worldMapGeometry.m_vboIndex3, worldMapGeometry.m_vboIndex4, worldMapGeometry.m_triangles.length / 2 - int7);
                        } else {
                            WorldMapVBOs.getInstance()
                                .drawElements(4, worldMapGeometry.m_vboIndex1, worldMapGeometry.m_vboIndex2, worldMapGeometry.m_triangles.length / 2);
                        }

                        GL11.glScalef(1.0F / float2, 1.0F / float2, 1.0F / float2);
                        GL11.glTranslatef(-float0, -float1, 0.0F);
                    } else {
                        WorldMapRenderer.m_vboLines.setMode(4);
                        double double0 = 0.0;
                        if (this.m_zoomF <= 11.5) {
                            double0 = 18.0;
                        } else if (this.m_zoomF <= 12.0) {
                            double0 = 12.0;
                        } else if (this.m_zoomF <= 12.5) {
                            double0 = 8.0;
                        } else if (this.m_zoomF <= 13.0) {
                            double0 = 4.0;
                        } else if (this.m_zoomF <= 13.5) {
                            double0 = 2.0;
                        } else if (this.m_zoomF <= 14.0) {
                            double0 = 1.0;
                        }

                        WorldMapGeometry.TrianglesPerZoom trianglesPerZoom = double0 == 0.0 ? null : worldMapGeometry.findTriangles(double0);
                        if (trianglesPerZoom != null) {
                            float[] floats0 = trianglesPerZoom.m_triangles;

                            for (byte byte0 = 0; byte0 < floats0.length; byte0 += 6) {
                                float float7 = floats0[byte0];
                                float float8 = floats0[byte0 + 1];
                                float float9 = floats0[byte0 + 2];
                                float float10 = floats0[byte0 + 3];
                                float float11 = floats0[byte0 + 4];
                                float float12 = floats0[byte0 + 5];
                                WorldMapRenderer.m_vboLines.reserve(3);
                                float float13 = 1.0F;
                                WorldMapRenderer.m_vboLines
                                    .addElement(
                                        float0 + float7 * float2, float1 + float8 * float2, 0.0F, float3 * float13, float4 * float13, float5 * float13, float6
                                    );
                                WorldMapRenderer.m_vboLines
                                    .addElement(
                                        float0 + float9 * float2, float1 + float10 * float2, 0.0F, float3 * float13, float4 * float13, float5 * float13, float6
                                    );
                                WorldMapRenderer.m_vboLines
                                    .addElement(
                                        float0 + float11 * float2,
                                        float1 + float12 * float2,
                                        0.0F,
                                        float3 * float13,
                                        float4 * float13,
                                        float5 * float13,
                                        float6
                                    );
                            }
                        } else {
                            float[] floats1 = worldMapGeometry.m_triangles;

                            for (byte byte1 = 0; byte1 < floats1.length; byte1 += 6) {
                                float float14 = floats1[byte1];
                                float float15 = floats1[byte1 + 1];
                                float float16 = floats1[byte1 + 2];
                                float float17 = floats1[byte1 + 3];
                                float float18 = floats1[byte1 + 4];
                                float float19 = floats1[byte1 + 5];
                                WorldMapRenderer.m_vboLines.reserve(3);
                                WorldMapRenderer.m_vboLines
                                    .addElement(float0 + float14 * float2, float1 + float15 * float2, 0.0F, float3, float4, float5, float6);
                                WorldMapRenderer.m_vboLines
                                    .addElement(float0 + float16 * float2, float1 + float17 * float2, 0.0F, float3, float4, float5, float6);
                                WorldMapRenderer.m_vboLines
                                    .addElement(float0 + float18 * float2, float1 + float19 * float2, 0.0F, float3, float4, float5, float6);
                            }
                        }
                    }
                }
            }
        }

        public void fillPolygon(
            WorldMapStyleLayer.RenderArgs args, WorldMapFeature feature, WorldMapStyleLayer.RGBAf color, Texture texture, float textureScale
        ) {
            WorldMapRenderer.m_vboLines.flush();
            float float0 = this.m_renderCellX;
            float float1 = this.m_renderCellY;
            float float2 = this.m_worldScale;
            float float3 = color.r;
            float float4 = color.g;
            float float5 = color.b;
            float float6 = color.a;

            for (int int0 = 0; int0 < feature.m_geometries.size(); int0++) {
                WorldMapGeometry worldMapGeometry = feature.m_geometries.get(int0);
                if (worldMapGeometry.m_type == WorldMapGeometry.Type.Polygon) {
                    if (worldMapGeometry.m_triangles == null) {
                        worldMapGeometry.triangulate(null);
                        if (worldMapGeometry.m_triangles == null) {
                            continue;
                        }
                    }

                    GL11.glEnable(3553);
                    GL11.glTexParameteri(3553, 10241, 9728);
                    GL11.glTexParameteri(3553, 10240, 9728);
                    WorldMapRenderer.m_vboLinesUV.setMode(4);
                    WorldMapRenderer.m_vboLinesUV.startRun(texture.getTextureId());
                    float[] floats = worldMapGeometry.m_triangles;
                    float float7 = args.cellX * 300 + worldMapGeometry.m_minX;
                    float float8 = args.cellY * 300 + worldMapGeometry.m_minY;
                    float float9 = texture.getWidth() * textureScale;
                    float float10 = texture.getHeight() * textureScale;
                    float float11 = texture.getWidthHW();
                    float float12 = texture.getHeightHW();
                    float float13 = PZMath.floor(float7 / float9) * float9;
                    float float14 = PZMath.floor(float8 / float10) * float10;

                    for (byte byte0 = 0; byte0 < floats.length; byte0 += 6) {
                        float float15 = floats[byte0];
                        float float16 = floats[byte0 + 1];
                        float float17 = floats[byte0 + 2];
                        float float18 = floats[byte0 + 3];
                        float float19 = floats[byte0 + 4];
                        float float20 = floats[byte0 + 5];
                        float float21 = (float15 + args.cellX * 300 - float13) / textureScale;
                        float float22 = (float16 + args.cellY * 300 - float14) / textureScale;
                        float float23 = (float17 + args.cellX * 300 - float13) / textureScale;
                        float float24 = (float18 + args.cellY * 300 - float14) / textureScale;
                        float float25 = (float19 + args.cellX * 300 - float13) / textureScale;
                        float float26 = (float20 + args.cellY * 300 - float14) / textureScale;
                        float15 = float0 + float15 * float2;
                        float16 = float1 + float16 * float2;
                        float17 = float0 + float17 * float2;
                        float18 = float1 + float18 * float2;
                        float19 = float0 + float19 * float2;
                        float20 = float1 + float20 * float2;
                        float float27 = float21 / float11;
                        float float28 = float22 / float12;
                        float float29 = float23 / float11;
                        float float30 = float24 / float12;
                        float float31 = float25 / float11;
                        float float32 = float26 / float12;
                        WorldMapRenderer.m_vboLinesUV.reserve(3);
                        WorldMapRenderer.m_vboLinesUV.addElement(float15, float16, 0.0F, float27, float28, float3, float4, float5, float6);
                        WorldMapRenderer.m_vboLinesUV.addElement(float17, float18, 0.0F, float29, float30, float3, float4, float5, float6);
                        WorldMapRenderer.m_vboLinesUV.addElement(float19, float20, 0.0F, float31, float32, float3, float4, float5, float6);
                    }

                    GL11.glDisable(3553);
                }
            }
        }

        void uploadTrianglesToVBO(WorldMapGeometry worldMapGeometry) {
            int[] ints = new int[2];
            int int0 = worldMapGeometry.m_triangles.length / 2;
            if (int0 > 2340) {
                int int1 = 0;

                while (int0 > 0) {
                    int int2 = PZMath.min(int0 / 3, 780);
                    WorldMapVBOs.getInstance().reserveVertices(int2 * 3, ints);
                    if (worldMapGeometry.m_vboIndex1 == -1) {
                        worldMapGeometry.m_vboIndex1 = ints[0];
                        worldMapGeometry.m_vboIndex2 = ints[1];
                    } else {
                        worldMapGeometry.m_vboIndex3 = ints[0];
                        worldMapGeometry.m_vboIndex4 = ints[1];
                    }

                    float[] floats0 = worldMapGeometry.m_triangles;
                    int int3 = int1 * 3 * 2;

                    for (int int4 = (int1 + int2) * 3 * 2; int3 < int4; int3 += 6) {
                        float float0 = floats0[int3];
                        float float1 = floats0[int3 + 1];
                        float float2 = floats0[int3 + 2];
                        float float3 = floats0[int3 + 3];
                        float float4 = floats0[int3 + 4];
                        float float5 = floats0[int3 + 5];
                        WorldMapVBOs.getInstance().addElement(float0, float1, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
                        WorldMapVBOs.getInstance().addElement(float2, float3, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
                        WorldMapVBOs.getInstance().addElement(float4, float5, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
                    }

                    int1 += int2;
                    int0 -= int2 * 3;
                }
            } else {
                WorldMapVBOs.getInstance().reserveVertices(int0, ints);
                worldMapGeometry.m_vboIndex1 = ints[0];
                worldMapGeometry.m_vboIndex2 = ints[1];
                float[] floats1 = worldMapGeometry.m_triangles;

                for (byte byte0 = 0; byte0 < floats1.length; byte0 += 6) {
                    float float6 = floats1[byte0];
                    float float7 = floats1[byte0 + 1];
                    float float8 = floats1[byte0 + 2];
                    float float9 = floats1[byte0 + 3];
                    float float10 = floats1[byte0 + 4];
                    float float11 = floats1[byte0 + 5];
                    WorldMapVBOs.getInstance().addElement(float6, float7, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
                    WorldMapVBOs.getInstance().addElement(float8, float9, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
                    WorldMapVBOs.getInstance().addElement(float10, float11, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        }

        void outlineTriangles(WorldMapGeometry worldMapGeometry, float float12, float float10, float float11) {
            WorldMapRenderer.m_vboLines.setMode(1);
            float float0 = 1.0F;
            float float1 = 1.0F;
            float float2 = 0.0F;
            float float3 = 0.0F;
            float[] floats = worldMapGeometry.m_triangles;

            for (byte byte0 = 0; byte0 < floats.length; byte0 += 6) {
                float float4 = floats[byte0];
                float float5 = floats[byte0 + 1];
                float float6 = floats[byte0 + 2];
                float float7 = floats[byte0 + 3];
                float float8 = floats[byte0 + 4];
                float float9 = floats[byte0 + 5];
                WorldMapRenderer.m_vboLines.addElement(float12 + float4 * float11, float10 + float5 * float11, 0.0F, float1, float3, float2, float0);
                WorldMapRenderer.m_vboLines.addElement(float12 + float6 * float11, float10 + float7 * float11, 0.0F, float1, float3, float2, float0);
                WorldMapRenderer.m_vboLines.addElement(float12 + float6 * float11, float10 + float7 * float11, 0.0F, float1, float3, float2, float0);
                WorldMapRenderer.m_vboLines.addElement(float12 + float8 * float11, float10 + float9 * float11, 0.0F, float1, float3, float2, float0);
                WorldMapRenderer.m_vboLines.addElement(float12 + float8 * float11, float10 + float9 * float11, 0.0F, float1, float3, float2, float0);
                WorldMapRenderer.m_vboLines.addElement(float12 + float4 * float11, float10 + float5 * float11, 0.0F, float1, float3, float2, float0);
            }
        }

        void outlinePolygon(WorldMapGeometry worldMapGeometry, float float6, float float4, float float5) {
            WorldMapRenderer.m_vboLines.setMode(1);
            float float0 = 1.0F;
            float float1 = 0.8F;
            float float2 = 0.8F;
            float float3 = 0.8F;
            WorldMapRenderer.m_vboLines.setLineWidth(4.0F);

            for (int int0 = 0; int0 < worldMapGeometry.m_points.size(); int0++) {
                WorldMapPoints worldMapPoints = worldMapGeometry.m_points.get(int0);

                for (int int1 = 0; int1 < worldMapPoints.numPoints(); int1++) {
                    int int2 = worldMapPoints.getX(int1);
                    int int3 = worldMapPoints.getY(int1);
                    int int4 = worldMapPoints.getX((int1 + 1) % worldMapPoints.numPoints());
                    int int5 = worldMapPoints.getY((int1 + 1) % worldMapPoints.numPoints());
                    WorldMapRenderer.m_vboLines.addElement(float6 + int2 * float5, float4 + int3 * float5, 0.0F, float3, float2, float1, float0);
                    WorldMapRenderer.m_vboLines.addElement(float6 + int4 * float5, float4 + int5 * float5, 0.0F, float3, float2, float1, float0);
                }
            }

            WorldMapRenderer.m_vboLines.setLineWidth(1.0F);
        }

        public void drawTexture(Texture texture, WorldMapStyleLayer.RGBAf fill, int worldX1, int worldY1, int worldX2, int worldY2) {
            if (texture != null && texture.isReady()) {
                WorldMapRenderer.m_vboLines.flush();
                WorldMapRenderer.m_vboLinesUV.flush();
                float float0 = this.m_worldScale;
                float float1 = (worldX1 - this.m_centerWorldX) * float0;
                float float2 = (worldY1 - this.m_centerWorldY) * float0;
                float float3 = float1 + (worldX2 - worldX1) * float0;
                float float4 = float2 + (worldY2 - worldY1) * float0;
                float float5 = PZMath.clamp(float1, this.m_renderCellX, this.m_renderCellX + 300.0F * float0);
                float float6 = PZMath.clamp(float2, this.m_renderCellY, this.m_renderCellY + 300.0F * float0);
                float float7 = PZMath.clamp(float3, this.m_renderCellX, this.m_renderCellX + 300.0F * float0);
                float float8 = PZMath.clamp(float4, this.m_renderCellY, this.m_renderCellY + 300.0F * float0);
                if (!(float5 >= float7) && !(float6 >= float8)) {
                    float float9 = (float)texture.getWidth() / (worldX2 - worldX1);
                    float float10 = (float)texture.getHeight() / (worldY2 - worldY1);
                    GL11.glEnable(3553);
                    GL11.glEnable(3042);
                    GL11.glDisable(2929);
                    if (texture.getID() == -1) {
                        texture.bind();
                    } else {
                        GL11.glBindTexture(3553, Texture.lastTextureID = texture.getID());
                        GL11.glTexParameteri(3553, 10241, 9728);
                        GL11.glTexParameteri(3553, 10240, 9728);
                    }

                    float float11 = (float5 - float1) / (texture.getWidthHW() * float0) * float9;
                    float float12 = (float6 - float2) / (texture.getHeightHW() * float0) * float10;
                    float float13 = (float7 - float1) / (texture.getWidthHW() * float0) * float9;
                    float float14 = (float8 - float2) / (texture.getHeightHW() * float0) * float10;
                    WorldMapRenderer.m_vboLinesUV.setMode(7);
                    WorldMapRenderer.m_vboLinesUV.startRun(texture.getTextureId());
                    WorldMapRenderer.m_vboLinesUV
                        .addQuad(float5, float6, float11, float12, float7, float8, float13, float14, 0.0F, fill.r, fill.g, fill.b, fill.a);
                }
            }
        }

        public void drawTextureTiled(Texture texture, WorldMapStyleLayer.RGBAf fill, int worldX1, int worldY1, int worldX2, int worldY2, int cellX, int cellY) {
            if (texture != null && texture.isReady()) {
                if (cellX * 300 < worldX2 && (cellX + 1) * 300 > worldX1) {
                    if (cellY * 300 < worldY2 && (cellY + 1) * 300 > worldY1) {
                        WorldMapRenderer.m_vboLines.flush();
                        float float0 = this.m_worldScale;
                        int int0 = texture.getWidth();
                        int int1 = texture.getHeight();
                        int int2 = (int)(PZMath.floor(cellX * 300.0F / int0) * int0);
                        int int3 = (int)(PZMath.floor(cellY * 300.0F / int1) * int1);
                        int int4 = int2 + (int)Math.ceil(((cellX + 1) * 300.0F - int2) / int0) * int0;
                        int int5 = int3 + (int)Math.ceil(((cellY + 1) * 300.0F - int3) / int1) * int1;
                        float float1 = PZMath.clamp(int2, cellX * 300, (cellX + 1) * 300);
                        float float2 = PZMath.clamp(int3, cellY * 300, (cellY + 1) * 300);
                        float float3 = PZMath.clamp(int4, cellX * 300, (cellX + 1) * 300);
                        float float4 = PZMath.clamp(int5, cellY * 300, (cellY + 1) * 300);
                        float1 = PZMath.clamp(float1, (float)worldX1, (float)worldX2);
                        float2 = PZMath.clamp(float2, (float)worldY1, (float)worldY2);
                        float3 = PZMath.clamp(float3, (float)worldX1, (float)worldX2);
                        float4 = PZMath.clamp(float4, (float)worldY1, (float)worldY2);
                        float float5 = (float1 - worldX1) / int0;
                        float float6 = (float2 - worldY1) / int1;
                        float float7 = (float3 - worldX1) / int0;
                        float float8 = (float4 - worldY1) / int1;
                        float1 = (float1 - this.m_centerWorldX) * float0;
                        float2 = (float2 - this.m_centerWorldY) * float0;
                        float3 = (float3 - this.m_centerWorldX) * float0;
                        float4 = (float4 - this.m_centerWorldY) * float0;
                        float float9 = float5 * texture.xEnd;
                        float float10 = float6 * texture.yEnd;
                        float float11 = (int)float7 + (float7 - (int)float7) * texture.xEnd;
                        float float12 = (int)float8 + (float8 - (int)float8) * texture.yEnd;
                        GL11.glEnable(3553);
                        if (texture.getID() == -1) {
                            texture.bind();
                        } else {
                            GL11.glBindTexture(3553, Texture.lastTextureID = texture.getID());
                            GL11.glTexParameteri(3553, 10241, 9728);
                            GL11.glTexParameteri(3553, 10240, 9728);
                            GL11.glTexParameteri(3553, 10242, 10497);
                            GL11.glTexParameteri(3553, 10243, 10497);
                        }

                        WorldMapRenderer.m_vboLinesUV.setMode(7);
                        WorldMapRenderer.m_vboLinesUV.startRun(texture.getTextureId());
                        WorldMapRenderer.m_vboLinesUV
                            .addQuad(float1, float2, float9, float10, float3, float4, float11, float12, 0.0F, fill.r, fill.g, fill.b, fill.a);
                        GL11.glDisable(3553);
                    }
                }
            }
        }

        public void drawTextureTiled(
            Texture texture, WorldMapStyleLayer.RGBAf fill, int worldX1, int worldY1, int worldX2, int worldY2, int tileW, int tileH, int cellX, int cellY
        ) {
            if (texture != null && texture.isReady()) {
                WorldMapRenderer.m_vboLines.flush();
                WorldMapRenderer.m_vboLinesUV.flush();
                float float0 = this.m_worldScale;
                float float1 = worldX1;
                float float2 = worldY1;
                float float3 = worldX2;
                float float4 = worldY2;
                float float5 = PZMath.clamp(float1, (float)(cellX * 300), (float)((cellX + 1) * 300));
                float float6 = PZMath.clamp(float2, (float)(cellY * 300), (float)((cellY + 1) * 300));
                float float7 = PZMath.clamp(float3, (float)(cellX * 300), (float)((cellX + 1) * 300));
                float float8 = PZMath.clamp(float4, (float)(cellY * 300), (float)((cellY + 1) * 300));
                float float9 = (float5 - worldX1) / tileW;
                float float10 = (float6 - worldY1) / tileH;
                float float11 = (float7 - worldX1) / tileW;
                float float12 = (float8 - worldY1) / tileH;
                float5 = (float5 - this.m_centerWorldX) * float0;
                float6 = (float6 - this.m_centerWorldY) * float0;
                float7 = (float7 - this.m_centerWorldX) * float0;
                float8 = (float8 - this.m_centerWorldY) * float0;
                float float13 = float9 * texture.xEnd;
                float float14 = float10 * texture.yEnd;
                float float15 = (int)float11 + (float11 - (int)float11) * texture.xEnd;
                float float16 = (int)float12 + (float12 - (int)float12) * texture.yEnd;
                GL11.glEnable(3553);
                if (texture.getID() == -1) {
                    texture.bind();
                } else {
                    GL11.glBindTexture(3553, Texture.lastTextureID = texture.getID());
                    GL11.glTexParameteri(3553, 10241, 9728);
                    GL11.glTexParameteri(3553, 10240, 9728);
                    GL11.glTexParameteri(3553, 10242, 10497);
                    GL11.glTexParameteri(3553, 10243, 10497);
                }

                GL11.glColor4f(fill.r, fill.g, fill.b, fill.a);
                GL11.glBegin(7);
                GL11.glTexCoord2f(float13, float14);
                GL11.glVertex2f(float5, float6);
                GL11.glTexCoord2f(float15, float14);
                GL11.glVertex2f(float7, float6);
                GL11.glTexCoord2f(float15, float16);
                GL11.glVertex2f(float7, float8);
                GL11.glTexCoord2f(float13, float16);
                GL11.glVertex2f(float5, float8);
                GL11.glEnd();
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(3553);
            }
        }

        void renderZones() {
            this.m_zoneSet.clear();

            for (byte byte0 = 0; byte0 < this.m_rasterizeXY.size() - 1; byte0 += 2) {
                int int0 = this.m_rasterizeXY_ints[byte0];
                int int1 = this.m_rasterizeXY_ints[byte0 + 1];
                if (this.m_renderer.m_visited == null || this.m_renderer.m_visited.isCellVisible(int0, int1)) {
                    IsoMetaCell metaCell = IsoWorld.instance.MetaGrid.getCellData(int0, int1);
                    if (metaCell != null) {
                        metaCell.getZonesUnique(this.m_zoneSet);
                    }
                }
            }

            this.m_zones.clear();
            this.m_zones.addAll(this.m_zoneSet);
            this.renderZones(this.m_zones, "Forest", 0.0F, 1.0F, 0.0F, 0.25F);
            this.renderZones(this.m_zones, "DeepForest", 0.0F, 0.5F, 0.0F, 0.25F);
            this.renderZones(this.m_zones, "Nav", 0.0F, 0.0F, 1.0F, 0.25F);
            this.renderZones(this.m_zones, "Vegitation", 1.0F, 1.0F, 0.0F, 0.25F);
        }

        void renderZones(ArrayList<IsoMetaGrid.Zone> arrayList, String string, float float5, float float6, float float7, float float16) {
            WorldMapRenderer.m_vboLinesUV.flush();
            float float0 = this.m_worldScale;
            WorldMapRenderer.m_vboLines.setMode(4);
            Iterator iterator = arrayList.iterator();

            while (true) {
                IsoMetaGrid.Zone zone0;
                label89:
                while (true) {
                    if (!iterator.hasNext()) {
                        WorldMapRenderer.m_vboLines.setMode(1);
                        WorldMapRenderer.m_vboLines.setLineWidth(2.0F);

                        for (IsoMetaGrid.Zone zone1 : arrayList) {
                            if (string.equals(zone1.type)) {
                                if (zone1.isRectangle()) {
                                    float float1 = (zone1.x - this.m_centerWorldX) * float0;
                                    float float2 = (zone1.y - this.m_centerWorldY) * float0;
                                    float float3 = (zone1.x + zone1.w - this.m_centerWorldX) * float0;
                                    float float4 = (zone1.y + zone1.h - this.m_centerWorldY) * float0;
                                    WorldMapRenderer.m_vboLines.addLine(float1, float2, 0.0F, float3, float2, 0.0F, float5, float6, float7, 1.0F);
                                    WorldMapRenderer.m_vboLines.addLine(float3, float2, 0.0F, float3, float4, 0.0F, float5, float6, float7, 1.0F);
                                    WorldMapRenderer.m_vboLines.addLine(float3, float4, 0.0F, float1, float4, 0.0F, float5, float6, float7, 1.0F);
                                    WorldMapRenderer.m_vboLines.addLine(float1, float4, 0.0F, float1, float2, 0.0F, float5, float6, float7, 1.0F);
                                }

                                if (zone1.isPolygon()) {
                                    for (byte byte0 = 0; byte0 < zone1.points.size(); byte0 += 2) {
                                        float float8 = (zone1.points.getQuick(byte0) - this.m_centerWorldX) * float0;
                                        float float9 = (zone1.points.getQuick(byte0 + 1) - this.m_centerWorldY) * float0;
                                        float float10 = (zone1.points.getQuick((byte0 + 2) % zone1.points.size()) - this.m_centerWorldX) * float0;
                                        float float11 = (zone1.points.getQuick((byte0 + 3) % zone1.points.size()) - this.m_centerWorldY) * float0;
                                        WorldMapRenderer.m_vboLines.addLine(float8, float9, 0.0F, float10, float11, 0.0F, float5, float6, float7, 1.0F);
                                    }
                                }

                                if (zone1.isPolyline()) {
                                    float[] floats0 = zone1.polylineOutlinePoints;
                                    if (floats0 != null) {
                                        for (byte byte1 = 0; byte1 < floats0.length; byte1 += 2) {
                                            float float12 = (floats0[byte1] - this.m_centerWorldX) * float0;
                                            float float13 = (floats0[byte1 + 1] - this.m_centerWorldY) * float0;
                                            float float14 = (floats0[(byte1 + 2) % floats0.length] - this.m_centerWorldX) * float0;
                                            float float15 = (floats0[(byte1 + 3) % floats0.length] - this.m_centerWorldY) * float0;
                                            WorldMapRenderer.m_vboLines.addLine(float12, float13, 0.0F, float14, float15, 0.0F, float5, float6, float7, 1.0F);
                                        }
                                    }
                                }
                            }
                        }

                        return;
                    }

                    zone0 = (IsoMetaGrid.Zone)iterator.next();
                    if (string.equals(zone0.type)) {
                        if (zone0.isRectangle()) {
                            WorldMapRenderer.m_vboLines
                                .addQuad(
                                    (zone0.x - this.m_centerWorldX) * float0,
                                    (zone0.y - this.m_centerWorldY) * float0,
                                    (zone0.x + zone0.w - this.m_centerWorldX) * float0,
                                    (zone0.y + zone0.h - this.m_centerWorldY) * float0,
                                    0.0F,
                                    float5,
                                    float6,
                                    float7,
                                    float16
                                );
                        }

                        if (!zone0.isPolygon()) {
                            break;
                        }

                        float[] floats1 = zone0.getPolygonTriangles();
                        if (floats1 != null) {
                            byte byte2 = 0;

                            while (true) {
                                if (byte2 >= floats1.length) {
                                    break label89;
                                }

                                float float17 = (floats1[byte2] - this.m_centerWorldX) * float0;
                                float float18 = (floats1[byte2 + 1] - this.m_centerWorldY) * float0;
                                float float19 = (floats1[byte2 + 2] - this.m_centerWorldX) * float0;
                                float float20 = (floats1[byte2 + 3] - this.m_centerWorldY) * float0;
                                float float21 = (floats1[byte2 + 4] - this.m_centerWorldX) * float0;
                                float float22 = (floats1[byte2 + 5] - this.m_centerWorldY) * float0;
                                WorldMapRenderer.m_vboLines
                                    .addTriangle(float17, float18, 0.0F, float19, float20, 0.0F, float21, float22, 0.0F, float5, float6, float7, float16);
                                byte2 += 6;
                            }
                        }
                    }
                }

                if (zone0.isPolyline()) {
                    float[] floats2 = zone0.getPolylineOutlineTriangles();
                    if (floats2 != null) {
                        for (byte byte3 = 0; byte3 < floats2.length; byte3 += 6) {
                            float float23 = (floats2[byte3] - this.m_centerWorldX) * float0;
                            float float24 = (floats2[byte3 + 1] - this.m_centerWorldY) * float0;
                            float float25 = (floats2[byte3 + 2] - this.m_centerWorldX) * float0;
                            float float26 = (floats2[byte3 + 3] - this.m_centerWorldY) * float0;
                            float float27 = (floats2[byte3 + 4] - this.m_centerWorldX) * float0;
                            float float28 = (floats2[byte3 + 5] - this.m_centerWorldY) * float0;
                            WorldMapRenderer.m_vboLines
                                .addTriangle(float23, float24, 0.0F, float25, float26, 0.0F, float27, float28, 0.0F, float5, float6, float7, float16);
                        }
                    }
                }
            }
        }

        @Override
        public void render() {
            try {
                PZGLUtil.pushAndLoadMatrix(5889, this.m_projection);
                PZGLUtil.pushAndLoadMatrix(5888, this.m_modelView);
                this.renderInternal();
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
            } finally {
                PZGLUtil.popMatrix(5889);
                PZGLUtil.popMatrix(5888);
            }
        }

        private void renderInternal() {
            float float0 = this.m_worldScale;
            int int0 = (int)Math.max(this.uiToWorldX(0.0F, 0.0F), (float)this.m_worldMap.getMinXInSquares()) / 300;
            int int1 = (int)Math.max(this.uiToWorldY(0.0F, 0.0F), (float)this.m_worldMap.getMinYInSquares()) / 300;
            int int2 = (int)Math.min(this.uiToWorldX(this.getWidth(), this.getHeight()), (float)(this.m_worldMap.m_maxX * 300)) / 300;
            int int3 = (int)Math.min(this.uiToWorldY(this.getWidth(), this.getHeight()), (float)(this.m_worldMap.m_maxY * 300)) / 300;
            int0 = this.m_worldMap.getMinXInSquares();
            int1 = this.m_worldMap.getMinYInSquares();
            int2 = this.m_worldMap.m_maxX;
            int3 = this.m_worldMap.m_maxY;
            GL11.glViewport(this.m_x, Core.height - this.m_height - this.m_y, this.m_width, this.m_height);
            GLVertexBufferObject.funcs.glBindBuffer(GLVertexBufferObject.funcs.GL_ARRAY_BUFFER(), 0);
            GLVertexBufferObject.funcs.glBindBuffer(GLVertexBufferObject.funcs.GL_ELEMENT_ARRAY_BUFFER(), 0);
            GL11.glPolygonMode(1032, this.m_renderer.Wireframe.getValue() ? 6913 : 6914);
            if (this.m_renderer.ImagePyramid.getValue()) {
                this.renderImagePyramids();
            }

            this.calculateVisibleCells();
            if (this.m_renderer.Features.getValue()) {
                this.renderCellFeatures();
            }

            if (this.m_renderer.ForestZones.getValue()) {
                this.renderZones();
            }

            if (this.m_renderer.VisibleCells.getValue()) {
                this.renderVisibleCells();
            }

            WorldMapRenderer.m_vboLines.flush();
            WorldMapRenderer.m_vboLinesUV.flush();
            GL11.glEnableClientState(32884);
            GL11.glEnableClientState(32886);
            GL13.glActiveTexture(33984);
            GL13.glClientActiveTexture(33984);
            GL11.glEnableClientState(32888);
            GL11.glTexEnvi(8960, 8704, 8448);
            GL11.glPolygonMode(1032, 6914);
            GL11.glEnable(3042);
            SpriteRenderer.ringBuffer.restoreBoundTextures = true;
            SpriteRenderer.ringBuffer.restoreVBOs = true;
            if (this.m_renderer.m_visited != null) {
                this.m_renderer
                    .m_visited
                    .render(
                        this.m_renderOriginX - (this.m_worldMap.getMinXInSquares() - this.m_renderer.m_visited.getMinX() * 300) * float0,
                        this.m_renderOriginY - (this.m_worldMap.getMinYInSquares() - this.m_renderer.m_visited.getMinY() * 300) * float0,
                        int0 / 300,
                        int1 / 300,
                        int2 / 300,
                        int3 / 300,
                        float0,
                        this.m_renderer.BlurUnvisited.getValue()
                    );
                if (this.m_renderer.UnvisitedGrid.getValue()) {
                    this.m_renderer
                        .m_visited
                        .renderGrid(
                            this.m_renderOriginX - (this.m_worldMap.getMinXInSquares() - this.m_renderer.m_visited.getMinX() * 300) * float0,
                            this.m_renderOriginY - (this.m_worldMap.getMinYInSquares() - this.m_renderer.m_visited.getMinY() * 300) * float0,
                            int0 / 300,
                            int1 / 300,
                            int2 / 300,
                            int3 / 300,
                            float0,
                            this.m_zoomF
                        );
                }
            }

            this.renderPlayers();
            if (this.m_renderer.CellGrid.getValue()) {
                this.renderCellGrid(int0 / 300, int1 / 300, int2 / 300, int3 / 300);
            }

            if (Core.bDebug) {
            }

            this.paintAreasOutsideBounds(int0, int1, int2, int3, float0);
            if (this.m_renderer.WorldBounds.getValue()) {
                this.renderWorldBounds();
            }

            WorldMapRenderer.m_vboLines.flush();
            WorldMapRenderer.m_vboLinesUV.flush();
            GL11.glViewport(0, 0, Core.width, Core.height);
        }

        private void rasterizeCellsCallback(int int1, int int2) {
            int int0 = int1 + int2 * this.m_worldMap.getWidthInCells();
            if (!this.m_rasterizeSet.contains(int0)) {
                for (int int3 = int2 * this.m_rasterizeMult; int3 < int2 * this.m_rasterizeMult + this.m_rasterizeMult; int3++) {
                    for (int int4 = int1 * this.m_rasterizeMult; int4 < int1 * this.m_rasterizeMult + this.m_rasterizeMult; int4++) {
                        if (int4 >= this.m_worldMap.getMinXInCells()
                            && int4 <= this.m_worldMap.getMaxXInCells()
                            && int3 >= this.m_worldMap.getMinYInCells()
                            && int3 <= this.m_worldMap.getMaxYInCells()) {
                            this.m_rasterizeSet.add(int0);
                            this.m_rasterizeXY.add(int4);
                            this.m_rasterizeXY.add(int3);
                        }
                    }
                }
            }
        }

        private void rasterizeTilesCallback(int int1, int int2) {
            int int0 = int1 + int2 * 1000;
            if (!this.m_rasterizeSet.contains(int0)) {
                if (!(int1 < this.m_rasterizeMinTileX)
                    && !(int1 > this.m_rasterizeMaxTileX)
                    && !(int2 < this.m_rasterizeMinTileY)
                    && !(int2 > this.m_rasterizeMaxTileY)) {
                    this.m_rasterizeSet.add(int0);
                    this.m_rasterizeXY.add(int1);
                    this.m_rasterizeXY.add(int2);
                }
            }
        }

        private void calculateVisibleCells() {
            boolean boolean0 = Core.bDebug && this.m_renderer.VisibleCells.getValue();
            int int0 = boolean0 ? 200 : 0;
            float float0 = this.m_worldScale;
            if (1.0F / float0 > 100.0F) {
                this.m_rasterizeXY.clear();

                for (int int1 = this.m_worldMap.getMinYInCells(); int1 <= this.m_worldMap.getMaxYInCells(); int1++) {
                    for (int int2 = this.m_worldMap.getMinXInCells(); int2 <= this.m_worldMap.getMaxYInCells(); int2++) {
                        this.m_rasterizeXY.add(int2);
                        this.m_rasterizeXY.add(int1);
                    }
                }

                if (this.m_rasterizeXY_ints == null || this.m_rasterizeXY_ints.length < this.m_rasterizeXY.size()) {
                    this.m_rasterizeXY_ints = new int[this.m_rasterizeXY.size()];
                }

                this.m_rasterizeXY_ints = this.m_rasterizeXY.toArray(this.m_rasterizeXY_ints);
            } else {
                float float1 = this.uiToWorldX(int0 + 0.0F, int0 + 0.0F) / 300.0F;
                float float2 = this.uiToWorldY(int0 + 0.0F, int0 + 0.0F) / 300.0F;
                float float3 = this.uiToWorldX(this.getWidth() - int0, 0.0F + int0) / 300.0F;
                float float4 = this.uiToWorldY(this.getWidth() - int0, 0.0F + int0) / 300.0F;
                float float5 = this.uiToWorldX(this.getWidth() - int0, this.getHeight() - int0) / 300.0F;
                float float6 = this.uiToWorldY(this.getWidth() - int0, this.getHeight() - int0) / 300.0F;
                float float7 = this.uiToWorldX(0.0F + int0, this.getHeight() - int0) / 300.0F;
                float float8 = this.uiToWorldY(0.0F + int0, this.getHeight() - int0) / 300.0F;
                int int3 = 1;

                while (
                    this.triangleArea(float7 / int3, float8 / int3, float5 / int3, float6 / int3, float3 / int3, float4 / int3)
                            + this.triangleArea(float3 / int3, float4 / int3, float1 / int3, float2 / int3, float7 / int3, float8 / int3)
                        > 80.0F
                ) {
                    int3++;
                }

                this.m_rasterizeMult = int3;
                this.m_rasterizeXY.clear();
                this.m_rasterizeSet.clear();
                this.m_rasterize
                    .scanTriangle(
                        float7 / int3, float8 / int3, float5 / int3, float6 / int3, float3 / int3, float4 / int3, 0, 1000, this::rasterizeCellsCallback
                    );
                this.m_rasterize
                    .scanTriangle(
                        float3 / int3, float4 / int3, float1 / int3, float2 / int3, float7 / int3, float8 / int3, 0, 1000, this::rasterizeCellsCallback
                    );
                if (this.m_rasterizeXY_ints == null || this.m_rasterizeXY_ints.length < this.m_rasterizeXY.size()) {
                    this.m_rasterizeXY_ints = new int[this.m_rasterizeXY.size()];
                }

                this.m_rasterizeXY_ints = this.m_rasterizeXY.toArray(this.m_rasterizeXY_ints);
            }
        }

        void renderVisibleCells() {
            boolean boolean0 = Core.bDebug && this.m_renderer.VisibleCells.getValue();
            int int0 = boolean0 ? 200 : 0;
            float float0 = this.m_worldScale;
            if (!(1.0F / float0 > 100.0F)) {
                WorldMapRenderer.m_vboLines.setMode(4);

                for (byte byte0 = 0; byte0 < this.m_rasterizeXY.size(); byte0 += 2) {
                    int int1 = this.m_rasterizeXY.get(byte0);
                    int int2 = this.m_rasterizeXY.get(byte0 + 1);
                    float float1 = this.m_renderOriginX + (int1 * 300 - this.m_worldMap.getMinXInSquares()) * float0;
                    float float2 = this.m_renderOriginY + (int2 * 300 - this.m_worldMap.getMinYInSquares()) * float0;
                    float float3 = this.m_renderOriginX + ((int1 + 1) * 300 - this.m_worldMap.getMinXInSquares()) * float0;
                    float float4 = this.m_renderOriginY + ((int2 + 1) * 300 - this.m_worldMap.getMinYInSquares()) * float0;
                    WorldMapRenderer.m_vboLines.addElement(float1, float2, 0.0F, 0.0F, 1.0F, 0.0F, 0.2F);
                    WorldMapRenderer.m_vboLines.addElement(float3, float2, 0.0F, 0.0F, 1.0F, 0.0F, 0.2F);
                    WorldMapRenderer.m_vboLines.addElement(float1, float4, 0.0F, 0.0F, 1.0F, 0.0F, 0.2F);
                    WorldMapRenderer.m_vboLines.addElement(float3, float2, 0.0F, 0.0F, 0.0F, 1.0F, 0.2F);
                    WorldMapRenderer.m_vboLines.addElement(float3, float4, 0.0F, 0.0F, 0.0F, 1.0F, 0.2F);
                    WorldMapRenderer.m_vboLines.addElement(float1, float4, 0.0F, 0.0F, 0.0F, 1.0F, 0.2F);
                }

                WorldMapRenderer.m_vboLines.flush();
                float float5 = this.uiToWorldX(int0 + 0.0F, int0 + 0.0F) / 300.0F;
                float float6 = this.uiToWorldY(int0 + 0.0F, int0 + 0.0F) / 300.0F;
                float float7 = this.uiToWorldX(this.getWidth() - int0, 0.0F + int0) / 300.0F;
                float float8 = this.uiToWorldY(this.getWidth() - int0, 0.0F + int0) / 300.0F;
                float float9 = this.uiToWorldX(this.getWidth() - int0, this.getHeight() - int0) / 300.0F;
                float float10 = this.uiToWorldY(this.getWidth() - int0, this.getHeight() - int0) / 300.0F;
                float float11 = this.uiToWorldX(0.0F + int0, this.getHeight() - int0) / 300.0F;
                float float12 = this.uiToWorldY(0.0F + int0, this.getHeight() - int0) / 300.0F;
                WorldMapRenderer.m_vboLines.setMode(1);
                WorldMapRenderer.m_vboLines.setLineWidth(4.0F);
                WorldMapRenderer.m_vboLines
                    .addLine(
                        this.m_renderOriginX + (float11 * 300.0F - this.m_worldMap.getMinXInSquares()) * float0,
                        this.m_renderOriginY + (float12 * 300.0F - this.m_worldMap.getMinYInSquares()) * float0,
                        0.0F,
                        this.m_renderOriginX + (float9 * 300.0F - this.m_worldMap.getMinXInSquares()) * float0,
                        this.m_renderOriginY + (float10 * 300.0F - this.m_worldMap.getMinYInSquares()) * float0,
                        0.0F,
                        1.0F,
                        0.0F,
                        0.0F,
                        1.0F
                    );
                WorldMapRenderer.m_vboLines
                    .addLine(
                        this.m_renderOriginX + (float9 * 300.0F - this.m_worldMap.getMinXInSquares()) * float0,
                        this.m_renderOriginY + (float10 * 300.0F - this.m_worldMap.getMinYInSquares()) * float0,
                        0.0F,
                        this.m_renderOriginX + (float7 * 300.0F - this.m_worldMap.getMinXInSquares()) * float0,
                        this.m_renderOriginY + (float8 * 300.0F - this.m_worldMap.getMinYInSquares()) * float0,
                        0.0F,
                        1.0F,
                        0.0F,
                        0.0F,
                        1.0F
                    );
                WorldMapRenderer.m_vboLines
                    .addLine(
                        this.m_renderOriginX + (float7 * 300.0F - this.m_worldMap.getMinXInSquares()) * float0,
                        this.m_renderOriginY + (float8 * 300.0F - this.m_worldMap.getMinYInSquares()) * float0,
                        0.0F,
                        this.m_renderOriginX + (float11 * 300.0F - this.m_worldMap.getMinXInSquares()) * float0,
                        this.m_renderOriginY + (float12 * 300.0F - this.m_worldMap.getMinYInSquares()) * float0,
                        0.0F,
                        0.5F,
                        0.5F,
                        0.5F,
                        1.0F
                    );
                WorldMapRenderer.m_vboLines
                    .addLine(
                        this.m_renderOriginX + (float7 * 300.0F - this.m_worldMap.getMinXInSquares()) * float0,
                        this.m_renderOriginY + (float8 * 300.0F - this.m_worldMap.getMinYInSquares()) * float0,
                        0.0F,
                        this.m_renderOriginX + (float5 * 300.0F - this.m_worldMap.getMinXInSquares()) * float0,
                        this.m_renderOriginY + (float6 * 300.0F - this.m_worldMap.getMinYInSquares()) * float0,
                        0.0F,
                        0.0F,
                        0.0F,
                        1.0F,
                        1.0F
                    );
                WorldMapRenderer.m_vboLines
                    .addLine(
                        this.m_renderOriginX + (float5 * 300.0F - this.m_worldMap.getMinXInSquares()) * float0,
                        this.m_renderOriginY + (float6 * 300.0F - this.m_worldMap.getMinYInSquares()) * float0,
                        0.0F,
                        this.m_renderOriginX + (float11 * 300.0F - this.m_worldMap.getMinXInSquares()) * float0,
                        this.m_renderOriginY + (float12 * 300.0F - this.m_worldMap.getMinYInSquares()) * float0,
                        0.0F,
                        0.0F,
                        0.0F,
                        1.0F,
                        1.0F
                    );
            }
        }

        void calcVisiblePyramidTiles(WorldMapImages worldMapImages) {
            if (Core.bDebug) {
            }

            boolean boolean0 = false;
            int int0 = boolean0 ? 200 : 0;
            float float0 = this.m_worldScale;
            int int1 = worldMapImages.getZoom(this.m_zoomF);
            short short0 = 256;
            float float1 = short0 * (1 << int1);
            int int2 = worldMapImages.getMinX();
            int int3 = worldMapImages.getMinY();
            float float2 = (this.uiToWorldX(int0 + 0.0F, int0 + 0.0F) - int2) / float1;
            float float3 = (this.uiToWorldY(int0 + 0.0F, int0 + 0.0F) - int3) / float1;
            float float4 = (this.uiToWorldX(this.getWidth() - int0, 0.0F + int0) - int2) / float1;
            float float5 = (this.uiToWorldY(this.getWidth() - int0, 0.0F + int0) - int3) / float1;
            float float6 = (this.uiToWorldX(this.getWidth() - int0, this.getHeight() - int0) - int2) / float1;
            float float7 = (this.uiToWorldY(this.getWidth() - int0, this.getHeight() - int0) - int3) / float1;
            float float8 = (this.uiToWorldX(0.0F + int0, this.getHeight() - int0) - int2) / float1;
            float float9 = (this.uiToWorldY(0.0F + int0, this.getHeight() - int0) - int3) / float1;
            if (boolean0) {
                WorldMapRenderer.m_vboLines.setMode(1);
                WorldMapRenderer.m_vboLines.setLineWidth(4.0F);
                WorldMapRenderer.m_vboLines
                    .addLine(
                        this.m_renderOriginX + float8 * float1 * float0,
                        this.m_renderOriginY + float9 * float1 * float0,
                        0.0F,
                        this.m_renderOriginX + float6 * float1 * float0,
                        this.m_renderOriginY + float7 * float1 * float0,
                        0.0F,
                        1.0F,
                        0.0F,
                        0.0F,
                        1.0F
                    );
                WorldMapRenderer.m_vboLines
                    .addLine(
                        this.m_renderOriginX + float6 * float1 * float0,
                        this.m_renderOriginY + float7 * float1 * float0,
                        0.0F,
                        this.m_renderOriginX + float4 * float1 * float0,
                        this.m_renderOriginY + float5 * float1 * float0,
                        0.0F,
                        1.0F,
                        0.0F,
                        0.0F,
                        1.0F
                    );
                WorldMapRenderer.m_vboLines
                    .addLine(
                        this.m_renderOriginX + float4 * float1 * float0,
                        this.m_renderOriginY + float5 * float1 * float0,
                        0.0F,
                        this.m_renderOriginX + float8 * float1 * float0,
                        this.m_renderOriginY + float9 * float1 * float0,
                        0.0F,
                        0.5F,
                        0.5F,
                        0.5F,
                        1.0F
                    );
                WorldMapRenderer.m_vboLines
                    .addLine(
                        this.m_renderOriginX + float4 * float1 * float0,
                        this.m_renderOriginY + float5 * float1 * float0,
                        0.0F,
                        this.m_renderOriginX + float2 * float1 * float0,
                        this.m_renderOriginY + float3 * float1 * float0,
                        0.0F,
                        0.0F,
                        0.0F,
                        1.0F,
                        1.0F
                    );
                WorldMapRenderer.m_vboLines
                    .addLine(
                        this.m_renderOriginX + float2 * float1 * float0,
                        this.m_renderOriginY + float3 * float1 * float0,
                        0.0F,
                        this.m_renderOriginX + float8 * float1 * float0,
                        this.m_renderOriginY + float9 * float1 * float0,
                        0.0F,
                        0.0F,
                        0.0F,
                        1.0F,
                        1.0F
                    );
            }

            this.m_rasterizeXY.clear();
            this.m_rasterizeSet.clear();
            this.m_rasterizeMinTileX = (int)((this.m_worldMap.getMinXInSquares() - worldMapImages.getMinX()) / float1);
            this.m_rasterizeMinTileY = (int)((this.m_worldMap.getMinYInSquares() - worldMapImages.getMinY()) / float1);
            this.m_rasterizeMaxTileX = (this.m_worldMap.getMaxXInSquares() - worldMapImages.getMinX()) / float1;
            this.m_rasterizeMaxTileY = (this.m_worldMap.getMaxYInSquares() - worldMapImages.getMinY()) / float1;
            this.m_rasterize.scanTriangle(float8, float9, float6, float7, float4, float5, 0, 1000, this::rasterizeTilesCallback);
            this.m_rasterize.scanTriangle(float4, float5, float2, float3, float8, float9, 0, 1000, this::rasterizeTilesCallback);
            if (this.m_rasterizeXY_ints == null || this.m_rasterizeXY_ints.length < this.m_rasterizeXY.size()) {
                this.m_rasterizeXY_ints = new int[this.m_rasterizeXY.size()];
            }

            this.m_rasterizeXY_ints = this.m_rasterizeXY.toArray(this.m_rasterizeXY_ints);
            if (boolean0) {
                WorldMapRenderer.m_vboLines.setMode(4);

                for (byte byte0 = 0; byte0 < this.m_rasterizeXY.size(); byte0 += 2) {
                    int int4 = this.m_rasterizeXY.get(byte0);
                    int int5 = this.m_rasterizeXY.get(byte0 + 1);
                    float float10 = this.m_renderOriginX + int4 * float1 * float0;
                    float float11 = this.m_renderOriginY + int5 * float1 * float0;
                    float float12 = this.m_renderOriginX + (int4 + 1) * float1 * float0;
                    float float13 = this.m_renderOriginY + (int5 + 1) * float1 * float0;
                    WorldMapRenderer.m_vboLines.addElement(float10, float11, 0.0F, 0.0F, 1.0F, 0.0F, 0.2F);
                    WorldMapRenderer.m_vboLines.addElement(float12, float11, 0.0F, 0.0F, 1.0F, 0.0F, 0.2F);
                    WorldMapRenderer.m_vboLines.addElement(float10, float13, 0.0F, 0.0F, 1.0F, 0.0F, 0.2F);
                    WorldMapRenderer.m_vboLines.addElement(float12, float11, 0.0F, 0.0F, 0.0F, 1.0F, 0.2F);
                    WorldMapRenderer.m_vboLines.addElement(float12, float13, 0.0F, 0.0F, 0.0F, 1.0F, 0.2F);
                    WorldMapRenderer.m_vboLines.addElement(float10, float13, 0.0F, 0.0F, 0.0F, 1.0F, 0.2F);
                }

                WorldMapRenderer.m_vboLines.flush();
            }
        }

        void renderImagePyramids() {
            for (int int0 = this.m_worldMap.getImagesCount() - 1; int0 >= 0; int0--) {
                WorldMapImages worldMapImages = this.m_worldMap.getImagesByIndex(int0);
                this.renderImagePyramid(worldMapImages);
                GL11.glDisable(3553);
            }
        }

        void renderImagePyramid(WorldMapImages worldMapImages) {
            float float0 = this.m_worldScale;
            short short0 = 256;
            int int0 = worldMapImages.getZoom(this.m_zoomF);
            float float1 = short0 * (1 << int0);
            this.calcVisiblePyramidTiles(worldMapImages);
            GL11.glEnable(3553);
            GL11.glEnable(3042);
            WorldMapRenderer.m_vboLinesUV.setMode(4);
            int int1 = PZMath.clamp(worldMapImages.getMinX(), this.m_worldMap.getMinXInSquares(), this.m_worldMap.getMaxXInSquares());
            int int2 = PZMath.clamp(worldMapImages.getMinY(), this.m_worldMap.getMinYInSquares(), this.m_worldMap.getMaxYInSquares());
            int int3 = PZMath.clamp(worldMapImages.getMaxX(), this.m_worldMap.getMinXInSquares(), this.m_worldMap.getMaxXInSquares() + 1);
            int int4 = PZMath.clamp(worldMapImages.getMaxY(), this.m_worldMap.getMinYInSquares(), this.m_worldMap.getMaxYInSquares() + 1);

            for (byte byte0 = 0; byte0 < this.m_rasterizeXY.size() - 1; byte0 += 2) {
                int int5 = this.m_rasterizeXY_ints[byte0];
                int int6 = this.m_rasterizeXY_ints[byte0 + 1];
                TextureID textureID = worldMapImages.getPyramid().getTexture(int5, int6, int0);
                if (textureID != null && textureID.isReady()) {
                    WorldMapRenderer.m_vboLinesUV.startRun(textureID);
                    float float2 = worldMapImages.getMinX() + int5 * float1;
                    float float3 = worldMapImages.getMinY() + int6 * float1;
                    float float4 = float2 + float1;
                    float float5 = float3 + float1;
                    float float6 = PZMath.clamp(float2, (float)int1, (float)int3);
                    float float7 = PZMath.clamp(float3, (float)int2, (float)int4);
                    float float8 = PZMath.clamp(float4, (float)int1, (float)int3);
                    float float9 = PZMath.clamp(float5, (float)int2, (float)int4);
                    float float10 = (float6 - this.m_centerWorldX) * float0;
                    float float11 = (float7 - this.m_centerWorldY) * float0;
                    float float12 = (float8 - this.m_centerWorldX) * float0;
                    float float13 = (float7 - this.m_centerWorldY) * float0;
                    float float14 = (float8 - this.m_centerWorldX) * float0;
                    float float15 = (float9 - this.m_centerWorldY) * float0;
                    float float16 = (float6 - this.m_centerWorldX) * float0;
                    float float17 = (float9 - this.m_centerWorldY) * float0;
                    float float18 = (float6 - float2) / float1;
                    float float19 = (float7 - float3) / float1;
                    float float20 = (float8 - float2) / float1;
                    float float21 = (float7 - float3) / float1;
                    float float22 = (float8 - float2) / float1;
                    float float23 = (float9 - float3) / float1;
                    float float24 = (float6 - float2) / float1;
                    float float25 = (float9 - float3) / float1;
                    float float26 = 1.0F;
                    float float27 = 1.0F;
                    float float28 = 1.0F;
                    float float29 = 1.0F;
                    WorldMapRenderer.m_vboLinesUV.addElement(float10, float11, 0.0F, float18, float19, float26, float27, float28, float29);
                    WorldMapRenderer.m_vboLinesUV.addElement(float12, float13, 0.0F, float20, float21, float26, float27, float28, float29);
                    WorldMapRenderer.m_vboLinesUV.addElement(float16, float17, 0.0F, float24, float25, float26, float27, float28, float29);
                    WorldMapRenderer.m_vboLinesUV.addElement(float12, float13, 0.0F, float20, float21, float26, float27, float28, float29);
                    WorldMapRenderer.m_vboLinesUV.addElement(float14, float15, 0.0F, float22, float23, float26, float27, float28, float29);
                    WorldMapRenderer.m_vboLinesUV.addElement(float16, float17, 0.0F, float24, float25, float26, float27, float28, float29);
                    if (this.m_renderer.TileGrid.getValue()) {
                        WorldMapRenderer.m_vboLinesUV.flush();
                        WorldMapRenderer.m_vboLines.setMode(1);
                        WorldMapRenderer.m_vboLines.setLineWidth(2.0F);
                        WorldMapRenderer.m_vboLines
                            .addLine(
                                (float2 - this.m_centerWorldX) * float0,
                                (float3 - this.m_centerWorldY) * float0,
                                0.0F,
                                (float4 - this.m_centerWorldX) * float0,
                                (float3 - this.m_centerWorldY) * float0,
                                0.0F,
                                1.0F,
                                0.0F,
                                0.0F,
                                0.5F
                            );
                        WorldMapRenderer.m_vboLines
                            .addLine(
                                (float2 - this.m_centerWorldX) * float0,
                                (float5 - this.m_centerWorldY) * float0,
                                0.0F,
                                (float4 - this.m_centerWorldX) * float0,
                                (float5 - this.m_centerWorldY) * float0,
                                0.0F,
                                1.0F,
                                0.0F,
                                0.0F,
                                0.5F
                            );
                        WorldMapRenderer.m_vboLines
                            .addLine(
                                (float4 - this.m_centerWorldX) * float0,
                                (float3 - this.m_centerWorldY) * float0,
                                0.0F,
                                (float4 - this.m_centerWorldX) * float0,
                                (float5 - this.m_centerWorldY) * float0,
                                0.0F,
                                1.0F,
                                0.0F,
                                0.0F,
                                0.5F
                            );
                        WorldMapRenderer.m_vboLines
                            .addLine(
                                (float2 - this.m_centerWorldX) * float0,
                                (float3 - this.m_centerWorldY) * float0,
                                0.0F,
                                (float2 - this.m_centerWorldX) * float0,
                                (float5 - this.m_centerWorldY) * float0,
                                0.0F,
                                1.0F,
                                0.0F,
                                0.0F,
                                0.5F
                            );
                        WorldMapRenderer.m_vboLines.flush();
                    }
                }
            }
        }

        void renderImagePyramidGrid(WorldMapImages worldMapImages) {
            float float0 = this.m_worldScale;
            short short0 = 256;
            int int0 = worldMapImages.getZoom(this.m_zoomF);
            float float1 = short0 * (1 << int0);
            float float2 = (worldMapImages.getMinX() - this.m_centerWorldX) * float0;
            float float3 = (worldMapImages.getMinY() - this.m_centerWorldY) * float0;
            int int1 = (int)Math.ceil((worldMapImages.getMaxX() - worldMapImages.getMinX()) / float1);
            int int2 = (int)Math.ceil((worldMapImages.getMaxY() - worldMapImages.getMinY()) / float1);
            float float4 = float2;
            float float5 = float3;
            float float6 = float2 + int1 * float1 * float0;
            float float7 = float3 + int2 * float1 * float0;
            WorldMapRenderer.m_vboLines.setMode(1);
            WorldMapRenderer.m_vboLines.setLineWidth(2.0F);

            for (int int3 = 0; int3 < int1 + 1; int3++) {
                WorldMapRenderer.m_vboLines
                    .addLine(float2 + int3 * float1 * float0, float5, 0.0F, float2 + int3 * float1 * float0, float7, 0.0F, 1.0F, 0.0F, 0.0F, 0.5F);
            }

            for (int int4 = 0; int4 < int2 + 1; int4++) {
                WorldMapRenderer.m_vboLines
                    .addLine(float4, float3 + int4 * float1 * float0, 0.0F, float6, float3 + int4 * float1 * float0, 0.0F, 1.0F, 0.0F, 0.0F, 0.5F);
            }

            WorldMapRenderer.m_vboLines.flush();
        }

        float triangleArea(float float4, float float2, float float3, float float1, float float7, float float6) {
            float float0 = Vector2f.length(float3 - float4, float1 - float2);
            float float5 = Vector2f.length(float7 - float3, float6 - float1);
            float float8 = Vector2f.length(float4 - float7, float2 - float6);
            float float9 = (float0 + float5 + float8) / 2.0F;
            return (float)Math.sqrt(float9 * (float9 - float0) * (float9 - float5) * (float9 - float8));
        }

        void paintAreasOutsideBounds(int int0, int int1, int int2, int int3, float float1) {
            float float0 = this.m_renderOriginX - int0 % 300 * float1;
            float float2 = this.m_renderOriginY - int1 % 300 * float1;
            float float3 = this.m_renderOriginX + ((this.m_worldMap.getMaxXInCells() + 1) * 300 - int0) * float1;
            float float4 = this.m_renderOriginY + ((this.m_worldMap.getMaxYInCells() + 1) * 300 - int1) * float1;
            float float5 = 0.0F;
            WorldMapStyleLayer.RGBAf rGBAf = this.m_fill;
            if (int0 % 300 != 0) {
                float float6 = this.m_renderOriginX;
                WorldMapRenderer.m_vboLines.setMode(4);
                WorldMapRenderer.m_vboLines.addQuad(float0, float2, float6, float4, float5, rGBAf.r, rGBAf.g, rGBAf.b, rGBAf.a);
            }

            if (int1 % 300 != 0) {
                float float7 = this.m_renderOriginX;
                float float8 = float7 + this.m_worldMap.getWidthInSquares() * this.m_worldScale;
                float float9 = this.m_renderOriginY;
                WorldMapRenderer.m_vboLines.setMode(4);
                WorldMapRenderer.m_vboLines.addQuad(float7, float2, float8, float9, float5, rGBAf.r, rGBAf.g, rGBAf.b, rGBAf.a);
            }

            if (int2 + 1 != 0) {
                float float10 = this.m_renderOriginX + (int2 - int0 + 1) * float1;
                WorldMapRenderer.m_vboLines.setMode(4);
                WorldMapRenderer.m_vboLines.addQuad(float10, float2, float3, float4, float5, rGBAf.r, rGBAf.g, rGBAf.b, rGBAf.a);
            }

            if (int3 + 1 != 0) {
                float float11 = this.m_renderOriginX;
                float float12 = this.m_renderOriginY + this.m_worldMap.getHeightInSquares() * float1;
                float float13 = this.m_renderOriginX + this.m_worldMap.getWidthInSquares() * float1;
                WorldMapRenderer.m_vboLines.setMode(4);
                WorldMapRenderer.m_vboLines.addQuad(float11, float12, float13, float4, float5, rGBAf.r, rGBAf.g, rGBAf.b, rGBAf.a);
            }
        }

        void renderWorldBounds() {
            float float0 = this.m_renderOriginX;
            float float1 = this.m_renderOriginY;
            float float2 = float0 + this.m_worldMap.getWidthInSquares() * this.m_worldScale;
            float float3 = float1 + this.m_worldMap.getHeightInSquares() * this.m_worldScale;
            this.renderDropShadow();
            WorldMapRenderer.m_vboLines.setMode(1);
            WorldMapRenderer.m_vboLines.setLineWidth(2.0F);
            float float4 = 0.5F;
            WorldMapRenderer.m_vboLines.addLine(float0, float1, 0.0F, float2, float1, 0.0F, float4, float4, float4, 1.0F);
            WorldMapRenderer.m_vboLines.addLine(float2, float1, 0.0F, float2, float3, 0.0F, float4, float4, float4, 1.0F);
            WorldMapRenderer.m_vboLines.addLine(float2, float3, 0.0F, float0, float3, 0.0F, float4, float4, float4, 1.0F);
            WorldMapRenderer.m_vboLines.addLine(float0, float3, 0.0F, float0, float1, 0.0F, float4, float4, float4, 1.0F);
        }

        private void renderDropShadow() {
            float float0 = this.m_renderer.m_dropShadowWidth
                * (this.m_renderer.getHeight() / 1080.0F)
                * this.m_worldScale
                / this.m_renderer.getWorldScale(this.m_renderer.getBaseZoom());
            if (!(float0 < 2.0F)) {
                float float1 = this.m_renderOriginX;
                float float2 = this.m_renderOriginY;
                float float3 = float1 + this.m_worldMap.getWidthInSquares() * this.m_worldScale;
                float float4 = float2 + this.m_worldMap.getHeightInSquares() * this.m_worldScale;
                WorldMapRenderer.m_vboLines.setMode(4);
                WorldMapRenderer.m_vboLines.addElement(float1 + float0, float4, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F);
                WorldMapRenderer.m_vboLines.addElement(float3, float4, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F);
                WorldMapRenderer.m_vboLines.addElement(float1 + float0, float4 + float0, 0.0F, 0.5F, 0.5F, 0.5F, 0.0F);
                WorldMapRenderer.m_vboLines.addElement(float3, float4, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F);
                WorldMapRenderer.m_vboLines.addElement(float3 + float0, float4 + float0, 0.0F, 0.5F, 0.5F, 0.5F, 0.0F);
                WorldMapRenderer.m_vboLines.addElement(float1 + float0, float4 + float0, 0.0F, 0.5F, 0.5F, 0.5F, 0.0F);
                WorldMapRenderer.m_vboLines.addElement(float3, float2 + float0, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F);
                WorldMapRenderer.m_vboLines.addElement(float3 + float0, float2 + float0, 0.0F, 0.5F, 0.5F, 0.5F, 0.0F);
                WorldMapRenderer.m_vboLines.addElement(float3, float4, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F);
                WorldMapRenderer.m_vboLines.addElement(float3 + float0, float2 + float0, 0.0F, 0.5F, 0.5F, 0.5F, 0.0F);
                WorldMapRenderer.m_vboLines.addElement(float3 + float0, float4 + float0, 0.0F, 0.5F, 0.5F, 0.5F, 0.0F);
                WorldMapRenderer.m_vboLines.addElement(float3, float4, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F);
            }
        }

        @Override
        public void postRender() {
            for (int int0 = 0; int0 < this.m_playerRenderData.length; int0++) {
                WorldMapRenderer.PlayerRenderData playerRenderData = this.m_playerRenderData[int0];
                if (playerRenderData.m_modelSlotRenderData != null) {
                    playerRenderData.m_modelSlotRenderData.postRender();
                }
            }
        }
    }

    private static final class PlayerRenderData {
        ModelSlotRenderData m_modelSlotRenderData;
        float m_angle;
        float m_x;
        float m_y;
    }

    public final class WorldMapBooleanOption extends BooleanConfigOption {
        public WorldMapBooleanOption(String string, boolean boolean0) {
            super(string, boolean0);
            WorldMapRenderer.this.options.add(this);
        }
    }

    public final class WorldMapDoubleOption extends DoubleConfigOption {
        public WorldMapDoubleOption(String string, double double0, double double1, double double2) {
            super(string, double0, double1, double2);
            WorldMapRenderer.this.options.add(this);
        }
    }
}
