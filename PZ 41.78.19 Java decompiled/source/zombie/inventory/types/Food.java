// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory.types;

import fmod.fmod.FMODManager;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorDesc;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemSoundManager;
import zombie.inventory.ItemType;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoFireplace;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;
import zombie.util.StringUtils;
import zombie.util.io.BitHeader;
import zombie.util.io.BitHeaderRead;
import zombie.util.io.BitHeaderWrite;

public final class Food extends InventoryItem {
    protected boolean bBadCold = false;
    protected boolean bGoodHot = false;
    private static final float MIN_HEAT = 0.2F;
    private static final float MAX_HEAT = 3.0F;
    protected float Heat = 1.0F;
    protected float endChange = 0.0F;
    protected float hungChange = 0.0F;
    protected String useOnConsume = null;
    protected boolean rotten = false;
    protected boolean bDangerousUncooked = false;
    protected int LastCookMinute = 0;
    public float thirstChange = 0.0F;
    public boolean Poison = false;
    private List<String> ReplaceOnCooked = null;
    private float baseHunger = 0.0F;
    public ArrayList<String> spices = null;
    private boolean isSpice = false;
    private int poisonDetectionLevel = -1;
    private Integer PoisonLevelForRecipe = 0;
    private int UseForPoison = 0;
    private int PoisonPower = 0;
    private String FoodType = null;
    private String CustomEatSound = null;
    private boolean RemoveNegativeEffectOnCooked = false;
    private String Chef = null;
    private String OnCooked = null;
    private String WorldTextureCooked;
    private String WorldTextureRotten;
    private String WorldTextureOverdone;
    private int fluReduction = 0;
    private int ReduceFoodSickness = 0;
    private float painReduction = 0.0F;
    private String HerbalistType;
    private float carbohydrates = 0.0F;
    private float lipids = 0.0F;
    private float proteins = 0.0F;
    private float calories = 0.0F;
    private boolean packaged = false;
    private float freezingTime = 0.0F;
    private boolean frozen = false;
    private boolean canBeFrozen = true;
    protected float LastFrozenUpdate = -1.0F;
    public static final float FreezerAgeMultiplier = 0.02F;
    private String replaceOnRotten = null;
    private boolean forceFoodTypeAsName = false;
    private float rottenTime = 0.0F;
    private float compostTime = 0.0F;
    private String onEat = null;
    private boolean badInMicrowave = false;
    private boolean cookedInMicrowave = false;
    private long m_cookingSound = 0L;
    private int m_cookingParameter = -1;
    private static final int COOKING_STATE_COOKING = 0;
    private static final int COOKING_STATE_BURNING = 1;

    @Override
    public String getCategory() {
        return this.mainCategory != null ? this.mainCategory : "Food";
    }

    public Food(String module, String name, String itemType, String texName) {
        super(module, name, itemType, texName);
        Texture.WarnFailFindTexture = false;
        this.texturerotten = Texture.trygetTexture(texName + "Rotten");
        this.textureCooked = Texture.trygetTexture(texName + "Cooked");
        this.textureBurnt = Texture.trygetTexture(texName + "Overdone");
        String string0 = "Overdone.png";
        if (this.textureBurnt == null) {
            this.textureBurnt = Texture.trygetTexture(texName + "Burnt");
            if (this.textureBurnt != null) {
                string0 = "Burnt.png";
            }
        }

        String string1 = "Rotten.png";
        if (this.texturerotten == null) {
            this.texturerotten = Texture.trygetTexture(texName + "Spoiled");
            if (this.texturerotten != null) {
                string1 = "Spoiled.png";
            }
        }

        Texture.WarnFailFindTexture = true;
        if (this.texturerotten == null) {
            this.texturerotten = this.texture;
        }

        if (this.textureCooked == null) {
            this.textureCooked = this.texture;
        }

        if (this.textureBurnt == null) {
            this.textureBurnt = this.texture;
        }

        this.WorldTextureCooked = this.WorldTexture.replace(".png", "Cooked.png");
        this.WorldTextureOverdone = this.WorldTexture.replace(".png", string0);
        this.WorldTextureRotten = this.WorldTexture.replace(".png", string1);
        this.cat = ItemType.Food;
    }

    public Food(String module, String name, String itemType, Item item) {
        super(module, name, itemType, item);
        String string = item.ItemName;
        Texture.WarnFailFindTexture = false;
        this.texture = item.NormalTexture;
        if (item.SpecialTextures.size() == 0) {
            boolean boolean0 = false;
        }

        if (item.SpecialTextures.size() > 0) {
            this.texturerotten = item.SpecialTextures.get(0);
        }

        if (item.SpecialTextures.size() > 1) {
            this.textureCooked = item.SpecialTextures.get(1);
        }

        if (item.SpecialTextures.size() > 2) {
            this.textureBurnt = item.SpecialTextures.get(2);
        }

        Texture.WarnFailFindTexture = true;
        if (this.texturerotten == null) {
            this.texturerotten = this.texture;
        }

        if (this.textureCooked == null) {
            this.textureCooked = this.texture;
        }

        if (this.textureBurnt == null) {
            this.textureBurnt = this.texture;
        }

        if (item.SpecialWorldTextureNames.size() > 0) {
            this.WorldTextureRotten = item.SpecialWorldTextureNames.get(0);
        }

        if (item.SpecialWorldTextureNames.size() > 1) {
            this.WorldTextureCooked = item.SpecialWorldTextureNames.get(1);
        }

        if (item.SpecialWorldTextureNames.size() > 2) {
            this.WorldTextureOverdone = item.SpecialWorldTextureNames.get(2);
        }

        this.cat = ItemType.Food;
    }

    @Override
    public boolean IsFood() {
        return true;
    }

    @Override
    public int getSaveType() {
        return Item.Type.Food.ordinal();
    }

