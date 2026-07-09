// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model.jassimp;

import jassimp.AiBone;
import jassimp.AiBoneWeight;
import jassimp.AiMesh;
import java.util.List;
import zombie.core.skinnedmodel.model.VertexBufferObject;

public final class ImportedSkinnedMesh {
    final ImportedSkeleton skeleton;
    String name;
    VertexBufferObject.VertexArray vertices = null;
    int[] elements = null;

    public ImportedSkinnedMesh(ImportedSkeleton importedSkeleton, AiMesh aiMesh) {
        this.skeleton = importedSkeleton;
        this.processAiScene(aiMesh);
    }

    private void processAiScene(AiMesh aiMesh) {
        this.name = aiMesh.getName();
        int int0 = aiMesh.getNumVertices();
        int int1 = int0 * 4;
        int[] ints = new int[int1];
        float[] floats = new float[int1];

        for (int int2 = 0; int2 < int1; int2++) {
            floats[int2] = 0.0F;
        }

        List list0 = aiMesh.getBones();
        int int3 = list0.size();

        for (int int4 = 0; int4 < int3; int4++) {
            AiBone aiBone = (AiBone)list0.get(int4);
            String string = aiBone.getName();
            int int5 = this.skeleton.boneIndices.get(string);
            List list1 = aiBone.getBoneWeights();

            for (int int6 = 0; int6 < aiBone.getNumWeights(); int6++) {
                AiBoneWeight aiBoneWeight = (AiBoneWeight)list1.get(int6);
                int int7 = aiBoneWeight.getVertexId() * 4;

                for (int int8 = 0; int8 < 4; int8++) {
                    if (floats[int7 + int8] == 0.0F) {
                        floats[int7 + int8] = aiBoneWeight.getWeight();
                        ints[int7 + int8] = int5;
                        break;
                    }
                }
            }
        }

        int int9 = getNumUVs(aiMesh);
        VertexBufferObject.VertexFormat vertexFormat = new VertexBufferObject.VertexFormat(5 + int9);
        vertexFormat.setElement(0, VertexBufferObject.VertexType.VertexArray, 12);
        vertexFormat.setElement(1, VertexBufferObject.VertexType.NormalArray, 12);
        vertexFormat.setElement(2, VertexBufferObject.VertexType.TangentArray, 12);
        vertexFormat.setElement(3, VertexBufferObject.VertexType.BlendWeightArray, 16);
        vertexFormat.setElement(4, VertexBufferObject.VertexType.BlendIndexArray, 16);

        for (int int10 = 0; int10 < int9; int10++) {
            vertexFormat.setElement(5 + int10, VertexBufferObject.VertexType.TextureCoordArray, 8);
        }

        vertexFormat.calculate();
        this.vertices = new VertexBufferObject.VertexArray(vertexFormat, int0);

        for (int int11 = 0; int11 < int0; int11++) {
            this.vertices.setElement(int11, 0, aiMesh.getPositionX(int11), aiMesh.getPositionY(int11), aiMesh.getPositionZ(int11));
            if (aiMesh.hasNormals()) {
                this.vertices.setElement(int11, 1, aiMesh.getNormalX(int11), aiMesh.getNormalY(int11), aiMesh.getNormalZ(int11));
            } else {
                this.vertices.setElement(int11, 1, 0.0F, 1.0F, 0.0F);
            }

            if (aiMesh.hasTangentsAndBitangents()) {
                this.vertices.setElement(int11, 2, aiMesh.getTangentX(int11), aiMesh.getTangentY(int11), aiMesh.getTangentZ(int11));
            } else {
                this.vertices.setElement(int11, 2, 0.0F, 0.0F, 1.0F);
            }

            this.vertices.setElement(int11, 3, floats[int11 * 4], floats[int11 * 4 + 1], floats[int11 * 4 + 2], floats[int11 * 4 + 3]);
            this.vertices.setElement(int11, 4, ints[int11 * 4], ints[int11 * 4 + 1], ints[int11 * 4 + 2], ints[int11 * 4 + 3]);
            if (int9 > 0) {
                int int12 = 0;

                for (int int13 = 0; int13 < 8; int13++) {
                    if (aiMesh.hasTexCoords(int13)) {
                        this.vertices.setElement(int11, 5 + int12, aiMesh.getTexCoordU(int11, int13), 1.0F - aiMesh.getTexCoordV(int11, int13));
                        int12++;
                    }
                }
            }
        }

        int int14 = aiMesh.getNumFaces();
        this.elements = new int[int14 * 3];

        for (int int15 = 0; int15 < int14; int15++) {
            this.elements[int15 * 3 + 2] = aiMesh.getFaceVertex(int15, 0);
            this.elements[int15 * 3 + 1] = aiMesh.getFaceVertex(int15, 1);
            this.elements[int15 * 3 + 0] = aiMesh.getFaceVertex(int15, 2);
        }
    }

    private static int getNumUVs(AiMesh aiMesh) {
        int int0 = 0;

        for (int int1 = 0; int1 < 8; int1++) {
            if (aiMesh.hasTexCoords(int1)) {
                int0++;
            }
        }

        return int0;
    }
}
