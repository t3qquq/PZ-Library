// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import zombie.characters.IsoPlayer;
import zombie.core.opengl.RenderSettings;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.areas.IsoRoom;

public final class IsoRoomLight {
    public static int NextID = 1;
    private static int SHINE_DIST = 5;
    public int ID;
    public IsoRoom room;
    public int x;
    public int y;
    public int z;
    public int width;
    public int height;
    public float r;
    public float g;
    public float b;
    public boolean bActive;
    public boolean bActiveJNI;
    public boolean bHydroPowered = true;

    public IsoRoomLight(IsoRoom _room, int _x, int _y, int _z, int _width, int _height) {
        this.room = _room;
        this.x = _x;
        this.y = _y;
        this.z = _z;
        this.width = _width;
        this.height = _height;
        this.r = 0.9F;
        this.b = 0.8F;
        this.b = 0.7F;
        this.bActive = _room.def.bLightsActive;
    }

    public void addInfluence() {
        this.r = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.8F * IsoGridSquare.rmod * 0.7F;
        this.g = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.8F * IsoGridSquare.gmod * 0.7F;
        this.b = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.8F * IsoGridSquare.bmod * 0.7F;
        this.r *= 2.0F;
        this.g *= 2.0F;
        this.b *= 2.0F;
        this.shineIn(this.x - 1, this.y, this.x, this.y + this.height, SHINE_DIST, 0);
        this.shineIn(this.x, this.y - 1, this.x + this.width, this.y, 0, SHINE_DIST);
        this.shineIn(this.x + this.width, this.y, this.x + this.width + 1, this.y + this.height, -SHINE_DIST, 0);
        this.shineIn(this.x, this.y + this.height, this.x + this.width, this.y + this.height + 1, 0, -SHINE_DIST);
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y, this.z);
        this.bActive = this.room.def.bLightsActive;
        if (!this.bHydroPowered || IsoWorld.instance.isHydroPowerOn() || square != null && square.haveElectricity()) {
            if (this.bActive) {
                this.r = 0.9F;
                this.g = 0.8F;
                this.b = 0.7F;

                for (int int0 = this.y; int0 < this.y + this.height; int0++) {
                    for (int int1 = this.x; int1 < this.x + this.width; int1++) {
                        square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int0, this.z);
                        if (square != null) {
                            square.setLampostTotalR(square.getLampostTotalR() + this.r);
                            square.setLampostTotalG(square.getLampostTotalG() + this.g);
                            square.setLampostTotalB(square.getLampostTotalB() + this.b);
                        }
                    }
                }

                this.shineOut(this.x, this.y, this.x + 1, this.y + this.height, -SHINE_DIST, 0);
                this.shineOut(this.x, this.y, this.x + this.width, this.y + 1, 0, -SHINE_DIST);
                this.shineOut(this.x + this.width - 1, this.y, this.x + this.width, this.y + this.height, SHINE_DIST, 0);
                this.shineOut(this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, 0, SHINE_DIST);
            }
        } else {
            this.bActive = false;
        }
    }

    private void shineOut(int int4, int int1, int int5, int int2, int int6, int int7) {
        for (int int0 = int1; int0 < int2; int0++) {
            for (int int3 = int4; int3 < int5; int3++) {
                this.shineOut(int3, int0, int6, int7);
            }
        }
    }

    private void shineOut(int int2, int int3, int int0, int int5) {
        if (int0 > 0) {
            for (int int1 = 1; int1 <= int0; int1++) {
                this.shineFromTo(int2, int3, int2 + int1, int3);
            }
        } else if (int0 < 0) {
            for (int int4 = 1; int4 <= -int0; int4++) {
                this.shineFromTo(int2, int3, int2 - int4, int3);
            }
        } else if (int5 > 0) {
            for (int int6 = 1; int6 <= int5; int6++) {
                this.shineFromTo(int2, int3, int2, int3 + int6);
            }
        } else if (int5 < 0) {
            for (int int7 = 1; int7 <= -int5; int7++) {
                this.shineFromTo(int2, int3, int2, int3 - int7);
            }
        }
    }

    private void shineFromTo(int int2, int int3, int int0, int int1) {
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, this.z);
        if (square != null) {
            if (square.getRoom() != this.room) {
                LosUtil.TestResults testResults = LosUtil.lineClear(IsoWorld.instance.CurrentCell, int2, int3, this.z, int0, int1, this.z, false);
                if (testResults != LosUtil.TestResults.Blocked) {
                    float float0 = Math.abs(int2 - int0) + Math.abs(int3 - int1);
                    float float1 = float0 / SHINE_DIST;
                    float1 = 1.0F - float1;
                    float1 *= float1;
                    float float2 = float1 * this.r * 2.0F;
                    float float3 = float1 * this.g * 2.0F;
                    float float4 = float1 * this.b * 2.0F;
                    square.setLampostTotalR(square.getLampostTotalR() + float2);
                    square.setLampostTotalG(square.getLampostTotalG() + float3);
                    square.setLampostTotalB(square.getLampostTotalB() + float4);
                }
            }
        }
    }

    private void shineIn(int int4, int int1, int int5, int int2, int int6, int int7) {
        for (int int0 = int1; int0 < int2; int0++) {
            for (int int3 = int4; int3 < int5; int3++) {
                this.shineIn(int3, int0, int6, int7);
            }
        }
    }

    private void shineIn(int int0, int int1, int int2, int int5) {
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, this.z);
        if (square != null && square.Is(IsoFlagType.exterior)) {
            if (int2 > 0) {
                for (int int3 = 1; int3 <= int2; int3++) {
                    this.shineFromToIn(int0, int1, int0 + int3, int1);
                }
            } else if (int2 < 0) {
                for (int int4 = 1; int4 <= -int2; int4++) {
                    this.shineFromToIn(int0, int1, int0 - int4, int1);
                }
            } else if (int5 > 0) {
                for (int int6 = 1; int6 <= int5; int6++) {
                    this.shineFromToIn(int0, int1, int0, int1 + int6);
                }
            } else if (int5 < 0) {
                for (int int7 = 1; int7 <= -int5; int7++) {
                    this.shineFromToIn(int0, int1, int0, int1 - int7);
                }
            }
        }
    }

    private void shineFromToIn(int int2, int int3, int int0, int int1) {
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, this.z);
        if (square != null) {
            LosUtil.TestResults testResults = LosUtil.lineClear(IsoWorld.instance.CurrentCell, int2, int3, this.z, int0, int1, this.z, false);
            if (testResults != LosUtil.TestResults.Blocked) {
                float float0 = Math.abs(int2 - int0) + Math.abs(int3 - int1);
                float float1 = float0 / SHINE_DIST;
                float1 = 1.0F - float1;
                float1 *= float1;
                float float2 = float1 * this.r * 2.0F;
                float float3 = float1 * this.g * 2.0F;
                float float4 = float1 * this.b * 2.0F;
                square.setLampostTotalR(square.getLampostTotalR() + float2);
                square.setLampostTotalG(square.getLampostTotalG() + float3);
                square.setLampostTotalB(square.getLampostTotalB() + float4);
            }
        }
    }

    public void clearInfluence() {
        for (int int0 = this.y - SHINE_DIST; int0 < this.y + this.height + SHINE_DIST; int0++) {
            for (int int1 = this.x - SHINE_DIST; int1 < this.x + this.width + SHINE_DIST; int1++) {
                IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int0, this.z);
                if (square != null) {
                    square.setLampostTotalR(0.0F);
                    square.setLampostTotalG(0.0F);
                    square.setLampostTotalB(0.0F);
                }
            }
        }
    }

    public boolean isInBounds() {
        IsoChunkMap[] chunkMaps = IsoWorld.instance.CurrentCell.ChunkMap;

        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            if (!chunkMaps[int0].ignore) {
                int int1 = chunkMaps[int0].getWorldXMinTiles();
                int int2 = chunkMaps[int0].getWorldXMaxTiles();
                int int3 = chunkMaps[int0].getWorldYMinTiles();
                int int4 = chunkMaps[int0].getWorldYMaxTiles();
                if (this.x - SHINE_DIST < int2
                    && this.x + this.width + SHINE_DIST > int1
                    && this.y - SHINE_DIST < int4
                    && this.y + this.height + SHINE_DIST > int3) {
                    return true;
                }
            }
        }

        return false;
    }
}
