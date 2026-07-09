// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory.types;

import java.util.List;
import zombie.GameTime;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.math.PZMath;
import zombie.interfaces.IUpdater;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemUser;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.objects.RainManager;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.StringUtils;

public final class DrainableComboItem extends InventoryItem implements Drainable, IUpdater {
    protected boolean bUseWhileEquiped = true;
    protected boolean bUseWhileUnequiped = false;
    protected int ticksPerEquipUse = 30;
    protected float useDelta = 0.03125F;
    protected float delta = 1.0F;
    protected float ticks = 0.0F;
    protected String ReplaceOnDeplete = null;
    protected String ReplaceOnDepleteFullType = null;
    public List<String> ReplaceOnCooked = null;
    private String OnCooked = null;
    private float rainFactor = 0.0F;
    private boolean canConsolidate = true;
    private float WeightEmpty = 0.0F;
    private static final float MIN_HEAT = 0.2F;
    private static final float MAX_HEAT = 3.0F;
    protected float Heat = 1.0F;
    protected int LastCookMinute = 0;

    public DrainableComboItem(String module, String name, String itemType, String texName) {
        super(module, name, itemType, texName);
    }

    public DrainableComboItem(String module, String name, String itemType, Item item) {
        super(module, name, itemType, item);
    }

    @Override
    public boolean IsDrainable() {
        return true;
    }

    @Override
    public int getSaveType() {
        return Item.Type.Drainable.ordinal();
    }

    @Override
    public boolean CanStack(InventoryItem item) {
        return false;
    }

    @Override
    public float getUsedDelta() {
        return this.delta;
    }

    public int getDrainableUsesInt() {
        return (int)Math.floor((this.getUsedDelta() + 1.0E-4) / this.getUseDelta());
    }

    public float getDrainableUsesFloat() {
        return this.getUsedDelta() / this.getUseDelta();
    }

    @Override
    public void render() {
    }

    @Override
    public void renderlast() {
    }

    @Override
    public void setUsedDelta(float usedDelta) {
        this.delta = PZMath.clamp(usedDelta, 0.0F, 1.0F);
        this.updateWeight();
    }

    @Override
    public boolean shouldUpdateInWorld() {
        if (!GameServer.bServer && this.Heat != 1.0F) {
            return true;
        } else if (this.canStoreWater() && this.isWaterSource() && this.getUsedDelta() < 1.0F) {
            IsoGridSquare square = this.getWorldItem().getSquare();
            return square != null && square.isOutside();
        } else {
            return false;
        }
    }

    @Override
    public void update() {
        ItemContainer container = this.getOutermostContainer();
        if (container != null) {
            float float0 = container.getTemprature();
            if (this.Heat > float0) {
                this.Heat = this.Heat - 0.001F * GameTime.instance.getMultiplier();
                if (this.Heat < Math.max(0.2F, float0)) {
                    this.Heat = Math.max(0.2F, float0);
                }
            }

            if (this.Heat < float0) {
                this.Heat = this.Heat + float0 / 1000.0F * GameTime.instance.getMultiplier();
                if (this.Heat > Math.min(3.0F, float0)) {
                    this.Heat = Math.min(3.0F, float0);
                }
            }

            if (this.IsCookable) {
                if (this.Heat > 1.6F) {
                    int int0 = GameTime.getInstance().getMinutes();
                    if (int0 != this.LastCookMinute) {
                        this.LastCookMinute = int0;
                        float float1 = this.Heat / 1.5F;
                        if (container.getTemprature() <= 1.6F) {
                            float1 *= 0.05F;
                        }

                        float float2 = this.CookingTime;
                        if (float2 < 1.0F) {
                            float2 = 10.0F;
                        }

                        float2 += float1;
                        if (this.isTaintedWater() && float2 > Math.min(this.MinutesToCook, 10.0F)) {
                            this.setTaintedWater(false);
                        }

                        if (!this.isCooked() && float2 > this.MinutesToCook) {
                            this.setCooked(true);
                            if (this.getReplaceOnCooked() != null) {
                                for (int int1 = 0; int1 < this.getReplaceOnCooked().size(); int1++) {
                                    InventoryItem item = this.container.AddItem(this.getReplaceOnCooked().get(int1));
                                    if (item != null) {
                                        if (item instanceof DrainableComboItem) {
                                            ((DrainableComboItem)item).setUsedDelta(this.getUsedDelta());
                                        }

                                        item.copyConditionModData(this);
                                    }
                                }

                                this.container.Remove(this);
                                IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this);
                                return;
                            }

                            if (this.getOnCooked() != null) {
                                LuaManager.caller.protectedCall(LuaManager.thread, LuaManager.env.rawget(this.getOnCooked()), this);
                                return;
                            }
                        }

                        if (this.CookingTime > this.MinutesToBurn) {
                            this.Burnt = true;
                            this.setCooked(false);
                        }
                    }
                }
            } else if (container != null && container.isMicrowave() && this.isTaintedWater() && this.Heat > 1.6F) {
                int int2 = GameTime.getInstance().getMinutes();
                if (int2 != this.LastCookMinute) {
                    this.LastCookMinute = int2;
                    float float3 = 1.0F;
                    if (container.getTemprature() <= 1.6F) {
                        float3 = (float)(float3 * 0.2);
                    }

                    this.CookingTime += float3;
                    if (this.CookingTime > 10.0F) {
                        this.setTaintedWater(false);
                    }
                }
            }
        }

