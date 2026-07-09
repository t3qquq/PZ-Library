// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.debug.options;

import zombie.debug.BooleanDebugOption;

public final class IsoSprite extends OptionGroup {
    public final BooleanDebugOption RenderSprites = newDebugOnlyOption(this.Group, "Render.Sprites", true);
    public final BooleanDebugOption RenderModels = newDebugOnlyOption(this.Group, "Render.Models", true);
    public final BooleanDebugOption MovingObjectEdges = newDebugOnlyOption(this.Group, "Render.MovingObjectEdges", false);
    public final BooleanDebugOption DropShadowEdges = newDebugOnlyOption(this.Group, "Render.DropShadowEdges", false);
    public final BooleanDebugOption NearestMagFilterAtMinZoom = newDebugOnlyOption(this.Group, "Render.NearestMagFilterAtMinZoom", true);
    public final BooleanDebugOption ItemHeight = newDebugOnlyOption(this.Group, "Render.ItemHeight", false);
    public final BooleanDebugOption Surface = newDebugOnlyOption(this.Group, "Render.Surface", false);
    public final BooleanDebugOption TextureWrapClampToEdge = newDebugOnlyOption(this.Group, "Render.TextureWrap.ClampToEdge", false);
    public final BooleanDebugOption TextureWrapRepeat = newDebugOnlyOption(this.Group, "Render.TextureWrap.Repeat", false);
    public final BooleanDebugOption ForceLinearMagFilter = newDebugOnlyOption(this.Group, "Render.ForceLinearMagFilter", false);
    public final BooleanDebugOption ForceNearestMagFilter = newDebugOnlyOption(this.Group, "Render.ForceNearestMagFilter", false);
    public final BooleanDebugOption ForceNearestMipMapping = newDebugOnlyOption(this.Group, "Render.ForceNearestMipMapping", false);
    public final BooleanDebugOption CharacterMipmapColors = newDebugOnlyOption(this.Group, "Render.CharacterMipmapColors", false);
    public final BooleanDebugOption WorldMipmapColors = newDebugOnlyOption(this.Group, "Render.WorldMipmapColors", false);

    public IsoSprite() {
        super("IsoSprite");
    }
}
