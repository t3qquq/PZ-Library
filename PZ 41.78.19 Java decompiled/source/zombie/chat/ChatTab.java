// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.chat;

import java.util.HashSet;
import zombie.core.Translator;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.PacketTypes;

public class ChatTab {
    private short id;
    private String titleID;
    private String translatedTitle;
    private HashSet<Integer> containedChats;
    private boolean enabled = false;

    public ChatTab(short tabID, String _titleID) {
        this.id = tabID;
        this.titleID = _titleID;
        this.translatedTitle = Translator.getText(_titleID);
        this.containedChats = new HashSet<>();
    }

    public ChatTab(short tabID, String _titleID, int chatID) {
        this(tabID, _titleID);
        this.containedChats.add(chatID);
    }

    public void RemoveChat(int chatID) {
        if (!this.containedChats.contains(chatID)) {
            throw new RuntimeException("Tab '" + this.id + "' doesn't contains a chat id: " + chatID);
        } else {
            this.containedChats.remove(chatID);
        }
    }

    public String getTitleID() {
        return this.titleID;
    }

    public String getTitle() {
        return this.translatedTitle;
    }

    public short getID() {
        return this.id;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean _enabled) {
        this.enabled = _enabled;
    }

    public void sendAddTabPacket(UdpConnection connection) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.AddChatTab.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(this.getID());
        PacketTypes.PacketType.AddChatTab.send(connection);
    }

    public void sendRemoveTabPacket(UdpConnection connection) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.RemoveChatTab.doPacket(byteBufferWriter);
        byteBufferWriter.putShort(this.getID());
        PacketTypes.PacketType.RemoveChatTab.send(connection);
    }
}
