// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import gnu.trove.set.hash.THashSet;
import java.util.ArrayList;
import zombie.iso.areas.IsoBuilding;

/**
 * Created by ChrisWood (Tanglewood Games Limited) on 09/10/2017.
 */
public class IsoGridOcclusionData {
    public static final int MAXBUILDINGOCCLUDERS = 3;
    private static final THashSet<IsoBuilding> _leftBuildings = new THashSet<>(3);
    private static final THashSet<IsoBuilding> _rightBuildings = new THashSet<>(3);
    private static final THashSet<IsoBuilding> _allBuildings = new THashSet<>(3);
    private static int _ObjectEpoch = 0;
    private final ArrayList<IsoBuilding> _leftBuildingsArray = new ArrayList<>(3);
    private final ArrayList<IsoBuilding> _rightBuildingsArray = new ArrayList<>(3);
    private final ArrayList<IsoBuilding> _allBuildingsArray = new ArrayList<>(3);
    private IsoGridSquare _ownerSquare = null;
    private boolean _bSoftInitialized = false;
    private boolean _bLeftOccludedByOrphanStructures = false;
    private boolean _bRightOccludedByOrphanStructures = false;
    private int _objectEpoch = -1;

    public IsoGridOcclusionData(IsoGridSquare inOwnerSquare) {
        this._ownerSquare = inOwnerSquare;
    }

    public static void SquareChanged() {
        _ObjectEpoch++;
        if (_ObjectEpoch < 0) {
            _ObjectEpoch = 0;
        }
    }

    public void Reset() {
        this._bSoftInitialized = false;
        this._bLeftOccludedByOrphanStructures = false;
        this._bRightOccludedByOrphanStructures = false;
        this._allBuildingsArray.clear();
        this._leftBuildingsArray.clear();
        this._rightBuildingsArray.clear();
        this._objectEpoch = -1;
    }

    /**
     * Returns whether built structures with no building id (orphans) could occlude some of the square.  Depending on the exact shape of the structures, the square might not be hidden at all.  This is used to hide player-built structures that might block our view of something in a square (at ground  level)
     */
    public boolean getCouldBeOccludedByOrphanStructures(IsoGridOcclusionData.OcclusionFilter filter) {
        if (this._objectEpoch != _ObjectEpoch) {
            if (this._bSoftInitialized) {
                this.Reset();
            }

            this._objectEpoch = _ObjectEpoch;
        }

        if (!this._bSoftInitialized) {
            this.LazyInitializeSoftOccluders();
        }

        if (filter == IsoGridOcclusionData.OcclusionFilter.Left) {
            return this._bLeftOccludedByOrphanStructures;
        } else {
            return filter == IsoGridOcclusionData.OcclusionFilter.Right
                ? this._bRightOccludedByOrphanStructures
                : this._bLeftOccludedByOrphanStructures || this._bRightOccludedByOrphanStructures;
        }
    }

    /**
     * Returns buildings that could occlude some of the square.  Depending on the exact shape of the building, the square might not be hidden at all.  This is used to hide buildings that might block our view of something in a square (at ground level)
     */
    public ArrayList<IsoBuilding> getBuildingsCouldBeOccluders(IsoGridOcclusionData.OcclusionFilter filter) {
        if (this._objectEpoch != _ObjectEpoch) {
            if (this._bSoftInitialized) {
                this.Reset();
            }

            this._objectEpoch = _ObjectEpoch;
        }

        if (!this._bSoftInitialized) {
            this.LazyInitializeSoftOccluders();
        }

        if (filter == IsoGridOcclusionData.OcclusionFilter.Left) {
            return this._leftBuildingsArray;
        } else {
            return filter == IsoGridOcclusionData.OcclusionFilter.Right ? this._rightBuildingsArray : this._allBuildingsArray;
        }
    }

