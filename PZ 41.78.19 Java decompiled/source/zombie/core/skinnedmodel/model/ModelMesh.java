// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import zombie.core.skinnedmodel.model.jassimp.JAssImpImporter;
import zombie.core.skinnedmodel.model.jassimp.ProcessedAiScene;
import zombie.core.skinnedmodel.shader.Shader;

/**
 * Created by LEMMYATI on 03/01/14.
 */
public final class ModelMesh extends Asset {
    public VertexBufferObject vb;
    public final Vector3f minXYZ = new Vector3f(Float.MAX_VALUE);
    public final Vector3f maxXYZ = new Vector3f(-Float.MAX_VALUE);
    public SkinningData skinningData;
    public SoftwareModelMesh softwareMesh;
    public ModelMesh.MeshAssetParams assetParams;
    public Matrix4f m_transform;
    public boolean m_bHasVBO = false;
    protected boolean bStatic;
    public ModelMesh m_animationsMesh;
    public String m_fullPath;
    public static final AssetType ASSET_TYPE = new AssetType("Mesh");

    public ModelMesh(AssetPath path, AssetManager manager, ModelMesh.MeshAssetParams params) {
        super(path, manager);
        this.assetParams = params;
        this.bStatic = this.assetParams != null && this.assetParams.bStatic;
        this.m_animationsMesh = this.assetParams == null ? null : this.assetParams.animationsMesh;
    }

    protected void onLoadedX(ProcessedAiScene processedAiScene) {
        JAssImpImporter.LoadMode loadMode = this.assetParams.bStatic ? JAssImpImporter.LoadMode.StaticMesh : JAssImpImporter.LoadMode.Normal;
        SkinningData skinningDatax = this.assetParams.animationsMesh == null ? null : this.assetParams.animationsMesh.skinningData;
        processedAiScene.applyToMesh(this, loadMode, false, skinningDatax);
    }

    protected void onLoadedTxt(ModelTxt modelTxt) {
        SkinningData skinningDatax = this.assetParams.animationsMesh == null ? null : this.assetParams.animationsMesh.skinningData;
        ModelLoader.instance.applyToMesh(modelTxt, this, skinningDatax);
    }

    public void SetVertexBuffer(VertexBufferObject _vb) {
        this.clear();
        this.vb = _vb;
        this.bStatic = _vb == null || _vb.bStatic;
    }

    public void Draw(Shader shader) {
        if (this.vb != null) {
            this.vb.Draw(shader);
        }
    }

    @Override
    public void onBeforeReady() {
        super.onBeforeReady();
        if (this.assetParams != null) {
            this.assetParams.animationsMesh = null;
            this.assetParams = null;
        }
    }

    @Override
    public boolean isReady() {
        return super.isReady() && (!this.m_bHasVBO || this.vb != null);
    }

    @Override
    public void setAssetParams(AssetManager.AssetParams params) {
        this.assetParams = (ModelMesh.MeshAssetParams)params;
    }

    @Override
    public AssetType getType() {
        return ASSET_TYPE;
    }

    public void clear() {
        if (this.vb != null) {
            this.vb.clear();
            this.vb = null;
        }
    }

    public static final class MeshAssetParams extends AssetManager.AssetParams {
        public boolean bStatic;
        public ModelMesh animationsMesh;
    }
}
