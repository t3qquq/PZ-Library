// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import java.util.ArrayList;
import java.util.Objects;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import zombie.IndieGL;
import zombie.Lua.LuaManager;
import zombie.characters.action.ActionContext;
import zombie.characters.action.ActionGroup;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.opengl.PZGLUtil;
import zombie.core.opengl.VBOLines;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.advancedanimation.AnimNode;
import zombie.core.skinnedmodel.advancedanimation.AnimState;
import zombie.core.skinnedmodel.advancedanimation.AnimatedModel;
import zombie.core.skinnedmodel.advancedanimation.AnimationSet;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.AnimationMultiTrack;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.skinnedmodel.animation.Keyframe;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.ModelInstanceRenderData;
import zombie.core.skinnedmodel.model.SkinningBone;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.core.skinnedmodel.shader.ShaderManager;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.input.Mouse;
import zombie.iso.IsoUtils;
import zombie.iso.Vector2;
import zombie.popman.ObjectPool;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.ModelScript;
import zombie.scripting.objects.VehicleScript;
import zombie.ui.UIElement;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;

public final class UI3DScene extends UIElement {
    private final ArrayList<UI3DScene.SceneObject> m_objects = new ArrayList<>();
    private UI3DScene.View m_view = UI3DScene.View.Right;
    private UI3DScene.TransformMode m_transformMode = UI3DScene.TransformMode.Local;
    private int m_view_x = 0;
    private int m_view_y = 0;
    private final Vector3f m_viewRotation = new Vector3f();
    private int m_zoom = 3;
    private int m_zoomMax = 10;
    private int m_gridDivisions = 1;
    private UI3DScene.GridPlane m_gridPlane = UI3DScene.GridPlane.YZ;
    private final Matrix4f m_projection = new Matrix4f();
    private final Matrix4f m_modelView = new Matrix4f();
    private long VIEW_CHANGE_TIME = 350L;
    private long m_viewChangeTime;
    private final Quaternionf m_modelViewChange = new Quaternionf();
    private boolean m_bDrawGrid = true;
    private boolean m_bDrawGridAxes = false;
    private boolean m_bDrawGridPlane = false;
    private final UI3DScene.CharacterSceneModelCamera m_CharacterSceneModelCamera = new UI3DScene.CharacterSceneModelCamera();
    private final UI3DScene.VehicleSceneModelCamera m_VehicleSceneModelCamera = new UI3DScene.VehicleSceneModelCamera();
    private static final ObjectPool<UI3DScene.SetModelCamera> s_SetModelCameraPool = new ObjectPool<>(UI3DScene.SetModelCamera::new);
    private final UI3DScene.StateData[] m_stateData = new UI3DScene.StateData[3];
    private UI3DScene.Gizmo m_gizmo;
    private final UI3DScene.RotateGizmo m_rotateGizmo = new UI3DScene.RotateGizmo();
    private final UI3DScene.ScaleGizmo m_scaleGizmo = new UI3DScene.ScaleGizmo();
    private final UI3DScene.TranslateGizmo m_translateGizmo = new UI3DScene.TranslateGizmo();
    private final Vector3f m_gizmoPos = new Vector3f();
    private final Vector3f m_gizmoRotate = new Vector3f();
    private UI3DScene.SceneObject m_gizmoParent = null;
    private UI3DScene.SceneObject m_gizmoOrigin = null;
    private UI3DScene.SceneObject m_gizmoChild = null;
    private final UI3DScene.OriginAttachment m_originAttachment = new UI3DScene.OriginAttachment(this);
    private final UI3DScene.OriginBone m_originBone = new UI3DScene.OriginBone(this);
    private final UI3DScene.OriginGizmo m_originGizmo = new UI3DScene.OriginGizmo(this);
    private float m_gizmoScale = 1.0F;
    private String m_selectedAttachment = null;
    private final ArrayList<UI3DScene.PositionRotation> m_axes = new ArrayList<>();
    private final UI3DScene.OriginBone m_highlightBone = new UI3DScene.OriginBone(this);
    private static final ObjectPool<UI3DScene.PositionRotation> s_posRotPool = new ObjectPool<>(UI3DScene.PositionRotation::new);
    private final ArrayList<UI3DScene.AABB> m_aabb = new ArrayList<>();
    private static final ObjectPool<UI3DScene.AABB> s_aabbPool = new ObjectPool<>(UI3DScene.AABB::new);
    private final ArrayList<UI3DScene.Box3D> m_box3D = new ArrayList<>();
    private static final ObjectPool<UI3DScene.Box3D> s_box3DPool = new ObjectPool<>(UI3DScene.Box3D::new);
    final Vector3f tempVector3f = new Vector3f();
    final Vector4f tempVector4f = new Vector4f();
    final int[] m_viewport = new int[]{0, 0, 0, 0};
    private final float GRID_DARK = 0.1F;
    private final float GRID_LIGHT = 0.2F;
    private float GRID_ALPHA = 1.0F;
    private final int HALF_GRID = 5;
    private static final VBOLines vboLines = new VBOLines();
    private static final ThreadLocal<ObjectPool<UI3DScene.Ray>> TL_Ray_pool = ThreadLocal.withInitial(UI3DScene.RayObjectPool::new);
    private static final ThreadLocal<ObjectPool<UI3DScene.Plane>> TL_Plane_pool = ThreadLocal.withInitial(UI3DScene.PlaneObjectPool::new);
    static final float SMALL_NUM = 1.0E-8F;

    public UI3DScene(KahluaTable table) {
        super(table);

        for (int int0 = 0; int0 < this.m_stateData.length; int0++) {
            this.m_stateData[int0] = new UI3DScene.StateData();
            this.m_stateData[int0].m_gridPlaneDrawer = new UI3DScene.GridPlaneDrawer(this);
            this.m_stateData[int0].m_overlaysDrawer = new UI3DScene.OverlaysDrawer();
        }
    }

    UI3DScene.SceneObject getSceneObjectById(String string, boolean boolean0) {
        for (int int0 = 0; int0 < this.m_objects.size(); int0++) {
            UI3DScene.SceneObject sceneObject = this.m_objects.get(int0);
            if (sceneObject.m_id.equalsIgnoreCase(string)) {
                return sceneObject;
            }
        }

        if (boolean0) {
            throw new NullPointerException("scene object \"" + string + "\" not found");
        } else {
            return null;
        }
    }

    <C> C getSceneObjectById(String string, Class<C> clazz, boolean boolean0) {
        for (int int0 = 0; int0 < this.m_objects.size(); int0++) {
            UI3DScene.SceneObject sceneObject = this.m_objects.get(int0);
            if (sceneObject.m_id.equalsIgnoreCase(string)) {
                if (sceneObject.getClass() == clazz) {
                    return (C)clazz.cast(sceneObject);
                }

                if (boolean0) {
                    throw new ClassCastException(
                        "scene object \"" + string + "\" is " + sceneObject.getClass().getSimpleName() + " expected " + clazz.getSimpleName()
                    );
                }
            }
        }

        if (boolean0) {
            throw new NullPointerException("scene object \"" + string + "\" not found");
        } else {
            return null;
        }
    }

    @Override
    public void render() {
        if (this.isVisible()) {
            super.render();
            IndieGL.glClear(256);
            UI3DScene.StateData stateData = this.stateDataMain();
            this.calcMatrices(this.m_projection, this.m_modelView);
            stateData.m_projection.set(this.m_projection);
            long long0 = System.currentTimeMillis();
            if (this.m_viewChangeTime + this.VIEW_CHANGE_TIME > long0) {
                float float0 = (float)(this.m_viewChangeTime + this.VIEW_CHANGE_TIME - long0) / (float)this.VIEW_CHANGE_TIME;
                Quaternionf quaternionf = allocQuaternionf().setFromUnnormalized(this.m_modelView);
                stateData.m_modelView.set(this.m_modelViewChange.slerp(quaternionf, 1.0F - float0));
                releaseQuaternionf(quaternionf);
            } else {
                stateData.m_modelView.set(this.m_modelView);
            }

            stateData.m_zoom = this.m_zoom;
            if (this.m_bDrawGridPlane) {
                SpriteRenderer.instance.drawGeneric(stateData.m_gridPlaneDrawer);
            }

            PZArrayUtil.forEach(stateData.m_objectData, UI3DScene.SceneObjectRenderData::release);
            stateData.m_objectData.clear();

            for (int int0 = 0; int0 < this.m_objects.size(); int0++) {
                UI3DScene.SceneObject sceneObject = this.m_objects.get(int0);
                if (sceneObject.m_visible) {
                    if (sceneObject.m_autoRotate) {
                        sceneObject.m_autoRotateAngle = (float)(sceneObject.m_autoRotateAngle + UIManager.getMillisSinceLastRender() / 30.0);
                        if (sceneObject.m_autoRotateAngle > 360.0F) {
                            sceneObject.m_autoRotateAngle = 0.0F;
                        }
                    }

                    UI3DScene.SceneObjectRenderData sceneObjectRenderData = sceneObject.renderMain();
                    if (sceneObjectRenderData != null) {
                        stateData.m_objectData.add(sceneObjectRenderData);
                    }
                }
            }

            float float1 = Mouse.getXA() - this.getAbsoluteX().intValue();
            float float2 = Mouse.getYA() - this.getAbsoluteY().intValue();
            stateData.m_gizmo = this.m_gizmo;
            if (this.m_gizmo != null) {
                stateData.m_gizmoTranslate.set(this.m_gizmoPos);
                stateData.m_gizmoRotate.set(this.m_gizmoRotate);
                stateData.m_gizmoTransform.translation(this.m_gizmoPos);
                stateData.m_gizmoTransform
                    .rotateXYZ(
                        this.m_gizmoRotate.x * (float) (Math.PI / 180.0),
                        this.m_gizmoRotate.y * (float) (Math.PI / 180.0),
                        this.m_gizmoRotate.z * (float) (Math.PI / 180.0)
                    );
                stateData.m_gizmoAxis = this.m_gizmo.hitTest(float1, float2);
            }

            stateData.m_gizmoChildTransform.identity();
            stateData.m_selectedAttachmentIsChildAttachment = this.m_gizmoChild != null
                && this.m_gizmoChild.m_attachment != null
                && this.m_gizmoChild.m_attachment.equals(this.m_selectedAttachment);
            if (this.m_gizmoChild != null) {
                this.m_gizmoChild.getLocalTransform(stateData.m_gizmoChildTransform);
            }

            stateData.m_gizmoOriginTransform.identity();
            stateData.m_hasGizmoOrigin = this.m_gizmoOrigin != null;
            if (this.m_gizmoOrigin != null && this.m_gizmoOrigin != this.m_gizmoParent) {
                this.m_gizmoOrigin.getGlobalTransform(stateData.m_gizmoOriginTransform);
            }

            stateData.m_gizmoParentTransform.identity();
            if (this.m_gizmoParent != null) {
                this.m_gizmoParent.getGlobalTransform(stateData.m_gizmoParentTransform);
            }

            stateData.m_overlaysDrawer.init();
            SpriteRenderer.instance.drawGeneric(stateData.m_overlaysDrawer);
            if (this.m_bDrawGrid) {
                Vector3f vector3f0 = this.uiToScene(float1, float2, 0.0F, this.tempVector3f);
                if (this.m_view == UI3DScene.View.UserDefined) {
                    Vector3f vector3f1 = allocVector3f();
                    switch (this.m_gridPlane) {
                        case XY:
                            vector3f1.set(0.0F, 0.0F, 1.0F);
                            break;
                        case XZ:
                            vector3f1.set(0.0F, 1.0F, 0.0F);
                            break;
                        case YZ:
                            vector3f1.set(1.0F, 0.0F, 0.0F);
                    }

                    Vector3f vector3f2 = allocVector3f().set(0.0F);
                    UI3DScene.Plane plane = allocPlane().set(vector3f1, vector3f2);
                    releaseVector3f(vector3f1);
                    releaseVector3f(vector3f2);
                    UI3DScene.Ray ray = this.getCameraRay(float1, this.screenHeight() - float2, allocRay());
                    if (intersect_ray_plane(plane, ray, vector3f0) != 1) {
                        vector3f0.set(0.0F);
                    }

                    releasePlane(plane);
                    releaseRay(ray);
                }

                vector3f0.x = Math.round(vector3f0.x * this.gridMult()) / this.gridMult();
                vector3f0.y = Math.round(vector3f0.y * this.gridMult()) / this.gridMult();
                vector3f0.z = Math.round(vector3f0.z * this.gridMult()) / this.gridMult();
                this.DrawText(UIFont.Small, String.valueOf(vector3f0.x), this.width - 200.0F, 10.0, 1.0, 0.0, 0.0, 1.0);
                this.DrawText(UIFont.Small, String.valueOf(vector3f0.y), this.width - 150.0F, 10.0, 0.0, 1.0, 0.0, 1.0);
                this.DrawText(UIFont.Small, String.valueOf(vector3f0.z), this.width - 100.0F, 10.0, 0.0, 0.5, 1.0, 1.0);
            }

            if (this.m_gizmo == this.m_rotateGizmo && this.m_rotateGizmo.m_trackAxis != UI3DScene.Axis.None) {
                Vector3f vector3f3 = this.m_rotateGizmo.m_startXfrm.getTranslation(allocVector3f());
                float float3 = this.sceneToUIX(vector3f3.x, vector3f3.y, vector3f3.z);
                float float4 = this.sceneToUIY(vector3f3.x, vector3f3.y, vector3f3.z);
                LineDrawer.drawLine(float3, float4, float1, float2, 0.5F, 0.5F, 0.5F, 1.0F, 1);
                releaseVector3f(vector3f3);
            }

            if (this.m_highlightBone.m_boneName != null) {
                Matrix4f matrix4f = this.m_highlightBone.getGlobalTransform(allocMatrix4f());
                this.m_highlightBone.m_character.getGlobalTransform(allocMatrix4f()).mul(matrix4f, matrix4f);
                Vector3f vector3f4 = matrix4f.getTranslation(allocVector3f());
                float float5 = this.sceneToUIX(vector3f4.x, vector3f4.y, vector3f4.z);
                float float6 = this.sceneToUIY(vector3f4.x, vector3f4.y, vector3f4.z);
                LineDrawer.drawCircle(float5, float6, 10.0F, 16, 1.0F, 1.0F, 1.0F);
                releaseVector3f(vector3f4);
                releaseMatrix4f(matrix4f);
            }
        }
    }

    private float gridMult() {
        return 100 * this.m_gridDivisions;
    }

