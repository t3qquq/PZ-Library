// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

public final class SoftwareModelMeshInstance {
    public SoftwareModelMesh softwareMesh;
    public VertexBufferObject vb;
    public String name;

    public SoftwareModelMeshInstance(String _name, SoftwareModelMesh _softwareMesh) {
        this.name = _name;
        this.softwareMesh = _softwareMesh;
        this.vb = new VertexBufferObject();
        this.vb.elements = _softwareMesh.indicesUnskinned;
    }
}
