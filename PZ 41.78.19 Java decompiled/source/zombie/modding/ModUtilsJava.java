// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.modding;

import java.util.ArrayList;
import java.util.UUID;
import zombie.characters.IsoPlayer;
import zombie.inventory.InventoryItem;
import zombie.network.GameClient;
import zombie.network.GameServer;

public final class ModUtilsJava {
    public static String getRandomUUID() {
        return UUID.randomUUID().toString();
    }

    public static boolean sendItemListNet(IsoPlayer player0, ArrayList<InventoryItem> arrayList, IsoPlayer player1, String string0, String string1) {
        if (arrayList != null) {
            string0 = string0 != null ? string0 : "-1";
            if (GameClient.bClient) {
                if (arrayList.size() > 50) {
                    return false;
                }

                for (int int0 = 0; int0 < arrayList.size(); int0++) {
                    InventoryItem item = (InventoryItem)arrayList.get(int0);
                    if (!player0.getInventory().getItems().contains(item)) {
                        return false;
                    }
                }

                return GameClient.sendItemListNet(player0, arrayList, player1, string0, string1);
            }

            if (GameServer.bServer) {
                return GameServer.sendItemListNet(null, player0, arrayList, player1, string0, string1);
            }
        }

        return false;
    }
}
