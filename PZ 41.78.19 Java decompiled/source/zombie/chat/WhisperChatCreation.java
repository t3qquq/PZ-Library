// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.chat;

import java.util.ArrayList;
import zombie.chat.defaultChats.WhisperChat;

public final class WhisperChatCreation {
    String destPlayerName = null;
    WhisperChat.ChatStatus status = WhisperChat.ChatStatus.None;
    long createTime = 0L;
    final ArrayList<String> messages = new ArrayList<>();
}