    private float zoomMult() {
        return (float)Math.exp(this.m_zoom * 0.2F) * 160.0F / Math.max(1.82F, 1.0F);
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

    public Object fromLua0(String func) {
        switch (func) {
            case "clearAABBs":
                s_aabbPool.release(this.m_aabb);
                this.m_aabb.clear();
                return null;
            case "clearAxes":
                s_posRotPool.release(this.m_axes);
                this.m_axes.clear();
                return null;
            case "clearBox3Ds":
                s_box3DPool.release(this.m_box3D);
                this.m_box3D.clear();
                return null;
            case "clearGizmoRotate":
                this.m_gizmoRotate.set(0.0F);
                return null;
            case "clearHighlightBone":
                this.m_highlightBone.m_boneName = null;
                return null;
            case "getGizmoPos":
                return this.m_gizmoPos;
            case "getGridMult":
                return BoxedStaticValues.toDouble(this.gridMult());
            case "getView":
                return this.m_view.name();
            case "getViewRotation":
                return this.m_viewRotation;
            case "getModelCount":
                int int0 = 0;

                for (int int1 = 0; int1 < this.m_objects.size(); int1++) {
                    if (this.m_objects.get(int1) instanceof UI3DScene.SceneModel) {
                        int0++;
                    }
                }

                return BoxedStaticValues.toDouble(int0);
            case "stopGizmoTracking":
                if (this.m_gizmo != null) {
                    this.m_gizmo.stopTracking();
                }

                return null;
            default:
                throw new IllegalArgumentException("unhandled \"" + func + "\"");
        }
    }

    public Object fromLua1(String func, Object arg0) {
        switch (func) {
            case "createCharacter":
                UI3DScene.SceneObject sceneObject8 = this.getSceneObjectById((String)arg0, false);
                if (sceneObject8 != null) {
                    throw new IllegalStateException("scene object \"" + arg0 + "\" exists");
                }

                UI3DScene.SceneCharacter sceneCharacter3 = new UI3DScene.SceneCharacter(this, (String)arg0);
                this.m_objects.add(sceneCharacter3);
                return sceneCharacter3;
            case "createVehicle":
                UI3DScene.SceneObject sceneObject7 = this.getSceneObjectById((String)arg0, false);
                if (sceneObject7 != null) {
                    throw new IllegalStateException("scene object \"" + arg0 + "\" exists");
                }

                UI3DScene.SceneVehicle sceneVehicle1 = new UI3DScene.SceneVehicle(this, (String)arg0);
                this.m_objects.add(sceneVehicle1);
                return null;
            case "getCharacterAnimationDuration":
                UI3DScene.SceneCharacter sceneCharacter2 = this.getSceneObjectById((String)arg0, UI3DScene.SceneCharacter.class, true);
                AnimationPlayer animationPlayer1 = sceneCharacter2.m_animatedModel.getAnimationPlayer();
                if (animationPlayer1 == null) {
                    return null;
                } else {
                    AnimationMultiTrack animationMultiTrack1 = animationPlayer1.getMultiTrack();
                    if (animationMultiTrack1 != null && !animationMultiTrack1.getTracks().isEmpty()) {
                        return KahluaUtil.toDouble((double)animationMultiTrack1.getTracks().get(0).getDuration());
                    }

                    return null;
                }
            case "getCharacterAnimationTime":
                UI3DScene.SceneCharacter sceneCharacter1 = this.getSceneObjectById((String)arg0, UI3DScene.SceneCharacter.class, true);
                AnimationPlayer animationPlayer0 = sceneCharacter1.m_animatedModel.getAnimationPlayer();
                if (animationPlayer0 == null) {
                    return null;
                } else {
                    AnimationMultiTrack animationMultiTrack0 = animationPlayer0.getMultiTrack();
                    if (animationMultiTrack0 != null && !animationMultiTrack0.getTracks().isEmpty()) {
                        return KahluaUtil.toDouble((double)animationMultiTrack0.getTracks().get(0).getCurrentTimeValue());
                    }

                    return null;
                }
            case "getModelScript":
                int int1 = 0;

                for (int int2 = 0; int2 < this.m_objects.size(); int2++) {
                    UI3DScene.SceneModel sceneModel1 = Type.tryCastTo(this.m_objects.get(int2), UI3DScene.SceneModel.class);
                    if (sceneModel1 != null && int1++ == ((Double)arg0).intValue()) {
                        return sceneModel1.m_modelScript;
                    }
                }

                return null;
            case "getObjectAutoRotate":
                UI3DScene.SceneObject sceneObject6 = this.getSceneObjectById((String)arg0, true);
                return sceneObject6.m_autoRotate ? Boolean.TRUE : Boolean.FALSE;
            case "getObjectParent":
                UI3DScene.SceneObject sceneObject5 = this.getSceneObjectById((String)arg0, true);
                return sceneObject5.m_parent == null ? null : sceneObject5.m_parent.m_id;
            case "getObjectParentAttachment":
                UI3DScene.SceneObject sceneObject0 = this.getSceneObjectById((String)arg0, true);
                return sceneObject0.m_parentAttachment;
            case "getObjectRotation":
                UI3DScene.SceneObject sceneObject1 = this.getSceneObjectById((String)arg0, true);
                return sceneObject1.m_rotate;
            case "getObjectTranslation":
                UI3DScene.SceneObject sceneObject2 = this.getSceneObjectById((String)arg0, true);
                return sceneObject2.m_translate;
            case "getVehicleScript":
                UI3DScene.SceneVehicle sceneVehicle0 = this.getSceneObjectById((String)arg0, UI3DScene.SceneVehicle.class, true);
                return sceneVehicle0.m_script;
            case "isCharacterFemale":
                UI3DScene.SceneCharacter sceneCharacter0 = this.getSceneObjectById((String)arg0, UI3DScene.SceneCharacter.class, true);
                return sceneCharacter0.m_animatedModel.isFemale();
            case "isObjectVisible":
                UI3DScene.SceneObject sceneObject4 = this.getSceneObjectById((String)arg0, true);
                return sceneObject4.m_visible ? Boolean.TRUE : Boolean.FALSE;
            case "removeModel":
                UI3DScene.SceneModel sceneModel0 = this.getSceneObjectById((String)arg0, UI3DScene.SceneModel.class, true);
                this.m_objects.remove(sceneModel0);

                for (UI3DScene.SceneObject sceneObject3 : this.m_objects) {
                    if (sceneObject3.m_parent == sceneModel0) {
                        sceneObject3.m_attachment = null;
                        sceneObject3.m_parent = null;
                        sceneObject3.m_parentAttachment = null;
                    }
                }

                return null;
            case "setDrawGrid":
                this.m_bDrawGrid = (Boolean)arg0;
                return null;
            case "setDrawGridAxes":
                this.m_bDrawGridAxes = (Boolean)arg0;
                return null;
            case "setDrawGridPlane":
                this.m_bDrawGridPlane = (Boolean)arg0;
                return null;
            case "setGizmoOrigin":
                String string1 = (String)arg0;
                byte byte0 = -1;
                switch (string1.hashCode()) {
                    case 3387192:
                        if (string1.equals("none")) {
                            byte0 = 0;
                        }
                    default:
                        switch (byte0) {
                            case 0:
                                this.m_gizmoParent = null;
                                this.m_gizmoOrigin = null;
                                this.m_gizmoChild = null;
                            default:
                                return null;
                        }
                }
            case "setGizmoPos":
                Vector3f vector3f1 = (Vector3f)arg0;
                if (!this.m_gizmoPos.equals(vector3f1)) {
                    this.m_gizmoPos.set(vector3f1);
                }

                return null;
            case "setGizmoRotate":
                Vector3f vector3f0 = (Vector3f)arg0;
                if (!this.m_gizmoRotate.equals(vector3f0)) {
                    this.m_gizmoRotate.set(vector3f0);
                }

                return null;
            case "setGizmoScale":
                this.m_gizmoScale = Math.max(((Double)arg0).floatValue(), 0.01F);
                return null;
            case "setGizmoVisible":
                String string0 = (String)arg0;
                this.m_rotateGizmo.m_visible = "rotate".equalsIgnoreCase(string0);
                this.m_scaleGizmo.m_visible = "scale".equalsIgnoreCase(string0);
                this.m_translateGizmo.m_visible = "translate".equalsIgnoreCase(string0);
                switch (string0) {
                    case "rotate":
                        this.m_gizmo = this.m_rotateGizmo;
                        break;
                    case "scale":
                        this.m_gizmo = this.m_scaleGizmo;
                        break;
                    case "translate":
                        this.m_gizmo = this.m_translateGizmo;
                        break;
                    default:
                        this.m_gizmo = null;
                }

                return null;
            case "setGridMult":
                this.m_gridDivisions = PZMath.clamp(((Double)arg0).intValue(), 1, 100);
                return null;
            case "setGridPlane":
                this.m_gridPlane = UI3DScene.GridPlane.valueOf((String)arg0);
                return null;
            case "setMaxZoom":
                this.m_zoomMax = PZMath.clamp(((Double)arg0).intValue(), 1, 20);
                return null;
            case "setSelectedAttachment":
                this.m_selectedAttachment = (String)arg0;
                return null;
            case "setTransformMode":
                this.m_transformMode = UI3DScene.TransformMode.valueOf((String)arg0);
                return null;
            case "setZoom":
                this.m_zoom = PZMath.clamp(((Double)arg0).intValue(), 1, this.m_zoomMax);
                this.calcMatrices(this.m_projection, this.m_modelView);
                return null;
            case "setView":
                UI3DScene.View view = this.m_view;
                this.m_view = UI3DScene.View.valueOf((String)arg0);
                if (view != this.m_view) {
                    long long0 = System.currentTimeMillis();
                    if (this.m_viewChangeTime + this.VIEW_CHANGE_TIME < long0) {
                        this.m_modelViewChange.setFromUnnormalized(this.m_modelView);
                    }

                    this.m_viewChangeTime = long0;
                }

                this.calcMatrices(this.m_projection, this.m_modelView);
                return null;
            case "zoom":
                int int0 = -((Double)arg0).intValue();
                float float0 = Mouse.getXA() - this.getAbsoluteX().intValue();
                float float1 = Mouse.getYA() - this.getAbsoluteY().intValue();
                float float2 = this.uiToSceneX(float0, float1);
                float float3 = this.uiToSceneY(float0, float1);
                this.m_zoom = PZMath.clamp(this.m_zoom + int0, 1, this.m_zoomMax);
                this.calcMatrices(this.m_projection, this.m_modelView);
                float float4 = this.uiToSceneX(float0, float1);
                float float5 = this.uiToSceneY(float0, float1);
                this.m_view_x = (int)(this.m_view_x - (float4 - float2) * this.zoomMult());
                this.m_view_y = (int)(this.m_view_y + (float5 - float3) * this.zoomMult());
                this.calcMatrices(this.m_projection, this.m_modelView);
                return null;
            default:
                throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\"", func, arg0));
        }
    }

    public Object fromLua2(String func, Object arg0, Object arg1) {
        switch (func) {
            case "addAttachment":
                UI3DScene.SceneModel sceneModel5 = this.getSceneObjectById((String)arg0, UI3DScene.SceneModel.class, true);
                if (sceneModel5.m_modelScript.getAttachmentById((String)arg1) != null) {
                    throw new IllegalArgumentException("model script \"" + arg0 + "\" already has attachment named \"" + arg1 + "\"");
                }

                ModelAttachment modelAttachment1 = new ModelAttachment((String)arg1);
                sceneModel5.m_modelScript.addAttachment(modelAttachment1);
                return modelAttachment1;
            case "addBoneAxis":
                UI3DScene.SceneCharacter sceneCharacter0 = this.getSceneObjectById((String)arg0, UI3DScene.SceneCharacter.class, true);
                String string0 = (String)arg1;
                UI3DScene.PositionRotation positionRotation = sceneCharacter0.getBoneAxis(string0, s_posRotPool.alloc());
                this.m_axes.add(positionRotation);
                return null;
            case "applyDeltaRotation":
                Vector3f vector3f0 = (Vector3f)arg0;
                Vector3f vector3f1 = (Vector3f)arg1;
                Quaternionf quaternionf0 = allocQuaternionf()
                    .rotationXYZ(vector3f0.x * (float) (Math.PI / 180.0), vector3f0.y * (float) (Math.PI / 180.0), vector3f0.z * (float) (Math.PI / 180.0));
                Quaternionf quaternionf1 = allocQuaternionf()
                    .rotationXYZ(vector3f1.x * (float) (Math.PI / 180.0), vector3f1.y * (float) (Math.PI / 180.0), vector3f1.z * (float) (Math.PI / 180.0));
                quaternionf0.mul(quaternionf1);
                quaternionf0.getEulerAnglesXYZ(vector3f0);
                releaseQuaternionf(quaternionf0);
                releaseQuaternionf(quaternionf1);
                vector3f0.mul(180.0F / (float)Math.PI);
                vector3f0.x = (float)Math.floor(vector3f0.x + 0.5F);
                vector3f0.y = (float)Math.floor(vector3f0.y + 0.5F);
                vector3f0.z = (float)Math.floor(vector3f0.z + 0.5F);
                return vector3f0;
            case "createModel":
                UI3DScene.SceneObject sceneObject2 = this.getSceneObjectById((String)arg0, false);
                if (sceneObject2 != null) {
                    throw new IllegalStateException("scene object \"" + arg0 + "\" exists");
                } else {
                    ModelScript modelScript = ScriptManager.instance.getModelScript((String)arg1);
                    if (modelScript == null) {
                        throw new NullPointerException("model script \"" + arg1 + "\" not found");
                    } else {
                        Model model = ModelManager.instance.getLoadedModel((String)arg1);
                        if (model == null) {
                            throw new NullPointerException("model \"" + arg1 + "\" not found");
                        }

                        UI3DScene.SceneModel sceneModel4 = new UI3DScene.SceneModel(this, (String)arg0, modelScript, model);
                        this.m_objects.add(sceneModel4);
                        return null;
                    }
                }
            case "dragGizmo":
                float float0 = ((Double)arg0).floatValue();
                float float1 = ((Double)arg1).floatValue();
                if (this.m_gizmo == null) {
                    throw new NullPointerException("gizmo is null");
                }

                this.m_gizmo.updateTracking(float0, float1);
                return null;
            case "dragView":
                int int0 = ((Double)arg0).intValue();
                int int1 = ((Double)arg1).intValue();
                this.m_view_x -= int0;
                this.m_view_y -= int1;
                this.calcMatrices(this.m_projection, this.m_modelView);
                return null;
            case "getCharacterAnimationKeyframeTimes":
                UI3DScene.SceneCharacter sceneCharacter14 = this.getSceneObjectById((String)arg0, UI3DScene.SceneCharacter.class, true);
                AnimationPlayer animationPlayer1 = sceneCharacter14.m_animatedModel.getAnimationPlayer();
                if (animationPlayer1 == null) {
                    return null;
                } else {
                    AnimationMultiTrack animationMultiTrack2 = animationPlayer1.getMultiTrack();
                    if (animationMultiTrack2 != null && !animationMultiTrack2.getTracks().isEmpty()) {
                        AnimationTrack animationTrack = animationMultiTrack2.getTracks().get(0);
                        AnimationClip animationClip = animationTrack.getClip();
                        if (animationClip == null) {
                            return null;
                        }

                        if (arg1 == null) {
                            arg1 = new ArrayList();
                        }

                        ArrayList arrayList = (ArrayList)arg1;
                        arrayList.clear();
                        Keyframe[] keyframes = animationClip.getKeyframes();

                        for (int int4 = 0; int4 < keyframes.length; int4++) {
                            Keyframe keyframe = keyframes[int4];
                            Double double0 = KahluaUtil.toDouble((double)keyframe.Time);
                            if (!arrayList.contains(double0)) {
                                arrayList.add(double0);
                            }
                        }

                        return arrayList;
                    }

                    return null;
                }
            case "removeAttachment":
                UI3DScene.SceneModel sceneModel3 = this.getSceneObjectById((String)arg0, UI3DScene.SceneModel.class, true);
                ModelAttachment modelAttachment0 = sceneModel3.m_modelScript.getAttachmentById((String)arg1);
                if (modelAttachment0 == null) {
                    throw new IllegalArgumentException("model script \"" + arg0 + "\" attachment \"" + arg1 + "\" not found");
                }

                sceneModel3.m_modelScript.removeAttachment(modelAttachment0);
                return null;
            case "setCharacterAlpha":
                UI3DScene.SceneCharacter sceneCharacter1 = this.getSceneObjectById((String)arg0, UI3DScene.SceneCharacter.class, true);
                sceneCharacter1.m_animatedModel.setAlpha(((Double)arg1).floatValue());
                return null;
            case "setCharacterAnimate":
                UI3DScene.SceneCharacter sceneCharacter2 = this.getSceneObjectById((String)arg0, UI3DScene.SceneCharacter.class, true);
                sceneCharacter2.m_animatedModel.setAnimate((Boolean)arg1);
                return null;
            case "setCharacterAnimationClip":
                UI3DScene.SceneCharacter sceneCharacter13 = this.getSceneObjectById((String)arg0, UI3DScene.SceneCharacter.class, true);
                AnimationSet animationSet = AnimationSet.GetAnimationSet(sceneCharacter13.m_animatedModel.GetAnimSetName(), false);
                if (animationSet == null) {
                    return null;
                } else {
                    AnimState animState = animationSet.GetState(sceneCharacter13.m_animatedModel.getState());
                    if (animState != null && !animState.m_Nodes.isEmpty()) {
                        AnimNode animNode = animState.m_Nodes.get(0);
                        animNode.m_AnimName = (String)arg1;
                        sceneCharacter13.m_animatedModel.getAdvancedAnimator().OnAnimDataChanged(false);
                        sceneCharacter13.m_animatedModel.getAdvancedAnimator().SetState(animState.m_Name);
                        return null;
                    }

                    return null;
                }
            case "setCharacterAnimationSpeed":
                UI3DScene.SceneCharacter sceneCharacter12 = this.getSceneObjectById((String)arg0, UI3DScene.SceneCharacter.class, true);
                AnimationMultiTrack animationMultiTrack1 = sceneCharacter12.m_animatedModel.getAnimationPlayer().getMultiTrack();
                if (animationMultiTrack1.getTracks().isEmpty()) {
                    return null;
                }

                animationMultiTrack1.getTracks().get(0).SpeedDelta = PZMath.clamp(((Double)arg1).floatValue(), 0.0F, 10.0F);
                return null;
            case "setCharacterAnimationTime":
                UI3DScene.SceneCharacter sceneCharacter11 = this.getSceneObjectById((String)arg0, UI3DScene.SceneCharacter.class, true);
                sceneCharacter11.m_animatedModel.setTrackTime(((Double)arg1).floatValue());
                AnimationPlayer animationPlayer0 = sceneCharacter11.m_animatedModel.getAnimationPlayer();
                if (animationPlayer0 == null) {
                    return null;
                } else {
                    AnimationMultiTrack animationMultiTrack0 = animationPlayer0.getMultiTrack();
                    if (animationMultiTrack0 != null && !animationMultiTrack0.getTracks().isEmpty()) {
                        animationMultiTrack0.getTracks().get(0).setCurrentTimeValue(((Double)arg1).floatValue());
                        return null;
                    }

                    return null;
                }
            case "setCharacterAnimSet":
                UI3DScene.SceneCharacter sceneCharacter10 = this.getSceneObjectById((String)arg0, UI3DScene.SceneCharacter.class, true);
                String string3 = (String)arg1;
                if (!string3.equals(sceneCharacter10.m_animatedModel.GetAnimSetName())) {
                    sceneCharacter10.m_animatedModel.setAnimSetName(string3);
                    sceneCharacter10.m_animatedModel.getAdvancedAnimator().OnAnimDataChanged(false);
                    ActionGroup actionGroup = ActionGroup.getActionGroup(sceneCharacter10.m_animatedModel.GetAnimSetName());
                    ActionContext actionContext = sceneCharacter10.m_animatedModel.getActionContext();
                    if (actionGroup != actionContext.getGroup()) {
                        actionContext.setGroup(actionGroup);
                    }

                    sceneCharacter10.m_animatedModel
                        .getAdvancedAnimator()
                        .SetState(actionContext.getCurrentStateName(), PZArrayUtil.listConvert(actionContext.getChildStates(), actionState -> actionState.name));
                }

                return null;
            case "setCharacterClearDepthBuffer":
                UI3DScene.SceneCharacter sceneCharacter3 = this.getSceneObjectById((String)arg0, UI3DScene.SceneCharacter.class, true);
                sceneCharacter3.m_bClearDepthBuffer = (Boolean)arg1;
                return null;
            case "setCharacterFemale":
                UI3DScene.SceneCharacter sceneCharacter9 = this.getSceneObjectById((String)arg0, UI3DScene.SceneCharacter.class, true);
                boolean boolean0 = (Boolean)arg1;
                if (boolean0 != sceneCharacter9.m_animatedModel.isFemale()) {
                    sceneCharacter9.m_animatedModel.setOutfitName("Naked", boolean0, false);
                }

                return null;
            case "setCharacterShowBones":
                UI3DScene.SceneCharacter sceneCharacter4 = this.getSceneObjectById((String)arg0, UI3DScene.SceneCharacter.class, true);
                sceneCharacter4.m_bShowBones = (Boolean)arg1;
                return null;
            case "setCharacterUseDeferredMovement":
                UI3DScene.SceneCharacter sceneCharacter5 = this.getSceneObjectById((String)arg0, UI3DScene.SceneCharacter.class, true);
                sceneCharacter5.m_bUseDeferredMovement = (Boolean)arg1;
                return null;
            case "setGizmoOrigin":
                String string2 = (String)arg0;
                switch (string2) {
                    case "centerOfMass":
                        this.m_gizmoParent = this.getSceneObjectById((String)arg1, UI3DScene.SceneVehicle.class, true);
                        this.m_gizmoOrigin = this.m_gizmoParent;
                        this.m_gizmoChild = null;
                        break;
                    case "chassis":
                        UI3DScene.SceneVehicle sceneVehicle1 = this.getSceneObjectById((String)arg1, UI3DScene.SceneVehicle.class, true);
                        this.m_gizmoParent = sceneVehicle1;
                        this.m_originGizmo.m_translate.set(sceneVehicle1.m_script.getCenterOfMassOffset());
                        this.m_originGizmo.m_rotate.zero();
                        this.m_gizmoOrigin = this.m_originGizmo;
                        this.m_gizmoChild = null;
                        break;
                    case "character":
                        UI3DScene.SceneCharacter sceneCharacter8 = this.getSceneObjectById((String)arg1, UI3DScene.SceneCharacter.class, true);
                        this.m_gizmoParent = sceneCharacter8;
                        this.m_gizmoOrigin = this.m_gizmoParent;
                        this.m_gizmoChild = null;
                        break;
                    case "model":
                        UI3DScene.SceneModel sceneModel2 = this.getSceneObjectById((String)arg1, UI3DScene.SceneModel.class, true);
                        this.m_gizmoParent = sceneModel2;
                        this.m_gizmoOrigin = this.m_gizmoParent;
                        this.m_gizmoChild = null;
                        break;
                    case "vehicleModel":
                        UI3DScene.SceneVehicle sceneVehicle2 = this.getSceneObjectById((String)arg1, UI3DScene.SceneVehicle.class, true);
                        this.m_gizmoParent = sceneVehicle2;
                        this.m_originGizmo.m_translate.set(sceneVehicle2.m_script.getModel().getOffset());
                        this.m_originGizmo.m_rotate.zero();
                        this.m_gizmoOrigin = this.m_originGizmo;
                        this.m_gizmoChild = null;
                }

                return null;
            case "setCharacterState":
                UI3DScene.SceneCharacter sceneCharacter6 = this.getSceneObjectById((String)arg0, UI3DScene.SceneCharacter.class, true);
                sceneCharacter6.m_animatedModel.setState((String)arg1);
                return null;
            case "setHighlightBone":
                UI3DScene.SceneCharacter sceneCharacter7 = this.getSceneObjectById((String)arg0, UI3DScene.SceneCharacter.class, true);
                String string1 = (String)arg1;
                this.m_highlightBone.m_character = sceneCharacter7;
                this.m_highlightBone.m_boneName = string1;
                return null;
            case "setModelUseWorldAttachment":
                UI3DScene.SceneModel sceneModel0 = this.getSceneObjectById((String)arg0, UI3DScene.SceneModel.class, true);
                sceneModel0.m_useWorldAttachment = (Boolean)arg1;
                return null;
            case "setModelWeaponRotationHack":
                UI3DScene.SceneModel sceneModel1 = this.getSceneObjectById((String)arg0, UI3DScene.SceneModel.class, true);
                sceneModel1.m_weaponRotationHack = (Boolean)arg1;
                return null;
            case "setObjectAutoRotate":
                UI3DScene.SceneObject sceneObject1 = this.getSceneObjectById((String)arg0, true);
                sceneObject1.m_autoRotate = (Boolean)arg1;
                if (!sceneObject1.m_autoRotate) {
                    sceneObject1.m_autoRotateAngle = 0.0F;
                }

                return null;
            case "setObjectVisible":
                UI3DScene.SceneObject sceneObject0 = this.getSceneObjectById((String)arg0, true);
                sceneObject0.m_visible = (Boolean)arg1;
                return null;
            case "setVehicleScript":
                UI3DScene.SceneVehicle sceneVehicle0 = this.getSceneObjectById((String)arg0, UI3DScene.SceneVehicle.class, true);
                sceneVehicle0.setScriptName((String)arg1);
                return null;
            case "testGizmoAxis":
                int int2 = ((Double)arg0).intValue();
                int int3 = ((Double)arg1).intValue();
                if (this.m_gizmo == null) {
                    return "None";
                }

                return this.m_gizmo.hitTest(int2, int3).toString();
            default:
                throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\" \"%s\"", func, arg0, arg1));
        }
    }

    public Object fromLua3(String func, Object arg0, Object arg1, Object arg2) {
        switch (func) {
            case "addAxis":
                float float0 = ((Double)arg0).floatValue();
                float float1 = ((Double)arg1).floatValue();
                float float2 = ((Double)arg2).floatValue();
                this.m_axes.add(s_posRotPool.alloc().set(float0, float1, float2));
                return null;
            case "pickCharacterBone":
                UI3DScene.SceneCharacter sceneCharacter0 = this.getSceneObjectById((String)arg0, UI3DScene.SceneCharacter.class, true);
                float float3 = ((Double)arg1).floatValue();
                float float4 = ((Double)arg2).floatValue();
                return sceneCharacter0.pickBone(float3, float4);
            case "setGizmoOrigin":
                String string = (String)arg0;
                byte byte0 = -1;
                switch (string.hashCode()) {
                    case 3029700:
                        if (string.equals("bone")) {
                            byte0 = 0;
                        }
                    default:
                        switch (byte0) {
                            case 0:
                                UI3DScene.SceneCharacter sceneCharacter1 = this.getSceneObjectById((String)arg1, UI3DScene.SceneCharacter.class, true);
                                this.m_gizmoParent = sceneCharacter1;
                                this.m_originBone.m_character = sceneCharacter1;
                                this.m_originBone.m_boneName = (String)arg2;
                                this.m_gizmoOrigin = this.m_originBone;
                                this.m_gizmoChild = null;
                            default:
                                return null;
                        }
                }
            case "setGizmoXYZ":
                float float5 = ((Double)arg0).floatValue();
                float float6 = ((Double)arg1).floatValue();
                float float7 = ((Double)arg2).floatValue();
                this.m_gizmoPos.set(float5, float6, float7);
                return null;
            case "startGizmoTracking":
                float float11 = ((Double)arg0).floatValue();
                float float12 = ((Double)arg1).floatValue();
                UI3DScene.Axis axis = UI3DScene.Axis.valueOf((String)arg2);
                if (this.m_gizmo != null) {
                    this.m_gizmo.startTracking(float11, float12, axis);
                }

                return null;
            case "setViewRotation":
                float float8 = ((Double)arg0).floatValue();
                float float9 = ((Double)arg1).floatValue();
                float float10 = ((Double)arg2).floatValue();
                float8 %= 360.0F;
                float9 %= 360.0F;
                float10 %= 360.0F;
                this.m_viewRotation.set(float8, float9, float10);
                return null;
            default:
                throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\" \"%s\" \"%s\"", func, arg0, arg1, arg2));
        }
    }

    public Object fromLua4(String func, Object arg0, Object arg1, Object arg2, Object arg3) {
        switch (func) {
            case "setGizmoOrigin":
                String string1 = (String)arg0;
                byte byte0 = -1;
                switch (string1.hashCode()) {
                    case -1963501277:
                        if (string1.equals("attachment")) {
                            byte0 = 0;
                        }
                    default:
                        switch (byte0) {
                            case 0:
                                UI3DScene.SceneObject sceneObject2 = this.getSceneObjectById((String)arg1, true);
                                this.m_gizmoParent = this.getSceneObjectById((String)arg2, true);
                                this.m_originAttachment.m_object = this.m_gizmoParent;
                                this.m_originAttachment.m_attachmentName = (String)arg3;
                                this.m_gizmoOrigin = this.m_originAttachment;
                                this.m_gizmoChild = sceneObject2;
                            default:
                                return null;
                        }
                }
            case "setObjectParent":
                UI3DScene.SceneObject sceneObject1 = this.getSceneObjectById((String)arg0, true);
                sceneObject1.m_translate.zero();
                sceneObject1.m_rotate.zero();
                sceneObject1.m_attachment = (String)arg1;
                sceneObject1.m_parent = this.getSceneObjectById((String)arg2, false);
                sceneObject1.m_parentAttachment = (String)arg3;
                if (sceneObject1.m_parent != null && sceneObject1.m_parent.m_parent == sceneObject1) {
                    sceneObject1.m_parent.m_parent = null;
                }

                return null;
            case "setObjectPosition":
                UI3DScene.SceneObject sceneObject0 = this.getSceneObjectById((String)arg0, true);
                sceneObject0.m_translate.set(((Double)arg1).floatValue(), ((Double)arg2).floatValue(), ((Double)arg3).floatValue());
                return null;
            case "setPassengerPosition":
                UI3DScene.SceneCharacter sceneCharacter = this.getSceneObjectById((String)arg0, UI3DScene.SceneCharacter.class, true);
                UI3DScene.SceneVehicle sceneVehicle = this.getSceneObjectById((String)arg1, UI3DScene.SceneVehicle.class, true);
                VehicleScript.Passenger passenger = sceneVehicle.m_script.getPassengerById((String)arg2);
                if (passenger == null) {
                    return null;
                }

                VehicleScript.Position position = passenger.getPositionById((String)arg3);
                if (position != null) {
                    this.tempVector3f.set(sceneVehicle.m_script.getModel().getOffset());
                    this.tempVector3f.add(position.getOffset());
                    this.tempVector3f.z *= -1.0F;
                    sceneCharacter.m_translate.set(this.tempVector3f);
                    sceneCharacter.m_rotate.set(position.rotate);
                    sceneCharacter.m_parent = sceneVehicle;
                    if (sceneCharacter.m_animatedModel != null) {
                        String string0 = "inside".equalsIgnoreCase(position.getId()) ? "player-vehicle" : "player-editor";
                        if (!string0.equals(sceneCharacter.m_animatedModel.GetAnimSetName())) {
                            sceneCharacter.m_animatedModel.setAnimSetName(string0);
                            sceneCharacter.m_animatedModel.getAdvancedAnimator().OnAnimDataChanged(false);
                            ActionGroup actionGroup = ActionGroup.getActionGroup(sceneCharacter.m_animatedModel.GetAnimSetName());
                            ActionContext actionContext = sceneCharacter.m_animatedModel.getActionContext();
                            if (actionGroup != actionContext.getGroup()) {
                                actionContext.setGroup(actionGroup);
                            }

                            sceneCharacter.m_animatedModel
                                .getAdvancedAnimator()
                                .SetState(
                                    actionContext.getCurrentStateName(),
                                    PZArrayUtil.listConvert(actionContext.getChildStates(), actionState -> actionState.name)
                                );
                        }
                    }
                }

                return null;
            default:
                throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\" \"%s\" \"%s\"", func, arg0, arg1, arg2));
        }
    }

    public Object fromLua6(String func, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        switch (func) {
            case "addAABB":
                float float0 = ((Double)arg0).floatValue();
                float float1 = ((Double)arg1).floatValue();
                float float2 = ((Double)arg2).floatValue();
                float float3 = ((Double)arg3).floatValue();
                float float4 = ((Double)arg4).floatValue();
                float float5 = ((Double)arg5).floatValue();
                this.m_aabb.add(s_aabbPool.alloc().set(float0, float1, float2, float3, float4, float5, 1.0F, 1.0F, 1.0F));
                return null;
            case "addAxis":
                float float6 = ((Double)arg0).floatValue();
                float float7 = ((Double)arg1).floatValue();
                float float8 = ((Double)arg2).floatValue();
                float float9 = ((Double)arg3).floatValue();
                float float10 = ((Double)arg4).floatValue();
                float float11 = ((Double)arg5).floatValue();
                this.m_axes.add(s_posRotPool.alloc().set(float6, float7, float8, float9, float10, float11));
                return null;
            case "addBox3D":
                Vector3f vector3f0 = (Vector3f)arg0;
                Vector3f vector3f1 = (Vector3f)arg1;
                Vector3f vector3f2 = (Vector3f)arg2;
                float float12 = ((Double)arg3).floatValue();
                float float13 = ((Double)arg4).floatValue();
                float float14 = ((Double)arg5).floatValue();
                this.m_box3D
                    .add(
                        s_box3DPool.alloc()
                            .set(
                                vector3f0.x,
                                vector3f0.y,
                                vector3f0.z,
                                vector3f1.x,
                                vector3f1.y,
                                vector3f1.z,
                                vector3f2.x,
                                vector3f2.y,
                                vector3f2.z,
                                float12,
                                float13,
                                float14
                            )
                    );
                return null;
            default:
                throw new IllegalArgumentException(
                    String.format("unhandled \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\"", func, arg0, arg1, arg2, arg3, arg4, arg5)
                );
        }
    }

    public Object fromLua9(String func, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8) {
        byte byte0 = -1;
        switch (func.hashCode()) {
            case -1149187967:
                if (func.equals("addAABB")) {
                    byte0 = 0;
                }
            default:
                switch (byte0) {
                    case 0:
                        float float0 = ((Double)arg0).floatValue();
                        float float1 = ((Double)arg1).floatValue();
                        float float2 = ((Double)arg2).floatValue();
                        float float3 = ((Double)arg3).floatValue();
                        float float4 = ((Double)arg4).floatValue();
                        float float5 = ((Double)arg5).floatValue();
                        float float6 = ((Double)arg6).floatValue();
                        float float7 = ((Double)arg7).floatValue();
                        float float8 = ((Double)arg8).floatValue();
                        this.m_aabb.add(s_aabbPool.alloc().set(float0, float1, float2, float3, float4, float5, float6, float7, float8));
                        return null;
                    default:
                        throw new IllegalArgumentException(
                            String.format(
                                "unhandled \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\"",
                                func,
                                arg0,
                                arg1,
                                arg2,
                                arg3,
                                arg4,
                                arg5,
                                arg6,
                                arg7,
                                arg8
                            )
                        );
                }
        }
    }

    private int screenWidth() {
        return (int)this.width;
    }

    private int screenHeight() {
        return (int)this.height;
    }

    public float uiToSceneX(float uiX, float uiY) {
        float float0 = uiX - this.screenWidth() / 2.0F;
        float0 += this.m_view_x;
        return float0 / this.zoomMult();
    }

    public float uiToSceneY(float uiX, float uiY) {
        float float0 = uiY - this.screenHeight() / 2.0F;
        float0 *= -1.0F;
        float0 -= this.m_view_y;
        return float0 / this.zoomMult();
    }

    public Vector3f uiToScene(float uiX, float uiY, float uiZ, Vector3f out) {
        this.uiToScene(null, uiX, uiY, uiZ, out);
        switch (this.m_view) {
            case Left:
            case Right:
                out.x = 0.0F;
                break;
            case Top:
            case Bottom:
                out.y = 0.0F;
                break;
            case Front:
            case Back:
                out.z = 0.0F;
        }

        return out;
    }

    public Vector3f uiToScene(Matrix4f modelTransform, float uiX, float uiY, float uiZ, Vector3f out) {
        uiY = this.screenHeight() - uiY;
        Matrix4f matrix4f = allocMatrix4f();
        matrix4f.set(this.m_projection);
        matrix4f.mul(this.m_modelView);
        if (modelTransform != null) {
            matrix4f.mul(modelTransform);
        }

        matrix4f.invert();
        this.m_viewport[2] = this.screenWidth();
        this.m_viewport[3] = this.screenHeight();
        matrix4f.unprojectInv(uiX, uiY, uiZ, this.m_viewport, out);
        releaseMatrix4f(matrix4f);
        return out;
    }

    public float sceneToUIX(float sceneX, float sceneY, float sceneZ) {
        this.tempVector4f.set(sceneX, sceneY, sceneZ, 1.0F);
        Matrix4f matrix4f = allocMatrix4f();
        matrix4f.set(this.m_projection);
        matrix4f.mul(this.m_modelView);
        this.m_viewport[2] = this.screenWidth();
        this.m_viewport[3] = this.screenHeight();
        matrix4f.project(sceneX, sceneY, sceneZ, this.m_viewport, this.tempVector3f);
        releaseMatrix4f(matrix4f);
        return this.tempVector3f.x();
    }

    public float sceneToUIY(float sceneX, float sceneY, float sceneZ) {
        this.tempVector4f.set(sceneX, sceneY, sceneZ, 1.0F);
        Matrix4f matrix4f = allocMatrix4f();
        matrix4f.set(this.m_projection);
        matrix4f.mul(this.m_modelView);
        int[] ints = new int[]{0, 0, this.screenWidth(), this.screenHeight()};
        matrix4f.project(sceneX, sceneY, sceneZ, ints, this.tempVector3f);
        releaseMatrix4f(matrix4f);
        return this.screenHeight() - this.tempVector3f.y();
    }

    private void renderGridXY(int int2) {
        for (int int0 = -5; int0 < 5; int0++) {
            for (int int1 = 1; int1 < int2; int1++) {
                vboLines.addLine(int0 + (float)int1 / int2, -5.0F, 0.0F, int0 + (float)int1 / int2, 5.0F, 0.0F, 0.2F, 0.2F, 0.2F, this.GRID_ALPHA);
            }
        }

        for (int int3 = -5; int3 < 5; int3++) {
            for (int int4 = 1; int4 < int2; int4++) {
                vboLines.addLine(-5.0F, int3 + (float)int4 / int2, 0.0F, 5.0F, int3 + (float)int4 / int2, 0.0F, 0.2F, 0.2F, 0.2F, this.GRID_ALPHA);
            }
        }

        for (int int5 = -5; int5 <= 5; int5++) {
            vboLines.addLine(int5, -5.0F, 0.0F, int5, 5.0F, 0.0F, 0.1F, 0.1F, 0.1F, this.GRID_ALPHA);
        }

        for (int int6 = -5; int6 <= 5; int6++) {
            vboLines.addLine(-5.0F, int6, 0.0F, 5.0F, int6, 0.0F, 0.1F, 0.1F, 0.1F, this.GRID_ALPHA);
        }

        if (this.m_bDrawGridAxes) {
            byte byte0 = 0;
            vboLines.addLine(-5.0F, 0.0F, byte0, 5.0F, 0.0F, byte0, 1.0F, 0.0F, 0.0F, this.GRID_ALPHA);
            byte0 = 0;
            vboLines.addLine(0.0F, -5.0F, byte0, 0.0F, 5.0F, byte0, 0.0F, 1.0F, 0.0F, this.GRID_ALPHA);
        }
    }

    private void renderGridXZ(int int2) {
        for (int int0 = -5; int0 < 5; int0++) {
            for (int int1 = 1; int1 < int2; int1++) {
                vboLines.addLine(int0 + (float)int1 / int2, 0.0F, -5.0F, int0 + (float)int1 / int2, 0.0F, 5.0F, 0.2F, 0.2F, 0.2F, this.GRID_ALPHA);
            }
        }

        for (int int3 = -5; int3 < 5; int3++) {
            for (int int4 = 1; int4 < int2; int4++) {
                vboLines.addLine(-5.0F, 0.0F, int3 + (float)int4 / int2, 5.0F, 0.0F, int3 + (float)int4 / int2, 0.2F, 0.2F, 0.2F, this.GRID_ALPHA);
            }
        }

        for (int int5 = -5; int5 <= 5; int5++) {
            vboLines.addLine(int5, 0.0F, -5.0F, int5, 0.0F, 5.0F, 0.1F, 0.1F, 0.1F, this.GRID_ALPHA);
        }

        for (int int6 = -5; int6 <= 5; int6++) {
            vboLines.addLine(-5.0F, 0.0F, int6, 5.0F, 0.0F, int6, 0.1F, 0.1F, 0.1F, this.GRID_ALPHA);
        }

        if (this.m_bDrawGridAxes) {
            byte byte0 = 0;
            vboLines.addLine(-5.0F, 0.0F, byte0, 5.0F, 0.0F, byte0, 1.0F, 0.0F, 0.0F, this.GRID_ALPHA);
            byte0 = 0;
            vboLines.addLine(byte0, 0.0F, -5.0F, byte0, 0.0F, 5.0F, 0.0F, 0.0F, 1.0F, this.GRID_ALPHA);
        }
    }

    private void renderGridYZ(int int2) {
        for (int int0 = -5; int0 < 5; int0++) {
            for (int int1 = 1; int1 < int2; int1++) {
                vboLines.addLine(0.0F, int0 + (float)int1 / int2, -5.0F, 0.0F, int0 + (float)int1 / int2, 5.0F, 0.2F, 0.2F, 0.2F, this.GRID_ALPHA);
            }
        }

        for (int int3 = -5; int3 < 5; int3++) {
            for (int int4 = 1; int4 < int2; int4++) {
                vboLines.addLine(0.0F, -5.0F, int3 + (float)int4 / int2, 0.0F, 5.0F, int3 + (float)int4 / int2, 0.2F, 0.2F, 0.2F, this.GRID_ALPHA);
            }
        }

        for (int int5 = -5; int5 <= 5; int5++) {
            vboLines.addLine(0.0F, int5, -5.0F, 0.0F, int5, 5.0F, 0.1F, 0.1F, 0.1F, this.GRID_ALPHA);
        }

        for (int int6 = -5; int6 <= 5; int6++) {
            vboLines.addLine(0.0F, -5.0F, int6, 0.0F, 5.0F, int6, 0.1F, 0.1F, 0.1F, this.GRID_ALPHA);
        }

        if (this.m_bDrawGridAxes) {
            byte byte0 = 0;
            vboLines.addLine(0.0F, -5.0F, byte0, 0.0F, 5.0F, byte0, 0.0F, 1.0F, 0.0F, this.GRID_ALPHA);
            byte0 = 0;
            vboLines.addLine(byte0, 0.0F, -5.0F, byte0, 0.0F, 5.0F, 0.0F, 0.0F, 1.0F, this.GRID_ALPHA);
        }
    }

    private void renderGrid() {
        vboLines.setLineWidth(1.0F);
        this.GRID_ALPHA = 1.0F;
        long long0 = System.currentTimeMillis();
        if (this.m_viewChangeTime + this.VIEW_CHANGE_TIME > long0) {
            float float0 = (float)(this.m_viewChangeTime + this.VIEW_CHANGE_TIME - long0) / (float)this.VIEW_CHANGE_TIME;
            this.GRID_ALPHA = 1.0F - float0;
            this.GRID_ALPHA = this.GRID_ALPHA * this.GRID_ALPHA;
        }

        switch (this.m_view) {
            case Left:
            case Right:
                this.renderGridYZ(10);
                return;
            case Top:
            case Bottom:
                this.renderGridXZ(10);
                return;
            case Front:
            case Back:
                this.renderGridXY(10);
                return;
            default:
                switch (this.m_gridPlane) {
                    case XY:
                        this.renderGridXY(10);
                        break;
                    case XZ:
                        this.renderGridXZ(10);
                        break;
                    case YZ:
                        this.renderGridYZ(10);
                }
        }
    }

    void renderAxis(UI3DScene.PositionRotation positionRotation) {
        this.renderAxis(positionRotation.pos, positionRotation.rot);
    }

    void renderAxis(Vector3f vector3f0, Vector3f vector3f1) {
        UI3DScene.StateData stateData = this.stateDataRender();
        vboLines.flush();
        Matrix4f matrix4f = allocMatrix4f();
        matrix4f.set(stateData.m_gizmoParentTransform);
        matrix4f.mul(stateData.m_gizmoOriginTransform);
        matrix4f.mul(stateData.m_gizmoChildTransform);
        matrix4f.translate(vector3f0);
        matrix4f.rotateXYZ(vector3f1.x * (float) (Math.PI / 180.0), vector3f1.y * (float) (Math.PI / 180.0), vector3f1.z * (float) (Math.PI / 180.0));
        stateData.m_modelView.mul(matrix4f, matrix4f);
        PZGLUtil.pushAndLoadMatrix(5888, matrix4f);
        releaseMatrix4f(matrix4f);
        float float0 = 0.1F;
        vboLines.setLineWidth(3.0F);
        vboLines.addLine(0.0F, 0.0F, 0.0F, float0, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F);
        vboLines.addLine(0.0F, 0.0F, 0.0F, 0.0F, 0.0F + float0, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F);
        vboLines.addLine(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F + float0, 0.0F, 0.0F, 1.0F, 1.0F);
        vboLines.flush();
        PZGLUtil.popMatrix(5888);
    }

    private void renderAABB(float float6, float float7, float float8, float float1, float float3, float float5, float float10, float float11, float float12) {
        float float0 = float1 / 2.0F;
        float float2 = float3 / 2.0F;
        float float4 = float5 / 2.0F;
        vboLines.setOffset(float6, float7, float8);
        vboLines.setLineWidth(1.0F);
        float float9 = 1.0F;
        vboLines.addLine(float0, float2, float4, -float0, float2, float4, float10, float11, float12, float9);
        vboLines.addLine(float0, float2, float4, float0, -float2, float4, float10, float11, float12, float9);
        vboLines.addLine(float0, float2, float4, float0, float2, -float4, float10, float11, float12, float9);
        vboLines.addLine(-float0, float2, float4, -float0, -float2, float4, float10, float11, float12, float9);
        vboLines.addLine(-float0, float2, float4, -float0, float2, -float4, float10, float11, float12, float9);
        vboLines.addLine(float0, float2, -float4, float0, -float2, -float4, float10, float11, float12, float9);
        vboLines.addLine(float0, float2, -float4, -float0, float2, -float4, float10, float11, float12, float9);
        vboLines.addLine(-float0, float2, -float4, -float0, -float2, -float4, float10, float11, float12, float9);
        vboLines.addLine(float0, -float2, -float4, -float0, -float2, -float4, float10, float11, float12, float9);
        vboLines.addLine(float0, -float2, float4, float0, -float2, -float4, float10, float11, float12, float9);
        vboLines.addLine(-float0, -float2, float4, -float0, -float2, -float4, float10, float11, float12, float9);
        vboLines.addLine(float0, -float2, float4, -float0, -float2, float4, float10, float11, float12, float9);
        vboLines.setOffset(0.0F, 0.0F, 0.0F);
    }

    private void renderAABB(float float0, float float1, float float2, Vector3f vector3f1, Vector3f vector3f0, float float4, float float5, float float6) {
        vboLines.setOffset(float0, float1, float2);
        vboLines.setLineWidth(1.0F);
        float float3 = 1.0F;
        vboLines.addLine(vector3f0.x, vector3f0.y, vector3f0.z, vector3f1.x, vector3f0.y, vector3f0.z, float4, float5, float6, float3);
        vboLines.addLine(vector3f0.x, vector3f0.y, vector3f0.z, vector3f0.x, vector3f1.y, vector3f0.z, float4, float5, float6, float3);
        vboLines.addLine(vector3f0.x, vector3f0.y, vector3f0.z, vector3f0.x, vector3f0.y, vector3f1.z, float4, float5, float6, float3);
        vboLines.addLine(vector3f1.x, vector3f0.y, vector3f0.z, vector3f1.x, vector3f1.y, vector3f0.z, float4, float5, float6, float3);
        vboLines.addLine(vector3f1.x, vector3f0.y, vector3f0.z, vector3f1.x, vector3f0.y, vector3f1.z, float4, float5, float6, float3);
        vboLines.addLine(vector3f0.x, vector3f0.y, vector3f1.z, vector3f0.x, vector3f1.y, vector3f1.z, float4, float5, float6, float3);
        vboLines.addLine(vector3f0.x, vector3f0.y, vector3f1.z, vector3f1.x, vector3f0.y, vector3f1.z, float4, float5, float6, float3);
        vboLines.addLine(vector3f1.x, vector3f0.y, vector3f1.z, vector3f1.x, vector3f1.y, vector3f1.z, float4, float5, float6, float3);
        vboLines.addLine(vector3f0.x, vector3f1.y, vector3f1.z, vector3f1.x, vector3f1.y, vector3f1.z, float4, float5, float6, float3);
        vboLines.addLine(vector3f0.x, vector3f1.y, vector3f0.z, vector3f0.x, vector3f1.y, vector3f1.z, float4, float5, float6, float3);
        vboLines.addLine(vector3f1.x, vector3f1.y, vector3f0.z, vector3f1.x, vector3f1.y, vector3f1.z, float4, float5, float6, float3);
        vboLines.addLine(vector3f0.x, vector3f1.y, vector3f0.z, vector3f1.x, vector3f1.y, vector3f0.z, float4, float5, float6, float3);
        vboLines.setOffset(0.0F, 0.0F, 0.0F);
    }

    private void renderBox3D(
        float float0,
        float float1,
        float float2,
        float float6,
        float float7,
        float float8,
        float float5,
        float float4,
        float float3,
        float float9,
        float float10,
        float float11
    ) {
        UI3DScene.StateData stateData = this.stateDataRender();
        vboLines.flush();
        Matrix4f matrix4f = allocMatrix4f();
        matrix4f.identity();
        matrix4f.translate(float0, float1, float2);
        matrix4f.rotateXYZ(float5 * (float) (Math.PI / 180.0), float4 * (float) (Math.PI / 180.0), float3 * (float) (Math.PI / 180.0));
        stateData.m_modelView.mul(matrix4f, matrix4f);
        PZGLUtil.pushAndLoadMatrix(5888, matrix4f);
        releaseMatrix4f(matrix4f);
        this.renderAABB(float0 * 0.0F, float1 * 0.0F, float2 * 0.0F, float6, float7, float8, float9, float10, float11);
        vboLines.flush();
        PZGLUtil.popMatrix(5888);
    }

    private void calcMatrices(Matrix4f matrix4f0, Matrix4f matrix4f1) {
        float float0 = this.screenWidth();
        float float1 = 1366.0F / float0;
        float float2 = this.screenHeight() * float1;
        float0 = 1366.0F;
        float0 /= this.zoomMult();
        float2 /= this.zoomMult();
        matrix4f0.setOrtho(-float0 / 2.0F, float0 / 2.0F, -float2 / 2.0F, float2 / 2.0F, -10.0F, 10.0F);
        float float3 = this.m_view_x / this.zoomMult() * float1;
        float float4 = this.m_view_y / this.zoomMult() * float1;
        matrix4f0.translate(-float3, float4, 0.0F);
        matrix4f1.identity();
        float float5 = 0.0F;
        float float6 = 0.0F;
        float float7 = 0.0F;
        switch (this.m_view) {
            case Left:
                float6 = 270.0F;
                break;
            case Right:
                float6 = 90.0F;
                break;
            case Top:
                float6 = 90.0F;
                float7 = 90.0F;
                break;
            case Bottom:
                float6 = 90.0F;
                float7 = 270.0F;
            case Front:
            default:
                break;
            case Back:
                float6 = 180.0F;
                break;
            case UserDefined:
                float5 = this.m_viewRotation.x;
                float6 = this.m_viewRotation.y;
                float7 = this.m_viewRotation.z;
        }

        matrix4f1.rotateXYZ(float5 * (float) (Math.PI / 180.0), float6 * (float) (Math.PI / 180.0), float7 * (float) (Math.PI / 180.0));
    }

    UI3DScene.Ray getCameraRay(float float0, float float1, UI3DScene.Ray ray) {
        return this.getCameraRay(float0, float1, this.m_projection, this.m_modelView, ray);
    }

    UI3DScene.Ray getCameraRay(float float0, float float1, Matrix4f matrix4f1, Matrix4f matrix4f2, UI3DScene.Ray ray) {
        Matrix4f matrix4f0 = allocMatrix4f();
        matrix4f0.set(matrix4f1);
        matrix4f0.mul(matrix4f2);
        matrix4f0.invert();
        this.m_viewport[2] = this.screenWidth();
        this.m_viewport[3] = this.screenHeight();
        Vector3f vector3f0 = matrix4f0.unprojectInv(float0, float1, 0.0F, this.m_viewport, allocVector3f());
        Vector3f vector3f1 = matrix4f0.unprojectInv(float0, float1, 1.0F, this.m_viewport, allocVector3f());
        ray.origin.set(vector3f0);
        ray.direction.set(vector3f1.sub(vector3f0).normalize());
        releaseVector3f(vector3f1);
        releaseVector3f(vector3f0);
        releaseMatrix4f(matrix4f0);
        return ray;
    }

    float closest_distance_between_lines(UI3DScene.Ray ray0, UI3DScene.Ray ray1) {
        Vector3f vector3f0 = allocVector3f().set(ray0.direction);
        Vector3f vector3f1 = allocVector3f().set(ray1.direction);
        Vector3f vector3f2 = allocVector3f().set(ray0.origin).sub(ray1.origin);
        float float0 = vector3f0.dot(vector3f0);
        float float1 = vector3f0.dot(vector3f1);
        float float2 = vector3f1.dot(vector3f1);
        float float3 = vector3f0.dot(vector3f2);
        float float4 = vector3f1.dot(vector3f2);
        float float5 = float0 * float2 - float1 * float1;
        float float6;
        float float7;
        if (float5 < 1.0E-8F) {
            float6 = 0.0F;
            float7 = float1 > float2 ? float3 / float1 : float4 / float2;
        } else {
            float6 = (float1 * float4 - float2 * float3) / float5;
            float7 = (float0 * float4 - float1 * float3) / float5;
        }

        Vector3f vector3f3 = vector3f2.add(vector3f0.mul(float6)).sub(vector3f1.mul(float7));
        ray0.t = float6;
        ray1.t = float7;
        releaseVector3f(vector3f0);
        releaseVector3f(vector3f1);
        releaseVector3f(vector3f2);
        return vector3f3.length();
    }

    Vector3f project(Vector3f vector3f1, Vector3f vector3f0, Vector3f vector3f2) {
        return vector3f2.set(vector3f0).mul(vector3f1.dot(vector3f0) / vector3f0.dot(vector3f0));
    }

    Vector3f reject(Vector3f vector3f1, Vector3f vector3f2, Vector3f vector3f3) {
        Vector3f vector3f0 = this.project(vector3f1, vector3f2, allocVector3f());
        vector3f3.set(vector3f1).sub(vector3f0);
        releaseVector3f(vector3f0);
        return vector3f3;
    }

    public static int intersect_ray_plane(UI3DScene.Plane Pn, UI3DScene.Ray S, Vector3f out) {
        Vector3f vector3f0 = allocVector3f().set(S.direction).mul(100.0F);
        Vector3f vector3f1 = allocVector3f().set(S.origin).sub(Pn.point);

        byte byte0;
        try {
            float float0 = Pn.normal.dot(vector3f0);
            float float1 = -Pn.normal.dot(vector3f1);
            if (!(Math.abs(float0) < 1.0E-8F)) {
                float float2 = float1 / float0;
                if (!(float2 < 0.0F) && !(float2 > 1.0F)) {
                    out.set(S.origin).add(vector3f0.mul(float2));
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

    float distance_between_point_ray(Vector3f vector3f2, UI3DScene.Ray ray) {
        Vector3f vector3f0 = allocVector3f().set(ray.direction).mul(100.0F);
        Vector3f vector3f1 = allocVector3f().set(vector3f2).sub(ray.origin);
        float float0 = vector3f1.dot(vector3f0);
        float float1 = vector3f0.dot(vector3f0);
        float float2 = float0 / float1;
        Vector3f vector3f3 = vector3f0.mul(float2).add(ray.origin);
        float float3 = vector3f3.sub(vector3f2).length();
        releaseVector3f(vector3f1);
        releaseVector3f(vector3f0);
        return float3;
    }

    float closest_distance_line_circle(UI3DScene.Ray ray, UI3DScene.Circle circle, Vector3f vector3f1) {
        UI3DScene.Plane plane = allocPlane().set(circle.orientation, circle.center);
        Vector3f vector3f0 = allocVector3f();
        float float0;
        if (intersect_ray_plane(plane, ray, vector3f0) == 1) {
            vector3f1.set(vector3f0).sub(circle.center).normalize().mul(circle.radius).add(circle.center);
            float0 = vector3f0.sub(vector3f1).length();
        } else {
            Vector3f vector3f2 = allocVector3f().set(ray.origin).sub(circle.center);
            Vector3f vector3f3 = this.reject(vector3f2, circle.orientation, allocVector3f());
            vector3f1.set(vector3f3.normalize().mul(circle.radius).add(circle.center));
            float0 = this.distance_between_point_ray(vector3f1, ray);
            releaseVector3f(vector3f3);
            releaseVector3f(vector3f2);
        }

        releaseVector3f(vector3f0);
        releasePlane(plane);
        return float0;
    }

    private UI3DScene.StateData stateDataMain() {
        return this.m_stateData[SpriteRenderer.instance.getMainStateIndex()];
    }

    private UI3DScene.StateData stateDataRender() {
        return this.m_stateData[SpriteRenderer.instance.getRenderStateIndex()];
    }

    private static final class AABB {
        float x;
        float y;
        float z;
        float w;
        float h;
        float L;
        float r;
        float g;
        float b;

        UI3DScene.AABB set(UI3DScene.AABB aabb1) {
            return this.set(aabb1.x, aabb1.y, aabb1.z, aabb1.w, aabb1.h, aabb1.L, aabb1.r, aabb1.g, aabb1.b);
        }

        UI3DScene.AABB set(float float0, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
            this.x = float0;
            this.y = float1;
            this.z = float2;
            this.w = float3;
            this.h = float4;
            this.L = float5;
            this.r = float6;
            this.g = float7;
            this.b = float8;
            return this;
        }
    }

    static enum Axis {
        None,
        X,
        Y,
        Z;
    }

    private static final class Box3D {
        float x;
        float y;
        float z;
        float w;
        float h;
        float L;
        float rx;
        float ry;
        float rz;
        float r;
        float g;
        float b;

        UI3DScene.Box3D set(UI3DScene.Box3D box3D1) {
            return this.set(box3D1.x, box3D1.y, box3D1.z, box3D1.w, box3D1.h, box3D1.L, box3D1.rx, box3D1.ry, box3D1.rz, box3D1.r, box3D1.g, box3D1.b);
        }

        UI3DScene.Box3D set(
            float float0,
            float float1,
            float float2,
            float float3,
            float float4,
            float float5,
            float float6,
            float float7,
            float float8,
            float float9,
            float float10,
            float float11
        ) {
            this.x = float0;
            this.y = float1;
            this.z = float2;
            this.w = float3;
            this.h = float4;
            this.L = float5;
            this.rx = float6;
            this.ry = float7;
            this.rz = float8;
            this.r = float9;
            this.g = float10;
            this.b = float11;
            return this;
        }
    }

    private static final class CharacterDrawer extends TextureDraw.GenericDrawer {
        UI3DScene.SceneCharacter m_character;
        UI3DScene.CharacterRenderData m_renderData;
        boolean bRendered;

        public void init(UI3DScene.SceneCharacter sceneCharacter, UI3DScene.CharacterRenderData characterRenderData) {
            this.m_character = sceneCharacter;
            this.m_renderData = characterRenderData;
            this.bRendered = false;
            this.m_character.m_animatedModel.renderMain();
        }

        @Override
        public void render() {
            if (this.m_character.m_bClearDepthBuffer) {
                GL11.glClear(256);
            }

            boolean boolean0 = DebugOptions.instance.ModelRenderBones.getValue();
            DebugOptions.instance.ModelRenderBones.setValue(this.m_character.m_bShowBones);
            this.m_character.m_scene.m_CharacterSceneModelCamera.m_renderData = this.m_renderData;
            this.m_character.m_animatedModel.DoRender(this.m_character.m_scene.m_CharacterSceneModelCamera);
            DebugOptions.instance.ModelRenderBones.setValue(boolean0);
            this.bRendered = true;
            GL11.glDepthMask(true);
        }

        @Override
        public void postRender() {
            this.m_character.m_animatedModel.postRender(this.bRendered);
        }
    }

    private static class CharacterRenderData extends UI3DScene.SceneObjectRenderData {
        final UI3DScene.CharacterDrawer m_drawer = new UI3DScene.CharacterDrawer();
        private static final ObjectPool<UI3DScene.CharacterRenderData> s_pool = new ObjectPool<>(UI3DScene.CharacterRenderData::new);

        UI3DScene.SceneObjectRenderData initCharacter(UI3DScene.SceneCharacter sceneCharacter) {
            this.m_drawer.init(sceneCharacter, this);
            super.init(sceneCharacter);
            return this;
        }

        @Override
        void release() {
            s_pool.release(this);
        }
    }

    private final class CharacterSceneModelCamera extends UI3DScene.SceneModelCamera {
        @Override
        public void Begin() {
            UI3DScene.StateData stateData = UI3DScene.this.stateDataRender();
            GL11.glViewport(
                UI3DScene.this.getAbsoluteX().intValue(),
                Core.getInstance().getScreenHeight() - UI3DScene.this.getAbsoluteY().intValue() - UI3DScene.this.getHeight().intValue(),
                UI3DScene.this.getWidth().intValue(),
                UI3DScene.this.getHeight().intValue()
            );
            PZGLUtil.pushAndLoadMatrix(5889, stateData.m_projection);
            Matrix4f matrix4f = UI3DScene.allocMatrix4f();
            matrix4f.set(stateData.m_modelView);
            matrix4f.mul(this.m_renderData.m_transform);
            PZGLUtil.pushAndLoadMatrix(5888, matrix4f);
            UI3DScene.releaseMatrix4f(matrix4f);
        }

        @Override
        public void End() {
            PZGLUtil.popMatrix(5889);
            PZGLUtil.popMatrix(5888);
        }
    }

    private static final class Circle {
        final Vector3f center = new Vector3f();
        final Vector3f orientation = new Vector3f();
        float radius = 1.0F;
    }

    private abstract class Gizmo {
        float LENGTH = 0.5F;
        float THICKNESS = 0.05F;
        boolean m_visible = false;

        abstract UI3DScene.Axis hitTest(float var1, float var2);

        abstract void startTracking(float var1, float var2, UI3DScene.Axis var3);

        abstract void updateTracking(float var1, float var2);

        abstract void stopTracking();

        abstract void render();

        Vector3f getPointOnAxis(float float1, float float0, UI3DScene.Axis axis, Matrix4f matrix4f, Vector3f vector3f) {
            UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
            float0 = UI3DScene.this.screenHeight() - float0;
            UI3DScene.Ray ray0 = UI3DScene.this.getCameraRay(float1, float0, UI3DScene.allocRay());
            UI3DScene.Ray ray1 = UI3DScene.allocRay();
            matrix4f.transformPosition(ray1.origin.set(0.0F, 0.0F, 0.0F));
            switch (axis) {
                case X:
                    ray1.direction.set(1.0F, 0.0F, 0.0F);
                    break;
                case Y:
                    ray1.direction.set(0.0F, 1.0F, 0.0F);
                    break;
                case Z:
                    ray1.direction.set(0.0F, 0.0F, 1.0F);
            }

            matrix4f.transformDirection(ray1.direction).normalize();
            UI3DScene.this.closest_distance_between_lines(ray1, ray0);
            UI3DScene.releaseRay(ray0);
            vector3f.set(ray1.direction).mul(ray1.t).add(ray1.origin);
            UI3DScene.releaseRay(ray1);
            return vector3f;
        }

        boolean hitTestRect(float float17, float float16, float float1, float float2, float float3, float float6, float float7, float float8) {
            float float0 = UI3DScene.this.sceneToUIX(float1, float2, float3);
            float float4 = UI3DScene.this.sceneToUIY(float1, float2, float3);
            float float5 = UI3DScene.this.sceneToUIX(float6, float7, float8);
            float float9 = UI3DScene.this.sceneToUIY(float6, float7, float8);
            float float10 = this.THICKNESS / 2.0F * UI3DScene.this.zoomMult();
            float float11 = this.THICKNESS / 2.0F * UI3DScene.this.zoomMult();
            float float12 = Math.min(float0 - float10, float5 - float10);
            float float13 = Math.max(float0 + float10, float5 + float10);
            float float14 = Math.min(float4 - float11, float9 - float11);
            float float15 = Math.max(float4 + float11, float9 + float11);
            return float17 >= float12 && float16 >= float14 && float17 < float13 && float16 < float15;
        }

        void renderLineToOrigin() {
            UI3DScene.StateData stateData = UI3DScene.this.stateDataRender();
            if (stateData.m_hasGizmoOrigin) {
                UI3DScene.this.renderAxis(stateData.m_gizmoTranslate, stateData.m_gizmoRotate);
                Vector3f vector3f = stateData.m_gizmoTranslate;
                UI3DScene.vboLines.flush();
                Matrix4f matrix4f = UI3DScene.allocMatrix4f();
                matrix4f.set(stateData.m_modelView);
                matrix4f.mul(stateData.m_gizmoParentTransform);
                matrix4f.mul(stateData.m_gizmoOriginTransform);
                matrix4f.mul(stateData.m_gizmoChildTransform);
                PZGLUtil.pushAndLoadMatrix(5888, matrix4f);
                UI3DScene.releaseMatrix4f(matrix4f);
                UI3DScene.vboLines.setLineWidth(1.0F);
                UI3DScene.vboLines.addLine(vector3f.x, vector3f.y, vector3f.z, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
                UI3DScene.vboLines.flush();
                PZGLUtil.popMatrix(5888);
            }
        }
    }

    private static enum GridPlane {
        XY,
        XZ,
        YZ;
    }

    private final class GridPlaneDrawer extends TextureDraw.GenericDrawer {
        final UI3DScene m_scene;

        GridPlaneDrawer(UI3DScene uI3DScene1) {
            this.m_scene = uI3DScene1;
        }

        @Override
        public void render() {
            UI3DScene.StateData stateData = UI3DScene.this.stateDataRender();
            PZGLUtil.pushAndLoadMatrix(5889, stateData.m_projection);
            PZGLUtil.pushAndLoadMatrix(5888, stateData.m_modelView);
            GL11.glPushAttrib(2048);
            GL11.glViewport(
                UI3DScene.this.getAbsoluteX().intValue(),
                Core.getInstance().getScreenHeight() - UI3DScene.this.getAbsoluteY().intValue() - UI3DScene.this.getHeight().intValue(),
                UI3DScene.this.getWidth().intValue(),
                UI3DScene.this.getHeight().intValue()
            );
            float float0 = 5.0F;
            UI3DScene.vboLines.setMode(4);
            UI3DScene.vboLines.setDepthTest(true);
            if (this.m_scene.m_gridPlane == UI3DScene.GridPlane.XZ) {
                UI3DScene.vboLines.addTriangle(-float0, 0.0F, -float0, float0, 0.0F, -float0, -float0, 0.0F, float0, 0.5F, 0.5F, 0.5F, 1.0F);
                UI3DScene.vboLines.addTriangle(float0, 0.0F, float0, -float0, 0.0F, float0, float0, 0.0F, -float0, 0.5F, 0.5F, 0.5F, 1.0F);
            }

            UI3DScene.vboLines.setMode(1);
            UI3DScene.vboLines.setDepthTest(false);
            GL11.glPopAttrib();
            PZGLUtil.popMatrix(5889);
            PZGLUtil.popMatrix(5888);
        }
    }

    private static final class ModelDrawer extends TextureDraw.GenericDrawer {
        UI3DScene.SceneModel m_model;
        UI3DScene.ModelRenderData m_renderData;
        boolean bRendered;

        public void init(UI3DScene.SceneModel sceneModel, UI3DScene.ModelRenderData modelRenderData) {
            this.m_model = sceneModel;
            this.m_renderData = modelRenderData;
            this.bRendered = false;
        }

        @Override
        public void render() {
            UI3DScene.StateData stateData = this.m_model.m_scene.stateDataRender();
            PZGLUtil.pushAndLoadMatrix(5889, stateData.m_projection);
            PZGLUtil.pushAndLoadMatrix(5888, stateData.m_modelView);
            Model model = this.m_model.m_model;
            Shader shader = model.Effect;
            if (shader != null && model.Mesh != null && model.Mesh.isReady()) {
                GL11.glPushAttrib(1048575);
                GL11.glPushClientAttrib(-1);
                UI3DScene uI3DScene = this.m_renderData.m_object.m_scene;
                GL11.glViewport(
                    uI3DScene.getAbsoluteX().intValue(),
                    Core.getInstance().getScreenHeight() - uI3DScene.getAbsoluteY().intValue() - uI3DScene.getHeight().intValue(),
                    uI3DScene.getWidth().intValue(),
                    uI3DScene.getHeight().intValue()
                );
                GL11.glDepthFunc(513);
                GL11.glDepthMask(true);
                GL11.glDepthRange(0.0, 1.0);
                GL11.glEnable(2929);
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
                shader.Start();
                if (model.tex != null) {
                    shader.setTexture(model.tex, "Texture", 0);
                }

                shader.setDepthBias(0.0F);
                shader.setAmbient(1.0F);
                shader.setLightingAmount(1.0F);
                shader.setHueShift(0.0F);
                shader.setTint(1.0F, 1.0F, 1.0F);
                shader.setAlpha(1.0F);

                for (int int0 = 0; int0 < 5; int0++) {
                    shader.setLight(int0, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Float.NaN, 0.0F, 0.0F, 0.0F, null);
                }

                shader.setTransformMatrix(this.m_renderData.m_transform, false);
                model.Mesh.Draw(shader);
                shader.End();
                if (Core.bDebug) {
                }

                GL11.glPopAttrib();
                GL11.glPopClientAttrib();
                Texture.lastTextureID = -1;
                SpriteRenderer.ringBuffer.restoreBoundTextures = true;
                SpriteRenderer.ringBuffer.restoreVBOs = true;
            }

            PZGLUtil.popMatrix(5889);
            PZGLUtil.popMatrix(5888);
            this.bRendered = true;
        }

        @Override
        public void postRender() {
        }
    }

    private static class ModelRenderData extends UI3DScene.SceneObjectRenderData {
        final UI3DScene.ModelDrawer m_drawer = new UI3DScene.ModelDrawer();
        private static final ObjectPool<UI3DScene.ModelRenderData> s_pool = new ObjectPool<>(UI3DScene.ModelRenderData::new);

        UI3DScene.SceneObjectRenderData initModel(UI3DScene.SceneModel sceneModel) {
            super.init(sceneModel);
            if (sceneModel.m_useWorldAttachment) {
                if (sceneModel.m_weaponRotationHack) {
                    this.m_transform.rotateXYZ(0.0F, (float) Math.PI, (float) (Math.PI / 2));
                }

                if (sceneModel.m_modelScript != null) {
                    ModelAttachment modelAttachment = sceneModel.m_modelScript.getAttachmentById("world");
                    if (modelAttachment != null) {
                        Matrix4f matrix4f = ModelInstanceRenderData.makeAttachmentTransform(modelAttachment, UI3DScene.allocMatrix4f());
                        matrix4f.invert();
                        this.m_transform.mul(matrix4f);
                        UI3DScene.releaseMatrix4f(matrix4f);
                    }
                }
            }

            if (sceneModel.m_model.isReady() && sceneModel.m_model.Mesh.m_transform != null) {
                sceneModel.m_model.Mesh.m_transform.transpose();
                this.m_transform.mul(sceneModel.m_model.Mesh.m_transform);
                sceneModel.m_model.Mesh.m_transform.transpose();
            }

            if (sceneModel.m_modelScript != null && sceneModel.m_modelScript.scale != 1.0F) {
                this.m_transform.scale(sceneModel.m_modelScript.scale);
            }

            this.m_drawer.init(sceneModel, this);
            return this;
        }

        @Override
        void release() {
            s_pool.release(this);
        }
    }

    private static final class OriginAttachment extends UI3DScene.SceneObject {
        UI3DScene.SceneObject m_object;
        String m_attachmentName;

        OriginAttachment(UI3DScene uI3DScene) {
            super(uI3DScene, "OriginAttachment");
        }

        @Override
        UI3DScene.SceneObjectRenderData renderMain() {
            return null;
        }

        @Override
        Matrix4f getGlobalTransform(Matrix4f matrix4f) {
            return this.m_object.getAttachmentTransform(this.m_attachmentName, matrix4f);
        }
    }

    private static final class OriginBone extends UI3DScene.SceneObject {
        UI3DScene.SceneCharacter m_character;
        String m_boneName;

        OriginBone(UI3DScene uI3DScene) {
            super(uI3DScene, "OriginBone");
        }

        @Override
        UI3DScene.SceneObjectRenderData renderMain() {
            return null;
        }

        @Override
        Matrix4f getGlobalTransform(Matrix4f matrix4f) {
            return this.m_character.getBoneMatrix(this.m_boneName, matrix4f);
        }
    }

    private static final class OriginGizmo extends UI3DScene.SceneObject {
        OriginGizmo(UI3DScene uI3DScene) {
            super(uI3DScene, "OriginGizmo");
        }

        @Override
        UI3DScene.SceneObjectRenderData renderMain() {
            return null;
        }
    }

    private final class OverlaysDrawer extends TextureDraw.GenericDrawer {
        void init() {
            UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
            UI3DScene.s_aabbPool.release(stateData.m_aabb);
            stateData.m_aabb.clear();

            for (int int0 = 0; int0 < UI3DScene.this.m_aabb.size(); int0++) {
                UI3DScene.AABB aabb = UI3DScene.this.m_aabb.get(int0);
                stateData.m_aabb.add(UI3DScene.s_aabbPool.alloc().set(aabb));
            }

            UI3DScene.s_box3DPool.release(stateData.m_box3D);
            stateData.m_box3D.clear();

            for (int int1 = 0; int1 < UI3DScene.this.m_box3D.size(); int1++) {
                UI3DScene.Box3D box3D = UI3DScene.this.m_box3D.get(int1);
                stateData.m_box3D.add(UI3DScene.s_box3DPool.alloc().set(box3D));
            }

            UI3DScene.s_posRotPool.release(stateData.m_axes);
            stateData.m_axes.clear();

            for (int int2 = 0; int2 < UI3DScene.this.m_axes.size(); int2++) {
                UI3DScene.PositionRotation positionRotation = UI3DScene.this.m_axes.get(int2);
                stateData.m_axes.add(UI3DScene.s_posRotPool.alloc().set(positionRotation));
            }
        }

        @Override
        public void render() {
            UI3DScene.StateData stateData = UI3DScene.this.stateDataRender();
            PZGLUtil.pushAndLoadMatrix(5889, stateData.m_projection);
            PZGLUtil.pushAndLoadMatrix(5888, stateData.m_modelView);
            GL11.glPushAttrib(2048);
            GL11.glViewport(
                UI3DScene.this.getAbsoluteX().intValue(),
                Core.getInstance().getScreenHeight() - UI3DScene.this.getAbsoluteY().intValue() - UI3DScene.this.getHeight().intValue(),
                UI3DScene.this.getWidth().intValue(),
                UI3DScene.this.getHeight().intValue()
            );
            UI3DScene.vboLines.setOffset(0.0F, 0.0F, 0.0F);
            if (UI3DScene.this.m_bDrawGrid) {
                UI3DScene.this.renderGrid();
            }

            for (int int0 = 0; int0 < stateData.m_aabb.size(); int0++) {
                UI3DScene.AABB aabb = stateData.m_aabb.get(int0);
                UI3DScene.this.renderAABB(aabb.x, aabb.y, aabb.z, aabb.w, aabb.h, aabb.L, aabb.r, aabb.g, aabb.b);
            }

            for (int int1 = 0; int1 < stateData.m_box3D.size(); int1++) {
                UI3DScene.Box3D box3D = stateData.m_box3D.get(int1);
                UI3DScene.this.renderBox3D(box3D.x, box3D.y, box3D.z, box3D.w, box3D.h, box3D.L, box3D.rx, box3D.ry, box3D.rz, box3D.r, box3D.g, box3D.b);
            }

            for (int int2 = 0; int2 < stateData.m_axes.size(); int2++) {
                UI3DScene.this.renderAxis(stateData.m_axes.get(int2));
            }

            UI3DScene.vboLines.flush();
            if (stateData.m_gizmo != null) {
                stateData.m_gizmo.render();
            }

            UI3DScene.vboLines.flush();
            GL11.glPopAttrib();
            PZGLUtil.popMatrix(5889);
            PZGLUtil.popMatrix(5888);
        }
    }

    public static final class Plane {
        public final Vector3f point = new Vector3f();
        public final Vector3f normal = new Vector3f();

        public Plane() {
        }

        public Plane(Vector3f _normal, Vector3f _point) {
            this.point.set(_point);
            this.normal.set(_normal);
        }

        public UI3DScene.Plane set(Vector3f _normal, Vector3f _point) {
            this.point.set(_point);
            this.normal.set(_normal);
            return this;
        }
    }

    public static final class PlaneObjectPool extends ObjectPool<UI3DScene.Plane> {
        int allocated = 0;

        public PlaneObjectPool() {
            super(UI3DScene.Plane::new);
        }

        protected UI3DScene.Plane makeObject() {
            this.allocated++;
            return (UI3DScene.Plane)super.makeObject();
        }
    }

    private static final class PositionRotation {
        final Vector3f pos = new Vector3f();
        final Vector3f rot = new Vector3f();

        UI3DScene.PositionRotation set(UI3DScene.PositionRotation positionRotation0) {
            this.pos.set(positionRotation0.pos);
            this.rot.set(positionRotation0.rot);
            return this;
        }

        UI3DScene.PositionRotation set(float float0, float float1, float float2) {
            this.pos.set(float0, float1, float2);
            this.rot.set(0.0F, 0.0F, 0.0F);
            return this;
        }

        UI3DScene.PositionRotation set(float float0, float float1, float float2, float float3, float float4, float float5) {
            this.pos.set(float0, float1, float2);
            this.rot.set(float3, float4, float5);
            return this;
        }
    }

    public static final class Ray {
        public final Vector3f origin = new Vector3f();
        public final Vector3f direction = new Vector3f();
        public float t;

        public Ray() {
        }

        Ray(UI3DScene.Ray ray1) {
            this.origin.set(ray1.origin);
            this.direction.set(ray1.direction);
            this.t = ray1.t;
        }
    }

    public static final class RayObjectPool extends ObjectPool<UI3DScene.Ray> {
        int allocated = 0;

        public RayObjectPool() {
            super(UI3DScene.Ray::new);
        }

        protected UI3DScene.Ray makeObject() {
            this.allocated++;
            return (UI3DScene.Ray)super.makeObject();
        }
    }

    private final class RotateGizmo extends UI3DScene.Gizmo {
        UI3DScene.Axis m_trackAxis = UI3DScene.Axis.None;
        final UI3DScene.Circle m_trackCircle = new UI3DScene.Circle();
        final Matrix4f m_startXfrm = new Matrix4f();
        final Matrix4f m_startInvXfrm = new Matrix4f();
        final Vector3f m_startPointOnCircle = new Vector3f();
        final Vector3f m_currentPointOnCircle = new Vector3f();
        final ArrayList<Vector3f> m_circlePointsMain = new ArrayList<>();
        final ArrayList<Vector3f> m_circlePointsRender = new ArrayList<>();

        @Override
        UI3DScene.Axis hitTest(float float1, float float0) {
            if (!this.m_visible) {
                return UI3DScene.Axis.None;
            } else {
                UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
                float0 = UI3DScene.this.screenHeight() - float0;
                UI3DScene.Ray ray = UI3DScene.this.getCameraRay(float1, float0, UI3DScene.allocRay());
                Matrix4f matrix4f = UI3DScene.allocMatrix4f();
                matrix4f.set(stateData.m_gizmoParentTransform);
                matrix4f.mul(stateData.m_gizmoOriginTransform);
                matrix4f.mul(stateData.m_gizmoChildTransform);
                matrix4f.mul(stateData.m_gizmoTransform);
                Vector3f vector3f0 = matrix4f.getScale(UI3DScene.allocVector3f());
                matrix4f.scale(1.0F / vector3f0.x, 1.0F / vector3f0.y, 1.0F / vector3f0.z);
                UI3DScene.releaseVector3f(vector3f0);
                if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
                    matrix4f.setRotationXYZ(0.0F, 0.0F, 0.0F);
                }

                float float2 = UI3DScene.this.m_gizmoScale / stateData.zoomMult() * 1000.0F;
                float float3 = this.LENGTH * float2;
                Vector3f vector3f1 = matrix4f.transformProject(UI3DScene.allocVector3f().set(0.0F, 0.0F, 0.0F));
                Vector3f vector3f2 = matrix4f.transformDirection(UI3DScene.allocVector3f().set(1.0F, 0.0F, 0.0F)).normalize();
                Vector3f vector3f3 = matrix4f.transformDirection(UI3DScene.allocVector3f().set(0.0F, 1.0F, 0.0F)).normalize();
                Vector3f vector3f4 = matrix4f.transformDirection(UI3DScene.allocVector3f().set(0.0F, 0.0F, 1.0F)).normalize();
                Vector2 vector = UI3DScene.allocVector2();
                this.getCircleSegments(vector3f1, float3, vector3f3, vector3f4, this.m_circlePointsMain);
                float float4 = this.hitTestCircle(ray, this.m_circlePointsMain, vector);
                BaseVehicle.TL_vector3f_pool.get().release(this.m_circlePointsMain);
                this.m_circlePointsMain.clear();
                this.getCircleSegments(vector3f1, float3, vector3f2, vector3f4, this.m_circlePointsMain);
                float float5 = this.hitTestCircle(ray, this.m_circlePointsMain, vector);
                BaseVehicle.TL_vector3f_pool.get().release(this.m_circlePointsMain);
                this.m_circlePointsMain.clear();
                this.getCircleSegments(vector3f1, float3, vector3f2, vector3f3, this.m_circlePointsMain);
                float float6 = this.hitTestCircle(ray, this.m_circlePointsMain, vector);
                BaseVehicle.TL_vector3f_pool.get().release(this.m_circlePointsMain);
                this.m_circlePointsMain.clear();
                UI3DScene.releaseVector2(vector);
                UI3DScene.releaseVector3f(vector3f2);
                UI3DScene.releaseVector3f(vector3f3);
                UI3DScene.releaseVector3f(vector3f4);
                UI3DScene.releaseVector3f(vector3f1);
                UI3DScene.releaseRay(ray);
                UI3DScene.releaseMatrix4f(matrix4f);
                float float7 = 8.0F;
                if (float4 < float5 && float4 < float6) {
                    return float4 <= float7 ? UI3DScene.Axis.X : UI3DScene.Axis.None;
                } else if (float5 < float4 && float5 < float6) {
                    return float5 <= float7 ? UI3DScene.Axis.Y : UI3DScene.Axis.None;
                } else if (float6 < float4 && float6 < float5) {
                    return float6 <= float7 ? UI3DScene.Axis.Z : UI3DScene.Axis.None;
                } else {
                    return UI3DScene.Axis.None;
                }
            }
        }

        @Override
        void startTracking(float float0, float float1, UI3DScene.Axis axis) {
            UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
            this.m_startXfrm.set(stateData.m_gizmoParentTransform);
            this.m_startXfrm.mul(stateData.m_gizmoOriginTransform);
            this.m_startXfrm.mul(stateData.m_gizmoChildTransform);
            this.m_startXfrm.mul(stateData.m_gizmoTransform);
            if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
                this.m_startXfrm.setRotationXYZ(0.0F, 0.0F, 0.0F);
            }

            this.m_startInvXfrm.set(stateData.m_gizmoParentTransform);
            this.m_startInvXfrm.mul(stateData.m_gizmoOriginTransform);
            this.m_startInvXfrm.mul(stateData.m_gizmoChildTransform);
            this.m_startInvXfrm.mul(stateData.m_gizmoTransform);
            this.m_startInvXfrm.invert();
            this.m_trackAxis = axis;
            this.getPointOnAxis(float0, float1, axis, this.m_trackCircle, this.m_startXfrm, this.m_startPointOnCircle);
        }

        @Override
        void updateTracking(float float0, float float1) {
            Vector3f vector3f0 = this.getPointOnAxis(float0, float1, this.m_trackAxis, this.m_trackCircle, this.m_startXfrm, UI3DScene.allocVector3f());
            if (this.m_currentPointOnCircle.equals(vector3f0)) {
                UI3DScene.releaseVector3f(vector3f0);
            } else {
                this.m_currentPointOnCircle.set(vector3f0);
                UI3DScene.releaseVector3f(vector3f0);
                float float2 = this.calculateRotation(this.m_startPointOnCircle, this.m_currentPointOnCircle, this.m_trackCircle);
                switch (this.m_trackAxis) {
                    case X:
                        this.m_trackCircle.orientation.set(1.0F, 0.0F, 0.0F);
                        break;
                    case Y:
                        this.m_trackCircle.orientation.set(0.0F, 1.0F, 0.0F);
                        break;
                    case Z:
                        this.m_trackCircle.orientation.set(0.0F, 0.0F, 1.0F);
                }

                Vector3f vector3f1 = UI3DScene.allocVector3f().set(this.m_trackCircle.orientation);
                if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
                    this.m_startInvXfrm.transformDirection(vector3f1);
                }

                UI3DScene.Ray ray = UI3DScene.this.getCameraRay(float0, float1, UI3DScene.allocRay());
                Vector3f vector3f2 = this.m_startXfrm.transformDirection(UI3DScene.allocVector3f().set(vector3f1)).normalize();
                float float3 = ray.direction.dot(vector3f2);
                UI3DScene.releaseVector3f(vector3f2);
                UI3DScene.releaseRay(ray);
                if (UI3DScene.this.m_gizmoParent instanceof UI3DScene.SceneCharacter) {
                    if (float3 > 0.0F) {
                        float2 *= -1.0F;
                    }
                } else if (float3 < 0.0F) {
                    float2 *= -1.0F;
                }

                Quaternionf quaternionf = UI3DScene.allocQuaternionf().fromAxisAngleDeg(vector3f1, float2);
                UI3DScene.releaseVector3f(vector3f1);
                vector3f2 = quaternionf.getEulerAnglesXYZ(new Vector3f());
                UI3DScene.releaseQuaternionf(quaternionf);
                vector3f2.x = (float)Math.floor(vector3f2.x * (180.0F / (float)Math.PI) + 0.5F);
                vector3f2.y = (float)Math.floor(vector3f2.y * (180.0F / (float)Math.PI) + 0.5F);
                vector3f2.z = (float)Math.floor(vector3f2.z * (180.0F / (float)Math.PI) + 0.5F);
                LuaManager.caller.pcall(UIManager.getDefaultThread(), UI3DScene.this.getTable().rawget("onGizmoChanged"), UI3DScene.this.table, vector3f2);
            }
        }

        @Override
        void stopTracking() {
            this.m_trackAxis = UI3DScene.Axis.None;
        }

        @Override
        void render() {
            if (this.m_visible) {
                UI3DScene.StateData stateData = UI3DScene.this.stateDataRender();
                Matrix4f matrix4f = UI3DScene.allocMatrix4f();
                matrix4f.set(stateData.m_gizmoParentTransform);
                matrix4f.mul(stateData.m_gizmoOriginTransform);
                matrix4f.mul(stateData.m_gizmoChildTransform);
                matrix4f.mul(stateData.m_gizmoTransform);
                Vector3f vector3f0 = matrix4f.getScale(UI3DScene.allocVector3f());
                matrix4f.scale(1.0F / vector3f0.x, 1.0F / vector3f0.y, 1.0F / vector3f0.z);
                UI3DScene.releaseVector3f(vector3f0);
                if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
                    matrix4f.setRotationXYZ(0.0F, 0.0F, 0.0F);
                }

                float float0 = Mouse.getXA() - UI3DScene.this.getAbsoluteX().intValue();
                float float1 = Mouse.getYA() - UI3DScene.this.getAbsoluteY().intValue();
                UI3DScene.Ray ray = UI3DScene.this.getCameraRay(
                    float0, UI3DScene.this.screenHeight() - float1, stateData.m_projection, stateData.m_modelView, UI3DScene.allocRay()
                );
                float float2 = UI3DScene.this.m_gizmoScale / stateData.zoomMult() * 1000.0F;
                float float3 = this.LENGTH * float2;
                Vector3f vector3f1 = matrix4f.transformProject(UI3DScene.allocVector3f().set(0.0F, 0.0F, 0.0F));
                Vector3f vector3f2 = matrix4f.transformDirection(UI3DScene.allocVector3f().set(1.0F, 0.0F, 0.0F)).normalize();
                Vector3f vector3f3 = matrix4f.transformDirection(UI3DScene.allocVector3f().set(0.0F, 1.0F, 0.0F)).normalize();
                Vector3f vector3f4 = matrix4f.transformDirection(UI3DScene.allocVector3f().set(0.0F, 0.0F, 1.0F)).normalize();
                GL11.glClear(256);
                GL11.glEnable(2929);
                UI3DScene.Axis axis = this.m_trackAxis == UI3DScene.Axis.None ? stateData.m_gizmoAxis : this.m_trackAxis;
                if (this.m_trackAxis == UI3DScene.Axis.None || this.m_trackAxis == UI3DScene.Axis.X) {
                    float float4 = axis == UI3DScene.Axis.X ? 1.0F : 0.5F;
                    float float5 = 0.0F;
                    float float6 = 0.0F;
                    this.renderAxis(vector3f1, float3, vector3f3, vector3f4, float4, float5, float6, ray);
                }

                if (this.m_trackAxis == UI3DScene.Axis.None || this.m_trackAxis == UI3DScene.Axis.Y) {
                    float float7 = 0.0F;
                    float float8 = axis == UI3DScene.Axis.Y ? 1.0F : 0.5F;
                    float float9 = 0.0F;
                    this.renderAxis(vector3f1, float3, vector3f2, vector3f4, float7, float8, float9, ray);
                }

                if (this.m_trackAxis == UI3DScene.Axis.None || this.m_trackAxis == UI3DScene.Axis.Z) {
                    float float10 = 0.0F;
                    float float11 = 0.0F;
                    float float12 = axis == UI3DScene.Axis.Z ? 1.0F : 0.5F;
                    this.renderAxis(vector3f1, float3, vector3f2, vector3f3, float10, float11, float12, ray);
                }

                UI3DScene.releaseVector3f(vector3f1);
                UI3DScene.releaseVector3f(vector3f2);
                UI3DScene.releaseVector3f(vector3f3);
                UI3DScene.releaseVector3f(vector3f4);
                UI3DScene.releaseRay(ray);
                UI3DScene.releaseMatrix4f(matrix4f);
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
                this.renderLineToOrigin();
            }
        }

        void getCircleSegments(Vector3f vector3f4, float float0, Vector3f vector3f2, Vector3f vector3f3, ArrayList<Vector3f> arrayList) {
            Vector3f vector3f0 = UI3DScene.allocVector3f();
            Vector3f vector3f1 = UI3DScene.allocVector3f();
            byte byte0 = 32;
            double double0 = 0.0 / byte0 * (float) (Math.PI / 180.0);
            double double1 = Math.cos(double0);
            double double2 = Math.sin(double0);
            vector3f2.mul((float)double1, vector3f0);
            vector3f3.mul((float)double2, vector3f1);
            vector3f0.add(vector3f1).mul(float0);
            arrayList.add(UI3DScene.allocVector3f().set(vector3f4).add(vector3f0));

            for (int int0 = 1; int0 <= byte0; int0++) {
                double0 = int0 * 360.0 / byte0 * (float) (Math.PI / 180.0);
                double1 = Math.cos(double0);
                double2 = Math.sin(double0);
                vector3f2.mul((float)double1, vector3f0);
                vector3f3.mul((float)double2, vector3f1);
                vector3f0.add(vector3f1).mul(float0);
                arrayList.add(UI3DScene.allocVector3f().set(vector3f4).add(vector3f0));
            }

            UI3DScene.releaseVector3f(vector3f0);
            UI3DScene.releaseVector3f(vector3f1);
        }

        private float hitTestCircle(UI3DScene.Ray ray1, ArrayList<Vector3f> arrayList, Vector2 vector) {
            UI3DScene.Ray ray0 = UI3DScene.allocRay();
            Vector3f vector3f0 = UI3DScene.allocVector3f();
            float float0 = UI3DScene.this.sceneToUIX(ray1.origin.x, ray1.origin.y, ray1.origin.z);
            float float1 = UI3DScene.this.sceneToUIY(ray1.origin.x, ray1.origin.y, ray1.origin.z);
            float float2 = Float.MAX_VALUE;
            Vector3f vector3f1 = (Vector3f)arrayList.get(0);

            for (int int0 = 1; int0 < arrayList.size(); int0++) {
                Vector3f vector3f2 = (Vector3f)arrayList.get(int0);
                float float3 = UI3DScene.this.sceneToUIX(vector3f1.x, vector3f1.y, vector3f1.z);
                float float4 = UI3DScene.this.sceneToUIY(vector3f1.x, vector3f1.y, vector3f1.z);
                float float5 = UI3DScene.this.sceneToUIX(vector3f2.x, vector3f2.y, vector3f2.z);
                float float6 = UI3DScene.this.sceneToUIY(vector3f2.x, vector3f2.y, vector3f2.z);
                double double0 = Math.pow(float5 - float3, 2.0) + Math.pow(float6 - float4, 2.0);
                if (double0 < 0.001) {
                    vector3f1 = vector3f2;
                } else {
                    double double1 = ((float0 - float3) * (float5 - float3) + (float1 - float4) * (float6 - float4)) / double0;
                    double double2 = float3 + double1 * (float5 - float3);
                    double double3 = float4 + double1 * (float6 - float4);
                    if (double1 <= 0.0) {
                        double2 = float3;
                        double3 = float4;
                    } else if (double1 >= 1.0) {
                        double2 = float5;
                        double3 = float6;
                    }

                    float float7 = IsoUtils.DistanceTo2D(float0, float1, (float)double2, (float)double3);
                    if (float7 < float2) {
                        float2 = float7;
                        vector.set((float)double2, (float)double3);
                    }

                    vector3f1 = vector3f2;
                }
            }

            UI3DScene.releaseVector3f(vector3f0);
            UI3DScene.releaseRay(ray0);
            return float2;
        }

        void renderAxis(Vector3f vector3f0, float float0, Vector3f vector3f1, Vector3f vector3f2, float float2, float float3, float float4, UI3DScene.Ray ray) {
            UI3DScene.vboLines.flush();
            UI3DScene.vboLines.setLineWidth(6.0F);
            this.getCircleSegments(vector3f0, float0, vector3f1, vector3f2, this.m_circlePointsRender);
            Vector3f vector3f3 = UI3DScene.allocVector3f();
            Vector3f vector3f4 = this.m_circlePointsRender.get(0);

            for (int int0 = 1; int0 < this.m_circlePointsRender.size(); int0++) {
                Vector3f vector3f5 = this.m_circlePointsRender.get(int0);
                vector3f3.set(vector3f5.x - vector3f0.x, vector3f5.y - vector3f0.y, vector3f5.z - vector3f0.z).normalize();
                float float1 = vector3f3.dot(ray.direction);
                if (float1 < 0.1F) {
                    UI3DScene.vboLines.addLine(vector3f4.x, vector3f4.y, vector3f4.z, vector3f5.x, vector3f5.y, vector3f5.z, float2, float3, float4, 1.0F);
                } else {
                    UI3DScene.vboLines
                        .addLine(
                            vector3f4.x, vector3f4.y, vector3f4.z, vector3f5.x, vector3f5.y, vector3f5.z, float2 / 2.0F, float3 / 2.0F, float4 / 2.0F, 0.25F
                        );
                }

                vector3f4 = vector3f5;
            }

            BaseVehicle.TL_vector3f_pool.get().release(this.m_circlePointsRender);
            this.m_circlePointsRender.clear();
            UI3DScene.releaseVector3f(vector3f3);
            UI3DScene.vboLines.flush();
        }

        Vector3f getPointOnAxis(float float3, float float4, UI3DScene.Axis var3, UI3DScene.Circle circle, Matrix4f matrix4f, Vector3f vector3f) {
            float float0 = 1.0F;
            circle.radius = this.LENGTH * float0;
            matrix4f.getTranslation(circle.center);
            float float1 = UI3DScene.this.sceneToUIX(circle.center.x, circle.center.y, circle.center.z);
            float float2 = UI3DScene.this.sceneToUIY(circle.center.x, circle.center.y, circle.center.z);
            circle.center.set(float1, float2, 0.0F);
            circle.orientation.set(0.0F, 0.0F, 1.0F);
            UI3DScene.Ray ray = UI3DScene.allocRay();
            ray.origin.set(float3, float4, 0.0F);
            ray.direction.set(0.0F, 0.0F, -1.0F);
            UI3DScene.this.closest_distance_line_circle(ray, circle, vector3f);
            UI3DScene.releaseRay(ray);
            return vector3f;
        }

        float calculateRotation(Vector3f vector3f0, Vector3f vector3f1, UI3DScene.Circle circle) {
            if (vector3f0.equals(vector3f1)) {
                return 0.0F;
            } else {
                Vector3f vector3f2 = UI3DScene.allocVector3f().set(vector3f0).sub(circle.center).normalize();
                Vector3f vector3f3 = UI3DScene.allocVector3f().set(vector3f1).sub(circle.center).normalize();
                float float0 = (float)Math.acos(vector3f3.dot(vector3f2));
                Vector3f vector3f4 = vector3f2.cross(vector3f3, UI3DScene.allocVector3f());
                int int0 = (int)Math.signum(vector3f4.dot(circle.orientation));
                UI3DScene.releaseVector3f(vector3f2);
                UI3DScene.releaseVector3f(vector3f3);
                UI3DScene.releaseVector3f(vector3f4);
                return int0 * float0 * (180.0F / (float)Math.PI);
            }
        }
    }

    private final class ScaleGizmo extends UI3DScene.Gizmo {
        final Matrix4f m_startXfrm = new Matrix4f();
        final Matrix4f m_startInvXfrm = new Matrix4f();
        final Vector3f m_startPos = new Vector3f();
        final Vector3f m_currentPos = new Vector3f();
        UI3DScene.Axis m_trackAxis = UI3DScene.Axis.None;
        boolean m_hideX;
        boolean m_hideY;
        boolean m_hideZ;
        final Cylinder cylinder = new Cylinder();

        @Override
        UI3DScene.Axis hitTest(float float1, float float0) {
            if (!this.m_visible) {
                return UI3DScene.Axis.None;
            } else {
                UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
                Matrix4f matrix4f = UI3DScene.allocMatrix4f();
                matrix4f.set(stateData.m_gizmoParentTransform);
                matrix4f.mul(stateData.m_gizmoOriginTransform);
                matrix4f.mul(stateData.m_gizmoTransform);
                if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
                    matrix4f.setRotationXYZ(0.0F, 0.0F, 0.0F);
                }

                float0 = UI3DScene.this.screenHeight() - float0;
                UI3DScene.Ray ray0 = UI3DScene.this.getCameraRay(float1, float0, UI3DScene.allocRay());
                UI3DScene.Ray ray1 = UI3DScene.allocRay();
                matrix4f.transformProject(ray1.origin.set(0.0F, 0.0F, 0.0F));
                float float2 = UI3DScene.this.m_gizmoScale / stateData.zoomMult() * 1000.0F;
                float float3 = this.LENGTH * float2;
                float float4 = this.THICKNESS * float2;
                float float5 = 0.1F * float2;
                matrix4f.transformDirection(ray1.direction.set(1.0F, 0.0F, 0.0F)).normalize();
                float float6 = UI3DScene.this.closest_distance_between_lines(ray1, ray0);
                float float7 = ray1.t;
                float float8 = ray0.t;
                if (float7 < float5 || float7 >= float5 + float3) {
                    float7 = Float.MAX_VALUE;
                    float6 = Float.MAX_VALUE;
                }

                float float9 = ray1.direction.dot(ray0.direction);
                this.m_hideX = Math.abs(float9) > 0.9F;
                matrix4f.transformDirection(ray1.direction.set(0.0F, 1.0F, 0.0F)).normalize();
                float float10 = UI3DScene.this.closest_distance_between_lines(ray1, ray0);
                float float11 = ray1.t;
                float float12 = ray0.t;
                if (float11 < float5 || float11 >= float5 + float3) {
                    float11 = Float.MAX_VALUE;
                    float10 = Float.MAX_VALUE;
                }

                float float13 = ray1.direction.dot(ray0.direction);
                this.m_hideY = Math.abs(float13) > 0.9F;
                matrix4f.transformDirection(ray1.direction.set(0.0F, 0.0F, 1.0F)).normalize();
                float float14 = UI3DScene.this.closest_distance_between_lines(ray1, ray0);
                float float15 = ray1.t;
                float float16 = ray0.t;
                if (float15 < float5 || float15 >= float5 + float3) {
                    float15 = Float.MAX_VALUE;
                    float14 = Float.MAX_VALUE;
                }

                float float17 = ray1.direction.dot(ray0.direction);
                this.m_hideZ = Math.abs(float17) > 0.9F;
                UI3DScene.releaseRay(ray1);
                UI3DScene.releaseRay(ray0);
                UI3DScene.releaseMatrix4f(matrix4f);
                if (float7 >= float5 && float7 < float5 + float3 && float6 < float10 && float6 < float14) {
                    return float6 <= float4 / 2.0F ? UI3DScene.Axis.X : UI3DScene.Axis.None;
                } else if (float11 >= float5 && float11 < float5 + float3 && float10 < float6 && float10 < float14) {
                    return float10 <= float4 / 2.0F ? UI3DScene.Axis.Y : UI3DScene.Axis.None;
                } else if (float15 >= float5 && float15 < float5 + float3 && float14 < float6 && float14 < float10) {
                    return float14 <= float4 / 2.0F ? UI3DScene.Axis.Z : UI3DScene.Axis.None;
                } else {
                    return UI3DScene.Axis.None;
                }
            }
        }

        @Override
        void startTracking(float float0, float float1, UI3DScene.Axis axis) {
            UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
            this.m_startXfrm.set(stateData.m_gizmoParentTransform);
            this.m_startXfrm.mul(stateData.m_gizmoOriginTransform);
            this.m_startXfrm.mul(stateData.m_gizmoTransform);
            this.m_startXfrm.setRotationXYZ(0.0F, 0.0F, 0.0F);
            this.m_startInvXfrm.set(this.m_startXfrm);
            this.m_startInvXfrm.invert();
            this.m_trackAxis = axis;
            this.getPointOnAxis(float0, float1, axis, this.m_startXfrm, this.m_startPos);
        }

        @Override
        void updateTracking(float float0, float float1) {
            Vector3f vector3f0 = this.getPointOnAxis(float0, float1, this.m_trackAxis, this.m_startXfrm, UI3DScene.allocVector3f());
            if (this.m_currentPos.equals(vector3f0)) {
                UI3DScene.releaseVector3f(vector3f0);
            } else {
                UI3DScene.releaseVector3f(vector3f0);
                this.m_currentPos.set(vector3f0);
                UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
                Vector3f vector3f1 = new Vector3f(this.m_currentPos).sub(this.m_startPos);
                if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
                    Vector3f vector3f2 = this.m_startInvXfrm.transformPosition(this.m_startPos, new Vector3f());
                    Vector3f vector3f3 = this.m_startInvXfrm.transformPosition(this.m_currentPos, new Vector3f());
                    Matrix4f matrix4f = new Matrix4f(stateData.m_gizmoParentTransform).invert();
                    matrix4f.transformPosition(vector3f2);
                    matrix4f.transformPosition(vector3f3);
                    vector3f1.set(vector3f3).sub(vector3f2);
                } else {
                    Vector3f vector3f4 = this.m_startInvXfrm.transformPosition(this.m_startPos, new Vector3f());
                    Vector3f vector3f5 = this.m_startInvXfrm.transformPosition(this.m_currentPos, new Vector3f());
                    vector3f1.set(vector3f5).sub(vector3f4);
                }

                vector3f1.x = (float)Math.floor(vector3f1.x * UI3DScene.this.gridMult()) / UI3DScene.this.gridMult();
                vector3f1.y = (float)Math.floor(vector3f1.y * UI3DScene.this.gridMult()) / UI3DScene.this.gridMult();
                vector3f1.z = (float)Math.floor(vector3f1.z * UI3DScene.this.gridMult()) / UI3DScene.this.gridMult();
                LuaManager.caller.pcall(UIManager.getDefaultThread(), UI3DScene.this.getTable().rawget("onGizmoChanged"), UI3DScene.this.table, vector3f1);
            }
        }

        @Override
        void stopTracking() {
            this.m_trackAxis = UI3DScene.Axis.None;
        }

        @Override
        void render() {
            if (this.m_visible) {
                UI3DScene.StateData stateData = UI3DScene.this.stateDataRender();
                float float0 = UI3DScene.this.m_gizmoScale / stateData.zoomMult() * 1000.0F;
                float float1 = this.LENGTH * float0;
                float float2 = this.THICKNESS * float0;
                float float3 = 0.1F * float0;
                Matrix4f matrix4f = UI3DScene.allocMatrix4f();
                matrix4f.set(stateData.m_gizmoParentTransform);
                matrix4f.mul(stateData.m_gizmoOriginTransform);
                matrix4f.mul(stateData.m_gizmoChildTransform);
                matrix4f.mul(stateData.m_gizmoTransform);
                if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
                    matrix4f.setRotationXYZ(0.0F, 0.0F, 0.0F);
                }

                stateData.m_modelView.mul(matrix4f, matrix4f);
                PZGLUtil.pushAndLoadMatrix(5888, matrix4f);
                UI3DScene.releaseMatrix4f(matrix4f);
                if (!this.m_hideX) {
                    GL11.glColor3f(stateData.m_gizmoAxis == UI3DScene.Axis.X ? 1.0F : 0.5F, 0.0F, 0.0F);
                    GL11.glRotated(90.0, 0.0, 1.0, 0.0);
                    GL11.glTranslatef(0.0F, 0.0F, float3);
                    this.cylinder.draw(float2 / 2.0F, float2 / 2.0F, float1, 8, 1);
                    GL11.glTranslatef(0.0F, 0.0F, float1);
                    this.cylinder.draw(float2, float2, 0.1F * float0, 8, 1);
                    GL11.glTranslatef(0.0F, 0.0F, -float3 - float1);
                    GL11.glRotated(-90.0, 0.0, 1.0, 0.0);
                }

                if (!this.m_hideY) {
                    GL11.glColor3f(0.0F, stateData.m_gizmoAxis == UI3DScene.Axis.Y ? 1.0F : 0.5F, 0.0F);
                    GL11.glRotated(-90.0, 1.0, 0.0, 0.0);
                    GL11.glTranslatef(0.0F, 0.0F, float3);
                    this.cylinder.draw(float2 / 2.0F, float2 / 2.0F, float1, 8, 1);
                    GL11.glTranslatef(0.0F, 0.0F, float1);
                    this.cylinder.draw(float2, float2, 0.1F * float0, 8, 1);
                    GL11.glTranslatef(0.0F, 0.0F, -float3 - float1);
                    GL11.glRotated(90.0, 1.0, 0.0, 0.0);
                }

                if (!this.m_hideZ) {
                    GL11.glColor3f(0.0F, 0.0F, stateData.m_gizmoAxis == UI3DScene.Axis.Z ? 1.0F : 0.5F);
                    GL11.glTranslatef(0.0F, 0.0F, float3);
                    this.cylinder.draw(float2 / 2.0F, float2 / 2.0F, float1, 8, 1);
                    GL11.glTranslatef(0.0F, 0.0F, float1);
                    this.cylinder.draw(float2, float2, 0.1F * float0, 8, 1);
                    GL11.glTranslatef(0.0F, 0.0F, -0.1F - float1);
                }

                GL11.glColor3f(1.0F, 1.0F, 1.0F);
                PZGLUtil.popMatrix(5888);
                this.renderLineToOrigin();
            }
        }
    }

    private static final class SceneCharacter extends UI3DScene.SceneObject {
        final AnimatedModel m_animatedModel;
        boolean m_bShowBones = false;
        boolean m_bClearDepthBuffer = true;
        boolean m_bUseDeferredMovement = false;

        SceneCharacter(UI3DScene uI3DScene, String string) {
            super(uI3DScene, string);
            this.m_animatedModel = new AnimatedModel();
            this.m_animatedModel.setAnimSetName("player-vehicle");
            this.m_animatedModel.setState("idle");
            this.m_animatedModel.setOutfitName("Naked", false, false);
            this.m_animatedModel.setVisual(new HumanVisual(this.m_animatedModel));
            this.m_animatedModel.getHumanVisual().setHairModel("Bald");
            this.m_animatedModel.getHumanVisual().setBeardModel("");
            this.m_animatedModel.getHumanVisual().setSkinTextureIndex(0);
            this.m_animatedModel.setAlpha(0.5F);
            this.m_animatedModel.setAnimate(false);
        }

        @Override
        UI3DScene.SceneObjectRenderData renderMain() {
            this.m_animatedModel.update();
            UI3DScene.CharacterRenderData characterRenderData = UI3DScene.CharacterRenderData.s_pool.alloc();
            characterRenderData.initCharacter(this);
            SpriteRenderer.instance.drawGeneric(characterRenderData.m_drawer);
            return characterRenderData;
        }

        @Override
        Matrix4f getLocalTransform(Matrix4f matrix4f) {
            matrix4f.identity();
            matrix4f.rotateY((float) Math.PI);
            matrix4f.translate(-this.m_translate.x, this.m_translate.y, this.m_translate.z);
            matrix4f.scale(-1.5F, 1.5F, 1.5F);
            float float0 = this.m_rotate.y;
            if (this.m_autoRotate) {
                float0 += this.m_autoRotateAngle;
            }

            matrix4f.rotateXYZ(this.m_rotate.x * (float) (Math.PI / 180.0), float0 * (float) (Math.PI / 180.0), this.m_rotate.z * (float) (Math.PI / 180.0));
            if (this.m_animatedModel.getAnimationPlayer().getMultiTrack().getTracks().isEmpty()) {
                return matrix4f;
            } else {
                if (this.m_bUseDeferredMovement) {
                    AnimationMultiTrack animationMultiTrack = this.m_animatedModel.getAnimationPlayer().getMultiTrack();
                    float float1 = animationMultiTrack.getTracks().get(0).getCurrentDeferredRotation();
                    org.lwjgl.util.vector.Vector3f vector3f = new org.lwjgl.util.vector.Vector3f();
                    animationMultiTrack.getTracks().get(0).getCurrentDeferredPosition(vector3f);
                    matrix4f.translate(vector3f.x, vector3f.y, vector3f.z);
                }

                return matrix4f;
            }
        }

        @Override
        Matrix4f getAttachmentTransform(String string, Matrix4f matrix4f0) {
            matrix4f0.identity();
            boolean boolean0 = this.m_animatedModel.isFemale();
            ModelScript modelScript = ScriptManager.instance.getModelScript(boolean0 ? "FemaleBody" : "MaleBody");
            if (modelScript == null) {
                return matrix4f0;
            } else {
                ModelAttachment modelAttachment = modelScript.getAttachmentById(string);
                if (modelAttachment == null) {
                    return matrix4f0;
                } else {
                    matrix4f0.translation(modelAttachment.getOffset());
                    Vector3f vector3f = modelAttachment.getRotate();
                    matrix4f0.rotateXYZ(vector3f.x * (float) (Math.PI / 180.0), vector3f.y * (float) (Math.PI / 180.0), vector3f.z * (float) (Math.PI / 180.0));
                    if (modelAttachment.getBone() != null) {
                        Matrix4f matrix4f1 = this.getBoneMatrix(modelAttachment.getBone(), UI3DScene.allocMatrix4f());
                        matrix4f1.mul(matrix4f0, matrix4f0);
                        UI3DScene.releaseMatrix4f(matrix4f1);
                    }

                    return matrix4f0;
                }
            }
        }

        int hitTestBone(int int1, UI3DScene.Ray ray0, UI3DScene.Ray ray1, Matrix4f matrix4f1) {
            AnimationPlayer animationPlayer = this.m_animatedModel.getAnimationPlayer();
            SkinningData skinningData = animationPlayer.getSkinningData();
            int int0 = skinningData.SkeletonHierarchy.get(int1);
            if (int0 == -1) {
                return -1;
            } else {
                org.lwjgl.util.vector.Matrix4f matrix4f0 = animationPlayer.modelTransforms[int0];
                ray0.origin.set(matrix4f0.m03, matrix4f0.m13, matrix4f0.m23);
                matrix4f1.transformPosition(ray0.origin);
                matrix4f0 = animationPlayer.modelTransforms[int1];
                Vector3f vector3f = UI3DScene.allocVector3f();
                vector3f.set(matrix4f0.m03, matrix4f0.m13, matrix4f0.m23);
                matrix4f1.transformPosition(vector3f);
                ray0.direction.set(vector3f).sub(ray0.origin);
                float float0 = ray0.direction.length();
                ray0.direction.normalize();
                this.m_scene.closest_distance_between_lines(ray1, ray0);
                float float1 = this.m_scene
                    .sceneToUIX(ray1.origin.x + ray1.direction.x * ray1.t, ray1.origin.y + ray1.direction.y * ray1.t, ray1.origin.z + ray1.direction.z * ray1.t);
                float float2 = this.m_scene
                    .sceneToUIY(ray1.origin.x + ray1.direction.x * ray1.t, ray1.origin.y + ray1.direction.y * ray1.t, ray1.origin.z + ray1.direction.z * ray1.t);
                float float3 = this.m_scene
                    .sceneToUIX(ray0.origin.x + ray0.direction.x * ray0.t, ray0.origin.y + ray0.direction.y * ray0.t, ray0.origin.z + ray0.direction.z * ray0.t);
                float float4 = this.m_scene
                    .sceneToUIY(ray0.origin.x + ray0.direction.x * ray0.t, ray0.origin.y + ray0.direction.y * ray0.t, ray0.origin.z + ray0.direction.z * ray0.t);
                int int2 = -1;
                float float5 = 10.0F;
                float float6 = (float)Math.sqrt(Math.pow(float3 - float1, 2.0) + Math.pow(float4 - float2, 2.0));
                if (float6 < float5) {
                    if (ray0.t >= 0.0F && ray0.t < float0 * 0.5F) {
                        int2 = int0;
                    } else if (ray0.t >= float0 * 0.5F && ray0.t < float0) {
                        int2 = int1;
                    }
                }

                UI3DScene.releaseVector3f(vector3f);
                return int2;
            }
        }

        String pickBone(float float1, float float0) {
            if (this.m_animatedModel.getAnimationPlayer().modelTransforms == null) {
                return "";
            } else {
                float0 = this.m_scene.screenHeight() - float0;
                UI3DScene.Ray ray0 = this.m_scene.getCameraRay(float1, float0, UI3DScene.allocRay());
                Matrix4f matrix4f = UI3DScene.allocMatrix4f();
                this.getLocalTransform(matrix4f);
                UI3DScene.Ray ray1 = UI3DScene.allocRay();
                int int0 = -1;

                for (int int1 = 0; int1 < this.m_animatedModel.getAnimationPlayer().modelTransforms.length; int1++) {
                    int0 = this.hitTestBone(int1, ray1, ray0, matrix4f);
                    if (int0 != -1) {
                        break;
                    }
                }

                UI3DScene.releaseRay(ray1);
                UI3DScene.releaseRay(ray0);
                UI3DScene.releaseMatrix4f(matrix4f);
                return int0 == -1 ? "" : this.m_animatedModel.getAnimationPlayer().getSkinningData().getBoneAt(int0).Name;
            }
        }

        Matrix4f getBoneMatrix(String string, Matrix4f matrix4f) {
            matrix4f.identity();
            if (this.m_animatedModel.getAnimationPlayer().modelTransforms == null) {
                return matrix4f;
            } else {
                SkinningBone skinningBone = this.m_animatedModel.getAnimationPlayer().getSkinningData().getBone(string);
                if (skinningBone == null) {
                    return matrix4f;
                } else {
                    matrix4f = PZMath.convertMatrix(this.m_animatedModel.getAnimationPlayer().modelTransforms[skinningBone.Index], matrix4f);
                    matrix4f.transpose();
                    return matrix4f;
                }
            }
        }

        UI3DScene.PositionRotation getBoneAxis(String var1, UI3DScene.PositionRotation positionRotation) {
            Matrix4f matrix4f = UI3DScene.allocMatrix4f().identity();
            matrix4f.getTranslation(positionRotation.pos);
            UI3DScene.releaseMatrix4f(matrix4f);
            Quaternionf quaternionf = matrix4f.getUnnormalizedRotation(UI3DScene.allocQuaternionf());
            quaternionf.getEulerAnglesXYZ(positionRotation.rot);
            UI3DScene.releaseQuaternionf(quaternionf);
            return positionRotation;
        }
    }

    private static final class SceneModel extends UI3DScene.SceneObject {
        ModelScript m_modelScript;
        Model m_model;
        boolean m_useWorldAttachment = false;
        boolean m_weaponRotationHack = false;

        SceneModel(UI3DScene uI3DScene, String string, ModelScript modelScript, Model model) {
            super(uI3DScene, string);
            Objects.requireNonNull(modelScript);
            Objects.requireNonNull(model);
            this.m_modelScript = modelScript;
            this.m_model = model;
        }

        @Override
        UI3DScene.SceneObjectRenderData renderMain() {
            if (!this.m_model.isReady()) {
                return null;
            } else {
                UI3DScene.ModelRenderData modelRenderData = UI3DScene.ModelRenderData.s_pool.alloc();
                modelRenderData.initModel(this);
                SpriteRenderer.instance.drawGeneric(modelRenderData.m_drawer);
                return modelRenderData;
            }
        }

        @Override
        Matrix4f getLocalTransform(Matrix4f matrix4f) {
            super.getLocalTransform(matrix4f);
            return matrix4f;
        }

        @Override
        Matrix4f getAttachmentTransform(String string, Matrix4f matrix4f) {
            matrix4f.identity();
            ModelAttachment modelAttachment = this.m_modelScript.getAttachmentById(string);
            if (modelAttachment == null) {
                return matrix4f;
            } else {
                matrix4f.translation(modelAttachment.getOffset());
                Vector3f vector3f = modelAttachment.getRotate();
                matrix4f.rotateXYZ(vector3f.x * (float) (Math.PI / 180.0), vector3f.y * (float) (Math.PI / 180.0), vector3f.z * (float) (Math.PI / 180.0));
                return matrix4f;
            }
        }
    }

    private abstract class SceneModelCamera extends ModelCamera {
        UI3DScene.SceneObjectRenderData m_renderData;
    }

    private abstract static class SceneObject {
        final UI3DScene m_scene;
        final String m_id;
        boolean m_visible = true;
        final Vector3f m_translate = new Vector3f();
        final Vector3f m_rotate = new Vector3f();
        UI3DScene.SceneObject m_parent;
        String m_attachment;
        String m_parentAttachment;
        boolean m_autoRotate = false;
        float m_autoRotateAngle = 0.0F;

        SceneObject(UI3DScene uI3DScene, String string) {
            this.m_scene = uI3DScene;
            this.m_id = string;
        }

        abstract UI3DScene.SceneObjectRenderData renderMain();

        Matrix4f getLocalTransform(Matrix4f matrix4f0) {
            UI3DScene.SceneModel sceneModel = Type.tryCastTo(this, UI3DScene.SceneModel.class);
            if (sceneModel != null && sceneModel.m_useWorldAttachment) {
                matrix4f0.translation(-this.m_translate.x, this.m_translate.y, this.m_translate.z);
                matrix4f0.scale(-1.5F, 1.5F, 1.5F);
            } else {
                matrix4f0.translation(this.m_translate);
            }

            float float0 = this.m_rotate.y;
            if (this.m_autoRotate) {
                float0 += this.m_autoRotateAngle;
            }

            matrix4f0.rotateXYZ(this.m_rotate.x * (float) (Math.PI / 180.0), float0 * (float) (Math.PI / 180.0), this.m_rotate.z * (float) (Math.PI / 180.0));
            if (this.m_attachment != null) {
                Matrix4f matrix4f1 = this.getAttachmentTransform(this.m_attachment, UI3DScene.allocMatrix4f());
                matrix4f1.invert();
                matrix4f0.mul(matrix4f1);
                UI3DScene.releaseMatrix4f(matrix4f1);
            }

            return matrix4f0;
        }

        Matrix4f getGlobalTransform(Matrix4f matrix4f0) {
            this.getLocalTransform(matrix4f0);
            if (this.m_parent != null) {
                if (this.m_parentAttachment != null) {
                    Matrix4f matrix4f1 = this.m_parent.getAttachmentTransform(this.m_parentAttachment, UI3DScene.allocMatrix4f());
                    matrix4f1.mul(matrix4f0, matrix4f0);
                    UI3DScene.releaseMatrix4f(matrix4f1);
                }

                Matrix4f matrix4f2 = this.m_parent.getGlobalTransform(UI3DScene.allocMatrix4f());
                matrix4f2.mul(matrix4f0, matrix4f0);
                UI3DScene.releaseMatrix4f(matrix4f2);
            }

            return matrix4f0;
        }

        Matrix4f getAttachmentTransform(String var1, Matrix4f matrix4f) {
            matrix4f.identity();
            return matrix4f;
        }
    }

    private static class SceneObjectRenderData {
        UI3DScene.SceneObject m_object;
        final Matrix4f m_transform = new Matrix4f();
        private static final ObjectPool<UI3DScene.SceneObjectRenderData> s_pool = new ObjectPool<>(UI3DScene.SceneObjectRenderData::new);

        UI3DScene.SceneObjectRenderData init(UI3DScene.SceneObject sceneObject) {
            this.m_object = sceneObject;
            sceneObject.getGlobalTransform(this.m_transform);
            return this;
        }

        void release() {
            s_pool.release(this);
        }
    }

    private static final class SceneVehicle extends UI3DScene.SceneObject {
        String m_scriptName = "Base.ModernCar";
        VehicleScript m_script;
        Model m_model;

        SceneVehicle(UI3DScene uI3DScene, String string) {
            super(uI3DScene, string);
            this.setScriptName("Base.ModernCar");
        }

        @Override
        UI3DScene.SceneObjectRenderData renderMain() {
            if (this.m_script == null) {
                this.m_model = null;
                return null;
            } else {
                String string = this.m_script.getModel().file;
                this.m_model = ModelManager.instance.getLoadedModel(string);
                if (this.m_model == null) {
                    return null;
                } else {
                    if (this.m_script.getSkinCount() > 0) {
                        this.m_model.tex = Texture.getSharedTexture("media/textures/" + this.m_script.getSkin(0).texture + ".png");
                    }

                    UI3DScene.VehicleRenderData vehicleRenderData = UI3DScene.VehicleRenderData.s_pool.alloc();
                    vehicleRenderData.initVehicle(this);
                    UI3DScene.SetModelCamera setModelCamera = UI3DScene.s_SetModelCameraPool.alloc();
                    SpriteRenderer.instance.drawGeneric(setModelCamera.init(this.m_scene.m_VehicleSceneModelCamera, vehicleRenderData));
                    SpriteRenderer.instance.drawGeneric(vehicleRenderData.m_drawer);
                    return vehicleRenderData;
                }
            }
        }

        void setScriptName(String string) {
            this.m_scriptName = string;
            this.m_script = ScriptManager.instance.getVehicle(string);
        }
    }

    private static final class SetModelCamera extends TextureDraw.GenericDrawer {
        UI3DScene.SceneModelCamera m_camera;
        UI3DScene.SceneObjectRenderData m_renderData;

        UI3DScene.SetModelCamera init(UI3DScene.SceneModelCamera sceneModelCamera, UI3DScene.SceneObjectRenderData sceneObjectRenderData) {
            this.m_camera = sceneModelCamera;
            this.m_renderData = sceneObjectRenderData;
            return this;
        }

        @Override
        public void render() {
            this.m_camera.m_renderData = this.m_renderData;
            ModelCamera.instance = this.m_camera;
        }

        @Override
        public void postRender() {
            UI3DScene.s_SetModelCameraPool.release(this);
        }
    }

    private static final class StateData {
        final Matrix4f m_projection = new Matrix4f();
        final Matrix4f m_modelView = new Matrix4f();
        int m_zoom;
        UI3DScene.GridPlaneDrawer m_gridPlaneDrawer;
        UI3DScene.OverlaysDrawer m_overlaysDrawer;
        final ArrayList<UI3DScene.SceneObjectRenderData> m_objectData = new ArrayList<>();
        UI3DScene.Gizmo m_gizmo = null;
        final Vector3f m_gizmoTranslate = new Vector3f();
        final Vector3f m_gizmoRotate = new Vector3f();
        final Matrix4f m_gizmoParentTransform = new Matrix4f();
        final Matrix4f m_gizmoOriginTransform = new Matrix4f();
        final Matrix4f m_gizmoChildTransform = new Matrix4f();
        final Matrix4f m_gizmoTransform = new Matrix4f();
        boolean m_hasGizmoOrigin;
        boolean m_selectedAttachmentIsChildAttachment;
        UI3DScene.Axis m_gizmoAxis = UI3DScene.Axis.None;
        final UI3DScene.TranslateGizmoRenderData m_translateGizmoRenderData = new UI3DScene.TranslateGizmoRenderData();
        final ArrayList<UI3DScene.PositionRotation> m_axes = new ArrayList<>();
        final ArrayList<UI3DScene.AABB> m_aabb = new ArrayList<>();
        final ArrayList<UI3DScene.Box3D> m_box3D = new ArrayList<>();

        private float zoomMult() {
            return (float)Math.exp(this.m_zoom * 0.2F) * 160.0F / Math.max(1.82F, 1.0F);
        }
    }

    private static enum TransformMode {
        Global,
        Local;
    }

    private final class TranslateGizmo extends UI3DScene.Gizmo {
        final Matrix4f m_startXfrm = new Matrix4f();
        final Matrix4f m_startInvXfrm = new Matrix4f();
        final Vector3f m_startPos = new Vector3f();
        final Vector3f m_currentPos = new Vector3f();
        UI3DScene.Axis m_trackAxis = UI3DScene.Axis.None;
        Cylinder cylinder = new Cylinder();

        @Override
        UI3DScene.Axis hitTest(float float1, float float0) {
            if (!this.m_visible) {
                return UI3DScene.Axis.None;
            } else {
                UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
                Matrix4f matrix4f = UI3DScene.allocMatrix4f();
                matrix4f.set(stateData.m_gizmoParentTransform);
                matrix4f.mul(stateData.m_gizmoOriginTransform);
                matrix4f.mul(stateData.m_gizmoChildTransform);
                matrix4f.mul(stateData.m_gizmoTransform);
                if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
                    matrix4f.setRotationXYZ(0.0F, 0.0F, 0.0F);
                }

                float0 = UI3DScene.this.screenHeight() - float0;
                UI3DScene.Ray ray0 = UI3DScene.this.getCameraRay(float1, float0, UI3DScene.allocRay());
                UI3DScene.Ray ray1 = UI3DScene.allocRay();
                matrix4f.transformPosition(ray1.origin.set(0.0F, 0.0F, 0.0F));
                float float2 = UI3DScene.this.m_gizmoScale / stateData.zoomMult() * 1000.0F;
                float float3 = this.LENGTH * float2;
                float float4 = this.THICKNESS * float2;
                float float5 = 0.1F * float2;
                matrix4f.transformDirection(ray1.direction.set(1.0F, 0.0F, 0.0F)).normalize();
                float float6 = UI3DScene.this.closest_distance_between_lines(ray1, ray0);
                float float7 = ray1.t;
                float float8 = ray0.t;
                if (float7 < float5 || float7 >= float5 + float3) {
                    float7 = Float.MAX_VALUE;
                    float6 = Float.MAX_VALUE;
                }

                float float9 = ray1.direction.dot(ray0.direction);
                stateData.m_translateGizmoRenderData.m_hideX = Math.abs(float9) > 0.9F;
                matrix4f.transformDirection(ray1.direction.set(0.0F, 1.0F, 0.0F)).normalize();
                float float10 = UI3DScene.this.closest_distance_between_lines(ray1, ray0);
                float float11 = ray1.t;
                float float12 = ray0.t;
                if (float11 < float5 || float11 >= float5 + float3) {
                    float11 = Float.MAX_VALUE;
                    float10 = Float.MAX_VALUE;
                }

                float float13 = ray1.direction.dot(ray0.direction);
                stateData.m_translateGizmoRenderData.m_hideY = Math.abs(float13) > 0.9F;
                matrix4f.transformDirection(ray1.direction.set(0.0F, 0.0F, 1.0F)).normalize();
                float float14 = UI3DScene.this.closest_distance_between_lines(ray1, ray0);
                float float15 = ray1.t;
                float float16 = ray0.t;
                if (float15 < float5 || float15 >= float5 + float3) {
                    float15 = Float.MAX_VALUE;
                    float14 = Float.MAX_VALUE;
                }

                float float17 = ray1.direction.dot(ray0.direction);
                stateData.m_translateGizmoRenderData.m_hideZ = Math.abs(float17) > 0.9F;
                UI3DScene.releaseRay(ray1);
                UI3DScene.releaseRay(ray0);
                UI3DScene.releaseMatrix4f(matrix4f);
                if (float7 >= float5 && float7 < float5 + float3 && float6 < float10 && float6 < float14) {
                    return float6 <= float4 / 2.0F ? UI3DScene.Axis.X : UI3DScene.Axis.None;
                } else if (float11 >= float5 && float11 < float5 + float3 && float10 < float6 && float10 < float14) {
                    return float10 <= float4 / 2.0F ? UI3DScene.Axis.Y : UI3DScene.Axis.None;
                } else if (float15 >= float5 && float15 < float5 + float3 && float14 < float6 && float14 < float10) {
                    return float14 <= float4 / 2.0F ? UI3DScene.Axis.Z : UI3DScene.Axis.None;
                } else {
                    return UI3DScene.Axis.None;
                }
            }
        }

        @Override
        void startTracking(float float0, float float1, UI3DScene.Axis axis) {
            UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
            this.m_startXfrm.set(stateData.m_gizmoParentTransform);
            this.m_startXfrm.mul(stateData.m_gizmoOriginTransform);
            this.m_startXfrm.mul(stateData.m_gizmoChildTransform);
            this.m_startXfrm.mul(stateData.m_gizmoTransform);
            if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
                this.m_startXfrm.setRotationXYZ(0.0F, 0.0F, 0.0F);
            }

            this.m_startInvXfrm.set(this.m_startXfrm);
            this.m_startInvXfrm.invert();
            this.m_trackAxis = axis;
            this.getPointOnAxis(float0, float1, axis, this.m_startXfrm, this.m_startPos);
        }

        @Override
        void updateTracking(float float0, float float1) {
            Vector3f vector3f0 = this.getPointOnAxis(float0, float1, this.m_trackAxis, this.m_startXfrm, UI3DScene.allocVector3f());
            if (this.m_currentPos.equals(vector3f0)) {
                UI3DScene.releaseVector3f(vector3f0);
            } else {
                UI3DScene.releaseVector3f(vector3f0);
                this.m_currentPos.set(vector3f0);
                UI3DScene.StateData stateData = UI3DScene.this.stateDataMain();
                Vector3f vector3f1 = new Vector3f(this.m_currentPos).sub(this.m_startPos);
                if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
                    Vector3f vector3f2 = this.m_startInvXfrm.transformPosition(this.m_startPos, UI3DScene.allocVector3f());
                    Vector3f vector3f3 = this.m_startInvXfrm.transformPosition(this.m_currentPos, UI3DScene.allocVector3f());
                    Matrix4f matrix4f0 = UI3DScene.allocMatrix4f();
                    matrix4f0.set(stateData.m_gizmoParentTransform);
                    matrix4f0.mul(stateData.m_gizmoOriginTransform);
                    matrix4f0.mul(stateData.m_gizmoChildTransform);
                    matrix4f0.invert();
                    matrix4f0.transformPosition(vector3f2);
                    matrix4f0.transformPosition(vector3f3);
                    UI3DScene.releaseMatrix4f(matrix4f0);
                    vector3f1.set(vector3f3).sub(vector3f2);
                    UI3DScene.releaseVector3f(vector3f2);
                    UI3DScene.releaseVector3f(vector3f3);
                } else {
                    Vector3f vector3f4 = this.m_startInvXfrm.transformPosition(this.m_startPos, UI3DScene.allocVector3f());
                    Vector3f vector3f5 = this.m_startInvXfrm.transformPosition(this.m_currentPos, UI3DScene.allocVector3f());
                    Matrix4f matrix4f1 = UI3DScene.allocMatrix4f();
                    matrix4f1.set(stateData.m_gizmoTransform);
                    matrix4f1.transformPosition(vector3f4);
                    matrix4f1.transformPosition(vector3f5);
                    UI3DScene.releaseMatrix4f(matrix4f1);
                    vector3f1.set(vector3f5).sub(vector3f4);
                    UI3DScene.releaseVector3f(vector3f4);
                    UI3DScene.releaseVector3f(vector3f5);
                }

                vector3f1.x = (float)Math.floor(vector3f1.x * UI3DScene.this.gridMult()) / UI3DScene.this.gridMult();
                vector3f1.y = (float)Math.floor(vector3f1.y * UI3DScene.this.gridMult()) / UI3DScene.this.gridMult();
                vector3f1.z = (float)Math.floor(vector3f1.z * UI3DScene.this.gridMult()) / UI3DScene.this.gridMult();
                if (stateData.m_selectedAttachmentIsChildAttachment) {
                    vector3f1.mul(-1.0F);
                }

                LuaManager.caller.pcall(UIManager.getDefaultThread(), UI3DScene.this.getTable().rawget("onGizmoChanged"), UI3DScene.this.table, vector3f1);
            }
        }

        @Override
        void stopTracking() {
            this.m_trackAxis = UI3DScene.Axis.None;
        }

        @Override
        void render() {
            if (this.m_visible) {
                UI3DScene.StateData stateData = UI3DScene.this.stateDataRender();
                Matrix4f matrix4f = UI3DScene.allocMatrix4f();
                matrix4f.set(stateData.m_gizmoParentTransform);
                matrix4f.mul(stateData.m_gizmoOriginTransform);
                matrix4f.mul(stateData.m_gizmoChildTransform);
                matrix4f.mul(stateData.m_gizmoTransform);
                Vector3f vector3f = matrix4f.getScale(UI3DScene.allocVector3f());
                matrix4f.scale(1.0F / vector3f.x, 1.0F / vector3f.y, 1.0F / vector3f.z);
                UI3DScene.releaseVector3f(vector3f);
                if (UI3DScene.this.m_transformMode == UI3DScene.TransformMode.Global) {
                    matrix4f.setRotationXYZ(0.0F, 0.0F, 0.0F);
                }

                stateData.m_modelView.mul(matrix4f, matrix4f);
                PZGLUtil.pushAndLoadMatrix(5888, matrix4f);
                UI3DScene.releaseMatrix4f(matrix4f);
                float float0 = UI3DScene.this.m_gizmoScale / stateData.zoomMult() * 1000.0F;
                float float1 = this.THICKNESS * float0;
                float float2 = this.LENGTH * float0;
                float float3 = 0.1F * float0;
                if (!stateData.m_translateGizmoRenderData.m_hideX) {
                    GL11.glColor3f(stateData.m_gizmoAxis == UI3DScene.Axis.X ? 1.0F : 0.5F, 0.0F, 0.0F);
                    GL11.glRotated(90.0, 0.0, 1.0, 0.0);
                    GL11.glTranslatef(0.0F, 0.0F, float3);
                    this.cylinder.draw(float1 / 2.0F, float1 / 2.0F, float2, 8, 1);
                    GL11.glTranslatef(0.0F, 0.0F, float2);
                    this.cylinder.draw(float1 / 2.0F * 2.0F, 0.0F, 0.1F * float0, 8, 1);
                    GL11.glTranslatef(0.0F, 0.0F, -float3 - float2);
                    GL11.glRotated(-90.0, 0.0, 1.0, 0.0);
                }

                if (!stateData.m_translateGizmoRenderData.m_hideY) {
                    GL11.glColor3f(0.0F, stateData.m_gizmoAxis == UI3DScene.Axis.Y ? 1.0F : 0.5F, 0.0F);
                    GL11.glRotated(-90.0, 1.0, 0.0, 0.0);
                    GL11.glTranslatef(0.0F, 0.0F, float3);
                    this.cylinder.draw(float1 / 2.0F, float1 / 2.0F, float2, 8, 1);
                    GL11.glTranslatef(0.0F, 0.0F, float2);
                    this.cylinder.draw(float1 / 2.0F * 2.0F, 0.0F, 0.1F * float0, 8, 1);
                    GL11.glTranslatef(0.0F, 0.0F, -float3 - float2);
                    GL11.glRotated(90.0, 1.0, 0.0, 0.0);
                }

                if (!stateData.m_translateGizmoRenderData.m_hideZ) {
                    GL11.glColor3f(0.0F, 0.0F, stateData.m_gizmoAxis == UI3DScene.Axis.Z ? 1.0F : 0.5F);
                    GL11.glTranslatef(0.0F, 0.0F, float3);
                    this.cylinder.draw(float1 / 2.0F, float1 / 2.0F, float2, 8, 1);
                    GL11.glTranslatef(0.0F, 0.0F, float2);
                    this.cylinder.draw(float1 / 2.0F * 2.0F, 0.0F, 0.1F * float0, 8, 1);
                    GL11.glTranslatef(0.0F, 0.0F, -float3 - float2);
                }

                GL11.glColor3f(1.0F, 1.0F, 1.0F);
                PZGLUtil.popMatrix(5888);
                this.renderLineToOrigin();
            }
        }
    }

    private static final class TranslateGizmoRenderData {
        boolean m_hideX;
        boolean m_hideY;
        boolean m_hideZ;
    }

    private static final class VehicleDrawer extends TextureDraw.GenericDrawer {
        UI3DScene.SceneVehicle m_vehicle;
        UI3DScene.VehicleRenderData m_renderData;
        boolean bRendered;
        final float[] fzeroes = new float[16];
        final Vector3f paintColor = new Vector3f(0.0F, 0.5F, 0.5F);
        final Matrix4f IDENTITY = new Matrix4f();

        public void init(UI3DScene.SceneVehicle sceneVehicle, UI3DScene.VehicleRenderData vehicleRenderData) {
            this.m_vehicle = sceneVehicle;
            this.m_renderData = vehicleRenderData;
            this.bRendered = false;
        }

        @Override
        public void render() {
            for (int int0 = 0; int0 < this.m_renderData.m_models.size(); int0++) {
                GL11.glPushAttrib(1048575);
                GL11.glPushClientAttrib(-1);
                this.render(int0);
                GL11.glPopAttrib();
                GL11.glPopClientAttrib();
                Texture.lastTextureID = -1;
                SpriteRenderer.ringBuffer.restoreBoundTextures = true;
                SpriteRenderer.ringBuffer.restoreVBOs = true;
            }
        }

        private void render(int int0) {
            this.m_renderData.m_transform.set(this.m_renderData.m_transforms.get(int0));
            ModelCamera.instance.Begin();
            Model model = this.m_renderData.m_models.get(int0);
            boolean boolean0 = model.bStatic;
            if (Core.bDebug && DebugOptions.instance.ModelRenderWireframe.getValue()) {
                GL11.glPolygonMode(1032, 6913);
                GL11.glEnable(2848);
                GL11.glLineWidth(0.75F);
                Shader shader0 = ShaderManager.instance.getOrCreateShader("vehicle_wireframe", boolean0);
                if (shader0 != null) {
                    shader0.Start();
                    shader0.setTransformMatrix(this.IDENTITY.identity(), false);
                    model.Mesh.Draw(shader0);
                    shader0.End();
                }

                GL11.glDisable(2848);
                ModelCamera.instance.End();
            } else {
                Shader shader1 = model.Effect;
                if (shader1 != null && shader1.isVehicleShader()) {
                    GL11.glDepthFunc(513);
                    GL11.glDepthMask(true);
                    GL11.glDepthRange(0.0, 1.0);
                    GL11.glEnable(2929);
                    GL11.glColor3f(1.0F, 1.0F, 1.0F);
                    shader1.Start();
                    if (model.tex != null) {
                        shader1.setTexture(model.tex, "Texture0", 0);
                        GL11.glTexEnvi(8960, 8704, 7681);
                        if (this.m_vehicle.m_script.getSkinCount() > 0 && this.m_vehicle.m_script.getSkin(0).textureMask != null) {
                            Texture texture = Texture.getSharedTexture("media/textures/" + this.m_vehicle.m_script.getSkin(0).textureMask + ".png");
                            shader1.setTexture(texture, "TextureMask", 2);
                            GL11.glTexEnvi(8960, 8704, 7681);
                        }
                    }

                    shader1.setDepthBias(0.0F);
                    shader1.setAmbient(1.0F);
                    shader1.setLightingAmount(1.0F);
                    shader1.setHueShift(0.0F);
                    shader1.setTint(1.0F, 1.0F, 1.0F);
                    shader1.setAlpha(1.0F);

                    for (int int1 = 0; int1 < 5; int1++) {
                        shader1.setLight(int1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Float.NaN, 0.0F, 0.0F, 0.0F, null);
                    }

                    shader1.setTextureUninstall1(this.fzeroes);
                    shader1.setTextureUninstall2(this.fzeroes);
                    shader1.setTextureLightsEnables2(this.fzeroes);
                    shader1.setTextureDamage1Enables1(this.fzeroes);
                    shader1.setTextureDamage1Enables2(this.fzeroes);
                    shader1.setTextureDamage2Enables1(this.fzeroes);
                    shader1.setTextureDamage2Enables2(this.fzeroes);
                    shader1.setMatrixBlood1(this.fzeroes, this.fzeroes);
                    shader1.setMatrixBlood2(this.fzeroes, this.fzeroes);
                    shader1.setTextureRustA(0.0F);
                    shader1.setTexturePainColor(this.paintColor, 1.0F);
                    shader1.setTransformMatrix(this.IDENTITY.identity(), false);
                    model.Mesh.Draw(shader1);
                    shader1.End();
                } else if (shader1 != null && model.Mesh != null && model.Mesh.isReady()) {
                    GL11.glDepthFunc(513);
                    GL11.glDepthMask(true);
                    GL11.glDepthRange(0.0, 1.0);
                    GL11.glEnable(2929);
                    GL11.glColor3f(1.0F, 1.0F, 1.0F);
                    shader1.Start();
                    if (model.tex != null) {
                        shader1.setTexture(model.tex, "Texture", 0);
                    }

                    shader1.setDepthBias(0.0F);
                    shader1.setAmbient(1.0F);
                    shader1.setLightingAmount(1.0F);
                    shader1.setHueShift(0.0F);
                    shader1.setTint(1.0F, 1.0F, 1.0F);
                    shader1.setAlpha(1.0F);

                    for (int int2 = 0; int2 < 5; int2++) {
                        shader1.setLight(int2, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Float.NaN, 0.0F, 0.0F, 0.0F, null);
                    }

                    shader1.setTransformMatrix(this.IDENTITY.identity(), false);
                    model.Mesh.Draw(shader1);
                    shader1.End();
                }

                ModelCamera.instance.End();
                this.bRendered = true;
            }
        }

        @Override
        public void postRender() {
        }
    }

    private static class VehicleRenderData extends UI3DScene.SceneObjectRenderData {
        final ArrayList<Model> m_models = new ArrayList<>();
        final ArrayList<Matrix4f> m_transforms = new ArrayList<>();
        final UI3DScene.VehicleDrawer m_drawer = new UI3DScene.VehicleDrawer();
        private static final ObjectPool<UI3DScene.VehicleRenderData> s_pool = new ObjectPool<>(UI3DScene.VehicleRenderData::new);

        UI3DScene.SceneObjectRenderData initVehicle(UI3DScene.SceneVehicle sceneVehicle) {
            super.init(sceneVehicle);
            this.m_models.clear();
            BaseVehicle.Matrix4fObjectPool matrix4fObjectPool = BaseVehicle.TL_matrix4f_pool.get();
            matrix4fObjectPool.release(this.m_transforms);
            this.m_transforms.clear();
            VehicleScript vehicleScript = sceneVehicle.m_script;
            if (vehicleScript.getModel() == null) {
                return null;
            } else {
                this.initVehicleModel(sceneVehicle);
                float float0 = vehicleScript.getModelScale();
                Vector3f vector3f = vehicleScript.getModel().getOffset();
                Matrix4f matrix4f = UI3DScene.allocMatrix4f();
                matrix4f.translationRotateScale(vector3f.x * 1.0F, vector3f.y, vector3f.z, 0.0F, 0.0F, 0.0F, 1.0F, float0);
                this.m_transform.mul(matrix4f, matrix4f);

                for (int int0 = 0; int0 < vehicleScript.getPartCount(); int0++) {
                    VehicleScript.Part part = vehicleScript.getPart(int0);
                    if (part.wheel != null) {
                        this.initWheelModel(sceneVehicle, part, matrix4f);
                    }
                }

                UI3DScene.releaseMatrix4f(matrix4f);
                this.m_drawer.init(sceneVehicle, this);
                return this;
            }
        }

        private void initVehicleModel(UI3DScene.SceneVehicle sceneVehicle) {
            VehicleScript vehicleScript = sceneVehicle.m_script;
            float float0 = vehicleScript.getModelScale();
            float float1 = 1.0F;
            ModelScript modelScript = ScriptManager.instance.getModelScript(vehicleScript.getModel().file);
            if (modelScript != null && modelScript.scale != 1.0F) {
                float1 = modelScript.scale;
            }

            float float2 = 1.0F;
            if (modelScript != null) {
                float2 = modelScript.invertX ? -1.0F : 1.0F;
            }

            float2 *= -1.0F;
            Quaternionf quaternionf = UI3DScene.allocQuaternionf();
            Matrix4f matrix4f = UI3DScene.allocMatrix4f();
            Vector3f vector3f0 = vehicleScript.getModel().getRotate();
            quaternionf.rotationXYZ(vector3f0.x * (float) (Math.PI / 180.0), vector3f0.y * (float) (Math.PI / 180.0), vector3f0.z * (float) (Math.PI / 180.0));
            Vector3f vector3f1 = vehicleScript.getModel().getOffset();
            matrix4f.translationRotateScale(
                vector3f1.x * 1.0F,
                vector3f1.y,
                vector3f1.z,
                quaternionf.x,
                quaternionf.y,
                quaternionf.z,
                quaternionf.w,
                float0 * float1 * float2,
                float0 * float1,
                float0 * float1
            );
            if (sceneVehicle.m_model.Mesh != null && sceneVehicle.m_model.Mesh.isReady() && sceneVehicle.m_model.Mesh.m_transform != null) {
                sceneVehicle.m_model.Mesh.m_transform.transpose();
                matrix4f.mul(sceneVehicle.m_model.Mesh.m_transform);
                sceneVehicle.m_model.Mesh.m_transform.transpose();
            }

            this.m_transform.mul(matrix4f, matrix4f);
            UI3DScene.releaseQuaternionf(quaternionf);
            this.m_models.add(sceneVehicle.m_model);
            this.m_transforms.add(matrix4f);
        }

        private void initWheelModel(UI3DScene.SceneVehicle sceneVehicle, VehicleScript.Part part, Matrix4f matrix4f2) {
            VehicleScript vehicleScript = sceneVehicle.m_script;
            float float0 = vehicleScript.getModelScale();
            VehicleScript.Wheel wheel = vehicleScript.getWheelById(part.wheel);
            if (wheel != null && !part.models.isEmpty()) {
                VehicleScript.Model model0 = part.models.get(0);
                Vector3f vector3f0 = model0.getOffset();
                Vector3f vector3f1 = model0.getRotate();
                Model model1 = ModelManager.instance.getLoadedModel(model0.file);
                if (model1 != null) {
                    float float1 = model0.scale;
                    float float2 = 1.0F;
                    float float3 = 1.0F;
                    ModelScript modelScript = ScriptManager.instance.getModelScript(model0.file);
                    if (modelScript != null) {
                        float2 = modelScript.scale;
                        float3 = modelScript.invertX ? -1.0F : 1.0F;
                    }

                    Quaternionf quaternionf = UI3DScene.allocQuaternionf();
                    quaternionf.rotationXYZ(
                        vector3f1.x * (float) (Math.PI / 180.0), vector3f1.y * (float) (Math.PI / 180.0), vector3f1.z * (float) (Math.PI / 180.0)
                    );
                    Matrix4f matrix4f0 = UI3DScene.allocMatrix4f();
                    matrix4f0.translation(wheel.offset.x / float0 * 1.0F, wheel.offset.y / float0, wheel.offset.z / float0);
                    Matrix4f matrix4f1 = UI3DScene.allocMatrix4f();
                    matrix4f1.translationRotateScale(
                        vector3f0.x * 1.0F,
                        vector3f0.y,
                        vector3f0.z,
                        quaternionf.x,
                        quaternionf.y,
                        quaternionf.z,
                        quaternionf.w,
                        float1 * float2 * float3,
                        float1 * float2,
                        float1 * float2
                    );
                    matrix4f0.mul(matrix4f1);
                    UI3DScene.releaseMatrix4f(matrix4f1);
                    matrix4f2.mul(matrix4f0, matrix4f0);
                    if (model1.Mesh != null && model1.Mesh.isReady() && model1.Mesh.m_transform != null) {
                        model1.Mesh.m_transform.transpose();
                        matrix4f0.mul(model1.Mesh.m_transform);
                        model1.Mesh.m_transform.transpose();
                    }

                    UI3DScene.releaseQuaternionf(quaternionf);
                    this.m_models.add(model1);
                    this.m_transforms.add(matrix4f0);
                }
            }
        }

        @Override
        void release() {
            s_pool.release(this);
        }
    }

    private final class VehicleSceneModelCamera extends UI3DScene.SceneModelCamera {
        @Override
        public void Begin() {
            UI3DScene.StateData stateData = UI3DScene.this.stateDataRender();
            GL11.glViewport(
                UI3DScene.this.getAbsoluteX().intValue(),
                Core.getInstance().getScreenHeight() - UI3DScene.this.getAbsoluteY().intValue() - UI3DScene.this.getHeight().intValue(),
                UI3DScene.this.getWidth().intValue(),
                UI3DScene.this.getHeight().intValue()
            );
            PZGLUtil.pushAndLoadMatrix(5889, stateData.m_projection);
            Matrix4f matrix4f = UI3DScene.allocMatrix4f();
            matrix4f.set(stateData.m_modelView);
            matrix4f.mul(this.m_renderData.m_transform);
            PZGLUtil.pushAndLoadMatrix(5888, matrix4f);
            UI3DScene.releaseMatrix4f(matrix4f);
            GL11.glDepthRange(0.0, 1.0);
            GL11.glDepthMask(true);
        }

        @Override
        public void End() {
            PZGLUtil.popMatrix(5889);
            PZGLUtil.popMatrix(5888);
        }
    }

    private static enum View {
        Left,
        Right,
        Top,
        Bottom,
        Front,
        Back,
        UserDefined;
    }
}
