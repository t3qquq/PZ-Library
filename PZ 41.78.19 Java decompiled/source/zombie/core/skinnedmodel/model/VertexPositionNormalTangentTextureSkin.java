// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import java.nio.ByteBuffer;
import zombie.core.skinnedmodel.Vector3;
import zombie.core.skinnedmodel.Vector4;
import zombie.iso.Vector2;

/**
 * Created by LEMMYATI on 03/01/14.
 */
public final class VertexPositionNormalTangentTextureSkin {
    public Vector3 Position;
    public Vector3 Normal;
    public Vector3 Tangent;
    public Vector2 TextureCoordinates;
    public Vector4 BlendWeights;
    public UInt4 BlendIndices;

    public VertexPositionNormalTangentTextureSkin() {
    }

    public VertexPositionNormalTangentTextureSkin(Vector3 position, Vector3 normal, Vector3 tangent, Vector2 uv, Vector4 blendweights, UInt4 blendIndices) {
        this.Position = position;
        this.Normal = normal;
        this.Tangent = tangent;
        this.TextureCoordinates = uv;
        this.BlendWeights = blendweights;
        this.BlendIndices = blendIndices;
    }

    public void put(ByteBuffer buf) {
        buf.putFloat(this.Position.x());
        buf.putFloat(this.Position.y());
        buf.putFloat(this.Position.z());
        buf.putFloat(this.Normal.x());
        buf.putFloat(this.Normal.y());
        buf.putFloat(this.Normal.z());
        buf.putFloat(this.Tangent.x());
        buf.putFloat(this.Tangent.y());
        buf.putFloat(this.Tangent.z());
        buf.putFloat(this.TextureCoordinates.x);
        buf.putFloat(this.TextureCoordinates.y);
        buf.putFloat(this.BlendWeights.x);
        buf.putFloat(this.BlendWeights.y);
        buf.putFloat(this.BlendWeights.z);
        buf.putFloat(this.BlendWeights.w);
        buf.putFloat(this.BlendIndices.X);
        buf.putFloat(this.BlendIndices.Y);
        buf.putFloat(this.BlendIndices.Z);
        buf.putFloat(this.BlendIndices.W);
    }
}
