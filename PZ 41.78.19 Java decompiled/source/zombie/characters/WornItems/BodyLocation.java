// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.WornItems;

import java.util.ArrayList;

public final class BodyLocation {
    protected final BodyLocationGroup group;
    protected final String id;
    protected final ArrayList<String> aliases = new ArrayList<>();
    protected final ArrayList<String> exclusive = new ArrayList<>();
    protected final ArrayList<String> hideModel = new ArrayList<>();
    protected boolean bMultiItem = false;

    public BodyLocation(BodyLocationGroup _group, String _id) {
        this.checkId(_id, "id");
        this.group = _group;
        this.id = _id;
    }

    public BodyLocation addAlias(String alias) {
        this.checkId(alias, "alias");
        if (this.aliases.contains(alias)) {
            return this;
        } else {
            this.aliases.add(alias);
            return this;
        }
    }

    public BodyLocation setExclusive(String otherId) {
        this.checkId(otherId, "otherId");
        if (this.aliases.contains(otherId)) {
            return this;
        } else if (this.exclusive.contains(otherId)) {
            return this;
        } else {
            this.exclusive.add(otherId);
            return this;
        }
    }

    public BodyLocation setHideModel(String otherId) {
        this.checkId(otherId, "otherId");
        if (this.hideModel.contains(otherId)) {
            return this;
        } else {
            this.hideModel.add(otherId);
            return this;
        }
    }

    public boolean isMultiItem() {
        return this.bMultiItem;
    }

    public BodyLocation setMultiItem(boolean _bMultiItem) {
        this.bMultiItem = _bMultiItem;
        return this;
    }

    public boolean isHideModel(String otherId) {
        return this.hideModel.contains(otherId);
    }

    public boolean isExclusive(String _id) {
        return this.group.isExclusive(this.id, _id);
    }

    public boolean isID(String _id) {
        return this.id.equals(_id) || this.aliases.contains(_id);
    }

    private void checkId(String string0, String string1) {
        if (string0 == null) {
            throw new NullPointerException(string1 + " is null");
        } else if (string0.isEmpty()) {
            throw new IllegalArgumentException(string1 + " is empty");
        }
    }

    public String getId() {
        return this.id;
    }
}