        if (this.container == null && this.Heat != 1.0F) {
            float float4 = 1.0F;
            if (this.Heat > float4) {
                this.Heat = this.Heat - 0.001F * GameTime.instance.getMultiplier();
                if (this.Heat < float4) {
                    this.Heat = float4;
                }
            }

            if (this.Heat < float4) {
                this.Heat = this.Heat + float4 / 1000.0F * GameTime.instance.getMultiplier();
                if (this.Heat > float4) {
                    this.Heat = float4;
                }
            }
        }

        if (this.bUseWhileEquiped && this.delta > 0.0F) {
            IsoPlayer player = null;
            if (this.container != null && this.container.parent instanceof IsoPlayer) {
                for (int int3 = 0; int3 < IsoPlayer.numPlayers; int3++) {
                    if (this.container.parent == IsoPlayer.players[int3]) {
                        player = IsoPlayer.players[int3];
                    }
                }
            }

            if (player != null
                && (this.canBeActivated() && this.isActivated() || !this.canBeActivated())
                && (player.isHandItem(this) || player.isAttachedItem(this))) {
                this.ticks = this.ticks + GameTime.instance.getMultiplier();

                while (this.ticks >= this.ticksPerEquipUse) {
                    this.ticks = this.ticks - this.ticksPerEquipUse;
                    if (this.delta > 0.0F) {
                        this.Use();
                    }
                }
            }
        }

        if (this.bUseWhileUnequiped && this.delta > 0.0F && (this.canBeActivated() && this.isActivated() || !this.canBeActivated())) {
            this.ticks = this.ticks + GameTime.instance.getMultiplier();

            while (this.ticks >= this.ticksPerEquipUse) {
                this.ticks = this.ticks - this.ticksPerEquipUse;
                if (this.delta > 0.0F) {
                    this.Use();
                }
            }
        }

