// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characterTextures;

import java.util.ArrayList;
import java.util.HashMap;
import zombie.SandboxOptions;
import zombie.core.Rand;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.inventory.types.Clothing;
import zombie.scripting.objects.Item;
import zombie.util.Type;

public enum BloodClothingType {
    Jacket,
    LongJacket,
    Trousers,
    ShortsShort,
    Shirt,
    ShirtLongSleeves,
    ShirtNoSleeves,
    Jumper,
    JumperNoSleeves,
    Shoes,
    FullHelmet,
    Apron,
    Bag,
    Hands,
    Head,
    Neck,
    UpperBody,
    LowerBody,
    LowerLegs,
    UpperLegs,
    LowerArms,
    UpperArms,
    Groin;

    private static HashMap<BloodClothingType, ArrayList<BloodBodyPartType>> coveredParts = null;
    private static final ArrayList<BloodBodyPartType> bodyParts = new ArrayList<>();

    public static BloodClothingType fromString(String str) {
        if (Jacket.toString().equals(str)) {
            return Jacket;
        } else if (LongJacket.toString().equals(str)) {
            return LongJacket;
        } else if (Trousers.toString().equals(str)) {
            return Trousers;
        } else if (ShortsShort.toString().equals(str)) {
            return ShortsShort;
        } else if (Shirt.toString().equals(str)) {
            return Shirt;
        } else if (ShirtLongSleeves.toString().equals(str)) {
            return ShirtLongSleeves;
        } else if (ShirtNoSleeves.toString().equals(str)) {
            return ShirtNoSleeves;
        } else if (Jumper.toString().equals(str)) {
            return Jumper;
        } else if (JumperNoSleeves.toString().equals(str)) {
            return JumperNoSleeves;
        } else if (Shoes.toString().equals(str)) {
            return Shoes;
        } else if (FullHelmet.toString().equals(str)) {
            return FullHelmet;
        } else if (Bag.toString().equals(str)) {
            return Bag;
        } else if (Hands.toString().equals(str)) {
            return Hands;
        } else if (Head.toString().equals(str)) {
            return Head;
        } else if (Neck.toString().equals(str)) {
            return Neck;
        } else if (Apron.toString().equals(str)) {
            return Apron;
        } else if (Bag.toString().equals(str)) {
            return Bag;
        } else if (Hands.toString().equals(str)) {
            return Hands;
        } else if (Head.toString().equals(str)) {
            return Head;
        } else if (Neck.toString().equals(str)) {
            return Neck;
        } else if (UpperBody.toString().equals(str)) {
            return UpperBody;
        } else if (LowerBody.toString().equals(str)) {
            return LowerBody;
        } else if (LowerLegs.toString().equals(str)) {
            return LowerLegs;
        } else if (UpperLegs.toString().equals(str)) {
            return UpperLegs;
        } else if (LowerArms.toString().equals(str)) {
            return LowerArms;
        } else if (UpperArms.toString().equals(str)) {
            return UpperArms;
        } else {
            return Groin.toString().equals(str) ? Groin : null;
        }
    }