    private void LazyInitializeSoftOccluders() {
        boolean boolean0 = false;
        int int0 = this._ownerSquare.getX();
        int int1 = this._ownerSquare.getY();
        int int2 = this._ownerSquare.getZ();
        _allBuildings.clear();
        _leftBuildings.clear();
        _rightBuildings.clear();
        boolean0 |= this.GetBuildingFloorsProjectedOnSquare(_allBuildings, int0, int1, int2);
        boolean0 |= this.GetBuildingFloorsProjectedOnSquare(_allBuildings, int0 + 1, int1 + 1, int2);
        boolean0 |= this.GetBuildingFloorsProjectedOnSquare(_allBuildings, int0 + 2, int1 + 2, int2);
        boolean0 |= this.GetBuildingFloorsProjectedOnSquare(_allBuildings, int0 + 3, int1 + 3, int2);
        this._bLeftOccludedByOrphanStructures = this._bLeftOccludedByOrphanStructures
            | this.GetBuildingFloorsProjectedOnSquare(_leftBuildings, int0, int1 + 1, int2);
        this._bLeftOccludedByOrphanStructures = this._bLeftOccludedByOrphanStructures
            | this.GetBuildingFloorsProjectedOnSquare(_leftBuildings, int0 + 1, int1 + 2, int2);
        this._bLeftOccludedByOrphanStructures = this._bLeftOccludedByOrphanStructures
            | this.GetBuildingFloorsProjectedOnSquare(_leftBuildings, int0 + 2, int1 + 3, int2);
        this._bRightOccludedByOrphanStructures = this._bRightOccludedByOrphanStructures
            | this.GetBuildingFloorsProjectedOnSquare(_rightBuildings, int0 + 1, int1, int2);
        this._bRightOccludedByOrphanStructures = this._bRightOccludedByOrphanStructures
            | this.GetBuildingFloorsProjectedOnSquare(_rightBuildings, int0 + 2, int1 + 1, int2);
        this._bRightOccludedByOrphanStructures = this._bRightOccludedByOrphanStructures
            | this.GetBuildingFloorsProjectedOnSquare(_rightBuildings, int0 + 3, int1 + 2, int2);
        this._bLeftOccludedByOrphanStructures |= boolean0;
        _leftBuildings.addAll(_allBuildings);
        this._bRightOccludedByOrphanStructures |= boolean0;
        _rightBuildings.addAll(_allBuildings);
        _allBuildings.clear();
        _allBuildings.addAll(_leftBuildings);
        _allBuildings.addAll(_rightBuildings);
        this._leftBuildingsArray.addAll(_leftBuildings);
        this._rightBuildingsArray.addAll(_rightBuildings);
        this._allBuildingsArray.addAll(_allBuildings);
        this._bSoftInitialized = true;
    }

    private boolean GetBuildingFloorsProjectedOnSquare(THashSet<IsoBuilding> tHashSet, int int1, int int3, int int5) {
        boolean boolean0 = false;
        int int0 = int1;
        int int2 = int3;

        for (int int4 = int5; int4 < IsoCell.MaxHeight; int2 += 3) {
            IsoGridSquare square0 = IsoWorld.instance.CurrentCell.getGridSquare(int0, int2, int4);
            if (square0 != null) {
                IsoBuilding building = square0.getBuilding();
                if (building == null) {
                    building = square0.roofHideBuilding;
                }

                if (building != null) {
                    tHashSet.add(building);
                }

                for (int int6 = int4 - 1; int6 >= 0 && building == null; int6--) {
                    IsoGridSquare square1 = IsoWorld.instance.CurrentCell.getGridSquare(int0, int2, int6);
                    if (square1 != null) {
                        building = square1.getBuilding();
                        if (building == null) {
                            building = square1.roofHideBuilding;
                        }

                        if (building != null) {
                            tHashSet.add(building);
                        }
                    }
                }

                if (building == null && !boolean0 && square0.getZ() != 0 && square0.getPlayerBuiltFloor() != null) {
                    boolean0 = true;
                }
            }

            int4++;
            int0 += 3;
        }

        return boolean0;
    }

    public static enum OccluderType {
        Unknown,
        NotFull,
        Full;
    }

    public static enum OcclusionFilter {
        Left,
        Right,
        All;
    }
}
