// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import org.joml.Vector2f;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.popman.ObjectPool;
import zombie.util.list.PZArrayList;

public final class IsoWaterGeometry {
    private static final Vector2f tempVector2f = new Vector2f();
    boolean hasWater = false;
    boolean bShore = false;
    final float[] x = new float[4];
    final float[] y = new float[4];
    final float[] depth = new float[4];
    final float[] flow = new float[4];
    final float[] speed = new float[4];
    float IsExternal = 0.0F;
    IsoGridSquare square = null;
    int m_adjacentChunkLoadedCounter;
    public static final ObjectPool<IsoWaterGeometry> pool = new ObjectPool<>(IsoWaterGeometry::new);

    public IsoWaterGeometry init(IsoGridSquare _square) throws Exception {
        this.x[0] = IsoUtils.XToScreen(_square.x, _square.y, 0.0F, 0);
        this.y[0] = IsoUtils.YToScreen(_square.x, _square.y, 0.0F, 0);
        this.x[1] = IsoUtils.XToScreen(_square.x, _square.y + 1, 0.0F, 0);
        this.y[1] = IsoUtils.YToScreen(_square.x, _square.y + 1, 0.0F, 0);
        this.x[2] = IsoUtils.XToScreen(_square.x + 1, _square.y + 1, 0.0F, 0);
        this.y[2] = IsoUtils.YToScreen(_square.x + 1, _square.y + 1, 0.0F, 0);
        this.x[3] = IsoUtils.XToScreen(_square.x + 1, _square.y, 0.0F, 0);
        this.y[3] = IsoUtils.YToScreen(_square.x + 1, _square.y, 0.0F, 0);
        this.hasWater = false;
        this.bShore = false;
        this.square = _square;
        this.IsExternal = _square.getProperties().Is(IsoFlagType.exterior) ? 1.0F : 0.0F;
        int int0 = IsoWaterFlow.getShore(_square.x, _square.y);
        IsoObject object = _square.getFloor();
        String string = object == null ? null : object.getSprite().getName();
        if (_square.getProperties().Is(IsoFlagType.water)) {
            this.hasWater = true;

            for (int int1 = 0; int1 < 4; int1++) {
                this.depth[int1] = 1.0F;
            }
        } else if (int0 == 1 && string != null && string.startsWith("blends_natural")) {
            for (int int2 = 0; int2 < 4; int2++) {
                this.depth[int2] = 0.0F;
            }

            IsoGridSquare square0 = _square.getAdjacentSquare(IsoDirections.W);
            IsoGridSquare square1 = _square.getAdjacentSquare(IsoDirections.NW);
            IsoGridSquare square2 = _square.getAdjacentSquare(IsoDirections.N);
            IsoGridSquare square3 = _square.getAdjacentSquare(IsoDirections.SW);
            IsoGridSquare square4 = _square.getAdjacentSquare(IsoDirections.S);
            IsoGridSquare square5 = _square.getAdjacentSquare(IsoDirections.SE);
            IsoGridSquare square6 = _square.getAdjacentSquare(IsoDirections.E);
            IsoGridSquare square7 = _square.getAdjacentSquare(IsoDirections.NE);
            if (square2 == null
                || square1 == null
                || square0 == null
                || square3 == null
                || square4 == null
                || square5 == null
                || square6 == null
                || square7 == null) {
                return null;
            }

            if (square0.getProperties().Is(IsoFlagType.water) || square1.getProperties().Is(IsoFlagType.water) || square2.getProperties().Is(IsoFlagType.water)
                )
             {
                this.bShore = true;
                this.depth[0] = 1.0F;
            }

            if (square0.getProperties().Is(IsoFlagType.water) || square3.getProperties().Is(IsoFlagType.water) || square4.getProperties().Is(IsoFlagType.water)
                )
             {
                this.bShore = true;
                this.depth[1] = 1.0F;
            }

            if (square4.getProperties().Is(IsoFlagType.water) || square5.getProperties().Is(IsoFlagType.water) || square6.getProperties().Is(IsoFlagType.water)
                )
             {
                this.bShore = true;
                this.depth[2] = 1.0F;
            }

            if (square6.getProperties().Is(IsoFlagType.water) || square7.getProperties().Is(IsoFlagType.water) || square2.getProperties().Is(IsoFlagType.water)
                )
             {
                this.bShore = true;
                this.depth[3] = 1.0F;
            }
        }

        Vector2f vector2f = IsoWaterFlow.getFlow(_square, 0, 0, tempVector2f);
        this.flow[0] = vector2f.x;
        this.speed[0] = vector2f.y;
        vector2f = IsoWaterFlow.getFlow(_square, 0, 1, vector2f);
        this.flow[1] = vector2f.x;
        this.speed[1] = vector2f.y;
        vector2f = IsoWaterFlow.getFlow(_square, 1, 1, vector2f);
        this.flow[2] = vector2f.x;
        this.speed[2] = vector2f.y;
        vector2f = IsoWaterFlow.getFlow(_square, 1, 0, vector2f);
        this.flow[3] = vector2f.x;
        this.speed[3] = vector2f.y;
        this.hideWaterObjects(_square);
        return this;
    }

    private void hideWaterObjects(IsoGridSquare squarex) {
        PZArrayList pZArrayList = squarex.getObjects();

        for (int int0 = 0; int0 < pZArrayList.size(); int0++) {
            IsoObject object = (IsoObject)pZArrayList.get(int0);
            if (object.sprite != null && object.sprite.name != null) {
                String string = object.sprite.name;
                if (string.startsWith("blends_natural_02")
                    && (
                        string.endsWith("_0")
                            || string.endsWith("_1")
                            || string.endsWith("_2")
                            || string.endsWith("_3")
                            || string.endsWith("_4")
                            || string.endsWith("_5")
                            || string.endsWith("_6")
                            || string.endsWith("_7")
                            || string.endsWith("_8")
                            || string.endsWith("_9")
                            || string.endsWith("_10")
                            || string.endsWith("_11")
                            || string.endsWith("_12")
                    )) {
                    object.sprite.setHideForWaterRender();
                }
            }
        }
    }

    public boolean isShore() {
        return IsoWaterFlow.getShore(this.square.x, this.square.y) == 0;
    }

    public float getFlow() {
        IsoWaterFlow.getShore(this.square.x, this.square.y);
        Vector2f vector2f = IsoWaterFlow.getFlow(this.square, 0, 0, tempVector2f);
        System.out.println("FLOW!  " + vector2f.x + " " + vector2f.y);
        return vector2f.x;
    }
}
