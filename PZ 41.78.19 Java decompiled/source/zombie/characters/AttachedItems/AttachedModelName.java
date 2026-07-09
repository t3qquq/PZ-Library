// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.AttachedItems;

import java.util.ArrayList;

public final class AttachedModelName {
    public String attachmentNameSelf;
    public String attachmentNameParent;
    public String modelName;
    public float bloodLevel;
    public ArrayList<AttachedModelName> children;

    public AttachedModelName(AttachedModelName other) {
        this.attachmentNameSelf = other.attachmentNameSelf;
        this.attachmentNameParent = other.attachmentNameParent;
        this.modelName = other.modelName;
        this.bloodLevel = other.bloodLevel;

        for (int int0 = 0; int0 < other.getChildCount(); int0++) {
            AttachedModelName attachedModelName = other.getChildByIndex(int0);
            this.addChild(new AttachedModelName(attachedModelName));
        }
    }

    public AttachedModelName(String attachmentName, String _modelName, float _bloodLevel) {
        this.attachmentNameSelf = attachmentName;
        this.attachmentNameParent = attachmentName;
        this.modelName = _modelName;
        this.bloodLevel = _bloodLevel;
    }

    public AttachedModelName(String _attachmentNameSelf, String _attachmentNameParent, String _modelName, float _bloodLevel) {
        this.attachmentNameSelf = _attachmentNameSelf;
        this.attachmentNameParent = _attachmentNameParent;
        this.modelName = _modelName;
        this.bloodLevel = _bloodLevel;
    }

    public void addChild(AttachedModelName child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }

        this.children.add(child);
    }

    public int getChildCount() {
        return this.children == null ? 0 : this.children.size();
    }

    public AttachedModelName getChildByIndex(int index) {
        return this.children.get(index);
    }
}
