// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.AttachedItems;

import java.util.ArrayList;

public final class AttachedLocationGroup {
    protected final String id;
    protected final ArrayList<AttachedLocation> locations = new ArrayList<>();

    public AttachedLocationGroup(String _id) {
        if (_id == null) {
            throw new NullPointerException("id is null");
        } else if (_id.isEmpty()) {
            throw new IllegalArgumentException("id is empty");
        } else {
            this.id = _id;
        }
    }

    public AttachedLocation getLocation(String locationId) {
        for (int int0 = 0; int0 < this.locations.size(); int0++) {
            AttachedLocation attachedLocation = this.locations.get(int0);
            if (attachedLocation.id.equals(locationId)) {
                return attachedLocation;
            }
        }

        return null;
    }

    public AttachedLocation getOrCreateLocation(String locationId) {
        AttachedLocation attachedLocation = this.getLocation(locationId);
        if (attachedLocation == null) {
            attachedLocation = new AttachedLocation(this, locationId);
            this.locations.add(attachedLocation);
        }

        return attachedLocation;
    }

    public AttachedLocation getLocationByIndex(int index) {
        return index >= 0 && index < this.size() ? this.locations.get(index) : null;
    }

    public int size() {
        return this.locations.size();
    }

    public int indexOf(String locationId) {
        for (int int0 = 0; int0 < this.locations.size(); int0++) {
            AttachedLocation attachedLocation = this.locations.get(int0);
            if (attachedLocation.id.equals(locationId)) {
                return int0;
            }
        }

        return -1;
    }

    public void checkValid(String locationId) {
        if (locationId == null) {
            throw new NullPointerException("locationId is null");
        } else if (locationId.isEmpty()) {
            throw new IllegalArgumentException("locationId is empty");
        } else if (this.indexOf(locationId) == -1) {
            throw new RuntimeException("no such location \"" + locationId + "\"");
        }
    }
}