        if (this.getWorldItem() != null && this.canStoreWater() && this.isWaterSource() && RainManager.isRaining() && this.getRainFactor() > 0.0F) {
            IsoGridSquare square = this.getWorldItem().getSquare();
            if (square != null && square.isOutside()) {
                this.setUsedDelta(this.getUsedDelta() + 0.001F * RainManager.getRainIntensity() * GameTime.instance.getMultiplier() * this.getRainFactor());
                if (this.getUsedDelta() > 1.0F) {
                    this.setUsedDelta(1.0F);
                }

                this.setTaintedWater(true);
                this.updateWeight();
            }
        }
    }

    @Override
    public void Use() {
        if (this.getWorldItem() != null) {
            ItemUser.UseItem(this);
        } else {
            this.delta = this.delta - this.useDelta;
            if (this.uses > 1) {
                int int0 = this.uses - 1;
                this.uses = 1;
                InventoryItem item0 = InventoryItemFactory.CreateItem(this.getFullType());
                item0.setUses(int0);
                this.container.AddItem(item0);
            }

            if (this.delta <= 1.0E-4F) {
                this.delta = 0.0F;
                if (this.getReplaceOnDeplete() != null) {
                    String string = this.getReplaceOnDepleteFullType();
                    if (this.container != null) {
                        InventoryItem item1 = this.container.AddItem(string);
                        if (this.container.parent instanceof IsoGameCharacter character) {
                            if (character.getPrimaryHandItem() == this) {
                                character.setPrimaryHandItem(item1);
                            }

                            if (character.getSecondaryHandItem() == this) {
                                character.setSecondaryHandItem(item1);
                            }
                        }

                        item1.setCondition(this.getCondition());
                        item1.setFavorite(this.isFavorite());
                        this.container.Remove(this);
                    }
                } else {
                    super.Use();
                }
            }

            this.updateWeight();
        }
    }

    public void updateWeight() {
        if (this.getReplaceOnDeplete() != null) {
            if (this.getUsedDelta() >= 1.0F) {
                this.setCustomWeight(true);
                this.setActualWeight(this.getScriptItem().getActualWeight());
                this.setWeight(this.getActualWeight());
                return;
            }

            Item item = ScriptManager.instance.getItem(this.ReplaceOnDepleteFullType);
            if (item != null) {
                this.setCustomWeight(true);
                this.setActualWeight((this.getScriptItem().getActualWeight() - item.getActualWeight()) * this.getUsedDelta() + item.getActualWeight());
                this.setWeight(this.getActualWeight());
            }
        }

        if (this.getWeightEmpty() != 0.0F) {
            this.setCustomWeight(true);
            this.setActualWeight((this.getScriptItem().getActualWeight() - this.WeightEmpty) * this.getUsedDelta() + this.WeightEmpty);
        }
    }

    /**
     * @return the EmptyWeight
     */
    public float getWeightEmpty() {
        return this.WeightEmpty;
    }

    /**
     * 
     * @param weight the EmptyWeight to set
     */
    public void setWeightEmpty(float weight) {
        this.WeightEmpty = weight;
    }

    /**
     * @return the bUseWhileEquiped
     */
    public boolean isUseWhileEquiped() {
        return this.bUseWhileEquiped;
    }

    /**
     * 
     * @param _bUseWhileEquiped the bUseWhileEquiped to set
     */
    public void setUseWhileEquiped(boolean _bUseWhileEquiped) {
        this.bUseWhileEquiped = _bUseWhileEquiped;
    }

    /**
     * @return the bUseWhileUnequiped
     */
    public boolean isUseWhileUnequiped() {
        return this.bUseWhileUnequiped;
    }

    /**
     * 
     * @param _bUseWhileUnequiped the bUseWhileUnequiped to set
     */
    public void setUseWhileUnequiped(boolean _bUseWhileUnequiped) {
        this.bUseWhileUnequiped = _bUseWhileUnequiped;
    }

    /**
     * @return the ticksPerEquipUse
     */
    public int getTicksPerEquipUse() {
        return this.ticksPerEquipUse;
    }

    /**
     * 
     * @param _ticksPerEquipUse the ticksPerEquipUse to set
     */
    public void setTicksPerEquipUse(int _ticksPerEquipUse) {
        this.ticksPerEquipUse = _ticksPerEquipUse;
    }

    /**
     * @return the useDelta
     */
    public float getUseDelta() {
        return this.useDelta;
    }

    /**
     * 
     * @param _useDelta the useDelta to set
     */
    public void setUseDelta(float _useDelta) {
        this.useDelta = _useDelta;
    }

    /**
     * @return the delta
     */
    public float getDelta() {
        return this.delta;
    }

    /**
     * 
     * @param _delta the delta to set
     */
    public void setDelta(float _delta) {
        this.delta = _delta;
    }

    /**
     * @return the ticks
     */
    public float getTicks() {
        return this.ticks;
    }

    /**
     * 
     * @param _ticks the ticks to set
     */
    public void setTicks(float _ticks) {
        this.ticks = _ticks;
    }

    public void setReplaceOnDeplete(String _ReplaceOnDeplete) {
        this.ReplaceOnDeplete = _ReplaceOnDeplete;
        this.ReplaceOnDepleteFullType = this.getReplaceOnDepleteFullType();
    }

    /**
     * @return the ReplaceOnDeplete
     */
    public String getReplaceOnDeplete() {
        return this.ReplaceOnDeplete;
    }

    public String getReplaceOnDepleteFullType() {
        return StringUtils.moduleDotType(this.getModule(), this.ReplaceOnDeplete);
    }

    public void setHeat(float heat) {
        this.Heat = PZMath.clamp(heat, 0.0F, 3.0F);
    }

    public float getHeat() {
        return this.Heat;
    }

    @Override
    public float getInvHeat() {
        return (1.0F - this.Heat) / 3.0F;
    }

    @Override
    public boolean finishupdate() {
        if (this.canStoreWater() && this.isWaterSource() && this.getWorldItem() != null && this.getWorldItem().getSquare() != null) {
            return this.getUsedDelta() >= 1.0F;
        } else if (this.isTaintedWater()) {
            return false;
        } else {
            if (this.container != null) {
                if (this.Heat != this.container.getTemprature() || this.container.isTemperatureChanging()) {
                    return false;
                }

                if (this.container.type.equals("campfire") || this.container.type.equals("barbecue")) {
                    return false;
                }
            }

            return true;
        }
    }

    public int getRemainingUses() {
        return Math.round(this.getUsedDelta() / this.getUseDelta());
    }

    public float getRainFactor() {
        return this.rainFactor;
    }

    public void setRainFactor(float _rainFactor) {
        this.rainFactor = _rainFactor;
    }

    public boolean canConsolidate() {
        return this.canConsolidate;
    }

    public void setCanConsolidate(boolean _canConsolidate) {
        this.canConsolidate = _canConsolidate;
    }

    /**
     * @return the ReplaceOnCooked
     */
    public List<String> getReplaceOnCooked() {
        return this.ReplaceOnCooked;
    }

    /**
     * 
     * @param replaceOnCooked the ReplaceOnCooked to set
     */
    public void setReplaceOnCooked(List<String> replaceOnCooked) {
        this.ReplaceOnCooked = replaceOnCooked;
    }

    /**
     * @return the OnCooked
     */
    public String getOnCooked() {
        return this.OnCooked;
    }

    /**
     * 
     * @param onCooked the onCooked to set
     */
    public void setOnCooked(String onCooked) {
        this.OnCooked = onCooked;
    }
}