    private static void init() {
        if (coveredParts == null) {
            coveredParts = new HashMap<>();
            ArrayList arrayList0 = new ArrayList();
            arrayList0.add(BloodBodyPartType.Torso_Upper);
            arrayList0.add(BloodBodyPartType.Torso_Lower);
            arrayList0.add(BloodBodyPartType.UpperLeg_L);
            arrayList0.add(BloodBodyPartType.UpperLeg_R);
            coveredParts.put(Apron, arrayList0);
            ArrayList arrayList1 = new ArrayList();
            arrayList1.add(BloodBodyPartType.Torso_Upper);
            arrayList1.add(BloodBodyPartType.Torso_Lower);
            arrayList1.add(BloodBodyPartType.Back);
            coveredParts.put(ShirtNoSleeves, arrayList1);
            coveredParts.put(JumperNoSleeves, arrayList1);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.addAll(arrayList1);
            arrayList2.add(BloodBodyPartType.UpperArm_L);
            arrayList2.add(BloodBodyPartType.UpperArm_R);
            coveredParts.put(Shirt, arrayList2);
            ArrayList arrayList3 = new ArrayList();
            arrayList3.addAll(arrayList2);
            arrayList3.add(BloodBodyPartType.ForeArm_L);
            arrayList3.add(BloodBodyPartType.ForeArm_R);
            coveredParts.put(ShirtLongSleeves, arrayList3);
            coveredParts.put(Jumper, arrayList3);
            ArrayList arrayList4 = new ArrayList();
            arrayList4.addAll(arrayList3);
            arrayList4.add(BloodBodyPartType.Neck);
            coveredParts.put(Jacket, arrayList4);
            ArrayList arrayList5 = new ArrayList();
            arrayList5.addAll(arrayList3);
            arrayList5.add(BloodBodyPartType.Neck);
            arrayList5.add(BloodBodyPartType.Groin);
            arrayList5.add(BloodBodyPartType.UpperLeg_L);
            arrayList5.add(BloodBodyPartType.UpperLeg_R);
            coveredParts.put(LongJacket, arrayList5);
            ArrayList arrayList6 = new ArrayList();
            arrayList6.add(BloodBodyPartType.Groin);
            arrayList6.add(BloodBodyPartType.UpperLeg_L);
            arrayList6.add(BloodBodyPartType.UpperLeg_R);
            coveredParts.put(ShortsShort, arrayList6);
            ArrayList arrayList7 = new ArrayList();
            arrayList7.addAll(arrayList6);
            arrayList7.add(BloodBodyPartType.LowerLeg_L);
            arrayList7.add(BloodBodyPartType.LowerLeg_R);
            coveredParts.put(Trousers, arrayList7);
            ArrayList arrayList8 = new ArrayList();
            arrayList8.add(BloodBodyPartType.Foot_L);
            arrayList8.add(BloodBodyPartType.Foot_R);
            coveredParts.put(Shoes, arrayList8);
            ArrayList arrayList9 = new ArrayList();
            arrayList9.add(BloodBodyPartType.Head);
            coveredParts.put(FullHelmet, arrayList9);
            ArrayList arrayList10 = new ArrayList();
            arrayList10.add(BloodBodyPartType.Back);
            coveredParts.put(Bag, arrayList10);
            ArrayList arrayList11 = new ArrayList();
            arrayList11.add(BloodBodyPartType.Hand_L);
            arrayList11.add(BloodBodyPartType.Hand_R);
            coveredParts.put(Hands, arrayList11);
            ArrayList arrayList12 = new ArrayList();
            arrayList12.add(BloodBodyPartType.Head);
            coveredParts.put(Head, arrayList12);
            ArrayList arrayList13 = new ArrayList();
            arrayList13.add(BloodBodyPartType.Neck);
            coveredParts.put(Neck, arrayList13);
            ArrayList arrayList14 = new ArrayList();
            arrayList14.add(BloodBodyPartType.Groin);
            coveredParts.put(Groin, arrayList14);
            ArrayList arrayList15 = new ArrayList();
            arrayList15.add(BloodBodyPartType.Torso_Upper);
            coveredParts.put(UpperBody, arrayList15);
            ArrayList arrayList16 = new ArrayList();
            arrayList16.add(BloodBodyPartType.Torso_Lower);
            coveredParts.put(LowerBody, arrayList16);
            ArrayList arrayList17 = new ArrayList();
            arrayList17.add(BloodBodyPartType.LowerLeg_L);
            arrayList17.add(BloodBodyPartType.LowerLeg_R);
            coveredParts.put(LowerLegs, arrayList17);
            ArrayList arrayList18 = new ArrayList();
            arrayList18.add(BloodBodyPartType.UpperLeg_L);
            arrayList18.add(BloodBodyPartType.UpperLeg_R);
            coveredParts.put(UpperLegs, arrayList18);
            ArrayList arrayList19 = new ArrayList();
            arrayList19.add(BloodBodyPartType.UpperArm_L);
            arrayList19.add(BloodBodyPartType.UpperArm_R);
            coveredParts.put(UpperArms, arrayList19);
            ArrayList arrayList20 = new ArrayList();
            arrayList20.add(BloodBodyPartType.ForeArm_L);
            arrayList20.add(BloodBodyPartType.ForeArm_R);
            coveredParts.put(LowerArms, arrayList20);
        }
    }

    public static ArrayList<BloodBodyPartType> getCoveredParts(ArrayList<BloodClothingType> bloodClothingType) {
        return getCoveredParts(bloodClothingType, new ArrayList<>());
    }

    public static ArrayList<BloodBodyPartType> getCoveredParts(ArrayList<BloodClothingType> bloodClothingType, ArrayList<BloodBodyPartType> result) {
        if (bloodClothingType == null) {
            return result;
        } else {
            init();

            for (int int0 = 0; int0 < bloodClothingType.size(); int0++) {
                BloodClothingType _bloodClothingType = (BloodClothingType)bloodClothingType.get(int0);
                result.addAll(coveredParts.get(_bloodClothingType));
            }

            return result;
        }
    }

