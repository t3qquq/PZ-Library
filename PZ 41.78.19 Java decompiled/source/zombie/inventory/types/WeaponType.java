// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory.types;

import java.util.Arrays;
import java.util.List;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;

public enum WeaponType {
    barehand("", Arrays.asList(""), true, false),
    twohanded("2handed", Arrays.asList("default", "default", "overhead", "uppercut"), true, false),
    onehanded("1handed", Arrays.asList("default", "default", "overhead", "uppercut"), true, false),
    heavy("heavy", Arrays.asList("default", "default", "overhead"), true, false),
    knife("knife", Arrays.asList("default", "default", "overhead", "uppercut"), true, false),
    spear("spear", Arrays.asList("default"), true, false),
    handgun("handgun", Arrays.asList(""), false, true),
    firearm("firearm", Arrays.asList(""), false, true),
    throwing("throwing", Arrays.asList(""), false, true),
    chainsaw("chainsaw", Arrays.asList("default"), true, false);

    public String type = "";
    public List<String> possibleAttack;
    public boolean canMiss = true;
    public boolean isRanged = false;

    private WeaponType(String string1, List<String> list, boolean boolean0, boolean boolean1) {
        this.type = string1;
        this.possibleAttack = list;
        this.canMiss = boolean0;
        this.isRanged = boolean1;
    }

    public static WeaponType getWeaponType(HandWeapon weapon) {
        WeaponType weaponType = null;
        if (weapon.getSwingAnim().equalsIgnoreCase("Stab")) {
            return knife;
        } else if (weapon.getSwingAnim().equalsIgnoreCase("Heavy")) {
            return heavy;
        } else if (weapon.getSwingAnim().equalsIgnoreCase("Throw")) {
            return throwing;
        } else {
            if (!weapon.isRanged()) {
                weaponType = onehanded;
                if (weapon.isTwoHandWeapon()) {
                    weaponType = twohanded;
                    if (weapon.getSwingAnim().equalsIgnoreCase("Spear")) {
                        return spear;
                    }

                    if ("Chainsaw".equals(weapon.getType())) {
                        return chainsaw;
                    }
                }
            } else {
                weaponType = handgun;
                if (weapon.isTwoHandWeapon()) {
                    weaponType = firearm;
                }
            }

            if (weaponType == null) {
                weaponType = barehand;
            }

            return weaponType;
        }
    }

    public static WeaponType getWeaponType(IsoGameCharacter chr) {
        if (chr == null) {
            return null;
        } else {
            WeaponType weaponType = null;
            chr.setVariable("rangedWeapon", false);
            InventoryItem item0 = chr.getPrimaryHandItem();
            InventoryItem item1 = chr.getSecondaryHandItem();
            if (item0 != null && item0 instanceof HandWeapon) {
                if (item0.getSwingAnim().equalsIgnoreCase("Stab")) {
                    return knife;
                }

                if (item0.getSwingAnim().equalsIgnoreCase("Heavy")) {
                    return heavy;
                }

                if (item0.getSwingAnim().equalsIgnoreCase("Throw")) {
                    chr.setVariable("rangedWeapon", true);
                    return throwing;
                }

                if (!((HandWeapon)item0).isRanged()) {
                    weaponType = onehanded;
                    if (item0 == item1 && item0.isTwoHandWeapon()) {
                        weaponType = twohanded;
                        if (item0.getSwingAnim().equalsIgnoreCase("Spear")) {
                            return spear;
                        }

                        if ("Chainsaw".equals(item0.getType())) {
                            return chainsaw;
                        }
                    }
                } else {
                    weaponType = handgun;
                    if (item0 == item1 && item0.isTwoHandWeapon()) {
                        weaponType = firearm;
                    }
                }
            }

            if (weaponType == null) {
                weaponType = barehand;
            }

            chr.setVariable("rangedWeapon", weaponType == handgun || weaponType == firearm);
            return weaponType;
        }
    }

    public String getType() {
        return this.type;
    }
}
