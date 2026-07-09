// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import zombie.core.Color;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.core.skinnedmodel.Vector3;
import zombie.iso.Vector2;

public class VertexDefinitions {
    class VertexPositionColour {
        public Vector3 Position;
        public int Colour;

        public VertexPositionColour(Vector3 vector3, Color color) {
            this.Position = vector3;
            this.Colour = HelperFunctions.ToRgba(color);
        }

        public VertexPositionColour(float float0, float float1, float float2, Color color) {
            this.Position = new Vector3(float0, float1, float2);
            this.Colour = HelperFunctions.ToRgba(color);
        }
    }

    class VertexPositionNormal {
        public Vector3 Position;
        public Vector3 Normal;

        public VertexPositionNormal(Vector3 vector30, Vector3 vector31, Vector2 var4) {
            this.Position = vector30;
            this.Normal = vector31;
        }

        public VertexPositionNormal(float float0, float float1, float float2, float float3, float float4, float float5) {
            this.Position = new Vector3(float0, float1, float2);
            this.Normal = new Vector3(float3, float4, float5);
        }
    }

    class VertexPositionNormalTangentTexture {
        public Vector3 Position;
        public Vector3 Normal;
        public Vector3 Tangent;
        public Vector2 TextureCoordinates;

        public VertexPositionNormalTangentTexture(Vector3 vector30, Vector3 vector31, Vector3 vector32, Vector2 vector) {
            this.Position = vector30;
            this.Normal = vector31;
            this.Tangent = vector32;
            this.TextureCoordinates = vector;
        }

        public VertexPositionNormalTangentTexture(
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
            float float10
        ) {
            this.Position = new Vector3(float0, float1, float2);
            this.Normal = new Vector3(float3, float4, float5);
            this.Tangent = new Vector3(float6, float7, float8);
            this.TextureCoordinates = new Vector2(float9, float10);
        }
    }

    class VertexPositionNormalTexture {
        public Vector3 Position;
        public Vector3 Normal;
        public Vector2 TextureCoordinates;

        public VertexPositionNormalTexture(Vector3 vector30, Vector3 vector31, Vector2 vector) {
            this.Position = vector30;
            this.Normal = vector31;
            this.TextureCoordinates = vector;
        }

        public VertexPositionNormalTexture(float float0, float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
            this.Position = new Vector3(float0, float1, float2);
            this.Normal = new Vector3(float3, float4, float5);
            this.TextureCoordinates = new Vector2(float6, float7);
        }
    }
}
