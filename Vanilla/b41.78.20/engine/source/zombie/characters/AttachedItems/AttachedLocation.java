// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.AttachedItems;

public final class AttachedLocation {
    protected final AttachedLocationGroup group;
    protected final String id;
    protected String attachmentName;

    public AttachedLocation(AttachedLocationGroup _group, String _id) {
        if (_id == null) {
            throw new NullPointerException("id is null");
        } else if (_id.isEmpty()) {
            throw new IllegalArgumentException("id is empty");
        } else {
            this.group = _group;
            this.id = _id;
        }
    }

    public void setAttachmentName(String _attachmentName) {
        if (this.id == null) {
            throw new NullPointerException("attachmentName is null");
        } else if (this.id.isEmpty()) {
            throw new IllegalArgumentException("attachmentName is empty");
        } else {
            this.attachmentName = _attachmentName;
        }
    }

    public String getAttachmentName() {
        return this.attachmentName;
    }

    public String getId() {
        return this.id;
    }
}
