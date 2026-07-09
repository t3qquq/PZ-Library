// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.erosion;

import zombie.erosion.categories.ErosionCategory;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;

public final class ErosionWorld {
    public boolean init() {
        ErosionRegions.init();
        return true;
    }

    public void validateSpawn(IsoGridSquare _sq, ErosionData.Square _sqErosionData, ErosionData.Chunk _chunkData) {
        boolean boolean0 = _sq.Is(IsoFlagType.exterior);
        boolean boolean1 = _sq.Has(IsoObjectType.wall);
        IsoObject object = _sq.getFloor();
        String string0 = object != null && object.getSprite() != null ? object.getSprite().getName() : null;
        if (string0 == null) {
            _sqErosionData.doNothing = true;
        } else {
            boolean boolean2 = false;

            for (int int0 = 0; int0 < ErosionRegions.regions.size(); int0++) {
                ErosionRegions.Region region = ErosionRegions.regions.get(int0);
                String string1 = region.tileNameMatch;
                if ((string1 == null || string0.startsWith(string1))
                    && (!region.checkExterior || region.isExterior == boolean0)
                    && (!region.hasWall || region.hasWall == boolean1)) {
                    for (int int1 = 0; int1 < region.categories.size(); int1++) {
                        ErosionCategory erosionCategory = region.categories.get(int1);
                        boolean boolean3 = erosionCategory.replaceExistingObject(_sq, _sqErosionData, _chunkData, boolean0, boolean1);
                        if (!boolean3) {
                            boolean3 = erosionCategory.validateSpawn(_sq, _sqErosionData, _chunkData, boolean0, boolean1, false);
                        }

                        if (boolean3) {
                            boolean2 = true;
                            break;
                        }
                    }
                }
            }

            if (!boolean2) {
                _sqErosionData.doNothing = true;
            }
        }
    }

    public void update(IsoGridSquare _sq, ErosionData.Square _sqErosionData, ErosionData.Chunk _chunkData, int _eTick) {
        if (_sqErosionData.regions != null) {
            for (int int0 = 0; int0 < _sqErosionData.regions.size(); int0++) {
                ErosionCategory.Data data = _sqErosionData.regions.get(int0);
                ErosionCategory erosionCategory = ErosionRegions.getCategory(data.regionID, data.categoryID);
                int int1 = _sqErosionData.regions.size();
                erosionCategory.update(_sq, _sqErosionData, data, _chunkData, _eTick);
                if (int1 > _sqErosionData.regions.size()) {
                    int0--;
                }
            }
        }
    }
}
