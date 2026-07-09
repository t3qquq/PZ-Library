// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.debug.options;

import zombie.debug.BooleanDebugOption;

public final class Terrain extends OptionGroup {
    public final Terrain.RenderTiles RenderTiles = new Terrain.RenderTiles(this.Group);

    public Terrain() {
        super("Terrain");
    }

    public final class RenderTiles extends OptionGroup {
        public final BooleanDebugOption Enable = newDebugOnlyOption(this.Group, "Enable", true);
        public final BooleanDebugOption NewRender = newDebugOnlyOption(this.Group, "NewRender", true);
        public final BooleanDebugOption Shadows = newDebugOnlyOption(this.Group, "Shadows", true);
        public final BooleanDebugOption BloodDecals = newDebugOnlyOption(this.Group, "BloodDecals", true);
        public final BooleanDebugOption Water = newDebugOnlyOption(this.Group, "Water", true);
        public final BooleanDebugOption WaterShore = newDebugOnlyOption(this.Group, "WaterShore", true);
        public final BooleanDebugOption WaterBody = newDebugOnlyOption(this.Group, "WaterBody", true);
        public final BooleanDebugOption Lua = newDebugOnlyOption(this.Group, "Lua", true);
        public final BooleanDebugOption VegetationCorpses = newDebugOnlyOption(this.Group, "VegetationCorpses", true);
        public final BooleanDebugOption MinusFloorCharacters = newDebugOnlyOption(this.Group, "MinusFloorCharacters", true);
        public final BooleanDebugOption RenderGridSquares = newDebugOnlyOption(this.Group, "RenderGridSquares", true);
        public final BooleanDebugOption RenderSprites = newDebugOnlyOption(this.Group, "RenderSprites", true);
        public final BooleanDebugOption OverlaySprites = newDebugOnlyOption(this.Group, "OverlaySprites", true);
        public final BooleanDebugOption AttachedAnimSprites = newDebugOnlyOption(this.Group, "AttachedAnimSprites", true);
        public final BooleanDebugOption AttachedChildren = newDebugOnlyOption(this.Group, "AttachedChildren", true);
        public final BooleanDebugOption AttachedWallBloodSplats = newDebugOnlyOption(this.Group, "AttachedWallBloodSplats", true);
        public final BooleanDebugOption UseShaders = newOption(this.Group, "UseShaders", true);
        public final BooleanDebugOption HighContrastBg = newDebugOnlyOption(this.Group, "HighContrastBg", false);
        public final Terrain.RenderTiles.IsoGridSquare IsoGridSquare = new Terrain.RenderTiles.IsoGridSquare(this.Group);

        public RenderTiles(IDebugOptionGroup iDebugOptionGroup) {
            super(iDebugOptionGroup, "RenderTiles");
        }

        public final class IsoGridSquare extends OptionGroup {
            public final BooleanDebugOption RenderMinusFloor = newDebugOnlyOption(this.Group, "RenderMinusFloor", true);
            public final BooleanDebugOption DoorsAndWalls = newDebugOnlyOption(this.Group, "DoorsAndWalls", true);
            public final BooleanDebugOption DoorsAndWalls_SimpleLighting = newDebugOnlyOption(this.Group, "DoorsAndWallsSL", true);
            public final BooleanDebugOption Objects = newDebugOnlyOption(this.Group, "Objects", true);
            public final BooleanDebugOption MeshCutdown = newDebugOnlyOption(this.Group, "MeshCutDown", true);
            public final BooleanDebugOption IsoPadding = newDebugOnlyOption(this.Group, "IsoPadding", true);
            public final BooleanDebugOption IsoPaddingDeDiamond = newDebugOnlyOption(this.Group, "IsoPaddingDeDiamond", true);
            public final BooleanDebugOption IsoPaddingAttached = newDebugOnlyOption(this.Group, "IsoPaddingAttached", true);
            public final BooleanDebugOption ShoreFade = newDebugOnlyOption(this.Group, "ShoreFade", true);
            public final Terrain.RenderTiles.IsoGridSquare.Walls Walls = new Terrain.RenderTiles.IsoGridSquare.Walls(this.Group);
            public final Terrain.RenderTiles.IsoGridSquare.Floor Floor = new Terrain.RenderTiles.IsoGridSquare.Floor(this.Group);

            public IsoGridSquare(IDebugOptionGroup iDebugOptionGroup) {
                super(iDebugOptionGroup, "IsoGridSquare");
            }

            public final class Floor extends OptionGroup {
                public final BooleanDebugOption Lighting = newDebugOnlyOption(this.Group, "Lighting", true);
                public final BooleanDebugOption LightingOld = newDebugOnlyOption(this.Group, "LightingOld", false);
                public final BooleanDebugOption LightingDebug = newDebugOnlyOption(this.Group, "LightingDebug", false);

                public Floor(IDebugOptionGroup iDebugOptionGroup) {
                    super(iDebugOptionGroup, "Floor");
                }
            }

            public final class Walls extends OptionGroup {
                public final BooleanDebugOption NW = newDebugOnlyOption(this.Group, "NW", true);
                public final BooleanDebugOption W = newDebugOnlyOption(this.Group, "W", true);
                public final BooleanDebugOption N = newDebugOnlyOption(this.Group, "N", true);
                public final BooleanDebugOption Render = newDebugOnlyOption(this.Group, "Render", true);
                public final BooleanDebugOption Lighting = newDebugOnlyOption(this.Group, "Lighting", true);
                public final BooleanDebugOption LightingDebug = newDebugOnlyOption(this.Group, "LightingDebug", false);
                public final BooleanDebugOption LightingOldDebug = newDebugOnlyOption(this.Group, "LightingOldDebug", false);
                public final BooleanDebugOption AttachedSprites = newDebugOnlyOption(this.Group, "AttachedSprites", true);

                public Walls(IDebugOptionGroup iDebugOptionGroup) {
                    super(iDebugOptionGroup, "Walls");
                }
            }
        }
    }
}