    @Override
    public void update() {
        if (this.hasTag("AlreadyCooked")) {
            this.setCooked(true);
        }

        this.updateTemperature();
        ItemContainer container = this.getOutermostContainer();
        if (container != null) {
            if (this.IsCookable && !this.isFrozen()) {
                if (this.Heat > 1.6F) {
                    int int0 = GameTime.getInstance().getMinutes();
                    if (int0 != this.LastCookMinute) {
                        this.LastCookMinute = int0;
                        float float0 = this.Heat / 1.5F;
                        if (container.getTemprature() <= 1.6F) {
                            float0 *= 0.05F;
                        }

                        this.CookingTime += float0;
                        if (this.shouldPlayCookingSound()) {
                            ItemSoundManager.addItem(this);
                        }

                        if (this.isTaintedWater() && this.CookingTime > Math.min(this.MinutesToCook, 10.0F)) {
                            this.setTaintedWater(false);
                        }

                        if (!this.isCooked() && !this.Burnt && this.CookingTime > this.MinutesToCook) {
                            if (this.getReplaceOnCooked() != null && !this.isRotten()) {
                                if (GameClient.bClient) {
                                    GameClient.instance.sendReplaceOnCooked(this);
                                    this.container.Remove(this);
                                    IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this);
                                    return;
                                }

                                for (int int1 = 0; int1 < this.getReplaceOnCooked().size(); int1++) {
                                    InventoryItem item = this.container.AddItem(this.getReplaceOnCooked().get(int1));
                                    if (item != null) {
                                        item.copyConditionModData(this);
                                        if (item instanceof Food && this instanceof Food) {
                                        }

                                        if (item instanceof Food && ((Food)item).isBadInMicrowave() && this.container.isMicrowave()) {
                                            item.setUnhappyChange(5.0F);
                                            item.setBoredomChange(5.0F);
                                            ((Food)item).cookedInMicrowave = true;
                                        }
                                    }
                                }

                                this.container.Remove(this);
                                IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this);
                                return;
                            }

                            this.setCooked(true);
                            if (this.getScriptItem().RemoveUnhappinessWhenCooked) {
                                this.setUnhappyChange(0.0F);
                            }

                            if (this.type.equals("RicePot")
                                || this.type.equals("PastaPot")
                                || this.type.equals("RicePan")
                                || this.type.equals("PastaPan")
                                || this.type.equals("WaterPotRice")
                                || this.type.equals("WaterPotPasta")
                                || this.type.equals("WaterSaucepanRice")
                                || this.type.equals("WaterSaucepanPasta")
                                || this.type.equals("RiceBowl")
                                || this.type.equals("PastaBowl")) {
                                this.setAge(0.0F);
                                this.setOffAge(1);
                                this.setOffAgeMax(2);
                            }

                            if (this.isRemoveNegativeEffectOnCooked()) {
                                if (this.thirstChange > 0.0F) {
                                    this.setThirstChange(0.0F);
                                }

                                if (this.unhappyChange > 0.0F) {
                                    this.setUnhappyChange(0.0F);
                                }

                                if (this.boredomChange > 0.0F) {
                                    this.setBoredomChange(0.0F);
                                }
                            }

                            if (this.getOnCooked() != null) {
                                LuaManager.caller.protectedCall(LuaManager.thread, LuaManager.env.rawget(this.getOnCooked()), this);
                            }

                            if (this.isBadInMicrowave() && this.container.isMicrowave()) {
                                this.setUnhappyChange(5.0F);
                                this.setBoredomChange(5.0F);
                                this.cookedInMicrowave = true;
                            }

                            if (this.Chef != null && !this.Chef.isEmpty()) {
                                for (int int2 = 0; int2 < IsoPlayer.numPlayers; int2++) {
                                    IsoPlayer player = IsoPlayer.players[int2];
                                    if (player != null && !player.isDead() && this.Chef.equals(player.getFullName())) {
                                        player.getXp().AddXP(PerkFactory.Perks.Cooking, 10.0F);
                                        break;
                                    }
                                }
                            }
                        }

                        if (this.CookingTime > this.MinutesToBurn) {
                            this.Burnt = true;
                            this.setCooked(false);
                        }

                        if (IsoWorld.instance.isHydroPowerOn()
                            && this.Burnt
                            && this.CookingTime >= 50.0F
                            && this.CookingTime >= this.MinutesToCook * 2.0F + this.MinutesToBurn / 2.0F
                            && Rand.Next(Rand.AdjustForFramerate(200)) == 0) {
                            boolean boolean0 = this.container != null
                                && this.container.getParent() != null
                                && this.container.getParent().getName() != null
                                && this.container.getParent().getName().equals("Campfire");
                            if (!boolean0 && this.container != null && this.container.getParent() != null && this.container.getParent() instanceof IsoFireplace
                                )
                             {
                                boolean0 = true;
                            }

                            if (this.container != null && this.container.SourceGrid != null && !boolean0) {
                                IsoFireManager.StartFire(this.container.SourceGrid.getCell(), this.container.SourceGrid, true, 500000);
                                this.IsCookable = false;
                            }
                        }
                    }
                }
            } else if (this.isTaintedWater() && this.Heat > 1.6F && !this.isFrozen()) {
                int int3 = GameTime.getInstance().getMinutes();
                if (int3 != this.LastCookMinute) {
                    this.LastCookMinute = int3;
                    float float1 = 1.0F;
                    if (container.getTemprature() <= 1.6F) {
                        float1 = (float)(float1 * 0.2);
                    }

                    this.CookingTime += float1;
                    if (this.CookingTime > 10.0F) {
                        this.setTaintedWater(false);
                    }
                }
            }
        }

        this.updateRotting(container);
    }

    @Override
    public void updateSound(BaseSoundEmitter emitter) {
        if (this.shouldPlayCookingSound()) {
            if (emitter.isPlaying(this.m_cookingSound)) {
                this.setCookingParameter(emitter);
                return;
            }

            ItemContainer container = this.getOutermostContainer();
            IsoGridSquare square = container.getParent().getSquare();
            emitter.setPos(square.getX() + 0.5F, square.getY() + 0.5F, square.getZ());
            this.m_cookingSound = emitter.playSoundImpl(this.getCookingSound(), (IsoObject)null);
            this.setCookingParameter(emitter);
        } else {
            emitter.stopOrTriggerSound(this.m_cookingSound);
            this.m_cookingSound = 0L;
            this.m_cookingParameter = -1;
            ItemSoundManager.removeItem(this);
        }
    }

    private boolean shouldPlayCookingSound() {
        if (GameServer.bServer) {
            return false;
        } else if (StringUtils.isNullOrWhitespace(this.getCookingSound())) {
            return false;
        } else {
            ItemContainer container = this.getOutermostContainer();
            return container != null && container.getParent() != null && container.getParent().getObjectIndex() != -1 && !(container.getTemprature() <= 1.6F)
                ? this.isCookable() && !this.isFrozen() && this.getHeat() > 1.6F
                : false;
        }
    }

    private void setCookingParameter(BaseSoundEmitter baseSoundEmitter) {
        boolean boolean0 = this.CookingTime > this.MinutesToCook;
        int int0 = boolean0 ? 1 : 0;
        if (int0 != this.m_cookingParameter) {
            this.m_cookingParameter = int0;
            baseSoundEmitter.setParameterValue(this.m_cookingSound, FMODManager.instance.getParameterDescription("CookingState"), this.m_cookingParameter);
        }
    }

    private void updateTemperature() {
        ItemContainer container = this.getOutermostContainer();
        float float0 = container == null ? 1.0F : container.getTemprature();
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
    }

    private void updateRotting(ItemContainer container) {
        if (this.OffAgeMax != 1.0E9) {
            if (!GameClient.bClient || this.isInLocalPlayerInventory()) {
                if (!GameServer.bServer || this.container == null || this.getOutermostContainer() == this.container) {
                    if (this.replaceOnRotten != null && !this.replaceOnRotten.isEmpty()) {
                        this.updateAge();
                        if (this.isRotten()) {
                            InventoryItem item = InventoryItemFactory.CreateItem(this.getModule() + "." + this.replaceOnRotten, this);
                            if (item == null) {
                                DebugLog.General.warn("ReplaceOnRotten = " + this.replaceOnRotten + " doesn't exist for " + this.getFullType());
                                this.destroyThisItem();
                                return;
                            }

                            item.setAge(this.getAge());
                            IsoWorldInventoryObject worldInventoryObject = this.getWorldItem();
                            if (worldInventoryObject != null && worldInventoryObject.getSquare() != null) {
                                IsoGridSquare square = worldInventoryObject.getSquare();
                                if (!GameServer.bServer) {
                                    worldInventoryObject.item = item;
                                    item.setWorldItem(worldInventoryObject);
                                    worldInventoryObject.updateSprite();
                                    IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this);
                                    LuaEventManager.triggerEvent("OnContainerUpdate");
                                    return;
                                }

                                square.AddWorldInventoryItem(item, worldInventoryObject.xoff, worldInventoryObject.yoff, worldInventoryObject.zoff, true);
                            } else if (this.container != null) {
                                this.container.AddItem(item);
                                if (GameServer.bServer) {
                                    GameServer.sendAddItemToContainer(this.container, item);
                                }
                            }

                            this.destroyThisItem();
                            return;
                        }
                    }

                    if (SandboxOptions.instance.DaysForRottenFoodRemoval.getValue() >= 0) {
                        if (container != null && container.parent instanceof IsoCompost) {
                            return;
                        }

                        this.updateAge();
                        if (this.getAge() > this.getOffAgeMax() + SandboxOptions.instance.DaysForRottenFoodRemoval.getValue()) {
                            this.destroyThisItem();
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateAge() {
        ItemContainer container = this.getOutermostContainer();
        this.updateFreezing(container);
        boolean boolean0 = false;
        if (container != null && container.getSourceGrid() != null && container.getSourceGrid().haveElectricity()) {
            boolean0 = true;
        }

        float float0 = (float)GameTime.getInstance().getWorldAgeHours();
        float float1 = 0.2F;
        if (SandboxOptions.instance.FridgeFactor.getValue() == 1) {
            float1 = 0.4F;
        } else if (SandboxOptions.instance.FridgeFactor.getValue() == 2) {
            float1 = 0.3F;
        } else if (SandboxOptions.instance.FridgeFactor.getValue() == 4) {
            float1 = 0.1F;
        } else if (SandboxOptions.instance.FridgeFactor.getValue() == 5) {
            float1 = 0.03F;
        }

        if (this.LastAged < 0.0F) {
            this.LastAged = float0;
        } else if (this.LastAged > float0) {
            this.LastAged = float0;
        }

        if (float0 > this.LastAged) {
            double double0 = float0 - this.LastAged;
            if (container != null && this.Heat != container.getTemprature()) {
                if (double0 < 0.33333334F) {
                    if (!IsoWorld.instance.getCell().getProcessItems().contains(this)) {
                        this.Heat = GameTime.instance.Lerp(this.Heat, container.getTemprature(), (float)double0 / 0.33333334F);
                        IsoWorld.instance.getCell().addToProcessItems(this);
                    }
                } else {
                    this.Heat = container.getTemprature();
                }
            }

            if (this.isFrozen()) {
                double0 *= 0.02F;
            } else if (container != null && (container.getType().equals("fridge") || container.getType().equals("freezer"))) {
                if (boolean0) {
                    double0 *= float1;
                } else if (SandboxOptions.instance.getElecShutModifier() > -1 && this.LastAged < SandboxOptions.instance.getElecShutModifier() * 24) {
                    float float2 = Math.min((float)(SandboxOptions.instance.getElecShutModifier() * 24), float0);
                    double0 = (float2 - this.LastAged) * float1;
                    if (float0 > SandboxOptions.instance.getElecShutModifier() * 24) {
                        double0 += float0 - SandboxOptions.instance.getElecShutModifier() * 24;
                    }
                }
            }

            float float3 = 1.0F;
            if (SandboxOptions.instance.FoodRotSpeed.getValue() == 1) {
                float3 = 1.7F;
            } else if (SandboxOptions.instance.FoodRotSpeed.getValue() == 2) {
                float3 = 1.4F;
            } else if (SandboxOptions.instance.FoodRotSpeed.getValue() == 4) {
                float3 = 0.7F;
            } else if (SandboxOptions.instance.FoodRotSpeed.getValue() == 5) {
                float3 = 0.4F;
            }

            boolean boolean1 = !this.Burnt && this.OffAge < 1000000000 && this.Age < this.OffAge;
            boolean boolean2 = !this.Burnt && this.OffAgeMax < 1000000000 && this.Age >= this.OffAgeMax;
            this.Age = (float)(this.Age + double0 * float3 / 24.0);
            this.LastAged = float0;
            boolean boolean3 = !this.Burnt && this.OffAge < 1000000000 && this.Age < this.OffAge;
            boolean boolean4 = !this.Burnt && this.OffAgeMax < 1000000000 && this.Age >= this.OffAgeMax;
            if (!GameServer.bServer && (boolean1 != boolean3 || boolean2 != boolean4)) {
                LuaEventManager.triggerEvent("OnContainerUpdate", this);
            }
        }
    }

    @Override
    public void setAutoAge() {
        ItemContainer container = this.getOutermostContainer();
        float float0 = (float)GameTime.getInstance().getWorldAgeHours() / 24.0F;
        float0 += (SandboxOptions.instance.TimeSinceApo.getValue() - 1) * 30;
        float float1 = float0;
        boolean boolean0 = false;
        if (container != null && container.getParent() != null && container.getParent().getSprite() != null) {
            boolean0 = container.getParent().getSprite().getProperties().Is("IsFridge");
        }

        if (container != null && (boolean0 || container.getType().equals("fridge") || container.getType().equals("freezer"))) {
            int int0 = SandboxOptions.instance.ElecShutModifier.getValue();
            if (int0 > -1) {
                float float2 = Math.min((float)int0, float0);
                int int1 = SandboxOptions.instance.FridgeFactor.getValue();
                float float3 = 0.2F;
                if (int1 == 1) {
                    float3 = 0.4F;
                } else if (int1 == 2) {
                    float3 = 0.3F;
                } else if (int1 == 4) {
                    float3 = 0.1F;
                } else if (int1 == 5) {
                    float3 = 0.03F;
                }

                if (!container.getType().equals("fridge") && this.canBeFrozen() && !boolean0) {
                    float float4 = float2;
                    float float5 = 100.0F;
                    if (float0 > float2) {
                        float float6 = (float0 - float2) * 24.0F;
                        float float7 = 1440.0F / GameTime.getInstance().getMinutesPerDay() * 60.0F * 5.0F;
                        float float8 = 0.0095999995F;
                        float5 -= float8 * float7 * float6;
                        if (float5 > 0.0F) {
                            float4 = float2 + float6 / 24.0F;
                        } else {
                            float float9 = 100.0F / (float8 * float7);
                            float4 = float2 + float9 / 24.0F;
                            float5 = 0.0F;
                        }
                    }

                    float1 = float0 - float4;
                    float1 += float4 * 0.02F;
                    this.setFreezingTime(float5);
                } else {
                    float1 = float0 - float2;
                    float1 += float2 * float3;
                }
            }
        }

        int int2 = SandboxOptions.instance.FoodRotSpeed.getValue();
        float float10 = 1.0F;
        if (int2 == 1) {
            float10 = 1.7F;
        } else if (int2 == 2) {
            float10 = 1.4F;
        } else if (int2 == 4) {
            float10 = 0.7F;
        } else if (int2 == 5) {
            float10 = 0.4F;
        }

        this.Age = float1 * float10;
        this.LastAged = (float)GameTime.getInstance().getWorldAgeHours();
        this.LastFrozenUpdate = this.LastAged;
        if (container != null) {
            this.setHeat(container.getTemprature());
        }
    }

    public void updateFreezing(ItemContainer outermostContainer) {
        float float0 = (float)GameTime.getInstance().getWorldAgeHours();
        if (this.LastFrozenUpdate < 0.0F) {
            this.LastFrozenUpdate = float0;
        } else if (this.LastFrozenUpdate > float0) {
            this.LastFrozenUpdate = float0;
        }

        if (float0 > this.LastFrozenUpdate) {
            float float1 = float0 - this.LastFrozenUpdate;
            float float2 = 4.0F;
            float float3 = 1.5F;
            if (this.isFreezing()) {
                this.setFreezingTime(this.getFreezingTime() + float1 / float2 * 100.0F);
            }

            if (this.isThawing()) {
                float float4 = float3;
                if (outermostContainer != null && "fridge".equals(outermostContainer.getType()) && outermostContainer.isPowered()) {
                    float4 = float3 * 2.0F;
                }

                if (outermostContainer != null && outermostContainer.getTemprature() > 1.0F) {
                    float4 /= 6.0F;
                }

                this.setFreezingTime(this.getFreezingTime() - float1 / float4 * 100.0F);
            }

            this.LastFrozenUpdate = float0;
        }
    }

    /**
     * @return the ActualWeight
     */
    @Override
    public float getActualWeight() {
        if (this.haveExtraItems()) {
            float float0 = this.getHungChange();
            float float1 = this.getBaseHunger();
            float float2 = float1 == 0.0F ? 0.0F : float0 / float1;
            float float3 = 0.0F;
            if (this.getReplaceOnUse() != null) {
                String string0 = this.getReplaceOnUseFullType();
                Item item0 = ScriptManager.instance.getItem(string0);
                if (item0 != null) {
                    float3 = item0.getActualWeight();
                }
            }

            float float4 = super.getActualWeight() + this.getExtraItemsWeight();
            return (float4 - float3) * float2 + float3;
        } else {
            if (this.getReplaceOnUse() != null && !this.isCustomWeight()) {
                String string1 = this.getReplaceOnUseFullType();
                Item item1 = ScriptManager.instance.getItem(string1);
                if (item1 != null) {
                    float float5 = 1.0F;
                    if (this.getScriptItem().getHungerChange() < 0.0F) {
                        float5 = this.getHungChange() * 100.0F / this.getScriptItem().getHungerChange();
                    } else if (this.getScriptItem().getThirstChange() < 0.0F) {
                        float5 = this.getThirstChange() * 100.0F / this.getScriptItem().getThirstChange();
                    }

                    return (this.getScriptItem().getActualWeight() - item1.getActualWeight()) * float5 + item1.getActualWeight();
                }
            } else if (!this.isCustomWeight()) {
                float float6 = 1.0F;
                if (this.getScriptItem().getHungerChange() < 0.0F) {
                    float6 = this.getHungChange() * 100.0F / this.getScriptItem().getHungerChange();
                } else if (this.getScriptItem().getThirstChange() < 0.0F) {
                    float6 = this.getThirstChange() * 100.0F / this.getScriptItem().getThirstChange();
                }

                return this.getScriptItem().getActualWeight() * float6;
            }

            return super.getActualWeight();
        }
    }

    /**
     * @return the Weight
     */
    @Override
    public float getWeight() {
        return this.getReplaceOnUse() != null ? this.getActualWeight() : super.getWeight();
    }

    @Override
    public boolean CanStack(InventoryItem item) {
        return false;
    }

    @Override
    public void save(ByteBuffer output, boolean net) throws IOException {
        super.save(output, net);
        output.putFloat(this.Age);
        output.putFloat(this.LastAged);
        BitHeaderWrite bitHeaderWrite0 = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, output);
        if (this.calories != 0.0F || this.proteins != 0.0F || this.lipids != 0.0F || this.carbohydrates != 0.0F) {
            bitHeaderWrite0.addFlags(1);
            output.putFloat(this.calories);
            output.putFloat(this.proteins);
            output.putFloat(this.lipids);
            output.putFloat(this.carbohydrates);
        }

        if (this.hungChange != 0.0F) {
            bitHeaderWrite0.addFlags(2);
            output.putFloat(this.hungChange);
        }

        if (this.baseHunger != 0.0F) {
            bitHeaderWrite0.addFlags(4);
            output.putFloat(this.baseHunger);
        }

        if (this.unhappyChange != 0.0F) {
            bitHeaderWrite0.addFlags(8);
            output.putFloat(this.unhappyChange);
        }

        if (this.boredomChange != 0.0F) {
            bitHeaderWrite0.addFlags(16);
            output.putFloat(this.boredomChange);
        }

        if (this.thirstChange != 0.0F) {
            bitHeaderWrite0.addFlags(32);
            output.putFloat(this.thirstChange);
        }

        BitHeaderWrite bitHeaderWrite1 = BitHeader.allocWrite(BitHeader.HeaderSize.Integer, output);
        if (this.Heat != 1.0F) {
            bitHeaderWrite1.addFlags(1);
            output.putFloat(this.Heat);
        }

        if (this.LastCookMinute != 0) {
            bitHeaderWrite1.addFlags(2);
            output.putInt(this.LastCookMinute);
        }

        if (this.CookingTime != 0.0F) {
            bitHeaderWrite1.addFlags(4);
            output.putFloat(this.CookingTime);
        }

        if (this.Cooked) {
            bitHeaderWrite1.addFlags(8);
        }

        if (this.Burnt) {
            bitHeaderWrite1.addFlags(16);
        }

        if (this.IsCookable) {
            bitHeaderWrite1.addFlags(32);
        }

        if (this.bDangerousUncooked) {
            bitHeaderWrite1.addFlags(64);
        }

        if (this.poisonDetectionLevel != -1) {
            bitHeaderWrite1.addFlags(128);
            output.put((byte)this.poisonDetectionLevel);
        }

        if (this.spices != null) {
            bitHeaderWrite1.addFlags(256);
            output.put((byte)this.spices.size());

            for (String string : this.spices) {
                GameWindow.WriteString(output, string);
            }
        }

        if (this.PoisonPower != 0) {
            bitHeaderWrite1.addFlags(512);
            output.put((byte)this.PoisonPower);
        }

        if (this.Chef != null) {
            bitHeaderWrite1.addFlags(1024);
            GameWindow.WriteString(output, this.Chef);
        }

        if (this.OffAge != 1.0E9) {
            bitHeaderWrite1.addFlags(2048);
            output.putInt(this.OffAge);
        }

        if (this.OffAgeMax != 1.0E9) {
            bitHeaderWrite1.addFlags(4096);
            output.putInt(this.OffAgeMax);
        }

        if (this.painReduction != 0.0F) {
            bitHeaderWrite1.addFlags(8192);
            output.putFloat(this.painReduction);
        }

        if (this.fluReduction != 0) {
            bitHeaderWrite1.addFlags(16384);
            output.putInt(this.fluReduction);
        }

        if (this.ReduceFoodSickness != 0) {
            bitHeaderWrite1.addFlags(32768);
            output.putInt(this.ReduceFoodSickness);
        }

        if (this.Poison) {
            bitHeaderWrite1.addFlags(65536);
        }

        if (this.UseForPoison != 0) {
            bitHeaderWrite1.addFlags(131072);
            output.putShort((short)this.UseForPoison);
        }

        if (this.freezingTime != 0.0F) {
            bitHeaderWrite1.addFlags(262144);
            output.putFloat(this.freezingTime);
        }

        if (this.isFrozen()) {
            bitHeaderWrite1.addFlags(524288);
        }

        if (this.LastFrozenUpdate != 0.0F) {
            bitHeaderWrite1.addFlags(1048576);
            output.putFloat(this.LastFrozenUpdate);
        }

        if (this.rottenTime != 0.0F) {
            bitHeaderWrite1.addFlags(2097152);
            output.putFloat(this.rottenTime);
        }

        if (this.compostTime != 0.0F) {
            bitHeaderWrite1.addFlags(4194304);
            output.putFloat(this.compostTime);
        }

        if (this.cookedInMicrowave) {
            bitHeaderWrite1.addFlags(8388608);
        }

        if (this.fatigueChange != 0.0F) {
            bitHeaderWrite1.addFlags(16777216);
            output.putFloat(this.fatigueChange);
        }

        if (this.endChange != 0.0F) {
            bitHeaderWrite1.addFlags(33554432);
            output.putFloat(this.endChange);
        }

        if (!bitHeaderWrite1.equals(0)) {
            bitHeaderWrite0.addFlags(64);
            bitHeaderWrite1.write();
        } else {
            output.position(bitHeaderWrite1.getStartPosition());
        }

        bitHeaderWrite0.write();
        bitHeaderWrite0.release();
        bitHeaderWrite1.release();
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion) throws IOException {
        super.load(input, WorldVersion);
        this.calories = 0.0F;
        this.proteins = 0.0F;
        this.lipids = 0.0F;
        this.carbohydrates = 0.0F;
        this.hungChange = 0.0F;
        this.baseHunger = 0.0F;
        this.unhappyChange = 0.0F;
        this.boredomChange = 0.0F;
        this.thirstChange = 0.0F;
        this.Heat = 1.0F;
        this.LastCookMinute = 0;
        this.CookingTime = 0.0F;
        this.Cooked = false;
        this.Burnt = false;
        this.IsCookable = false;
        this.bDangerousUncooked = false;
        this.poisonDetectionLevel = -1;
        this.spices = null;
        this.PoisonPower = 0;
        this.Chef = null;
        this.OffAge = 1000000000;
        this.OffAgeMax = 1000000000;
        this.painReduction = 0.0F;
        this.fluReduction = 0;
        this.ReduceFoodSickness = 0;
        this.Poison = false;
        this.UseForPoison = 0;
        this.freezingTime = 0.0F;
        this.frozen = false;
        this.LastFrozenUpdate = 0.0F;
        this.rottenTime = 0.0F;
        this.compostTime = 0.0F;
        this.cookedInMicrowave = false;
        this.fatigueChange = 0.0F;
        this.endChange = 0.0F;
        this.Age = input.getFloat();
        this.LastAged = input.getFloat();
        BitHeaderRead bitHeaderRead0 = BitHeader.allocRead(BitHeader.HeaderSize.Byte, input);
        if (!bitHeaderRead0.equals(0)) {
            if (bitHeaderRead0.hasFlags(1)) {
                this.calories = input.getFloat();
                this.proteins = input.getFloat();
                this.lipids = input.getFloat();
                this.carbohydrates = input.getFloat();
            }

            if (bitHeaderRead0.hasFlags(2)) {
                this.hungChange = input.getFloat();
            }

            if (bitHeaderRead0.hasFlags(4)) {
                this.baseHunger = input.getFloat();
            }

            if (bitHeaderRead0.hasFlags(8)) {
                this.unhappyChange = input.getFloat();
            }

            if (bitHeaderRead0.hasFlags(16)) {
                this.boredomChange = input.getFloat();
            }

            if (bitHeaderRead0.hasFlags(32)) {
                this.thirstChange = input.getFloat();
            }

            if (bitHeaderRead0.hasFlags(64)) {
                BitHeaderRead bitHeaderRead1 = BitHeader.allocRead(BitHeader.HeaderSize.Integer, input);
                if (bitHeaderRead1.hasFlags(1)) {
                    this.Heat = input.getFloat();
                }

                if (bitHeaderRead1.hasFlags(2)) {
                    this.LastCookMinute = input.getInt();
                }

                if (bitHeaderRead1.hasFlags(4)) {
                    this.CookingTime = input.getFloat();
                }

                this.Cooked = bitHeaderRead1.hasFlags(8);
                this.Burnt = bitHeaderRead1.hasFlags(16);
                this.IsCookable = bitHeaderRead1.hasFlags(32);
                this.bDangerousUncooked = bitHeaderRead1.hasFlags(64);
                if (bitHeaderRead1.hasFlags(128)) {
                    this.poisonDetectionLevel = input.get();
                }

                if (bitHeaderRead1.hasFlags(256)) {
                    this.spices = new ArrayList<>();
                    byte byte0 = input.get();

                    for (int int0 = 0; int0 < byte0; int0++) {
                        String string = GameWindow.ReadString(input);
                        this.spices.add(string);
                    }
                }

                if (bitHeaderRead1.hasFlags(512)) {
                    this.PoisonPower = input.get();
                }

                if (bitHeaderRead1.hasFlags(1024)) {
                    this.Chef = GameWindow.ReadString(input);
                }

                if (bitHeaderRead1.hasFlags(2048)) {
                    this.OffAge = input.getInt();
                }

                if (bitHeaderRead1.hasFlags(4096)) {
                    this.OffAgeMax = input.getInt();
                }

                if (bitHeaderRead1.hasFlags(8192)) {
                    this.painReduction = input.getFloat();
                }

                if (bitHeaderRead1.hasFlags(16384)) {
                    this.fluReduction = input.getInt();
                }

                if (bitHeaderRead1.hasFlags(32768)) {
                    this.ReduceFoodSickness = input.getInt();
                }

                this.Poison = bitHeaderRead1.hasFlags(65536);
                if (bitHeaderRead1.hasFlags(131072)) {
                    this.UseForPoison = input.getShort();
                }

                if (bitHeaderRead1.hasFlags(262144)) {
                    this.freezingTime = input.getFloat();
                }

                this.setFrozen(bitHeaderRead1.hasFlags(524288));
                if (bitHeaderRead1.hasFlags(1048576)) {
                    this.LastFrozenUpdate = input.getFloat();
                }

                if (bitHeaderRead1.hasFlags(2097152)) {
                    this.rottenTime = input.getFloat();
                }

                if (bitHeaderRead1.hasFlags(4194304)) {
                    this.compostTime = input.getFloat();
                }

                this.cookedInMicrowave = bitHeaderRead1.hasFlags(8388608);
                if (bitHeaderRead1.hasFlags(16777216)) {
                    this.fatigueChange = input.getFloat();
                }

                if (bitHeaderRead1.hasFlags(33554432)) {
                    this.endChange = input.getFloat();
                }

                bitHeaderRead1.release();
            }
        }

        bitHeaderRead0.release();
        if (GameServer.bServer && this.LastAged == -1.0F) {
            this.LastAged = (float)GameTime.getInstance().getWorldAgeHours();
        }
    }

    @Override
    public boolean finishupdate() {
        if (this.container != null || this.getWorldItem() != null && this.getWorldItem().getSquare() != null) {
            if (this.IsCookable) {
                return false;
            } else if (this.container == null || this.Heat == this.container.getTemprature() && !this.container.isTemperatureChanging()) {
                if (this.isTaintedWater() && this.container != null && this.container.getTemprature() > 1.0F) {
                    return false;
                } else {
                    if ((!GameClient.bClient || this.isInLocalPlayerInventory()) && this.OffAgeMax != 1.0E9) {
                        if (this.replaceOnRotten != null && !this.replaceOnRotten.isEmpty()) {
                            return false;
                        }

                        if (SandboxOptions.instance.DaysForRottenFoodRemoval.getValue() != -1) {
                            return false;
                        }
                    }

                    return true;
                }
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean shouldUpdateInWorld() {
        if (!GameClient.bClient && this.OffAgeMax != 1.0E9) {
            if (this.replaceOnRotten != null && !this.replaceOnRotten.isEmpty()) {
                return true;
            }

            if (SandboxOptions.instance.DaysForRottenFoodRemoval.getValue() != -1) {
                return true;
            }
        }

        return this.getHeat() != 1.0F;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        String string = "";
        if (this.Burnt) {
            string = string + this.BurntString + " ";
        } else if (this.OffAge < 1000000000 && this.Age < this.OffAge) {
            string = string + this.FreshString + " ";
        } else if (this.OffAgeMax < 1000000000 && this.Age >= this.OffAgeMax) {
            string = string + this.OffString + " ";
        } else if (this.OffAgeMax < 1000000000 && this.Age >= this.OffAge) {
            string = string + this.StaleString + " ";
        }

        if (this.isCooked() && !this.Burnt && !this.hasTag("HideCooked")) {
            string = string + this.CookedString + " ";
        } else if (this.IsCookable && !this.Burnt && !this.hasTag("HideCooked")) {
            string = string + this.UnCookedString + " ";
        }

        if (this.isFrozen()) {
            string = string + this.FrozenString + " ";
        }

        string = string.trim();
        return string.isEmpty() ? this.name : Translator.getText("IGUI_FoodNaming", string, this.name);
    }

    @Override
    public void DoTooltip(ObjectTooltip tooltipUI, ObjectTooltip.Layout layout) {
        if (this.getHungerChange() != 0.0F) {
            ObjectTooltip.LayoutItem layoutItem0 = layout.addItem();
            layoutItem0.setLabel(Translator.getText("Tooltip_food_Hunger") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            int int0 = (int)(this.getHungerChange() * 100.0F);
            layoutItem0.setValueRight(int0, false);
        }

        if (this.getThirstChange() != 0.0F) {
            ObjectTooltip.LayoutItem layoutItem1 = layout.addItem();
            layoutItem1.setLabel(Translator.getText("Tooltip_food_Thirst") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            int int1 = (int)(this.getThirstChange() * 100.0F);
            layoutItem1.setValueRight(int1, false);
        }

        if (this.getEnduranceChange() != 0.0F) {
            ObjectTooltip.LayoutItem layoutItem2 = layout.addItem();
            int int2 = (int)(this.getEnduranceChange() * 100.0F);
            layoutItem2.setLabel(Translator.getText("Tooltip_food_Endurance") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem2.setValueRight(int2, true);
        }

        if (this.getStressChange() != 0.0F) {
            ObjectTooltip.LayoutItem layoutItem3 = layout.addItem();
            int int3 = (int)(this.getStressChange() * 100.0F);
            layoutItem3.setLabel(Translator.getText("Tooltip_food_Stress") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem3.setValueRight(int3, false);
        }

        if (this.getBoredomChange() != 0.0F) {
            ObjectTooltip.LayoutItem layoutItem4 = layout.addItem();
            int int4 = (int)this.getBoredomChange();
            layoutItem4.setLabel(Translator.getText("Tooltip_food_Boredom") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem4.setValueRight(int4, false);
        }

        if (this.getUnhappyChange() != 0.0F) {
            ObjectTooltip.LayoutItem layoutItem5 = layout.addItem();
            int int5 = (int)this.getUnhappyChange();
            layoutItem5.setLabel(Translator.getText("Tooltip_food_Unhappiness") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem5.setValueRight(int5, false);
        }

        if (this.isIsCookable() && !this.isFrozen() && !this.Burnt && this.getHeat() > 1.6) {
            float float0 = this.getCookingTime();
            float float1 = this.getMinutesToCook();
            float float2 = this.getMinutesToBurn();
            float float3 = float0 / float1;
            ColorInfo colorInfo = Core.getInstance().getGoodHighlitedColor();
            float float4 = colorInfo.getR();
            float float5 = colorInfo.getG();
            float float6 = colorInfo.getB();
            float float7 = 1.0F;
            float float8 = colorInfo.getR();
            float float9 = colorInfo.getG();
            float float10 = colorInfo.getB();
            String string = Translator.getText("IGUI_invpanel_Cooking");
            if (float0 > float1) {
                colorInfo = Core.getInstance().getBadHighlitedColor();
                string = Translator.getText("IGUI_invpanel_Burning");
                float8 = colorInfo.getR();
                float9 = colorInfo.getG();
                float10 = colorInfo.getB();
                float3 = (float0 - float1) / (float2 - float1);
                float4 = colorInfo.getR();
                float5 = colorInfo.getG();
                float6 = colorInfo.getB();
            }

            ObjectTooltip.LayoutItem layoutItem6 = layout.addItem();
            layoutItem6.setLabel(string + ": ", float8, float9, float10, 1.0F);
            layoutItem6.setProgress(float3, float4, float5, float6, float7);
        }

        if (this.getFreezingTime() < 100.0F && this.getFreezingTime() > 0.0F) {
            float float11 = this.getFreezingTime() / 100.0F;
            float float12 = 0.0F;
            float float13 = 0.6F;
            float float14 = 0.0F;
            float float15 = 0.7F;
            float float16 = 1.0F;
            float float17 = 1.0F;
            float float18 = 0.8F;
            ObjectTooltip.LayoutItem layoutItem7 = layout.addItem();
            layoutItem7.setLabel(Translator.getText("IGUI_invpanel_FreezingTime") + ": ", float16, float17, float18, 1.0F);
            layoutItem7.setProgress(float11, float12, float13, float14, float15);
        }

        if (Core.bDebug && DebugOptions.instance.TooltipInfo.getValue()
            || this.isPackaged()
            || tooltipUI.getCharacter() != null
                && (tooltipUI.getCharacter().Traits.Nutritionist.isSet() || tooltipUI.getCharacter().Traits.Nutritionist2.isSet())) {
            ObjectTooltip.LayoutItem layoutItem8 = layout.addItem();
            layoutItem8.setLabel(Translator.getText("Tooltip_food_Calories") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem8.setValueRightNoPlus(this.getCalories());
            layoutItem8 = layout.addItem();
            layoutItem8.setLabel(Translator.getText("Tooltip_food_Carbs") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem8.setValueRightNoPlus(this.getCarbohydrates());
            layoutItem8 = layout.addItem();
            layoutItem8.setLabel(Translator.getText("Tooltip_food_Prots") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem8.setValueRightNoPlus(this.getProteins());
            layoutItem8 = layout.addItem();
            layoutItem8.setLabel(Translator.getText("Tooltip_food_Fat") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem8.setValueRightNoPlus(this.getLipids());
        }

        if (this.isbDangerousUncooked() && !this.isCooked() && !this.isBurnt()) {
            ObjectTooltip.LayoutItem layoutItem9 = layout.addItem();
            layoutItem9.setLabel(
                Translator.getText("Tooltip_food_Dangerous_uncooked"),
                Core.getInstance().getBadHighlitedColor().getR(),
                Core.getInstance().getBadHighlitedColor().getG(),
                Core.getInstance().getBadHighlitedColor().getB(),
                1.0F
            );
            if (this.hasTag("Egg")) {
                layoutItem9.setLabel(Translator.getText("Tooltip_food_SlightDanger_uncooked"), 1.0F, 0.0F, 0.0F, 1.0F);
            }
        }

        if (this.getScriptItem().RemoveUnhappinessWhenCooked && !this.isCooked()) {
            ObjectTooltip.LayoutItem layoutItem10 = layout.addItem();
            layoutItem10.setLabel(
                Translator.getText("Tooltip_food_CookToRemoveUnhappiness"),
                Core.getInstance().getBadHighlitedColor().getR(),
                Core.getInstance().getBadHighlitedColor().getG(),
                Core.getInstance().getBadHighlitedColor().getB(),
                1.0F
            );
        }

        if ((this.isGoodHot() || this.isBadCold()) && this.Heat < 1.3F) {
            ObjectTooltip.LayoutItem layoutItem11 = layout.addItem();
            layoutItem11.setLabel(Translator.getText("Tooltip_food_BetterHot"), 1.0F, 0.9F, 0.9F, 1.0F);
        }

        if (this.cookedInMicrowave) {
            ObjectTooltip.LayoutItem layoutItem12 = layout.addItem();
            layoutItem12.setLabel(Translator.getText("Tooltip_food_CookedInMicrowave"), 1.0F, 0.9F, 0.9F, 1.0F);
        }

        if (Core.bDebug && DebugOptions.instance.TooltipInfo.getValue()) {
            ObjectTooltip.LayoutItem layoutItem13 = layout.addItem();
            layoutItem13.setLabel("DBG: BaseHunger", 0.0F, 1.0F, 0.0F, 1.0F);
            layoutItem13.setValueRight((int)(this.getBaseHunger() * 100.0F), false);
            layoutItem13 = layout.addItem();
            layoutItem13.setLabel("DBG: Age", 0.0F, 1.0F, 0.0F, 1.0F);
            layoutItem13.setValueRightNoPlus(this.getAge() * 24.0F);
            if (this.getOffAgeMax() != 1.0E9) {
                layoutItem13 = layout.addItem();
                layoutItem13.setLabel("DBG: Age Fresh", 0.0F, 1.0F, 0.0F, 1.0F);
                layoutItem13.setValueRightNoPlus(this.getOffAge() * 24.0F);
                layoutItem13 = layout.addItem();
                layoutItem13.setLabel("DBG: Age Rotten", 0.0F, 1.0F, 0.0F, 1.0F);
                layoutItem13.setValueRightNoPlus(this.getOffAgeMax() * 24);
            }

            layoutItem13 = layout.addItem();
            layoutItem13.setLabel("DBG: Heat", 0.0F, 1.0F, 0.0F, 1.0F);
            layoutItem13.setValueRightNoPlus(this.getHeat());
            layoutItem13 = layout.addItem();
            layoutItem13.setLabel("DBG: Freeze Time", 0.0F, 1.0F, 0.0F, 1.0F);
            layoutItem13.setValueRightNoPlus(this.getFreezingTime());
            layoutItem13 = layout.addItem();
            layoutItem13.setLabel("DBG: Compost Time", 0.0F, 1.0F, 0.0F, 1.0F);
            layoutItem13.setValueRightNoPlus(this.getCompostTime());
        }
    }

    public float getEnduranceChange() {
        if (this.Burnt) {
            return this.endChange / 3.0F;
        } else if (this.Age >= this.OffAge && this.Age < this.OffAgeMax) {
            return this.endChange / 2.0F;
        } else {
            return this.isCooked() ? this.endChange * 2.0F : this.endChange;
        }
    }

    public void setEnduranceChange(float _endChange) {
        this.endChange = _endChange;
    }

    /**
     * @return the unhappyChange
     */
    @Override
    public float getUnhappyChange() {
        float float0 = this.unhappyChange;
        Boolean boolean0 = "Icecream".equals(this.getType()) || this.hasTag("GoodFrozen");
        if (this.isFrozen() && !boolean0) {
            float0 += 30.0F;
        }

        if (this.Burnt) {
            float0 += 20.0F;
        }

        if (this.Age >= this.OffAge && this.Age < this.OffAgeMax) {
            float0 += 10.0F;
        }

        if (this.Age >= this.OffAgeMax) {
            float0 += 20.0F;
        }

        if (this.isBadCold() && this.IsCookable && this.isCooked() && this.Heat < 1.3F) {
            float0 += 2.0F;
        }

        if (this.isGoodHot() && this.IsCookable && this.isCooked() && this.Heat > 1.3F) {
            float0 -= 2.0F;
        }

        return float0;
    }

    /**
     * @return the boredomChange
     */
    @Override
    public float getBoredomChange() {
        float float0 = this.boredomChange;
        Boolean boolean0 = "Icecream".equals(this.getType()) || this.hasTag("GoodFrozen");
        if (this.isFrozen() && !boolean0) {
            float0 += 30.0F;
        }

        if (this.Burnt) {
            float0 += 20.0F;
        }

        if (this.Age >= this.OffAge && this.Age < this.OffAgeMax) {
            float0 += 10.0F;
        }

        if (this.Age >= this.OffAgeMax) {
            float0 += 20.0F;
        }

        return float0;
    }

    public float getHungerChange() {
        float float0 = this.hungChange;
        if (this.Burnt) {
            return float0 / 3.0F;
        } else if (this.Age >= this.OffAge && this.Age < this.OffAgeMax) {
            return float0 / 1.3F;
        } else if (this.Age >= this.OffAgeMax) {
            return float0 / 2.2F;
        } else {
            return this.isCooked() ? float0 * 1.3F : float0;
        }
    }

    /**
     * @return the stressChange
     */
    @Override
    public float getStressChange() {
        if (this.Burnt) {
            return this.stressChange / 4.0F;
        } else if (this.Age >= this.OffAge && this.Age < this.OffAgeMax) {
            return this.stressChange / 1.3F;
        } else if (this.Age >= this.OffAgeMax) {
            return this.stressChange / 2.0F;
        } else {
            return this.isCooked() ? this.stressChange * 1.3F : this.stressChange;
        }
    }

    public float getBoredomChangeUnmodified() {
        return this.boredomChange;
    }

    public float getEnduranceChangeUnmodified() {
        return this.endChange;
    }

    public float getStressChangeUnmodified() {
        return this.stressChange;
    }

    public float getThirstChangeUnmodified() {
        return this.thirstChange;
    }

    public float getUnhappyChangeUnmodified() {
        return this.unhappyChange;
    }

    @Override
    public float getScore(SurvivorDesc desc) {
        float float0 = 0.0F;
        return float0 - this.getHungerChange() * 100.0F;
    }

    public boolean isBadCold() {
        return this.bBadCold;
    }

    public void setBadCold(boolean _bBadCold) {
        this.bBadCold = _bBadCold;
    }

    public boolean isGoodHot() {
        return this.bGoodHot;
    }

    public void setGoodHot(boolean _bGoodHot) {
        this.bGoodHot = _bGoodHot;
    }

    public boolean isCookedInMicrowave() {
        return this.cookedInMicrowave;
    }

    public void setCookedInMicrowave(boolean b) {
        this.cookedInMicrowave = b;
    }

    public float getHeat() {
        return this.Heat;
    }

    @Override
    public float getInvHeat() {
        return this.Heat > 1.0F ? (this.Heat - 1.0F) / 2.0F : 1.0F - (this.Heat - 0.2F) / 0.8F;
    }

    public void setHeat(float _Heat) {
        this.Heat = _Heat;
    }

    public float getEndChange() {
        return this.endChange;
    }

    public void setEndChange(float _endChange) {
        this.endChange = _endChange;
    }

    @Deprecated
    public float getBaseHungChange() {
        return this.getHungChange();
    }

    public float getHungChange() {
        return this.hungChange;
    }

    public void setHungChange(float _hungChange) {
        this.hungChange = _hungChange;
    }

    public String getUseOnConsume() {
        return this.useOnConsume;
    }

    public void setUseOnConsume(String _useOnConsume) {
        this.useOnConsume = _useOnConsume;
    }

    public boolean isRotten() {
        return this.Age >= this.OffAgeMax;
    }

    public boolean isFresh() {
        return this.Age < this.OffAge;
    }

    public void setRotten(boolean _rotten) {
        this.rotten = _rotten;
    }

    public boolean isbDangerousUncooked() {
        return this.bDangerousUncooked;
    }

    public void setbDangerousUncooked(boolean _bDangerousUncooked) {
        this.bDangerousUncooked = _bDangerousUncooked;
    }

    public int getLastCookMinute() {
        return this.LastCookMinute;
    }

    public void setLastCookMinute(int _LastCookMinute) {
        this.LastCookMinute = _LastCookMinute;
    }

    public float getThirstChange() {
        float float0 = this.thirstChange;
        if (this.Burnt) {
            return float0 / 5.0F;
        } else {
            return this.isCooked() ? float0 / 2.0F : float0;
        }
    }

    public void setThirstChange(float _thirstChange) {
        this.thirstChange = _thirstChange;
    }

    public void setReplaceOnCooked(List<String> replaceOnCooked) {
        this.ReplaceOnCooked = replaceOnCooked;
    }

    public List<String> getReplaceOnCooked() {
        return this.ReplaceOnCooked;
    }

    public float getBaseHunger() {
        return this.baseHunger;
    }

    public void setBaseHunger(float _baseHunger) {
        this.baseHunger = _baseHunger;
    }

    public boolean isSpice() {
        return this.isSpice;
    }

    public void setSpice(boolean _isSpice) {
        this.isSpice = _isSpice;
    }

    public boolean isPoison() {
        return this.Poison;
    }

    public int getPoisonDetectionLevel() {
        return this.poisonDetectionLevel;
    }

    public void setPoisonDetectionLevel(int _poisonDetectionLevel) {
        this.poisonDetectionLevel = _poisonDetectionLevel;
    }

    public Integer getPoisonLevelForRecipe() {
        return this.PoisonLevelForRecipe;
    }

    public void setPoisonLevelForRecipe(Integer poisonLevelForRecipe) {
        this.PoisonLevelForRecipe = poisonLevelForRecipe;
    }

    public int getUseForPoison() {
        return this.UseForPoison;
    }

    public void setUseForPoison(int useForPoison) {
        this.UseForPoison = useForPoison;
    }

    public int getPoisonPower() {
        return this.PoisonPower;
    }

    public void setPoisonPower(int poisonPower) {
        this.PoisonPower = poisonPower;
    }

    public String getFoodType() {
        return this.FoodType;
    }

    public void setFoodType(String foodType) {
        this.FoodType = foodType;
    }

    public boolean isRemoveNegativeEffectOnCooked() {
        return this.RemoveNegativeEffectOnCooked;
    }

    public void setRemoveNegativeEffectOnCooked(boolean removeNegativeEffectOnCooked) {
        this.RemoveNegativeEffectOnCooked = removeNegativeEffectOnCooked;
    }

    public String getCookingSound() {
        return this.getScriptItem().getCookingSound();
    }

    public String getCustomEatSound() {
        return this.CustomEatSound;
    }

    public void setCustomEatSound(String customEatSound) {
        this.CustomEatSound = customEatSound;
    }

    public String getChef() {
        return this.Chef;
    }

    public void setChef(String chef) {
        this.Chef = chef;
    }

    public String getOnCooked() {
        return this.OnCooked;
    }

    public void setOnCooked(String onCooked) {
        this.OnCooked = onCooked;
    }

    public String getHerbalistType() {
        return this.HerbalistType;
    }

    public void setHerbalistType(String type) {
        this.HerbalistType = type;
    }

    public ArrayList<String> getSpices() {
        return this.spices;
    }

    public void setSpices(ArrayList<String> _spices) {
        if (_spices != null && !_spices.isEmpty()) {
            if (this.spices == null) {
                this.spices = new ArrayList<>(_spices);
            } else {
                this.spices.clear();
                this.spices.addAll(_spices);
            }
        } else {
            if (this.spices != null) {
                this.spices.clear();
            }
        }
    }

    @Override
    public Texture getTex() {
        if (this.Burnt) {
            return this.textureBurnt;
        } else if (this.Age >= this.OffAgeMax) {
            return this.texturerotten;
        } else {
            return this.isCooked() ? this.textureCooked : super.getTex();
        }
    }

    /**
     * @return the WorldTexture
     */
    @Override
    public String getWorldTexture() {
        if (this.Burnt) {
            return this.WorldTextureOverdone;
        } else if (this.Age >= this.OffAgeMax) {
            return this.WorldTextureRotten;
        } else {
            return this.isCooked() ? this.WorldTextureCooked : this.WorldTexture;
        }
    }

    public int getReduceFoodSickness() {
        return this.ReduceFoodSickness;
    }

    public void setReduceFoodSickness(int _ReduceFoodSickness) {
        this.ReduceFoodSickness = _ReduceFoodSickness;
    }

    public int getFluReduction() {
        return this.fluReduction;
    }

    public void setFluReduction(int _fluReduction) {
        this.fluReduction = _fluReduction;
    }

    public float getPainReduction() {
        return this.painReduction;
    }

    public void setPainReduction(float _painReduction) {
        this.painReduction = _painReduction;
    }

    public float getCarbohydrates() {
        return this.carbohydrates;
    }

    public void setCarbohydrates(float _carbohydrates) {
        this.carbohydrates = _carbohydrates;
    }

    public float getLipids() {
        return this.lipids;
    }

    public void setLipids(float _lipids) {
        this.lipids = _lipids;
    }

    public float getProteins() {
        return this.proteins;
    }

    public void setProteins(float _proteins) {
        this.proteins = _proteins;
    }

    public float getCalories() {
        return this.calories;
    }

    public void setCalories(float _calories) {
        this.calories = _calories;
    }

    public boolean isPackaged() {
        return this.packaged;
    }

    public void setPackaged(boolean _packaged) {
        this.packaged = _packaged;
    }

    public float getFreezingTime() {
        return this.freezingTime;
    }

    public void setFreezingTime(float _freezingTime) {
        if (_freezingTime >= 100.0F) {
            this.setFrozen(true);
            _freezingTime = 100.0F;
        } else if (_freezingTime <= 0.0F) {
            _freezingTime = 0.0F;
            this.setFrozen(false);
        }

        this.freezingTime = _freezingTime;
    }

    public void freeze() {
        this.setFreezingTime(100.0F);
    }

    public boolean isFrozen() {
        return this.frozen;
    }

    public void setFrozen(boolean _frozen) {
        this.frozen = _frozen;
    }

    public boolean canBeFrozen() {
        return this.canBeFrozen;
    }

    public void setCanBeFrozen(boolean _canBeFrozen) {
        this.canBeFrozen = _canBeFrozen;
    }

    public boolean isFreezing() {
        return this.canBeFrozen()
                && !(this.getFreezingTime() >= 100.0F)
                && this.getOutermostContainer() != null
                && "freezer".equals(this.getOutermostContainer().getType())
            ? this.getOutermostContainer().isPowered()
            : false;
    }

    public boolean isThawing() {
        if (!this.canBeFrozen() || this.getFreezingTime() <= 0.0F) {
            return false;
        } else {
            return this.getOutermostContainer() != null && "freezer".equals(this.getOutermostContainer().getType())
                ? !this.getOutermostContainer().isPowered()
                : true;
        }
    }

    public String getReplaceOnRotten() {
        return this.replaceOnRotten;
    }

    public void setReplaceOnRotten(String _replaceOnRotten) {
        this.replaceOnRotten = _replaceOnRotten;
    }

    public void multiplyFoodValues(float percentage) {
        this.setBoredomChange(this.getBoredomChangeUnmodified() * percentage);
        this.setUnhappyChange(this.getUnhappyChangeUnmodified() * percentage);
        this.setHungChange(this.getHungChange() * percentage);
        this.setFluReduction((int)(this.getFluReduction() * percentage));
        this.setThirstChange(this.getThirstChangeUnmodified() * percentage);
        this.setPainReduction(this.getPainReduction() * percentage);
        this.setReduceFoodSickness((int)(this.getReduceFoodSickness() * percentage));
        this.setEndChange(this.getEnduranceChangeUnmodified() * percentage);
        this.setStressChange(this.getStressChangeUnmodified() * percentage);
        this.setFatigueChange(this.getFatigueChange() * percentage);
        this.setCalories(this.getCalories() * percentage);
        this.setCarbohydrates(this.getCarbohydrates() * percentage);
        this.setProteins(this.getProteins() * percentage);
        this.setLipids(this.getLipids() * percentage);
    }

    public float getRottenTime() {
        return this.rottenTime;
    }

    public void setRottenTime(float time) {
        this.rottenTime = time;
    }

    public float getCompostTime() {
        return this.compostTime;
    }

    public void setCompostTime(float _compostTime) {
        this.compostTime = _compostTime;
    }

    public String getOnEat() {
        return this.onEat;
    }

    public void setOnEat(String _onEat) {
        this.onEat = _onEat;
    }

    public boolean isBadInMicrowave() {
        return this.badInMicrowave;
    }

    public void setBadInMicrowave(boolean _badInMicrowave) {
        this.badInMicrowave = _badInMicrowave;
    }

    private void destroyThisItem() {
        IsoWorldInventoryObject worldInventoryObject = this.getWorldItem();
        if (worldInventoryObject != null && worldInventoryObject.getSquare() != null) {
            if (GameServer.bServer) {
                GameServer.RemoveItemFromMap(worldInventoryObject);
            } else {
                worldInventoryObject.removeFromWorld();
                worldInventoryObject.removeFromSquare();
            }

            this.setWorldItem(null);
        } else if (this.container != null) {
            IsoObject object = this.container.getParent();
            if (GameServer.bServer) {
                if (!this.isInPlayerInventory()) {
                    GameServer.sendRemoveItemFromContainer(this.container, this);
                }

                this.container.Remove(this);
            } else {
                this.container.Remove(this);
            }

            IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this);
            LuaManager.updateOverlaySprite(object);
        }

        if (!GameServer.bServer) {
            LuaEventManager.triggerEvent("OnContainerUpdate");
        }
    }
}
