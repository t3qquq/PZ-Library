// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import zombie.Lua.LuaEventManager;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.opengl.Shader;
import zombie.core.skinnedmodel.model.WorldItemModelDrawer;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoPhysicsObject;

public class IsoFallingClothing extends IsoPhysicsObject {
    private InventoryItem clothing = null;
    private int dropTimer = 0;
    public boolean addWorldItem = true;

    @Override
    public String getObjectName() {
        return "FallingClothing";
    }

    public IsoFallingClothing(IsoCell cell) {
        super(cell);
    }

    public IsoFallingClothing(IsoCell cell, float float4, float float5, float float6, float float0, float float1, InventoryItem item) {
        super(cell);
        this.clothing = item;
        this.dropTimer = 60;
        this.velX = float0;
        this.velY = float1;
        float float2 = Rand.Next(4000) / 10000.0F;
        float float3 = Rand.Next(4000) / 10000.0F;
        float2 -= 0.2F;
        float3 -= 0.2F;
        this.velX += float2;
        this.velY += float3;
        this.x = float4;
        this.y = float5;
        this.z = float6;
        this.nx = float4;
        this.ny = float5;
        this.offsetX = 0.0F;
        this.offsetY = 0.0F;
        this.terminalVelocity = -0.02F;
        Texture texture = this.sprite.LoadFrameExplicit(item.getTex().getName());
        if (texture != null) {
            this.sprite.Animate = false;
            int int0 = Core.TileScale;
            this.sprite.def.scaleAspect(texture.getWidthOrig(), texture.getHeightOrig(), 16 * int0, 16 * int0);
        }

        this.speedMod = 4.5F;
    }

    @Override
    public void collideGround() {
        this.drop();
    }

    @Override
    public void collideWall() {
        this.drop();
    }

    @Override
    public void update() {
        super.update();
        this.dropTimer--;
        if (this.dropTimer <= 0) {
            this.drop();
        }
    }

    @Override
    public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean0, boolean boolean1, Shader shader) {
        float float0 = (60 - this.dropTimer) / 60.0F * 360.0F;
        if (!WorldItemModelDrawer.renderMain(this.clothing, this.getCurrentSquare(), this.getX(), this.getY(), this.getZ(), float0)) {
            super.render(float1, float2, float3, colorInfo, boolean0, boolean1, shader);
        }
    }

    void drop() {
        IsoGridSquare square = this.getCurrentSquare();
        if (square != null && this.clothing != null) {
            if (this.addWorldItem) {
                float float0 = square.getApparentZ(this.getX() % 1.0F, this.getY() % 1.0F);
                square.AddWorldInventoryItem(this.clothing, this.getX() % 1.0F, this.getY() % 1.0F, float0 - square.getZ());
            }

            this.clothing = null;
            this.setDestroyed(true);
            square.getMovingObjects().remove(this);
            this.getCell().Remove(this);
            LuaEventManager.triggerEvent("OnContainerUpdate", square);
        }
    }

    void Trigger() {
    }
}
