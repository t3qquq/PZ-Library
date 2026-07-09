// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import java.util.ArrayList;
import zombie.GameTime;
import zombie.ZomboidGlobals;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.BloodClothingType;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.BodyDamage.Thermoregulator;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Clothing;

public final class ClothingWetness {
    private static final ItemVisuals itemVisuals = new ItemVisuals();
    private static final ArrayList<BloodBodyPartType> coveredParts = new ArrayList<>();
    public final IsoGameCharacter character;
    public final ClothingWetness.ItemList[] clothing = new ClothingWetness.ItemList[BloodBodyPartType.MAX.index()];
    public final int[] perspiringParts = new int[BloodBodyPartType.MAX.index()];
    public boolean changed = true;

    public ClothingWetness(IsoGameCharacter _character) {
        this.character = _character;

        for (int int0 = 0; int0 < this.clothing.length; int0++) {
            this.clothing[int0] = new ClothingWetness.ItemList();
        }
    }

    public void calculateExposedItems() {
        for (int int0 = 0; int0 < this.clothing.length; int0++) {
            this.clothing[int0].clear();
        }

        this.character.getItemVisuals(itemVisuals);

        for (int int1 = itemVisuals.size() - 1; int1 >= 0; int1--) {
            ItemVisual itemVisual = itemVisuals.get(int1);
            InventoryItem item = itemVisual.getInventoryItem();
            ArrayList arrayList = item.getBloodClothingType();
            if (arrayList != null) {
                coveredParts.clear();
                BloodClothingType.getCoveredParts(arrayList, coveredParts);

                for (int int2 = 0; int2 < coveredParts.size(); int2++) {
                    BloodBodyPartType bloodBodyPartType = coveredParts.get(int2);
                    this.clothing[bloodBodyPartType.index()].add(item);
                }
            }
        }
    }

