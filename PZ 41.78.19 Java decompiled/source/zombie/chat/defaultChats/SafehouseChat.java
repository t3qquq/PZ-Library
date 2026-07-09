// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.chat.defaultChats;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatBase;
import zombie.chat.ChatSettings;
import zombie.chat.ChatTab;
import zombie.core.Color;
import zombie.network.chat.ChatType;

public class SafehouseChat extends ChatBase {
    public SafehouseChat(ByteBuffer byteBuffer, ChatTab chatTab, IsoPlayer player) {
        super(byteBuffer, ChatType.safehouse, chatTab, player);
        if (!this.isCustomSettings()) {
            this.setSettings(getDefaultSettings());
        }
    }

    public SafehouseChat(int int0, ChatTab chatTab) {
        super(int0, ChatType.safehouse, chatTab);
        if (!this.isCustomSettings()) {
            this.setSettings(getDefaultSettings());
        }
    }

    public static ChatSettings getDefaultSettings() {
        ChatSettings chatSettings = new ChatSettings();
        chatSettings.setBold(true);
        chatSettings.setFontColor(Color.lightGreen);
        chatSettings.setShowAuthor(true);
        chatSettings.setShowChatTitle(true);
        chatSettings.setShowTimestamp(true);
        chatSettings.setUnique(true);
        return chatSettings;
    }
}
