// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import zombie.core.skinnedmodel.Vector3;
import zombie.iso.Vector2;

public final class SoftwareModelMesh {
    public int[] indicesUnskinned;
    public VertexPositionNormalTangentTextureSkin[] verticesUnskinned;
    public String Texture;
    public VertexBufferObject vb;

    public SoftwareModelMesh(VertexPositionNormalTangentTextureSkin[] vertexPositionNormalTangentTextureSkins, int[] ints) {
        this.indicesUnskinned = ints;
        this.verticesUnskinned = vertexPositionNormalTangentTextureSkins;
    }

    public SoftwareModelMesh(VertexPositionNormalTangentTexture[] vertexPositionNormalTangentTextures, int[] ints) {
        this.indicesUnskinned = ints;
        this.verticesUnskinned = new VertexPositionNormalTangentTextureSkin[vertexPositionNormalTangentTextures.length];

        for (int int0 = 0; int0 < vertexPositionNormalTangentTextures.length; int0++) {
            VertexPositionNormalTangentTexture vertexPositionNormalTangentTexture = vertexPositionNormalTangentTextures[int0];
            this.verticesUnskinned[int0] = new VertexPositionNormalTangentTextureSkin();
            this.verticesUnskinned[int0].Position = new Vector3(
                vertexPositionNormalTangentTexture.Position.x(),
                vertexPositionNormalTangentTexture.Position.y(),
                vertexPositionNormalTangentTexture.Position.z()
            );
            this.verticesUnskinned[int0].Normal = new Vector3(
                vertexPositionNormalTangentTexture.Normal.x(), vertexPositionNormalTangentTexture.Normal.y(), vertexPositionNormalTangentTexture.Normal.z()
            );
            this.verticesUnskinned[int0].TextureCoordinates = new Vector2(
                vertexPositionNormalTangentTexture.TextureCoordinates.x, vertexPositionNormalTangentTexture.TextureCoordinates.y
            );
        }
    }
}
