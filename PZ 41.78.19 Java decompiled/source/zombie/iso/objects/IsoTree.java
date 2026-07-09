// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import fmod.fmod.FMODManager;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.lwjgl.opengl.ARBShaderObjects;
import zombie.GameTime;
import zombie.IndieGL;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.parameters.ParameterMeleeHitSurface;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.opengl.ShaderProgram;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.inventory.types.HandWeapon;
import zombie.iso.CellLoader;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.BaseVehicle;

public class IsoTree extends IsoObject {
    public static final int MAX_SIZE = 6;
    public int LogYield = 1;
    public int damage = 500;
    public int size = 4;
    public boolean bRenderFlag;
    public float fadeAlpha;
    private static final IsoGameCharacter.Location[] s_chopTreeLocation = new IsoGameCharacter.Location[4];
    private static final ArrayList<IsoGridSquare> s_chopTreeIndicators = new ArrayList<>();
    private static IsoTree s_chopTreeHighlighted = null;

    public static IsoTree getNew() {
        synchronized (CellLoader.isoTreeCache) {
            if (CellLoader.isoTreeCache.isEmpty()) {
                return new IsoTree();
            } else {
                IsoTree tree = CellLoader.isoTreeCache.pop();
                tree.sx = 0.0F;
                return tree;
            }
        }
    }

    public IsoTree() {
    }

    public IsoTree(IsoCell cell) {
        super(cell);
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        super.save(output, IS_DEBUG_SAVE);
        output.put((byte)this.LogYield);
        output.put((byte)(this.damage / 10));
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(input, WorldVersion, IS_DEBUG_SAVE);
        this.LogYield = input.get();
        this.damage = input.get() * 10;
        if (this.sprite != null && this.sprite.getProperties().Val("tree") != null) {
            this.size = Integer.parseInt(this.sprite.getProperties().Val("tree"));
            if (this.size < 1) {
                this.size = 1;
            }

            if (this.size > 6) {
                this.size = 6;
            }
        }
    }

    @Override
    protected void checkMoveWithWind() {
        this.checkMoveWithWind(true);
    }

    @Override
    public void reset() {
        super.reset();
    }

    public IsoTree(IsoGridSquare sq, String gid) {
        super(sq, gid, false);
        this.initTree();
    }

    public IsoTree(IsoGridSquare sq, IsoSprite gid) {
        super(sq.getCell(), sq, gid);
        this.initTree();
    }

    public void initTree() {
        this.setType(IsoObjectType.tree);
        if (this.sprite.getProperties().Val("tree") != null) {
            this.size = Integer.parseInt(this.sprite.getProperties().Val("tree"));
            if (this.size < 1) {
                this.size = 1;
            }

            if (this.size > 6) {
                this.size = 6;
            }
        } else {
            this.size = 4;
        }

        switch (this.size) {
            case 1:
            case 2:
                this.LogYield = 1;
                break;
            case 3:
            case 4:
                this.LogYield = 2;
                break;
            case 5:
                this.LogYield = 3;
                break;
            case 6:
                this.LogYield = 4;
        }

        this.damage = this.LogYield * 80;
    }

    @Override
    public String getObjectName() {
        return "Tree";
    }

    @Override
    public void Damage(float amount) {
        float float0 = amount * 0.05F;
        this.damage = (int)(this.damage - float0);
        if (this.damage <= 0) {
            this.square.transmitRemoveItemFromSquare(this);
            this.square.RecalcAllWithNeighbours(true);
            int int0 = this.LogYield;

            for (int int1 = 0; int1 < int0; int1++) {
                this.square.AddWorldInventoryItem("Base.Log", 0.0F, 0.0F, 0.0F);
                if (Rand.Next(4) == 0) {
                    this.square.AddWorldInventoryItem("Base.TreeBranch", 0.0F, 0.0F, 0.0F);
                }

                if (Rand.Next(4) == 0) {
                    this.square.AddWorldInventoryItem("Base.Twigs", 0.0F, 0.0F, 0.0F);
                }
            }

            this.reset();
            CellLoader.isoTreeCache.add(this);

            for (int int2 = 0; int2 < IsoPlayer.numPlayers; int2++) {
                LosUtil.cachecleared[int2] = true;
            }

            IsoGridSquare.setRecalcLightTime(-1);
            GameTime.instance.lightSourceUpdate = 100.0F;
            LuaEventManager.triggerEvent("OnContainerUpdate");
        }
    }

