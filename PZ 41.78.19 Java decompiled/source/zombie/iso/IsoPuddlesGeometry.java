// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.popman.ObjectPool;

public final class IsoPuddlesGeometry {
    final float[] x = new float[4];
    final float[] y = new float[4];
    final float[] pdne = new float[4];
    final float[] pdnw = new float[4];
    final float[] pda = new float[4];
    final float[] pnon = new float[4];
    final int[] color = new int[4];
    IsoGridSquare square = null;
    boolean bRecalc = true;
    private boolean interiorCalc = false;
    public static final ObjectPool<IsoPuddlesGeometry> pool = new ObjectPool<>(IsoPuddlesGeometry::new);

    public IsoPuddlesGeometry init(IsoGridSquare _square) {
        this.interiorCalc = false;
        this.x[0] = IsoUtils.XToScreen(_square.x - _square.z * 3, _square.y - _square.z * 3, _square.z, _square.z);
        this.y[0] = IsoUtils.YToScreen(_square.x - _square.z * 3, _square.y - _square.z * 3, _square.z, _square.z);
        this.x[1] = IsoUtils.XToScreen(_square.x - _square.z * 3, _square.y - _square.z * 3 + 1, 0.0F, 0);
        this.y[1] = IsoUtils.YToScreen(_square.x - _square.z * 3, _square.y - _square.z * 3 + 1, 0.0F, 0);
        this.x[2] = IsoUtils.XToScreen(_square.x - _square.z * 3 + 1, _square.y - _square.z * 3 + 1, 0.0F, 0);
        this.y[2] = IsoUtils.YToScreen(_square.x - _square.z * 3 + 1, _square.y - _square.z * 3 + 1, 0.0F, 0);
        this.x[3] = IsoUtils.XToScreen(_square.x - _square.z * 3 + 1, _square.y - _square.z * 3, 0.0F, 0);
        this.y[3] = IsoUtils.YToScreen(_square.x - _square.z * 3 + 1, _square.y - _square.z * 3, 0.0F, 0);
        this.square = _square;
        if (!_square.getProperties().Is(IsoFlagType.water) && _square.getProperties().Is(IsoFlagType.exterior)) {
            for (int int0 = 0; int0 < 4; int0++) {
                this.pdne[int0] = 0.0F;
                this.pdnw[int0] = 0.0F;
                this.pda[int0] = 1.0F;
                this.pnon[int0] = 0.0F;
            }

            if (Core.getInstance().getPerfPuddles() > 1) {
                return this;
            } else {
                IsoCell cell = _square.getCell();
                IsoGridSquare square0 = cell.getGridSquare(_square.x - 1, _square.y, _square.z);
                IsoGridSquare square1 = cell.getGridSquare(_square.x - 1, _square.y - 1, _square.z);
                IsoGridSquare square2 = cell.getGridSquare(_square.x, _square.y - 1, _square.z);
                IsoGridSquare square3 = cell.getGridSquare(_square.x - 1, _square.y + 1, _square.z);
                IsoGridSquare square4 = cell.getGridSquare(_square.x, _square.y + 1, _square.z);
                IsoGridSquare square5 = cell.getGridSquare(_square.x + 1, _square.y + 1, _square.z);
                IsoGridSquare square6 = cell.getGridSquare(_square.x + 1, _square.y, _square.z);
                IsoGridSquare square7 = cell.getGridSquare(_square.x + 1, _square.y - 1, _square.z);
                if (square2 != null
                    && square1 != null
                    && square0 != null
                    && square3 != null
                    && square4 != null
                    && square5 != null
                    && square6 != null
                    && square7 != null) {
                    this.setFlags(0, square0.getPuddlesDir() | square1.getPuddlesDir() | square2.getPuddlesDir());
                    this.setFlags(1, square0.getPuddlesDir() | square3.getPuddlesDir() | square4.getPuddlesDir());
                    this.setFlags(2, square4.getPuddlesDir() | square5.getPuddlesDir() | square6.getPuddlesDir());
                    this.setFlags(3, square6.getPuddlesDir() | square7.getPuddlesDir() | square2.getPuddlesDir());
                    return this;
                } else {
                    return this;
                }
            }
        } else {
            for (int int1 = 0; int1 < 4; int1++) {
                this.pdne[int1] = 0.0F;
                this.pdnw[int1] = 0.0F;
                this.pda[int1] = 0.0F;
                this.pnon[int1] = 0.0F;
            }

            return this;
        }
    }