    public static int getCoveredPartCount(ArrayList<BloodClothingType> bloodClothingType) {
        if (bloodClothingType == null) {
            return 0;
        } else {
            init();
            int int0 = 0;

            for (int int1 = 0; int1 < bloodClothingType.size(); int1++) {
                BloodClothingType _bloodClothingType = (BloodClothingType)bloodClothingType.get(int1);
                int0 += coveredParts.get(_bloodClothingType).size();
            }

            return int0;
        }
    }

    public static void addBlood(int count, HumanVisual humanVisual, ArrayList<ItemVisual> itemVisuals, boolean allLayers) {
        for (int int0 = 0; int0 < count; int0++) {
            BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(Rand.Next(0, BloodBodyPartType.MAX.index()));
            addBlood(bloodBodyPartType, humanVisual, itemVisuals, allLayers);
        }
    }

    public static void addBlood(BloodBodyPartType part, HumanVisual humanVisual, ArrayList<ItemVisual> itemVisuals, boolean allLayers) {
        init();
        float float0 = 0.0F;
        if (SandboxOptions.instance.ClothingDegradation.getValue() > 1) {
            float float1 = 0.01F;
            float float2 = 0.05F;
            if (SandboxOptions.instance.ClothingDegradation.getValue() == 2) {
                float1 = 0.001F;
                float2 = 0.01F;
            }

            if (SandboxOptions.instance.ClothingDegradation.getValue() == 3) {
                float1 = 0.05F;
                float2 = 0.1F;
            }

            float0 = OutfitRNG.Next(float1, float2);
        }

        addBlood(part, float0, humanVisual, itemVisuals, allLayers);
    }

    public static void addDirt(BloodBodyPartType part, HumanVisual humanVisual, ArrayList<ItemVisual> itemVisuals, boolean allLayers) {
        init();
        float float0 = 0.0F;
        if (SandboxOptions.instance.ClothingDegradation.getValue() > 1) {
            float float1 = 0.01F;
            float float2 = 0.05F;
            if (SandboxOptions.instance.ClothingDegradation.getValue() == 2) {
                float1 = 0.001F;
                float2 = 0.01F;
            }

            if (SandboxOptions.instance.ClothingDegradation.getValue() == 3) {
                float1 = 0.05F;
                float2 = 0.1F;
            }

            float0 = OutfitRNG.Next(float1, float2);
        }

        addDirt(part, float0, humanVisual, itemVisuals, allLayers);
    }

    public static void addHole(BloodBodyPartType part, HumanVisual humanVisual, ArrayList<ItemVisual> itemVisuals) {
        addHole(part, humanVisual, itemVisuals, false);
    }

    public static boolean addHole(BloodBodyPartType part, HumanVisual humanVisual, ArrayList<ItemVisual> itemVisuals, boolean allLayers) {
        init();
        ItemVisual itemVisual0 = null;
        boolean boolean0 = false;

        for (int int0 = itemVisuals.size() - 1; int0 >= 0; int0--) {
            ItemVisual itemVisual1 = (ItemVisual)itemVisuals.get(int0);
            Item item = itemVisual1.getScriptItem();
            if (item != null && (itemVisual1.getInventoryItem() == null || !itemVisual1.getInventoryItem().isBroken())) {
                ArrayList arrayList = item.getBloodClothingType();
                if (arrayList != null) {
                    for (int int1 = 0; int1 < arrayList.size(); int1++) {
                        BloodClothingType bloodClothingType = item.getBloodClothingType().get(int1);
                        if (coveredParts.get(bloodClothingType).contains(part) && item.canHaveHoles && itemVisual1.getHole(part) == 0.0F) {
                            itemVisual0 = itemVisual1;
                            break;
                        }
                    }

                    if (itemVisual0 != null) {
                        itemVisual0.setHole(part);
                        Clothing clothing = Type.tryCastTo(itemVisual0.getInventoryItem(), Clothing.class);
                        if (clothing != null) {
                            clothing.removePatch(part);
                            clothing.setCondition(clothing.getCondition() - clothing.getCondLossPerHole());
                        }

                        boolean0 = true;
                        if (!allLayers) {
                            break;
                        }

                        itemVisual0 = null;
                    }
                }
            }
        }

        if (itemVisual0 == null || allLayers) {
            humanVisual.setHole(part);
        }

        return boolean0;
    }

