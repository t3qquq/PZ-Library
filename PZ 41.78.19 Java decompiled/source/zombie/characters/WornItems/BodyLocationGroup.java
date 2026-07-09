// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.WornItems;

import java.util.ArrayList;

public final class BodyLocationGroup {
    protected final String id;
    protected final ArrayList<BodyLocation> locations = new ArrayList<>();

    public BodyLocationGroup(String _id) {
        if (_id == null) {
            throw new NullPointerException("id is null");
        } else if (_id.isEmpty()) {
            throw new IllegalArgumentException("id is empty");
        } else {
            this.id = _id;
        }
    }

    public BodyLocation getLocation(String locationId) {
        for (int int0 = 0; int0 < this.locations.size(); int0++) {
            BodyLocation bodyLocation = this.locations.get(int0);
            if (bodyLocation.isID(locationId)) {
                return bodyLocation;
            }
        }

        return null;
    }

    public BodyLocation getLocationNotNull(String locationId) {
        BodyLocation bodyLocation = this.getLocation(locationId);
        if (bodyLocation == null) {
            throw new RuntimeException("unknown location \"" + locationId + "\"");
        } else {
            return bodyLocation;
        }
    }

    public BodyLocation getOrCreateLocation(String locationId) {
        BodyLocation bodyLocation = this.getLocation(locationId);
        if (bodyLocation == null) {
            bodyLocation = new BodyLocation(this, locationId);
            this.locations.add(bodyLocation);
        }

        return bodyLocation;
    }

    public BodyLocation getLocationByIndex(int index) {
        return index >= 0 && index < this.size() ? this.locations.get(index) : null;
    }

    public int size() {
        return this.locations.size();
    }

    public void setExclusive(String firstId, String secondId) {
        BodyLocation bodyLocation0 = this.getLocationNotNull(firstId);
        BodyLocation bodyLocation1 = this.getLocationNotNull(secondId);
        bodyLocation0.setExclusive(secondId);
        bodyLocation1.setExclusive(firstId);
    }

    public boolean isExclusive(String firstId, String secondId) {
        BodyLocation bodyLocation = this.getLocationNotNull(firstId);
        this.checkValid(secondId);
        return bodyLocation.exclusive.contains(secondId);
    }

    public void setHideModel(String firstId, String secondId) {
        BodyLocation bodyLocation = this.getLocationNotNull(firstId);
        this.checkValid(secondId);
        bodyLocation.setHideModel(secondId);
    }

    public boolean isHideModel(String firstId, String secondId) {
        BodyLocation bodyLocation = this.getLocationNotNull(firstId);
        this.checkValid(secondId);
        return bodyLocation.isHideModel(secondId);
    }

    public int indexOf(String locationId) {
        for (int int0 = 0; int0 < this.locations.size(); int0++) {
            BodyLocation bodyLocation = this.locations.get(int0);
            if (bodyLocation.isID(locationId)) {
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
            throw new RuntimeException("unknown location \"" + locationId + "\"");
        }
    }

    public void setMultiItem(String locationId, boolean bMultiItem) {
        BodyLocation bodyLocation = this.getLocationNotNull(locationId);
        bodyLocation.setMultiItem(bMultiItem);
    }

    public boolean isMultiItem(String locationId) {
        BodyLocation bodyLocation = this.getLocationNotNull(locationId);
        return bodyLocation.isMultiItem();
    }

    public ArrayList<BodyLocation> getAllLocations() {
        return this.locations;
    }
}