    public void updateWetness(float outerWetnessInc, float outerWetnessDec) {
        boolean boolean0 = false;
        InventoryItem item0 = this.character.getPrimaryHandItem();
        if (item0 != null && item0.isProtectFromRainWhileEquipped()) {
            boolean0 = true;
        }

        item0 = this.character.getSecondaryHandItem();
        if (item0 != null && item0.isProtectFromRainWhileEquipped()) {
            boolean0 = true;
        }

        if (this.changed) {
            this.changed = false;
            this.calculateExposedItems();
        }

        this.character.getItemVisuals(itemVisuals);

        for (int int0 = 0; int0 < itemVisuals.size(); int0++) {
            InventoryItem item1 = itemVisuals.get(int0).getInventoryItem();
            if (item1 instanceof Clothing) {
                if (item1.getBloodClothingType() == null) {
                    ((Clothing)item1).updateWetness(true);
                } else {
                    ((Clothing)item1).flushWetness();
                }
            }
        }

        float float0 = (float)ZomboidGlobals.WetnessIncrease * GameTime.instance.getMultiplier();
        float float1 = (float)ZomboidGlobals.WetnessDecrease * GameTime.instance.getMultiplier();

        label282:
        for (int int1 = 0; int1 < this.clothing.length; int1++) {
            BloodBodyPartType bloodBodyPartType0 = BloodBodyPartType.FromIndex(int1);
            BodyPartType bodyPartType0 = BodyPartType.FromIndex(int1);
            if (bodyPartType0 != BodyPartType.MAX) {
                BodyPart bodyPart0 = this.character.getBodyDamage().getBodyPart(bodyPartType0);
                Thermoregulator.ThermalNode thermalNode0 = this.character.getBodyDamage().getThermoregulator().getNodeForBloodType(bloodBodyPartType0);
                if (bodyPart0 != null && thermalNode0 != null) {
                    float float2 = 0.0F;
                    float float3 = PZMath.clamp(thermalNode0.getSecondaryDelta(), 0.0F, 1.0F);
                    float3 *= float3;
                    float3 *= 0.2F + 0.8F * (1.0F - thermalNode0.getDistToCore());
                    if (float3 > 0.1F) {
                        float2 += float3;
                    } else {
                        float float4 = (thermalNode0.getSkinCelcius() - 20.0F) / 22.0F;
                        float4 *= float4;
                        float4 -= outerWetnessInc;
                        float4 = Math.max(0.0F, float4);
                        float2 -= float4;
                        if (outerWetnessInc > 0.0F) {
                            float2 = 0.0F;
                        }
                    }

                    this.perspiringParts[int1] = float2 > 0.0F ? 1 : 0;
                    if (float2 != 0.0F) {
                        if (float2 > 0.0F) {
                            float2 *= float0;
                        } else {
                            float2 *= float1;
                        }

                        bodyPart0.setWetness(bodyPart0.getWetness() + float2);
                        if ((!(float2 > 0.0F) || !(bodyPart0.getWetness() < 25.0F)) && (!(float2 < 0.0F) || !(bodyPart0.getWetness() > 50.0F))) {
                            if (float2 > 0.0F) {
                                float float5 = this.character.getBodyDamage().getThermoregulator().getExternalAirTemperature();
                                float5 += 10.0F;
                                float5 = PZMath.clamp(float5, 0.0F, 20.0F) / 20.0F;
                                float2 *= 0.4F + 0.6F * float5;
                            }

                            boolean boolean1 = false;
                            boolean boolean2 = false;
                            boolean boolean3 = false;
                            float float6 = 1.0F;
                            int int2 = this.clothing[int1].size() - 1;

                            InventoryItem item2;
                            Clothing clothing0;
                            while (true) {
                                if (int2 < 0) {
                                    continue label282;
                                }

                                if (float2 > 0.0F) {
                                    this.perspiringParts[int1]++;
                                }

                                item2 = this.clothing[int1].get(int2);
                                if (item2 instanceof Clothing) {
                                    float6 = 1.0F;
                                    clothing0 = (Clothing)item2;
                                    ItemVisual itemVisual0 = clothing0.getVisual();
                                    if (itemVisual0 == null) {
                                        break;
                                    }

                                    if (itemVisual0.getHole(bloodBodyPartType0) > 0.0F) {
                                        boolean1 = true;
                                    } else if (float2 > 0.0F && clothing0.getWetness() >= 100.0F) {
                                        boolean2 = true;
                                    } else {
                                        if (!(float2 < 0.0F) || !(clothing0.getWetness() <= 0.0F)) {
                                            if (float2 > 0.0F && clothing0.getWaterResistance() > 0.0F) {
                                                float6 = PZMath.max(0.0F, 1.0F - clothing0.getWaterResistance());
                                                if (float6 <= 0.0F) {
                                                    this.perspiringParts[int1]--;
                                                    continue label282;
                                                }
                                            }
                                            break;
                                        }

                                        boolean3 = true;
                                    }
                                }

                                int2--;
                            }

                            coveredParts.clear();
                            BloodClothingType.getCoveredParts(item2.getBloodClothingType(), coveredParts);
                            int int3 = coveredParts.size();
                            float float7 = float2;
                            if (float2 > 0.0F) {
                                float7 = float2 * float6;
                            }

                            if (boolean1 || boolean2 || boolean3) {
                                float7 /= 2.0F;
                            }

                            clothing0.setWetness(clothing0.getWetness() + float7);
                        }
                    }
                }
            }
        }

        for (int int4 = 0; int4 < this.clothing.length; int4++) {
            BloodBodyPartType bloodBodyPartType1 = BloodBodyPartType.FromIndex(int4);
            BodyPartType bodyPartType1 = BodyPartType.FromIndex(int4);
            if (bodyPartType1 != BodyPartType.MAX) {
                BodyPart bodyPart1 = this.character.getBodyDamage().getBodyPart(bodyPartType1);
                Thermoregulator.ThermalNode thermalNode1 = this.character.getBodyDamage().getThermoregulator().getNodeForBloodType(bloodBodyPartType1);
                if (bodyPart1 != null && thermalNode1 != null) {
                    float float8 = 100.0F;
                    if (boolean0) {
                        float8 = 100.0F * BodyPartType.GetUmbrellaMod(bodyPartType1);
                    }

                    float float9 = 0.0F;
                    if (outerWetnessInc > 0.0F) {
                        float9 = outerWetnessInc * float0;
                    } else {
                        float9 -= outerWetnessDec * float1;
                    }

                    boolean boolean4 = false;
                    boolean boolean5 = false;
                    boolean boolean6 = false;
                    float float10 = 1.0F;
                    float float11 = 2.0F;

                    for (int int5 = 0; int5 < this.clothing[int4].size(); int5++) {
                        int int6 = 1 + (this.clothing[int4].size() - int5);
                        float10 = 1.0F;
                        InventoryItem item3 = this.clothing[int4].get(int5);
                        if (item3 instanceof Clothing clothing1) {
                            ItemVisual itemVisual1 = clothing1.getVisual();
                            if (itemVisual1 != null) {
                                if (itemVisual1.getHole(bloodBodyPartType1) > 0.0F) {
                                    boolean4 = true;
                                    continue;
                                }

                                if (float9 > 0.0F && clothing1.getWetness() >= 100.0F) {
                                    boolean5 = true;
                                    continue;
                                }

                                if (float9 < 0.0F && clothing1.getWetness() <= 0.0F) {
                                    boolean6 = true;
                                    continue;
                                }

                                if (float9 > 0.0F && clothing1.getWaterResistance() > 0.0F) {
                                    float10 = PZMath.max(0.0F, 1.0F - clothing1.getWaterResistance());
                                    if (float10 <= 0.0F) {
                                        break;
                                    }
                                }
                            }

                            coveredParts.clear();
                            BloodClothingType.getCoveredParts(item3.getBloodClothingType(), coveredParts);
                            int int7 = coveredParts.size();
                            float float12 = float9;
                            if (float9 > 0.0F) {
                                float12 = float9 * float10;
                            }

                            float12 /= int7;
                            if (boolean4 || boolean5 || boolean6) {
                                float12 /= float11;
                            }

                            if (float9 < 0.0F && int6 > this.perspiringParts[int4] || float9 > 0.0F && clothing1.getWetness() <= float8) {
                                clothing1.setWetness(clothing1.getWetness() + float12);
                            }

                            if (float9 > 0.0F) {
                                break;
                            }

                            if (boolean6) {
                                float11 *= 2.0F;
                            }
                        }
                    }

                    if (!this.clothing[int4].isEmpty()) {
                        InventoryItem item4 = this.clothing[int4].get(this.clothing[int4].size() - 1);
                        if (item4 instanceof Clothing clothing2) {
                            if (float9 > 0.0F && this.perspiringParts[int4] == 0 && clothing2.getWetness() >= 50.0F && bodyPart1.getWetness() <= float8) {
                                bodyPart1.setWetness(bodyPart1.getWetness() + float9 / 2.0F);
                            }

                            if (float9 < 0.0F && this.perspiringParts[int4] == 0 && clothing2.getWetness() <= 50.0F) {
                                bodyPart1.setWetness(bodyPart1.getWetness() + float9 / 2.0F);
                            }
                        }
                    } else if (float9 < 0.0F && this.perspiringParts[int4] == 0 || bodyPart1.getWetness() <= float8) {
                        bodyPart1.setWetness(bodyPart1.getWetness() + float9);
                    }
                }
            }
        }
    }