    private void setFlags(int int0, int int1) {
        this.pdne[int0] = 0.0F;
        this.pdnw[int0] = 0.0F;
        this.pda[int0] = 0.0F;
        this.pnon[int0] = 0.0F;
        if ((int1 & IsoGridSquare.PuddlesDirection.PUDDLES_DIR_NE) != 0) {
            this.pdne[int0] = 1.0F;
        }

        if ((int1 & IsoGridSquare.PuddlesDirection.PUDDLES_DIR_NW) != 0) {
            this.pdnw[int0] = 1.0F;
        }

        if ((int1 & IsoGridSquare.PuddlesDirection.PUDDLES_DIR_ALL) != 0) {
            this.pda[int0] = 1.0F;
        }
    }

    public void recalcIfNeeded() {
        if (this.bRecalc) {
            this.bRecalc = false;

            try {
                this.init(this.square);
            } catch (Throwable throwable) {
                ExceptionLogger.logException(throwable);
            }
        }
    }

    public boolean shouldRender() {
        this.recalcIfNeeded();

        for (int int0 = 0; int0 < 4; int0++) {
            if (this.pdne[int0] + this.pdnw[int0] + this.pda[int0] + this.pnon[int0] > 0.0F) {
                return true;
            }
        }

        if (this.square.getProperties().Is(IsoFlagType.water)) {
            return false;
        } else {
            if (IsoPuddles.leakingPuddlesInTheRoom && !this.interiorCalc && this.square != null) {
                for (int int1 = 0; int1 < 4; int1++) {
                    this.pdne[int1] = 0.0F;
                    this.pdnw[int1] = 0.0F;
                    this.pda[int1] = 0.0F;
                    this.pnon[int1] = 1.0F;
                }

                IsoGridSquare square0 = this.square.getAdjacentSquare(IsoDirections.W);
                IsoGridSquare square1 = this.square.getAdjacentSquare(IsoDirections.NW);
                IsoGridSquare square2 = this.square.getAdjacentSquare(IsoDirections.N);
                IsoGridSquare square3 = this.square.getAdjacentSquare(IsoDirections.SW);
                IsoGridSquare square4 = this.square.getAdjacentSquare(IsoDirections.S);
                IsoGridSquare square5 = this.square.getAdjacentSquare(IsoDirections.SE);
                IsoGridSquare square6 = this.square.getAdjacentSquare(IsoDirections.E);
                IsoGridSquare square7 = this.square.getAdjacentSquare(IsoDirections.NE);
                if (square0 == null
                    || square2 == null
                    || square4 == null
                    || square6 == null
                    || square1 == null
                    || square7 == null
                    || square3 == null
                    || square5 == null
                    || !square0.getProperties().Is(IsoFlagType.exterior)
                        && !square2.getProperties().Is(IsoFlagType.exterior)
                        && !square4.getProperties().Is(IsoFlagType.exterior)
                        && !square6.getProperties().Is(IsoFlagType.exterior)) {
                    return false;
                }

                if (!this.square.getProperties().Is(IsoFlagType.collideW) && square0.getProperties().Is(IsoFlagType.exterior)) {
                    this.pnon[0] = 0.0F;
                    this.pnon[1] = 0.0F;

                    for (int int2 = 0; int2 < 4; int2++) {
                        this.pda[int2] = 1.0F;
                    }
                }

                if (!square4.getProperties().Is(IsoFlagType.collideN) && square4.getProperties().Is(IsoFlagType.exterior)) {
                    this.pnon[1] = 0.0F;
                    this.pnon[2] = 0.0F;

                    for (int int3 = 0; int3 < 4; int3++) {
                        this.pda[int3] = 1.0F;
                    }
                }

                if (!square6.getProperties().Is(IsoFlagType.collideW) && square6.getProperties().Is(IsoFlagType.exterior)) {
                    this.pnon[2] = 0.0F;
                    this.pnon[3] = 0.0F;

                    for (int int4 = 0; int4 < 4; int4++) {
                        this.pda[int4] = 1.0F;
                    }
                }

                if (!this.square.getProperties().Is(IsoFlagType.collideN) && square2.getProperties().Is(IsoFlagType.exterior)) {
                    this.pnon[3] = 0.0F;
                    this.pnon[0] = 0.0F;

                    for (int int5 = 0; int5 < 4; int5++) {
                        this.pda[int5] = 1.0F;
                    }
                }

                if (square2.getProperties().Is(IsoFlagType.collideW) || !square1.getProperties().Is(IsoFlagType.exterior)) {
                    this.pnon[0] = 1.0F;

                    for (int int6 = 0; int6 < 4; int6++) {
                        this.pda[int6] = 1.0F;
                    }
                }

                if (square4.getProperties().Is(IsoFlagType.collideW) || !square3.getProperties().Is(IsoFlagType.exterior)) {
                    this.pnon[1] = 1.0F;

                    for (int int7 = 0; int7 < 4; int7++) {
                        this.pda[int7] = 1.0F;
                    }
                }

                if (square3.getProperties().Is(IsoFlagType.collideN) || !square3.getProperties().Is(IsoFlagType.exterior)) {
                    this.pnon[1] = 1.0F;

                    for (int int8 = 0; int8 < 4; int8++) {
                        this.pda[int8] = 1.0F;
                    }
                }

                if (square5.getProperties().Is(IsoFlagType.collideN) || !square5.getProperties().Is(IsoFlagType.exterior)) {
                    this.pnon[2] = 1.0F;

                    for (int int9 = 0; int9 < 4; int9++) {
                        this.pda[int9] = 1.0F;
                    }
                }

                if (square5.getProperties().Is(IsoFlagType.collideW) || !square5.getProperties().Is(IsoFlagType.exterior)) {
                    this.pnon[2] = 1.0F;

                    for (int int10 = 0; int10 < 4; int10++) {
                        this.pda[int10] = 1.0F;
                    }
                }

                if (square7.getProperties().Is(IsoFlagType.collideW) || !square7.getProperties().Is(IsoFlagType.exterior)) {
                    this.pnon[3] = 1.0F;

                    for (int int11 = 0; int11 < 4; int11++) {
                        this.pda[int11] = 1.0F;
                    }
                }

                if (square6.getProperties().Is(IsoFlagType.collideN) || !square7.getProperties().Is(IsoFlagType.exterior)) {
                    this.pnon[3] = 1.0F;

                    for (int int12 = 0; int12 < 4; int12++) {
                        this.pda[int12] = 1.0F;
                    }
                }

                if (square0.getProperties().Is(IsoFlagType.collideN) || !square1.getProperties().Is(IsoFlagType.exterior)) {
                    this.pnon[0] = 1.0F;

                    for (int int13 = 0; int13 < 4; int13++) {
                        this.pda[int13] = 1.0F;
                    }
                }

                this.interiorCalc = true;

                for (int int14 = 0; int14 < 4; int14++) {
                    if (this.pdne[int14] + this.pdnw[int14] + this.pda[int14] + this.pnon[int14] > 0.0F) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public void updateLighting(int playerIndex) {
        this.setLightingAtVert(0, this.square.getVertLight(0, playerIndex));
        this.setLightingAtVert(1, this.square.getVertLight(3, playerIndex));
        this.setLightingAtVert(2, this.square.getVertLight(2, playerIndex));
        this.setLightingAtVert(3, this.square.getVertLight(1, playerIndex));
    }

    private void setLightingAtVert(int int1, int int0) {
        this.color[int1] = int0;
    }
}
