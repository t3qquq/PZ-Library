// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.AttachedItems;

import java.util.ArrayList;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.WeaponPart;
import zombie.scripting.objects.ModelWeaponPart;
import zombie.util.StringUtils;
import zombie.util.Type;

public final class AttachedModelNames {
    protected AttachedLocationGroup group;
    protected final ArrayList<AttachedModelName> models = new ArrayList<>();

    AttachedLocationGroup getGroup() {
        return this.group;
    }

    public void copyFrom(AttachedModelNames other) {
        this.models.clear();

        for (int int0 = 0; int0 < other.models.size(); int0++) {
            AttachedModelName attachedModelName = other.models.get(int0);
            this.models.add(new AttachedModelName(attachedModelName));
        }
    }

    public void initFrom(AttachedItems attachedItems) {
        this.group = attachedItems.getGroup();
        this.models.clear();

        for (int int0 = 0; int0 < attachedItems.size(); int0++) {
            AttachedItem attachedItem = attachedItems.get(int0);
            String string0 = attachedItem.getItem().getStaticModel();
            if (!StringUtils.isNullOrWhitespace(string0)) {
                String string1 = this.group.getLocation(attachedItem.getLocation()).getAttachmentName();
                HandWeapon weapon = Type.tryCastTo(attachedItem.getItem(), HandWeapon.class);
                float float0 = weapon == null ? 0.0F : weapon.getBloodLevel();
                AttachedModelName attachedModelName0 = new AttachedModelName(string1, string0, float0);
                this.models.add(attachedModelName0);
                if (weapon != null) {
                    ArrayList arrayList0 = weapon.getModelWeaponPart();
                    if (arrayList0 != null) {
                        ArrayList arrayList1 = weapon.getAllWeaponParts();

                        for (int int1 = 0; int1 < arrayList1.size(); int1++) {
                            WeaponPart weaponPart = (WeaponPart)arrayList1.get(int1);

                            for (int int2 = 0; int2 < arrayList0.size(); int2++) {
                                ModelWeaponPart modelWeaponPart = (ModelWeaponPart)arrayList0.get(int2);
                                if (weaponPart.getFullType().equals(modelWeaponPart.partType)) {
                                    AttachedModelName attachedModelName1 = new AttachedModelName(
                                        modelWeaponPart.attachmentNameSelf, modelWeaponPart.attachmentParent, modelWeaponPart.modelName, 0.0F
                                    );
                                    attachedModelName0.addChild(attachedModelName1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public int size() {
        return this.models.size();
    }

    public AttachedModelName get(int index) {
        return this.models.get(index);
    }

    public void clear() {
        this.models.clear();
    }
}