    @Override
    public void HitByVehicle(BaseVehicle vehicle, float amount) {
        BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter(this.square.x + 0.5F, this.square.y + 0.5F, this.square.z);
        long long0 = baseSoundEmitter.playSound("VehicleHitTree");
        baseSoundEmitter.setParameterValue(long0, FMODManager.instance.getParameterDescription("VehicleSpeed"), vehicle.getCurrentSpeedKmHour());
        WorldSoundManager.instance.addSound(null, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
        this.Damage(this.damage);
    }

    @Override
    public void WeaponHit(IsoGameCharacter owner, HandWeapon weapon) {
        int int0 = weapon.getConditionLowerChance() * 2 + owner.getMaintenanceMod();
        if (!weapon.getCategories().contains("Axe")) {
            int0 = weapon.getConditionLowerChance() / 2 + owner.getMaintenanceMod();
        }

        if (Rand.NextBool(int0)) {
            weapon.setCondition(weapon.getCondition() - 1);
        }

        if (owner instanceof IsoPlayer) {
            ((IsoPlayer)owner).setMeleeHitSurface(ParameterMeleeHitSurface.Material.Tree);
            owner.getEmitter().playSound(weapon.getZombieHitSound());
        } else {
            owner.getEmitter().playSound("ChopTree");
        }

        WorldSoundManager.instance.addSound(null, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
        this.setRenderEffect(RenderEffectType.Hit_Tree_Shudder, true);
        float float0 = weapon.getTreeDamage();
        if (owner.Traits.Axeman.isSet() && weapon.getCategories().contains("Axe")) {
            float0 *= 1.5F;
        }

        this.damage = (int)(this.damage - float0);
        if (this.damage <= 0) {
            this.square.transmitRemoveItemFromSquare(this);
            owner.getEmitter().playSound("FallingTree");
            this.square.RecalcAllWithNeighbours(true);
            int int1 = this.LogYield;

            for (int int2 = 0; int2 < int1; int2++) {
                this.square.AddWorldInventoryItem("Base.Log", 0.0F, 0.0F, 0.0F);
                if (Rand.Next(4) == 0) {
                    this.square.AddWorldInventoryItem("Base.TreeBranch", 0.0F, 0.0F, 0.0F);
                }

                if (Rand.Next(4) == 0) {
                    this.square.AddWorldInventoryItem("Base.Twigs", 0.0F, 0.0F, 0.0F);
                }
            }

            this.reset();
            CellLoader.isoTreeCache.add(this);

            for (int int3 = 0; int3 < IsoPlayer.numPlayers; int3++) {
                LosUtil.cachecleared[int3] = true;
            }

            IsoGridSquare.setRecalcLightTime(-1);
            GameTime.instance.lightSourceUpdate = 100.0F;
            LuaEventManager.triggerEvent("OnContainerUpdate");
        }

        LuaEventManager.triggerEvent("OnWeaponHitTree", owner, weapon);
    }

    public void setHealth(int health) {
        this.damage = Math.max(health, 0);
    }

    public int getHealth() {
        return this.damage;
    }

    public int getMaxHealth() {
        return this.LogYield * 80;
    }

    public int getSize() {
        return this.size;
    }

    public float getSlowFactor(IsoMovingObject chr) {
        float float0 = 1.0F;
        if (chr instanceof IsoGameCharacter) {
            if ("parkranger".equals(((IsoGameCharacter)chr).getDescriptor().getProfession())) {
                float0 = 1.5F;
            }

            if ("lumberjack".equals(((IsoGameCharacter)chr).getDescriptor().getProfession())) {
                float0 = 1.2F;
            }
        }

        if (this.size == 1 || this.size == 2) {
            return 0.8F * float0;
        } else {
            return this.size != 3 && this.size != 4 ? 0.3F * float0 : 0.5F * float0;
        }
    }

    @Override
    public void render(float x, float y, float z, ColorInfo col, boolean bDoAttached, boolean bWallLightingPass, Shader shader) {
        if (this.isHighlighted()) {
            if (this.square != null) {
                s_chopTreeHighlighted = this;
            }
        } else {
            int int0 = IsoCamera.frameState.playerIndex;
            if (!this.bRenderFlag && !(this.fadeAlpha < this.getTargetAlpha(int0))) {
                this.renderInner(x, y, z, col, bDoAttached, false);
            } else {
                IndieGL.enableStencilTest();
                IndieGL.glStencilFunc(517, 128, 128);
                this.renderInner(x, y, z, col, bDoAttached, false);
                float float0 = 0.044999998F * (GameTime.getInstance().getMultiplier() / 1.6F);
                if (this.bRenderFlag && this.fadeAlpha > 0.25F) {
                    this.fadeAlpha -= float0;
                    if (this.fadeAlpha < 0.25F) {
                        this.fadeAlpha = 0.25F;
                    }
                }

                if (!this.bRenderFlag) {
                    float float1 = this.getTargetAlpha(int0);
                    if (this.fadeAlpha < float1) {
                        this.fadeAlpha += float0;
                        if (this.fadeAlpha > float1) {
                            this.fadeAlpha = float1;
                        }
                    }
                }

                float float2 = this.getAlpha(int0);
                float float3 = this.getTargetAlpha(int0);
                this.setAlphaAndTarget(int0, this.fadeAlpha);
                IndieGL.glStencilFunc(514, 128, 128);
                this.renderInner(x, y, z, col, true, false);
                this.setAlpha(int0, float2);
                this.setTargetAlpha(int0, float3);
                if (IsoTree.TreeShader.instance.StartShader()) {
                    IsoTree.TreeShader.instance.setOutlineColor(0.1F, 0.1F, 0.1F, 1.0F - this.fadeAlpha);
                    this.renderInner(x, y, z, col, true, true);
                    IndieGL.EndShader();
                }

                IndieGL.glStencilFunc(519, 255, 255);
            }

            this.checkChopTreeIndicator(x, y, z);
        }
    }

    private void renderInner(float float4, float float5, float float6, ColorInfo colorInfo, boolean var5, boolean boolean0) {
        if (this.sprite != null && this.sprite.name != null && this.sprite.name.contains("JUMBO")) {
            float float0 = this.offsetX;
            float float1 = this.offsetY;
            this.offsetX = 384 * Core.TileScale / 2 - 96 * Core.TileScale;
            this.offsetY = 256 * Core.TileScale - 32 * Core.TileScale;
            if (this.offsetX != float0 || this.offsetY != float1) {
                this.sx = 0.0F;
            }
        } else {
            float float2 = this.offsetX;
            float float3 = this.offsetY;
            this.offsetX = 32 * Core.TileScale;
            this.offsetY = 96 * Core.TileScale;
            if (this.offsetX != float2 || this.offsetY != float3) {
                this.sx = 0.0F;
            }
        }

        if (boolean0 && this.sprite != null) {
            Texture texture = this.sprite.getTextureForCurrentFrame(this.dir);
            if (texture != null) {
                IsoTree.TreeShader.instance.setStepSize(0.25F, texture.getWidth(), texture.getHeight());
            }
        }

        super.render(float4, float5, float6, colorInfo, false, false, null);
        if (this.AttachedAnimSprite != null) {
            int int0 = this.AttachedAnimSprite.size();

            for (int int1 = 0; int1 < int0; int1++) {
                IsoSpriteInstance spriteInstance = this.AttachedAnimSprite.get(int1);
                int int2 = IsoCamera.frameState.playerIndex;
                float float7 = this.getTargetAlpha(int2);
                this.setTargetAlpha(int2, 1.0F);
                spriteInstance.render(
                    this, float4, float5, float6, this.dir, this.offsetX, this.offsetY, this.isHighlighted() ? this.getHighlightColor() : colorInfo
                );
                this.setTargetAlpha(int2, float7);
                spriteInstance.update();
            }
        }
    }

    /**
     * 
     * @param sprite the sprite to set
     */
    @Override
    public void setSprite(IsoSprite sprite) {
        super.setSprite(sprite);
        this.initTree();
    }

    @Override
    public boolean isMaskClicked(int x, int y, boolean flip) {
        if (super.isMaskClicked(x, y, flip)) {
            return true;
        } else if (this.AttachedAnimSprite == null) {
            return false;
        } else {
            for (int int0 = 0; int0 < this.AttachedAnimSprite.size(); int0++) {
                if (this.AttachedAnimSprite.get(int0).parentSprite.isMaskClicked(this.dir, x, y, flip)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static void setChopTreeCursorLocation(int playerIndex, int x, int y, int z) {
        if (s_chopTreeLocation[playerIndex] == null) {
            s_chopTreeLocation[playerIndex] = new IsoGameCharacter.Location(-1, -1, -1);
        }

        IsoGameCharacter.Location location = s_chopTreeLocation[playerIndex];
        location.x = x;
        location.y = y;
        location.z = z;
    }

    private void checkChopTreeIndicator(float var1, float var2, float var3) {
        if (!this.isHighlighted()) {
            int int0 = IsoCamera.frameState.playerIndex;
            IsoGameCharacter.Location location = s_chopTreeLocation[int0];
            if (location != null && location.x != -1 && this.square != null) {
                if (this.getCell().getDrag(int0) == null) {
                    location.x = -1;
                } else {
                    if (IsoUtils.DistanceToSquared(this.square.x + 0.5F, this.square.y + 0.5F, location.x + 0.5F, location.y + 0.5F) < 12.25F) {
                        s_chopTreeIndicators.add(this.square);
                    }
                }
            }
        }
    }

    public static void renderChopTreeIndicators() {
        if (!s_chopTreeIndicators.isEmpty()) {
            PZArrayUtil.forEach(s_chopTreeIndicators, IsoTree::renderChopTreeIndicator);
            s_chopTreeIndicators.clear();
        }

        if (s_chopTreeHighlighted != null) {
            IsoTree tree = s_chopTreeHighlighted;
            s_chopTreeHighlighted = null;
            tree.renderInner(tree.square.x, tree.square.y, tree.square.z, tree.getHighlightColor(), false, false);
        }
    }

    private static void renderChopTreeIndicator(IsoGridSquare square) {
        Texture texture = Texture.getSharedTexture("media/ui/chop_tree.png");
        if (texture != null && texture.isReady()) {
            float float0 = square.x;
            float float1 = square.y;
            float float2 = square.z;
            float float3 = IsoUtils.XToScreen(float0, float1, float2, 0) + IsoSprite.globalOffsetX;
            float float4 = IsoUtils.YToScreen(float0, float1, float2, 0) + IsoSprite.globalOffsetY;
            float3 -= 32 * Core.TileScale;
            float4 -= 96 * Core.TileScale;
            SpriteRenderer.instance.render(texture, float3, float4, 64 * Core.TileScale, 128 * Core.TileScale, 0.0F, 0.5F, 0.0F, 0.75F, null);
        }
    }

    public static class TreeShader {
        public static final IsoTree.TreeShader instance = new IsoTree.TreeShader();
        private ShaderProgram shaderProgram;
        private int stepSize;
        private int outlineColor;

        public void initShader() {
            this.shaderProgram = ShaderProgram.createShaderProgram("tree", false, true);
            if (this.shaderProgram.isCompiled()) {
                this.stepSize = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), "stepSize");
                this.outlineColor = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), "outlineColor");
                ARBShaderObjects.glUseProgramObjectARB(this.shaderProgram.getShaderID());
                ARBShaderObjects.glUniform2fARB(this.stepSize, 0.001F, 0.001F);
                ARBShaderObjects.glUseProgramObjectARB(0);
            }
        }

        public void setOutlineColor(float float0, float float1, float float2, float float3) {
            SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.outlineColor, float0, float1, float2, float3);
        }

        public void setStepSize(float float0, int int1, int int0) {
            SpriteRenderer.instance.ShaderUpdate2f(this.shaderProgram.getShaderID(), this.stepSize, float0 / int1, float0 / int0);
        }

        public boolean StartShader() {
            if (this.shaderProgram == null) {
                RenderThread.invokeOnRenderContext(this::initShader);
            }

            if (this.shaderProgram.isCompiled()) {
                IndieGL.StartShader(this.shaderProgram.getShaderID(), 0);
                return true;
            } else {
                return false;
            }
        }
    }
}
