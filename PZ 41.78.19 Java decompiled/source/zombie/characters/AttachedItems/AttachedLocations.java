// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.AttachedItems;

import java.util.ArrayList;

public final class AttachedLocations {
    protected static final ArrayList<AttachedLocationGroup> groups = new ArrayList<>();

    public static AttachedLocationGroup getGroup(String id) {
        for (int int0 = 0; int0 < groups.size(); int0++) {
            AttachedLocationGroup attachedLocationGroup0 = groups.get(int0);
            if (attachedLocationGroup0.id.equals(id)) {
                return attachedLocationGroup0;
            }
        }

        AttachedLocationGroup attachedLocationGroup1 = new AttachedLocationGroup(id);
        groups.add(attachedLocationGroup1);
        return attachedLocationGroup1;
    }

    public static void Reset() {
        groups.clear();
    }
}
