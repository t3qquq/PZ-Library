// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.characters.AttachedItems;

import java.util.ArrayList;
import zombie.UsedFromLua;

@UsedFromLua
public final class AttachedLocationGroup {
    protected final String id;
    protected final ArrayList<AttachedLocation> locations = new ArrayList<>();

    public AttachedLocationGroup(String id) {
        if (id == null) {
            throw new NullPointerException("id is null");
        }

        if (id.isEmpty()) {
            throw new IllegalArgumentException("id is empty");
        }

        this.id = id;
    }

    public AttachedLocation getLocation(String locationId) {
        for (int i = 0; i < this.locations.size(); i++) {
            AttachedLocation location = this.locations.get(i);
            if (location.id.equals(locationId)) {
                return location;
            }
        }

        return null;
    }

    public AttachedLocation getOrCreateLocation(String locationId) {
        AttachedLocation location = this.getLocation(locationId);
        if (location == null) {
            location = new AttachedLocation(this, locationId);
            this.locations.add(location);
        }

        return location;
    }

    public AttachedLocation getLocationByIndex(int index) {
        return index >= 0 && index < this.size() ? this.locations.get(index) : null;
    }

    public int size() {
        return this.locations.size();
    }

    public int indexOf(String locationId) {
        for (int i = 0; i < this.locations.size(); i++) {
            AttachedLocation location = this.locations.get(i);
            if (location.id.equals(locationId)) {
                return i;
            }
        }

        return -1;
    }

    public void checkValid(String locationId) {
        if (locationId == null) {
            throw new NullPointerException("locationId is null");
        }

        if (locationId.isEmpty()) {
            throw new IllegalArgumentException("locationId is empty");
        }

        if (this.indexOf(locationId) == -1) {
            throw new RuntimeException("no such location \"" + locationId + "\"");
        }
    }
}
