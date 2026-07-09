// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.WornItems;

import java.util.ArrayList;

public final class BodyLocations {
    protected static final ArrayList<BodyLocationGroup> groups = new ArrayList<>();

    public static BodyLocationGroup getGroup(String id) {
        for (int int0 = 0; int0 < groups.size(); int0++) {
            BodyLocationGroup bodyLocationGroup0 = groups.get(int0);
            if (bodyLocationGroup0.id.equals(id)) {
                return bodyLocationGroup0;
            }
        }

        BodyLocationGroup bodyLocationGroup1 = new BodyLocationGroup(id);
        groups.add(bodyLocationGroup1);
        return bodyLocationGroup1;
    }

    public static void Reset() {
        groups.clear();
    }
}
