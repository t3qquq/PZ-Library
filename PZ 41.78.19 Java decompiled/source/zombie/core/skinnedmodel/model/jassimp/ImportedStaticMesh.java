// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model.jassimp;

import jassimp.AiMesh;
import org.joml.Vector3f;
import zombie.core.skinnedmodel.model.VertexBufferObject;

public final class ImportedStaticMesh {
    VertexBufferObject.VertexArray verticesUnskinned = null;
    int[] elements = null;
    final Vector3f minXYZ = new Vector3f(Float.MAX_VALUE);
    final Vector3f maxXYZ = new Vector3f(-Float.MAX_VALUE);

    public ImportedStaticMesh(AiMesh aiMesh) {
        this.processAiScene(aiMesh);
    }

    private void processAiScene(AiMesh aiMesh) {
        int int0 = aiMesh.getNumVertices();
        int int1 = 0;

        for (int int2 = 0; int2 < 8; int2++) {
            if (aiMesh.hasTexCoords(int2)) {
                int1++;
            }
        }

        VertexBufferObject.VertexFormat vertexFormat = new VertexBufferObject.VertexFormat(3 + int1);
        vertexFormat.setElement(0, VertexBufferObject.VertexType.VertexArray, 12);
        vertexFormat.setElement(1, VertexBufferObject.VertexType.NormalArray, 12);
        vertexFormat.setElement(2, VertexBufferObject.VertexType.TangentArray, 12);

        for (int int3 = 0; int3 < int1; int3++) {
            vertexFormat.setElement(3 + int3, VertexBufferObject.VertexType.TextureCoordArray, 8);
        }

        vertexFormat.calculate();
        this.verticesUnskinned = new VertexBufferObject.VertexArray(vertexFormat, int0);
        Vector3f vector3f = new Vector3f();

        for (int int4 = 0; int4 < int0; int4++) {
            float float0 = aiMesh.getPositionX(int4);
            float float1 = aiMesh.getPositionY(int4);
            float float2 = aiMesh.getPositionZ(int4);
            this.minXYZ.min(vector3f.set(float0, float1, float2));
            this.maxXYZ.max(vector3f.set(float0, float1, float2));
            this.verticesUnskinned.setElement(int4, 0, aiMesh.getPositionX(int4), aiMesh.getPositionY(int4), aiMesh.getPositionZ(int4));
            if (aiMesh.hasNormals()) {
                this.verticesUnskinned.setElement(int4, 1, aiMesh.getNormalX(int4), aiMesh.getNormalY(int4), aiMesh.getNormalZ(int4));
            } else {
                this.verticesUnskinned.setElement(int4, 1, 0.0F, 1.0F, 0.0F);
            }

            if (aiMesh.hasTangentsAndBitangents()) {
                this.verticesUnskinned.setElement(int4, 2, aiMesh.getTangentX(int4), aiMesh.getTangentY(int4), aiMesh.getTangentZ(int4));
            } else {
                this.verticesUnskinned.setElement(int4, 2, 0.0F, 0.0F, 1.0F);
            }

            if (int1 > 0) {
                int int5 = 0;

                for (int int6 = 0; int6 < 8; int6++) {
                    if (aiMesh.hasTexCoords(int6)) {
                        this.verticesUnskinned.setElement(int4, 3 + int5, aiMesh.getTexCoordU(int4, int6), 1.0F - aiMesh.getTexCoordV(int4, int6));
                        int5++;
                    }
                }
            }
        }

        int int7 = aiMesh.getNumFaces();
        this.elements = new int[int7 * 3];

        for (int int8 = 0; int8 < int7; int8++) {
            this.elements[int8 * 3 + 2] = aiMesh.getFaceVertex(int8, 0);
            this.elements[int8 * 3 + 1] = aiMesh.getFaceVertex(int8, 1);
            this.elements[int8 * 3 + 0] = aiMesh.getFaceVertex(int8, 2);
        }
    }
}