    @Deprecated
    public void increaseWetness(float wetness) {
        if (!(wetness <= 0.0F)) {
            if (this.changed) {
                this.changed = false;
                this.calculateExposedItems();
            }

            this.character.getItemVisuals(itemVisuals);

            for (int int0 = 0; int0 < itemVisuals.size(); int0++) {
                InventoryItem item0 = itemVisuals.get(int0).getInventoryItem();
                if (item0 instanceof Clothing) {
                    ((Clothing)item0).flushWetness();
                }
            }

            int int1 = 0;

            for (int int2 = 0; int2 < this.clothing.length; int2++) {
                BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(int2);
                boolean boolean0 = false;
                boolean boolean1 = false;
                int int3 = 0;

                label85: {
                    InventoryItem item1;
                    while (true) {
                        if (int3 >= this.clothing[int2].size()) {
                            break label85;
                        }

                        item1 = this.clothing[int2].get(int3);
                        if (item1 instanceof Clothing clothing0) {
                            ItemVisual itemVisual = clothing0.getVisual();
                            if (itemVisual == null) {
                                break;
                            }

                            if (itemVisual.getHole(bloodBodyPartType) > 0.0F) {
                                boolean0 = true;
                            } else {
                                if (!(clothing0.getWetness() >= 100.0F)) {
                                    break;
                                }

                                boolean1 = true;
                            }
                        }

                        int3++;
                    }

                    coveredParts.clear();
                    BloodClothingType.getCoveredParts(item1.getBloodClothingType(), coveredParts);
                    int int4 = coveredParts.size();
                    float float0 = wetness / int4;
                    if (boolean0 || boolean1) {
                        float0 /= 2.0F;
                    }

                    clothing0.setWetness(clothing0.getWetness() + float0);
                }

                if (this.clothing[int2].isEmpty()) {
                    int1++;
                } else {
                    InventoryItem item2 = this.clothing[int2].get(this.clothing[int2].size() - 1);
                    if (item2 instanceof Clothing clothing1 && clothing1.getWetness() >= 100.0F) {
                        int1++;
                    }
                }
            }

            if (int1 > 0) {
                float float1 = this.character.getBodyDamage().getWetness();
                float float2 = wetness * ((float)int1 / this.clothing.length);
                this.character.getBodyDamage().setWetness(float1 + float2);
            }
        }
    }

    @Deprecated
    public void decreaseWetness(float wetness) {
        if (!(wetness <= 0.0F)) {
            if (this.changed) {
                this.changed = false;
                this.calculateExposedItems();
            }

            this.character.getItemVisuals(itemVisuals);

            for (int int0 = itemVisuals.size() - 1; int0 >= 0; int0--) {
                ItemVisual itemVisual = itemVisuals.get(int0);
                if (itemVisual.getInventoryItem() instanceof Clothing clothingx && clothingx.getWetness() > 0.0F) {
                    clothingx.setWetness(clothingx.getWetness() - wetness);
                }
            }
        }
    }

    private static final class ItemList extends ArrayList<InventoryItem> {
    }
}
