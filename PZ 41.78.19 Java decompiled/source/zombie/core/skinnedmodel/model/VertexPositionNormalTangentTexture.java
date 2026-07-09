// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import java.nio.ByteBuffer;
import zombie.core.skinnedmodel.Vector3;
import zombie.iso.Vector2;

/**
 * Created by LEMMYATI on 09/03/14.
 */
public final class VertexPositionNormalTangentTexture {
    public Vector3 Position;
    public Vector3 Normal;
    public Vector3 Tangent;
    public Vector2 TextureCoordinates;

    public VertexPositionNormalTangentTexture(Vector3 position, Vector3 normal, Vector3 tangent, Vector2 uv) {
        this.Position = position;
        this.Normal = normal;
        this.Tangent = tangent;
        this.TextureCoordinates = uv;
    }

    public VertexPositionNormalTangentTexture() {
        this.Position = new Vector3(0.0F, 0.0F, 0.0F);
        this.Normal = new Vector3(0.0F, 0.0F, 1.0F);
        this.Tangent = new Vector3(0.0F, 1.0F, 0.0F);
        this.TextureCoordinates = new Vector2(0.0F, 0.0F);
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
    }
}