    /**
     * Should be used only for debug, use Clothing.addPatch for gameplay stuff
     */
    public static void addBasicPatch(BloodBodyPartType part, HumanVisual humanVisual, ArrayList<ItemVisual> itemVisuals) {
        init();
        ItemVisual itemVisual0 = null;

        for (int int0 = itemVisuals.size() - 1; int0 >= 0; int0--) {
            ItemVisual itemVisual1 = (ItemVisual)itemVisuals.get(int0);
            Item item = itemVisual1.getScriptItem();
            if (item != null) {
                ArrayList arrayList = item.getBloodClothingType();
                if (arrayList != null) {
                    for (int int1 = 0; int1 < arrayList.size(); int1++) {
                        BloodClothingType bloodClothingType = (BloodClothingType)arrayList.get(int1);
                        if (coveredParts.get(bloodClothingType).contains(part) && itemVisual1.getBasicPatch(part) == 0.0F) {
                            itemVisual0 = itemVisual1;
                            break;
                        }
                    }

                    if (itemVisual0 != null) {
                        break;
                    }
                }
            }
        }

        if (itemVisual0 != null) {
            itemVisual0.removeHole(BloodBodyPartType.ToIndex(part));
            itemVisual0.setBasicPatch(part);
        }
    }

    public static void addDirt(BloodBodyPartType part, float intensity, HumanVisual humanVisual, ArrayList<ItemVisual> itemVisuals, boolean allLayers) {
        init();
        ItemVisual itemVisual0 = null;
        if (!allLayers) {
            for (int int0 = itemVisuals.size() - 1; int0 >= 0; int0--) {
                ItemVisual itemVisual1 = (ItemVisual)itemVisuals.get(int0);
                Item item0 = itemVisual1.getScriptItem();
                if (item0 != null) {
                    ArrayList arrayList0 = item0.getBloodClothingType();
                    if (arrayList0 != null) {
                        for (int int1 = 0; int1 < arrayList0.size(); int1++) {
                            BloodClothingType bloodClothingType0 = (BloodClothingType)arrayList0.get(int1);
                            if (coveredParts.get(bloodClothingType0).contains(part) && itemVisual1.getHole(part) == 0.0F) {
                                itemVisual0 = itemVisual1;
                                break;
                            }
                        }

                        if (itemVisual0 != null) {
                            break;
                        }
                    }
                }
            }

            if (itemVisual0 != null) {
                if (intensity > 0.0F) {
                    itemVisual0.setDirt(part, itemVisual0.getDirt(part) + intensity);
                    if (itemVisual0.getInventoryItem() instanceof Clothing) {
                        calcTotalDirtLevel((Clothing)itemVisual0.getInventoryItem());
                    }
                }
            } else {
                float float0 = humanVisual.getDirt(part);
                humanVisual.setDirt(part, float0 + 0.05F);
            }
        } else {
            float float1 = humanVisual.getDirt(part);
            humanVisual.setDirt(part, float1 + 0.05F);
            float float2 = humanVisual.getDirt(part);
            if (Rand.NextBool(Math.abs(new Float(float2 * 100.0F).intValue() - 100))) {
                return;
            }

            for (int int2 = 0; int2 < itemVisuals.size(); int2++) {
                itemVisual0 = null;
                ItemVisual itemVisual2 = (ItemVisual)itemVisuals.get(int2);
                Item item1 = itemVisual2.getScriptItem();
                if (item1 != null) {
                    ArrayList arrayList1 = item1.getBloodClothingType();
                    if (arrayList1 != null) {
                        for (int int3 = 0; int3 < arrayList1.size(); int3++) {
                            BloodClothingType bloodClothingType1 = (BloodClothingType)arrayList1.get(int3);
                            if (coveredParts.get(bloodClothingType1).contains(part) && itemVisual2.getHole(part) == 0.0F) {
                                itemVisual0 = itemVisual2;
                                break;
                            }
                        }

                        if (itemVisual0 != null) {
                            if (intensity > 0.0F) {
                                itemVisual0.setDirt(part, itemVisual0.getDirt(part) + intensity);
                                if (itemVisual0.getInventoryItem() instanceof Clothing) {
                                    calcTotalDirtLevel((Clothing)itemVisual0.getInventoryItem());
                                }

                                float2 = itemVisual0.getDirt(part);
                            }

                            if (Rand.NextBool(Math.abs(new Float(float2 * 100.0F).intValue() - 100))) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void addBlood(BloodBodyPartType part, float intensity, HumanVisual humanVisual, ArrayList<ItemVisual> itemVisuals, boolean allLayers) {
        init();
        ItemVisual itemVisual0 = null;
        if (!allLayers) {
            for (int int0 = itemVisuals.size() - 1; int0 >= 0; int0--) {
                ItemVisual itemVisual1 = (ItemVisual)itemVisuals.get(int0);
                Item item0 = itemVisual1.getScriptItem();
                if (item0 != null) {
                    ArrayList arrayList0 = item0.getBloodClothingType();
                    if (arrayList0 != null) {
                        for (int int1 = 0; int1 < arrayList0.size(); int1++) {
                            BloodClothingType bloodClothingType0 = (BloodClothingType)arrayList0.get(int1);
                            if (coveredParts.get(bloodClothingType0).contains(part) && itemVisual1.getHole(part) == 0.0F) {
                                itemVisual0 = itemVisual1;
                                break;
                            }
                        }

                        if (itemVisual0 != null) {
                            break;
                        }
                    }
                }
            }

            if (itemVisual0 != null) {
                if (intensity > 0.0F) {
                    itemVisual0.setBlood(part, itemVisual0.getBlood(part) + intensity);
                    if (itemVisual0.getInventoryItem() instanceof Clothing) {
                        calcTotalBloodLevel((Clothing)itemVisual0.getInventoryItem());
                    }
                }
            } else {
                float float0 = humanVisual.getBlood(part);
                humanVisual.setBlood(part, float0 + 0.05F);
            }
        } else {
            float float1 = humanVisual.getBlood(part);
            humanVisual.setBlood(part, float1 + 0.05F);
            float float2 = humanVisual.getBlood(part);
            if (OutfitRNG.NextBool(Math.abs(new Float(float2 * 100.0F).intValue() - 100))) {
                return;
            }

            for (int int2 = 0; int2 < itemVisuals.size(); int2++) {
                itemVisual0 = null;
                ItemVisual itemVisual2 = (ItemVisual)itemVisuals.get(int2);
                Item item1 = itemVisual2.getScriptItem();
                if (item1 != null) {
                    ArrayList arrayList1 = item1.getBloodClothingType();
                    if (arrayList1 != null) {
                        for (int int3 = 0; int3 < arrayList1.size(); int3++) {
                            BloodClothingType bloodClothingType1 = (BloodClothingType)arrayList1.get(int3);
                            if (coveredParts.get(bloodClothingType1).contains(part) && itemVisual2.getHole(part) == 0.0F) {
                                itemVisual0 = itemVisual2;
                                break;
                            }
                        }

                        if (itemVisual0 != null) {
                            if (intensity > 0.0F) {
                                itemVisual0.setBlood(part, itemVisual0.getBlood(part) + intensity);
                                if (itemVisual0.getInventoryItem() instanceof Clothing) {
                                    calcTotalBloodLevel((Clothing)itemVisual0.getInventoryItem());
                                }

                                float2 = itemVisual0.getBlood(part);
                            }

                            if (OutfitRNG.NextBool(Math.abs(new Float(float2 * 100.0F).intValue() - 100))) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static synchronized void calcTotalBloodLevel(Clothing clothing) {
        ItemVisual itemVisual = clothing.getVisual();
        if (itemVisual == null) {
            clothing.setBloodLevel(0.0F);
        } else {
            ArrayList arrayList = clothing.getBloodClothingType();
            if (arrayList == null) {
                clothing.setBloodLevel(0.0F);
            } else {
                bodyParts.clear();
                getCoveredParts(arrayList, bodyParts);
                if (bodyParts.isEmpty()) {
                    clothing.setBloodLevel(0.0F);
                } else {
                    float float0 = 0.0F;

                    for (int int0 = 0; int0 < bodyParts.size(); int0++) {
                        float0 += itemVisual.getBlood(bodyParts.get(int0)) * 100.0F;
                    }

                    clothing.setBloodLevel(float0 / bodyParts.size());
                }
            }
        }
    }

    public static synchronized void calcTotalDirtLevel(Clothing clothing) {
        ItemVisual itemVisual = clothing.getVisual();
        if (itemVisual == null) {
            clothing.setDirtyness(0.0F);
        } else {
            ArrayList arrayList = clothing.getBloodClothingType();
            if (arrayList == null) {
                clothing.setDirtyness(0.0F);
            } else {
                bodyParts.clear();
                getCoveredParts(arrayList, bodyParts);
                if (bodyParts.isEmpty()) {
                    clothing.setDirtyness(0.0F);
                } else {
                    float float0 = 0.0F;

                    for (int int0 = 0; int0 < bodyParts.size(); int0++) {
                        float0 += itemVisual.getDirt(bodyParts.get(int0)) * 100.0F;
                    }

                    clothing.setDirtyness(float0 / bodyParts.size());
                }
            }
        }
    }
}
